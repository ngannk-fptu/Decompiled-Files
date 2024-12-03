/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  com.google.common.collect.Maps
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;

public class LicenseAwareCoverageConfigService
implements InternalAuditCoverageConfigService {
    private final ProductLicenseChecker licenseChecker;
    private final InternalAuditCoverageConfigService origin;

    public LicenseAwareCoverageConfigService(ProductLicenseChecker licenseChecker, InternalAuditCoverageConfigService origin) {
        this.licenseChecker = licenseChecker;
        this.origin = origin;
    }

    @Override
    public void updateConfig(AuditCoverageConfig config) {
        if (this.licenseChecker.isNotDcLicense()) {
            for (EffectiveCoverageLevel newLevel : config.getLevelByArea().values()) {
                if (!newLevel.shouldAllow(CoverageLevel.ADVANCED)) continue;
                throw new IllegalArgumentException("License doesn't allow to set this coverage level " + newLevel);
            }
        }
        this.origin.updateConfig(config);
    }

    public AuditCoverageConfig getConfig() {
        AuditCoverageConfig config = this.origin.getConfig();
        if (this.licenseChecker.isNotDcLicense()) {
            return new AuditCoverageConfig(Maps.transformValues((Map)config.getLevelByArea(), level -> Objects.requireNonNull(level).mostRestrictive(EffectiveCoverageLevel.BASE)));
        }
        return config;
    }
}

