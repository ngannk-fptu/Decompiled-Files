/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ssl;

import java.util.Properties;
import javax.net.ssl.SSLContext;

public interface SSLContextFactory {
    public void init(Properties var1) throws Exception;

    public SSLContext getSSLContext();
}

