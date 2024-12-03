/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.builder.EqualsBuilder
 *  org.apache.commons.lang.builder.HashCodeBuilder
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.notification;

import com.atlassian.plugin.notifications.api.event.EventRepresentation;
import com.atlassian.plugin.notifications.api.medium.recipient.RecipientRepresentation;
import com.atlassian.plugin.notifications.api.notification.FilterConfiguration;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationRepresentation {
    @JsonProperty
    private final int id;
    @JsonProperty
    private final FilterConfiguration filterConfiguration;
    @JsonProperty
    private final String filterSummary;
    @JsonProperty
    private final int schemeId;
    @JsonProperty
    private final List<RecipientRepresentation> recipients;
    @JsonProperty
    private final List<EventRepresentation> events;

    @JsonCreator
    public NotificationRepresentation(@JsonProperty(value="id") int id, @JsonProperty(value="filterConfiguration") FilterConfiguration filterConfiguration, @JsonProperty(value="filterSummary") String filterSummary, @JsonProperty(value="schemeId") int schemeId, @JsonProperty(value="recipients") List<RecipientRepresentation> recipients, @JsonProperty(value="events") List<EventRepresentation> events) {
        this.id = id;
        this.filterSummary = filterSummary;
        this.schemeId = schemeId;
        this.recipients = recipients;
        this.events = events;
        this.filterConfiguration = filterConfiguration;
    }

    public int getId() {
        return this.id;
    }

    public List<RecipientRepresentation> getRecipients() {
        return this.recipients;
    }

    public List<EventRepresentation> getEvents() {
        return this.events;
    }

    public int getSchemeId() {
        return this.schemeId;
    }

    public FilterConfiguration getFilterConfiguration() {
        return this.filterConfiguration;
    }

    public String getFilterSummary() {
        return this.filterSummary;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        NotificationRepresentation rhs = (NotificationRepresentation)obj;
        return new EqualsBuilder().append(this.id, rhs.id).isEquals();
    }
}

