/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.coverage.CoverageUpdatedEvent;
import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SalAuditCoverageConfigService
implements InternalAuditCoverageConfigService {
    private static final String COVERAGE_CONFIG_PREFIX = "com.atlassian.audit.plugin:audit-config:coverage:";
    private static final BiMap<EffectiveCoverageLevel, String> stringKeyByLevel = ImmutableBiMap.copyOf(Stream.of(EffectiveCoverageLevel.values()).collect(Collectors.toMap(Function.identity(), EffectiveCoverageLevel::getKey)));
    private static final BiMap<CoverageArea, String> stringKeyByArea = ImmutableBiMap.copyOf(Stream.of(CoverageArea.values()).collect(Collectors.toMap(Function.identity(), a -> a.toString().toLowerCase())));
    private final PluginSettingsFactory pluginSettingsFactory;
    private final EventPublisher eventPublisher;

    public SalAuditCoverageConfigService(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.eventPublisher = eventPublisher;
    }

    public AuditCoverageConfig getConfig() {
        PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
        return this.getCoverage(settings);
    }

    @Override
    public void updateConfig(AuditCoverageConfig config) {
        PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
        config.getLevelByArea().forEach((area, level) -> settings.put(this.getCoverageConfigurationKey((CoverageArea)area), stringKeyByLevel.get(level)));
        this.eventPublisher.publish((Object)new CoverageUpdatedEvent());
    }

    private AuditCoverageConfig getCoverage(PluginSettings settings) {
        HashMap<CoverageArea, EffectiveCoverageLevel> levelByArea = new HashMap<CoverageArea, EffectiveCoverageLevel>();
        for (CoverageArea area : CoverageArea.values()) {
            String levelValue = (String)settings.get(this.getCoverageConfigurationKey(area));
            EffectiveCoverageLevel level = Optional.ofNullable(levelValue).map(arg_0 -> stringKeyByLevel.inverse().get(arg_0)).orElse(EffectiveCoverageLevel.BASE);
            levelByArea.put(area, level);
        }
        return new AuditCoverageConfig(levelByArea);
    }

    static String levelToString(EffectiveCoverageLevel level) {
        return (String)stringKeyByLevel.get((Object)level);
    }

    static String areaToString(CoverageArea area) {
        return (String)stringKeyByArea.get((Object)area);
    }

    private String getCoverageConfigurationKey(CoverageArea area) {
        return COVERAGE_CONFIG_PREFIX + (String)stringKeyByArea.get((Object)area);
    }
}

