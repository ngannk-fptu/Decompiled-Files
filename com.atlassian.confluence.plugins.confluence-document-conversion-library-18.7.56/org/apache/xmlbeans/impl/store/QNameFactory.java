/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;

public interface QNameFactory {
    public QName getQName(String var1, String var2);

    public QName getQName(String var1, String var2, String var3);

    public QName getQName(char[] var1, int var2, int var3, char[] var4, int var5, int var6);

    public QName getQName(char[] var1, int var2, int var3, char[] var4, int var5, int var6, char[] var7, int var8, int var9);
}

