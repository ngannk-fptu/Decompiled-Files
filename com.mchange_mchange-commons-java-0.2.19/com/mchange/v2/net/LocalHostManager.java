/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class LocalHostManager {
    Set localAddresses;
    Set knownGoodNames;
    Set knownBadNames;

    public synchronized void update() throws SocketException {
        HashSet<InetAddress> hashSet = new HashSet<InetAddress>();
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            Enumeration<InetAddress> enumeration2 = networkInterface.getInetAddresses();
            while (enumeration2.hasMoreElements()) {
                hashSet.add(enumeration2.nextElement());
            }
        }
        this.localAddresses = Collections.unmodifiableSet(hashSet);
        this.knownGoodNames = new HashSet();
        this.knownBadNames = new HashSet();
    }

    public synchronized Set getLocalAddresses() {
        return this.localAddresses;
    }

    public synchronized boolean isLocalAddress(InetAddress inetAddress) {
        return this.localAddresses.contains(inetAddress);
    }

    public synchronized boolean isLocalHostName(String string) {
        if (this.knownGoodNames.contains(string)) {
            return true;
        }
        if (this.knownBadNames.contains(string)) {
            return false;
        }
        try {
            InetAddress inetAddress = InetAddress.getByName(string);
            if (this.localAddresses.contains(inetAddress)) {
                this.knownGoodNames.add(string);
                return true;
            }
            this.knownBadNames.add(string);
            return false;
        }
        catch (UnknownHostException unknownHostException) {
            this.knownBadNames.add(string);
            return false;
        }
    }

    public LocalHostManager() throws SocketException {
        this.update();
    }

    public static void main(String[] stringArray) {
        try {
            LocalHostManager localHostManager = new LocalHostManager();
            System.out.println(localHostManager.getLocalAddresses());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

