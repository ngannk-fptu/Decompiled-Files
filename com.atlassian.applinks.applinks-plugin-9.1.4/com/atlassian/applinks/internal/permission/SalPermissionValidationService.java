/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.permission;

import com.atlassian.applinks.core.ElevatedPermissionsService;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NotAuthenticatedException;
import com.atlassian.applinks.internal.common.exception.PermissionException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

public class SalPermissionValidationService
implements PermissionValidationService {
    private static final String ADMIN_I18N_KEY = "applinks.service.permission.admin";
    private static final String SYSADMIN_I18N_KEY = "applinks.service.permission.sysadmin";
    private static final I18nKey DEFAULT_OPERATION_KEY = I18nKey.newI18nKey("applinks.service.error.access.defaultoperation", new Serializable[0]);
    private final I18nResolver i18nResolver;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final UserManager userManager;
    private final ElevatedPermissionsService elevatedPermissions;

    @Autowired
    public SalPermissionValidationService(I18nResolver i18nResolver, ServiceExceptionFactory serviceExceptionFactory, UserManager userManager, ElevatedPermissionsService elevatedPermissions) {
        this.i18nResolver = i18nResolver;
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.userManager = userManager;
        this.elevatedPermissions = elevatedPermissions;
    }

    @Override
    public void validateAuthenticated() throws NotAuthenticatedException {
        this.validateAuthenticated(DEFAULT_OPERATION_KEY);
    }

    @Override
    public void validateAuthenticated(@Nonnull I18nKey operationI18n) throws NotAuthenticatedException {
        if (!this.elevatedPermissions.isElevatedTo(PermissionLevel.USER)) {
            this.validateAuthenticated(this.userManager.getRemoteUserKey(), operationI18n);
        }
    }

    @Override
    public void validateAuthenticated(@Nullable UserKey user) throws NotAuthenticatedException {
        this.validateAuthenticated(user, DEFAULT_OPERATION_KEY);
    }

    @Override
    public void validateAuthenticated(@Nullable UserKey user, @Nonnull I18nKey operationI18n) throws NotAuthenticatedException {
        Objects.requireNonNull(operationI18n, "operationI18n");
        if (user == null) {
            throw this.serviceExceptionFactory.create(NotAuthenticatedException.class, new Serializable[]{this.i18nResolver.getText((Message)operationI18n)});
        }
    }

    @Override
    public void validateAdmin() throws NoAccessException {
        this.validateAdmin(DEFAULT_OPERATION_KEY);
    }

    @Override
    public void validateAdmin(@Nonnull I18nKey operationI18n) throws NoAccessException {
        if (!this.elevatedPermissions.isElevatedTo(PermissionLevel.ADMIN)) {
            this.validateAdmin(this.userManager.getRemoteUserKey(), operationI18n);
        }
    }

    @Override
    public void validateAdmin(@Nullable UserKey user) throws NoAccessException {
        this.validateAdmin(user, DEFAULT_OPERATION_KEY);
    }

    @Override
    public void validateAdmin(@Nullable UserKey user, @Nonnull I18nKey operationI18n) throws NoAccessException {
        this.validateAuthenticated(user, operationI18n);
        if (!this.userManager.isAdmin(user)) {
            throw this.serviceExceptionFactory.create(PermissionException.class, new Serializable[]{this.i18nResolver.getText(ADMIN_I18N_KEY), this.i18nResolver.getText((Message)operationI18n)});
        }
    }

    @Override
    public void validateSysadmin() throws NoAccessException {
        this.validateSysadmin(DEFAULT_OPERATION_KEY);
    }

    @Override
    public void validateSysadmin(@Nonnull I18nKey operationI18n) throws NoAccessException {
        if (!this.elevatedPermissions.isElevatedTo(PermissionLevel.SYSADMIN)) {
            this.validateSysadmin(this.userManager.getRemoteUserKey(), operationI18n);
        }
    }

    @Override
    public void validateSysadmin(@Nullable UserKey user) throws NoAccessException {
        this.validateSysadmin(user, DEFAULT_OPERATION_KEY);
    }

    @Override
    public void validateSysadmin(@Nullable UserKey user, @Nonnull I18nKey operationI18n) throws NoAccessException {
        this.validateAuthenticated(user, operationI18n);
        if (!this.userManager.isSystemAdmin(user)) {
            throw this.serviceExceptionFactory.create(PermissionException.class, new Serializable[]{this.i18nResolver.getText(SYSADMIN_I18N_KEY), this.i18nResolver.getText((Message)operationI18n)});
        }
    }
}

