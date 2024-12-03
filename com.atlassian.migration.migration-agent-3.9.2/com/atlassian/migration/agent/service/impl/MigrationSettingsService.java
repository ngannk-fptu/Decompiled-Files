/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.ConcurrencySettingsEnum;
import com.atlassian.migration.agent.dto.MigrationSettingsDto;
import com.atlassian.migration.agent.service.impl.CloudTypeSettingsService;
import com.atlassian.migration.agent.service.impl.ConcurrencySettingsService;
import com.atlassian.migration.agent.service.impl.MigrationSettingsType;
import java.util.Map;

public class MigrationSettingsService {
    private final CloudTypeSettingsService cloudTypeSettingsService;
    private final ConcurrencySettingsService concurrencySettingsService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public MigrationSettingsService(MigrationDarkFeaturesManager migrationDarkFeaturesManager, CloudTypeSettingsService cloudTypeSettingsService, ConcurrencySettingsService concurrencySettingsService) {
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.cloudTypeSettingsService = cloudTypeSettingsService;
        this.concurrencySettingsService = concurrencySettingsService;
    }

    public MigrationSettingsDto getSettings(String type) {
        if (type == null) {
            return this.getAllSettings();
        }
        return this.getTypeSettings(type);
    }

    private MigrationSettingsDto getAllSettings() {
        MigrationSettingsDto migrationSettingsDto = new MigrationSettingsDto();
        if (this.migrationDarkFeaturesManager.fedRAMPEnabled()) {
            migrationSettingsDto.setCloudType(this.cloudTypeSettingsService.getCloudTypeSettings());
        }
        migrationSettingsDto.setConcurrency((Map<ConcurrencySettingsEnum, Integer>)this.concurrencySettingsService.getSettings());
        return migrationSettingsDto;
    }

    private MigrationSettingsDto getTypeSettings(String type) {
        try {
            MigrationSettingsType migrationSettingsType = MigrationSettingsType.getByType(type);
            MigrationSettingsDto migrationSettingsDto = new MigrationSettingsDto();
            if (migrationSettingsType.equals((Object)MigrationSettingsType.CLOUD_TYPE)) {
                migrationSettingsDto.setCloudType(this.cloudTypeSettingsService.getCloudTypeSettings());
            } else if (migrationSettingsType.equals((Object)MigrationSettingsType.CONCURRENCY)) {
                migrationSettingsDto.setConcurrency((Map<ConcurrencySettingsEnum, Integer>)this.concurrencySettingsService.getSettings());
            }
            return migrationSettingsDto;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public void updateSettings(MigrationSettingsDto migrationSettingsDto) {
        if (migrationSettingsDto.getCloudType() != null) {
            if (this.migrationDarkFeaturesManager.fedRAMPEnabled()) {
                this.cloudTypeSettingsService.setCloudTypeSettings(migrationSettingsDto.getCloudType());
            } else {
                throw new IllegalArgumentException("FedRAMP is not enabled");
            }
        }
        if (migrationSettingsDto.getConcurrency() != null) {
            this.concurrencySettingsService.putSettings(migrationSettingsDto.getConcurrency());
        }
    }
}

