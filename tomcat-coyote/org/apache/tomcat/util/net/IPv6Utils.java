/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

public class IPv6Utils {
    private static final int MAX_NUMBER_OF_GROUPS = 8;
    private static final int MAX_GROUP_LENGTH = 4;

    public static String canonize(String ipv6Address) throws IllegalArgumentException {
        if (ipv6Address == null) {
            return null;
        }
        if (!IPv6Utils.mayBeIPv6Address(ipv6Address)) {
            return ipv6Address;
        }
        int ipv6AddressLength = ipv6Address.length();
        if (ipv6Address.contains(".")) {
            int lastColonPos = ipv6Address.lastIndexOf(58);
            int lastColonsPos = ipv6Address.lastIndexOf("::");
            ipv6AddressLength = lastColonsPos >= 0 && lastColonPos == lastColonsPos + 1 ? lastColonPos + 1 : lastColonPos;
        } else if (ipv6Address.contains("%")) {
            ipv6AddressLength = ipv6Address.lastIndexOf(37);
        }
        StringBuilder result = new StringBuilder();
        char[][] groups = new char[8][4];
        int groupCounter = 0;
        int charInGroupCounter = 0;
        int zeroGroupIndex = -1;
        int zeroGroupLength = 0;
        int maxZeroGroupIndex = -1;
        int maxZeroGroupLength = 0;
        boolean isZero = true;
        boolean groupStart = true;
        StringBuilder expanded = new StringBuilder(ipv6Address);
        int colonsPos = ipv6Address.indexOf("::");
        int length = ipv6AddressLength;
        int change = 0;
        if (colonsPos >= 0 && colonsPos < ipv6AddressLength - 1) {
            int i;
            int colonCounter = 0;
            for (i = 0; i < ipv6AddressLength; ++i) {
                if (ipv6Address.charAt(i) != ':') continue;
                ++colonCounter;
            }
            if (colonsPos == 0) {
                expanded.insert(0, "0");
                ++change;
            }
            for (i = 0; i < 8 - colonCounter; ++i) {
                expanded.insert(colonsPos + 1, "0:");
                change += 2;
            }
            if (colonsPos == ipv6AddressLength - 2) {
                expanded.setCharAt(colonsPos + change + 1, '0');
            } else {
                expanded.deleteCharAt(colonsPos + change + 1);
                --change;
            }
            length += change;
        }
        for (int charCounter = 0; charCounter < length; ++charCounter) {
            char c = expanded.charAt(charCounter);
            if (c >= 'A' && c <= 'F') {
                c = (char)(c + 32);
            }
            if (c != ':') {
                groups[groupCounter][charInGroupCounter] = c;
                if (!groupStart || c != '0') {
                    ++charInGroupCounter;
                    groupStart = false;
                }
                if (c != '0') {
                    isZero = false;
                }
            }
            if (c != ':' && charCounter != length - 1) continue;
            if (isZero) {
                ++zeroGroupLength;
                if (zeroGroupIndex == -1) {
                    zeroGroupIndex = groupCounter;
                }
            }
            if (!isZero || charCounter == length - 1) {
                if (zeroGroupLength > maxZeroGroupLength) {
                    maxZeroGroupLength = zeroGroupLength;
                    maxZeroGroupIndex = zeroGroupIndex;
                }
                zeroGroupLength = 0;
                zeroGroupIndex = -1;
            }
            ++groupCounter;
            charInGroupCounter = 0;
            isZero = true;
            groupStart = true;
        }
        int numberOfGroups = groupCounter;
        for (groupCounter = 0; groupCounter < numberOfGroups; ++groupCounter) {
            if (maxZeroGroupLength <= 1 || groupCounter < maxZeroGroupIndex || groupCounter >= maxZeroGroupIndex + maxZeroGroupLength) {
                for (int j = 0; j < 4; ++j) {
                    if (groups[groupCounter][j] == '\u0000') continue;
                    result.append(groups[groupCounter][j]);
                }
                if (groupCounter >= numberOfGroups - 1 || groupCounter == maxZeroGroupIndex - 1 && maxZeroGroupLength > 1) continue;
                result.append(':');
                continue;
            }
            if (groupCounter != maxZeroGroupIndex) continue;
            result.append("::");
        }
        int resultLength = result.length();
        if (result.charAt(resultLength - 1) == ':' && ipv6AddressLength < ipv6Address.length() && ipv6Address.charAt(ipv6AddressLength) == ':') {
            result.delete(resultLength - 1, resultLength);
        }
        for (int i = ipv6AddressLength; i < ipv6Address.length(); ++i) {
            result.append(ipv6Address.charAt(i));
        }
        return result.toString();
    }

    static boolean mayBeIPv6Address(String input) {
        char c;
        if (input == null) {
            return false;
        }
        int colonsCounter = 0;
        int length = input.length();
        for (int i = 0; i < length && (c = input.charAt(i)) != '.' && c != '%'; ++i) {
            if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F' || c == ':')) {
                return false;
            }
            if (c != ':') continue;
            ++colonsCounter;
        }
        return colonsCounter >= 2;
    }
}

