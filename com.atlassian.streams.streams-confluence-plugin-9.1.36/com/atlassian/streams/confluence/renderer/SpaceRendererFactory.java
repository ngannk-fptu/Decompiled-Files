/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.renderer.StreamsEntryRendererFactory
 *  com.atlassian.streams.spi.renderer.Renderers
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.streams.confluence.renderer;

import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.renderer.StreamsEntryRendererFactory;
import com.atlassian.streams.confluence.ConfluenceActivityObjectTypes;
import com.atlassian.streams.spi.renderer.Renderers;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public final class SpaceRendererFactory {
    private final StreamsEntryRendererFactory rendererFactory;
    private final TemplateRenderer templateRenderer;

    SpaceRendererFactory(StreamsEntryRendererFactory rendererFactory, TemplateRenderer templateRenderer) {
        this.rendererFactory = (StreamsEntryRendererFactory)Preconditions.checkNotNull((Object)rendererFactory, (Object)"rendererFactory");
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
    }

    public StreamsEntry.Renderer newInstance(SpaceDescription space) {
        return new SpaceRenderer(space);
    }

    private final class SpaceRenderer
    implements StreamsEntry.Renderer {
        private final SpaceDescription space;

        public SpaceRenderer(SpaceDescription space) {
            this.space = space;
        }

        public Option<Html> renderContentAsHtml(StreamsEntry entry) {
            return Option.none();
        }

        public Option<Html> renderSummaryAsHtml(StreamsEntry entry) {
            return Option.none();
        }

        public Html renderTitleAsHtml(StreamsEntry entry) {
            String key = "streams.item.confluence." + (this.space.isPersonalSpace() ? ConfluenceActivityObjectTypes.personalSpace().key() : ConfluenceActivityObjectTypes.space().key()) + "." + entry.getVerb().key();
            Function titleRenderer = SpaceRendererFactory.this.rendererFactory.newTitleRenderer(key, SpaceRendererFactory.this.rendererFactory.newAuthorsRenderer(), Option.some((Object)SpaceRendererFactory.this.rendererFactory.newActivityObjectsRenderer(this.spaceObjectRenderer())), Option.none());
            return (Html)titleRenderer.apply((Object)entry);
        }

        private Function<StreamsEntry.ActivityObject, Option<Html>> spaceObjectRenderer() {
            return o -> Option.some((Object)new Html(Renderers.render((TemplateRenderer)SpaceRendererFactory.this.templateRenderer, (String)"activity-object-link-space.vm", (Map)ImmutableMap.of((Object)"activityObject", (Object)o, (Object)"isPersonalSpace", (Object)o.getActivityObjectType().equals((Object)Option.some((Object)ConfluenceActivityObjectTypes.personalSpace()))))));
        }
    }
}

