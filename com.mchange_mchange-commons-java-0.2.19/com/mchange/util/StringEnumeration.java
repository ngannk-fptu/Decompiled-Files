/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.io.IOStringEnumeration;
import com.mchange.util.MEnumeration;

public interface StringEnumeration
extends MEnumeration,
IOStringEnumeration {
    @Override
    public boolean hasMoreStrings();

    @Override
    public String nextString();
}

