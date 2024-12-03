/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

public class Base64 {
    private static final byte[] ALPHASET;
    private static final int I6O2 = 252;
    private static final int O6I2 = 3;
    private static final int I4O4 = 240;
    private static final int O4I4 = 15;
    private static final int I2O6 = 192;
    private static final int O2I6 = 63;
    private static final int[] CODES;

    public static String encode(String s) {
        byte[] sBytes = s.getBytes();
        sBytes = Base64.encode(sBytes);
        s = new String(sBytes);
        return s;
    }

    public static String decode(String s) throws IllegalArgumentException {
        s = s.replaceAll("\n", "");
        s = s.replaceAll("\r", "");
        byte[] sBytes = s.getBytes();
        sBytes = Base64.decode(sBytes);
        s = new String(sBytes);
        return s;
    }

    public static byte[] encode(byte[] dData) {
        if (dData == null) {
            throw new IllegalArgumentException("Cannot encode null");
        }
        byte[] eData = new byte[(dData.length + 2) / 3 * 4];
        int eIndex = 0;
        for (int i = 0; i < dData.length; i += 3) {
            byte d2 = 0;
            byte d3 = 0;
            int pad = 0;
            byte d1 = dData[i];
            if (i + 1 < dData.length) {
                d2 = dData[i + 1];
                if (i + 2 < dData.length) {
                    d3 = dData[i + 2];
                } else {
                    pad = 1;
                }
            } else {
                pad = 2;
            }
            byte e1 = ALPHASET[(d1 & 0xFC) >> 2];
            byte e2 = ALPHASET[(d1 & 3) << 4 | (d2 & 0xF0) >> 4];
            byte e3 = ALPHASET[(d2 & 0xF) << 2 | (d3 & 0xC0) >> 6];
            byte e4 = ALPHASET[d3 & 0x3F];
            eData[eIndex++] = e1;
            eData[eIndex++] = e2;
            eData[eIndex++] = pad < 2 ? (int)e3 : 61;
            eData[eIndex++] = pad < 1 ? (int)e4 : 61;
        }
        return eData;
    }

    public static byte[] decode(byte[] eData) {
        if (eData == null) {
            throw new IllegalArgumentException("Cannot decode null");
        }
        byte[] cleanEData = (byte[])eData.clone();
        int cleanELength = 0;
        for (byte element : eData) {
            if (element >= 256 || CODES[element] >= 64) continue;
            cleanEData[cleanELength++] = element;
        }
        int dLength = cleanELength / 4 * 3;
        switch (cleanELength % 4) {
            case 3: {
                dLength += 2;
                break;
            }
            case 2: {
                ++dLength;
            }
        }
        byte[] dData = new byte[dLength];
        int dIndex = 0;
        for (int i = 0; i < eData.length; i += 4) {
            if (i + 3 > eData.length) {
                throw new IllegalArgumentException("byte array is not a valid com.rometools.rome.io.impl.Base64 encoding");
            }
            int e1 = CODES[cleanEData[i]];
            int e2 = CODES[cleanEData[i + 1]];
            int e3 = CODES[cleanEData[i + 2]];
            int e4 = CODES[cleanEData[i + 3]];
            dData[dIndex++] = (byte)(e1 << 2 | e2 >> 4);
            if (dIndex < dData.length) {
                dData[dIndex++] = (byte)(e2 << 4 | e3 >> 2);
            }
            if (dIndex >= dData.length) continue;
            dData[dIndex++] = (byte)(e3 << 6 | e4);
        }
        return dData;
    }

