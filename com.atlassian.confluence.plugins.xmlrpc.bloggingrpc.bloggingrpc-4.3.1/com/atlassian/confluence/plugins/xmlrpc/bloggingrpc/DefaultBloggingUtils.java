/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.rpc.AuthenticationFailedException
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.xmlrpc.bloggingrpc;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.BloggingUtils;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBloggingUtils
implements BloggingUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBloggingUtils.class);
    private final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final XhtmlContent xhtmlContent;

    public DefaultBloggingUtils(UserAccessor userAccessor, SpaceManager spaceManager, SpacePermissionManager spacePermissionManager, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, XhtmlContent xhtmlContent) {
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.xhtmlContent = xhtmlContent;
    }

    @Override
    public ConfluenceUser authenticateUser(String username, String password) throws AuthenticationFailedException {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            throw new AuthenticationFailedException(this.getText("error.validation.authentication"));
        }
        boolean authenticated = this.userAccessor.authenticate(username, password);
        if (authenticated) {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)user);
            return user;
        }
        throw new AuthenticationFailedException(this.getText("error.validation.authentication"));
    }

    @Override
    public List<Space> getBlogs(User user) {
        ArrayList<Space> blogs = new ArrayList<Space>();
        if (user != null) {
            List allSpaces = this.spaceManager.getAllSpaces();
            blogs.addAll(allSpaces.stream().filter(space -> this.spacePermissionManager.hasPermission("EDITBLOG", space, user)).collect(Collectors.toList()));
            Collections.sort(blogs, (left, right) -> left.getName().compareToIgnoreCase(right.getName()));
        }
        return blogs;
    }

    @Override
    public String getText(String s) {
        return this.getI18nBean().getText(s);
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    @Override
    public String getText(String s, String s1) {
        return this.getText(s, Arrays.asList(s1));
    }

    @Override
    public String getText(String s, Object[] objects) {
        return this.getI18nBean().getText(s, objects);
    }

    public String getText(String s, List<?> list) {
        return this.getI18nBean().getText(s, list);
    }

    @Override
    public String convertStorageFormatToView(BlogPost blogPost) {
        try {
            return this.xhtmlContent.convertStorageToView(blogPost.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)blogPost.toPageContext()));
        }
        catch (XhtmlException xhtmlError) {
            LOG.error(String.format("Unable to convert content of %s to view. There's a problem with the markup", blogPost.getIdAsString()), (Throwable)xhtmlError);
        }
        catch (XMLStreamException xmlError) {
            LOG.error(String.format("Unable to convert content of %s to view. Unable to stream storage markup", blogPost.getIdAsString()), (Throwable)xmlError);
        }
        return blogPost.getBodyAsString();
    }
}

