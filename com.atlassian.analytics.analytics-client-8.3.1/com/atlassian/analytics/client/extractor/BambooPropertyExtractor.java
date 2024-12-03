/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.PluginPropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.Collections;
import java.util.Map;

public class BambooPropertyExtractor
implements PropertyExtractor {
    private final UserManager userManager;
    private final PropertyExtractorHelper helper;

    public BambooPropertyExtractor(UserManager userManager) {
        this.userManager = userManager;
        this.helper = new PropertyExtractorHelper(Collections.emptySet(), new PluginPropertyContributor());
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
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        return remoteUserKey != null ? remoteUserKey.getStringValue() : null;
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

