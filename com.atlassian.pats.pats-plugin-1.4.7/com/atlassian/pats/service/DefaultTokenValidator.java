/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.service;

import com.atlassian.pats.api.TokenValidator;
import com.atlassian.pats.service.TokenUtils;
import com.atlassian.security.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTokenValidator
implements TokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenValidator.class);
    private final PasswordEncoder passwordEncoder;

    public DefaultTokenValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean doTokensMatch(String token, String hashedToken) {
        logger.trace("Using password encoder to see if tokens match with hashed token: [{}]", (Object)hashedToken);
        try {
            String secret = TokenUtils.extractTokenInfo(token).getSecret();
            return this.passwordEncoder.isValidPassword(secret, hashedToken);
        }
        catch (IllegalArgumentException e) {
            logger.debug("Not a valid password! - error: [{}]", (Object)e.getMessage());
            return false;
        }
    }
}

