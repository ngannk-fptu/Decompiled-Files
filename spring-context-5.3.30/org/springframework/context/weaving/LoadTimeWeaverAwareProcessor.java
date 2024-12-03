/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.weaving;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class LoadTimeWeaverAwareProcessor
implements BeanPostProcessor,
BeanFactoryAware {
    @Nullable
    private LoadTimeWeaver loadTimeWeaver;
    @Nullable
    private BeanFactory beanFactory;

    public LoadTimeWeaverAwareProcessor() {
    }

    public LoadTimeWeaverAwareProcessor(@Nullable LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object postProcessBeforeInitialization(Object bean2, String beanName) throws BeansException {
        if (bean2 instanceof LoadTimeWeaverAware) {
            LoadTimeWeaver ltw = this.loadTimeWeaver;
            if (ltw == null) {
                Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"BeanFactory required if no LoadTimeWeaver explicitly specified");
                ltw = (LoadTimeWeaver)this.beanFactory.getBean("loadTimeWeaver", LoadTimeWeaver.class);
            }
            ((LoadTimeWeaverAware)bean2).setLoadTimeWeaver(ltw);
        }
        return bean2;
    }

    public Object postProcessAfterInitialization(Object bean2, String name) {
        return bean2;
    }
}

