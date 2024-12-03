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
    private static final String[] NO_ALIASES = new String[0];
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
    protected TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
        return this.isSupportedBeanName(beanClass, beanName) ? super.getCustomTargetSource(beanClass, beanName) : null;
    }

    @Override
    @Nullable
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
        return this.isSupportedBeanName(beanClass, beanName) ? PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS : DO_NOT_PROXY;
    }

    private boolean isSupportedBeanName(Class<?> beanClass, String beanName) {
        if (this.beanNames != null) {
            String[] aliases;
            boolean isFactoryBean = FactoryBean.class.isAssignableFrom(beanClass);
            for (String mappedName : this.beanNames) {
                if (isFactoryBean) {
                    if (!mappedName.startsWith("&")) continue;
                    mappedName = mappedName.substring("&".length());
                }
                if (!this.isMatch(beanName, mappedName)) continue;
                return true;
            }
            BeanFactory beanFactory = this.getBeanFactory();
            for (String alias : aliases = beanFactory != null ? beanFactory.getAliases(beanName) : NO_ALIASES) {
                for (String mappedName : this.beanNames) {
                    if (!this.isMatch(alias, mappedName)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }
}

