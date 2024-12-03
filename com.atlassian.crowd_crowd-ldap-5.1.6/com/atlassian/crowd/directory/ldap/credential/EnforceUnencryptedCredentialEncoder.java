/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.util.PasswordHelper
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.directory.ldap.credential;

import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.util.PasswordHelper;
import com.google.common.base.Preconditions;

public class EnforceUnencryptedCredentialEncoder
implements LDAPCredentialEncoder.LDAPCredentialToStringEncoder {
    private final PasswordHelper passwordHelper;

    public EnforceUnencryptedCredentialEncoder(PasswordHelper passwordHelper) {
        this.passwordHelper = (PasswordHelper)Preconditions.checkNotNull((Object)passwordHelper);
    }

    @Override
    public String encodeCredential(PasswordCredential passwordCredential) throws InvalidCredentialException {
        if (PasswordCredential.NONE.equals((Object)passwordCredential)) {
            return this.passwordHelper.generateRandomPassword();
        }
        if (passwordCredential.isEncryptedCredential()) {
            throw new InvalidCredentialException("Setting already encrypted passwords is not supported in this directory");
        }
        return passwordCredential.getCredential();
    }

    @Override
    public boolean supportsSettingEncryptedPasswords() {
        return false;
    }

    public PasswordHelper getPasswordHelper() {
        return this.passwordHelper;
    }
}

