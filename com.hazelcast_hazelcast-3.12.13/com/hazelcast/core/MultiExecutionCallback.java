/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Member;
import java.util.Map;

public interface MultiExecutionCallback {
    public void onResponse(Member var1, Object var2);

    public void onComplete(Map<Member, Object> var1);
}

