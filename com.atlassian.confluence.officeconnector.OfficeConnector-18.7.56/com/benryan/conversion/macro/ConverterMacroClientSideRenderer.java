/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 */
package com.benryan.conversion.macro;

import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.benryan.conversion.macro.ConverterMacroRenderer;
import com.benryan.conversion.macro.MacroParameters;
import java.util.Collections;

class ConverterMacroClientSideRenderer
implements ConverterMacroRenderer {
    private final TemplateRenderer templateRenderer;
    private final MacroParameters macroParameters;

    public ConverterMacroClientSideRenderer(TemplateRenderer templateRenderer, MacroParameters macroParameters) {
        this.templateRenderer = templateRenderer;
        this.macroParameters = macroParameters;
    }

    @Override
    public void render(Appendable output) {
        this.templateRenderer.renderTo(output, "com.atlassian.confluence.extra.officeconnector:converter-macro-soy-templates", "Confluence.OfficeConnector.ConverterMacro.ajaxContainer.soy", Collections.singletonMap("macroParameters", this.macroParameters.toJson().toString()));
    }
}

