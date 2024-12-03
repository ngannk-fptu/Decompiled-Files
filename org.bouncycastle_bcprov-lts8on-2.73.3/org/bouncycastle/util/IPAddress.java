/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class IPAddress {
    public static boolean isValid(String address) {
        return IPAddress.isValidIPv4(address) || IPAddress.isValidIPv6(address);
    }

    public static boolean isValidWithNetMask(String address) {
        return IPAddress.isValidIPv4WithNetmask(address) || IPAddress.isValidIPv6WithNetmask(address);
    }

    public static boolean isValidIPv4(String address) {
        int length = address.length();
        if (length < 7 || length > 15) {
            return false;
        }
        int pos = 0;
        for (int octetIndex = 0; octetIndex < 3; ++octetIndex) {
            int end = address.indexOf(46, pos);
            if (!IPAddress.isParseableIPv4Octet(address, pos, end)) {
                return false;
            }
            pos = end + 1;
        }
        return IPAddress.isParseableIPv4Octet(address, pos, length);
    }

    public static boolean isValidIPv4WithNetmask(String address) {
        int index = address.indexOf("/");
        if (index < 1) {
            return false;
        }
        String before = address.substring(0, index);
        String after = address.substring(index + 1);
        return IPAddress.isValidIPv4(before) && (IPAddress.isValidIPv4(after) || IPAddress.isParseableIPv4Mask(after));
    }

    public static boolean isValidIPv6(String address) {
        int end;
        if (address.length() == 0) {
            return false;
        }
        char firstChar = address.charAt(0);
        if (firstChar != ':' && Character.digit(firstChar, 16) < 0) {
            return false;
        }
        int segmentCount = 0;
        String temp = address + ":";
        boolean doubleColonFound = false;
        int pos = 0;
        while (pos < temp.length() && (end = temp.indexOf(58, pos)) >= pos) {
            if (segmentCount == 8) {
                return false;
            }
            if (pos != end) {
                String value = temp.substring(pos, end);
                if (end == temp.length() - 1 && value.indexOf(46) > 0) {
                    if (++segmentCount == 8) {
                        return false;
                    }
                    if (!IPAddress.isValidIPv4(value)) {
                        return false;
                    }
                } else if (!IPAddress.isParseableIPv6Segment(temp, pos, end)) {
                    return false;
                }
            } else {
                if (end != 1 && end != temp.length() - 1 && doubleColonFound) {
                    return false;
                }
                doubleColonFound = true;
            }
            pos = end + 1;
            ++segmentCount;
        }
        return segmentCount == 8 || doubleColonFound;
    }

    public static boolean isValidIPv6WithNetmask(String address) {
        int index = address.indexOf("/");
        if (index < 1) {
            return false;
        }
        String before = address.substring(0, index);
        String after = address.substring(index + 1);
        return IPAddress.isValidIPv6(before) && (IPAddress.isValidIPv6(after) || IPAddress.isParseableIPv6Mask(after));
    }

    private static boolean isParseableIPv4Mask(String s) {
        return IPAddress.isParseable(s, 0, s.length(), 10, 2, false, 0, 32);
    }

    private static boolean isParseableIPv4Octet(String s, int pos, int end) {
        return IPAddress.isParseable(s, pos, end, 10, 3, true, 0, 255);
    }

    private static boolean isParseableIPv6Mask(String s) {
        return IPAddress.isParseable(s, 0, s.length(), 10, 3, false, 1, 128);
    }

    private static boolean isParseableIPv6Segment(String s, int pos, int end) {
        return IPAddress.isParseable(s, pos, end, 16, 4, true, 0, 65535);
    }

    private static boolean isParseable(String s, int pos, int end, int radix, int maxLength, boolean allowLeadingZero, int minValue, int maxValue) {
        int length = end - pos;
        if (length < 1 | length > maxLength) {
            return false;
        }
        boolean checkLeadingZero = length > 1 & !allowLeadingZero;
        if (checkLeadingZero && Character.digit(s.charAt(pos), radix) <= 0) {
            return false;
        }
        int value = 0;
        while (pos < end) {
            char c;
            int d;
            if ((d = Character.digit(c = s.charAt(pos++), radix)) < 0) {
                return false;
            }
            value *= radix;
            value += d;
        }
        return value >= minValue & value <= maxValue;
    }
}

