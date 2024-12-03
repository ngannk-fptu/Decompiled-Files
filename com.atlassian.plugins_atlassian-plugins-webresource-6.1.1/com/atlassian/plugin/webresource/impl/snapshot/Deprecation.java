/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.snapshot;

import javax.annotation.Nonnull;

public class Deprecation {
    private final String moduleCompleteKey;
    private String sinceVersion;
    private String removeInVersion;
    private String alternative;
    private String extraInfo;

    public Deprecation(@Nonnull String moduleCompleteKey) {
        this.moduleCompleteKey = moduleCompleteKey;
    }

    public String getSinceVersion() {
        return this.sinceVersion;
    }

    public void setSinceVersion(String sinceVersion) {
        this.sinceVersion = this.clean(sinceVersion);
    }

    public String getRemoveInVersion() {
        return this.removeInVersion;
    }

    public void setRemoveInVersion(String removeInVersion) {
        this.removeInVersion = this.clean(removeInVersion);
    }

    public String getAlternative() {
        return this.alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = this.clean(alternative);
    }

    public String getExtraInfo() {
        return this.extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = this.clean(extraInfo);
    }

    public String buildLogMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[DEPRECATED] \"");
        sb.append(this.moduleCompleteKey);
        sb.append("\" has been deprecated");
        if (this.getSinceVersion() != null) {
            sb.append(" since ");
            sb.append(this.getSinceVersion());
        }
        sb.append(" and will be removed in ");
        if (this.getRemoveInVersion() != null) {
            sb.append(this.getRemoveInVersion());
        } else {
            sb.append("a future release");
        }
        sb.append(".");
        if (this.getAlternative() != null) {
            sb.append(" Use ");
            sb.append(this.getAlternative());
            sb.append(" instead.");
        }
        if (this.getExtraInfo() != null) {
            sb.append(" ");
            sb.append(this.getExtraInfo());
        }
        return sb.toString();
    }

    private String clean(String input) {
        String val = input != null ? input.trim().replaceAll("\\s+", " ") : "";
        return val.length() > 0 ? val : null;
    }
}

