/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.config.MCMutualAuthConfig;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public interface ManagementCenterConnectionFactory {
    public void init(MCMutualAuthConfig var1) throws Exception;

    public URLConnection openConnection(URL var1) throws IOException;
}

