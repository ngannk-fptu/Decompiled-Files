/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces;

public class SpaceLogo {
    private String downloadPath;
    private int type;
    public static final int DEFAULT = 0;
    public static final int GLOBAL = 1;
    public static final int CUSTOM = 2;
    public static final String DEFAULT_LOGO_PATH = "/images/logo/default-space-logo.svg";
    public static final SpaceLogo DEFAULT_SPACE_LOGO = new SpaceLogo("/images/logo/default-space-logo.svg", 0);

    public SpaceLogo(String downloadPath, int type) {
        this.downloadPath = downloadPath;
        this.type = type;
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public boolean isDefaultLogo() {
        return this.type == 0;
    }

    public boolean isGlobalLogo() {
        return this.type == 1;
    }

    public boolean isCustomLogo() {
        return this.type == 2;
    }
}

