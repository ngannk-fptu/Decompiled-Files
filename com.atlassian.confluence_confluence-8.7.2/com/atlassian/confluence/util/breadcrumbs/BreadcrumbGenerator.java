/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.Action
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.opensymphony.xwork2.Action;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface BreadcrumbGenerator {
    public List<Breadcrumb> getFilteredBreadcrumbTrail(ConfluenceActionSupport var1, HttpServletRequest var2);

    public List<Breadcrumb> getFilteredBreadcrumbTrail(Space var1, Breadcrumb var2);

    public Breadcrumb getContentActionBreadcrumb(Action var1, Space var2, AbstractPage var3, DisplayableLabel var4);

    public Breadcrumb getContentDetailActionBreadcrumb(Action var1, Space var2, AbstractPage var3);

    public Breadcrumb getContentBreadcrumb(Space var1, AbstractPage var2);

    public Breadcrumb getContentCollectorBreadcrumb(Space var1, Class var2);

    public Breadcrumb getSpaceAdminBreadcrumb(Action var1, Space var2);

    public Breadcrumb getSpaceOperationsBreadcrumb(Space var1);

    public Breadcrumb getAdvancedBreadcrumb(Space var1);

    public Breadcrumb getBlogCollectorBreadcrumb(Space var1);
}

