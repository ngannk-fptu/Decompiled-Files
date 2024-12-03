/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.client.reliability;

import com.atlassian.mywork.client.reliability.UnreliableTask;
import java.util.concurrent.Future;

public interface ReliabilityService {
    public Future<String> submit(UnreliableTask var1);
}

