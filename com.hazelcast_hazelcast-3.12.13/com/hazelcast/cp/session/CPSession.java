/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.session;

import com.hazelcast.nio.Address;

public interface CPSession {
    public long id();

    public long creationTime();

    public long expirationTime();

    public long version();

    public boolean isExpired(long var1);

    public Address endpoint();

    public CPSessionOwnerType endpointType();

    public String endpointName();

    public static enum CPSessionOwnerType {
        SERVER,
        CLIENT;

    }
}

