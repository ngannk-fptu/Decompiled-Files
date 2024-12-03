/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.SystemUtils
 */
package com.atlassian.troubleshooting.preupgrade.accessors;

import com.atlassian.troubleshooting.preupgrade.accessors.PupEnvironmentAccessor;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import org.apache.commons.lang3.SystemUtils;

public class DefaultPupEnvironmentAccessor
implements PupEnvironmentAccessor {
    @Override
    public MicroservicePreUpgradeDataDTO.Version.Platform getPlatform() {
        if ("32".equals(System.getProperty("sun.arch.data.model"))) {
            return MicroservicePreUpgradeDataDTO.Version.Platform.x32;
        }
        return MicroservicePreUpgradeDataDTO.Version.Platform.x64;
    }

    @Override
    public PupEnvironmentAccessor.OperatingSystem getOperatingSystem() {
        return SystemUtils.IS_OS_WINDOWS ? PupEnvironmentAccessor.OperatingSystem.WINDOWS : PupEnvironmentAccessor.OperatingSystem.LINUX;
    }

    @Override
    public String getJavaSpecificationVersion() {
        return System.getProperty("java.specification.version");
    }
}

