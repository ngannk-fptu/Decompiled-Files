/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.SecureRandomFactory
 *  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.exception.PasswordEncoderException;
import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.security.random.SecureRandomFactory;
import java.security.SecureRandom;

public class BCryptPasswordEncoder
implements InternalPasswordEncoder {
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder crypto;

    public BCryptPasswordEncoder() {
        this(10, SecureRandomFactory.newInstance());
    }

    public BCryptPasswordEncoder(int rounds, SecureRandom random) {
        this.crypto = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(rounds, random);
    }

    @Override
    public String getKey() {
        return "bcrypt";
    }

    @Override
    public String encodePassword(String rawPass, Object salt) throws PasswordEncoderException {
        return this.crypto.encode((CharSequence)rawPass);
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        try {
            return this.crypto.matches((CharSequence)rawPass, encPass);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }
}

