/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 *  com.atlassian.crowd.embedded.api.SwitchableEncryptor
 *  com.atlassian.crowd.manager.property.EncryptionSettings
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.embedded.api.Encryptor;
import com.atlassian.crowd.embedded.api.SwitchableEncryptor;
import com.atlassian.crowd.manager.property.EncryptionSettings;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixBasedSwitchableEncryptor
implements SwitchableEncryptor {
    private final Logger log = LoggerFactory.getLogger(PrefixBasedSwitchableEncryptor.class);
    private final EncryptionSettings encryptionSettings;
    private final Map<String, Encryptor> encryptors;

    public PrefixBasedSwitchableEncryptor(EncryptionSettings encryptionSettings, Map<String, Encryptor> encryptors) {
        this.encryptionSettings = encryptionSettings;
        this.encryptors = encryptors;
    }

    static String addPrefix(String password, String prefix) {
        return "{" + prefix + "}" + password;
    }

    public String encrypt(String password) {
        Optional defaultEncryptor = this.encryptionSettings.getDefaultEncryptor();
        if (!defaultEncryptor.isPresent()) {
            return password;
        }
        Encryptor encryptor = this.getEncryptor((String)defaultEncryptor.get());
        if (encryptor == null) {
            this.log.warn("Default encryptor not found for key {}, encryption not performed. Please select one of existing keys {}", (Object)defaultEncryptor, this.encryptors.keySet());
            return password;
        }
        this.log.debug("Encrypting password with encryptor {}", defaultEncryptor.get());
        return PrefixBasedSwitchableEncryptor.addPrefix(encryptor.encrypt(password), (String)defaultEncryptor.get());
    }

    protected Encryptor getEncryptor(String encryptorKey) {
        return this.encryptors.get(encryptorKey);
    }

    public String decrypt(String encryptedPassword) {
        int closeIndex = encryptedPassword.indexOf(125);
        if (closeIndex > 0 && encryptedPassword.charAt(0) == '{') {
            String encryptorKey = encryptedPassword.substring(1, closeIndex);
            Encryptor encryptor = this.getEncryptor(encryptorKey);
            if (encryptor != null) {
                try {
                    return encryptor.decrypt(encryptedPassword.substring(closeIndex + 1));
                }
                catch (RuntimeException e) {
                    this.log.error("Error during decryption", (Throwable)e);
                    return encryptedPassword;
                }
            }
            this.log.error("Password encrypted with unknown encryptor {}. Cannot decrypt password. ", (Object)encryptorKey);
        } else {
            this.log.debug("Cannot detected encryptor. Decryption not performed. Returned encrypted password");
        }
        return encryptedPassword;
    }

    public void switchEncryptor(String encryptorKey) {
        if (encryptorKey == null) {
            this.encryptionSettings.setDefaultEncryptor(null);
            this.log.info("Switched to non-encrypting/decrypting encryptor. Encryption is now disabled");
            return;
        }
        Preconditions.checkArgument((boolean)this.encryptors.containsKey(encryptorKey), (String)"Unknown encryptor %s", (Object)encryptorKey);
        Encryptor encryptor = this.getEncryptor(encryptorKey);
        encryptor.changeEncryptionKey();
        this.encryptionSettings.setDefaultEncryptor(encryptorKey);
        this.log.info("Switched to encryptor with key: {}", (Object)encryptorKey);
    }

    public boolean changeEncryptionKey() {
        Optional defaultEncryptor = this.encryptionSettings.getDefaultEncryptor();
        Preconditions.checkArgument((boolean)defaultEncryptor.isPresent(), (Object)"Cannot rotate encryption key when encryption is disabled");
        Encryptor encryptor = this.getEncryptor((String)defaultEncryptor.get());
        Preconditions.checkArgument((encryptor != null ? 1 : 0) != 0, (Object)"Unsupported encryptor selected. Key rotation is possible only for supported encryptors");
        boolean keyChangeSupported = encryptor.changeEncryptionKey();
        Preconditions.checkArgument((boolean)keyChangeSupported, (String)"Encryptor %s does not support key rotation", defaultEncryptor.get());
        return true;
    }

    public Collection<String> getAvailableEncryptorKeys() {
        return this.encryptors.keySet();
    }

    public Optional<String> getCurrentEncryptorKey() {
        return this.encryptionSettings.getDefaultEncryptor();
    }
}

