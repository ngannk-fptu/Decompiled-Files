/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;

public interface SyndContent
extends Cloneable,
CopyFrom {
    public String getType();

    public void setType(String var1);

    public String getMode();

    public void setMode(String var1);

    public String getValue();

    public void setValue(String var1);

    public Object clone() throws CloneNotSupportedException;
}

