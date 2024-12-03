/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.core.NamedThreadLocal
 */
package org.springframework.aop.target;

import java.util.HashSet;
import java.util.Set;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.target.AbstractPrototypeBasedTargetSource;
import org.springframework.aop.target.ThreadLocalTargetSourceStats;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.NamedThreadLocal;

public class ThreadLocalTargetSource
extends AbstractPrototypeBasedTargetSource
implements ThreadLocalTargetSourceStats,
DisposableBean {
    private final ThreadLocal<Object> targetInThread = new NamedThreadLocal<Object>("Thread-local instance of bean"){

        public String toString() {
            return super.toString() + " '" + ThreadLocalTargetSource.this.getTargetBeanName() + "'";
        }
    };
    private final Set<Object> targetSet = new HashSet<Object>();
    private int invocationCount;
    private int hitCount;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getTarget() throws BeansException {
        ++this.invocationCount;
        Object target = this.targetInThread.get();
        if (target == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("No target for prototype '" + this.getTargetBeanName() + "' bound to thread: creating one and binding it to thread '" + Thread.currentThread().getName() + "'"));
            }
            target = this.newPrototypeInstance();
            this.targetInThread.set(target);
            Set<Object> set = this.targetSet;
            synchronized (set) {
                this.targetSet.add(target);
            }
        } else {
            ++this.hitCount;
        }
        return target;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        this.logger.debug((Object)"Destroying ThreadLocalTargetSource bindings");
        Set<Object> set = this.targetSet;
        synchronized (set) {
            for (Object target : this.targetSet) {
                this.destroyPrototypeInstance(target);
            }
            this.targetSet.clear();
        }
        this.targetInThread.remove();
    }

    @Override
    public int getInvocationCount() {
        return this.invocationCount;
    }

    @Override
    public int getHitCount() {
        return this.hitCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getObjectCount() {
        Set<Object> set = this.targetSet;
        synchronized (set) {
            return this.targetSet.size();
        }
    }

    public IntroductionAdvisor getStatsMixin() {
        DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
        return new DefaultIntroductionAdvisor(dii, ThreadLocalTargetSourceStats.class);
    }
}

