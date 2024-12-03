/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;

public class DefaultAdvisorAutoProxyCreator
extends AbstractAdvisorAutoProxyCreator
implements BeanNameAware {
    public static final String SEPARATOR = ".";
    private boolean usePrefix = false;
    @Nullable
    private String advisorBeanNamePrefix;

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public boolean isUsePrefix() {
        return this.usePrefix;
    }

    public void setAdvisorBeanNamePrefix(@Nullable String advisorBeanNamePrefix) {
        this.advisorBeanNamePrefix = advisorBeanNamePrefix;
    }

    @Nullable
    public String getAdvisorBeanNamePrefix() {
        return this.advisorBeanNamePrefix;
    }

    public void setBeanName(String name) {
        if (this.advisorBeanNamePrefix == null) {
            this.advisorBeanNamePrefix = name + SEPARATOR;
        }
    }

    @Override
    protected boolean isEligibleAdvisorBean(String beanName) {
        if (!this.isUsePrefix()) {
            return true;
        }
        String prefix = this.getAdvisorBeanNamePrefix();
        return prefix != null && beanName.startsWith(prefix);
    }
}

