/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

final class NetUtilInitializations {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtilInitializations.class);

    private NetUtilInitializations() {
    }

    static Inet4Address createLocalhost4() {
        byte[] LOCALHOST4_BYTES = new byte[]{127, 0, 0, 1};
        Inet4Address localhost4 = null;
        try {
            localhost4 = (Inet4Address)InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
        }
        return localhost4;
    }

    static Inet6Address createLocalhost6() {
        byte[] LOCALHOST6_BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        Inet6Address localhost6 = null;
        try {
            localhost6 = (Inet6Address)InetAddress.getByAddress("localhost", LOCALHOST6_BYTES);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
        }
        return localhost6;
    }

    static Collection<NetworkInterface> networkInterfaces() {
        ArrayList<NetworkInterface> networkInterfaces;
        block5: {
            networkInterfaces = new ArrayList<NetworkInterface>();
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces != null) {
                    while (interfaces.hasMoreElements()) {
                        networkInterfaces.add(interfaces.nextElement());
                    }
                }
            }
            catch (SocketException e) {
                logger.warn("Failed to retrieve the list of available network interfaces", e);
            }
            catch (NullPointerException e) {
                if (PlatformDependent.isAndroid()) break block5;
                throw e;
            }
        }
        return Collections.unmodifiableList(networkInterfaces);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static NetworkIfaceAndInetAddress determineLoopback(Collection<NetworkInterface> networkInterfaces, Inet4Address localhost4, Inet6Address localhost6) {
        Enumeration<InetAddress> i;
        ArrayList<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
        for (NetworkInterface iface : networkInterfaces) {
            if (!SocketUtils.addressesFromNetworkInterface(iface).hasMoreElements()) continue;
            ifaces.add(iface);
        }
        NetworkInterface loopbackIface = null;
        InetAddress loopbackAddr = null;
        block8: for (NetworkInterface iface : ifaces) {
            i = SocketUtils.addressesFromNetworkInterface(iface);
            while (i.hasMoreElements()) {
                InetAddress addr = i.nextElement();
                if (!addr.isLoopbackAddress()) continue;
                loopbackIface = iface;
                loopbackAddr = addr;
                break block8;
            }
        }
        if (loopbackIface == null) {
            try {
                for (NetworkInterface iface : ifaces) {
                    if (!iface.isLoopback() || !(i = SocketUtils.addressesFromNetworkInterface(iface)).hasMoreElements()) continue;
                    loopbackIface = iface;
                    loopbackAddr = i.nextElement();
                    break;
                }
                if (loopbackIface == null) {
                    logger.warn("Failed to find the loopback interface");
                }
            }
            catch (SocketException e) {
                logger.warn("Failed to find the loopback interface", e);
            }
        }
        if (loopbackIface != null) {
            logger.debug("Loopback interface: {} ({}, {})", loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress());
        } else if (loopbackAddr == null) {
            try {
                if (NetworkInterface.getByInetAddress(localhost6) != null) {
                    logger.debug("Using hard-coded IPv6 localhost address: {}", (Object)localhost6);
                    loopbackAddr = localhost6;
                }
            }
            catch (Exception exception) {
            }
            finally {
                if (loopbackAddr == null) {
                    logger.debug("Using hard-coded IPv4 localhost address: {}", (Object)localhost4);
                    loopbackAddr = localhost4;
                }
            }
        }
        return new NetworkIfaceAndInetAddress(loopbackIface, loopbackAddr);
    }

    static final class NetworkIfaceAndInetAddress {
        private final NetworkInterface iface;
        private final InetAddress address;

        NetworkIfaceAndInetAddress(NetworkInterface iface, InetAddress address) {
            this.iface = iface;
            this.address = address;
        }

        public NetworkInterface iface() {
            return this.iface;
        }

        public InetAddress address() {
            return this.address;
        }
    }
}

