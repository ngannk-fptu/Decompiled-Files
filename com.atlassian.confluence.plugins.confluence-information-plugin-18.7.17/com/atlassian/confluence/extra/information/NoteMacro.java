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

public class NoteMacro
extends AbstractInformationMacro {
    public NoteMacro(PageBuilderService pageBuilderService, TemplateRenderer templateRenderer) {
        super(pageBuilderService, templateRenderer);
    }

    @Override
    protected String getCssClass() {
        return "confluence-information-macro-note";
    }

    @Override
    protected String getAuiMessageClass() {
        return "aui-message-warning";
    }

    @Override
    protected String getAuiIconClass() {
        return "aui-iconfont-warning";
    }
}

