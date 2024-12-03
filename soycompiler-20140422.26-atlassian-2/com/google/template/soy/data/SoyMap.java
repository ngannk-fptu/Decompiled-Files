/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyMap
extends SoyValue {
    public int getItemCnt();

    @Nonnull
    public Iterable<? extends SoyValue> getItemKeys();

    public boolean hasItem(SoyValue var1);

    public SoyValue getItem(SoyValue var1);

    public SoyValueProvider getItemProvider(SoyValue var1);
}

