/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidCredentialException
 */
package com.atlassian.crowd.directory.ldap.credential;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidCredentialException;

public interface LDAPCredentialEncoder {
    public Object encodeCredential(PasswordCredential var1) throws InvalidCredentialException;

    public boolean supportsSettingEncryptedPasswords();

    public static interface LDAPCredentialToByteArrayEncoder
    extends LDAPCredentialEncoder {
        public byte[] encodeCredential(PasswordCredential var1) throws InvalidCredentialException;
    }

    public static interface LDAPCredentialToStringEncoder
    extends LDAPCredentialEncoder {
        @Override
        public String encodeCredential(PasswordCredential var1) throws InvalidCredentialException;
    }
}

