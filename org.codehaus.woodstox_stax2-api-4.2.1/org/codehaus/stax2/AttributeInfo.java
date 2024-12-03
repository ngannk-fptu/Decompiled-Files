/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

public interface AttributeInfo {
    public int getAttributeCount();

    public int findAttributeIndex(String var1, String var2);

    public int getIdAttributeIndex();

    public int getNotationAttributeIndex();
}

