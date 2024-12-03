/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import javax.xml.ws.WebServiceFeature;

public interface WSFeatureList
extends Iterable<WebServiceFeature> {
    public boolean isEnabled(@NotNull Class<? extends WebServiceFeature> var1);

    @Nullable
    public <F extends WebServiceFeature> F get(@NotNull Class<F> var1);

    @NotNull
    public WebServiceFeature[] toArray();

    public void mergeFeatures(@NotNull WebServiceFeature[] var1, boolean var2);

    public void mergeFeatures(@NotNull Iterable<WebServiceFeature> var1, boolean var2);
}

