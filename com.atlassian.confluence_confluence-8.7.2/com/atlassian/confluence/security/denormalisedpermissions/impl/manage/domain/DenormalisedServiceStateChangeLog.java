/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain;

import com.atlassian.confluence.core.NotExportable;

public class DenormalisedServiceStateChangeLog
implements NotExportable {
    private long id;
    private String message;
    private long timestamp;
    private String messageLevel;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageLevel() {
        return this.messageLevel;
    }

    public void setMessageLevel(String messageLevel) {
        this.messageLevel = messageLevel;
    }
}

