/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.checks;

import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import java.util.List;

public interface PlatformsChecker {
    public List<PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus> checkSupportedPlatforms(MicroservicePreUpgradeDataDTO.Version var1, List<MicroservicePreUpgradeDataDTO.SupportedPlatform> var2);
}

