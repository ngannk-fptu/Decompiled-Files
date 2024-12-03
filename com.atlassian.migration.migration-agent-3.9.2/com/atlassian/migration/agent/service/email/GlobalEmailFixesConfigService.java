/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.InvalidEmailsConfigDto;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class GlobalEmailFixesConfigService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(GlobalEmailFixesConfigService.class);
    private static final String DUPLICATE_EMAILS_CONFIG = "duplicateEmailsConfig";
    private static final String INVALID_EMAILS_CONFIG = "invalidEmailsConfig";
    private final Supplier<PluginSettings> pluginSettingsSupplier = () -> ((PluginSettingsFactory)pluginSettingsFactory).createGlobalSettings();
    private final MigrationAgentConfiguration migrationAgentConfiguration;

    public GlobalEmailFixesConfigService(PluginSettingsFactory pluginSettingsFactory, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    public DuplicateEmailsConfigDto getDuplicateEmailsConfig() {
        String savedConfig = (String)this.pluginSettingsSupplier.get().get(this.getPluginKey(DUPLICATE_EMAILS_CONFIG));
        if (StringUtils.isNotBlank((CharSequence)savedConfig)) {
            return Jsons.readValue(savedConfig, DuplicateEmailsConfigDto.class);
        }
        return new DuplicateEmailsConfigDto();
    }

    public void saveDuplicateEmailsConfig(DuplicateEmailsConfigDto duplicateEmailsConfigDto) {
        this.pluginSettingsSupplier.get().put(this.getPluginKey(DUPLICATE_EMAILS_CONFIG), (Object)Jsons.valueAsString(duplicateEmailsConfigDto));
    }

    public InvalidEmailsConfigDto getInvalidEmailsConfig() {
        String savedConfig = (String)this.pluginSettingsSupplier.get().get(this.getPluginKey(INVALID_EMAILS_CONFIG));
        if (StringUtils.isNotBlank((CharSequence)savedConfig)) {
            return Jsons.readValue(savedConfig, InvalidEmailsConfigDto.class);
        }
        return new InvalidEmailsConfigDto();
    }

    public void saveInvalidEmailsConfig(InvalidEmailsConfigDto invalidEmailsConfig) {
        this.pluginSettingsSupplier.get().put(this.getPluginKey(INVALID_EMAILS_CONFIG), (Object)Jsons.valueAsString(invalidEmailsConfig));
    }

    @NotNull
    private String getPluginKey(String configKey) {
        return this.migrationAgentConfiguration.getPluginKey() + ":" + configKey;
    }
}

