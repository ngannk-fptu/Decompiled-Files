/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import net.fortuna.ical4j.util.HostInfo;

public class InetAddressHostInfo
implements HostInfo {
    private final InetAddress hostAddress;

    public InetAddressHostInfo() throws SocketException {
        this(InetAddressHostInfo.findNonLoopbackAddress());
    }

    public InetAddressHostInfo(InetAddress address) {
        this.hostAddress = address;
    }

    @Override
    public String getHostName() {
        return this.hostAddress.getHostName();
    }

    private static InetAddress findNonLoopbackAddress() throws SocketException {
        Enumeration<NetworkInterface> enumInterfaceAddress = NetworkInterface.getNetworkInterfaces();
        while (enumInterfaceAddress.hasMoreElements()) {
            NetworkInterface netIf = enumInterfaceAddress.nextElement();
            Enumeration<InetAddress> enumInetAdress = netIf.getInetAddresses();
            while (enumInetAdress.hasMoreElements()) {
                InetAddress address = enumInetAdress.nextElement();
                if (address.isLoopbackAddress()) continue;
                return address;
            }
        }
        return null;
    }
}

