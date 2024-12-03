/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import lombok.Generated;

public class EmailError {
    private final int code;
    private final String message;

    @Generated
    public EmailError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Generated
    public int getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }
}

