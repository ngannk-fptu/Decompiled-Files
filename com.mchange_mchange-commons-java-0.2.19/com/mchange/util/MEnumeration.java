/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.io.IOEnumeration;
import com.mchange.util.impl.EmptyMEnumeration;
import java.util.Enumeration;

public interface MEnumeration
extends IOEnumeration,
Enumeration {
    public static final MEnumeration EMPTY = EmptyMEnumeration.SINGLETON;

    @Override
    public Object nextElement();

    @Override
    public boolean hasMoreElements();
}

