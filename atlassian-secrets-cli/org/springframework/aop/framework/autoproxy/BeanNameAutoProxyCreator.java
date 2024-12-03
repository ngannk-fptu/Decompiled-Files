/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.List;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

public class BeanNameAutoProxyCreator
extends AbstractAutoProxyCreator {
    @Nullable
    private List<String> beanNames;

    public void setBeanNames(String ... beanNames) {
        Assert.notEmpty((Object[])beanNames, "'beanNames' must not be empty");
        this.beanNames = new ArrayList<String>(beanNames.length);
        for (String mappedName : beanNames) {
            this.beanNames.add(StringUtils.trimWhitespace(mappedName));
        }
    }

    @Override
    @Nullable
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
        if (this.beanNames != null) {
            for (String mappedName : this.beanNames) {
                String[] aliases;
                if (FactoryBean.class.isAssignableFrom(beanClass)) {
                    if (!mappedName.startsWith("&")) continue;
                    mappedName = mappedName.substring("&".length());
                }
                if (this.isMatch(beanName, mappedName)) {
                    return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                }
                BeanFactory beanFactory = this.getBeanFactory();
                if (beanFactory == null) continue;
                for (String alias : aliases = beanFactory.getAliases(beanName)) {
                    if (!this.isMatch(alias, mappedName)) continue;
                    return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                }
            }
        }
        return DO_NOT_PROXY;
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }
}

