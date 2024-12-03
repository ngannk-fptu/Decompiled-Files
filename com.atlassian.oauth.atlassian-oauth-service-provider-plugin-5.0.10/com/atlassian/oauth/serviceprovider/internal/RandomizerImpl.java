/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.SecureRandomService
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.serviceprovider.internal.Randomizer;
import com.atlassian.security.random.SecureRandomService;

public class RandomizerImpl
implements Randomizer {
    private static final String ALPHANUMERIC_SOURCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final SecureRandomService secureRandomService;

    public RandomizerImpl(SecureRandomService secureRandomService) {
        this.secureRandomService = secureRandomService;
    }

    @Override
    public String randomAlphanumericString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            builder.append(ALPHANUMERIC_SOURCE.charAt(this.secureRandomService.nextInt(ALPHANUMERIC_SOURCE.length())));
        }
        return builder.toString();
    }
}

