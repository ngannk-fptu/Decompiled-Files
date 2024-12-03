/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public enum ConnectionType {
    NONE(false, false),
    MEMBER(true, true),
    JAVA_CLIENT(false, true),
    CSHARP_CLIENT(false, true),
    CPP_CLIENT(false, true),
    PYTHON_CLIENT(false, true),
    RUBY_CLIENT(false, true),
    NODEJS_CLIENT(false, true),
    GO_CLIENT(false, true),
    BINARY_CLIENT(false, true),
    REST_CLIENT(false, false),
    MEMCACHE_CLIENT(false, false);

    final boolean member;
    final boolean binary;

    private ConnectionType(boolean member, boolean binary) {
        this.member = member;
        this.binary = binary;
    }

    public boolean isBinary() {
        return this.binary;
    }

    public boolean isClient() {
        return !this.member;
    }
}

