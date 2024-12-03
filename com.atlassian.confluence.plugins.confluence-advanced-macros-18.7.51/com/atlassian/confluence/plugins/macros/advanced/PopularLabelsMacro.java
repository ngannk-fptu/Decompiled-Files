/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.actions.RankedNameComparator
 *  com.atlassian.confluence.labels.actions.RankedRankComparator
 *  com.atlassian.confluence.labels.dto.LiteLabelSearchResult
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.actions.RankedNameComparator;
import com.atlassian.confluence.labels.actions.RankedRankComparator;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.macros.advanced.BaseUrlHelper;
import com.atlassian.confluence.plugins.macros.advanced.label.RankedRankLiteComparator;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class PopularLabelsMacro
extends BaseMacro {
    private static final String TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/popularlabels.vm";
    private static final String TEMPLATE_NAME_FOR_LITE_LABELS = "com/atlassian/confluence/plugins/macros/advanced/popular-lite-labels.vm";
    private static final int DEFAULT_COUNT = 100;
    private final LabelManager labelManager;
    private final SettingsManager settingsManager;
    private final VelocityHelperService velocityHelperService;
    private static final boolean DISABLE_POPULAR_MACRO_CACHE = Boolean.getBoolean("confluence.popular-label-macro.disable-cache");

    public PopularLabelsMacro(LabelManager labelManager, VelocityHelperService velocityHelperService, SettingsManager settingsManager) {
        this.labelManager = labelManager;
        this.velocityHelperService = velocityHelperService;
        this.settingsManager = settingsManager;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        String spaceKey = (String)parameters.get("spaceKey");
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            contextMap.put("spaceKey", spaceKey);
        }
        String style = (String)parameters.get("style");
        contextMap.put("style", style);
        int maxResults = this.calculateLimit(parameters, 100);
        if (DISABLE_POPULAR_MACRO_CACHE) {
            RankedNameComparator labelsComparator = StringUtils.equals((CharSequence)"heatmap", (CharSequence)style) ? new RankedNameComparator() : new RankedRankComparator();
            Set labels = StringUtils.isNotEmpty((CharSequence)spaceKey) ? this.labelManager.getMostPopularLabelsWithRanksInSpace(spaceKey.trim(), maxResults, (Comparator)labelsComparator) : this.labelManager.getMostPopularLabelsWithRanks(maxResults, (Comparator)labelsComparator);
            contextMap.put("labels", labels);
            return this.velocityHelperService.getRenderedTemplate(TEMPLATE_NAME, contextMap);
        }
        RankedRankLiteComparator labelsComparator = StringUtils.equals((CharSequence)"heatmap", (CharSequence)style) ? Comparator.comparing(LiteLabelSearchResult::getName) : new RankedRankLiteComparator();
        List liteLabels = StringUtils.isNotEmpty((CharSequence)spaceKey) ? this.labelManager.getMostPopularLabelsInSpaceLite(spaceKey.trim(), maxResults) : this.labelManager.getMostPopularLabelsInSiteLite(maxResults);
        Set rankedLabels = this.labelManager.calculateRanksForLiteLabels(liteLabels, (Comparator)labelsComparator);
        String baseUrl = BaseUrlHelper.calculateBaseUrl(this.settingsManager);
        List simpleLinks = rankedLabels.stream().map(label -> new LabelLink(label.getName(), baseUrl + label.getUrlPath(spaceKey), label.getRank())).collect(Collectors.toList());
        contextMap.put("labels", simpleLinks);
        return this.velocityHelperService.getRenderedTemplate(TEMPLATE_NAME_FOR_LITE_LABELS, contextMap);
    }

    private int calculateLimit(Map parameters, int defaultValue) {
        try {
            int maxResults = Integer.parseInt((String)parameters.get("count"));
            return maxResults <= 0 ? defaultValue : maxResults;
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static class LabelLink {
        private final String name;
        private final String href;
        private final Integer rank;

        public LabelLink(String name, String href, Integer rank) {
            this.name = name;
            this.href = href;
            this.rank = rank;
        }

        public String getName() {
            return this.name;
        }

        public String getHref() {
            return this.href;
        }

        public Integer getRank() {
            return this.rank;
        }
    }
}

