/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.confluence.plugins.gatekeeper.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.gatekeeper.service.AddonGlobal;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;

public class WhoCanViewMacro
implements Macro {
    private AddonGlobal addonGlobal;
    private PageBuilderService pageBuilderService;
    private TransactionTemplate transactionTemplate;

    public WhoCanViewMacro(AddonGlobal addonGlobal, TransactionTemplate transactionTemplate, PageBuilderService pageBuilderService) {
        this.addonGlobal = addonGlobal;
        this.transactionTemplate = transactionTemplate;
        this.pageBuilderService = pageBuilderService;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        this.pageBuilderService.assembler().resources().requireContext("com.atlassian.confluence.plugins.gatekeeper.macro.who-can-view");
        PageContext pc = context.getPageContext();
        String spaceKey = pc.getSpaceKey();
        long pageId = pc.getEntity().getId();
        boolean hideAnonymous = "true".equals(parameters.get("hide-anonymous"));
        boolean ignoreRestrictions = "true".equals(parameters.get("ignore-restrictions"));
        return "<div class='who-can-view-macro' data-space-key='" + spaceKey + "' data-page-id='" + pageId + "' data-hide-anonymous='" + hideAnonymous + "' data-ignore-restrictions='" + ignoreRestrictions + "'></div>";
    }

    private String getParameter(Map<String, String> parameters, String key, String defaultValue) {
        return parameters.containsKey(key) ? parameters.get(key) : defaultValue;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

