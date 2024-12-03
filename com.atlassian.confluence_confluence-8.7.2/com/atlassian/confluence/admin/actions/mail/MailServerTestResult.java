/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.actions.mail;

import java.io.Serializable;

public class MailServerTestResult
implements Serializable {
    private static final long serialVersionUID = 7152186177464471859L;
    private final Status status;
    private final String message;

    public MailServerTestResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    static enum Status {
        OK,
        FAILED;

    }
}

