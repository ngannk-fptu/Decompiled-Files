/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.password.DefaultPasswordEncoder
 *  com.atlassian.security.password.PKCS5S2PasswordHashGenerator
 *  com.atlassian.security.password.PasswordEncoder
 *  com.atlassian.security.password.PasswordHashGenerator
 *  com.atlassian.security.password.SaltGenerator
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.exception.PasswordEncoderException;
import com.atlassian.crowd.password.encoder.AtlassianSHA1PasswordEncoder;
import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.PasswordEncoder;
import com.atlassian.crowd.password.encoder.UpgradeablePasswordEncoder;
import com.atlassian.crowd.password.saltgenerator.SecureRandomSaltGenerator;
import com.atlassian.security.password.DefaultPasswordEncoder;
import com.atlassian.security.password.PKCS5S2PasswordHashGenerator;
import com.atlassian.security.password.PasswordHashGenerator;
import com.atlassian.security.password.SaltGenerator;

public class AtlassianSecurityPasswordEncoder
implements InternalPasswordEncoder,
UpgradeablePasswordEncoder {
    private final com.atlassian.security.password.PasswordEncoder defaultPasswordEncoder;
    private final PasswordEncoder oldPasswordEncoder;

    public AtlassianSecurityPasswordEncoder() {
        this.defaultPasswordEncoder = new DefaultPasswordEncoder("PKCS5S2", (PasswordHashGenerator)new PKCS5S2PasswordHashGenerator(), (SaltGenerator)SecureRandomSaltGenerator.INSTANCE);
        this.oldPasswordEncoder = new AtlassianSHA1PasswordEncoder();
    }

    AtlassianSecurityPasswordEncoder(com.atlassian.security.password.PasswordEncoder defaultPasswordEncoder, PasswordEncoder oldPasswordEncoder) {
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.oldPasswordEncoder = oldPasswordEncoder;
    }

    @Override
    public String encodePassword(String rawPass, Object salt) throws PasswordEncoderException {
        try {
            return this.defaultPasswordEncoder.encodePassword(rawPass);
        }
        catch (IllegalArgumentException e) {
            throw new PasswordEncoderException("Password could not be encoded.", e);
        }
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        if (this.defaultPasswordEncoder.canDecodePassword(encPass)) {
            try {
                return this.defaultPasswordEncoder.isValidPassword(rawPass, encPass);
            }
            catch (IllegalArgumentException e) {
                return false;
            }
        }
        return this.oldPasswordEncoder.isPasswordValid(encPass, rawPass, salt);
    }

    @Override
    public boolean isUpgradeRequired(String encPass) {
        return !this.defaultPasswordEncoder.canDecodePassword(encPass);
    }

    @Override
    public String getKey() {
        return "atlassian-security";
    }
}

