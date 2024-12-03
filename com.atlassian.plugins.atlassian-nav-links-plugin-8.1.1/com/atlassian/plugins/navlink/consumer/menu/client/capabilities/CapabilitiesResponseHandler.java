/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.impl.client.BasicResponseHandler
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.plugins.navlink.consumer.menu.client.capabilities;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilitiesBuilder;
import com.atlassian.plugins.navlink.util.date.UniversalDateFormatter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class CapabilitiesResponseHandler
implements ResponseHandler<RemoteApplicationWithCapabilities> {
    private static final String CAPABILITIES = "capabilities";
    protected final ReadOnlyApplicationLink applicationLink;
    protected final ResponseHandler<String> basicHandler = new BasicResponseHandler();

    public CapabilitiesResponseHandler(ReadOnlyApplicationLink applicationLink) {
        this.applicationLink = (ReadOnlyApplicationLink)Preconditions.checkNotNull((Object)applicationLink);
    }

    public RemoteApplicationWithCapabilities handleResponse(HttpResponse response) throws IOException {
        String responseBody = (String)this.basicHandler.handleResponse(response);
        return StringUtils.isNotBlank((CharSequence)responseBody) ? this.parseBody(responseBody) : null;
    }

    protected RemoteApplicationWithCapabilities parseBody(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodes = (JsonNode)mapper.readValue(responseBody, JsonNode.class);
        return new RemoteApplicationWithCapabilitiesBuilder().setApplicationLinkId(this.applicationLink.getId().toString()).setSelfUrl(this.parseSelfUrl(jsonNodes)).setType(jsonNodes.path("application").getTextValue()).setBuildDateTime(this.getBuildDate(jsonNodes)).addAllCapabilities(this.collectCapabilities(jsonNodes)).build();
    }

    @Nullable
    private String parseSelfUrl(JsonNode jsonNodes) {
        return jsonNodes.path("links").path("self").getTextValue();
    }

    @Nullable
    private ZonedDateTime getBuildDate(JsonNode jsonNodes) {
        String buildDateString = jsonNodes.path("buildDate").getTextValue();
        if (!Strings.isNullOrEmpty((String)buildDateString)) {
            return UniversalDateFormatter.parse(buildDateString);
        }
        return null;
    }

    private Map<String, String> collectCapabilities(JsonNode jsonNodes) {
        ImmutableMap.Builder capabilitiesBuilder = ImmutableMap.builder();
        Iterator capabilities = jsonNodes.path(CAPABILITIES).getFieldNames();
        while (capabilities.hasNext()) {
            String capabilityUrl;
            String capabilityName = (String)capabilities.next();
            if (Strings.isNullOrEmpty((String)capabilityName) || Strings.isNullOrEmpty((String)(capabilityUrl = jsonNodes.path(CAPABILITIES).path(capabilityName).getTextValue()))) continue;
            capabilitiesBuilder.put((Object)capabilityName, (Object)capabilityUrl);
        }
        return capabilitiesBuilder.build();
    }
}

