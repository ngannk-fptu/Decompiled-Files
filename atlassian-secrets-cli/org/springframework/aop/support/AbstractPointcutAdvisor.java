/*
 * Decompiled with CFR 0.152.
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

    @Override
    public int getOrder() {
        if (this.order != null) {
            return this.order;
        }
        Advice advice = this.getAdvice();
        if (advice instanceof Ordered) {
            return ((Ordered)((Object)advice)).getOrder();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PointcutAdvisor)) {
            return false;
        }
        PointcutAdvisor otherAdvisor = (PointcutAdvisor)other;
        return ObjectUtils.nullSafeEquals(this.getAdvice(), otherAdvisor.getAdvice()) && ObjectUtils.nullSafeEquals(this.getPointcut(), otherAdvisor.getPointcut());
    }

    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }
}

