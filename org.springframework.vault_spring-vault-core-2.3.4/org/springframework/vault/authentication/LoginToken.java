/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.authentication;

import java.time.Duration;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.support.VaultToken;

public class LoginToken
extends VaultToken {
    private final boolean renewable;
    private final Duration leaseDuration;
    @Nullable
    private final String accessor;
    @Nullable
    private final String type;

    private LoginToken(char[] token, Duration duration, boolean renewable, @Nullable String accessor, @Nullable String type) {
        super(token);
        this.leaseDuration = duration;
        this.renewable = renewable;
        this.accessor = accessor;
        this.type = type;
    }

    public static LoginTokenBuilder builder() {
        return new LoginTokenBuilder();
    }

    public static LoginToken of(String token) {
        Assert.hasText((String)token, (String)"Token must not be empty");
        return LoginToken.of(token.toCharArray(), Duration.ZERO);
    }

    public static LoginToken of(char[] token) {
        return LoginToken.of(token, Duration.ZERO);
    }

    @Deprecated
    public static LoginToken of(String token, long leaseDurationSeconds) {
        Assert.hasText((String)token, (String)"Token must not be empty");
        Assert.isTrue((leaseDurationSeconds >= 0L ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return LoginToken.of(token.toCharArray(), Duration.ofSeconds(leaseDurationSeconds));
    }

    @Deprecated
    public static LoginToken of(char[] token, long leaseDurationSeconds) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        Assert.isTrue((token.length > 0 ? 1 : 0) != 0, (String)"Token must not be empty");
        Assert.isTrue((leaseDurationSeconds >= 0L ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new LoginToken(token, Duration.ofSeconds(leaseDurationSeconds), false, null, null);
    }

    public static LoginToken of(char[] token, Duration leaseDuration) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        Assert.isTrue((token.length > 0 ? 1 : 0) != 0, (String)"Token must not be empty");
        Assert.notNull((Object)leaseDuration, (String)"Lease duration must not be null");
        Assert.isTrue((!leaseDuration.isNegative() ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new LoginToken(token, leaseDuration, false, null, null);
    }

    @Deprecated
    public static LoginToken renewable(String token, long leaseDurationSeconds) {
        Assert.hasText((String)token, (String)"Token must not be empty");
        Assert.isTrue((leaseDurationSeconds >= 0L ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return LoginToken.renewable(token.toCharArray(), Duration.ofSeconds(leaseDurationSeconds));
    }

    @Deprecated
    public static LoginToken renewable(char[] token, long leaseDurationSeconds) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        Assert.isTrue((token.length > 0 ? 1 : 0) != 0, (String)"Token must not be empty");
        Assert.isTrue((leaseDurationSeconds >= 0L ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new LoginToken(token, Duration.ofSeconds(leaseDurationSeconds), true, null, null);
    }

    public static LoginToken renewable(VaultToken token, Duration leaseDuration) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        return LoginToken.renewable(token.toCharArray(), leaseDuration);
    }

    public static LoginToken renewable(char[] token, Duration leaseDuration) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        Assert.isTrue((token.length > 0 ? 1 : 0) != 0, (String)"Token must not be empty");
        Assert.notNull((Object)leaseDuration, (String)"Lease duration must not be null");
        Assert.isTrue((!leaseDuration.isNegative() ? 1 : 0) != 0, (String)"Lease duration must not be negative");
        return new LoginToken(token, leaseDuration, true, null, null);
    }

    static boolean hasAccessor(VaultToken token) {
        return token instanceof LoginToken && StringUtils.hasText((String)((LoginToken)token).getAccessor());
    }

    public Duration getLeaseDuration() {
        return this.leaseDuration;
    }

    public boolean isRenewable() {
        return this.renewable;
    }

    @Nullable
    public String getAccessor() {
        return this.accessor;
    }

    @Nullable
    public String getType() {
        return this.type;
    }

    public boolean isBatchToken() {
        return "batch".equals(this.type);
    }

    public boolean isServiceToken() {
        return this.type == null || "service".equals(this.type);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [renewable=").append(this.renewable);
        sb.append(", leaseDuration=").append(this.leaseDuration);
        sb.append(", type=").append(this.type);
        sb.append(']');
        return sb.toString();
    }

    public static class LoginTokenBuilder {
        @Nullable
        private char[] token;
        private boolean renewable;
        private Duration leaseDuration = Duration.ZERO;
        @Nullable
        private String accessor;
        @Nullable
        private String type;

        private LoginTokenBuilder() {
        }

        public LoginTokenBuilder token(String token) {
            Assert.hasText((String)token, (String)"Token must not be empty");
            return this.token(token.toCharArray());
        }

        public LoginTokenBuilder token(char[] token) {
            Assert.notNull((Object)token, (String)"Token must not be null");
            Assert.isTrue((token.length > 0 ? 1 : 0) != 0, (String)"Token must not be empty");
            this.token = token;
            return this;
        }

        public LoginTokenBuilder renewable(boolean renewable) {
            this.renewable = renewable;
            return this;
        }

        public LoginTokenBuilder leaseDuration(Duration leaseDuration) {
            Assert.notNull((Object)leaseDuration, (String)"Lease duration must not be empty");
            this.leaseDuration = leaseDuration;
            return this;
        }

        public LoginTokenBuilder accessor(String accessor) {
            Assert.hasText((String)accessor, (String)"Token accessor must not be empty");
            this.accessor = accessor;
            return this;
        }

        public LoginTokenBuilder type(String type) {
            Assert.hasText((String)type, (String)"Token type must not be empty");
            this.type = type;
            return this;
        }

        public LoginToken build() {
            Assert.notNull((Object)this.token, (String)"Token must not be null");
            return new LoginToken(Arrays.copyOf(this.token, this.token.length), this.leaseDuration, this.renewable, this.accessor, this.type);
        }
    }
}

