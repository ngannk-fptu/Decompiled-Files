/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

public final class ConfigRenderOptions {
    private final boolean originComments;
    private final boolean comments;
    private final boolean formatted;
    private final boolean json;
    private final boolean showEnvVariableValues;

    private ConfigRenderOptions(boolean originComments, boolean comments, boolean formatted, boolean json, boolean showEnvVariableValues) {
        this.originComments = originComments;
        this.comments = comments;
        this.formatted = formatted;
        this.json = json;
        this.showEnvVariableValues = showEnvVariableValues;
    }

    public static ConfigRenderOptions defaults() {
        return new ConfigRenderOptions(true, true, true, true, true);
    }

    public static ConfigRenderOptions concise() {
        return new ConfigRenderOptions(false, false, false, true, true);
    }

    public ConfigRenderOptions setComments(boolean value) {
        if (value == this.comments) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, value, this.formatted, this.json, this.showEnvVariableValues);
    }

    public boolean getComments() {
        return this.comments;
    }

    public ConfigRenderOptions setOriginComments(boolean value) {
        if (value == this.originComments) {
            return this;
        }
        return new ConfigRenderOptions(value, this.comments, this.formatted, this.json, this.showEnvVariableValues);
    }

    public boolean getOriginComments() {
        return this.originComments;
    }

    public ConfigRenderOptions setFormatted(boolean value) {
        if (value == this.formatted) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, this.comments, value, this.json, this.showEnvVariableValues);
    }

    public boolean getFormatted() {
        return this.formatted;
    }

    public ConfigRenderOptions setJson(boolean value) {
        if (value == this.json) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, this.comments, this.formatted, value, this.showEnvVariableValues);
    }

    public ConfigRenderOptions setShowEnvVariableValues(boolean value) {
        if (value == this.showEnvVariableValues) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, this.comments, this.formatted, this.json, value);
    }

    public boolean getShowEnvVariableValues() {
        return this.showEnvVariableValues;
    }

    public boolean getJson() {
        return this.json;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ConfigRenderOptions(");
        if (this.originComments) {
            sb.append("originComments,");
        }
        if (this.comments) {
            sb.append("comments,");
        }
        if (this.formatted) {
            sb.append("formatted,");
        }
        if (this.json) {
            sb.append("json,");
        }
        if (this.showEnvVariableValues) {
            sb.append("showEnvVariableValues,");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }
}

