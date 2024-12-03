/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import javax.xml.ws.WebServiceFeature;

public interface WSDLFeaturedObject
extends WSDLObject {
    @Nullable
    public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> var1);

    @NotNull
    public WSFeatureList getFeatures();

    public void addFeature(@NotNull WebServiceFeature var1);
}

