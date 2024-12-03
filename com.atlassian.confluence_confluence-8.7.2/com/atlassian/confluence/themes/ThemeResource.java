/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

public interface ThemeResource {
    public Type getType();

    public String getLocation();

    public String getCompleteModuleKey();

    public String getName();

    public boolean isIeOnly();

    public static enum Type {
        JAVSCRIPT,
        CSS;

    }
}

