/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.applinks.internal.web.data;

import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.JsonApplinksFeatures;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class ApplinksFeaturesDataProvider
implements WebResourceDataProvider {
    private final JsonApplinksFeatures jsonApplinksFeatures;

    public ApplinksFeaturesDataProvider(ApplinksFeatureService featureService) {
        this.jsonApplinksFeatures = new JsonApplinksFeatures(featureService);
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal((Object)this.jsonApplinksFeatures.allFeatures());
    }
}

