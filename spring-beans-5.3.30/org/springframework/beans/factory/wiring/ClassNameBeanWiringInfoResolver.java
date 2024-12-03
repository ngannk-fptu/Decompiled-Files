/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
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
        Assert.notNull((Object)beanInstance, (String)"Bean instance must not be null");
        return new BeanWiringInfo(ClassUtils.getUserClass((Object)beanInstance).getName(), true);
    }
}

