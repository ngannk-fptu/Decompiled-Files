/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.internal.common.rest.util;

import com.atlassian.applinks.internal.common.rest.util.RestResponses;
import com.atlassian.applinks.internal.rest.model.RestError;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

public class RestEnumParser<E extends Enum<E>> {
    public static final String DEFAULT_MESSAGE_KEY = "applinks.rest.enum.notfound";
    private final I18nResolver i18nResolver;
    private final Response.Status errorStatus;
    private final String errorMessageKey;
    private final Class<E> enumType;

    public RestEnumParser(@Nonnull Class<E> enumType, @Nonnull I18nResolver i18nResolver, @Nonnull String errorMessageKey, @Nullable Response.Status errorStatus) {
        this.enumType = Objects.requireNonNull(enumType, "enumType");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.errorStatus = errorStatus != null ? errorStatus : Response.Status.BAD_REQUEST;
        this.errorMessageKey = Objects.requireNonNull(errorMessageKey, "errorMessageKey");
    }

    public RestEnumParser(@Nonnull Class<E> enumType, @Nonnull I18nResolver i18nResolver, @Nonnull String errorMessageKey) {
        this(enumType, i18nResolver, errorMessageKey, null);
    }

    public RestEnumParser(@Nonnull Class<E> enumType, @Nonnull I18nResolver i18nResolver) {
        this(enumType, i18nResolver, DEFAULT_MESSAGE_KEY);
    }

    @Nonnull
    public E parseEnumParameter(@Nonnull String name, @Nonnull String context) {
        Objects.requireNonNull(name, "value");
        try {
            return Enum.valueOf(this.enumType, name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new WebApplicationException(this.badEnumParam(name, context));
        }
    }

    @Nonnull
    public E parseEnumParameter(@Nullable String name, @Nonnull E defaultValue, @Nonnull String context) {
        return StringUtils.isNotEmpty((CharSequence)name) ? this.parseEnumParameter(name, context) : defaultValue;
    }

    private Response badEnumParam(String name, String context) {
        return RestResponses.error(this.errorStatus, new RestError(context, this.i18nResolver.getText(this.errorMessageKey, new Serializable[]{name, context}), null));
    }
}

