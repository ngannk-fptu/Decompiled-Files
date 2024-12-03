/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response$StatusType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.internal.integration.jira.rest;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SimpleRestError {
    @XmlElement
    private final int statusCode;
    @XmlElement
    private final String message;
    @XmlElement
    private final String reason;

    public SimpleRestError() {
        this.statusCode = 0;
        this.message = null;
        this.reason = null;
    }

    public SimpleRestError(Response.StatusType statusType, String message) {
        this(statusType.getStatusCode(), statusType.getReasonPhrase(), message);
    }

    public SimpleRestError(int status, String message) {
        this(status, null, message);
    }

    SimpleRestError(int status, String reason, String message) {
        this.statusCode = status;
        this.message = message;
        this.reason = reason;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getReason() {
        return this.reason;
    }
}

