/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ip;

import com.atlassian.ip.InetAddresses;
import java.net.InetAddress;

public class Subnet {
    private final InetAddress ipAddress;
    private final int mask;

    private Subnet(InetAddress ipAddress, int mask) {
        this.ipAddress = ipAddress;
        this.mask = mask;
    }

    public static Subnet forPattern(String pattern) {
        int mask;
        InetAddress ipAddress;
        if (pattern.matches("(\\d+\\.)*\\*(\\.\\*)*")) {
            String[] parts = pattern.split("\\.");
            int asteriskCount = 0;
            for (int i = parts.length - 1; i >= 0 && parts[i].equals("*"); --i) {
                ++asteriskCount;
            }
            ipAddress = InetAddresses.forString(pattern.replace("*", "0"));
            mask = 32 - 8 * asteriskCount;
        } else if (pattern.contains("/")) {
            String[] addressComponents = pattern.split("/", 2);
            ipAddress = InetAddresses.forString(addressComponents[0]);
            mask = Integer.parseInt(addressComponents[1]);
        } else {
            ipAddress = InetAddresses.forString(pattern);
            mask = 8 * ipAddress.getAddress().length;
        }
        return new Subnet(ipAddress, mask);
    }

    public static Subnet forAddress(InetAddress ipAddress) {
        return new Subnet(ipAddress, ipAddress.getAddress().length * 8);
    }

    public static boolean isValidPattern(String pattern) {
        try {
            Subnet.forPattern(pattern);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public byte[] getAddress() {
        return this.ipAddress.getAddress();
    }

    public int getMask() {
        return this.mask;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Subnet subnet = (Subnet)o;
        if (this.mask != subnet.mask) {
            return false;
        }
        return this.ipAddress.equals(subnet.ipAddress);
    }

    public int hashCode() {
        int result = this.ipAddress.hashCode();
        result = 31 * result + this.mask;
        return result;
    }
}

