/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public interface ExtendedHierarchicalStreamReader
extends HierarchicalStreamReader {
    public String peekNextChild();
}

