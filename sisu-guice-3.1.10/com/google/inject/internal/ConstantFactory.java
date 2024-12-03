/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializable;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ConstantFactory<T>
implements InternalFactory<T> {
    private final Initializable<T> initializable;

    public ConstantFactory(Initializable<T> initializable) {
        this.initializable = initializable;
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency dependency, boolean linked) throws ErrorsException {
        return this.initializable.get(errors);
    }

    public String toString() {
        return Objects.toStringHelper(ConstantFactory.class).add("value", this.initializable).toString();
    }
}

