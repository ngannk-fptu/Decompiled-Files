/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public final class EmptyTargetSource
implements TargetSource,
Serializable {
    private static final long serialVersionUID = 3680494563553489691L;
    public static final EmptyTargetSource INSTANCE = new EmptyTargetSource(null, true);
    @Nullable
    private final Class<?> targetClass;
    private final boolean isStatic;

    public static EmptyTargetSource forClass(@Nullable Class<?> targetClass) {
        return EmptyTargetSource.forClass(targetClass, true);
    }

    public static EmptyTargetSource forClass(@Nullable Class<?> targetClass, boolean isStatic) {
        return targetClass == null && isStatic ? INSTANCE : new EmptyTargetSource(targetClass, isStatic);
    }

    private EmptyTargetSource(@Nullable Class<?> targetClass, boolean isStatic) {
        this.targetClass = targetClass;
        this.isStatic = isStatic;
    }

    @Override
    @Nullable
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    @Nullable
    public Object getTarget() {
        return null;
    }

    @Override
    public void releaseTarget(Object target) {
    }

    private Object readResolve() {
        return this.targetClass == null && this.isStatic ? INSTANCE : this;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmptyTargetSource)) {
            return false;
        }
        EmptyTargetSource otherTs = (EmptyTargetSource)other;
        return ObjectUtils.nullSafeEquals(this.targetClass, otherTs.targetClass) && this.isStatic == otherTs.isStatic;
    }

    public int hashCode() {
        return EmptyTargetSource.class.hashCode() * 13 + ObjectUtils.nullSafeHashCode(this.targetClass);
    }

    public String toString() {
        return "EmptyTargetSource: " + (this.targetClass != null ? "target class [" + this.targetClass.getName() + "]" : "no target class") + ", " + (this.isStatic ? "static" : "dynamic");
    }
}

