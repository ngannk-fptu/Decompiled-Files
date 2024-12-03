/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.generators.BCrypt;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class OpenBSDBCrypt {
    private static final byte[] encodingTable;
    private static final byte[] decodingTable;
    private static final String defaultVersion = "2y";
    private static final Set<String> allowedVersions;

    private OpenBSDBCrypt() {
    }

    public static String generate(char[] password, byte[] salt, int cost) {
        return OpenBSDBCrypt.generate(defaultVersion, password, salt, cost);
    }

    public static String generate(byte[] password, byte[] salt, int cost) {
        return OpenBSDBCrypt.generate(defaultVersion, password, salt, cost);
    }

    public static String generate(String version, char[] password, byte[] salt, int cost) {
        if (password == null) {
            throw new IllegalArgumentException("Password required.");
        }
        return OpenBSDBCrypt.doGenerate(version, Strings.toUTF8ByteArray(password), salt, cost);
    }

    public static String generate(String version, byte[] password, byte[] salt, int cost) {
        if (password == null) {
            throw new IllegalArgumentException("Password required.");
        }
        return OpenBSDBCrypt.doGenerate(version, Arrays.clone(password), salt, cost);
    }

    private static String doGenerate(String version, byte[] psw, byte[] salt, int cost) {
        if (!allowedVersions.contains(version)) {
            throw new IllegalArgumentException("Version " + version + " is not accepted by this implementation.");
        }
        if (salt == null) {
            throw new IllegalArgumentException("Salt required.");
        }
        if (salt.length != 16) {
            throw new DataLengthException("16 byte salt required: " + salt.length);
        }
        if (cost < 4 || cost > 31) {
            throw new IllegalArgumentException("Invalid cost factor.");
        }
        byte[] tmp = new byte[psw.length >= 72 ? 72 : psw.length + 1];
        if (tmp.length > psw.length) {
            System.arraycopy(psw, 0, tmp, 0, psw.length);
        } else {
            System.arraycopy(psw, 0, tmp, 0, tmp.length);
        }
        Arrays.fill(psw, (byte)0);
        String rv = OpenBSDBCrypt.createBcryptString(version, tmp, salt, cost);
        Arrays.fill(tmp, (byte)0);
        return rv;
    }

    public static boolean checkPassword(String bcryptString, char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Missing password.");
        }
        return OpenBSDBCrypt.doCheckPassword(bcryptString, Strings.toUTF8ByteArray(password));
    }

    public static boolean checkPassword(String bcryptString, byte[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Missing password.");
        }
        return OpenBSDBCrypt.doCheckPassword(bcryptString, Arrays.clone(password));
    }

    private static boolean doCheckPassword(String bcryptString, byte[] password) {
        int base;
        String version;
        if (bcryptString == null) {
            throw new IllegalArgumentException("Missing bcryptString.");
        }
        if (bcryptString.charAt(1) != '2') {
            throw new IllegalArgumentException("not a Bcrypt string");
        }
        int sLength = bcryptString.length();
        if (sLength != 60 && (sLength != 59 || bcryptString.charAt(2) != '$')) {
            throw new DataLengthException("Bcrypt String length: " + sLength + ", 60 required.");
        }
        if (bcryptString.charAt(2) == '$' ? bcryptString.charAt(0) != '$' || bcryptString.charAt(5) != '$' : bcryptString.charAt(0) != '$' || bcryptString.charAt(3) != '$' || bcryptString.charAt(6) != '$') {
            throw new IllegalArgumentException("Invalid Bcrypt String format.");
        }
        if (bcryptString.charAt(2) == '$') {
            version = bcryptString.substring(1, 2);
            base = 3;
        } else {
            version = bcryptString.substring(1, 3);
            base = 4;
        }
        if (!allowedVersions.contains(version)) {
            throw new IllegalArgumentException("Bcrypt version '" + version + "' is not supported by this implementation");
        }
        int cost = 0;
        String costStr = bcryptString.substring(base, base + 2);
        try {
            cost = Integer.parseInt(costStr);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid cost factor: " + costStr);
        }
        if (cost < 4 || cost > 31) {
            throw new IllegalArgumentException("Invalid cost factor: " + cost + ", 4 < cost < 31 expected.");
        }
        byte[] salt = OpenBSDBCrypt.decodeSaltString(bcryptString.substring(bcryptString.lastIndexOf(36) + 1, sLength - 31));
        String newBcryptString = OpenBSDBCrypt.doGenerate(version, password, salt, cost);
        return Strings.constantTimeAreEqual(bcryptString, newBcryptString);
    }

    private static String createBcryptString(String version, byte[] password, byte[] salt, int cost) {
        if (!allowedVersions.contains(version)) {
            throw new IllegalArgumentException("Version " + version + " is not accepted by this implementation.");
        }
        StringBuilder sb = new StringBuilder(60);
        sb.append('$');
        sb.append(version);
        sb.append('$');
        sb.append(cost < 10 ? "0" + cost : Integer.toString(cost));
        sb.append('$');
        OpenBSDBCrypt.encodeData(sb, salt);
        byte[] key = BCrypt.generate(password, salt, cost);
        OpenBSDBCrypt.encodeData(sb, key);
        return sb.toString();
    }

    private static void encodeData(StringBuilder sb, byte[] data) {
        if (data.length != 24 && data.length != 16) {
            throw new DataLengthException("Invalid length: " + data.length + ", 24 for key or 16 for salt expected");
        }
        boolean salt = false;
        if (data.length == 16) {
            salt = true;
            byte[] tmp = new byte[18];
            System.arraycopy(data, 0, tmp, 0, data.length);
            data = tmp;
        } else {
            data[data.length - 1] = 0;
        }
        int len = data.length;
        for (int i = 0; i < len; i += 3) {
            int a1 = data[i] & 0xFF;
            int a2 = data[i + 1] & 0xFF;
            int a3 = data[i + 2] & 0xFF;
            sb.append((char)encodingTable[a1 >>> 2 & 0x3F]);
            sb.append((char)encodingTable[(a1 << 4 | a2 >>> 4) & 0x3F]);
            sb.append((char)encodingTable[(a2 << 2 | a3 >>> 6) & 0x3F]);
            sb.append((char)encodingTable[a3 & 0x3F]);
        }
        if (salt) {
            sb.setLength(sb.length() - 2);
        } else {
            sb.setLength(sb.length() - 1);
        }
    }

    private static byte[] decodeSaltString(String saltString) {
        char[] saltChars = saltString.toCharArray();
        ByteArrayOutputStream out = new ByteArrayOutputStream(16);
        if (saltChars.length != 22) {
            throw new DataLengthException("Invalid base64 salt length: " + saltChars.length + " , 22 required.");
        }
        for (int i = 0; i < saltChars.length; ++i) {
            char value = saltChars[i];
            if (value <= 'z' && value >= '.' && (value <= '9' || value >= 'A')) continue;
            throw new IllegalArgumentException("Salt string contains invalid character: " + value);
        }
        char[] tmp = new char[24];
        System.arraycopy(saltChars, 0, tmp, 0, saltChars.length);
        saltChars = tmp;
        int len = saltChars.length;
        for (int i = 0; i < len; i += 4) {
            byte b1 = decodingTable[saltChars[i]];
            byte b2 = decodingTable[saltChars[i + 1]];
            byte b3 = decodingTable[saltChars[i + 2]];
            byte b4 = decodingTable[saltChars[i + 3]];
            out.write(b1 << 2 | b2 >> 4);
            out.write(b2 << 4 | b3 >> 2);
            out.write(b3 << 6 | b4);
        }
        byte[] saltBytes = out.toByteArray();
        byte[] tmpSalt = new byte[16];
        System.arraycopy(saltBytes, 0, tmpSalt, 0, tmpSalt.length);
        saltBytes = tmpSalt;
        return saltBytes;
    }

    static {
        int i;
        encodingTable = new byte[]{46, 47, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
        decodingTable = new byte[128];
        allowedVersions = new HashSet<String>();
        allowedVersions.add("2");
        allowedVersions.add("2x");
        allowedVersions.add("2a");
        allowedVersions.add(defaultVersion);
        allowedVersions.add("2b");
        for (i = 0; i < decodingTable.length; ++i) {
            OpenBSDBCrypt.decodingTable[i] = -1;
        }
        for (i = 0; i < encodingTable.length; ++i) {
            OpenBSDBCrypt.decodingTable[OpenBSDBCrypt.encodingTable[i]] = (byte)i;
        }
    }
}

