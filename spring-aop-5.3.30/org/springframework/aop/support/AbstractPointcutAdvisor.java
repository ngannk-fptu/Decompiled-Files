/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public abstract class AbstractPointcutAdvisor
implements PointcutAdvisor,
Ordered,
Serializable {
    @Nullable
    private Integer order;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        if (this.order != null) {
            return this.order;
        }
        Advice advice = this.getAdvice();
        if (advice instanceof Ordered) {
            return ((Ordered)advice).getOrder();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PointcutAdvisor)) {
            return false;
        }
        PointcutAdvisor otherAdvisor = (PointcutAdvisor)other;
        return ObjectUtils.nullSafeEquals((Object)this.getAdvice(), (Object)otherAdvisor.getAdvice()) && ObjectUtils.nullSafeEquals((Object)this.getPointcut(), (Object)otherAdvisor.getPointcut());
    }

    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }
}

