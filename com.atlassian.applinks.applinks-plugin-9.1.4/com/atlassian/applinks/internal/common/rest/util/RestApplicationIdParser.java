/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  javax.annotation.Nonnull
 *  javax.ws.rs.WebApplicationException
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.rest.util;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.internal.common.exception.InvalidApplicationIdException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.rest.util.RestResponses;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import org.springframework.beans.factory.annotation.Autowired;

public final class RestApplicationIdParser {
    private final ServiceExceptionFactory serviceExceptionFactory;

    @Autowired
    public RestApplicationIdParser(ServiceExceptionFactory serviceExceptionFactory) {
        this.serviceExceptionFactory = serviceExceptionFactory;
    }

    @Deprecated
    public static ApplicationId parseApplicationId(@Nonnull String id) {
        try {
            return new ApplicationId(id);
        }
        catch (IllegalArgumentException e) {
            throw new WebApplicationException(RestResponses.badRequest(e.getMessage()));
        }
    }

    @Nonnull
    public ApplicationId parse(@Nonnull String id) throws InvalidApplicationIdException {
        try {
            return new ApplicationId(Objects.requireNonNull(id, "id"));
        }
        catch (IllegalArgumentException e) {
            throw this.serviceExceptionFactory.raise(InvalidApplicationIdException.class, InvalidApplicationIdException.invalidIdI18nKey(id), e);
        }
    }
}

