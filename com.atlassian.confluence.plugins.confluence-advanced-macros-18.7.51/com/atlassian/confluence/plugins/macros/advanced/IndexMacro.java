/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.AbstractHtmlGeneratingMacro
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.VersionSpecificDocumentationBean
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.time.StopWatch
 *  org.radeox.macro.parameter.MacroParameter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.macros.advanced.BaseUrlHelper;
import com.atlassian.confluence.plugins.macros.advanced.BulkPermissionHelper;
import com.atlassian.confluence.plugins.macros.advanced.analytics.IndexMacroMetrics;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.AbstractHtmlGeneratingMacro;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.denormalisedpermissions.BulkPermissionService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.VersionSpecificDocumentationBean;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.time.StopWatch;
import org.radeox.macro.parameter.MacroParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexMacro
extends AbstractHtmlGeneratingMacro {
    public static final String DOC_LINK = "help.page.index.macro";
    @VisibleForTesting
    static final int EXCERPT_RENDER_LIMIT = Integer.getInteger("page.index.macro.excerpt-limit", 200);
    @VisibleForTesting
    static final int INDEX_RENDER_LIMIT = 1000;
    private static final int MAX_PAGES_TO_RENDER = Integer.getInteger("page.index.macro.max.pages", 1000);
    @VisibleForTesting
    static final int MAX_PAGES_TO_RENDER_FOR_BULK_PERMISSIONS_ENABLED = Integer.getInteger("page.index.macro.max.pages.bulk.permissions", 10000);
    @VisibleForTesting
    static final String TEMPLATE = "com/atlassian/confluence/plugins/macros/advanced/alphaindex.vm";
    @VisibleForTesting
    static final String TEMPLATE_ERROR = "com/atlassian/confluence/plugins/macros/advanced/alphaindexerror.vm";
    private static final Logger log = LoggerFactory.getLogger(IndexMacro.class);
    private static final String MACRO_NAME = "index";
    private String[] myParamDescription = new String[]{"1: ignored"};
    private SpaceManager spaceManager;
    private PermissionManager permissionManager;
    private PageManager pageManager;
    private SettingsManager settingsManager;
    private VersionSpecificDocumentationBean docBean;
    private EventPublisher eventPublisher;
    private UserAccessor userAccessor;
    private CrowdService crowdService;
    private BulkPermissionHelper bulkPermissionHelper;
    private BulkPermissionService bulkPermissionService;

    public String getName() {
        return MACRO_NAME;
    }

    public String[] getParamDescription() {
        return this.myParamDescription;
    }

    public String getHtml(MacroParameter macroParameter) throws IllegalArgumentException, IOException {
        List<Page> pages;
        StopWatch watch = StopWatch.createStarted();
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        Map contextParams = macroParameter.getContext().getParameters();
        String currentSpaceKey = ((PageContext)contextParams.get("RENDER_CONTEXT")).getSpaceKey();
        Space currentSpace = this.spaceManager.getSpace(currentSpaceKey);
        contextMap.put("isBulkPermissionsUpAndRunning", this.bulkPermissionHelper.isBulkPermissionsUpAndRunning());
        contextMap.put("shouldCallBulkPermissionsAPI", this.bulkPermissionHelper.shouldCallBulkPermissionsAPI());
        long totalPages = this.pageManager.getPageCount(currentSpace.getKey());
        contextMap.put("baseurl", BaseUrlHelper.calculateBaseUrl(this.settingsManager));
        contextMap.put("printDebugInformation", this.bulkPermissionHelper.shouldPrintDebugInformation());
        contextMap.put("thisMacro", (Object)this);
        contextMap.put("excerptRenderLimit", EXCERPT_RENDER_LIMIT);
        boolean permissionsExempt = BulkPermissionHelper.isPermissionExempt(this.userAccessor, this.crowdService, (User)AuthenticatedUserThreadLocal.get());
        if (this.bulkPermissionHelper.shouldCallBulkPermissionsAPI()) {
            contextMap.put("totalPages", totalPages);
            contextMap.put("maxPages", MAX_PAGES_TO_RENDER_FOR_BULK_PERMISSIONS_ENABLED);
            if (totalPages > (long)MAX_PAGES_TO_RENDER_FOR_BULK_PERMISSIONS_ENABLED) {
                contextMap.put("docUrl", this.docBean.getLink(DOC_LINK));
                IndexMacroMetrics metrics = new IndexMacroMetrics(permissionsExempt, 0, watch.getTime(), false, this.bulkPermissionHelper.isBulkPermissionsUpAndRunning(), MAX_PAGES_TO_RENDER_FOR_BULK_PERMISSIONS_ENABLED, true);
                this.eventPublisher.publish((Object)metrics);
                return this.getVelocityRenderedTemplate(TEMPLATE_ERROR, contextMap);
            }
            pages = this.fetchPagesFromBulkPermissionService(currentSpace);
            contextMap.put("totalVisiblePages", pages.size());
            contextMap.put("pages", new AlphabeticPageListing(pages));
            contextMap.put("totalPages", pages.size());
            contextMap.put("duration", watch.getTime());
            IndexMacroMetrics metrics = new IndexMacroMetrics(permissionsExempt, pages.size(), watch.getTime(), true, this.bulkPermissionHelper.isBulkPermissionsUpAndRunning(), 0, false);
            this.eventPublisher.publish((Object)metrics);
        } else {
            contextMap.put("totalPages", totalPages);
            contextMap.put("maxPages", MAX_PAGES_TO_RENDER);
            if (totalPages > (long)MAX_PAGES_TO_RENDER) {
                contextMap.put("docUrl", this.docBean.getLink(DOC_LINK));
                IndexMacroMetrics metrics = new IndexMacroMetrics(permissionsExempt, 0, watch.getTime(), false, false, MAX_PAGES_TO_RENDER, true);
                this.eventPublisher.publish((Object)metrics);
                return this.getVelocityRenderedTemplate(TEMPLATE_ERROR, contextMap);
            }
            pages = this.fetchPages(currentSpace);
            contextMap.put("totalVisiblePages", pages.size());
            contextMap.put("pages", new AlphabeticPageListing(pages));
            contextMap.put("duration", watch.getTime());
            IndexMacroMetrics metrics = new IndexMacroMetrics(permissionsExempt, pages.size(), watch.getTime(), false, false, MAX_PAGES_TO_RENDER, false);
            this.eventPublisher.publish((Object)metrics);
        }
        try {
            return this.getVelocityRenderedTemplate(TEMPLATE, contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to assemble the IndexMacro result!", (Throwable)e);
            throw new IOException(e.getMessage());
        }
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    @VisibleForTesting
    protected String getVelocityRenderedTemplate(String templateName, Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)templateName, contextMap);
    }

    @HtmlSafe
    public String getHtmlSafeExcerpt(Page page) {
        return HtmlUtil.htmlEncode((String)page.getExcerpt());
    }

    private List<Page> fetchPages(Space currentSpace) {
        List pagesWithPermissions = this.pageManager.getPagesWithPermissions(currentSpace);
        return this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, pagesWithPermissions);
    }

    private List<Page> fetchPagesFromBulkPermissionService(Space space) {
        List allPages = this.bulkPermissionService.getAllVisiblePagesInSpace(AuthenticatedUserThreadLocal.get(), space.getId());
        return BulkPermissionHelper.fromSimpleContentList(allPages, space);
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public VersionSpecificDocumentationBean getDocBean() {
        return this.docBean;
    }

    public void setDocBean(VersionSpecificDocumentationBean docBean) {
        this.docBean = docBean;
    }

    public void setBulkPermissionHelper(BulkPermissionHelper bulkPermissionHelper) {
        this.bulkPermissionHelper = bulkPermissionHelper;
    }

    public void setBulkPermissionService(BulkPermissionService bulkPermissionService) {
        this.bulkPermissionService = bulkPermissionService;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setCrowdService(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public static class AlphabeticPageListing {
        private static final String[] keys = new String[]{"0-9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "!@#$"};
        private Map<String, TreeSet<Page>> pageMap;
        private List<String> keyList = Arrays.asList(keys);

        public AlphabeticPageListing(List<Page> pages) {
            this.pageMap = new HashMap<String, TreeSet<Page>>();
            for (Page page : pages) {
                char c = Character.toUpperCase(page.getTitle().charAt(0));
                if (Character.isDigit(c)) {
                    this.addToMap("0-9", page);
                    continue;
                }
                if (c >= 'A' && c <= 'Z') {
                    this.addToMap(Character.toString(c), page);
                    continue;
                }
                this.addToMap("@", page);
            }
        }

        public List<String> getKeys() {
            return this.keyList;
        }

        public int getPageCount(String key) {
            if (!this.pageMap.containsKey(key)) {
                return 0;
            }
            return this.pageMap.get(key).size();
        }

        public Set getPages(String key) {
            if (!this.pageMap.containsKey(key)) {
                return new TreeSet();
            }
            return this.pageMap.get(key);
        }

        private void addToMap(String key, Page page) {
            if (!this.pageMap.containsKey(key)) {
                this.pageMap.put(key, new TreeSet());
            }
            this.pageMap.get(key).add(page);
        }
    }
}

