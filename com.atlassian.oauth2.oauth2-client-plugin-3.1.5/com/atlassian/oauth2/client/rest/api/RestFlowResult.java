/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.oauth2.client.rest.api;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;

public class RestFlowResult {
    @XmlElement
    private boolean success;
    @XmlElement
    private String error;

    public RestFlowResult() {
    }

    public RestFlowResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RestFlowResult that = (RestFlowResult)o;
        return this.success == that.success && Objects.equals(this.error, that.error);
    }

    public int hashCode() {
        return Objects.hash(this.success, this.error);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("success", this.getSuccess()).add("error", (Object)this.getError()).toString();
    }
}

