/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractBeanFactoryPointcutAdvisor
extends AbstractPointcutAdvisor
implements BeanFactoryAware {
    @Nullable
    private String adviceBeanName;
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private volatile transient Advice advice;
    private volatile transient Object adviceMonitor = new Object();

    public void setAdviceBeanName(@Nullable String adviceBeanName) {
        this.adviceBeanName = adviceBeanName;
    }

    @Nullable
    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.resetAdviceMonitor();
    }

    private void resetAdviceMonitor() {
        this.adviceMonitor = this.beanFactory instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.beanFactory).getSingletonMutex() : new Object();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAdvice(Advice advice) {
        Object object = this.adviceMonitor;
        synchronized (object) {
            this.advice = advice;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Advice getAdvice() {
        Advice advice = this.advice;
        if (advice != null) {
            return advice;
        }
        Assert.state(this.adviceBeanName != null, "'adviceBeanName' must be specified");
        Assert.state(this.beanFactory != null, "BeanFactory must be set to resolve 'adviceBeanName'");
        if (this.beanFactory.isSingleton(this.adviceBeanName)) {
            this.advice = advice = this.beanFactory.getBean(this.adviceBeanName, Advice.class);
            return advice;
        }
        Object object = this.adviceMonitor;
        synchronized (object) {
            advice = this.advice;
            if (advice == null) {
                this.advice = advice = this.beanFactory.getBean(this.adviceBeanName, Advice.class);
            }
            return advice;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append(": advice ");
        if (this.adviceBeanName != null) {
            sb.append("bean '").append(this.adviceBeanName).append("'");
        } else {
            sb.append(this.advice);
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.resetAdviceMonitor();
    }
}

