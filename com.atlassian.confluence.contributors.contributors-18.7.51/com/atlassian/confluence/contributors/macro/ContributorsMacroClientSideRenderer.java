/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.contributors.macro.ContributorsMacroRenderer;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ContributorsMacroClientSideRenderer
implements ContributorsMacroRenderer {
    private final TemplateRenderer templateRenderer;

    @Autowired
    public ContributorsMacroClientSideRenderer(@ComponentImport TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void render(Appendable output, MacroParameterModel macroParameterModel) {
        this.templateRenderer.renderTo(output, "com.atlassian.confluence.contributors:soy-templates", "Confluence.ContributorsMacro.ajaxContainer.soy", Collections.singletonMap("macroParameters", macroParameterModel.toJson().toString()));
    }
}

