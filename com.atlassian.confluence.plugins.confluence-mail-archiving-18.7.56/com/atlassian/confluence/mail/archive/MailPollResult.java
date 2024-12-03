/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.archive;

public class MailPollResult {
    private final int mailRetrieved;
    private final boolean success;
    private final String errorMessage;
    private final String mailAccountDescription;

    public static MailPollResult success(String description, int mailRetrieved) {
        return new MailPollResult(description, mailRetrieved, true, "");
    }

    public static MailPollResult failure(String description, String errorMessage) {
        return new MailPollResult(description, 0, false, errorMessage);
    }

    private MailPollResult(String description, int mailRetrieved, boolean success, String errorMessage) {
        this.mailAccountDescription = description;
        this.mailRetrieved = mailRetrieved;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public int getMailRetrieved() {
        return this.mailRetrieved;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getMailAccountDescription() {
        return this.mailAccountDescription;
    }

    public String toString() {
        if (!this.isSuccess()) {
            return this.mailAccountDescription + " mail poll FAILED: " + this.errorMessage;
        }
        return this.mailAccountDescription + " retrieved " + this.mailRetrieved + " new emails";
    }
}

