/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crucible.event.UserActionEvent
 *  com.atlassian.crucible.spi.data.UserData
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.PluginPropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.crucible.event.UserActionEvent;
import com.atlassian.crucible.spi.data.UserData;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Collections;
import java.util.Map;

public class FecruPropertyExtractor
implements PropertyExtractor {
    private final UserManager userManager;
    private final PropertyExtractorHelper helper;

    public FecruPropertyExtractor(UserManager userManager) {
        this.userManager = userManager;
        this.helper = new PropertyExtractorHelper(Collections.emptySet(), new PluginPropertyContributor());
    }

    @Override
    public Map<String, Object> extractProperty(String name, Object value) {
        return this.helper.extractProperty(name, value);
    }

    @Override
    public boolean isExcluded(String name) {
        return false;
    }

    @Override
    public String extractName(Object event) {
        return this.helper.extractName(event);
    }

    @Override
    public String extractUser(Object event, Map<String, Object> properties) {
        if (event instanceof UserActionEvent) {
            UserData actioner = ((UserActionEvent)event).getActioner();
            if (actioner != null) {
                return actioner.getUserName();
            }
            return null;
        }
        UserProfile remoteUser = this.userManager.getRemoteUser();
        if (remoteUser != null) {
            return remoteUser.getUsername();
        }
        return null;
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

