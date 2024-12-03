/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.core.spi.service.IpAddressProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.core.spi.service.IpAddressProvider;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfluenceAuditIpAddressProvider
implements IpAddressProvider {
    @Nullable
    public String currentIpAddress() {
        String proxyAddresses = RequestCacheThreadLocal.getXForwardedFor();
        String remoteAddress = RequestCacheThreadLocal.getRemoteAddress();
        if (proxyAddresses == null && remoteAddress == null) {
            return null;
        }
        return Stream.of(proxyAddresses, remoteAddress).filter(Objects::nonNull).collect(Collectors.joining(", "));
    }

    @Nullable
    public String remoteIpAddress() {
        return RequestCacheThreadLocal.getRemoteAddress();
    }

    @Nonnull
    public Optional<String> forwarderIpAddress() {
        return Optional.ofNullable(RequestCacheThreadLocal.getXForwardedFor());
    }
}

