/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.IPAddressFormatException;
import com.atlassian.security.auth.trustedapps.IPMatcher;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class DefaultIPMatcher
implements IPMatcher {
    private static final String WILDCARD = "*";
    private final List<AddressMask> addressMasks = new LinkedList<AddressMask>();

    public DefaultIPMatcher(Set<String> patterns) throws IPAddressFormatException {
        for (String patternStr : patterns) {
            this.addressMasks.add(AddressMask.create(DefaultIPMatcher.parsePatternString(patternStr)));
        }
    }

    public static int[] parsePatternString(String patternStr) {
        int[] pattern = new int[4];
        StringTokenizer st = new StringTokenizer(patternStr, ".");
        if (st.countTokens() != 4) {
            throw new IPAddressFormatException(patternStr);
        }
        for (int i = 0; i < 4; ++i) {
            String token = st.nextToken().trim();
            if (WILDCARD.equals(token)) {
                pattern[i] = -1;
                continue;
            }
            try {
                int value = Integer.valueOf(token);
                if (value < 0 || value > 255) {
                    throw new IPAddressFormatException(patternStr);
                }
                pattern[i] = value;
                continue;
            }
            catch (NumberFormatException e) {
                throw new IPAddressFormatException(patternStr);
            }
        }
        return pattern;
    }

    @Override
    public boolean match(String ipAddress) {
        if (this.addressMasks.isEmpty()) {
            return true;
        }
        int address = this.toAddress(ipAddress);
        for (AddressMask element : this.addressMasks) {
            AddressMask addressMask = element;
            if (!addressMask.matches(address)) continue;
            return true;
        }
        return false;
    }

    private int toAddress(String ipAddress) {
        int[] parsedIPAddr;
        int address = 0;
        for (int element : parsedIPAddr = DefaultIPMatcher.parsePatternString(ipAddress)) {
            address <<= 8;
            address |= element;
        }
        return address;
    }

    private static class AddressMask {
        private final int address;
        private final int mask;

        public AddressMask(int address, int mask) {
            this.address = address;
            this.mask = mask;
        }

        public boolean matches(int otherAddress) {
            return this.address == (otherAddress & this.mask);
        }

        static AddressMask create(int[] pattern) {
            int address = 0;
            int mask = 0;
            for (int element : pattern) {
                address <<= 8;
                mask <<= 8;
                if (element == -1) continue;
                address |= element;
                mask |= 0xFF;
            }
            return new AddressMask(address, mask);
        }
    }
}

