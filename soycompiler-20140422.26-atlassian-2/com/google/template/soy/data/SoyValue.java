/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyValue
extends SoyValueProvider {
    public boolean equals(SoyValue var1);

    public boolean coerceToBoolean();

    public String coerceToString();

    public boolean booleanValue();

    public int integerValue();

    public long longValue();

    public double floatValue();

    public double numberValue();

    public String stringValue();
}

