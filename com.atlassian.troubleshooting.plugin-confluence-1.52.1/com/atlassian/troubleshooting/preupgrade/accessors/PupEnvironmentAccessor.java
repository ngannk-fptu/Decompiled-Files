/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.accessors;

import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;

public interface PupEnvironmentAccessor {
    public MicroservicePreUpgradeDataDTO.Version.Platform getPlatform();

    public OperatingSystem getOperatingSystem();

    public String getJavaSpecificationVersion();

    public static enum OperatingSystem {
        WINDOWS,
        LINUX;

    }
}

