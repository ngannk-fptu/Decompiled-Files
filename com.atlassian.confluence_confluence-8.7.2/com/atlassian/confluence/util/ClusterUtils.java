/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.UnmodifiableIterator
 *  com.google.common.net.InetAddresses
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import org.apache.commons.codec.digest.DigestUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ClusterUtils {
    private static final String CONFLUENCE_ALLOW_LOOPBACK_CLUSTER = "confluence.allow.loopback.cluster";
    private static final NonLoopbackInterfacePredicate NON_LOOPBACK_INTERFACE_PREDICATE = new NonLoopbackInterfacePredicate();
    static final Predicate<NetworkInterface> CLUSTERABLE_INTERFACE_PREDICATE = Boolean.getBoolean("confluence.allow.loopback.cluster") ? Predicates.alwaysTrue() : NON_LOOPBACK_INTERFACE_PREDICATE;

    private ClusterUtils() {
    }

    public static InetAddress resolveName(String name) {
        return ClusterUtils.hashNameToMulticastAddress(name);
    }

    public static InetAddress hashNameToAddress(String name) {
        byte[] hash = DigestUtils.md5((String)name);
        byte[] addrBytes = new byte[]{hash[0], hash[1], hash[2], hash[3]};
        return ClusterUtils.getAddress(addrBytes);
    }

    public static InetAddress hashNameToMulticastAddress(String name) {
        InetAddress addr = ClusterUtils.hashNameToAddress(name);
        byte[] addrBytes = addr.getAddress();
        addrBytes[0] = (byte)(addrBytes[0] | 0xFFFFFFE0);
        addrBytes[0] = (byte)(addrBytes[0] & 0xFFFFFFEF);
        if (addrBytes[0] == -17) {
            addrBytes[0] = -18;
        }
        if (!(addr = ClusterUtils.getAddress(addrBytes)).isMCLinkLocal()) {
            return addr;
        }
        addrBytes[3] = 1;
        return ClusterUtils.getAddress(addrBytes);
    }

    public static Iterator<NetworkInterface> getClusterableInterfaces() throws SocketException {
        return Iterators.filter(ClusterUtils.getAllNetworkInterfaces(), CLUSTERABLE_INTERFACE_PREDICATE);
    }

    public static InetAddress addressFromIpString(String ipString) {
        return InetAddresses.forString((String)ipString);
    }

    private static UnmodifiableIterator<NetworkInterface> getAllNetworkInterfaces() throws SocketException {
        return Iterators.forEnumeration(NetworkInterface.getNetworkInterfaces());
    }

    public static boolean isLoopbackInterface(NetworkInterface iface) {
        return !NON_LOOPBACK_INTERFACE_PREDICATE.apply(iface);
    }

    private static InetAddress getAddress(byte[] addrBytes) {
        try {
            return InetAddress.getByAddress(addrBytes);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static class LoopbackAddressPredicate
    implements Predicate<InetAddress> {
        private LoopbackAddressPredicate() {
        }

        public boolean apply(@NonNull InetAddress address) {
            return address.isLoopbackAddress();
        }
    }

    private static class NonLoopbackInterfacePredicate
    implements Predicate<NetworkInterface> {
        private static final Predicate<InetAddress> NON_LOOPBACK_ADDRESS_PREDICATE = Predicates.not((Predicate)new LoopbackAddressPredicate());

        private NonLoopbackInterfacePredicate() {
        }

        public boolean apply(@NonNull NetworkInterface networkInterface) {
            return Iterators.filter((Iterator)Iterators.forEnumeration(networkInterface.getInetAddresses()), NON_LOOPBACK_ADDRESS_PREDICATE).hasNext();
        }
    }
}

