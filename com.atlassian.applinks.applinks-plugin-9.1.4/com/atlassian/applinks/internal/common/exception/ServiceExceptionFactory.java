/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import java.io.Serializable;
import javax.annotation.Nonnull;

@Unrestricted(value="Internal component with no security implications")
public interface ServiceExceptionFactory {
    @Nonnull
    public <E extends ServiceException> E raise(@Nonnull Class<E> var1, Serializable ... var2) throws E;

    @Nonnull
    public <E extends ServiceException> E raise(@Nonnull Class<E> var1, @Nonnull I18nKey var2) throws E;

    @Nonnull
    public <E extends ServiceException> E raise(@Nonnull Class<E> var1, @Nonnull I18nKey var2, @Nonnull Throwable var3) throws E;

    @Nonnull
    public <E extends ServiceException> E create(@Nonnull Class<E> var1, Serializable ... var2);

    @Nonnull
    public <E extends ServiceException> E create(@Nonnull Class<E> var1, @Nonnull I18nKey var2);

    @Nonnull
    public <E extends ServiceException> E create(@Nonnull Class<E> var1, @Nonnull I18nKey var2, @Nonnull Throwable var3);
}

