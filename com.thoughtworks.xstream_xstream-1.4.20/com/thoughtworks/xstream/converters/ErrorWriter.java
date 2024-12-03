/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import java.util.Iterator;

public interface ErrorWriter {
    public void add(String var1, String var2);

    public void set(String var1, String var2);

    public String get(String var1);

    public Iterator keys();
}

