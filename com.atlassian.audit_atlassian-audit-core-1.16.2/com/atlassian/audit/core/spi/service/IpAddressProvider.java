/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.core.spi.service;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IpAddressProvider {
    @Nullable
    @Deprecated
    public String currentIpAddress();

    @Nullable
    public String remoteIpAddress();

    @Nonnull
    public Optional<String> forwarderIpAddress();
}

