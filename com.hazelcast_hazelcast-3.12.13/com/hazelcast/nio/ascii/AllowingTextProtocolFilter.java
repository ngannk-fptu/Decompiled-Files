/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.nio.ascii.TextProtocolFilter;
import com.hazelcast.nio.tcp.TcpIpConnection;

public class AllowingTextProtocolFilter
implements TextProtocolFilter {
    public static final AllowingTextProtocolFilter INSTANCE = new AllowingTextProtocolFilter();

    @Override
    public void filterConnection(String commandLine, TcpIpConnection connection) {
    }
}

