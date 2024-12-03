/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidCredentialException
 */
package com.atlassian.crowd.directory.ldap.credential;

import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidCredentialException;
import java.io.UnsupportedEncodingException;

public class ActiveDirectoryCredentialEncoder
implements LDAPCredentialEncoder.LDAPCredentialToByteArrayEncoder {
    private static final String AD_PASSWORD_ENCODED = "UTF-16LE";
    private final LDAPCredentialEncoder.LDAPCredentialToStringEncoder baseEncoder;

    public ActiveDirectoryCredentialEncoder(LDAPCredentialEncoder.LDAPCredentialToStringEncoder baseEncoder) {
        this.baseEncoder = baseEncoder;
    }

    @Override
    public byte[] encodeCredential(PasswordCredential passwordCredential) throws InvalidCredentialException {
        String guaranteedUnencryptedPassword = this.baseEncoder.encodeCredential(passwordCredential);
        return ActiveDirectoryCredentialEncoder.encodeValueForUnicodePwdAttr(guaranteedUnencryptedPassword);
    }

    private static byte[] encodeValueForUnicodePwdAttr(String unhashedPasswordToEncode) throws InvalidCredentialException {
        try {
            String newQuotedPassword = "\"" + unhashedPasswordToEncode + "\"";
            return newQuotedPassword.getBytes(AD_PASSWORD_ENCODED);
        }
        catch (UnsupportedEncodingException e) {
            throw new InvalidCredentialException(e.getMessage(), (Throwable)e);
        }
    }

    @Override
    public boolean supportsSettingEncryptedPasswords() {
        return this.baseEncoder.supportsSettingEncryptedPasswords();
    }

    public LDAPCredentialEncoder.LDAPCredentialToStringEncoder getBaseEncoder() {
        return this.baseEncoder;
    }
}

