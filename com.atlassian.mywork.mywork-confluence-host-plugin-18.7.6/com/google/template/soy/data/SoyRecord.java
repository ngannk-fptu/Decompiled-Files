/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyRecord
extends SoyValue {
    public boolean hasField(String var1);

    public SoyValue getField(String var1);

    public SoyValueProvider getFieldProvider(String var1);
}

