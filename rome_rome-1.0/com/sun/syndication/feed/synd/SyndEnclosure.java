/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;

public interface SyndEnclosure
extends Cloneable,
CopyFrom {
    public String getUrl();

    public void setUrl(String var1);

    public long getLength();

    public void setLength(long var1);

    public String getType();

    public void setType(String var1);
}

