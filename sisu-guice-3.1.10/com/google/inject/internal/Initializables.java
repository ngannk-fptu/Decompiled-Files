/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class Initializables {
    Initializables() {
    }

    static <T> Initializable<T> of(final T instance) {
        return new Initializable<T>(){

            @Override
            public T get(Errors errors) throws ErrorsException {
                return instance;
            }

            public String toString() {
                return String.valueOf(instance);
            }
        };
    }
}

