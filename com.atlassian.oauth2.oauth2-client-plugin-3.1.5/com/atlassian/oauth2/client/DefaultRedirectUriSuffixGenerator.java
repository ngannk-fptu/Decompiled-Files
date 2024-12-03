/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.DigestUtils
 */
package com.atlassian.oauth2.client;

import com.atlassian.oauth2.client.RedirectUriSuffixGenerator;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.util.DigestUtils;

public class DefaultRedirectUriSuffixGenerator
implements RedirectUriSuffixGenerator {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Override
    public String generateRedirectUriSuffix(String authorizationEndpoint) {
        byte[] hash = DigestUtils.md5Digest((byte[])authorizationEndpoint.getBytes(StandardCharsets.UTF_8));
        return ENCODER.encodeToString(hash);
    }
}

