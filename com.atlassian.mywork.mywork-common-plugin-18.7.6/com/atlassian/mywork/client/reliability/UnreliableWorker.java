/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.client.reliability;

import com.atlassian.mywork.client.reliability.UnreliableTask;
import com.atlassian.mywork.client.reliability.UnreliableTaskListener;

public interface UnreliableWorker {
    public void start(UnreliableTask var1, UnreliableTaskListener var2);
}

