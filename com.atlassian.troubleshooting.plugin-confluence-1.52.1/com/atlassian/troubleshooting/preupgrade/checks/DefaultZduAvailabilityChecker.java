/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.checks;

import com.atlassian.troubleshooting.preupgrade.checks.ZduAvailabilityChecker;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultZduAvailabilityChecker
implements ZduAvailabilityChecker {
    @Override
    public boolean isZduAvailable(Version localVersion, MicroservicePreUpgradeDataDTO.Version.VersionNumber targetVersion, List<MicroservicePreUpgradeDataDTO.SupportedPlatform> supportedPlatforms) {
        return this.getMinimumVersionForZduCompatibility(targetVersion, supportedPlatforms).map(base -> base.compareTo(localVersion) <= 0).orElse(false);
    }

    private Optional<Version> getMinimumVersionForZduCompatibility(MicroservicePreUpgradeDataDTO.Version.VersionNumber version, List<MicroservicePreUpgradeDataDTO.SupportedPlatform> supportedPlatforms) {
        Version targetVersion = Version.of(version.getMajor(), version.getMinor(), version.getBugfix());
        return supportedPlatforms.stream().filter(sp -> sp.getVersion().isSameMajorAndMinorVersion(version)).flatMap(sp -> sp.getZduBaseVersions().stream()).map(Version::of).sorted(Collections.reverseOrder()).filter(base -> base.compareTo(targetVersion) <= 0).findFirst();
    }
}

