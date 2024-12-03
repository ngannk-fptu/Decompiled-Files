/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ip;

import com.atlassian.ip.InetAddresses;
import com.atlassian.ip.Subnet;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IPMatcher {
    private final Set<Subnet> subnets;

    private IPMatcher(Set<Subnet> subnets) {
        this.subnets = new HashSet<Subnet>(subnets);
    }

    public boolean matches(String ipAddress) {
        return this.matches(InetAddresses.forString(ipAddress));
    }

    public boolean matches(InetAddress ipAddress) {
        for (Subnet subnet : this.subnets) {
            if (!this.matches(subnet, ipAddress)) continue;
            return true;
        }
        return false;
    }

    private boolean matches(Subnet subnet, InetAddress ipAddress) {
        byte[] requestIpAddress;
        int nMaskBits = subnet.getMask();
        int oddBits = nMaskBits % 8;
        int nMaskBytes = nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
        byte[] mask = new byte[nMaskBytes];
        byte[] allowedIpAddress = subnet.getAddress();
        if (allowedIpAddress.length != (requestIpAddress = ipAddress.getAddress()).length) {
            return false;
        }
        Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte)-1);
        if (oddBits != 0) {
            int finalByte = (1 << oddBits) - 1;
            mask[mask.length - 1] = (byte)(finalByte <<= 8 - oddBits);
        }
        for (int i = 0; i < mask.length; ++i) {
            if ((allowedIpAddress[i] & mask[i]) == (requestIpAddress[i] & mask[i])) continue;
            return false;
        }
        return true;
    }

    public static boolean isValidPatternOrHost(String patternOrHost) {
        if (patternOrHost == null || patternOrHost.trim().isEmpty()) {
            return false;
        }
        return !IPMatcher.builder().addPatternOrHost(patternOrHost).subnets.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<Subnet> subnets = new HashSet<Subnet>();

        private Builder() {
        }

        public Builder addPatternOrHost(String patternOrHost) {
            if (patternOrHost == null || patternOrHost.length() == 0) {
                throw new IllegalArgumentException("You cannot add an empty pattern or host");
            }
            try {
                this.subnets.add(Subnet.forPattern(patternOrHost));
            }
            catch (IllegalArgumentException e) {
                try {
                    for (InetAddress address : this.getAllByName(patternOrHost)) {
                        this.subnets.add(Subnet.forAddress(address));
                    }
                }
                catch (UnknownHostException unknownHostException) {
                    // empty catch block
                }
            }
            return this;
        }

        public Builder addPattern(String pattern) {
            if (pattern == null || pattern.length() == 0) {
                throw new IllegalArgumentException("You cannot add an empty pattern");
            }
            this.subnets.add(Subnet.forPattern(pattern));
            return this;
        }

        public Builder addSubnet(Subnet subnet) {
            if (subnet == null) {
                throw new NullPointerException("Subnet is null");
            }
            this.subnets.add(subnet);
            return this;
        }

        public IPMatcher build() {
            return new IPMatcher(this.subnets);
        }

        InetAddress[] getAllByName(String host) throws UnknownHostException {
            return InetAddress.getAllByName(host);
        }
    }
}

