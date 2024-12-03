/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.accessors;

import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatform;
import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.modz.Modifications;
import java.util.Optional;

public class NoopPupPlatformAccessor
implements PupPlatformAccessor {
    @Override
    public Optional<DbPlatform> getCurrentDbPlatform() {
        return Optional.empty();
    }

    @Override
    public Optional<Modifications> getModifiedFiles() {
        return Optional.empty();
    }

    @Override
    public MicroservicePreUpgradeDataDTO.Version.SubProduct calculateSubProduct() {
        throw new UnsupportedOperationException("calculateSubProduct is not implemented for this product.");
    }
}

