/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;

public interface SyndCategory
extends Cloneable,
CopyFrom {
    public String getName();

    public void setName(String var1);

    public String getTaxonomyUri();

    public void setTaxonomyUri(String var1);

    public void setLabel(String var1);

    public String getLabel();

    public Object clone() throws CloneNotSupportedException;
}

