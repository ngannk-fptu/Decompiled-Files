/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.contributors.analytics.ContributorsMacroMetricsEvent;
import com.atlassian.confluence.contributors.macro.ContributorsMacroHelper;
import com.atlassian.confluence.contributors.macro.ContributorsMacroRenderer;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

class ContributorsMacroServerSideRenderer
implements ContributorsMacroRenderer {
    private final TemplateRenderer templateRenderer;
    private final ContributorsMacroHelper macroHelper;
    private final EventPublisher eventPublisher;

    public ContributorsMacroServerSideRenderer(@ComponentImport TemplateRenderer templateRenderer, ContributorsMacroHelper macroHelper, @ComponentImport EventPublisher eventPublisher) {
        this.templateRenderer = templateRenderer;
        this.macroHelper = macroHelper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void render(Appendable output, MacroParameterModel parameterModel) throws IOException {
        ContributorsMacroMetricsEvent.Builder metrics = ContributorsMacroMetricsEvent.builder();
        Either<String, Map<String, Object>> result = this.macroHelper.getAuthorRankingsModel(metrics, parameterModel);
        result.left().foreach(errorMessage -> {
            try {
                output.append(StringEscapeUtils.escapeHtml4((String)errorMessage));
            }
            catch (IOException e) {
                throw Throwables.propagate((Throwable)e);
            }
        });
        result.right().foreach(templateModel -> {
            metrics.templateRenderStart();
            this.templateRenderer.renderTo(output, "com.atlassian.confluence.contributors:soy-templates", "Confluence.ContributorsMacro.renderContent.soy", templateModel);
            metrics.templateRenderFinish();
        });
        this.eventPublisher.publish((Object)metrics.build());
    }
}

