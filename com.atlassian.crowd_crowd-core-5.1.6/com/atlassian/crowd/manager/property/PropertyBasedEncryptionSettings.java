/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.property.EncryptionSettings
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.atlassian.crowd.service.HomeDirectoryService
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.property;

import com.atlassian.crowd.manager.property.EncryptionSettings;
import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.crowd.service.HomeDirectoryService;
import java.io.File;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PropertyBasedEncryptionSettings
implements EncryptionSettings {
    private final PropertyManager propertyManager;
    private final HomeDirectoryService homeDirectoryService;

    public PropertyBasedEncryptionSettings(PropertyManager propertyManager, HomeDirectoryService homeDirectoryService) {
        this.propertyManager = propertyManager;
        this.homeDirectoryService = homeDirectoryService;
    }

    public Optional<String> getDefaultEncryptor() {
        return this.propertyManager.getOptionalProperty("crowd.encryption.encryptor.default");
    }

    public void setDefaultEncryptor(String encryptor) {
        this.propertyManager.setProperty("crowd.encryption.encryptor.default", encryptor);
    }

    public void setEncryptionKeyPath(String encryptor, String keyPath) {
        this.propertyManager.setProperty(this.getPropertyNameForEncryptionKeyPath(encryptor), keyPath);
    }

    public Optional<String> getEncryptionKeyPath(String encryptor) {
        return this.propertyManager.getOptionalProperty(this.getPropertyNameForEncryptionKeyPath(encryptor));
    }

    public String getKeyFilesDirectoryPath() {
        return this.homeDirectoryService.getSharedHome() + File.separator + "keys";
    }

    private String getPropertyNameForEncryptionKeyPath(String encryptor) {
        return String.format("crowd.encryption.encryptor.%s.keyPath", encryptor);
    }
}

