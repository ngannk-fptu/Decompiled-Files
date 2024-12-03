/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.embedded.api.SwitchableEncryptor
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.crypto;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.embedded.api.SwitchableEncryptor;
import com.atlassian.crowd.lock.ClusterLockWrapper;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;

public class ClusterLockingEncryptor
implements SwitchableEncryptor {
    private final SwitchableEncryptor delegate;
    private final ClusterLockWrapper clusterLockWrapper;

    public ClusterLockingEncryptor(SwitchableEncryptor delegate, ClusterLockService clusterLockService) {
        this.delegate = delegate;
        this.clusterLockWrapper = new ClusterLockWrapper(() -> clusterLockService.getLockForName("CROWD_ENCRYPTION"));
    }

    public String encrypt(String password) {
        return this.clusterLockWrapper.run(() -> this.delegate.encrypt(password));
    }

    public String decrypt(String encryptedPassword) {
        return this.delegate.decrypt(encryptedPassword);
    }

    public boolean changeEncryptionKey() {
        return this.clusterLockWrapper.run(() -> ((SwitchableEncryptor)this.delegate).changeEncryptionKey());
    }

    public void switchEncryptor(@Nullable String encryptorKey) {
        this.clusterLockWrapper.run(() -> this.delegate.switchEncryptor(encryptorKey));
    }

    public Collection<String> getAvailableEncryptorKeys() {
        return this.delegate.getAvailableEncryptorKeys();
    }

    public Optional<String> getCurrentEncryptorKey() {
        return this.delegate.getCurrentEncryptorKey();
    }
}

