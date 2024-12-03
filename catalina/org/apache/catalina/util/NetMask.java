/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.tomcat.util.res.StringManager;

public final class NetMask {
    private static final StringManager sm = StringManager.getManager(NetMask.class);
    private final String expression;
    private final byte[] netaddr;
    private final int nrBytes;
    private final int lastByteShift;
    private final boolean foundPort;
    private final Pattern portPattern;

    public NetMask(String input) {
        int cidr;
        String nonPortPart;
        this.expression = input;
        int portIdx = input.indexOf(59);
        if (portIdx == -1) {
            this.foundPort = false;
            nonPortPart = input;
            this.portPattern = null;
        } else {
            this.foundPort = true;
            nonPortPart = input.substring(0, portIdx);
            try {
                this.portPattern = Pattern.compile(input.substring(portIdx + 1));
            }
            catch (PatternSyntaxException e) {
                throw new IllegalArgumentException(sm.getString("netmask.invalidPort", new Object[]{input}), e);
            }
        }
        int idx = nonPortPart.indexOf(47);
        if (idx == -1) {
            try {
                this.netaddr = InetAddress.getByName(nonPortPart).getAddress();
            }
            catch (UnknownHostException e) {
                throw new IllegalArgumentException(sm.getString("netmask.invalidAddress", new Object[]{nonPortPart}));
            }
            this.nrBytes = this.netaddr.length;
            this.lastByteShift = 0;
            return;
        }
        String addressPart = nonPortPart.substring(0, idx);
        String cidrPart = nonPortPart.substring(idx + 1);
        try {
            this.netaddr = InetAddress.getByName(addressPart).getAddress();
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException(sm.getString("netmask.invalidAddress", new Object[]{addressPart}));
        }
        int addrlen = this.netaddr.length * 8;
        try {
            cidr = Integer.parseInt(cidrPart);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(sm.getString("netmask.cidrNotNumeric", new Object[]{cidrPart}));
        }
        if (cidr < 0) {
            throw new IllegalArgumentException(sm.getString("netmask.cidrNegative", new Object[]{cidrPart}));
        }
        if (cidr > addrlen) {
            throw new IllegalArgumentException(sm.getString("netmask.cidrTooBig", new Object[]{cidrPart, addrlen}));
        }
        this.nrBytes = cidr / 8;
        int remainder = cidr % 8;
        this.lastByteShift = remainder == 0 ? 0 : 8 - remainder;
    }

    public boolean matches(InetAddress addr, int port) {
        if (!this.foundPort) {
            return false;
        }
        String portString = Integer.toString(port);
        if (!this.portPattern.matcher(portString).matches()) {
            return false;
        }
        return this.matches(addr, true);
    }

    public boolean matches(InetAddress addr) {
        return this.matches(addr, false);
    }

    public boolean matches(InetAddress addr, boolean checkedPort) {
        int i;
        if (!checkedPort && this.foundPort) {
            return false;
        }
        byte[] candidate = addr.getAddress();
        if (candidate.length != this.netaddr.length) {
            return false;
        }
        for (i = 0; i < this.nrBytes; ++i) {
            if (this.netaddr[i] == candidate[i]) continue;
            return false;
        }
        if (this.lastByteShift == 0) {
            return true;
        }
        int lastByte = this.netaddr[i] ^ candidate[i];
        return lastByte >> this.lastByteShift == 0;
    }

    public String toString() {
        return this.expression;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NetMask other = (NetMask)o;
        return this.nrBytes == other.nrBytes && this.lastByteShift == other.lastByteShift && Arrays.equals(this.netaddr, other.netaddr);
    }

    public int hashCode() {
        int result = 31 * Arrays.hashCode(this.netaddr) + this.lastByteShift;
        return result;
    }
}

