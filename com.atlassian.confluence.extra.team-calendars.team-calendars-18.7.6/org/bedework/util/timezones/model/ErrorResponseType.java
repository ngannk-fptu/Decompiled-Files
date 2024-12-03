/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import org.bedework.util.misc.ToString;

public class ErrorResponseType {
    protected String error;
    protected String description;

    public ErrorResponseType(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String value) {
        this.error = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("error", this.getError());
        ts.append("description", this.getDescription());
        return ts.toString();
    }
}

