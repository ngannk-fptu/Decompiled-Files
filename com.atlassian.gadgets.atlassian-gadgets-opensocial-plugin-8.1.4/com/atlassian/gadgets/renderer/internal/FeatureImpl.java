/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.spec.Feature
 *  org.apache.shindig.gadgets.spec.Feature
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.spec.Feature;

public class FeatureImpl
implements Feature {
    private org.apache.shindig.gadgets.spec.Feature shindigFeature;

    public FeatureImpl(org.apache.shindig.gadgets.spec.Feature feature) {
        this.shindigFeature = feature;
    }

    public String getParameterValue(String paramName) {
        return (String)this.shindigFeature.getParams().get(paramName);
    }

    public String getName() {
        return this.shindigFeature.getName();
    }
}

