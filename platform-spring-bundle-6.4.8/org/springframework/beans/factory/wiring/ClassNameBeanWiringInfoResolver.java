/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.wiring;

import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ClassNameBeanWiringInfoResolver
implements BeanWiringInfoResolver {
    @Override
    public BeanWiringInfo resolveWiringInfo(Object beanInstance) {
        Assert.notNull(beanInstance, "Bean instance must not be null");
        return new BeanWiringInfo(ClassUtils.getUserClass(beanInstance).getName(), true);
    }
}

