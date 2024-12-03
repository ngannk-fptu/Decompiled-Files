/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.MembershipListener;

public interface InitialMembershipListener
extends MembershipListener {
    public void init(InitialMembershipEvent var1);
}

