/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyValueProvider;
import javax.annotation.Nonnull;

public interface SoyValueConverter {
    @Nonnull
    public SoyValueProvider convert(Object var1);
}

