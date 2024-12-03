/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.business.insights.core.rest.model;

import java.util.List;
import java.util.Objects;

public class OptOutConfigurationRequest {
    private String type;
    private List<String> keys;

    public OptOutConfigurationRequest() {
    }

    public OptOutConfigurationRequest(String type, List<String> keys) {
        this.type = type;
        this.keys = keys;
    }

    public String getType() {
        return this.type;
    }

    public List<String> getKeys() {
        return this.keys;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptOutConfigurationRequest)) {
            return false;
        }
        OptOutConfigurationRequest that = (OptOutConfigurationRequest)o;
        return Objects.equals(this.type, that.type) && Objects.equals(this.keys, that.keys);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.keys);
    }

    public String toString() {
        return "OptOutConfigurationRequest{type='" + this.type + '\'' + ", keys=" + this.keys + '}';
    }
}

