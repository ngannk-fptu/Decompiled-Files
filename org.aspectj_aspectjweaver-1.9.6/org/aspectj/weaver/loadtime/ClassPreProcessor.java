/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.security.ProtectionDomain;

public interface ClassPreProcessor {
    public void initialize();

    public byte[] preProcess(String var1, byte[] var2, ClassLoader var3, ProtectionDomain var4);

    public void prepareForRedefinition(ClassLoader var1, String var2);
}

