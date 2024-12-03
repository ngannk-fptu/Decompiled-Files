/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Member;

public interface MemberSelector {
    public boolean select(Member var1);
}

