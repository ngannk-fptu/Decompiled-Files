/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod
 *  com.atlassian.oauth2.provider.api.pkce.PkceService
 *  javax.annotation.Nonnull
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.pkce;

import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.provider.api.pkce.PkceService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPkceService
implements PkceService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPkceService.class);

    public boolean isValidCode(@Nonnull String pkceCode) {
        return pkceCode.matches("[0-9a-zA-Z\\~\\-\\.\\_]{43,128}");
    }

    public boolean isExpectedCodeChallengeGenerated(@Nonnull String expectedCodeChallenge, @Nonnull CodeChallengeMethod codeChallengeMethod, @Nonnull String codeVerifier) {
        switch (codeChallengeMethod) {
            case PLAIN: {
                logger.debug("Verifying code challenge using PLAIN.");
                return expectedCodeChallenge.equals(codeVerifier);
            }
            case S256: {
                logger.debug("Verifying code challenge using S256.");
                return expectedCodeChallenge.equals(this.sha256CodeChallengeFromCodeVerifier(codeVerifier));
            }
        }
        return false;
    }

    private String sha256CodeChallengeFromCodeVerifier(String codeVerifier) {
        return this.base64UrlEncode(DigestUtils.sha256((byte[])this.ascii(codeVerifier)));
    }

    private byte[] ascii(String codeVerifier) {
        return codeVerifier.getBytes(StandardCharsets.US_ASCII);
    }

    private String base64UrlEncode(byte[] codeVerifier) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }
}

