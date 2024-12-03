/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data.internal;

import com.google.template.soy.data.SoyAbstractRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.Nonnull;

public abstract class ParamStore
extends SoyAbstractRecord {
    public static final ParamStore EMPTY_INSTANCE = new ParamStore(){

        @Override
        public void setField(String name, @Nonnull SoyValueProvider valueProvider) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasField(String name) {
            return false;
        }

        @Override
        public SoyValueProvider getFieldProvider(String name) {
            return null;
        }
    };

    public abstract void setField(String var1, @Nonnull SoyValueProvider var2);

    @Override
    public boolean coerceToBoolean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String coerceToString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(SoyValue other) {
        throw new UnsupportedOperationException();
    }
}

