/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.Lists
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.util.I18NSupport;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.PeopleBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogCollectorBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.PageBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.PagesCollectorBreadcrumb;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.struts2.ServletActionContext;

public class GlobalHelper
implements ThemeHelper {
    private ConfluenceActionSupport action;
    private Space space;
    private AbstractPage abstractPage;
    private DisplayableLabel label;
    private SettingsManager settingsManager;
    private WikiStyleRenderer wikiStyleRenderer;
    private BreadcrumbGenerator breadcrumbGenerator;

    public GlobalHelper() {
    }

    public GlobalHelper(ConfluenceActionSupport action) {
        this.action = action;
    }

    @Override
    @HtmlSafe
    public String getText(String property) {
        if (this.action != null) {
            return this.action.getText(property);
        }
        return I18NSupport.getText(property);
    }

    @HtmlSafe
    public String getText(String property, Object[] args) {
        if (this.action != null) {
            return this.action.getText(property, args);
        }
        return I18NSupport.getText(property, args);
    }

    @Override
    public ConfluenceActionSupport getAction() {
        return this.action;
    }

    @Override
    public String getDomainName() {
        return this.getSettingsManager().getGlobalSettings().getBaseUrl();
    }

    public String getSiteTitle() {
        String title = this.action.getGlobalSettings().getSiteTitle();
        if (title != null) {
            return title;
        }
        return "Confluence";
    }

    public Space getSpace() {
        if (this.space == null) {
            if (this.action instanceof Spaced) {
                Spaced spaced = (Spaced)((Object)this.action);
                this.space = spaced.getSpace();
            } else if (this.getPage() != null) {
                this.space = this.getPage().getSpace();
                if (this.space == null && !this.getPage().isLatestVersion()) {
                    this.space = this.getPage().getOriginalVersionPage().getSpace();
                }
            }
        }
        return this.space;
    }

    @Override
    public String getSpaceKey() {
        if (this.getSpace() != null) {
            return this.getSpace().getKey();
        }
        return null;
    }

    @Override
    public String getSpaceName() {
        if (this.getSpace() != null) {
            return this.getSpace().getName();
        }
        return null;
    }

    public String getSpaceType() {
        if (this.getSpace() != null) {
            return this.getSpace().getSpaceType().toString();
        }
        return null;
    }

    public AbstractPage getPage() {
        if (this.abstractPage == null && this.action instanceof PageAware) {
            PageAware pageAware = (PageAware)((Object)this.action);
            this.abstractPage = pageAware.getPage();
        }
        return this.abstractPage;
    }

    public WikiStyleRenderer getWikiStyleRenderer() {
        if (this.wikiStyleRenderer == null) {
            this.wikiStyleRenderer = (WikiStyleRenderer)ContainerManager.getInstance().getContainerContext().getComponent((Object)"wikiStyleRenderer");
        }
        return this.wikiStyleRenderer;
    }

    private SettingsManager getSettingsManager() {
        if (this.settingsManager == null) {
            this.settingsManager = (SettingsManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"settingsManager");
        }
        return this.settingsManager;
    }

    private BreadcrumbGenerator getBreadcrumbGenerator() {
        if (this.breadcrumbGenerator == null) {
            this.breadcrumbGenerator = (BreadcrumbGenerator)ContainerManager.getInstance().getContainerContext().getComponent((Object)"breadcrumbGenerator");
        }
        return this.breadcrumbGenerator;
    }

    @Override
    @HtmlSafe
    public String renderConfluenceMacro(String wikiCall) {
        PageContext pageContext = this.getPage() != null ? new PageContext(this.getPage()) : (this.getSpace() != null ? new PageContext(this.getSpaceKey()) : new PageContext());
        return this.getWikiStyleRenderer().convertWikiToXHtml((RenderContext)pageContext, wikiCall);
    }

    @HtmlSafe
    public String renderConfluenceMacro(String messageFormatString, String ... args) {
        return this.renderConfluenceMacro(String.format(messageFormatString, args));
    }

    public DisplayableLabel getLabel() {
        WebInterfaceContext webInterfaceContext = this.action.getWebInterfaceContext();
        if (webInterfaceContext != null) {
            return webInterfaceContext.getLabel();
        }
        return null;
    }

    public List<Breadcrumb> getBreadcrumbs() {
        try {
            return this.getBreadcrumbGenerator().getFilteredBreadcrumbTrail(this.action, ServletActionContext.getRequest());
        }
        catch (RuntimeException exception) {
            if (ConfluenceSystemProperties.isDevMode()) {
                throw exception;
            }
            return new ArrayList<Breadcrumb>();
        }
    }

    public List<Breadcrumb> getEllipsisCrumbs(List<Breadcrumb> trail) {
        Breadcrumb crumb;
        if (trail.size() < 4) {
            return Collections.emptyList();
        }
        LinkedList ellipsedCrumbs = Lists.newLinkedList(trail);
        ellipsedCrumbs.remove(0);
        ListIterator ellipsedCrumbIterator = ellipsedCrumbs.listIterator(ellipsedCrumbs.size());
        while (ellipsedCrumbIterator.hasPrevious()) {
            crumb = (Breadcrumb)ellipsedCrumbIterator.previous();
            ellipsedCrumbIterator.remove();
            if (!(crumb instanceof com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb) && !(crumb instanceof PageBreadcrumb) || "edited-page-title".equals(crumb.getCssClass())) continue;
            break;
        }
        while (ellipsedCrumbIterator.hasPrevious()) {
            crumb = (Breadcrumb)ellipsedCrumbIterator.previous();
            if (!(crumb instanceof SpaceBreadcrumb) && !(crumb instanceof com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb) && !(crumb instanceof PeopleBreadcrumb) && !(crumb instanceof PagesCollectorBreadcrumb) && !(crumb instanceof BlogCollectorBreadcrumb)) continue;
            ellipsedCrumbIterator.remove();
            while (ellipsedCrumbIterator.hasPrevious()) {
                ellipsedCrumbIterator.previous();
                ellipsedCrumbIterator.remove();
            }
        }
        if (ellipsedCrumbs.size() < 2) {
            return Collections.emptyList();
        }
        return ellipsedCrumbs;
    }
}

