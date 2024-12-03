/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.business.insights.core.rest.model;

import java.util.Objects;

public class ConfigExportPathRequest {
    private String path;

    public ConfigExportPathRequest() {
    }

    public ConfigExportPathRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfigExportPathRequest that = (ConfigExportPathRequest)o;
        return Objects.equals(this.path, that.path);
    }

    public int hashCode() {
        return Objects.hash(this.path);
    }
}

