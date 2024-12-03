/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.CopyFrom;
import java.io.Serializable;

public interface Module
extends Cloneable,
CopyFrom,
Serializable {
    public String getUri();

    public Object clone() throws CloneNotSupportedException;
}

