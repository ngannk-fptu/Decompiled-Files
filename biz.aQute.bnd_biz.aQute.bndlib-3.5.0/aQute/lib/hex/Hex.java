/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.hex;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Hex {
    static Pattern HEX_P = Pattern.compile("(?:[0-9a-fA-F][0-9a-fA-Z])+");
    static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static final byte[] toByteArray(String string) {
        Objects.requireNonNull(string, "The hex string must not be null.");
        string = string.trim();
        if ((string.length() & 1) != 0) {
            throw new IllegalArgumentException("a hex string must have an even length");
        }
        byte[] out = new byte[string.length() / 2];
        for (int i = 0; i < out.length; ++i) {
            int high = Hex.nibble(string.charAt(i * 2)) << 4;
            int low = Hex.nibble(string.charAt(i * 2 + 1));
            out[i] = (byte)(high + low);
        }
        return out;
    }

    public static final int nibble(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 65 + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 97 + 10;
        }
        throw new IllegalArgumentException("Not a hex digit: " + c);
    }

    public static final String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        try {
            Hex.append(sb, data);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return sb.toString();
    }

    public static final void append(Appendable sb, byte[] data) throws IOException {
        for (int i = 0; i < data.length; ++i) {
            sb.append(Hex.nibble(data[i] >> 4));
            sb.append(Hex.nibble(data[i]));
        }
    }

    public static final char nibble(int i) {
        return HEX[i & 0xF];
    }

    public static boolean isHex(String pub) {
        return HEX_P.matcher(pub).matches();
    }

    public static boolean isHexCharacter(char c) {
        if (c < '0') {
            return false;
        }
        if (c <= '9') {
            return true;
        }
        if (c < 'A') {
            return false;
        }
        if (c <= 'F') {
            return true;
        }
        if (c < 'a') {
            return false;
        }
        return c <= 'f';
    }
}

