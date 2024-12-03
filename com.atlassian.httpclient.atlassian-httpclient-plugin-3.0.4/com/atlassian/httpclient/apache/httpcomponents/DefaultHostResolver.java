/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.api.HostResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;

public class DefaultHostResolver
implements HostResolver {
    public static final HostResolver INSTANCE = new DefaultHostResolver();

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return SystemDefaultDnsResolver.INSTANCE.resolve(host);
    }
}

