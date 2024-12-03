/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

public interface Identicator {
    public boolean identical(Object var1, Object var2);

    public int hash(Object var1);
}

