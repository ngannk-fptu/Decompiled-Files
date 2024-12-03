/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

public interface SyndLink {
    public Object clone() throws CloneNotSupportedException;

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();

    public String getRel();

    public void setRel(String var1);

    public String getType();

    public void setType(String var1);

    public String getHref();

    public void setHref(String var1);

    public String getTitle();

    public void setTitle(String var1);

    public String getHreflang();

    public void setHreflang(String var1);

    public long getLength();

    public void setLength(long var1);
}

