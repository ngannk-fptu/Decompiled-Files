/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.impl.client.BasicResponseHandler
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.consumer.menu.client.navigation;

import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.navigation.ApplicationNavigationLinks;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.atlassian.plugins.navlink.producer.navigation.links.LinkSource;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationLinkResponseHandler
implements ResponseHandler<ApplicationNavigationLinks> {
    private static final Logger logger = LoggerFactory.getLogger(NavigationLinkResponseHandler.class);
    private static final String WEIGHT_FIELD = "weight";
    private static final String LINKS_FIELD = "links";
    private static final String BASE_FIELD = "base";
    private final ResponseHandler<String> basicResponseHandler = new BasicResponseHandler();
    private final String applicationType;
    private final String applicationId;
    private final Locale locale;

    public NavigationLinkResponseHandler(RemoteApplicationWithCapabilities application, Locale locale) {
        this.applicationType = application.getType();
        this.applicationId = application.getApplicationLinkId();
        this.locale = locale;
    }

    public ApplicationNavigationLinks handleResponse(HttpResponse response) throws IOException {
        return this.decodeResponse((String)this.basicResponseHandler.handleResponse(response));
    }

    private ApplicationNavigationLinks decodeResponse(@Nullable String responseBody) throws IOException {
        if (responseBody == null) {
            return new ApplicationNavigationLinks(this.locale, Collections.emptySet());
        }
        return this.parseBody(responseBody);
    }

    protected ApplicationNavigationLinks parseBody(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodes = (JsonNode)mapper.readValue(responseBody, JsonNode.class);
        String baseUrl = this.parseBaseUrl(jsonNodes);
        Set<NavigationLink> result = this.parseLinks(jsonNodes, baseUrl);
        return new ApplicationNavigationLinks(this.locale, result);
    }

    private String parseBaseUrl(JsonNode jsonNodes) {
        String baseUrl = jsonNodes.path(LINKS_FIELD).path(BASE_FIELD).getTextValue();
        return Strings.emptyToNull((String)baseUrl);
    }

    private Set<NavigationLink> parseLinks(JsonNode jsonNodes, String baseUrl) {
        HashSet<NavigationLink> result = new HashSet<NavigationLink>();
        Iterator fieldNames = jsonNodes.getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String)fieldNames.next();
            if (fieldName.equals(LINKS_FIELD) || !jsonNodes.path(fieldName).isArray()) continue;
            Iterator elements = jsonNodes.path(fieldName).getElements();
            while (elements.hasNext()) {
                NavigationLink navigationLink = this.createNavigationLink(fieldName, (JsonNode)elements.next(), baseUrl);
                if (navigationLink == null) continue;
                result.add(navigationLink);
            }
        }
        return result;
    }

    @Nullable
    private NavigationLink createNavigationLink(String menuKey, JsonNode navigationLinkNode, String baseUrl) {
        String href = navigationLinkNode.path("href").getTextValue();
        String label = navigationLinkNode.path("label").getTextValue();
        String icon = navigationLinkNode.path("icon").getTextValue();
        String tooltip = navigationLinkNode.path("tooltip").getTextValue();
        if (!Strings.isNullOrEmpty((String)href) && !Strings.isNullOrEmpty((String)label)) {
            return ((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)new NavigationLinkBuilder().key(menuKey)).href(href)).baseUrl(baseUrl)).iconUrl(icon)).label(label).tooltip(tooltip).applicationType(this.applicationType)).source(LinkSource.remote(this.applicationId))).weight(this.parseWeight(navigationLinkNode))).build();
        }
        return null;
    }

    private int parseWeight(JsonNode jsonNodes) {
        JsonNode weightNode = jsonNodes.get(WEIGHT_FIELD);
        if (weightNode != null) {
            if (weightNode.isNumber()) {
                return weightNode.asInt();
            }
            logger.warn("Encountered non-numeric weight property in parsed JSON response");
            logger.debug("Non-numeric weight property in response " + jsonNodes.toString());
        }
        return Integer.MAX_VALUE;
    }
}

