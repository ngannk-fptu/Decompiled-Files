/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.password.encoder.PasswordEncoder
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.directory.ldap.credential;

import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.password.encoder.PasswordEncoder;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import org.apache.commons.lang3.StringUtils;

public class EncryptingCredentialEncoder
implements LDAPCredentialEncoder.LDAPCredentialToStringEncoder {
    private final PasswordEncoderFactory passwordEncoderFactory;
    private final String encryptionAlgorithm;

    public EncryptingCredentialEncoder(PasswordEncoderFactory passwordEncoderFactory, String encryptionAlgorithm) {
        this.passwordEncoderFactory = passwordEncoderFactory;
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    @Override
    public String encodeCredential(PasswordCredential passwordCredential) throws InvalidCredentialException {
        if (passwordCredential.isEncryptedCredential() || StringUtils.isBlank((CharSequence)this.encryptionAlgorithm)) {
            return passwordCredential.getCredential();
        }
        PasswordEncoder passwordEncoder = this.passwordEncoderFactory.getLdapEncoder(this.encryptionAlgorithm);
        return passwordEncoder.encodePassword(passwordCredential.getCredential(), null);
    }

    @Override
    public boolean supportsSettingEncryptedPasswords() {
        return true;
    }

    public PasswordEncoderFactory getPasswordEncoderFactory() {
        return this.passwordEncoderFactory;
    }

    public String getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }
}

