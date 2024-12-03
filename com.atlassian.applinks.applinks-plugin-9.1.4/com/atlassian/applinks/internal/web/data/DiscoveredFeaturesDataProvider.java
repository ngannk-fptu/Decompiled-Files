/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.applinks.internal.web.data;

import com.atlassian.applinks.internal.common.exception.NotAuthenticatedException;
import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.applinks.internal.feature.FeatureDiscoveryService;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.Collections;
import java.util.Set;

public class DiscoveredFeaturesDataProvider
implements WebResourceDataProvider {
    private final UserManager userManager;
    private final FeatureDiscoveryService featureDiscoveryService;

    public DiscoveredFeaturesDataProvider(UserManager userManager, FeatureDiscoveryService featureDiscoveryService) {
        this.userManager = userManager;
        this.featureDiscoveryService = featureDiscoveryService;
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal(this.getDiscoveredFeatureKeys());
    }

    private Set<String> getDiscoveredFeatureKeys() {
        UserKey userKey = this.userManager.getRemoteUserKey();
        try {
            return userKey != null ? this.featureDiscoveryService.getAllDiscoveredFeatureKeys() : Collections.emptySet();
        }
        catch (NotAuthenticatedException e) {
            throw new IllegalStateException("FeatureDiscoveryService threw authentication exception despite existing user context: " + userKey, e);
        }
    }
}

