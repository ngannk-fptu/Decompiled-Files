/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.DnsResolver
 */
package com.amazonaws.http;

import com.amazonaws.DnsResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DelegatingDnsResolver
implements org.apache.http.conn.DnsResolver {
    private final DnsResolver delegate;

    public DelegatingDnsResolver(DnsResolver delegate) {
        this.delegate = delegate;
    }

    public InetAddress[] resolve(String host) throws UnknownHostException {
        return this.delegate.resolve(host);
    }
}

