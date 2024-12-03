/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer;

public interface RendererConfiguration {
    public String getWebAppContextPath();

    public boolean isNofollowExternalLinks();

    public boolean isAllowCamelCase();

    public String getCharacterEncoding();
}

