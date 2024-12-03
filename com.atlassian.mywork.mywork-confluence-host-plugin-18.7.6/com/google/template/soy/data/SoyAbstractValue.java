/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class SoyAbstractValue
implements SoyValue {
    @Override
    @Nonnull
    public SoyValue resolve() {
        return this;
    }

    @Override
    public boolean equals(SoyValueProvider other) {
        if (other instanceof SoyValue) {
            return this.equals((SoyValue)other);
        }
        return other.equals(this);
    }

    @Override
    public boolean booleanValue() {
        throw new SoyDataException("Expecting boolean value but instead encountered type " + this.getClass().getSimpleName());
    }

    @Override
    public int integerValue() {
        throw new SoyDataException("Expecting integer value but instead encountered type " + this.getClass().getSimpleName());
    }

    @Override
    public long longValue() {
        return this.integerValue();
    }

    @Override
    public double floatValue() {
        throw new SoyDataException("Expecting float value but instead encountered type " + this.getClass().getSimpleName());
    }

    @Override
    public double numberValue() {
        throw new SoyDataException("Expecting number value but instead encountered type " + this.getClass().getSimpleName());
    }

    @Override
    public String stringValue() {
        throw new SoyDataException("Expecting string value but instead encountered type " + this.getClass().getSimpleName());
    }
}

