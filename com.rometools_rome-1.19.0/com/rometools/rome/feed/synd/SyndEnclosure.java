/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;

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

