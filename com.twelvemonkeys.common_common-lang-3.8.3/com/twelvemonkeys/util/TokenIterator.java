/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.util.Enumeration;
import java.util.Iterator;

public interface TokenIterator
extends Iterator<String>,
Enumeration<String> {
    public boolean hasMoreTokens();

    public String nextToken();

    public void reset();
}

