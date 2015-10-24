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
import org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration;
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
@AutoConfigureBefore(MongoDataAutoConfiguration.class)
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
            Resource resource = resourceLoader.getResource(mongeezProperties.getLocation());
            Assert.state(resource.exists(),
                    "Cannot find Mongeez migration script at '" + mongeezProperties.getLocation() + "'");
        }

        @Bean(initMethod = "process")
        public Mongeez mongeez(MongoProperties mongoProperties, Mongo mongo) {
            Mongeez mongeez = new Mongeez();
            mongeez.setMongo(mongo);
            if (StringUtils.hasText(mongeezProperties.getDatabase())) {
                mongeez.setDbName(mongeezProperties.getDatabase());
            } else {
                mongeez.setDbName(mongoProperties.getDatabase());
            }
            if (hasCredentials(mongeezProperties)) {
                MongoAuth auth = new MongoAuth(mongeezProperties.getUsername(), mongeezProperties.getPassword());
                mongeez.setAuth(auth);
            } else if (hasCredentials(mongoProperties)) {
                String msg = "Credentials under spring.data.mongodb.* found but no credentials for mongeez.* defined." +
                        "Please add correct mongeez.password and mongeez.username";
                throw new BeanCreationException(msg);
            }
            mongeez.setFile(resourceLoader.getResource(mongeezProperties.getLocation()));
            return mongeez;
        }

        private boolean hasCredentials(MongeezProperties properties) {
            return properties.getUsername() != null && properties.getPassword() != null;
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
        public BeanFactoryPostProcessor mongoDbFactoryDependsOnPostProcessor() {
            return new MongoDbFactoryDependsOnPostProcessor("mongeez");
        }

    }
}
