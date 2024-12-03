/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.checks;

import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.List;

public interface ZduAvailabilityChecker {
    public boolean isZduAvailable(Version var1, MicroservicePreUpgradeDataDTO.Version.VersionNumber var2, List<MicroservicePreUpgradeDataDTO.SupportedPlatform> var3);
}

