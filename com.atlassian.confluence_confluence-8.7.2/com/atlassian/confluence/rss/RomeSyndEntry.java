/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rss;

import java.util.Collection;
import java.util.Date;

interface RomeSyndEntry {
    public void setTitle(String var1);

    public void setLink(String var1);

    public void setUri(String var1);

    public void setAuthor(String var1);

    public void setPublishedDate(Date var1);

    public void setUpdatedDate(Date var1);

    public void setDescription(String var1, String var2);

    public void setCategoryNames(Collection<String> var1);
}

