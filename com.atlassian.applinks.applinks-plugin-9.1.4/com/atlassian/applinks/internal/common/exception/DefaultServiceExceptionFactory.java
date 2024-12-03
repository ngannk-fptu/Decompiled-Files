/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultServiceExceptionFactory
implements ServiceExceptionFactory {
    @VisibleForTesting
    static final String DEFAULT_MESSAGE_FIELD_NAME = "DEFAULT_MESSAGE";
    private final I18nResolver i18nResolver;

    @Autowired
    public DefaultServiceExceptionFactory(@Nonnull I18nResolver i18nResolver) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
    }

    @Override
    @Nonnull
    public <E extends ServiceException> E raise(@Nonnull Class<E> exceptionClass, Serializable ... args) throws E {
        E exc = this.create(exceptionClass, args);
        ((Throwable)exc).fillInStackTrace();
        throw exc;
    }

    @Override
    @Nonnull
    public <E extends ServiceException> E raise(@Nonnull Class<E> exceptionClass, @Nonnull I18nKey i18nKey) throws E {
        E exc = this.create(exceptionClass, i18nKey);
        ((Throwable)exc).fillInStackTrace();
        throw exc;
    }

    @Override
    @Nonnull
    public <E extends ServiceException> E raise(@Nonnull Class<E> exceptionClass, @Nonnull I18nKey i18nKey, @Nonnull Throwable cause) throws E {
        E exc = this.create(exceptionClass, i18nKey, cause);
        ((Throwable)exc).fillInStackTrace();
        throw exc;
    }

    @Override
    @Nonnull
    public <E extends ServiceException> E create(@Nonnull Class<E> exceptionClass, Serializable ... args) {
        Objects.requireNonNull(exceptionClass, "exceptionClass");
        Objects.requireNonNull(args, "args");
        try {
            String defaultMessageKey = (String)exceptionClass.getField(DEFAULT_MESSAGE_FIELD_NAME).get(null);
            Preconditions.checkState((boolean)StringUtils.isNotEmpty((CharSequence)defaultMessageKey), (Object)"Default message key must not be empty");
            return this.create(exceptionClass, I18nKey.newI18nKey(defaultMessageKey, args));
        }
        catch (NoSuchFieldException e) {
            return this.create(exceptionClass, I18nKey.newI18nKey("applinks.service.error.default.message.not.specified", new Serializable[]{exceptionClass.getName()}));
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to instantiate " + exceptionClass.getName(), e);
        }
    }

    @Override
    @Nonnull
    public <E extends ServiceException> E create(@Nonnull Class<E> exceptionClass, @Nonnull I18nKey i18nKey) {
        Objects.requireNonNull(exceptionClass, "exceptionClass");
        Objects.requireNonNull(i18nKey, "i18nKey");
        try {
            return (E)((ServiceException)exceptionClass.getConstructor(String.class).newInstance(this.i18nResolver.getText((Message)i18nKey)));
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to instantiate " + exceptionClass.getName(), e);
        }
    }

    @Override
    @Nonnull
    public <E extends ServiceException> E create(@Nonnull Class<E> exceptionClass, @Nonnull I18nKey i18nKey, @Nonnull Throwable cause) {
        Objects.requireNonNull(exceptionClass, "exceptionClass");
        Objects.requireNonNull(i18nKey, "i18nKey");
        Objects.requireNonNull(cause, "cause");
        try {
            return (E)((ServiceException)exceptionClass.getConstructor(String.class, Throwable.class).newInstance(this.i18nResolver.getText((Message)i18nKey), cause));
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to instantiate " + exceptionClass.getName(), e);
        }
    }
}

