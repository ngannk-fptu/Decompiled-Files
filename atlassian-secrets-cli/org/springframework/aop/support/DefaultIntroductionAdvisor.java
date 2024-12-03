/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class DefaultIntroductionAdvisor
implements IntroductionAdvisor,
ClassFilter,
Ordered,
Serializable {
    private final Advice advice;
    private final Set<Class<?>> interfaces = new LinkedHashSet();
    private int order = Integer.MAX_VALUE;

    public DefaultIntroductionAdvisor(Advice advice) {
        this(advice, advice instanceof IntroductionInfo ? (IntroductionInfo)((Object)advice) : null);
    }

    public DefaultIntroductionAdvisor(Advice advice, @Nullable IntroductionInfo introductionInfo) {
        Assert.notNull((Object)advice, "Advice must not be null");
        this.advice = advice;
        if (introductionInfo != null) {
            Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
            if (introducedInterfaces.length == 0) {
                throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
            }
            for (Class<?> ifc : introducedInterfaces) {
                this.addInterface(ifc);
            }
        }
    }

    public DefaultIntroductionAdvisor(DynamicIntroductionAdvice advice, Class<?> intf) {
        Assert.notNull((Object)advice, "Advice must not be null");
        this.advice = advice;
        this.addInterface(intf);
    }

    public void addInterface(Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
        }
        this.interfaces.add(intf);
    }

    @Override
    public Class<?>[] getInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    @Override
    public void validateInterfaces() throws IllegalArgumentException {
        for (Class<?> ifc : this.interfaces) {
            if (!(this.advice instanceof DynamicIntroductionAdvice) || ((DynamicIntroductionAdvice)this.advice).implementsInterface(ifc)) continue;
            throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] does not implement interface [" + ifc.getName() + "] specified for introduction");
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultIntroductionAdvisor)) {
            return false;
        }
        DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor)other;
        return this.advice.equals(otherAdvisor.advice) && this.interfaces.equals(otherAdvisor.interfaces);
    }

    public int hashCode() {
        return this.advice.hashCode() * 13 + this.interfaces.hashCode();
    }

    public String toString() {
        return ClassUtils.getShortName(this.getClass()) + ": advice [" + this.advice + "]; interfaces " + ClassUtils.classNamesToString(this.interfaces);
    }
}

