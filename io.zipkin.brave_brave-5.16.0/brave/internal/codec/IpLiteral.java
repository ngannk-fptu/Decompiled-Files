/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

import brave.internal.Nullable;

public final class IpLiteral {
    @Nullable
    public static String ipOrNull(@Nullable String ip) {
        if (ip == null || ip.isEmpty()) {
            return null;
        }
        if ("::1".equals(ip) || "127.0.0.1".equals(ip)) {
            return ip;
        }
        IpFamily format = IpLiteral.detectFamily(ip);
        if (format == IpFamily.IPv4Embedded) {
            ip = ip.substring(ip.lastIndexOf(58) + 1);
        } else if (format == IpFamily.Unknown) {
            ip = null;
        }
        return ip;
    }

    public static IpFamily detectFamily(String ipString) {
        char c;
        boolean hasColon = false;
        boolean hasDot = false;
        int length = ipString.length();
        for (int i = 0; i < length; ++i) {
            c = ipString.charAt(i);
            if (c == '.') {
                hasDot = true;
                continue;
            }
            if (c == ':') {
                if (hasDot) {
                    return IpFamily.Unknown;
                }
                hasColon = true;
                continue;
            }
            if (!IpLiteral.notHex(c)) continue;
            return IpFamily.Unknown;
        }
        if (hasColon) {
            if (hasDot) {
                int lastColonIndex = ipString.lastIndexOf(58);
                if (!IpLiteral.isValidIpV4Address(ipString, lastColonIndex + 1, ipString.length())) {
                    return IpFamily.Unknown;
                }
                if (lastColonIndex == 1 && ipString.charAt(0) == ':') {
                    return IpFamily.IPv4Embedded;
                }
                if (lastColonIndex != 6 || ipString.charAt(0) != ':' || ipString.charAt(1) != ':') {
                    return IpFamily.Unknown;
                }
                for (int i = 2; i < 6; ++i) {
                    c = ipString.charAt(i);
                    if (c == 'f' || c == 'F' || c == '0') continue;
                    return IpFamily.Unknown;
                }
                return IpFamily.IPv4Embedded;
            }
            return IpFamily.IPv6;
        }
        if (hasDot && IpLiteral.isValidIpV4Address(ipString, 0, ipString.length())) {
            return IpFamily.IPv4;
        }
        return IpFamily.Unknown;
    }

    private static boolean notHex(char c) {
        return !(c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F');
    }

    private static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
        int i;
        int len = toExcluded - from;
        return len <= 15 && len >= 7 && (i = ip.indexOf(46, from + 1)) > 0 && IpLiteral.isValidIpV4Word(ip, from, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && IpLiteral.isValidIpV4Word(ip, from - 1, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && IpLiteral.isValidIpV4Word(ip, from - 1, i) && IpLiteral.isValidIpV4Word(ip, i + 1, toExcluded);
    }

    private static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
        int len = toExclusive - from;
        if (len < 1 || len > 3) {
            return false;
        }
        char c0 = word.charAt(from);
        if (len == 3) {
            char c2;
            char c1 = word.charAt(from + 1);
            return c1 >= '0' && (c2 = word.charAt(from + 2)) >= '0' && (c0 <= '1' && c1 <= '9' && c2 <= '9' || c0 == '2' && c1 <= '5' && (c2 <= '5' || c1 < '5' && c2 <= '9'));
        }
        return c0 <= '9' && (len == 1 || IpLiteral.isValidNumericChar(word.charAt(from + 1)));
    }

    private static boolean isValidNumericChar(char c) {
        return c >= '0' && c <= '9';
    }

    public static enum IpFamily {
        Unknown,
        IPv4,
        IPv4Embedded,
        IPv6;

    }
}

