/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.RomeSyndEntry;

interface RomeSyndFeed {
    public void setTitle(String var1);

    public void setLink(String var1);

    public void setUri(String var1);

    public void setDescription(String var1);

    public RomeSyndEntry addEntry();

    public int getEntryCount();
}

