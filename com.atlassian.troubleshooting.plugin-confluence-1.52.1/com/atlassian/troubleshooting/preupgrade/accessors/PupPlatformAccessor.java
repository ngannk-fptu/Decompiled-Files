/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.preupgrade.accessors;

import com.atlassian.troubleshooting.healthcheck.accessors.PlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.modz.Modifications;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PupPlatformAccessor
extends PlatformAccessor {
    public Optional<Modifications> getModifiedFiles();

    public MicroservicePreUpgradeDataDTO.Version.SubProduct calculateSubProduct();
}

