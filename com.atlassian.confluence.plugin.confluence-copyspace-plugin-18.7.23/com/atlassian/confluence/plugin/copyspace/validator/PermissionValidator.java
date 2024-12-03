/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.confluence.plugin.copyspace.service.PermissionService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="permissionValidator")
@Order(value=5)
public class PermissionValidator
implements Consumer<CopySpaceRequest> {
    private final SpaceManager spaceManager;
    private final I18NBeanProvider i18NBeanProvider;
    private final PermissionService permissionService;

    @Autowired
    public PermissionValidator(@ComponentImport SpaceManager spaceManager, I18NBeanProvider i18NBeanProvider, PermissionService permissionService) {
        this.spaceManager = spaceManager;
        this.i18NBeanProvider = i18NBeanProvider;
        this.permissionService = permissionService;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        Space space;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionService.canInitiateSpaceCopy((User)user, space = this.spaceManager.getSpace(request.getOldKey()))) {
            throw new PermissionException(this.i18NBeanProvider.getI18NBean().getText("copyspace.validation.permission.violation"));
        }
    }
}

