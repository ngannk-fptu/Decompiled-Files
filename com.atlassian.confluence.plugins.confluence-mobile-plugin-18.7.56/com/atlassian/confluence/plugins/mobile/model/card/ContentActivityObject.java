/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.model.card.ActivityObject;
import java.util.Date;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public class ContentActivityObject
implements ActivityObject.ContentActivityObject {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String excerpt;
    @JsonProperty
    private Person actionBy;
    @JsonProperty
    private Date actionTime;
    @JsonProperty
    private Map<String, Object> properties;

    public ContentActivityObject(Long id, Person actionBy, Date actionTime, String excerpt, Map<String, Object> properties) {
        this.id = id;
        this.actionBy = actionBy;
        this.actionTime = actionTime != null ? new Date(actionTime.getTime()) : null;
        this.excerpt = excerpt;
        this.properties = properties;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public Person getActionBy() {
        return this.actionBy;
    }

    @Override
    public Date getActionTime() {
        return this.actionTime != null ? new Date(this.actionTime.getTime()) : null;
    }

    @Override
    public String getExcerpt() {
        return this.excerpt;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }
}

