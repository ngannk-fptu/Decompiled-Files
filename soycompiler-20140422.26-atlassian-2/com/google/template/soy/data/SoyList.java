/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyList
extends SoyMap {
    public int length();

    @Nonnull
    public List<? extends SoyValueProvider> asJavaList();

    @Nonnull
    public List<? extends SoyValue> asResolvedJavaList();

    public SoyValue get(int var1);

    public SoyValueProvider getProvider(int var1);
}

