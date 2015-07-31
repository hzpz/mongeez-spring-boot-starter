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

import org.mongeez.Mongeez;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Disables the initMethod of the {@link Mongeez} bean,
 * preventing Spring from actually trying to run Mongeez.
 */
@Component
public class DoNotExecuteMongeezPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] mongeezBeanNames = beanFactory.getBeanNamesForType(Mongeez.class);
        Assert.state(mongeezBeanNames.length == 1);
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(mongeezBeanNames[0]);
        Assert.state(beanDefinition instanceof RootBeanDefinition);
        ((RootBeanDefinition) beanDefinition).setInitMethodName(null);
    }

}
