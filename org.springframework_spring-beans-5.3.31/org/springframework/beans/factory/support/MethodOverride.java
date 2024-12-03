/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class MethodOverride
implements BeanMetadataElement {
    private final String methodName;
    private boolean overloaded = true;
    @Nullable
    private Object source;

    protected MethodOverride(String methodName) {
        Assert.notNull((Object)methodName, (String)"Method name must not be null");
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    protected void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    protected boolean isOverloaded() {
        return this.overloaded;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public abstract boolean matches(Method var1);

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodOverride)) {
            return false;
        }
        MethodOverride that = (MethodOverride)other;
        return ObjectUtils.nullSafeEquals((Object)this.methodName, (Object)that.methodName) && ObjectUtils.nullSafeEquals((Object)this.source, (Object)that.source);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode((Object)this.methodName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object)this.source);
        return hashCode;
    }
}

