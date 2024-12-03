/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.LinkRenderer
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.macro.ContentFilteringMacro
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.ContentIncludeStack
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.ContentComparatorFactory
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.LinkRenderer;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.macro.ContentFilteringMacro;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.macros.advanced.BaseUrlHelper;
import com.atlassian.confluence.plugins.macros.advanced.BulkPermissionHelper;
import com.atlassian.confluence.plugins.macros.advanced.analytics.ChildrenMacroMetrics;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.AdvancedMacrosExcerpter;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptType;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.ContentComparatorFactory;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class ChildrenMacro
extends ContentFilteringMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(ChildrenMacro.class);
    private static final String PAGE_LIMIT_PARAMETER_NAME = "confluence.child-macro.page-limit";
    private static final int PAGE_LIMIT = Integer.getInteger("confluence.child-macro.page-limit", Integer.MAX_VALUE);
    private static final String MAX_DEPTH_PARAMETER_NAME = "confluence.child-macro.max-depth";
    private static final int MAX_DEPTH = Integer.getInteger("confluence.child-macro.max-depth", Integer.MAX_VALUE);
    private static final String DISABLE_EXCERPT_PARAMETER_NAME = "confluence.child-macro.disable-excerpt";
    private static final boolean DISABLE_EXCERPT = Boolean.getBoolean("confluence.child-macro.disable-excerpt");
    private PageManager pageManager;
    private SpaceManager spaceManager;
    private LinkRenderer viewLinkRenderer;
    private PermissionManager permissionManager;
    private ContentPermissionManager contentPermissionManager;
    private ConfluenceActionSupport confluenceActionSupport;
    private WebResourceManager webResourceManager;
    private EventPublisher eventPublisher;
    private AdvancedMacrosExcerpter advancedMacrosExcerpter;
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private BulkPermissionHelper bulkPermissionHelper;
    private SettingsManager settingsManager;
    private UserAccessor userAccessor;
    private CrowdService crowdService;
    private Comparator<Page> sort;
    private final AtomicReference<String> baseUrl = new AtomicReference();

    public boolean isInline() {
        return this.getOutputType() == Macro.OutputType.INLINE;
    }

    public boolean hasBody() {
        return this.getBodyType() != Macro.BodyType.NONE;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        List<Page> children;
        int depth;
        StopWatch watch = StopWatch.createStarted();
        this.baseUrl.set(BaseUrlHelper.calculateBaseUrl(this.settingsManager));
        HashMap<Long, List<Page>> fullPageTree = new HashMap<Long, List<Page>>();
        AtomicInteger renderedPageCounter = new AtomicInteger();
        ChildrenMacroMetrics.Builder metrics = ChildrenMacroMetrics.builder();
        Helper helper = new Helper(metrics, context);
        this.webResourceManager.requireResource("confluence.macros.advanced:children-resource");
        try {
            depth = this.calculateDepth(parameters);
        }
        catch (NumberFormatException e) {
            return RenderUtils.blockError((String)this.getConfluenceActionSupport().getText("children.error.unable-to-render"), (String)this.getConfluenceActionSupport().getText("children.error.invalid-depth", new String[]{StringEscapeUtils.escapeHtml4((String)parameters.get("depth"))}));
        }
        String pageTitle = parameters.get("page");
        try {
            children = pageTitle != null && (pageTitle.equals("/") || pageTitle.endsWith(":")) ? helper.getRootPagesForSpace(pageTitle, depth, fullPageTree, this.bulkPermissionHelper) : helper.getChildrenFromPage(pageTitle, depth, fullPageTree, this.bulkPermissionHelper);
        }
        catch (IllegalArgumentException e) {
            return RenderUtils.blockError((String)this.getConfluenceActionSupport().getText("children.error.unable-to-render"), (String)e.getMessage());
        }
        this.sort = this.configureComparator(parameters);
        if (this.sort != null) {
            children.sort(this.sort);
        }
        if (StringUtils.isNotEmpty((CharSequence)parameters.get("first"))) {
            try {
                int first = Integer.parseInt(parameters.get("first"));
                if (first > 0 && first < children.size()) {
                    children = children.subList(0, first);
                }
            }
            catch (NumberFormatException first) {
                // empty catch block
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Child macro parameters: " + parameters);
        }
        ExcerptType excerptType = this.disableExcerptIfRequired(ExcerptType.fromString(parameters.get("excerptType")));
        String output = helper.render(parameters, children, excerptType, this.limitDepthIfRequired(depth), renderedPageCounter, fullPageTree, this.bulkPermissionHelper);
        watch.stop();
        boolean permissionsExempt = BulkPermissionHelper.isPermissionExempt(this.userAccessor, this.crowdService, (User)AuthenticatedUserThreadLocal.get());
        metrics.setPermissionsExempt(permissionsExempt);
        metrics.setOverallDuration(watch.getTime());
        metrics.setRenderedLinksTotal(renderedPageCounter.get());
        metrics.bulkPermissionsOptions(this.bulkPermissionHelper.shouldCallBulkPermissionsAPI(), this.bulkPermissionHelper.isBulkPermissionsUpAndRunning());
        ChildrenMacroMetrics finalMetrics = metrics.build();
        this.eventPublisher.publish((Object)finalMetrics);
        log.debug("Child Macro Metrics: permissions for {} children were checked in {} ms ({} invocations), {} excerpts were rendered in {} ms. Macro rendering took {} ms. {} links were rendered. Bulk permissions service was used: {}, up and running: {}. Permissions exempt: {}", new Object[]{finalMetrics.getPermittedChildrenFetchItemTotal(), finalMetrics.getPermittedChildrenFetchMillis(), finalMetrics.getPermittedChildrenFetchInvocationCount(), finalMetrics.getExcerptSummariseInvocationCount(), finalMetrics.getExcerptSumariseMillis(), watch.getTime(), renderedPageCounter.get(), this.bulkPermissionHelper.shouldCallBulkPermissionsAPI(), this.bulkPermissionHelper.isBulkPermissionsUpAndRunning(), permissionsExempt});
        if (this.bulkPermissionHelper.shouldPrintDebugInformation()) {
            return "<div class=\"debug-information-block\"><header><h1>Debug information</h1><small>This block is displayed because 'force-use-bulk-permissions' GET parameter was provided</small></header><p>Fast permissions up and running:" + this.bulkPermissionHelper.isBulkPermissionsUpAndRunning() + "</p><p><b>Fast permissions API called:" + this.bulkPermissionHelper.shouldCallBulkPermissionsAPI() + "</b></p><p>Pages processed:" + renderedPageCounter.get() + "</p><p>Macro rendering time:" + watch + "</p></div>" + output;
        }
        return output;
    }

    private int calculateDepth(Map<String, String> parameters) {
        if ("true".equalsIgnoreCase(parameters.get("all")) || "all".equalsIgnoreCase(parameters.get("depth"))) {
            return 0;
        }
        if (StringUtils.isNotEmpty((CharSequence)parameters.get("depth"))) {
            return Integer.parseInt(parameters.get("depth"));
        }
        return 1;
    }

    private ExcerptType disableExcerptIfRequired(ExcerptType excerptType) {
        if (DISABLE_EXCERPT && !ExcerptType.NONE.equals((Object)excerptType)) {
            log.debug("Child Macro 'Display Excerpt' parameter was disabled by the system variable ({})", (Object)DISABLE_EXCERPT_PARAMETER_NAME);
            return ExcerptType.NONE;
        }
        return excerptType;
    }

    private int limitDepthIfRequired(int depth) {
        int newDepth;
        int n = newDepth = depth == 0 && MAX_DEPTH != Integer.MAX_VALUE ? MAX_DEPTH : Math.min(depth, MAX_DEPTH);
        if (newDepth != depth) {
            log.debug("Child Macro depth parameter was decreased from {} to {} by the system variable ({})", new Object[]{depth != 0 ? Integer.valueOf(depth) : "unlimited", newDepth, MAX_DEPTH_PARAMETER_NAME});
        }
        return newDepth;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String execute(MacroExecutionContext ctx) throws MacroException {
        try {
            return this.execute(ctx.getParams(), ctx.getBody(), (ConversionContext)new DefaultConversionContext((RenderContext)ctx.getPageContext()));
        }
        catch (MacroExecutionException ex) {
            throw new MacroException(ex.getCause() != null ? ex.getCause() : ex);
        }
    }

    private Comparator<Page> configureComparator(Map<String, String> parameters) {
        String sortType = parameters.get("sort");
        boolean reverse = Boolean.valueOf(parameters.get("reverse"));
        return ContentComparatorFactory.getComparator((String)sortType, (boolean)reverse);
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setCrowdService(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Qualifier(value="contentPermissionManager")
    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    public void setXmlStreamWriterTemplate(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    public void setAdvancedMacrosExcerpter(AdvancedMacrosExcerpter advancedMacrosExcerpter) {
        this.advancedMacrosExcerpter = advancedMacrosExcerpter;
    }

    public void setBulkPermissionHelper(BulkPermissionHelper bulkPermissionHelper) {
        this.bulkPermissionHelper = bulkPermissionHelper;
    }

    public void setWebResourceManager(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    protected ConfluenceActionSupport getConfluenceActionSupport() {
        if (null == this.confluenceActionSupport) {
            this.confluenceActionSupport = new ConfluenceActionSupport();
            ContainerManager.autowireComponent((Object)this.confluenceActionSupport);
        }
        return this.confluenceActionSupport;
    }

    public void setViewLinkRenderer(LinkRenderer viewLinkRenderer) {
        this.viewLinkRenderer = viewLinkRenderer;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    private class Helper {
        private final ChildrenMacroMetrics.Builder metrics;
        private final ConversionContext conversionContext;
        private final PageContext pageContext;

        Helper(ChildrenMacroMetrics.Builder metrics, ConversionContext conversionContext) {
            this.metrics = metrics;
            this.conversionContext = conversionContext;
            this.pageContext = conversionContext != null && conversionContext.getPageContext() != null ? conversionContext.getPageContext() : new PageContext();
        }

        private String render(Map<String, String> parameters, List<Page> children, ExcerptType excerptType, int depth, AtomicInteger renderedPageCounter, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) throws MacroExecutionException {
            this.metrics.renderOptions(excerptType, depth);
            if (StringUtils.isNotEmpty((CharSequence)parameters.get("style"))) {
                String style = parameters.get("style");
                if (style.matches("h[1-6]")) {
                    return this.printChildrenUnderHeadings(children, depth, excerptType, style.charAt(1), renderedPageCounter, fullPageTree, bulkPermissionHelper);
                }
                return RenderUtils.blockError((String)ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.unable-to-render"), (String)ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.unknown-style", new String[]{StringEscapeUtils.escapeHtml4((String)style)}));
            }
            return this.printChildren(children, depth, excerptType, renderedPageCounter, fullPageTree, bulkPermissionHelper);
        }

        private List<Page> getRootPagesForSpace(String pageTitle, int depth, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) {
            Space space = this.getSpace(pageTitle);
            if (bulkPermissionHelper.shouldCallBulkPermissionsAPI()) {
                fullPageTree.putAll(bulkPermissionHelper.getAllDescendants(space, null, depth));
                List<Page> topLevelPages = fullPageTree.getOrDefault(null, Collections.emptyList());
                if (this.pageContext.getEntity() != null) {
                    return topLevelPages.stream().filter(this.excludeCurrentPagePredicate(this.pageContext.getEntity().getId())).collect(Collectors.toList());
                }
                return topLevelPages;
            }
            List topLevelPages = ChildrenMacro.this.pageManager.getTopLevelPages(space);
            List pages = this.filterPermittedEntities(topLevelPages);
            if (this.pageContext.getEntity() != null) {
                this.removeThisPageFromList(pages, this.pageContext.getEntity());
            }
            return pages;
        }

        private Predicate<Page> excludeCurrentPagePredicate(long currentPageId) {
            return page -> page.getId() != currentPageId;
        }

        private Space getSpace(String pageTitle) {
            Space space = null;
            if ("/".equals(pageTitle)) {
                space = this.getCurrentSpace();
            } else if (pageTitle.length() > 1 && pageTitle.endsWith(":")) {
                space = ChildrenMacro.this.spaceManager.getSpace(pageTitle.substring(0, pageTitle.length() - 1));
            }
            if (!ChildrenMacro.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
                throw new IllegalArgumentException(ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.space-does-not-exists", new String[]{StringEscapeUtils.escapeHtml4((String)pageTitle)}));
            }
            return space;
        }

        private List filterPermittedEntities(List topLevelPages) {
            this.metrics.filterPermittedEntitiesStart(topLevelPages.size());
            List permittedEntities = ChildrenMacro.this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, topLevelPages);
            this.metrics.filterPermittedEntitiesFinish();
            return permittedEntities;
        }

        private Space getCurrentSpace() {
            ContentEntityObject obj = this.pageContext.getEntity();
            if (!(obj instanceof SpaceContentEntityObject)) {
                throw new IllegalArgumentException(ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.content-not-belong-to-space"));
            }
            Space space = ((SpaceContentEntityObject)obj).getSpace();
            return space;
        }

        private void removeThisPageFromList(List<Page> pages, ContentEntityObject thisPage) {
            pages.removeIf(page -> page.getId() == thisPage.getId());
        }

        private List<Page> getChildrenFromPage(String pageTitle, int depth, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) {
            ContentEntityObject target = this.getPage(this.pageContext, pageTitle);
            if (target == null) {
                if (StringUtils.isNotEmpty((CharSequence)pageTitle)) {
                    throw new IllegalArgumentException(ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.page-not-found", new String[]{StringEscapeUtils.escapeHtml4((String)pageTitle)}));
                }
                throw new IllegalArgumentException(ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.macro-works-on-only-pages"));
            }
            if (!ChildrenMacro.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)target)) {
                throw new IllegalArgumentException(ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.page-not-found", new String[]{StringEscapeUtils.escapeHtml4((String)pageTitle)}));
            }
            if (!(target instanceof Page)) {
                throw new IllegalArgumentException(ChildrenMacro.this.getConfluenceActionSupport().getText("children.error.can-only-find-children-of-a-page", new String[]{target.getType()}));
            }
            Page page = (Page)target;
            if (bulkPermissionHelper.shouldCallBulkPermissionsAPI()) {
                fullPageTree.putAll(bulkPermissionHelper.getAllDescendants(page.getSpace(), page.getId(), depth));
                return fullPageTree.getOrDefault(page.getId(), Collections.emptyList());
            }
            return this.getPermittedChildren(page, fullPageTree, bulkPermissionHelper);
        }

        private ContentEntityObject getPage(PageContext context, String pageTitleToRetrieve) {
            if (StringUtils.isEmpty((CharSequence)pageTitleToRetrieve)) {
                return this.getCurrentPage(context);
            }
            String spaceKey = context.getSpaceKey();
            String pageTitle = pageTitleToRetrieve;
            int colonIndex = pageTitleToRetrieve.indexOf(":");
            if (colonIndex != -1 && colonIndex != pageTitleToRetrieve.length() - 1) {
                spaceKey = pageTitleToRetrieve.substring(0, colonIndex);
                pageTitle = pageTitleToRetrieve.substring(colonIndex + 1);
            }
            return ChildrenMacro.this.pageManager.getPage(spaceKey, pageTitle);
        }

        private ContentEntityObject getCurrentPage(PageContext context) {
            ContentEntityObject entity = ContentIncludeStack.peek();
            if (entity instanceof Page) {
                return entity;
            }
            return context.getEntity();
        }

        private String printChildren(List<Page> pages, int depth, ExcerptType excerptType, AtomicInteger renderedPageCounter, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) throws MacroExecutionException {
            StringBuilder buf = new StringBuilder();
            this.printChildren(pages, buf, depth, excerptType, 0, renderedPageCounter, fullPageTree, bulkPermissionHelper);
            return buf.toString();
        }

        private String printChildrenUnderHeadings(List<Page> children, int depth, ExcerptType excerptType, char headerType, AtomicInteger renderedPageCounter, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) throws MacroExecutionException {
            StringBuilder buffer = new StringBuilder();
            int pageCounter = 0;
            for (Page child : children) {
                renderedPageCounter.incrementAndGet();
                if (++pageCounter > PAGE_LIMIT) {
                    log.debug("Child Macro: page limit ({}) was reached. Current depth level is {}. Processing stopped. Limit was set by the system variable ({}).", new Object[]{PAGE_LIMIT, depth, ChildrenMacro.PAGE_LIMIT_PARAMETER_NAME});
                    return buffer.toString();
                }
                buffer.append("<h").append(headerType).append(">");
                buffer.append(this.makePageLink(child, bulkPermissionHelper));
                buffer.append("</h").append(headerType).append(">\n");
                buffer.append(this.renderExcerpt(child, excerptType, "<p>", "</p>\n"));
                if (depth == 1) continue;
                this.printChildren(this.getPermittedChildren(child, fullPageTree, bulkPermissionHelper), buffer, depth - 1, excerptType, pageCounter, renderedPageCounter, fullPageTree, bulkPermissionHelper);
            }
            return buffer.toString();
        }

        private void printChildren(List<Page> children, StringBuilder buffer, int depth, ExcerptType excerptType, int pageCounter, AtomicInteger renderedPageCounter, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) throws MacroExecutionException {
            if (children.size() > 0) {
                if (ChildrenMacro.this.sort != null) {
                    children.sort(ChildrenMacro.this.sort);
                }
                buffer.append("<ul class='childpages-macro'>");
                for (Page child : children) {
                    renderedPageCounter.incrementAndGet();
                    if (++pageCounter > PAGE_LIMIT) {
                        log.debug("Child Macro: page limit ({}) was reached. Current depth level is {}. Processing stopped. Limit was set by the system variable ({}).", new Object[]{PAGE_LIMIT, depth, ChildrenMacro.PAGE_LIMIT_PARAMETER_NAME});
                        return;
                    }
                    buffer.append("<li>").append(this.makePageLink(child, bulkPermissionHelper));
                    buffer.append(this.renderExcerpt(child, excerptType, " &mdash; <span class=\"smalltext\">", "</span>"));
                    if (depth != 1) {
                        this.printChildren(this.getPermittedChildren(child, fullPageTree, bulkPermissionHelper), buffer, depth - 1, excerptType, pageCounter, renderedPageCounter, fullPageTree, bulkPermissionHelper);
                    }
                    buffer.append("</li>");
                }
                buffer.append("</ul>");
            }
        }

        private String renderExcerpt(Page child, ExcerptType excerptType, String legacyWrapperStart, String legacyWrapperEnd) {
            this.metrics.excerptSummariseStart();
            String summary = ChildrenMacro.this.advancedMacrosExcerpter.createExcerpt((ContentEntityObject)child, excerptType, this.conversionContext, legacyWrapperStart, legacyWrapperEnd);
            this.metrics.excerptSummariseFinish();
            return summary;
        }

        private List<Page> getPermittedChildren(Page child, Map<Long, List<Page>> fullPageTree, BulkPermissionHelper bulkPermissionHelper) {
            if (bulkPermissionHelper.shouldCallBulkPermissionsAPI()) {
                return fullPageTree.getOrDefault(child.getId(), Collections.emptyList());
            }
            this.metrics.permittedChildrenFetchStart();
            List children = ChildrenMacro.this.contentPermissionManager.getPermittedChildren(child, (User)AuthenticatedUserThreadLocal.get());
            this.metrics.permittedChildrenFetchFinish(children.size());
            return children;
        }

        private String makePageLink(Page child, BulkPermissionHelper bulkPermissionHelper) throws MacroExecutionException {
            try {
                this.metrics.renderPageLinkStart();
                String string = bulkPermissionHelper.shouldCallBulkPermissionsAPI() ? this.renderLinkQuickly(child) : ChildrenMacro.this.viewLinkRenderer.render((ContentEntityObject)child, this.conversionContext);
                return string;
            }
            catch (XhtmlException e) {
                throw new MacroExecutionException((Throwable)e);
            }
            finally {
                this.metrics.renderPageLinkFinish();
            }
        }

        private String renderLinkQuickly(Page page) {
            Streamable streamable = Streamables.from((XmlStreamWriterTemplate)ChildrenMacro.this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
                xmlStreamWriter.writeStartElement("a");
                xmlStreamWriter.writeAttribute("href", ChildrenMacro.this.baseUrl.get() + page.getUrlPath());
                StaxUtils.writeRawXML((XMLStreamWriter)xmlStreamWriter, (Writer)underlyingWriter, (Streamable)Streamables.from((String)StringEscapeUtils.escapeHtml4((String)page.getTitle())));
                xmlStreamWriter.writeEndElement();
            });
            return Streamables.writeToString((Streamable)streamable);
        }
    }
}

