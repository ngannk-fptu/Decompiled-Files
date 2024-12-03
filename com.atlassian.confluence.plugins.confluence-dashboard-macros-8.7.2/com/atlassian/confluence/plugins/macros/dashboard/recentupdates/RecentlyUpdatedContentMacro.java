/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentUpdateGroup;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedContentService;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroTabProvider;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events.DashboardPopularTabViewEvent;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events.DashboardRecentlyUpdatedViewEvent;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.AllContentTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.PopularTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.RecentlyUpdatedMacroTab;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentlyUpdatedContentMacro
extends BaseMacro {
    private static final Logger log = LoggerFactory.getLogger(RecentlyUpdatedContentMacro.class);
    private static final Set<String> IGNORED_PARAMS = Sets.newHashSet((Object[])new String[]{"updatesSelectedTab", "updatesSelectedTeam", "maxRecentlyUpdatedPageCount"});
    public static final String MACRO_NAME = "recently-updated-dashboard";
    public static final int UPDATES_STEP_SIZE = 10;
    public static final int UPDATES_MAX_SIZE = 50;
    public static final int UPDATES_MIN_SIZE = 10;
    private final HttpContext httpContext;
    private final I18NBeanFactory i18NBeanFactory;
    private final LabelManager labelManager;
    private final EventPublisher eventPublisher;
    private final RecentlyUpdatedContentService recentlyUpdatedContentService;
    private final RecentlyUpdatedMacroTabProvider tabProvider;
    private final TemplateRenderer templateRenderer;
    private final LocaleManager localeManager;
    private final PageBuilderService pageBuilderService;

    public RecentlyUpdatedContentMacro(RecentlyUpdatedContentService recentlyUpdatedContentService, RecentlyUpdatedMacroTabProvider tabProvider, @ComponentImport LabelManager labelManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport HttpContext httpContext, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport TemplateRenderer templateRenderer, @ComponentImport LocaleManager localeManager, @ComponentImport PageBuilderService pageBuilderService) {
        this.recentlyUpdatedContentService = recentlyUpdatedContentService;
        this.tabProvider = tabProvider;
        this.labelManager = labelManager;
        this.eventPublisher = eventPublisher;
        this.httpContext = httpContext;
        this.i18NBeanFactory = i18NBeanFactory;
        this.templateRenderer = templateRenderer;
        this.localeManager = localeManager;
        this.pageBuilderService = pageBuilderService;
    }

    public String execute(Map macroParamsUnchecked, String body, RenderContext renderContext) throws MacroException {
        String string;
        block9: {
            Ticker ignored = Timers.start((String)"Macro: {recently-updated-dashboard}");
            try {
                long start = System.currentTimeMillis();
                Map<String, String> rawMacroParams = RecentlyUpdatedContentMacro.castMacroParams(macroParamsUnchecked);
                RecentlyUpdatedMacroParams macroParams = new RecentlyUpdatedMacroParams(rawMacroParams, this.labelManager);
                RecentlyUpdatedMacroRequestParams requestParams = new RecentlyUpdatedMacroRequestParams(this.httpContext);
                int numUpdates = this.parseMaxResults(requestParams);
                List<RecentlyUpdatedMacroTab> visibleTabs = this.getVisibleTabs(renderContext);
                RecentlyUpdatedMacroTab selectedTab = this.getSelectedTab(requestParams, visibleTabs, renderContext);
                RecentUpdateQueryParameters query = selectedTab.getQueryParameters(macroParams, requestParams, renderContext);
                List<RecentUpdateGroup> changeSets = this.recentlyUpdatedContentService.getRecentUpdates(query, numUpdates);
                Map contextMap = MacroUtils.defaultVelocityContext();
                contextMap.putAll(selectedTab.getRenderContext(requestParams, renderContext));
                contextMap.put("tabs", visibleTabs);
                contextMap.put("selectedTab", selectedTab.getName());
                contextMap.put("baseUrl", this.getBaseUrl());
                contextMap.put("contextPath", this.httpContext.getRequest().getContextPath());
                contextMap.put("numUpdates", numUpdates);
                contextMap.put("updatesStepSize", 10);
                contextMap.put("updatesMaxSize", 50);
                contextMap.put("updatesMinSize", 10);
                contextMap.put("changeSets", changeSets);
                contextMap.put("showProfilePic", macroParams.isShowProfilePic());
                contextMap.put("showSpaceName", this.isSearchingMultipleSpaces(query));
                contextMap.put("showMoreUrl", this.getUrlForMaxResults(numUpdates + 10));
                contextMap.put("showLessUrl", this.getUrlForMaxResults(numUpdates - 10));
                contextMap.put("showMoreEnabled", numUpdates < 50);
                contextMap.put("showLessEnabled", numUpdates > 10);
                contextMap.put("nonExistentLabels", macroParams.getNonExistentLabels());
                contextMap.put("currentUsername", AuthenticatedUserThreadLocal.getUsername());
                contextMap.put("macroParamsLabelsFilter", rawMacroParams.get("labels"));
                contextMap.put("macroParamsSpacesFilter", rawMacroParams.get("spaces"));
                contextMap.put("macroParamsUsersFilter", rawMacroParams.get("users"));
                contextMap.put("macroParamsTypesFilter", rawMacroParams.get("types"));
                if (changeSets.isEmpty()) {
                    contextMap.put("noContentMessage", selectedTab.getNoContentMessage());
                }
                String template = this.renderTemplate(contextMap);
                long end = System.currentTimeMillis();
                this.publishTabViewEvent(selectedTab, start, end);
                string = template;
                if (ignored == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (InvalidSearchException e) {
                    log.error("recently-updated-dashboard could not search for recent updates: ", (Throwable)e);
                    throw new MacroException("Could not search for recent updates: " + e.getMessage(), (Throwable)e);
                }
            }
            ignored.close();
        }
        return string;
    }

    private String renderTemplate(Map<String, Object> contextMap) {
        this.pageBuilderService.assembler().resources().requireWebResource("confluence.macros.dashboard:old-dashboard-resource-loader");
        StringBuilder buf = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)buf, "confluence.macros.dashboard:dashboard-macros-resources", "Confluence.Templates.Dashboard.Updates.tabs.soy", contextMap);
        return buf.toString();
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    private List<RecentlyUpdatedMacroTab> getVisibleTabs(RenderContext renderContext) {
        return ImmutableList.copyOf((Iterable)Iterables.filter(this.tabProvider.getAvailableTabs(), tab -> tab.shouldDisplay(renderContext)));
    }

    private void publishTabViewEvent(RecentlyUpdatedMacroTab selectedTab, long start, long end) {
        if (selectedTab instanceof PopularTab) {
            this.eventPublisher.publish((Object)new DashboardPopularTabViewEvent((Object)this, selectedTab.getName(), end - start));
        } else {
            this.eventPublisher.publish((Object)new DashboardRecentlyUpdatedViewEvent((Object)this, selectedTab.getName(), end - start));
        }
    }

    private static Map<String, String> castMacroParams(Map macroParams) {
        return macroParams;
    }

    private boolean isSearchingMultipleSpaces(RecentUpdateQueryParameters query) {
        return query.getSpaceKeys() == null || query.getSpaceKeys().size() != 1;
    }

    private int parseMaxResults(RecentlyUpdatedMacroRequestParams requestParams) {
        if (requestParams.hasMaxRecentUpdates()) {
            this.recentlyUpdatedContentService.setPreferredMaxResults(requestParams.getMaxRecentUpdates());
        }
        return this.recentlyUpdatedContentService.getPreferredMaxResults();
    }

    private RecentlyUpdatedMacroTab getSelectedTab(RecentlyUpdatedMacroRequestParams requestParams, List<RecentlyUpdatedMacroTab> visibleTabs, RenderContext renderContext) {
        String tabName = requestParams.getSelectedTab();
        if (tabName != null) {
            this.recentlyUpdatedContentService.setPreferredTab(tabName);
        } else {
            tabName = this.recentlyUpdatedContentService.getPreferredTab();
        }
        RecentlyUpdatedMacroTab selectedTab = this.tabProvider.getTabByName(tabName);
        if (!visibleTabs.contains(selectedTab)) {
            RecentlyUpdatedMacroTab defaultTab = this.tabProvider.getDefaultTab();
            if (!visibleTabs.contains(defaultTab)) {
                selectedTab = new AllContentTab(this.httpContext, this.i18NBeanFactory, this.localeManager);
                if (!selectedTab.shouldDisplay(renderContext)) {
                    throw new AssertionError((Object)"all content tab should always be visible");
                }
            } else {
                selectedTab = defaultTab;
            }
        }
        return selectedTab;
    }

    private String getBaseUrl() {
        HttpServletRequest request = this.httpContext.getRequest();
        String uri = request.getRequestURI();
        UrlBuilder baseUrl = new UrlBuilder(uri);
        Map<String, String[]> requestParams = RecentlyUpdatedContentMacro.castRequestParams(request.getParameterMap());
        for (Map.Entry<String, String[]> param : requestParams.entrySet()) {
            if (IGNORED_PARAMS.contains(param.getKey())) continue;
            baseUrl.add(param.getKey(), param.getValue()[0]);
        }
        return baseUrl.toUrl();
    }

    private String getUrlForMaxResults(int maxResults) {
        HttpServletRequest request = this.httpContext.getRequest();
        String pageUrl = request.getContextPath() + request.getServletPath();
        UrlBuilder urlBuilder = new UrlBuilder(pageUrl);
        urlBuilder.add("maxRecentlyUpdatedPageCount", maxResults);
        return urlBuilder.toString();
    }

    private static Map<String, String[]> castRequestParams(Map requestParams) {
        return requestParams;
    }
}

