/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.crypto;

import java.util.Collection;
import java.util.Optional;

public interface EncryptionManager {
    public void changeEncryptor(String var1);

    public void changeEncryptionKey();

    public Collection<String> getAvailableEncryptorNames();

    public Optional<String> getDefaultEncryptorName();

    public void disableEncryption();
}

