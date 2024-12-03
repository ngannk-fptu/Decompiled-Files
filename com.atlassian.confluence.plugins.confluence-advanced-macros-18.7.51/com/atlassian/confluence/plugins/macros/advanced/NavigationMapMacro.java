/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.util.StringHyphenBean
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.StringHyphenBean;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NavigationMapMacro
extends BaseMacro {
    private static final int MAX_ENTITIES = 500;
    private CQLSearchService searchService;
    private BootstrapManager bootstrapManager;

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String string, RenderContext renderContext) throws MacroException {
        List content;
        String labelName = (String)parameters.get("0");
        if (labelName == null) {
            throw new MacroException(ConfluenceActionSupport.getTextStatic((String)"navmap.error.must-specify-label-name"));
        }
        String title = (String)parameters.get("title");
        Integer wrapAfter = this.getIntegerFromParams(parameters, "wrapAfter", 5);
        Integer cellWidth = this.getIntegerFromParams(parameters, "cellWidth", 90);
        Integer cellHeight = this.getIntegerFromParams(parameters, "cellHeight", 60);
        int hyphenateAfter = 12 + 7 * (cellWidth - 90);
        Map<String, Object> contextMap = this.getMacroVelocityContext();
        try {
            PageResponse contentEntities = this.getSearchService().searchContent(String.format("label = \"%s\" ORDER BY title", labelName), (PageRequest)new SimplePageRequest(0, 500), new Expansion[0]);
            content = new ArrayList(contentEntities.getResults());
        }
        catch (BadRequestException e) {
            content = Collections.emptyList();
            title = e.getLocalizedMessage();
        }
        StringHyphenBean hyphenBean = new StringHyphenBean(hyphenateAfter);
        List pages = content.stream().map(page -> new SimpleContent(hyphenBean.getString(page.getTitle()), this.getBootstrapManager().getWebAppContextPath() + ((Link)page.getLinks().get(LinkType.WEB_UI)).getPath())).collect(Collectors.toList());
        contextMap.put("pages", pages);
        contextMap.put("title", title);
        contextMap.put("wrapAfter", wrapAfter);
        contextMap.put("cellWidth", cellWidth);
        contextMap.put("cellHeight", cellHeight);
        return this.renderNavMap(parameters, contextMap);
    }

    protected String renderNavMap(Map parameters, Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)this.getTemplate((String)parameters.get("theme")), contextMap);
    }

    protected Map<String, Object> getMacroVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    public Integer getIntegerFromParams(Map parameters, String key, int defaultValue) {
        try {
            return Integer.valueOf((String)parameters.get(key));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @VisibleForTesting
    String getTemplate(String theme) {
        Theme themeEnum;
        try {
            themeEnum = Theme.valueOf(theme.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            themeEnum = Theme.DEFAULT;
        }
        return "/com/atlassian/confluence/plugins/macros/advanced/navmap-" + themeEnum.toString().toLowerCase() + ".vm";
    }

    private BootstrapManager getBootstrapManager() {
        if (this.bootstrapManager == null) {
            this.setBootstrapManager((BootstrapManager)BootstrapUtils.getBootstrapManager());
        }
        return this.bootstrapManager;
    }

    private CQLSearchService getSearchService() {
        if (this.searchService == null) {
            OsgiContainerManager osgiContainerManager = (OsgiContainerManager)ContainerManager.getComponent((String)"osgiContainerManager");
            this.setSearchService((CQLSearchService)osgiContainerManager.getServiceTracker(CQLSearchService.class.getName()).getService());
        }
        return this.searchService;
    }

    public void setSearchService(CQLSearchService searchService) {
        this.searchService = searchService;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    private static enum Theme {
        DEFAULT,
        HELP;

    }

    public static class SimpleContent {
        private String title;
        private String href;

        public SimpleContent(String title, String href) {
            this.title = title;
            this.href = href;
        }

        public String getTitle() {
            return this.title;
        }

        public String getHref() {
            return this.href;
        }
    }
}

