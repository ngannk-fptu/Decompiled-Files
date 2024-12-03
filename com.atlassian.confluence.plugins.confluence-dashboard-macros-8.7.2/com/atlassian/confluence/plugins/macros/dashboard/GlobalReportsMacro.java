/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.util.RendererUtil
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.util.RendererUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalReportsMacro
extends BaseMacro {
    private static final String OUTPUT_TYPE_KEY = "outputType";
    private static final String MACRO_NAME = "global-reports";
    private static final Logger log = LoggerFactory.getLogger(GlobalReportsMacro.class);
    @ComponentImport
    private VelocityHelperService velocityHelperService;

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        Map<String, String> macroParameters = GlobalReportsMacro.castMacroParams(parameters);
        String widthParameter = macroParameters.get("width");
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        if (renderContext instanceof PageContext) {
            PageContext pageContext = (PageContext)renderContext;
            contextMap.put(OUTPUT_TYPE_KEY, pageContext.getOutputType());
        }
        contextMap.put("i18NBean", this.getI18nBean());
        contextMap.put("rendererUtil", new RendererUtil());
        if (widthParameter != null) {
            contextMap.put("tableWidth", widthParameter);
        }
        contextMap.put("remoteUser", AuthenticatedUserThreadLocal.get());
        if (widthParameter != null) {
            contextMap.put("tableWidth", widthParameter);
        }
        try {
            return this.renderGlobalReports(contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to load the global reports macro template.", (Throwable)e);
            throw new MacroException((Throwable)e);
        }
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    protected I18NBean getI18nBean() {
        return GeneralUtil.getI18n();
    }

    protected String renderGlobalReports(Map contextMap) {
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/plugins/macros/dashboard/macro-globalreports.vm", contextMap);
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    public String getName() {
        return MACRO_NAME;
    }

    private static Map<String, String> castMacroParams(Map macroParams) {
        return macroParams;
    }
}

