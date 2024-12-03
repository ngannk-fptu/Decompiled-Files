/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.opensymphony.xwork2.Action
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.admin.actions.LongRunningTaskMonitorAction;
import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.dashboard.actions.DashboardAction;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractBlogPostsAction;
import com.atlassian.confluence.pages.actions.AbstractCreatePageAction;
import com.atlassian.confluence.pages.actions.PageNotFoundAction;
import com.atlassian.confluence.pages.actions.PageNotPermittedAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.user.actions.AbstractGroupAction;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.user.actions.PeopleDirectoryAction;
import com.atlassian.confluence.user.actions.SearchUsersAction;
import com.atlassian.confluence.user.actions.ViewMembersOfGroupAction;
import com.atlassian.confluence.util.breadcrumbs.AdminActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.BrowseSpaceBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.ContentActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.EmptyBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.GroupAdminActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.LongRunningTaskBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.MailServersActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PageTemplatesActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PeopleBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceAdminActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.UserAdminActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogCollectorBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogPostBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.confluence.util.breadcrumbs.spaceia.PageBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.PagesCollectorBreadcrumb;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.opensymphony.xwork2.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import javax.servlet.http.HttpServletRequest;

public class DefaultBreadcrumbGenerator
implements BreadcrumbGenerator {
    private final ThemeManager themeManager;

    public DefaultBreadcrumbGenerator(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    @Override
    public List<Breadcrumb> getFilteredBreadcrumbTrail(ConfluenceActionSupport action, HttpServletRequest request) {
        Space space = this.getSpace(action);
        return this.getFilteredBreadcrumbTrail(space, this.getBreadcrumb(action, request, space));
    }

    @Override
    public List<Breadcrumb> getFilteredBreadcrumbTrail(Space space, Breadcrumb breadcrumb) {
        if (breadcrumb == null) {
            return Collections.emptyList();
        }
        ArrayList trail = breadcrumb.getBreadcrumbsTrail();
        if (this.isSpaceIA(space)) {
            trail = this.replaceLegacyIABreadcrumbs(trail);
        }
        trail = this.filterTrailingBreadcrumb(trail);
        trail = Lists.newArrayList((Iterable)Iterables.filter(trail, (Predicate)Predicates.not((Predicate)Predicates.instanceOf(DashboardBreadcrumb.class))));
        return trail;
    }

    private List<Breadcrumb> replaceLegacyIABreadcrumbs(List<Breadcrumb> trail) {
        trail = Lists.reverse(trail);
        ListIterator itr = trail.listIterator();
        while (itr.hasNext()) {
            Breadcrumb breadcrumb = (Breadcrumb)itr.next();
            Breadcrumb replacement = this.getSpaceIABreadcrumb(breadcrumb);
            if (replacement == null) continue;
            trail = trail.subList(0, itr.previousIndex());
            trail.addAll(Lists.reverse(replacement.getBreadcrumbsTrail()));
            break;
        }
        if (trail.size() == 1 && trail.get(0) instanceof SpaceBreadcrumb) {
            trail = new ArrayList<EmptyBreadcrumb>();
            trail.add(new EmptyBreadcrumb());
        }
        return Lists.reverse((List)trail);
    }

    private Breadcrumb getSpaceIABreadcrumb(Breadcrumb breadcrumb) {
        if (breadcrumb instanceof com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb) {
            return new PageBreadcrumb(((com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb)breadcrumb).getPage());
        }
        if (breadcrumb instanceof com.atlassian.confluence.util.breadcrumbs.BlogPostBreadcrumb) {
            return new BlogPostBreadcrumb(((com.atlassian.confluence.util.breadcrumbs.BlogPostBreadcrumb)breadcrumb).getBlogPost());
        }
        if (breadcrumb instanceof SpaceAdminActionBreadcrumb) {
            return new EmptyBreadcrumb();
        }
        if (breadcrumb instanceof BrowseSpaceBreadcrumb) {
            return new EmptyBreadcrumb();
        }
        return null;
    }

    private List<Breadcrumb> filterTrailingBreadcrumb(List<Breadcrumb> trail) {
        int size = trail.size();
        if (size > 0 && trail.get(size - 1).filterTrailingBreadcrumb()) {
            trail.remove(size - 1);
        }
        return trail;
    }

    private Breadcrumb getBreadcrumb(ConfluenceActionSupport action, HttpServletRequest request, Space space) {
        if (action instanceof BreadcrumbAware) {
            return ((BreadcrumbAware)((Object)action)).getBreadcrumb();
        }
        String urlPath = this.getUrlPath(request);
        if (action instanceof AbstractUsersAction) {
            return new UserAdminActionBreadcrumb(action, (User)((AbstractUsersAction)action).getUser());
        }
        if (action instanceof SearchUsersAction) {
            return new UserAdminActionBreadcrumb(action, null);
        }
        if (this.isInPackage((Action)action, "com.atlassian.confluence.admin.actions.mail")) {
            return new MailServersActionBreadcrumb(action);
        }
        if (urlPath.startsWith("/admin")) {
            if (action instanceof AbstractGroupAction) {
                return new GroupAdminActionBreadcrumb(action, ((AbstractGroupAction)action).getGroup());
            }
            if (action instanceof ViewMembersOfGroupAction) {
                return new GroupAdminActionBreadcrumb(action, null);
            }
            return new AdminActionBreadcrumb(action);
        }
        if (urlPath.equals("/pages/templates")) {
            return new PageTemplatesActionBreadcrumb(action, space);
        }
        if (action instanceof DashboardAction) {
            return DashboardBreadcrumb.getInstance();
        }
        if (action instanceof PeopleDirectoryAction) {
            return PeopleBreadcrumb.getInstance();
        }
        if (action instanceof LongRunningTaskMonitorAction) {
            return new LongRunningTaskBreadcrumb((Action)action);
        }
        if (this.isInPackage((Action)action, "com.atlassian.confluence.admin")) {
            return new AdminActionBreadcrumb(action);
        }
        if (action instanceof AbstractSpaceAdminAction || action instanceof SpaceAdministrative) {
            return this.getSpaceAdminBreadcrumb((Action)action, space);
        }
        if (action instanceof AbstractSpaceAction && !(action instanceof AbstractBlogPostsAction)) {
            return this.getSpaceOperationsBreadcrumb(space);
        }
        if (action instanceof LookAndFeel) {
            return new AdminActionBreadcrumb(action);
        }
        if (action instanceof PageNotFoundAction) {
            return space != null ? new SpaceBreadcrumb(space) : new EmptyBreadcrumb();
        }
        if (action instanceof ContentDetailAction) {
            return this.getContentDetailActionBreadcrumb((Action)action, space, this.getPage(action));
        }
        if (action instanceof AbstractCreatePageAction) {
            return this.getContentActionBreadcrumb((Action)action, space, ((AbstractCreatePageAction)action).getFromPage(), this.getLabel(action));
        }
        if (action instanceof PageNotPermittedAction) {
            return new EmptyBreadcrumb();
        }
        return this.getContentActionBreadcrumb((Action)action, space, this.getPage(action), this.getLabel(action));
    }

    private String getUrlPath(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String path = request.getRequestURI();
        path = path.substring(request.getContextPath().length());
        return path.substring(0, path.lastIndexOf(47));
    }

    private boolean isInPackage(Action action, String packagePrefix) {
        return action.getClass().getName().startsWith(packagePrefix);
    }

    private DisplayableLabel getLabel(ConfluenceActionSupport action) {
        return new GlobalHelper(action).getLabel();
    }

    private Space getSpace(ConfluenceActionSupport action) {
        return new GlobalHelper(action).getSpace();
    }

    private AbstractPage getPage(ConfluenceActionSupport action) {
        AbstractPage page = new GlobalHelper(action).getPage();
        return page != null ? page.getLatestVersion() : null;
    }

    private boolean isSpaceIA(Space space) {
        Theme theme = space != null ? this.themeManager.getSpaceTheme(space.getKey()) : null;
        return theme != null && theme.hasSpaceSideBar();
    }

    @Override
    public Breadcrumb getContentActionBreadcrumb(Action action, Space space, AbstractPage page, DisplayableLabel label) {
        if (this.isSpaceIA(space)) {
            return DefaultBreadcrumbGenerator.getActionSpaceIAContentBreadcrumb(action, page);
        }
        return new ContentActionBreadcrumb(action, space, page, label, this.getSpaceOperationsBreadcrumb(space));
    }

    @Override
    public Breadcrumb getContentBreadcrumb(Space space, AbstractPage page) {
        if (this.isSpaceIA(space)) {
            return DefaultBreadcrumbGenerator.getSpaceIAContentBreadcrumb(page);
        }
        return new ContentActionBreadcrumb((Action)null, space, page, null, this.getSpaceOperationsBreadcrumb(space)).getParent();
    }

    @Override
    public Breadcrumb getContentCollectorBreadcrumb(Space space, Class contentClass) {
        if (this.isSpaceIA(space)) {
            if (contentClass == Page.class) {
                return new PagesCollectorBreadcrumb(space);
            }
            if (contentClass == BlogPost.class) {
                return new BlogCollectorBreadcrumb(space);
            }
        }
        return null;
    }

    private static Breadcrumb getSpaceIAContentBreadcrumb(AbstractPage page) {
        if (page instanceof Page) {
            return new PageBreadcrumb((Page)page);
        }
        if (page instanceof BlogPost) {
            return new BlogPostBreadcrumb((BlogPost)page);
        }
        return null;
    }

    private static Breadcrumb getActionSpaceIAContentBreadcrumb(Action action, AbstractPage page) {
        if (page instanceof BlogPost && action instanceof ConfluenceActionSupport) {
            return new BlogPostBreadcrumb((BlogPost)page, ((ConfluenceActionSupport)action).getDateFormatter());
        }
        return DefaultBreadcrumbGenerator.getSpaceIAContentBreadcrumb(page);
    }

    @Override
    public Breadcrumb getSpaceAdminBreadcrumb(Action action, Space space) {
        return this.isSpaceIA(space) ? null : new SpaceAdminActionBreadcrumb(action, space);
    }

    @Override
    public Breadcrumb getSpaceOperationsBreadcrumb(Space space) {
        if (space == null) {
            return null;
        }
        return this.isSpaceIA(space) ? null : new BrowseSpaceBreadcrumb(space);
    }

    @Override
    public Breadcrumb getAdvancedBreadcrumb(Space space) {
        return this.isSpaceIA(space) ? new EmptyBreadcrumb() : new BrowseSpaceBreadcrumb(space);
    }

    @Override
    public Breadcrumb getBlogCollectorBreadcrumb(Space space) {
        if (!this.isSpaceIA(space)) {
            return new SpaceBreadcrumb(space);
        }
        return new BlogCollectorBreadcrumb(space);
    }

    @Override
    public Breadcrumb getContentDetailActionBreadcrumb(Action action, Space space, AbstractPage page) {
        Breadcrumb breadcrumb = this.getContentBreadcrumb(space, page);
        breadcrumb.setFilterTrailingBreadcrumb(false);
        return breadcrumb;
    }
}

