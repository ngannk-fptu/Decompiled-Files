/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.IPAddressFormatException;

@Deprecated
public interface IPMatcher {
    public boolean match(String var1) throws IPAddressFormatException;
}

