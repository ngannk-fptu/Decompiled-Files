/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.client.reliability;

public interface UnreliableTaskListener {
    public void succeeded(String var1);

    public void failed(Throwable var1);

    public void cancel();
}

