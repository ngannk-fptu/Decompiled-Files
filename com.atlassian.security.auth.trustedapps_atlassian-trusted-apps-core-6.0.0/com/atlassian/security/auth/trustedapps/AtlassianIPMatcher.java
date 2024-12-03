/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.ip.IPMatcher
 *  com.atlassian.ip.IPMatcher$Builder
 *  com.atlassian.ip.Subnet
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.ip.IPMatcher;
import com.atlassian.ip.Subnet;
import com.atlassian.security.auth.trustedapps.IPAddressFormatException;
import com.atlassian.security.auth.trustedapps.IPMatcher;
import java.util.Set;

public class AtlassianIPMatcher
implements IPMatcher {
    private final com.atlassian.ip.IPMatcher ipMatcher;

    public AtlassianIPMatcher(Set<String> patterns) throws IPAddressFormatException {
        if (!patterns.isEmpty()) {
            IPMatcher.Builder builder = com.atlassian.ip.IPMatcher.builder();
            for (String patternStr : patterns) {
                builder.addPattern(patternStr);
            }
            this.ipMatcher = builder.build();
        } else {
            this.ipMatcher = null;
        }
    }

    @Override
    public boolean match(String ipAddress) {
        return this.ipMatcher == null || this.ipMatcher.matches(ipAddress);
    }

    public static void parsePatternString(String pattern) throws IPAddressFormatException {
        if (!Subnet.isValidPattern((String)pattern)) {
            throw new IPAddressFormatException(pattern);
        }
    }
}

