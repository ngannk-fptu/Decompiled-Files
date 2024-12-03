/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.naming;

public interface NameCoder {
    public String encodeNode(String var1);

    public String encodeAttribute(String var1);

    public String decodeNode(String var1);

    public String decodeAttribute(String var1);
}

