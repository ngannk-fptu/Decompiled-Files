/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.streaming;

import javax.xml.namespace.QName;

public interface Attributes {
    public int getLength();

    public boolean isNamespaceDeclaration(int var1);

    public QName getName(int var1);

    public String getURI(int var1);

    public String getLocalName(int var1);

    public String getPrefix(int var1);

    public String getValue(int var1);

    public int getIndex(QName var1);

    public int getIndex(String var1, String var2);

    public int getIndex(String var1);

    public String getValue(QName var1);

    public String getValue(String var1, String var2);

    public String getValue(String var1);
}

