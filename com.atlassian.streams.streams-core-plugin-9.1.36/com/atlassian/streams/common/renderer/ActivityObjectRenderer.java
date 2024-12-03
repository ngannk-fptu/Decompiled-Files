/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.common.Fold
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.streams.common.renderer;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Fold;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

final class ActivityObjectRenderer
implements Function<StreamsEntry.ActivityObject, Option<Html>> {
    private final TemplateRenderer templateRenderer;
    private final boolean withSummary;

    public ActivityObjectRenderer(TemplateRenderer templateRenderer, boolean withSummary) {
        this.templateRenderer = templateRenderer;
        this.withSummary = withSummary;
    }

    public Option<Html> apply(StreamsEntry.ActivityObject o) {
        return ActivityObjectRenderer.titleAsHtml(o).map(this.renderHtml(o));
    }

    private Function<Html, Html> renderHtml(StreamsEntry.ActivityObject o) {
        return title -> new Html(Renderers.render((TemplateRenderer)this.templateRenderer, (String)"activity-object-link.vm", (Map)ImmutableMap.of((Object)"activityObject", (Object)o, (Object)"title", (Object)title, (Object)"summary", ActivityObjectRenderer.summaryAsHtml(o), (Object)"withSummary", (Object)this.withSummary)));
    }

    public static Option<Html> titleAsHtml(StreamsEntry.ActivityObject o) {
        return (Option)Fold.foldl((Iterable)o.getTitle(), (Object)o.getTitleAsHtml(), (title, titleAsHtml) -> Option.some((Object)new Html(StringEscapeUtils.escapeHtml4((String)title))));
    }

    public static Option<Html> summaryAsHtml(StreamsEntry.ActivityObject o) {
        return o.getSummary().map(summary -> new Html(StringEscapeUtils.escapeHtml4((String)summary)));
    }
}

