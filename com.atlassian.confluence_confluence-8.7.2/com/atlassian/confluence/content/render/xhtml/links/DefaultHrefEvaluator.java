/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.util.concurrent.Suppliers
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.concurrent.Suppliers;
import org.apache.commons.lang3.StringUtils;

public class DefaultHrefEvaluator
implements HrefEvaluator {
    private final ContextPathHolder contextPathHolder;
    private final Supplier<PermissionManager> permissionManagerReference;

    public DefaultHrefEvaluator(ContextPathHolder contextPathHolder, PermissionManager permissionManager) {
        this.contextPathHolder = contextPathHolder;
        this.permissionManagerReference = Suppliers.memoize((Object)permissionManager);
    }

    @Override
    public String createHref(ConversionContext context, Object destination, String anchor) {
        StringBuilder resultBuilder = new StringBuilder(this.contextPathHolder.getContextPath());
        if (destination instanceof Attachment) {
            resultBuilder.append(((Attachment)destination).getDownloadPath());
        } else if (destination instanceof Addressable) {
            this.handleAddressibleDestination(context, destination, anchor, resultBuilder);
        } else if (destination instanceof User) {
            resultBuilder.append(UserProfileLink.getLinkPath(((User)destination).getName()));
        } else {
            if (destination instanceof WebLink) {
                return ((WebLink)destination).getHref();
            }
            if (destination instanceof PageTemplate) {
                resultBuilder.setLength(0);
                if (context.getSpaceKey() != null) {
                    resultBuilder.append("#" + HtmlUtil.htmlEncode(context.getSpaceKey() + "-" + anchor));
                } else {
                    resultBuilder.append("#" + HtmlUtil.htmlEncode(anchor));
                }
            } else {
                throw new UnsupportedOperationException("The link destination is of an unexpected type: " + destination.getClass().getName());
            }
        }
        return resultBuilder.toString();
    }

    private void handleAddressibleDestination(ConversionContext context, Object destination, String anchor, StringBuilder resultBuilder) {
        boolean isPageOrBlogPost = destination instanceof Page || destination instanceof BlogPost;
        boolean isDiffOrEmail = context != null && context.isDiffOrEmail();
        boolean canViewPageOrBlogPost = true;
        if (isPageOrBlogPost) {
            if (isDiffOrEmail) {
                resultBuilder.append(GeneralUtil.getIdBasedPageUrl((AbstractPage)destination));
            } else {
                canViewPageOrBlogPost = ((PermissionManager)this.permissionManagerReference.get()).hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, destination);
                if (canViewPageOrBlogPost) {
                    resultBuilder.append(((Addressable)destination).getUrlPath());
                } else {
                    resultBuilder.append(GeneralUtil.getIdBasedPageUrl((AbstractPage)destination));
                }
            }
        } else {
            resultBuilder.append(((Addressable)destination).getUrlPath());
        }
        if (StringUtils.isNotBlank((CharSequence)anchor) && isPageOrBlogPost && !isDiffOrEmail && canViewPageOrBlogPost) {
            if (context != null && context.getPageContext() != null && destination.equals(context.getPageContext().getEntity())) {
                resultBuilder.setLength(0);
                resultBuilder.append("#").append(AbstractPageLink.generateAnchor(context.getPageContext(), anchor));
            } else {
                resultBuilder.append("#").append(AbstractPageLink.generateAnchor(new PageContext((ContentEntityObject)destination), anchor));
            }
        }
    }
}

