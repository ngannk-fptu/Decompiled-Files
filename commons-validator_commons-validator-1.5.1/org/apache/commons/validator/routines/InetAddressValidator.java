/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.validator.routines.RegexValidator;

public class InetAddressValidator
implements Serializable {
    private static final int IPV4_MAX_OCTET_VALUE = 255;
    private static final int MAX_UNSIGNED_SHORT = 65535;
    private static final int BASE_16 = 16;
    private static final long serialVersionUID = -919201640201914789L;
    private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";
    private static final int IPV6_MAX_HEX_GROUPS = 8;
    private static final int IPV6_MAX_HEX_DIGITS_PER_GROUP = 4;
    private static final InetAddressValidator VALIDATOR = new InetAddressValidator();
    private final RegexValidator ipv4Validator = new RegexValidator("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    public static InetAddressValidator getInstance() {
        return VALIDATOR;
    }

    public boolean isValid(String inetAddress) {
        return this.isValidInet4Address(inetAddress) || this.isValidInet6Address(inetAddress);
    }

    public boolean isValidInet4Address(String inet4Address) {
        String[] groups = this.ipv4Validator.match(inet4Address);
        if (groups == null) {
            return false;
        }
        for (String ipSegment : groups) {
            if (ipSegment == null || ipSegment.length() == 0) {
                return false;
            }
            int iIpSegment = 0;
            try {
                iIpSegment = Integer.parseInt(ipSegment);
            }
            catch (NumberFormatException e) {
                return false;
            }
            if (iIpSegment > 255) {
                return false;
            }
            if (ipSegment.length() <= 1 || !ipSegment.startsWith("0")) continue;
            return false;
        }
        return true;
    }

    public boolean isValidInet6Address(String inet6Address) {
        boolean containsCompressedZeroes = inet6Address.contains("::");
        if (containsCompressedZeroes && inet6Address.indexOf("::") != inet6Address.lastIndexOf("::")) {
            return false;
        }
        if (inet6Address.startsWith(":") && !inet6Address.startsWith("::") || inet6Address.endsWith(":") && !inet6Address.endsWith("::")) {
            return false;
        }
        String[] octets = inet6Address.split(":");
        if (containsCompressedZeroes) {
            ArrayList<String> octetList = new ArrayList<String>(Arrays.asList(octets));
            if (inet6Address.endsWith("::")) {
                octetList.add("");
            } else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                octetList.remove(0);
            }
            octets = octetList.toArray(new String[octetList.size()]);
        }
        if (octets.length > 8) {
            return false;
        }
        int validOctets = 0;
        int emptyOctets = 0;
        for (int index = 0; index < octets.length; ++index) {
            String octet = octets[index];
            if (octet.length() == 0) {
                if (++emptyOctets > 1) {
                    return false;
                }
            } else {
                emptyOctets = 0;
                if (octet.contains(".")) {
                    if (!inet6Address.endsWith(octet)) {
                        return false;
                    }
                    if (index > octets.length - 1 || index > 6) {
                        return false;
                    }
                    if (!this.isValidInet4Address(octet)) {
                        return false;
                    }
                    validOctets += 2;
                    continue;
                }
                if (octet.length() > 4) {
                    return false;
                }
                int octetInt = 0;
                try {
                    octetInt = Integer.valueOf(octet, 16);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                if (octetInt < 0 || octetInt > 65535) {
                    return false;
                }
            }
            ++validOctets;
        }
        return validOctets >= 8 || containsCompressedZeroes;
    }
}

