/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.core.ConfluenceActionSupport;

public interface ThemeHelper {
    public String getText(String var1);

    public ConfluenceActionSupport getAction();

    public String getDomainName();

    public String getSpaceKey();

    public String getSpaceName();

    public String renderConfluenceMacro(String var1);
}

