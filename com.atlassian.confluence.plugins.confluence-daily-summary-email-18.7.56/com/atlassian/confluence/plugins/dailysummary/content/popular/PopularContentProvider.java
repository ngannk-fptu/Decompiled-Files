/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.storage.InlineTasksUtils
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.content.render.xhtml.storage.InlineTasksUtils;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.plugins.dailysummary.content.SummaryEmailPanelData;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentContext;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.activation.DataSource;

public class PopularContentProvider
implements ContextProvider {
    private final PopularContentManager popularContentManager;
    private final DataSourceFactory dataSourceFactory;
    private static final String PANEL_KEY = "com.atlassian.confluence.plugins.confluence-daily-summary-email:daily-summary-popular-content";
    public static final int NUM_RESULTS = 3;

    public PopularContentProvider(PopularContentManager popularContentManager, @ComponentImport DataSourceFactory dataSourceFactory) {
        this.popularContentManager = popularContentManager;
        this.dataSourceFactory = dataSourceFactory;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        PopularContentContext popularContentContext = new PopularContentContext(context);
        context.putAll(MacroUtils.defaultVelocityContext());
        String schedule = (String)context.get("summary-schedule");
        int days = 7;
        if ("daily".equals(schedule)) {
            days = 1;
        }
        SummaryEmailPanelData.Builder dataBuilder = SummaryEmailPanelData.builder(PANEL_KEY);
        List<PopularContentExcerptDto> popularContentExcerpts = this.popularContentManager.getPopularContent((User)context.get("summary-recipient"), (Date)context.get("summary-date"), (Space)context.get("summary-space"), 3, days);
        dataBuilder.hasContent(!popularContentExcerpts.isEmpty());
        dataBuilder.addImageDataSources(Iterables.concat((Iterable)popularContentExcerpts.stream().map(input -> input.getImageDataSources().values()).collect(Collectors.toList())));
        DataSource commentDatasource = this.dataSourceFactory.getServletContainerResource("/images/icons/contenttypes/comment_16.png", "icon-comment");
        dataBuilder.addImageDataSource(commentDatasource);
        DataSource likeDatasource = this.dataSourceFactory.getServletContainerResource("/images/icons/like_16.png", "icon-like");
        dataBuilder.addImageDataSource(likeDatasource);
        dataBuilder.addImageDataSources(InlineTasksUtils.getRequiredResources((DataSourceFactory)this.dataSourceFactory, (String)PopularContentProvider.concatenate(popularContentExcerpts)));
        ((List)context.get("summary-panel-data")).add(dataBuilder.build());
        popularContentContext.setPopularContentExcerpts(popularContentExcerpts);
        popularContentContext.setCommentIconDatasource(commentDatasource);
        popularContentContext.setLikeIconDatasource(likeDatasource);
        return context;
    }

    private static String concatenate(List<PopularContentExcerptDto> popularContentExcerpts) {
        StringBuilder sb = new StringBuilder();
        for (PopularContentExcerptDto excerptDto : popularContentExcerpts) {
            sb.append(excerptDto.getExcerptHtml());
        }
        return sb.toString();
    }
}

