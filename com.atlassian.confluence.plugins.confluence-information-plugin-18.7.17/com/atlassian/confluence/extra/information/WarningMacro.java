/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.confluence.extra.information;

import com.atlassian.confluence.extra.information.AbstractInformationMacro;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class WarningMacro
extends AbstractInformationMacro {
    public WarningMacro(PageBuilderService pageBuilderService, TemplateRenderer templateRenderer) {
        super(pageBuilderService, templateRenderer);
    }

    @Override
    protected String getCssClass() {
        return "confluence-information-macro-warning";
    }

    @Override
    protected String getAuiMessageClass() {
        return "aui-message-error";
    }

    @Override
    protected String getAuiIconClass() {
        return "aui-iconfont-error";
    }
}

