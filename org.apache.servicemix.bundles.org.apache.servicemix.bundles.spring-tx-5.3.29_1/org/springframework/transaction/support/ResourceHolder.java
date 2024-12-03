/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.support;

public interface ResourceHolder {
    public void reset();

    public void unbound();

    public boolean isVoid();
}

