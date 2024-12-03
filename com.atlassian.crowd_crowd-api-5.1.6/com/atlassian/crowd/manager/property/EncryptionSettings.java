/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.property;

import java.util.Optional;

public interface EncryptionSettings {
    public Optional<String> getDefaultEncryptor();

    public void setDefaultEncryptor(String var1);

    public void setEncryptionKeyPath(String var1, String var2);

    public Optional<String> getEncryptionKeyPath(String var1);

    public String getKeyFilesDirectoryPath();
}

