/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.httpclient.apache.httpcomponents;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

public final class IpAddressMatcher {
    private final int nMaskBits;
    private final InetAddress requiredAddress;

    public IpAddressMatcher(String ipAddress) {
        if (ipAddress.indexOf(47) > 0) {
            String[] addressAndMask = ipAddress.split("/");
            ipAddress = addressAndMask[0];
            this.nMaskBits = Integer.parseInt(addressAndMask[1]);
        } else {
            this.nMaskBits = -1;
        }
        this.requiredAddress = this.parseAddress(ipAddress);
    }

    public boolean matches(HttpServletRequest request) {
        return this.matches(request.getRemoteAddr());
    }

    public boolean matches(String address) {
        InetAddress remoteAddress = this.parseAddress(address);
        if (!this.requiredAddress.getClass().equals(remoteAddress.getClass())) {
            return false;
        }
        if (this.nMaskBits < 0) {
            return remoteAddress.equals(this.requiredAddress);
        }
        byte[] remAddr = remoteAddress.getAddress();
        byte[] reqAddr = this.requiredAddress.getAddress();
        int oddBits = this.nMaskBits % 8;
        int nMaskBytes = this.nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
        byte[] mask = new byte[nMaskBytes];
        Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte)-1);
        if (oddBits != 0) {
            int finalByte = (1 << oddBits) - 1;
            mask[mask.length - 1] = (byte)(finalByte <<= 8 - oddBits);
        }
        for (int i = 0; i < mask.length; ++i) {
            if ((remAddr[i] & mask[i]) == (reqAddr[i] & mask[i])) continue;
            return false;
        }
        return true;
    }

    private InetAddress parseAddress(String address) {
        try {
            return InetAddress.getByName(address);
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException("Failed to parse address" + address, e);
        }
    }
}

