/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.security.password;

import com.atlassian.user.security.password.Credential;

public interface PasswordEncryptor {
    public String encrypt(String var1);

    public String getEncryptedValue(Credential var1);
}

