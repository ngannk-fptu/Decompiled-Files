/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.preupgrade.model;

import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformQuery;
import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformRules;
import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultSupportedPlatformRules
implements SupportedPlatformRules {
    private static final Comparator<MicroservicePreUpgradeDataDTO.Version> COMPARE_VERSIONS = Comparator.comparing(v -> v.getVersion().major).thenComparing(v -> v.getVersion().minor).thenComparing(v -> v.getVersion().bugfix).reversed();
    private static final Comparator<MicroservicePreUpgradeDataDTO.Version> COMPARE_VERSIONS_ER_FIRST = Comparator.comparing(v -> v.isEnterprise() ? 0 : 1).thenComparing(COMPARE_VERSIONS);
    private static final Comparator<MicroservicePreUpgradeDataDTO.SupportedPlatform.Version> COMPARE_IGNORING_BUGFIX = Comparator.comparing(v -> v.major).thenComparing(v -> v.minor);

    @Override
    public MicroservicePreUpgradeDataDTO apply(MicroservicePreUpgradeDataDTO input, SupportedPlatformQuery query) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(query);
        return new MicroservicePreUpgradeDataDTO(input.product, this.filterAndSortVersions(input.versions, query.getVersion(), query.isEnterpriseRecommended()), this.filterSupportedPlatformsByVersions(input.supportedPlatforms, query.getVersion()));
    }

    private List<MicroservicePreUpgradeDataDTO.SupportedPlatform> filterSupportedPlatformsByVersions(List<MicroservicePreUpgradeDataDTO.SupportedPlatform> supportedPlatforms, String version) {
        Version currentVersion = Version.of(version);
        MicroservicePreUpgradeDataDTO.SupportedPlatform.Version currentComparisonVersion = new MicroservicePreUpgradeDataDTO.SupportedPlatform.Version(currentVersion.getMajor(), currentVersion.getMinor());
        return supportedPlatforms.stream().filter(p -> COMPARE_IGNORING_BUGFIX.compare(currentComparisonVersion, p.version) <= 0).collect(Collectors.toList());
    }

    private List<MicroservicePreUpgradeDataDTO.Version> filterAndSortVersions(List<MicroservicePreUpgradeDataDTO.Version> versions, String version, boolean enterpriseRecommended) {
        Version currentVersion = Version.of(version);
        return versions.stream().filter(v -> currentVersion.compareTo(Version.of(v.getVersion().major, v.getVersion().minor, v.getVersion().bugfix)) <= 0).sorted(enterpriseRecommended ? COMPARE_VERSIONS_ER_FIRST : COMPARE_VERSIONS).collect(Collectors.toList());
    }
}

