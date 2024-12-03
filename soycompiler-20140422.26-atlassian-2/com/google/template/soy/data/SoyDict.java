/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyDict
extends SoyRecord,
SoyMap {
    @Nonnull
    public Map<String, ? extends SoyValueProvider> asJavaStringMap();

    @Nonnull
    public Map<String, ? extends SoyValue> asResolvedJavaStringMap();
}

