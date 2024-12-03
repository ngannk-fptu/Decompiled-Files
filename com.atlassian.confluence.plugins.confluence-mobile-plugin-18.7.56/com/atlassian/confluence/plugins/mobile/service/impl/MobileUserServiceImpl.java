/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.core.HeartbeatManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.mobile.dto.UserDto;
import com.atlassian.confluence.plugins.mobile.helper.EditingHelper;
import com.atlassian.confluence.plugins.mobile.service.MobileUserService;
import com.atlassian.confluence.plugins.mobile.service.factory.PersonFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MobileUserServiceImpl
implements MobileUserService {
    private final PersonFactory personFactory;
    private final HeartbeatManager heartbeatManager;
    private final EditingHelper editingHelper;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;

    @Autowired
    public MobileUserServiceImpl(PersonFactory personFactory, @ComponentImport HeartbeatManager heartbeatManager, EditingHelper editingHelper, @Qualifier(value="pageManager") PageManager pageManager, @ComponentImport PermissionManager permissionManager) {
        this.personFactory = Objects.requireNonNull(personFactory);
        this.heartbeatManager = Objects.requireNonNull(heartbeatManager);
        this.editingHelper = Objects.requireNonNull(editingHelper);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    @Override
    public UserDto getCurrentUser() {
        return this.personFactory.forUserDto(AuthenticatedUserThreadLocal.get());
    }

    @Override
    public List<Person> getConcurrentEditingUser(Long contentId) {
        AbstractPage abstractPage = this.pageManager.getAbstractPage(contentId.longValue());
        if (abstractPage == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)abstractPage)) {
            throw new NotFoundException("Cannot find page/blogpost with id: " + contentId);
        }
        if (this.editingHelper.getEditingMode() != EditingHelper.EditingMode.COLLAB_EDITING) {
            throw new ServiceException("Collaboration editing is disabled");
        }
        List users = this.heartbeatManager.getUsersForActivity(contentId + abstractPage.getType());
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(u -> this.personFactory.forUser(u.getName())).collect(Collectors.toList());
    }
}

