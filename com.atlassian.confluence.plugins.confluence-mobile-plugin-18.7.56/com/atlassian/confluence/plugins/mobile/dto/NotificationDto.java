/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.dto.ActionDto;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationDto {
    @JsonProperty
    private long id;
    @JsonProperty
    private boolean read;
    @JsonProperty
    private ActionDto action;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRead() {
        return this.read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public ActionDto getAction() {
        return this.action;
    }

    public void setAction(ActionDto action) {
        this.action = action;
    }
}

