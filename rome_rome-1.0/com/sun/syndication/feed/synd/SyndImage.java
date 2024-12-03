/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;

public interface SyndImage
extends Cloneable,
CopyFrom {
    public String getTitle();

    public void setTitle(String var1);

    public String getUrl();

    public void setUrl(String var1);

    public String getLink();

    public void setLink(String var1);

    public String getDescription();

    public void setDescription(String var1);

    public Object clone() throws CloneNotSupportedException;
}

