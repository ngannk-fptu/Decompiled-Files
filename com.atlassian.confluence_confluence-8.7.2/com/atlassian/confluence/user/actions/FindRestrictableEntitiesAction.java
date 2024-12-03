/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PermittedUserFinder;
import com.atlassian.user.Entity;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class FindRestrictableEntitiesAction
extends ConfluenceActionSupport
implements Beanable,
SpaceAware,
PageAware {
    private PageManager pageManager;
    private List<PermittedUserFinder.SearchResult> bean;
    private String[] names;
    private String type;
    private Space space;
    private static final String GROUP = "group";
    private static final String USER = "user";
    private Page parentPage;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        PermittedUserFinder finder = new PermittedUserFinder(this.pageManager, this.permissionManager, this.spacePermissionManager, this.parentPage, this.space);
        this.bean = new ArrayList<PermittedUserFinder.SearchResult>();
        for (String name : this.names) {
            ConfluenceUser entity = null;
            if ((StringUtils.isBlank((CharSequence)this.type) || USER.equals(this.type)) && (entity = this.userAccessor.getUserByName(name)) != null) {
                this.bean.add(finder.makeResult((Entity)entity));
            }
            if ((entity != null || !StringUtils.isBlank((CharSequence)this.type)) && !GROUP.equals(this.type) || (entity = this.userAccessor.getGroup(name)) == null) continue;
            this.bean.add(finder.makeResult((Entity)entity));
        }
        return "success";
    }

    @Override
    public Object getBean() {
        return this.bean;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String[] names) {
        this.names = names;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public AbstractPage getPage() {
        return this.parentPage;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.parentPage = (Page)page;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public Space getSpace() {
        return this.space;
    }
}

