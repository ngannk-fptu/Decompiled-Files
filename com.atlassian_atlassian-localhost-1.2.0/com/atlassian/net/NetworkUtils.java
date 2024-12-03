/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    public static final String getLocalHostName() throws UnknownHostException {
        InetAddress host = InetAddress.getLocalHost();
        String hostname = host.getHostName();
        if (hostname != null && !hostname.equals("localhost")) {
            return hostname;
        }
        return host.getCanonicalHostName();
    }

    public static final InetAddress getLANAddress() throws UnknownHostException {
        try {
            InetAddress nonLoopbackNonSiteLocal = null;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nic = interfaces.nextElement();
                Enumeration<InetAddress> addresses = nic.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress()) continue;
                    if (addr.isSiteLocalAddress()) {
                        return addr;
                    }
                    if (nonLoopbackNonSiteLocal != null) continue;
                    nonLoopbackNonSiteLocal = addr;
                }
            }
            if (nonLoopbackNonSiteLocal != null) {
                return nonLoopbackNonSiteLocal;
            }
            InetAddress fallbackAddress = InetAddress.getLocalHost();
            if (fallbackAddress == null) {
                throw new UnknownHostException();
            }
            return fallbackAddress;
        }
        catch (SocketException se) {
            throw new UnknownHostException();
        }
    }

    public static final int addressToInt(InetAddress address) {
        return ByteBuffer.wrap(address.getAddress()).getInt();
    }
}

