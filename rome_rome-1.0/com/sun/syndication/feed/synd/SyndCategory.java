/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd;

public interface SyndCategory
extends Cloneable {
    public String getName();

    public void setName(String var1);

    public String getTaxonomyUri();

    public void setTaxonomyUri(String var1);

    public Object clone() throws CloneNotSupportedException;
}

