/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.page.PageProvider;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.content.service.space.SpaceProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.user.User;
import java.util.List;

public interface PageService {
    public MovePageCommand newMovePageCommand(PageLocator var1, PageLocator var2, String var3);

    public MovePageCommand newMovePageCommand(PageLocator var1, SpaceLocator var2);

    public PageLocator getIdPageLocator(long var1);

    public PageLocator getTitleAndSpaceKeyPageLocator(String var1, String var2);

    public PageLocator getPageVersionLocator(long var1, int var3);

    public ServiceCommand newSetPageOrderCommand(PageLocator var1, List<Long> var2);

    public ServiceCommand newRevertPageOrderCommand(PageLocator var1);

    public ServiceCommand newDeletePageCommand(PageLocator var1);

    public ServiceCommand newRemovePageVersionCommand(PageLocator var1);

    public ServiceCommand newRevertPageCommand(PageLocator var1, int var2, String var3, boolean var4);

    public ServiceCommand newCreatePageCommand(PageProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, Page var4, User var5, boolean var6);

    public ServiceCommand newCreatePageCommand(PageProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, ContentEntityObject var4, User var5, boolean var6);

    public ServiceCommand newCreatePageCommand(PageProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, Draft var4, User var5, boolean var6);

    public ServiceCommand newCreatePageCommandFromExisting(PageProvider var1, ContentPermissionProvider var2, Draft var3, User var4, boolean var5, SpaceProvider var6);

    public ServiceCommand newCreatePageCommandFromExisting(PageProvider var1, ContentPermissionProvider var2, ContentEntityObject var3, User var4, boolean var5, SpaceProvider var6);

    public ServiceCommand newCreatePageCommandFromExisting(PageProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, ContentEntityObject var4, User var5, boolean var6, SpaceProvider var7);

    public ServiceCommand newCreatePageCommandFromExisting(PageProvider var1, ContentPermissionProvider var2, CreateContextProvider var3, Page var4, User var5, boolean var6, SpaceProvider var7);
}

