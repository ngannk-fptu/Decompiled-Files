/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Exceptions;

public class ByteScanner {
    protected static int encodeNibbleToHexAsciiCharByte(int nibble) {
        switch (nibble) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: {
                return nibble + 48;
            }
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                return nibble + 87;
            }
        }
        Exceptions.die("illegal nibble: " + nibble);
        return -1;
    }

    public static void encodeByteIntoTwoAsciiCharBytes(int decoded, byte[] encoded) {
        encoded[0] = (byte)ByteScanner.encodeNibbleToHexAsciiCharByte(decoded >> 4 & 0xF);
        encoded[1] = (byte)ByteScanner.encodeNibbleToHexAsciiCharByte(decoded & 0xF);
    }
}

