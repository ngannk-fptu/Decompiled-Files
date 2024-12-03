/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.common.net;

import com.atlassian.applinks.internal.common.exception.EntityModificationException;
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.atlassian.applinks.internal.common.exception.InvalidEntityStateException;
import com.atlassian.applinks.internal.common.exception.NoSuchEntityException;
import com.atlassian.applinks.internal.common.exception.NotAuthenticatedException;
import com.atlassian.applinks.internal.common.exception.PermissionException;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public final class ServiceExceptionHttpMapper {
    private static final Response.Status FALLBACK_STATUS = Response.Status.BAD_REQUEST;
    @VisibleForTesting
    static final Map<Class<?>, Response.Status> ERROR_TO_CODE = ImmutableMap.builder().put(ServiceException.class, (Object)FALLBACK_STATUS).put(InvalidArgumentException.class, (Object)Response.Status.BAD_REQUEST).put(PermissionException.class, (Object)Response.Status.FORBIDDEN).put(NotAuthenticatedException.class, (Object)Response.Status.UNAUTHORIZED).put(NoSuchEntityException.class, (Object)Response.Status.NOT_FOUND).put(EntityModificationException.class, (Object)Response.Status.CONFLICT).put(InvalidEntityStateException.class, (Object)Response.Status.CONFLICT).build();

    private ServiceExceptionHttpMapper() {
    }

    @Nonnull
    public static Response.Status getStatus(@Nonnull ServiceException serviceException) {
        for (Class<?> errorClass = serviceException.getClass(); errorClass != null; errorClass = errorClass.getSuperclass()) {
            if (!ERROR_TO_CODE.containsKey(errorClass)) continue;
            return ERROR_TO_CODE.get(errorClass);
        }
        return FALLBACK_STATUS;
    }
}

