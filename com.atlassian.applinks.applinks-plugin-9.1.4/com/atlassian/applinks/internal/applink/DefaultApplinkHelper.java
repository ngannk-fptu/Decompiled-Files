/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.applink;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultApplinkHelper
implements ApplinkHelper {
    private final ApplicationLinkService applicationLinkService;
    private final MutatingApplicationLinkService mutatingApplicationLinkService;
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final ServiceExceptionFactory serviceExceptionFactory;

    @Autowired
    public DefaultApplinkHelper(ApplicationLinkService applicationLinkService, MutatingApplicationLinkService mutatingApplicationLinkService, ReadOnlyApplicationLinkService readOnlyApplicationLinkService, ServiceExceptionFactory serviceExceptionFactory) {
        this.applicationLinkService = applicationLinkService;
        this.mutatingApplicationLinkService = mutatingApplicationLinkService;
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.serviceExceptionFactory = serviceExceptionFactory;
    }

    @Override
    @Nonnull
    public ApplicationLink getApplicationLink(@Nonnull ApplicationId id) throws NoSuchApplinkException {
        Objects.requireNonNull(id, "id");
        try {
            ApplicationLink link = this.applicationLinkService.getApplicationLink(id);
            this.checkExists(id, (ReadOnlyApplicationLink)link);
            return link;
        }
        catch (TypeNotInstalledException e) {
            throw this.typeNotInstalled(e);
        }
    }

    @Override
    @Nonnull
    public MutableApplicationLink getMutableApplicationLink(@Nonnull ApplicationId id) throws NoSuchApplinkException {
        Objects.requireNonNull(id, "id");
        try {
            MutableApplicationLink link = this.mutatingApplicationLinkService.getApplicationLink(id);
            this.checkExists(id, (ReadOnlyApplicationLink)link);
            return link;
        }
        catch (TypeNotInstalledException e) {
            throw this.typeNotInstalled(e);
        }
    }

    @Override
    @Nonnull
    public ReadOnlyApplicationLink getReadOnlyApplicationLink(@Nonnull ApplicationId id) throws NoSuchApplinkException {
        Objects.requireNonNull(id, "id");
        ReadOnlyApplicationLink link = this.readOnlyApplicationLinkService.getApplicationLink(id);
        this.checkExists(id, link);
        return link;
    }

    @Override
    public void makePrimary(@Nonnull ApplicationId id) throws NoSuchApplinkException {
        Objects.requireNonNull(id, "id");
        try {
            this.mutatingApplicationLinkService.makePrimary(id);
        }
        catch (IllegalArgumentException e) {
            throw this.serviceExceptionFactory.raise(NoSuchApplinkException.class, new Serializable[]{id});
        }
        catch (TypeNotInstalledException e) {
            throw this.typeNotInstalled(e);
        }
    }

    private void checkExists(@Nonnull ApplicationId id, ReadOnlyApplicationLink link) throws NoSuchApplinkException {
        if (link == null) {
            throw this.serviceExceptionFactory.raise(NoSuchApplinkException.class, new Serializable[]{id});
        }
    }

    private NoSuchApplinkException typeNotInstalled(TypeNotInstalledException e) throws NoSuchApplinkException {
        return this.serviceExceptionFactory.raise(NoSuchApplinkException.class, I18nKey.newI18nKey("applinks.service.error.nosuchentity.apptype", new Serializable[]{e.getName(), e.getType()}), e);
    }
}

