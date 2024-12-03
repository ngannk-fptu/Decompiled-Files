/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import java.util.EventListener;

public interface MembershipListener
extends EventListener {
    public void memberAdded(MembershipEvent var1);

    public void memberRemoved(MembershipEvent var1);

    public void memberAttributeChanged(MemberAttributeEvent var1);
}

