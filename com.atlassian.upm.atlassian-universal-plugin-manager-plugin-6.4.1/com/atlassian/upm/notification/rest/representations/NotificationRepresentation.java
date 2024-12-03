/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.notification.rest.representations;

import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.rest.representations.InstalledMarketplacePluginRepresentation;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationRepresentation {
    @JsonProperty
    private final String notificationType;
    @JsonProperty
    private final InstalledMarketplacePluginRepresentation plugin;
    @JsonProperty
    private final Boolean dismissed;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final Map<String, URI> links;

    @JsonCreator
    public NotificationRepresentation(@JsonProperty(value="notificationType") String notificationType, @JsonProperty(value="plugin") InstalledMarketplacePluginRepresentation plugin, @JsonProperty(value="dismissed") Boolean dismissed, @JsonProperty(value="title") String title, @JsonProperty(value="message") String message, @JsonProperty(value="links") Map<String, URI> links) {
        this(NotificationType.fromKey(notificationType), plugin, dismissed, title, message, links);
    }

    public NotificationRepresentation(NotificationType notificationType, InstalledMarketplacePluginRepresentation plugin, Boolean dismissed, String title, String message, Map<String, URI> links) {
        this.notificationType = Objects.requireNonNull(notificationType, "notificationType").getKey();
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.dismissed = dismissed;
        this.title = Objects.requireNonNull(title, "title");
        this.message = Objects.requireNonNull(message, "message");
        this.links = Collections.unmodifiableMap(links);
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public InstalledMarketplacePluginRepresentation getPlugin() {
        return this.plugin;
    }

    public Boolean isDismissed() {
        return this.dismissed;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

