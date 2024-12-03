/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.embedded.api.DataReEncryptor
 *  com.atlassian.crowd.embedded.api.SwitchableEncryptor
 *  com.atlassian.crowd.manager.crypto.EncryptionManager
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.crypto;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.crypto.Algorithm;
import com.atlassian.crowd.embedded.api.DataReEncryptor;
import com.atlassian.crowd.embedded.api.SwitchableEncryptor;
import com.atlassian.crowd.lock.ClusterLockWrapper;
import com.atlassian.crowd.manager.crypto.EncryptionManager;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultEncryptionManager
implements EncryptionManager {
    public static final String LOCK_NAME = "CROWD_ENCRYPTION";
    private static final Set<String> ALLOWED_ENCRYPTORS = Algorithm.getSecureKeySet();
    private static final Logger log = LoggerFactory.getLogger(DefaultEncryptionManager.class);
    private final Collection<DataReEncryptor> dataReEncryptors;
    private final ClusterLockWrapper clusterLockWrapper;
    private final SwitchableEncryptor switchableEncryptor;

    public DefaultEncryptionManager(Collection<DataReEncryptor> dataReEncryptors, SwitchableEncryptor switchableEncryptor, ClusterLockService clusterLockService) {
        this.dataReEncryptors = Collections.unmodifiableList(new ArrayList<DataReEncryptor>(dataReEncryptors));
        this.switchableEncryptor = switchableEncryptor;
        this.clusterLockWrapper = new ClusterLockWrapper(() -> clusterLockService.getLockForName(LOCK_NAME));
    }

    private void reEncryptAllPasswords() {
        log.info("Starting re-encryption of passwords in database");
        this.dataReEncryptors.forEach(DataReEncryptor::reEncrypt);
        log.info("Re-encryption of passwords in database finished");
    }

    public void changeEncryptor(String encryptorKey) {
        Collection<String> availableEncryptors = this.getAvailableEncryptorNames();
        Preconditions.checkArgument((boolean)availableEncryptors.contains(encryptorKey), (String)"Cannot switch to '%s', the encryptor must be one of %s", (Object)encryptorKey, availableEncryptors);
        this.clusterLockWrapper.run(() -> this.switchEncryptor(encryptorKey));
    }

    public void changeEncryptionKey() {
        this.clusterLockWrapper.run(() -> {
            this.switchableEncryptor.changeEncryptionKey();
            this.reEncryptAllPasswords();
        });
    }

    public void disableEncryption() {
        this.clusterLockWrapper.run(() -> this.switchEncryptor(null));
    }

    public Collection<String> getAvailableEncryptorNames() {
        return this.filterInsecureEncryptorNames(this.switchableEncryptor.getAvailableEncryptorKeys());
    }

    public Optional<String> getDefaultEncryptorName() {
        return this.switchableEncryptor.getCurrentEncryptorKey();
    }

    private void switchEncryptor(String encryptorKey) {
        this.switchableEncryptor.switchEncryptor(encryptorKey);
        this.reEncryptAllPasswords();
    }

    private Collection<String> filterInsecureEncryptorNames(Collection<String> allAvailableEncryptorNames) {
        return allAvailableEncryptorNames.stream().filter(ALLOWED_ENCRYPTORS::contains).collect(Collectors.toSet());
    }
}

