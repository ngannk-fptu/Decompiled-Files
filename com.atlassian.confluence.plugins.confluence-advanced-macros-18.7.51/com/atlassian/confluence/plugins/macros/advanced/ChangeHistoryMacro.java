/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.datetime.DateFormatterFactory
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.util.synchrony.SynchronyConfigurationReader
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.synchrony.SynchronyConfigurationReader;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeHistoryMacro
extends BaseMacro {
    private static final String TEMPLATE_NAME = "pages/includes/previous-versions-table.vm";
    private static final Logger logger = LoggerFactory.getLogger((String)ChangeHistoryMacro.class.getName());
    private final PageManager pageManager;
    private final DateFormatterFactory dateFormatterFactory;
    private final SynchronyConfigurationReader synchronyConfigurationReader;

    public ChangeHistoryMacro(PageManager pageManager, DateFormatterFactory dateFormatterFactory, SynchronyConfigurationReader synchronyConfigurationReader) {
        this.pageManager = pageManager;
        this.dateFormatterFactory = dateFormatterFactory;
        this.synchronyConfigurationReader = synchronyConfigurationReader;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        PageContext context = (PageContext)renderContext;
        ContentEntityObject ceo = context.getEntity();
        if (ceo instanceof AbstractPage) {
            AbstractPage page = (AbstractPage)ceo;
            List pageVersions = this.pageManager.getVersionHistorySummaries((ContentEntityObject)page);
            int limit = this.determineMacroLimit(parameters, page, pageVersions.size());
            List limitedVersions = pageVersions.subList(0, limit);
            contextMap.put("page", page);
            contextMap.put("allVersions", limitedVersions);
            contextMap.put("space", page.getSpace());
            contextMap.put("dateFormatter", this.dateFormatterFactory.createForUser());
            contextMap.put("offlineCollabEditingMode", this.isOfflineCollabEditingMode());
            contextMap.put("showFullPageHistoryLink", pageVersions.size() != limitedVersions.size());
            return this.getRenderedTemplate(contextMap);
        }
        return RenderUtils.blockError((String)this.getConfluenceActionSupportTextStatic(), (String)"");
    }

    private int determineMacroLimit(Map parameters, AbstractPage page, int totalVersions) {
        Integer limit = this.parseLimitParameter(parameters, page);
        if (limit == null || limit < 0) {
            return totalVersions;
        }
        return Math.min(totalVersions, limit);
    }

    private Integer parseLimitParameter(Map parameters, AbstractPage page) {
        try {
            return Optional.ofNullable(parameters.get("limit")).map(String.class::cast).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null);
        }
        catch (NumberFormatException e) {
            logger.warn("Error occurred during parsing 'limit' macro parameter on page {}: {}", (Object)page.getId(), (Object)e.getMessage());
            return null;
        }
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

    public boolean isInline() {
        return false;
    }

    private boolean isOfflineCollabEditingMode() {
        return !this.synchronyConfigurationReader.isSharedDraftsEnabled() && !this.synchronyConfigurationReader.isSynchronyEnabled();
    }

    protected String getConfluenceActionSupportTextStatic() {
        return ConfluenceActionSupport.getTextStatic((String)"changehistory.error.can-only-be-used-in-pages-or-blogposts");
    }

    protected String getRenderedTemplate(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)TEMPLATE_NAME, contextMap);
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }
}

