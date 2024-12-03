/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core.impl.service;

import com.atlassian.audit.core.spi.service.IpAddressProvider;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorIgnoredAuditIpAddressProvider
implements IpAddressProvider {
    private static final Logger log = LoggerFactory.getLogger(ErrorIgnoredAuditIpAddressProvider.class);
    private final IpAddressProvider delegate;

    public ErrorIgnoredAuditIpAddressProvider(IpAddressProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public String currentIpAddress() {
        try {
            return this.delegate.currentIpAddress();
        }
        catch (RuntimeException e) {
            log.error("Fail to determine source.", (Throwable)e);
            return null;
        }
    }

    @Override
    @Nullable
    public String remoteIpAddress() {
        try {
            return this.delegate.remoteIpAddress();
        }
        catch (RuntimeException e) {
            log.error("Fail to determine remote IP address.", (Throwable)e);
            return null;
        }
    }

    @Override
    public Optional<String> forwarderIpAddress() {
        try {
            return this.delegate.forwarderIpAddress();
        }
        catch (RuntimeException e) {
            log.error("Fail to determine forwarder IP address.", (Throwable)e);
            return Optional.empty();
        }
    }
}

