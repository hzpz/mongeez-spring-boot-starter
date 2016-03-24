/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hzpz.spring.boot.autoconfigure.mongeez;

import com.mongodb.Mongo;
import org.mongeez.Mongeez;
import org.mongeez.MongeezRunner;
import org.mongeez.MongoAuth;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Mongeez.
 *
 * @author Timo Kockert
 */
@Configuration
@ConditionalOnClass(Mongeez.class)
@ConditionalOnBean(Mongo.class)
@ConditionalOnProperty(prefix = "mongeez", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({MongoProperties.class, MongeezProperties.class})
@AutoConfigureAfter(MongoAutoConfiguration.class)
@AutoConfigureBefore(name = {
        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration", // Spring Boot >= 1.3.0
        "org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration" // Spring Boot < 1.3.0
})
public class MongeezAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(MongeezRunner.class)
    @EnableConfigurationProperties(MongeezProperties.class)
    @Import(MongeezDataMongoDependencyConfiguration.class)
    public static class MongeezConfiguration {

        @Autowired
        private MongeezProperties mongeezProperties = new MongeezProperties();

        private ResourceLoader resourceLoader = new DefaultResourceLoader();

        @PostConstruct
        public void checkLocationExists() {
            Resource resource = this.resourceLoader.getResource(this.mongeezProperties.getLocation());
            Assert.state(resource.exists(),
                    "Cannot find Mongeez migration script at '" + this.mongeezProperties.getLocation() + "'");
        }

        @Bean(initMethod = "process")
        public Mongeez mongeez(MongoProperties mongoProperties, Mongo mongo) {
            Mongeez mongeez = new Mongeez();
            mongeez.setMongo(mongo);

            copyMissingProperties(mongoProperties, this.mongeezProperties);

            mongeez.setDbName(this.mongeezProperties.getDatabase());
            if (this.mongeezProperties.hasCredentials()) {
                MongoAuth auth = this.mongeezProperties.createMongoAuth();
                mongeez.setAuth(auth);
            }
            mongeez.setFile(this.resourceLoader.getResource(this.mongeezProperties.getLocation()));
            return mongeez;
        }

        private void copyMissingProperties(MongoProperties mongoProperties, MongeezProperties mongeezProperties) {
            if (StringUtils.isEmpty(mongeezProperties.getDatabase())) {
                mongeezProperties.setDatabase(mongoProperties.getMongoClientDatabase());
            }
            if (StringUtils.isEmpty(mongeezProperties.getAuthenticationDatabase())) {
                mongeezProperties.setAuthenticationDatabase(mongoProperties.getAuthenticationDatabase());
            }
            if (!mongeezProperties.hasCredentials() && hasCredentials(mongoProperties)) {
                // cannot copy credentials because Spring Data MongoDB clears the password after using it
                String msg = "Found credentials for Spring Data MongoDB but no credentials for Mongeez. " +
                        "You need to define both for authentication to work.";
                throw new BeanCreationException(msg);
            }
        }

        private boolean hasCredentials(MongoProperties properties) {
            return properties.getUsername() != null && properties.getPassword() != null;
        }

    }

    /**
     * Additional configuration to ensure that {@link MongoDbFactory} beans
     * depend-on the Mongeez bean.
     */
    @Configuration
    @ConditionalOnClass(MongoDbFactory.class)
    protected static class MongeezDataMongoDependencyConfiguration {

        @Bean
        public static BeanFactoryPostProcessor mongoDbFactoryDependsOnPostProcessor() {
            return new MongoDbFactoryDependsOnPostProcessor("mongeez");
        }

    }
}
