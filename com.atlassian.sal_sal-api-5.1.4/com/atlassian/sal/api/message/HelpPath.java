/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.message;

public interface HelpPath {
    public String getKey();

    public String getUrl();

    public String getTitle();

    public String getAlt();

    public boolean isLocal();
}

