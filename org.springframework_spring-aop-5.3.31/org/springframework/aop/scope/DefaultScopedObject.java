/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.util.Assert
 */
package org.springframework.aop.scope;

import java.io.Serializable;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.Assert;

public class DefaultScopedObject
implements ScopedObject,
Serializable {
    private final ConfigurableBeanFactory beanFactory;
    private final String targetBeanName;

    public DefaultScopedObject(ConfigurableBeanFactory beanFactory, String targetBeanName) {
        Assert.notNull((Object)beanFactory, (String)"BeanFactory must not be null");
        Assert.hasText((String)targetBeanName, (String)"'targetBeanName' must not be empty");
        this.beanFactory = beanFactory;
        this.targetBeanName = targetBeanName;
    }

    @Override
    public Object getTargetObject() {
        return this.beanFactory.getBean(this.targetBeanName);
    }

    @Override
    public void removeFromScope() {
        this.beanFactory.destroyScopedBean(this.targetBeanName);
    }
}

