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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.util.StringUtils;

/**
 * {@link BeanFactoryPostProcessor} that can be used to dynamically declare that all
 * {@link MongoDbFactory} beans should "depend on" a specific bean.
 *
 * @author Timo Kockert
 * @see BeanDefinition#setDependsOn(String[])
 */
public class MongoDbFactoryDependsOnPostProcessor implements BeanFactoryPostProcessor {

    private final String dependsOn;

    public MongoDbFactoryDependsOnPostProcessor(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        for (String beanName : getMongoDbFactoryBeanNames(beanFactory)) {
            BeanDefinition definition = getBeanDefinition(beanName, beanFactory);
            definition.setDependsOn(StringUtils.addStringToArray(
                    definition.getDependsOn(), this.dependsOn));
        }

    }

    private static BeanDefinition getBeanDefinition(String beanName,
                                                    ConfigurableListableBeanFactory beanFactory) {
        try {
            return beanFactory.getBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
            if (parentBeanFactory instanceof ConfigurableListableBeanFactory) {
                return getBeanDefinition(beanName,
                        (ConfigurableListableBeanFactory) parentBeanFactory);
            }
            throw ex;
        }
    }

    private String[] getMongoDbFactoryBeanNames(ListableBeanFactory beanFactory) {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, MongoDbFactory.class, true, false);
    }

}
