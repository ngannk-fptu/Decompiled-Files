/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.PluginPropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CrowdPropertyExtractor
implements PropertyExtractor {
    private final UserManager userManager;
    private final PropertyExtractorHelper helper;

    public CrowdPropertyExtractor(UserManager userManager) {
        this.userManager = userManager;
        this.helper = new PropertyExtractorHelper((Set<String>)ImmutableSet.of(), new PluginPropertyContributor());
    }

    @Override
    public Map<String, Object> extractProperty(String name, Object value) {
        return this.helper.extractProperty(name, value);
    }

    @Override
    public boolean isExcluded(String name) {
        return this.helper.isExcluded(name);
    }

    @Override
    public String extractName(Object event) {
        return this.helper.extractName(event);
    }

    @Override
    public String extractUser(Object event, Map<String, Object> properties) {
        UserProfile remoteUser = this.userManager.getRemoteUser();
        return remoteUser == null ? null : remoteUser.getUsername();
    }

    @Override
    public Map<String, Object> enrichProperties(Object event) {
        return Collections.emptyMap();
    }

    @Override
    public String extractSubProduct(Object event, String product) {
        return this.helper.extractSubProduct(event, product);
    }

    @Override
    public String getApplicationAccess() {
        return "";
    }

    @Override
    public String extractRequestCorrelationId(RequestInfo request) {
        return this.helper.extractRequestCorrelationId(request);
    }
}

