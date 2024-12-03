/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.api;

import com.atlassian.secrets.api.SecretStore;
import java.util.Optional;

public interface SecretStoreProvider {
    public static final String ATLASSIAN_PASSWORD_CIPHER_PROVIDER_XML_KEY = "atlassian-password-cipher-provider";
    public static final String ATLASSIAN_PASSWORD_CIPHER_PROVIDER_PROPERTY_KEY = "jdbc.password.decrypter.classname";

    public String getDefaultSecretStoreClassName();

    public Optional<SecretStore> getInstance(String var1);
}

