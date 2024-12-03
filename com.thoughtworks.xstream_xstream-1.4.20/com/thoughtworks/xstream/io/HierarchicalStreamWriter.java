/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io;

public interface HierarchicalStreamWriter {
    public void startNode(String var1);

    public void addAttribute(String var1, String var2);

    public void setValue(String var1);

    public void endNode();

    public void flush();

    public void close();

    public HierarchicalStreamWriter underlyingWriter();
}

