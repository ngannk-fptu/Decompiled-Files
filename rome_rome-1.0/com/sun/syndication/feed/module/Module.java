/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.module;

import com.sun.syndication.feed.CopyFrom;
import java.io.Serializable;

public interface Module
extends Cloneable,
CopyFrom,
Serializable {
    public String getUri();

    public Object clone() throws CloneNotSupportedException;
}

