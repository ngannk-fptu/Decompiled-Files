/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.util.Map;

public interface VaultTransitKey {
    public String getName();

    public String getType();

    public boolean isDeletionAllowed();

    public boolean isDerived();

    public boolean isExportable();

    public Map<String, Object> getKeys();

    public int getLatestVersion();

    public int getMinDecryptionVersion();

    public int getMinEncryptionVersion();

    public boolean supportsDecryption();

    public boolean supportsEncryption();

    public boolean supportsDerivation();

    public boolean supportsSigning();
}

