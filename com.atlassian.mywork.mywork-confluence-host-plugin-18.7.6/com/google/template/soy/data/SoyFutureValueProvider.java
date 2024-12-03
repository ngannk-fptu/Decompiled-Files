/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyAbstractCachingValueProvider;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

public final class SoyFutureValueProvider
extends SoyAbstractCachingValueProvider {
    private final SoyValueHelper valueHelper;
    private final Future<?> future;

    public SoyFutureValueProvider(SoyValueHelper valueHelper, Future<?> future) {
        this.valueHelper = valueHelper;
        this.future = future;
    }

    @Override
    @Nonnull
    protected final SoyValue compute() {
        try {
            return this.valueHelper.convert(this.future.get()).resolve();
        }
        catch (ExecutionException e) {
            throw new SoyDataException("Error dereferencing future", e.getCause());
        }
        catch (Exception e) {
            throw new SoyDataException("Error dereferencing future", e);
        }
    }
}

