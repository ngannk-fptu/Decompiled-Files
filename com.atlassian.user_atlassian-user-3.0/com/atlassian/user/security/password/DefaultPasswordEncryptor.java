/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.user.security.password;

import com.atlassian.user.security.password.Credential;
import com.atlassian.user.security.password.PasswordEncryptor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public final class DefaultPasswordEncryptor
implements PasswordEncryptor {
    public String encrypt(String unencryptedPassword) {
        byte[] bytes = unencryptedPassword.getBytes();
        byte[] hash = DigestUtils.sha512((byte[])bytes);
        return new String(Base64.encodeBase64((byte[])hash));
    }

    public String getEncryptedValue(Credential credential) {
        if (credential.isEncrypted()) {
            return credential.getValue();
        }
        return this.encrypt(credential.getValue());
    }
}

