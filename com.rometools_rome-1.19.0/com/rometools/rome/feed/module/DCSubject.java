/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.CopyFrom;

public interface DCSubject
extends Cloneable,
CopyFrom {
    public String getTaxonomyUri();

    public void setTaxonomyUri(String var1);

    public String getValue();

    public void setValue(String var1);

    public Object clone() throws CloneNotSupportedException;
}

