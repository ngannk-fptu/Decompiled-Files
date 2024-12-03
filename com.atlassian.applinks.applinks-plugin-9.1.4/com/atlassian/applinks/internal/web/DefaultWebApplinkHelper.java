/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.web;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.exception.InvalidApplicationIdException;
import com.atlassian.applinks.internal.common.exception.InvalidRequestException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.common.net.Uris;
import com.atlassian.applinks.internal.web.WebApplinkHelper;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultWebApplinkHelper
implements WebApplinkHelper {
    private final ApplinkHelper applinkHelper;
    private final ServiceExceptionFactory serviceExceptionFactory;

    @Autowired
    public DefaultWebApplinkHelper(ApplinkHelper applinkHelper, ServiceExceptionFactory serviceExceptionFactory) {
        this.applinkHelper = applinkHelper;
        this.serviceExceptionFactory = serviceExceptionFactory;
    }

    @Override
    @Nonnull
    public ApplicationLink getApplicationLink(@Nonnull HttpServletRequest request) throws InvalidRequestException, InvalidApplicationIdException, NoSuchApplinkException {
        String applinkId = (String)Iterables.getFirst(Uris.toComponents(request.getPathInfo()), null);
        if (applinkId == null) {
            throw this.serviceExceptionFactory.raise(InvalidRequestException.class, I18nKey.newI18nKey("applinks.service.error.request.invalid.noapplinkid", new Serializable[0]));
        }
        try {
            return this.applinkHelper.getApplicationLink(new ApplicationId(applinkId));
        }
        catch (IllegalArgumentException e) {
            throw this.serviceExceptionFactory.raise(InvalidApplicationIdException.class, InvalidApplicationIdException.invalidIdI18nKey(applinkId), e);
        }
    }
}

