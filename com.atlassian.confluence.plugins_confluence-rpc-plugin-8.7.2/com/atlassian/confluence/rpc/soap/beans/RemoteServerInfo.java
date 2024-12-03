/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.util.GeneralUtil;
import java.util.StringTokenizer;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteServerInfo {
    private int majorVersion;
    private int minorVersion;
    private int patchLevel;
    private boolean developmentBuild;
    private String buildId = GeneralUtil.getBuildNumber();
    private String baseUrl;
    public static final String __PARANAMER_DATA = "";

    public RemoteServerInfo() {
        this.setVersion(GeneralUtil.getVersionNumber());
        this.baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public int getPatchLevel() {
        return this.patchLevel;
    }

    public boolean isDevelopmentBuild() {
        return this.developmentBuild;
    }

    public String getBuildId() {
        return this.buildId;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    private void setVersion(String version) {
        if (version.endsWith("dev") || version.endsWith("DEV")) {
            this.developmentBuild = true;
        }
        StringTokenizer tok = new StringTokenizer(version, ".- ");
        this.majorVersion = this.nextNumber(tok);
        this.minorVersion = this.nextNumber(tok);
        this.patchLevel = this.nextNumber(tok);
    }

    private int nextNumber(StringTokenizer tok) {
        if (!tok.hasMoreTokens()) {
            return 0;
        }
        try {
            return Integer.parseInt(tok.nextToken());
        }
        catch (NumberFormatException e) {
            this.developmentBuild = true;
            return 0;
        }
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

