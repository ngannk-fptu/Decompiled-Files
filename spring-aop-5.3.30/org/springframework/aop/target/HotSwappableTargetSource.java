/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.util.Assert;

public class HotSwappableTargetSource
implements TargetSource,
Serializable {
    private static final long serialVersionUID = 7497929212653839187L;
    private Object target;

    public HotSwappableTargetSource(Object initialTarget) {
        Assert.notNull((Object)initialTarget, (String)"Target object must not be null");
        this.target = initialTarget;
    }

    @Override
    public synchronized Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override
    public final boolean isStatic() {
        return false;
    }

    @Override
    public synchronized Object getTarget() {
        return this.target;
    }

    @Override
    public void releaseTarget(Object target) {
    }

    public synchronized Object swap(Object newTarget) throws IllegalArgumentException {
        Assert.notNull((Object)newTarget, (String)"Target object must not be null");
        Object old = this.target;
        this.target = newTarget;
        return old;
    }

    public boolean equals(Object other) {
        return this == other || other instanceof HotSwappableTargetSource && this.target.equals(((HotSwappableTargetSource)other).target);
    }

    public int hashCode() {
        return HotSwappableTargetSource.class.hashCode();
    }

    public String toString() {
        return "HotSwappableTargetSource for target: " + this.target;
    }
}

