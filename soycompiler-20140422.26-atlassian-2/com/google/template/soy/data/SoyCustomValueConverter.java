/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyValueConverter;
import com.google.template.soy.data.SoyValueProvider;

public interface SoyCustomValueConverter {
    public SoyValueProvider convert(SoyValueConverter var1, Object var2);
}

