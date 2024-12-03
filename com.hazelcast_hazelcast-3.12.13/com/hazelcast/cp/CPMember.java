/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp;

import com.hazelcast.core.Endpoint;
import com.hazelcast.nio.Address;

public interface CPMember
extends Endpoint {
    @Override
    public String getUuid();

    public Address getAddress();
}

