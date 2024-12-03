/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.applinks.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="applinks.view.entitylinks.admin")
public class EntityLinksAdminViewEvent {
    private final String typeId;
    private final String entityKey;

    public EntityLinksAdminViewEvent(String typeId, String entityKey) {
        this.typeId = typeId;
        this.entityKey = entityKey;
    }

    public String getTypeId() {
        return this.typeId;
    }

    public String getEntityKey() {
        return this.entityKey;
    }
}

