/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ip;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

final class InetAddresses {
    private static final int IPV4_PART_COUNT = 4;
    private static final int IPV6_PART_COUNT = 8;

    private InetAddresses() {
    }

    public static InetAddress forString(String ipString) {
        byte[] addr = InetAddresses.textToNumericFormatV4(ipString);
        if (addr == null) {
            addr = InetAddresses.textToNumericFormatV6(ipString);
        }
        if (addr == null) {
            throw new IllegalArgumentException(String.format("'%s' is not an IP string literal.", ipString));
        }
        try {
            return InetAddress.getByAddress(addr);
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException(String.format("'%s' is extremely broken.", ipString), e);
        }
    }

    public static boolean isInetAddress(String ipString) {
        try {
            InetAddresses.forString(ipString);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] textToNumericFormatV4(String ipString) {
        if (ipString.contains(":")) {
            return null;
        }
        String[] address = ipString.split("\\.");
        if (address.length != 4) {
            return null;
        }
        byte[] bytes = new byte[4];
        try {
            for (int i = 0; i < bytes.length; ++i) {
                int piece = Integer.parseInt(address[i]);
                if (piece < 0 || piece > 255) {
                    return null;
                }
                if (address[i].startsWith("0") && address[i].length() != 1) {
                    return null;
                }
                bytes[i] = (byte)piece;
            }
        }
        catch (NumberFormatException ex) {
            return null;
        }
        return bytes;
    }

    private static byte[] textToNumericFormatV6(String ipString) {
        int totalParts;
        int i;
        String[] parts;
        if (!ipString.contains(":")) {
            return null;
        }
        if (ipString.contains(":::")) {
            return null;
        }
        if (ipString.contains(".") && (ipString = InetAddresses.convertDottedQuadToHex(ipString)) == null) {
            return null;
        }
        ByteBuffer rawBytes = ByteBuffer.allocate(16);
        int partsHi = 0;
        int partsLo = 0;
        String[] addressHalves = ipString.split("::", 2);
        if (!addressHalves[0].equals("")) {
            parts = addressHalves[0].split(":", 8);
            try {
                for (i = 0; i < parts.length; ++i) {
                    if (parts[i].equals("")) {
                        return null;
                    }
                    int piece = Integer.parseInt(parts[i], 16);
                    rawBytes.putShort(2 * i, (short)piece);
                }
                partsHi = parts.length;
            }
            catch (NumberFormatException ex) {
                return null;
            }
        } else {
            partsHi = 1;
        }
        if (addressHalves.length > 1) {
            if (!addressHalves[1].equals("")) {
                parts = addressHalves[1].split(":", 8);
                try {
                    for (i = 0; i < parts.length; ++i) {
                        int partsIndex = parts.length - i - 1;
                        if (parts[partsIndex].equals("")) {
                            return null;
                        }
                        int piece = Integer.parseInt(parts[partsIndex], 16);
                        int bytesIndex = 2 * (8 - i - 1);
                        rawBytes.putShort(bytesIndex, (short)piece);
                    }
                    partsLo = parts.length;
                }
                catch (NumberFormatException ex) {
                    return null;
                }
            } else {
                partsLo = 1;
            }
        }
        if ((totalParts = partsHi + partsLo) > 8) {
            return null;
        }
        if (addressHalves.length == 1 && totalParts != 8) {
            return null;
        }
        return rawBytes.array();
    }

    private static String convertDottedQuadToHex(String ipString) {
        int lastColon = ipString.lastIndexOf(58);
        String initialPart = ipString.substring(0, lastColon + 1);
        String dottedQuad = ipString.substring(lastColon + 1);
        byte[] quad = InetAddresses.textToNumericFormatV4(dottedQuad);
        if (quad == null) {
            return null;
        }
        String penultimate = Integer.toHexString((quad[0] & 0xFF) << 8 | quad[1] & 0xFF);
        String ultimate = Integer.toHexString((quad[2] & 0xFF) << 8 | quad[3] & 0xFF);
        return initialPart + penultimate + ":" + ultimate;
    }

    public static InetAddress forUriString(String hostAddr) {
        InetAddress retval = null;
        try {
            retval = InetAddresses.forString(hostAddr);
            if (retval instanceof Inet4Address) {
                return retval;
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        if (!hostAddr.startsWith("[") || !hostAddr.endsWith("]")) {
            throw new IllegalArgumentException("Not a valid address: \"" + hostAddr + '\"');
        }
        retval = InetAddresses.forString(hostAddr.substring(1, hostAddr.length() - 1));
        if (retval instanceof Inet6Address) {
            return retval;
        }
        throw new IllegalArgumentException("Not a valid address: \"" + hostAddr + '\"');
    }
}

