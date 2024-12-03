/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Configurable
 */
package com.atlassian.migration.agent.service.encryption;

import com.atlassian.migration.agent.service.encryption.AutowireHelper;
import com.atlassian.migration.agent.service.encryption.EncryptionService;
import javax.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class EncryptedContainerTokenConverter
implements AttributeConverter<String, String> {
    @Autowired
    EncryptionService encryptionService;

    public String convertToDatabaseColumn(String attribute) {
        AutowireHelper.autowire(this, this.encryptionService);
        return this.encryptionService.encrypt(attribute);
    }

    public String convertToEntityAttribute(String dbData) {
        AutowireHelper.autowire(this, this.encryptionService);
        return this.encryptionService.decrypt(dbData);
    }
}

