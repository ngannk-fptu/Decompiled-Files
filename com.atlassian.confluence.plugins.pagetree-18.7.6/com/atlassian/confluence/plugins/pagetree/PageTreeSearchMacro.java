/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.pagetree;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTreeSearchMacro
extends BaseMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(PageTreeSearchMacro.class);
    public static final String ROOTPAGE_PARAM = "rootPage";
    public static final String ROOTPAGE_PARAM_ALIAS = "rootpage";
    private PageManager pageManager;
    private SpaceManager spaceManager;
    private SettingsManager settingsManager;
    private WebResourceManager webResourceManager;

    public String execute(Map paramaters, String body, RenderContext renderContext) {
        return this.execute((Map<String, String>)paramaters, body, (ConversionContext)new DefaultConversionContext(renderContext));
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

    public PageManager getPageManager() {
        return this.pageManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setWebResourceManager(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    protected String getRenderedTemplateWithoutSwallowingErrors(Map velocityContext) throws Exception {
        return VelocityUtils.getRenderedTemplateWithoutSwallowingErrors((String)"vm/search.vm", (Map)velocityContext);
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        ArrayList<String> errors = new ArrayList<String>();
        String rootPageString = parameters.get(ROOTPAGE_PARAM);
        if (rootPageString == null) {
            rootPageString = parameters.get(ROOTPAGE_PARAM_ALIAS);
        }
        if (rootPageString == null) {
            rootPageString = parameters.get("0");
        }
        Page rootPage = null;
        String spaceKey = null;
        String pageName = null;
        if (rootPageString != null) {
            int delimeterPos = rootPageString.indexOf(":");
            if (delimeterPos > -1) {
                spaceKey = rootPageString.substring(0, delimeterPos);
            }
            if (rootPageString.length() > delimeterPos) {
                pageName = rootPageString.substring(delimeterPos + 1);
            }
            if (StringUtils.isEmpty((CharSequence)spaceKey)) {
                spaceKey = conversionContext.getSpaceKey();
            } else if (this.spaceManager.getSpace(spaceKey) == null) {
                errors.add("pagetreesearch.rootspace.notfound");
            }
            if (!StringUtils.isEmpty((CharSequence)pageName) && (rootPage = this.pageManager.getPage(spaceKey, pageName)) == null) {
                errors.add("pagetreesearch.rootpage.notfound");
            }
        } else {
            ContentEntityObject contentObject = conversionContext.getEntity();
            if (contentObject instanceof Page) {
                spaceKey = conversionContext.getSpaceKey();
                rootPage = (Page)contentObject;
            } else {
                errors.add("pagetreesearch.rootpage.invalidpage");
            }
        }
        Map<String, Object> velocityContext = this.getDefaultVelocityContext();
        velocityContext.put(ROOTPAGE_PARAM, rootPage);
        velocityContext.put("spaceKey", spaceKey);
        velocityContext.put("errors", errors);
        velocityContext.put("outputType", conversionContext.getOutputType());
        velocityContext.put("baseUrl", this.settingsManager.getGlobalSettings().getBaseUrl());
        boolean mobile = false;
        if ("mobile".equals(conversionContext.getOutputDeviceType())) {
            this.webResourceManager.requireResourcesForContext("atl.confluence.plugins.pagetree-mobile");
            mobile = true;
        } else {
            this.webResourceManager.requireResourcesForContext("atl.confluence.plugins.pagetree-desktop");
        }
        velocityContext.put("mobile", mobile);
        try {
            return this.getRenderedTemplateWithoutSwallowingErrors(velocityContext);
        }
        catch (Exception e) {
            log.error("Error happened", (Throwable)e);
            return "";
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

