/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels;

public interface DisplayableLabel {
    public boolean isRealTitleSafeForUrl();

    public String getName();

    public String getRealTitle();

    public String getUrlPath();

    public String getUrlPath(String var1);
}

