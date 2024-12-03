/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.service.helper;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraEpicPropertiesHelper {
    public static final String URL = "/rest/greenhopper/1.0/api/epicproperties";
    public static final String TYPE_ID = "epicTypeId";
    public static final String NAME_FIELD = "epicNameField";
    public static final String STATUS_FIELD = "epicStatusField";
    public static final String DONE_STATUS_ID = "epicDoneStatus";
    private final Cache<String, Map<String, String>> cache;

    @Autowired
    public JiraEpicPropertiesHelper(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(this.getClass().getCanonicalName());
    }

    public Map<String, String> getCachedEpicProperties(ReadOnlyApplicationLink jiraAppLink) {
        HashMap properties = (HashMap)this.cache.get((Object)jiraAppLink.getId().get());
        if (properties == null) {
            properties = new HashMap();
        }
        return properties;
    }

    public Map<String, String> getEpicProperties(ReadOnlyApplicationLink jiraAppLink, ApplicationLinkRequest request) throws ResponseException, CredentialsRequiredException {
        HashMap<String, String> properties = new HashMap<String, String>();
        JsonObject result = new JsonParser().parse(request.execute()).getAsJsonObject();
        JsonObject doneStatus = result.getAsJsonObject(DONE_STATUS_ID);
        properties.put(TYPE_ID, result.getAsJsonPrimitive(TYPE_ID).getAsString());
        properties.put(NAME_FIELD, "customfield_" + result.getAsJsonObject(NAME_FIELD).getAsJsonPrimitive("id").getAsString());
        properties.put(STATUS_FIELD, "customfield_" + result.getAsJsonObject(STATUS_FIELD).getAsJsonPrimitive("id").getAsString());
        if (doneStatus != null) {
            properties.put(DONE_STATUS_ID, result.getAsJsonObject(DONE_STATUS_ID).getAsJsonPrimitive("id").getAsString());
        }
        if (this.epicPropertiesDifferent(this.getCachedEpicProperties(jiraAppLink), properties)) {
            this.cache.put((Object)jiraAppLink.getId().get(), properties);
        }
        return properties;
    }

    public boolean epicPropertiesDifferent(Map<String, String> old, Map<String, String> current) {
        return current.size() != old.size() || !current.get(TYPE_ID).equals(old.get(TYPE_ID)) || !current.get(NAME_FIELD).equals(old.get(NAME_FIELD)) || !current.get(STATUS_FIELD).equals(old.get(STATUS_FIELD)) || current.get(DONE_STATUS_ID) != null && !current.get(DONE_STATUS_ID).equals(old.get(DONE_STATUS_ID));
    }
}

