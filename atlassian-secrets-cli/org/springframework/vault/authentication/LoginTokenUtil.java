/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.time.Duration;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.authentication.LoginToken;

final class LoginTokenUtil {
    private LoginTokenUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static LoginToken from(Map<String, Object> auth) {
        Assert.notNull(auth, "Authentication must not be null");
        String token = (String)auth.get("client_token");
        return LoginTokenUtil.from(token.toCharArray(), auth);
    }

    static LoginToken from(char[] token, Map<String, ?> auth) {
        Assert.notNull(auth, "Authentication must not be null");
        Boolean renewable = (Boolean)auth.get("renewable");
        Number leaseDuration = (Number)auth.get("lease_duration");
        String accessor = (String)auth.get("accessor");
        String type = (String)auth.get("type");
        if (leaseDuration == null) {
            leaseDuration = (Number)auth.get("ttl");
        }
        if (type == null) {
            type = (String)auth.get("token_type");
        }
        LoginToken.LoginTokenBuilder builder = LoginToken.builder();
        builder.token(token);
        if (StringUtils.hasText(accessor)) {
            builder.accessor(accessor);
        }
        if (leaseDuration != null) {
            builder.leaseDuration(Duration.ofSeconds(leaseDuration.longValue()));
        }
        if (renewable != null) {
            builder.renewable(renewable);
        }
        if (StringUtils.hasText(type)) {
            builder.type(type);
        }
        return builder.build();
    }
}

