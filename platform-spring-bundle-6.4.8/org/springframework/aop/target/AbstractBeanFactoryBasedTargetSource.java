/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.aop.target;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class AbstractBeanFactoryBasedTargetSource
implements TargetSource,
BeanFactoryAware,
Serializable {
    private static final long serialVersionUID = -4721607536018568393L;
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private String targetBeanName;
    @Nullable
    private volatile Class<?> targetClass;
    @Nullable
    private BeanFactory beanFactory;

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public String getTargetBeanName() {
        Assert.state(this.targetBeanName != null, "Target bean name not set");
        return this.targetBeanName;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.targetBeanName == null) {
            throw new IllegalStateException("Property 'targetBeanName' is required");
        }
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        Assert.state(this.beanFactory != null, "BeanFactory not set");
        return this.beanFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Class<?> getTargetClass() {
        Class<?> targetClass = this.targetClass;
        if (targetClass != null) {
            return targetClass;
        }
        AbstractBeanFactoryBasedTargetSource abstractBeanFactoryBasedTargetSource = this;
        synchronized (abstractBeanFactoryBasedTargetSource) {
            targetClass = this.targetClass;
            if (targetClass == null && this.beanFactory != null && this.targetBeanName != null) {
                targetClass = this.beanFactory.getType(this.targetBeanName);
                if (targetClass == null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Getting bean with name '" + this.targetBeanName + "' for type determination"));
                    }
                    Object beanInstance = this.beanFactory.getBean(this.targetBeanName);
                    targetClass = beanInstance.getClass();
                }
                this.targetClass = targetClass;
            }
            return targetClass;
        }
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void releaseTarget(Object target) throws Exception {
    }

    protected void copyFrom(AbstractBeanFactoryBasedTargetSource other) {
        this.targetBeanName = other.targetBeanName;
        this.targetClass = other.targetClass;
        this.beanFactory = other.beanFactory;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        AbstractBeanFactoryBasedTargetSource otherTargetSource = (AbstractBeanFactoryBasedTargetSource)other;
        return ObjectUtils.nullSafeEquals(this.beanFactory, otherTargetSource.beanFactory) && ObjectUtils.nullSafeEquals(this.targetBeanName, otherTargetSource.targetBeanName);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 13 + ObjectUtils.nullSafeHashCode(this.targetBeanName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append(" for target bean '").append(this.targetBeanName).append('\'');
        Class<?> targetClass = this.targetClass;
        if (targetClass != null) {
            sb.append(" of type [").append(targetClass.getName()).append(']');
        }
        return sb.toString();
    }
}

