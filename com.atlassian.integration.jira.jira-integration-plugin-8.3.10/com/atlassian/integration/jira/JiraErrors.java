/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.integration.jira;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraErrors {
    public List<String> errorMessages = new ArrayList<String>();
    public Map<String, String> errors = new HashMap<String, String>();
    private int responseCode;

    public JiraErrors addAllErrors(List<String> messages) {
        for (String message : messages) {
            this.addError(message);
        }
        return this;
    }

    public JiraErrors addAllErrors(Map<String, String> messages) {
        for (Map.Entry<String, String> message : messages.entrySet()) {
            this.addError(message.getKey(), message.getValue());
        }
        return this;
    }

    public JiraErrors addError(String msg) {
        this.errorMessages.add(msg);
        return this;
    }

    public JiraErrors addError(String key, String value) {
        this.errors.put(key, value);
        return this;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public boolean hasErrors() {
        return this.errorMessages != null && !this.errorMessages.isEmpty() || this.errors != null && !this.errors.isEmpty();
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String toString() {
        return "JiraErrors{errorMessages=" + this.errorMessages + ", errors=" + this.errors + '}';
    }
}

