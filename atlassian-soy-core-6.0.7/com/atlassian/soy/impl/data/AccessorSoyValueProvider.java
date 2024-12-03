/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyException
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.SoyValueConverter
 *  com.google.template.soy.data.SoyValueProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.impl.data;

import com.atlassian.soy.renderer.SoyException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueConverter;
import com.google.template.soy.data.SoyValueProvider;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;

class AccessorSoyValueProvider
implements SoyValueProvider {
    private final SoyValueConverter converter;
    private final Method accessorMethod;
    private final Object delegate;
    private volatile SoyValue resolvedValue;

    public AccessorSoyValueProvider(SoyValueConverter converter, Object delegate, Method accessorMethod) {
        this.converter = converter;
        this.delegate = delegate;
        this.accessorMethod = accessorMethod;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public SoyValue resolve() {
        SoyValue resolvedValue = this.resolvedValue;
        if (resolvedValue == null) {
            AccessorSoyValueProvider accessorSoyValueProvider = this;
            synchronized (accessorSoyValueProvider) {
                resolvedValue = this.resolvedValue;
                if (resolvedValue == null) {
                    resolvedValue = this.resolvedValue = this.doResolve();
                }
            }
        }
        return resolvedValue;
    }

    private SoyValue doResolve() {
        Object resolvedValue;
        try {
            resolvedValue = this.accessorMethod.invoke(this.delegate, new Object[0]);
        }
        catch (Exception e) {
            throw new SoyException("Failed to invoke accessor " + this.accessorMethod.getName() + " on instance of " + this.delegate.getClass(), (Throwable)e);
        }
        return this.converter.convert(resolvedValue).resolve();
    }

    public boolean equals(@Nonnull SoyValueProvider o) {
        if (!(o instanceof AccessorSoyValueProvider)) {
            return false;
        }
        AccessorSoyValueProvider other = (AccessorSoyValueProvider)o;
        return this.accessorMethod.equals(other.accessorMethod) && this.delegate.equals(other.delegate);
    }
}

