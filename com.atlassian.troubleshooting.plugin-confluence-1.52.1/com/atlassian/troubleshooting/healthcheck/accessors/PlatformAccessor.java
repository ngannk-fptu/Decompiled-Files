/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.healthcheck.accessors;

import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatform;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PlatformAccessor {
    public Optional<DbPlatform> getCurrentDbPlatform();
}

