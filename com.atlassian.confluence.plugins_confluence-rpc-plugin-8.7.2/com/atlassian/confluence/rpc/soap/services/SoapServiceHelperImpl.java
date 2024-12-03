/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.rpc.WebSudoRequiredException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.websudo.WebSudoManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.WebSudoRequiredException;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.websudo.WebSudoManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;

public class SoapServiceHelperImpl
implements SoapServiceHelper {
    protected SpaceManager spaceManager;
    protected PermissionManager permissionManager;
    protected PageManager pageManager;
    protected UserAccessor userAccessor;
    protected ContentEntityManager contentEntityManager;
    protected WebSudoManager webSudoManager;
    public static final String __PARANAMER_DATA = "assertCanAdminister com.atlassian.confluence.spaces.Space space \nassertCanCreateBlogPost com.atlassian.confluence.spaces.Space space \nassertCanCreatePage com.atlassian.confluence.spaces.Space space \nassertCanExport com.atlassian.confluence.spaces.Space space \nassertCanModify com.atlassian.confluence.pages.AbstractPage page \nassertCanModifyObject java.lang.Object,java.lang.String obj,typeDescription \nassertCanRemove com.atlassian.confluence.pages.AbstractPage page \nassertCanView com.atlassian.confluence.pages.AbstractPage page \nassertCanView com.atlassian.confluence.spaces.Space space \nretrieveAbstractPage long abstractPageId \nretrieveContent long contentId \nretrievePage java.lang.String,java.lang.String spaceKey,pageTitle \nretrieveSpace java.lang.String spaceKey \nretrieveUser java.lang.String username \nsetContentEntityManager com.atlassian.confluence.core.ContentEntityManager contentEntityManager \nsetPageManager com.atlassian.confluence.pages.PageManager pageManager \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetSpaceManager com.atlassian.confluence.spaces.SpaceManager spaceManager \nsetUserAccessor com.atlassian.confluence.user.UserAccessor userAccessor \nsetWebSudoManager com.atlassian.confluence.security.websudo.WebSudoManager webSudoManager \n";

    public void setWebSudoManager(WebSudoManager webSudoManager) {
        this.webSudoManager = webSudoManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public ContentEntityObject retrieveContent(long contentId) throws RemoteException {
        ContentEntityObject content;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)(content = this.contentEntityManager.getById(contentId)))) {
            throw new RemoteException("You're not allowed to view that content, or it does not exist.");
        }
        return content;
    }

    @Override
    public Space retrieveSpace(String spaceKey) throws RemoteException {
        Space space;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)(space = this.spaceManager.getSpace(spaceKey)))) {
            throw new RemoteException("You're not allowed to view that space, or it does not exist.");
        }
        return space;
    }

    @Override
    public AbstractPage retrieveAbstractPage(long abstractPageId) throws RemoteException {
        AbstractPage page = this.pageManager.getAbstractPage(abstractPageId);
        this.assertCanView(page);
        return page;
    }

    @Override
    public Page retrievePage(String spaceKey, String pageTitle) throws RemoteException {
        Page page = this.pageManager.getPage(spaceKey, pageTitle);
        this.assertCanView((AbstractPage)page);
        return page;
    }

    public ConfluenceUser retrieveUser(String username) throws RemoteException {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            throw new RemoteException("The user '" + username + "' does not exist.");
        }
        return user;
    }

    @Override
    public void assertCanView(AbstractPage page) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (page != null) {
            page = page.getLatestVersion();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page)) {
            throw new RemoteException("You're not allowed to view that page, or it does not exist.");
        }
    }

    @Override
    public void assertCanView(Space space) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)space)) {
            throw new NotPermittedException("Space does not exist, or you do not have permission to view it.");
        }
    }

    @Override
    public void assertCanModifyObject(Object obj, String typeDescription) throws NotPermittedException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, obj)) {
            throw new NotPermittedException("You do not have permission to edit " + typeDescription);
        }
    }

    @Override
    public void assertCanCreatePage(Space space) throws RemoteException {
        this.assertCanCreate(space, Page.class, "pages");
    }

    @Override
    public void assertCanCreateBlogPost(Space space) throws RemoteException {
        this.assertCanCreate(space, BlogPost.class, "blog posts");
    }

    private void assertCanCreate(Space space, Class typeToCreate, String typeDescription) throws RemoteException {
        this.assertCanView(space);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasCreatePermission((User)user, (Object)space, typeToCreate)) {
            throw new NotPermittedException("You do not have permission to create " + typeDescription + " in this space.");
        }
    }

    @Override
    public void assertCanModify(AbstractPage page) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)page)) {
            throw new NotPermittedException("You do not have permission to edit this page");
        }
    }

    @Override
    public void assertCanRemove(AbstractPage page) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.REMOVE, (Object)page)) {
            throw new NotPermittedException("You do not have permission to remove this page");
        }
    }

    @Override
    public void assertCanAdminister() throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            throw new NotPermittedException("You don't have the 'Confluence Administrator' permission.");
        }
        this.assertHasValidWebSudoSession();
    }

    @Override
    public void assertHasValidWebSudoSession() throws RemoteException {
        if (!this.webSudoManager.isEnabled()) {
            return;
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        String servletPath = (String)request.getAttribute("javax.servlet.forward.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        if (servletPath.startsWith("/rpc/soap-axis") || servletPath.startsWith("/rpc/xmlrpc")) {
            return;
        }
        HttpSession session = request.getSession(false);
        boolean hasWebSudoSession = this.webSudoManager.hasValidSession(session);
        if (!hasWebSudoSession) {
            throw new WebSudoRequiredException();
        }
        HttpServletResponse response = ServletActionContext.getResponse();
        this.webSudoManager.startSession(request, response);
    }

    @Override
    public void assertCanExport(Space space) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.hasPermission((User)user, Permission.EXPORT, space)) {
            throw new NotPermittedException("You don't have permission to export the space: " + space.getKey());
        }
    }

    @Override
    public void assertCanAdminister(Space space) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.hasPermission((User)user, Permission.ADMINISTER, space)) {
            throw new NotPermittedException("You don't have permission to administer the space: " + space.getKey());
        }
    }

    private boolean hasPermission(User user, Permission permission, Space space) {
        return this.permissionManager.hasPermission(user, permission, (Object)space);
    }
}

