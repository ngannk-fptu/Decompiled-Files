/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.core.ApplinkStatus
 *  com.atlassian.applinks.core.ApplinkStatusService
 *  com.atlassian.applinks.internal.common.exception.NoAccessException
 *  com.atlassian.applinks.internal.common.exception.NoSuchApplinkException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.pulp.wrm;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.core.ApplinkStatusService;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Named
public class ApplicationLinkStatusInterrogator {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLinkStatusInterrogator.class);
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final ApplinkStatusService applinkStatusService;

    @Inject
    public ApplicationLinkStatusInterrogator(@ComponentImport ReadOnlyApplicationLinkService readOnlyApplicationLinkService, @ComponentImport ApplinkStatusService applinkStatusService) {
        this.readOnlyApplicationLinkService = Objects.requireNonNull(readOnlyApplicationLinkService);
        this.applinkStatusService = Objects.requireNonNull(applinkStatusService);
    }

    public boolean areApplicationLinksInstalled() {
        return this.readOnlyApplicationLinkService.getApplicationLinks() != null && this.readOnlyApplicationLinkService.getApplicationLinks().iterator().hasNext();
    }

    public long getNumberOfFailedApplicationLinks() {
        if (this.readOnlyApplicationLinkService.getApplicationLinks() == null) {
            return 0L;
        }
        return StreamSupport.stream(this.readOnlyApplicationLinkService.getApplicationLinks().spliterator(), false).filter(this::isFailedApplicationLink).count();
    }

    private boolean isFailedApplicationLink(ReadOnlyApplicationLink applicationLink) {
        return this.getStatus(applicationLink).map(status -> !status.isWorking()).orElse(false);
    }

    private Optional<ApplinkStatus> getStatus(ReadOnlyApplicationLink applicationLink) {
        try {
            return Optional.of(this.applinkStatusService.getApplinkStatus(applicationLink.getId()));
        }
        catch (NoAccessException | NoSuchApplinkException e) {
            log.debug("Unable to retrieve application link status", e);
            return Optional.empty();
        }
    }
}

