/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.secrets.api.SecretStore
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.internal.cipher;

import com.atlassian.confluence.internal.cipher.DataSourcePasswordDecrypter;
import com.atlassian.confluence.internal.cipher.DataSourcePasswordDecryptionException;
import com.atlassian.secrets.api.SecretStore;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class SecretStoreDataSourcePasswordDecrypter
implements DataSourcePasswordDecrypter {
    private final SecretStore secretStore;

    public SecretStoreDataSourcePasswordDecrypter(@Nonnull SecretStore secretStore) {
        this.secretStore = Objects.requireNonNull(secretStore, "secretStore");
    }

    @Override
    public String decrypt(String encryptedPassword) {
        if (StringUtils.isBlank((CharSequence)encryptedPassword)) {
            return encryptedPassword;
        }
        try {
            return this.secretStore.get(encryptedPassword);
        }
        catch (RuntimeException e) {
            throw new DataSourcePasswordDecryptionException(e);
        }
    }
}

