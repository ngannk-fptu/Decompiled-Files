/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import java.util.List;

public interface UpgradePathSectionFactory {
    public static final String KEY_PREFIX = "stp.pup.";
    public static final String MANAGE_APPS_URL = "/plugins/servlet/upm";
    public static final String UPDATE_CHECK_URL = "/plugins/servlet/upm/check?source=manage";
    public static final String VERSIONS_AND_LICENSES_URL = "/plugins/servlet/applications/versions-licenses";

    public String getPlatformId();

    public boolean isClustered();

    public List<PreUpgradeInfoDto.Version.UpgradePathSection> getSections(MicroservicePreUpgradeDataDTO.Version var1, boolean var2);
}

