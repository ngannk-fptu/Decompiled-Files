/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.request.rest.representations;

import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginRequestRepresentation
implements Comparable<PluginRequestRepresentation> {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String pluginKey;
    @JsonProperty
    private final String pluginName;
    @JsonProperty
    private final UserProfileRepresentation user;
    @JsonProperty
    private final Date timestamp;
    @JsonProperty
    private final String message;

    @JsonCreator
    public PluginRequestRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="pluginName") String pluginName, @JsonProperty(value="user") UserProfileRepresentation user, @JsonProperty(value="timestamp") Date timestamp, @JsonProperty(value="message") String message) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.pluginKey = pluginKey;
        this.pluginName = pluginName;
        this.user = user;
        this.timestamp = timestamp;
        this.message = message;
    }

    public PluginRequestRepresentation(String pluginKey, PluginRequest pluginRequest, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder) {
        this.links = linkBuilder.buildLinkForSelf(uriBuilder.buildPluginRequestResourceUri(pluginRequest.getPluginKey(), pluginRequest.getUser().getUserKey())).build();
        this.pluginKey = pluginKey;
        this.pluginName = pluginRequest.getPluginName();
        this.user = new UserProfileRepresentation(pluginRequest.getUser(), uriBuilder);
        this.timestamp = pluginRequest.getTimestamp().toDate();
        this.message = pluginRequest.getMessage().getOrElse((String)null);
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public UserProfileRepresentation getUser() {
        return this.user;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    @Override
    public int compareTo(PluginRequestRepresentation request) {
        return this.getTimestamp().compareTo(request.getTimestamp());
    }

    public static PluginRequestRepresentation anonymize(PluginRequestRepresentation from) {
        return new PluginRequestRepresentation(from.getLinks(), from.getPluginKey(), from.getPluginName(), null, from.getTimestamp(), null);
    }

    public static class UserProfileRepresentation {
        @JsonProperty
        private final String userKey;
        @JsonProperty
        private final String displayName;
        @JsonProperty
        private final URI userProfileUri;

        @JsonCreator
        public UserProfileRepresentation(@JsonProperty(value="userKey") String userKey, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="userProfileUri") URI userProfileUri) {
            this.userKey = userKey;
            this.displayName = displayName;
            this.userProfileUri = userProfileUri;
        }

        public UserProfileRepresentation(UserProfile userProfile, UpmUriBuilder uriBuilder) {
            this.userKey = userProfile.getUserKey().getStringValue();
            this.displayName = userProfile.getFullName();
            this.userProfileUri = uriBuilder.buildAbsoluteProfileUri(userProfile);
        }

        public String getUserKey() {
            return this.userKey;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public URI getUserProfileUri() {
            return this.userProfileUri;
        }
    }
}

