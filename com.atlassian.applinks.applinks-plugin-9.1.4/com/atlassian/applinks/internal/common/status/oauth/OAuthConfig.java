/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.status.oauth;

import com.google.common.collect.Ordering;
import java.math.BigInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OAuthConfig {
    public static Ordering<OAuthConfig> ORDER_BY_LEVEL = new Ordering<OAuthConfig>(){

        public int compare(@Nullable OAuthConfig left, @Nullable OAuthConfig right) {
            if (left == right) {
                return 0;
            }
            if (left == null) {
                return -1;
            }
            if (right == null) {
                return 1;
            }
            if (left.equals(right)) {
                return 0;
            }
            return left.levels.compareTo(right.levels);
        }
    };
    private final BigInteger levels;

    private OAuthConfig(boolean threeLoEnabled, boolean twoLoEnabled, boolean twoLoImpersonationEnabled) {
        BigInteger value = BigInteger.ZERO;
        value = OAuthConfig.setBit(value, 0, threeLoEnabled);
        value = OAuthConfig.setBit(value, 1, twoLoEnabled);
        this.levels = value = OAuthConfig.setBit(value, 2, twoLoImpersonationEnabled);
    }

    @Nonnull
    public static OAuthConfig createDisabledConfig() {
        return new OAuthConfig(false, false, false);
    }

    @Nonnull
    public static OAuthConfig createThreeLoOnlyConfig() {
        return new OAuthConfig(true, false, false);
    }

    @Nonnull
    public static OAuthConfig createDefaultOAuthConfig() {
        return new OAuthConfig(true, true, false);
    }

    @Nonnull
    public static OAuthConfig createOAuthWithImpersonationConfig() {
        return new OAuthConfig(true, true, true);
    }

    @Nonnull
    public static OAuthConfig fromConfig(boolean is3LoConfigured, boolean is2LoConfigured, boolean is2LoIConfigured) {
        if (!is3LoConfigured) {
            return OAuthConfig.createDisabledConfig();
        }
        if (is2LoIConfigured) {
            return OAuthConfig.createOAuthWithImpersonationConfig();
        }
        if (is2LoConfigured) {
            return OAuthConfig.createDefaultOAuthConfig();
        }
        return OAuthConfig.createThreeLoOnlyConfig();
    }

    public boolean isEnabled() {
        return this.levels.testBit(0);
    }

    public boolean isTwoLoEnabled() {
        return this.levels.testBit(1);
    }

    public boolean isTwoLoImpersonationEnabled() {
        return this.levels.testBit(2);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OAuthConfig that = (OAuthConfig)o;
        return this.levels.equals(that.levels);
    }

    public int hashCode() {
        return this.levels.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("OAuthConfig{").append("enabled=").append(this.isEnabled()).append(", ").append("twoLoEnabled=").append(this.isTwoLoEnabled()).append(", ").append("twoLoImpersonationEnabled=").append(this.isTwoLoImpersonationEnabled()).append("}").toString();
    }

    private static BigInteger setBit(BigInteger integer, int position, boolean value) {
        return value ? integer.setBit(position) : integer;
    }
}

