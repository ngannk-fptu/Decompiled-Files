/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import java.io.Serializable;

public class CustomPageSettings
implements Serializable {
    private String header;
    private String footer;
    private String sidebar;
    public static final CustomPageSettings DEFAULT_SETTINGS = new CustomPageSettings("", "", "");

    public CustomPageSettings(String header, String footer, String sidebar) {
        this.header = header;
        this.footer = footer;
        this.sidebar = sidebar;
    }

    public String getHeader() {
        return this.header;
    }

    public String getFooter() {
        return this.footer;
    }

    public String getSidebar() {
        return this.sidebar;
    }
}

