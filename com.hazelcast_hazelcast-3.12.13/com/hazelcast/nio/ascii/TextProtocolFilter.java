/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.nio.tcp.TcpIpConnection;

interface TextProtocolFilter {
    public void filterConnection(String var1, TcpIpConnection var2);
}

