/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.permission;

import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NotAuthenticatedException;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.sal.api.user.UserKey;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PermissionValidationService {
    public void validateAuthenticated() throws NotAuthenticatedException;

    public void validateAuthenticated(@Nonnull I18nKey var1) throws NotAuthenticatedException;

    public void validateAuthenticated(@Nullable UserKey var1) throws NotAuthenticatedException;

    public void validateAuthenticated(@Nullable UserKey var1, @Nonnull I18nKey var2) throws NotAuthenticatedException;

    public void validateAdmin() throws NoAccessException;

    public void validateAdmin(@Nonnull I18nKey var1) throws NoAccessException;

    public void validateAdmin(@Nullable UserKey var1) throws NoAccessException;

    public void validateAdmin(@Nullable UserKey var1, @Nonnull I18nKey var2) throws NoAccessException;

    public void validateSysadmin() throws NoAccessException;

    public void validateSysadmin(@Nonnull I18nKey var1) throws NoAccessException;

    public void validateSysadmin(@Nullable UserKey var1) throws NoAccessException;

    public void validateSysadmin(@Nullable UserKey var1, @Nonnull I18nKey var2) throws NoAccessException;
}

