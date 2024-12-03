/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public interface SocketInterceptor {
    public void init(Properties var1);

    public void onConnect(Socket var1) throws IOException;
}

