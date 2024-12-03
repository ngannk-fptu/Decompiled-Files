/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.actions.AbstractCreateAndEditPageAction
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.atlassian.confluence.themes.GlobalHelper
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.plugins.editorloader;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.actions.AbstractCreateAndEditPageAction;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb;
import com.atlassian.user.User;
import com.opensymphony.xwork2.Action;

public class EditorAction
extends AbstractCreateAndEditPageAction
implements SpaceAware,
BreadcrumbAware {
    protected BreadcrumbGenerator breadcrumbGenerator;
    private Space space;

    public String doDefault() throws Exception {
        this.setTitle(this.getPage().getTitle());
        return super.doDefault();
    }

    public String getWysiwygContent() {
        return "";
    }

    public Space getSpace() {
        return this.space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public boolean isSpaceRequired() {
        return true;
    }

    public String getMode() {
        return "richtext";
    }

    public boolean isPermitted() {
        return true;
    }

    public boolean isUserWatchingPage() {
        if (this.isAnonymousUser() || this.getPage() == null) {
            return false;
        }
        try {
            return this.notificationManager.isWatchingContent((User)this.getAuthenticatedUser(), (ContentEntityObject)this.getPage());
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean isUserWatchingSpace() {
        if (this.isAnonymousUser() || this.getPage() == null) {
            return false;
        }
        ContentTypeEnum typeEnum = ContentTypeEnum.getByRepresentation((String)this.getPage().getType());
        return this.notificationManager.getNotificationByUserAndSpaceAndType((User)this.getAuthenticatedUser(), this.getSpace(), typeEnum) != null;
    }

    public String getContentType() {
        if (this.getPage() == null) {
            return null;
        }
        return this.getPage().getType();
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public Breadcrumb getBreadcrumb() {
        Breadcrumb breadcrumb = this.breadcrumbGenerator.getContentActionBreadcrumb((Action)this, this.getSpace(), this.getPage(), new GlobalHelper((ConfluenceActionSupport)this).getLabel());
        breadcrumb.setCssClass("edited-page-title");
        breadcrumb.setFilterTrailingBreadcrumb(false);
        return new SpaceBreadcrumb(this.getSpace()).concatWith(breadcrumb);
    }

    @Internal
    public boolean startHeartbeatOnDoDefault() {
        return false;
    }
}

