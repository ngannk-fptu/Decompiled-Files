/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.SocketInterceptor;
import java.io.IOException;
import java.net.Socket;

public interface MemberSocketInterceptor
extends SocketInterceptor {
    public void onAccept(Socket var1) throws IOException;
}

