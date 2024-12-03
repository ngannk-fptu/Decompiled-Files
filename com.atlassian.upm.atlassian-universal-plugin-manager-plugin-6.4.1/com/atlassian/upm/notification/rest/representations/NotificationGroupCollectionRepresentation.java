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

import com.atlassian.upm.notification.rest.representations.NotificationGroupRepresentation;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationGroupCollectionRepresentation {
    @JsonProperty
    private final Collection<NotificationGroupRepresentation> notificationGroups;
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private int totalNotificationCount = 0;

    @JsonCreator
    public NotificationGroupCollectionRepresentation(@JsonProperty(value="notificationGroups") Collection<NotificationGroupRepresentation> notificationGroups, @JsonProperty(value="links") Map<String, URI> links) {
        this.notificationGroups = Collections.unmodifiableCollection(notificationGroups);
        this.links = Collections.unmodifiableMap(links);
        for (NotificationGroupRepresentation notificationGroup : notificationGroups) {
            this.totalNotificationCount += notificationGroup.getNotificationCount();
        }
    }

    public Collection<NotificationGroupRepresentation> getNotificationGroups() {
        return this.notificationGroups;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public int getTotalNotificationCount() {
        return this.totalNotificationCount;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

