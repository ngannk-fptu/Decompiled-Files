/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ProtocolType {
    MEMBER(1, "HZC"),
    CLIENT(1, "CB2"),
    WAN(Integer.MAX_VALUE, "HZC"),
    REST(1, "HTTP"),
    MEMCACHE(1, "Memcached");

    private static final Set<ProtocolType> PROTOCOL_TYPES_SET;
    private static final ProtocolType[] PROTOCOL_TYPES;
    private final int serverSocketCardinality;
    private final String descriptor;

    private ProtocolType(int serverSocketCardinality, String descriptor) {
        this.serverSocketCardinality = serverSocketCardinality;
        this.descriptor = descriptor;
    }

    public static ProtocolType valueOf(int ordinal) {
        return PROTOCOL_TYPES[ordinal];
    }

    public static Set<ProtocolType> valuesAsSet() {
        return PROTOCOL_TYPES_SET;
    }

    public int getServerSocketCardinality() {
        return this.serverSocketCardinality;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

    static {
        EnumSet<ProtocolType> allProtocolTypes = EnumSet.allOf(ProtocolType.class);
        PROTOCOL_TYPES_SET = Collections.unmodifiableSet(allProtocolTypes);
        PROTOCOL_TYPES = ProtocolType.values();
    }
}