    public static void main(String[] args) throws Exception {
        String s = "\nPGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCI+V2UncmUgcHJvcG9zaW5nIDxhIGhy\nZWY9Imh0dHA6Ly93d3cuZ29vZ2xlLmNvbS9jb3Jwb3JhdGUvc29mdHdhcmVfcHJpbmNpcGxlcy5odG1sIj5z\nb21lIGd1aWRlbGluZXMgPC9hPnRvIGhlbHAgY3VyYiB0aGUgcHJvYmxlbSBvZiBJbnRlcm5ldCBzb2Z0d2Fy\nZSB0aGF0IGluc3RhbGxzIGl0c2VsZiB3aXRob3V0IHRlbGxpbmcgeW91LCBvciBiZWhhdmVzIGJhZGx5IG9u\nY2UgaXQgZ2V0cyBvbiB5b3VyIGNvbXB1dGVyLiBXZSd2ZSBiZWVuIGhlYXJpbmcgYSBsb3Qgb2YgY29tcGxh\naW50cyBhYm91dCB0aGlzIGxhdGVseSBhbmQgaXQgc2VlbXMgdG8gYmUgZ2V0dGluZyB3b3JzZS4gV2UgdGhp\nbmsgaXQncyBpbXBvcnRhbnQgdGhhdCB5b3UgcmV0YWluIGNvbnRyb2wgb2YgeW91ciBjb21wdXRlciBhbmQg\ndGhhdCB0aGVyZSBiZSBzb21lIGNsZWFyIHN0YW5kYXJkcyBpbiBvdXIgaW5kdXN0cnkuIExldCB1cyBrbm93\nIGlmIHlvdSB0aGluayB0aGVzZSBndWlkZWxpbmVzIGFyZSB1c2VmdWwgb3IgaWYgeW91IGhhdmUgc3VnZ2Vz\ndGlvbnMgdG8gaW1wcm92ZSB0aGVtLgo8YnIgLz4KPGJyIC8+Sm9uYXRoYW4gUm9zZW5iZXJnCjxiciAvPgo8\nL2Rpdj4K\n";
        System.out.println(Base64.decode("\nPGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCI+V2UncmUgcHJvcG9zaW5nIDxhIGhy\nZWY9Imh0dHA6Ly93d3cuZ29vZ2xlLmNvbS9jb3Jwb3JhdGUvc29mdHdhcmVfcHJpbmNpcGxlcy5odG1sIj5z\nb21lIGd1aWRlbGluZXMgPC9hPnRvIGhlbHAgY3VyYiB0aGUgcHJvYmxlbSBvZiBJbnRlcm5ldCBzb2Z0d2Fy\nZSB0aGF0IGluc3RhbGxzIGl0c2VsZiB3aXRob3V0IHRlbGxpbmcgeW91LCBvciBiZWhhdmVzIGJhZGx5IG9u\nY2UgaXQgZ2V0cyBvbiB5b3VyIGNvbXB1dGVyLiBXZSd2ZSBiZWVuIGhlYXJpbmcgYSBsb3Qgb2YgY29tcGxh\naW50cyBhYm91dCB0aGlzIGxhdGVseSBhbmQgaXQgc2VlbXMgdG8gYmUgZ2V0dGluZyB3b3JzZS4gV2UgdGhp\nbmsgaXQncyBpbXBvcnRhbnQgdGhhdCB5b3UgcmV0YWluIGNvbnRyb2wgb2YgeW91ciBjb21wdXRlciBhbmQg\ndGhhdCB0aGVyZSBiZSBzb21lIGNsZWFyIHN0YW5kYXJkcyBpbiBvdXIgaW5kdXN0cnkuIExldCB1cyBrbm93\nIGlmIHlvdSB0aGluayB0aGVzZSBndWlkZWxpbmVzIGFyZSB1c2VmdWwgb3IgaWYgeW91IGhhdmUgc3VnZ2Vz\ndGlvbnMgdG8gaW1wcm92ZSB0aGVtLgo8YnIgLz4KPGJyIC8+Sm9uYXRoYW4gUm9zZW5iZXJnCjxiciAvPgo8\nL2Rpdj4K\n"));
    }

    static {
        int i;
        ALPHASET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes();
        CODES = new int[256];
        for (i = 0; i < CODES.length; ++i) {
            Base64.CODES[i] = 64;
        }
        for (i = 0; i < ALPHASET.length; ++i) {
            Base64.CODES[Base64.ALPHASET[i]] = i;
        }
    }
}

