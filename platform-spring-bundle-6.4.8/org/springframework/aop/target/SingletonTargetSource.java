/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class SingletonTargetSource
implements TargetSource,
Serializable {
    private static final long serialVersionUID = 9031246629662423738L;
    private final Object target;

    public SingletonTargetSource(Object target) {
        Assert.notNull(target, "Target object must not be null");
        this.target = target;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public void releaseTarget(Object target) {
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    public boolean equals(Object other) {
        return this == other || other instanceof SingletonTargetSource && this.target.equals(((SingletonTargetSource)other).target);
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    public String toString() {
        return "SingletonTargetSource for target object [" + ObjectUtils.identityToString(this.target) + "]";
    }
}

