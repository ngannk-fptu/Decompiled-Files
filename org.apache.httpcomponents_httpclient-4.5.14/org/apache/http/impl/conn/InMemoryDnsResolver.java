/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.conn;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.DnsResolver;
import org.apache.http.util.Args;

public class InMemoryDnsResolver
implements DnsResolver {
    private final Log log = LogFactory.getLog(InMemoryDnsResolver.class);
    private final Map<String, InetAddress[]> dnsMap = new ConcurrentHashMap<String, InetAddress[]>();

    public void add(String host, InetAddress ... ips) {
        Args.notNull((Object)host, (String)"Host name");
        Args.notNull((Object)ips, (String)"Array of IP addresses");
        this.dnsMap.put(host, ips);
    }

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        Object[] resolvedAddresses = this.dnsMap.get(host);
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)("Resolving " + host + " to " + Arrays.deepToString(resolvedAddresses)));
        }
        if (resolvedAddresses == null) {
            throw new UnknownHostException(host + " cannot be resolved");
        }
        return resolvedAddresses;
    }
}

