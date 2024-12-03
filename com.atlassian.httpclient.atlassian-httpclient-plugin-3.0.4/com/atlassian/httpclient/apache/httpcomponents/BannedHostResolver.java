/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.DefaultHostResolver;
import com.atlassian.httpclient.apache.httpcomponents.IpAddressMatcher;
import com.atlassian.httpclient.api.BannedHostException;
import com.atlassian.httpclient.api.HostResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

public class BannedHostResolver
implements HostResolver {
    private final List<IpAddressMatcher> cidrs;

    public BannedHostResolver(List<String> bannedCidrs) {
        this.cidrs = bannedCidrs.stream().map(IpAddressMatcher::new).collect(Collectors.toList());
    }

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        InetAddress[] addresses = DefaultHostResolver.INSTANCE.resolve(host);
        if (this.isBanned(addresses)) {
            throw new BannedHostException("The host " + host + " has been blocked for access");
        }
        return addresses;
    }

    private boolean isBanned(InetAddress[] addresses) {
        for (IpAddressMatcher cidr : this.cidrs) {
            for (InetAddress address : addresses) {
                String hostAddress = address.getHostAddress();
                if (!cidr.matches(hostAddress)) continue;
                return true;
            }
        }
        return false;
    }
}

