/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;

public interface SyndImage
extends Cloneable,
CopyFrom {
    public String getTitle();

    public void setTitle(String var1);

    public String getUrl();

    public void setUrl(String var1);

    public Integer getWidth();

    public void setWidth(Integer var1);

    public Integer getHeight();

    public void setHeight(Integer var1);

    public String getLink();

    public void setLink(String var1);

    public String getDescription();

    public void setDescription(String var1);

    public Object clone() throws CloneNotSupportedException;
}

