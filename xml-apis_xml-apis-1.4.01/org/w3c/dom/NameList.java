/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom;

public interface NameList {
    public String getName(int var1);

    public String getNamespaceURI(int var1);

    public int getLength();

    public boolean contains(String var1);

    public boolean containsNS(String var1, String var2);
}

