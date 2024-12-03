/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.integration.rest.service;

import com.atlassian.crowd.integration.rest.entity.ErrorEntity;

class CrowdRestException
extends Exception {
    private final ErrorEntity errorEntity;
    private final int statusCode;

    CrowdRestException(String msg, ErrorEntity errorEntity, int statusCode) {
        super(msg);
        this.errorEntity = errorEntity;
        this.statusCode = statusCode;
    }

    ErrorEntity getErrorEntity() {
        return this.errorEntity;
    }

    int getStatusCode() {
        return this.statusCode;
    }
}

