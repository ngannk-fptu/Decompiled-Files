/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import java.io.InputStream;
import java.util.function.Function;

public interface TwoPhaseResourceTransformer {
    public void loadTwoPhaseProperties(ResourceLocation var1, Function<String, InputStream> var2);

    public boolean hasTwoPhaseProperties();
}

