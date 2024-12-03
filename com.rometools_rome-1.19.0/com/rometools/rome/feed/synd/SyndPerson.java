/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.module.Extendable;

public interface SyndPerson
extends Cloneable,
Extendable {
    public String getName();

    public void setName(String var1);

    public String getUri();

    public void setUri(String var1);

    public String getEmail();

    public void setEmail(String var1);

    public Object clone() throws CloneNotSupportedException;
}

