/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

public interface StylesheetManager {
    public String getSpaceStylesheet(String var1);

    public String getSpaceStylesheet(String var1, boolean var2);

    public String getGlobalStylesheet();

    public void addGlobalStylesheet(String var1);

    public void addSpaceStylesheet(String var1, String var2);

    public void removeSpaceStylesheet(String var1);

    public void removeGlobalStylesheet();
}

