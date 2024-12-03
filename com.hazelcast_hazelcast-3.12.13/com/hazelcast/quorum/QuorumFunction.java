/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.core.Member;
import java.util.Collection;

public interface QuorumFunction {
    public boolean apply(Collection<Member> var1);
}

