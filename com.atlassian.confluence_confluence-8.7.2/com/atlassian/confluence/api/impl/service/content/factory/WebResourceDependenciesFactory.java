/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.HtmlString
 *  com.atlassian.confluence.api.model.content.webresource.ResourceType
 *  com.atlassian.confluence.api.model.content.webresource.SuperBatchWebResources
 *  com.atlassian.confluence.api.model.content.webresource.SuperBatchWebResources$SuperBatchWebResourcesBuilder
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies$WebResourceDependenciesBuilder
 *  com.atlassian.confluence.api.model.content.webresource.WebResourcesBuilder
 *  com.atlassian.plugin.webresource.CssWebResource
 *  com.atlassian.plugin.webresource.JavascriptWebResource
 *  com.atlassian.plugin.webresource.WebResourceFormatter
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.WebResource
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResource
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.io.output.StringBuilderWriter
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.HtmlString;
import com.atlassian.confluence.api.model.content.webresource.ResourceType;
import com.atlassian.confluence.api.model.content.webresource.SuperBatchWebResources;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.content.webresource.WebResourcesBuilder;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceService;
import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.WebResourceFormatter;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.WebResource;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import com.atlassian.webresource.api.assembler.resource.PluginJsResource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.data.PluginDataResource;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.io.output.StringBuilderWriter;

public class WebResourceDependenciesFactory {
    private static final ImmutableMap<Class<? extends WebResource>, ResourceType> TYPES = ImmutableMap.of(PluginDataResource.class, (Object)ResourceType.DATA, PluginCssResource.class, (Object)ResourceType.CSS, PluginJsResource.class, (Object)ResourceType.JS);
    private static final ImmutableMap<ResourceType, WebResourceFormatter> FORMATTERS = ImmutableMap.of((Object)ResourceType.CSS, (Object)new CssWebResource(), (Object)ResourceType.JS, (Object)new JavascriptWebResource());
    private static final UrlMode DEFAULT_URL_MODE = UrlMode.valueOf((String)System.getProperty("webresource.expansions.urlmode", UrlMode.ABSOLUTE.name()));
    private final ConfluenceWebResourceService confluenceWebResourceService;
    private final ContextPathHolder contextPathHolder;

    public WebResourceDependenciesFactory(ConfluenceWebResourceService confluenceWebResourceService, ContextPathHolder contextPathHolder) {
        this.confluenceWebResourceService = confluenceWebResourceService;
        this.contextPathHolder = contextPathHolder;
    }

    public WebResourceDependencies build(WebResourceDependenciesRecorder.RecordedResources recorded, Expansions wrExpansion) {
        WebResourceDependencies.WebResourceDependenciesBuilder builder = WebResourceDependencies.builder();
        Supplier<WebResourceSet> webResourcesSet = recorded.webresources();
        if (wrExpansion.canExpand("contexts")) {
            builder.contexts(recorded.contexts());
        }
        if (wrExpansion.canExpand("keys")) {
            builder.keys(recorded.resourceKeys());
        }
        if (wrExpansion.canExpand("tags")) {
            this.addTags((WebResourcesBuilder)builder, webResourcesSet, wrExpansion);
        }
        if (wrExpansion.canExpand("uris")) {
            this.addUris((WebResourcesBuilder)builder, webResourcesSet, wrExpansion);
        }
        if (wrExpansion.canExpand("superbatch")) {
            this.addSuperbatchResources(builder, recorded, wrExpansion.getSubExpansions("superbatch"));
        }
        return builder.build();
    }

    private void addSuperbatchResources(WebResourceDependencies.WebResourceDependenciesBuilder builder, WebResourceDependenciesRecorder.RecordedResources recorded, Expansions wrExpansion) {
        SuperBatchWebResources.SuperBatchWebResourcesBuilder superbatchBuilder = SuperBatchWebResources.builder();
        Supplier<WebResourceSet> superbatchResource = recorded.superbatch();
        if (wrExpansion.canExpand("tags")) {
            this.addTags((WebResourcesBuilder)superbatchBuilder, superbatchResource, wrExpansion);
        }
        if (wrExpansion.canExpand("uris")) {
            this.addUris((WebResourcesBuilder)superbatchBuilder, superbatchResource, wrExpansion);
        }
        if (wrExpansion.canExpand("metatags")) {
            this.addMetatags(superbatchBuilder);
        }
        builder.superbatch(superbatchBuilder.build());
    }

    private void addMetatags(SuperBatchWebResources.SuperBatchWebResourcesBuilder superbatchBuilder) {
        superbatchBuilder.metatags(new HtmlString("<meta name='ajs-context-path' content='" + this.contextPathHolder.getContextPath() + "'>"));
    }

    private void addUris(WebResourcesBuilder builder, Supplier<WebResourceSet> webResourcesSet, Expansions wrExpansion) {
        Expansions uriExpand = wrExpansion.getSubExpansions("uris");
        Map<ResourceType, List<PluginUrlResource>> resourcesByType = StreamSupport.stream(webResourcesSet.get().getResources().spliterator(), false).filter(r -> r instanceof PluginUrlResource && this.isResourceTypeExpandable((WebResource)r, uriExpand)).map(PluginUrlResource.class::cast).filter(r -> Strings.isNullOrEmpty((String)r.getParams().conditionalComment())).collect(Collectors.groupingBy(r -> (ResourceType)TYPES.entrySet().stream().filter(type -> ((Class)type.getKey()).isAssignableFrom(r.getClass())).findFirst().get().getValue(), Collectors.mapping(PluginUrlResource.class::cast, Collectors.toList())));
        this.addUrisAll(builder, uriExpand, resourcesByType);
        this.addUrisByType(builder, uriExpand, resourcesByType);
    }

    private void addUrisByType(WebResourcesBuilder builder, Expansions uriExpand, Map<ResourceType, List<PluginUrlResource>> resourcesByType) {
        for (Map.Entry tagType : TYPES.entrySet()) {
            ResourceType type = (ResourceType)tagType.getValue();
            if (uriExpand.canExpand(type.serialise()) && resourcesByType.containsKey(type)) {
                builder.uris(type, resourcesByType.get(type).stream().map(r -> URI.create(r.getStaticUrl(DEFAULT_URL_MODE))).collect(Collectors.toList()));
                continue;
            }
            builder.addCollapsedUris(type);
        }
    }

    private void addUrisAll(WebResourcesBuilder builder, Expansions uriExpand, Map<ResourceType, List<PluginUrlResource>> resourcesByType) {
        if (uriExpand.canExpand(ResourceType.ALL.serialise())) {
            builder.uris(ResourceType.ALL, resourcesByType.values().stream().flatMap(rs -> rs.stream().map(r -> URI.create(r.getStaticUrl(DEFAULT_URL_MODE)))).collect(Collectors.toList()));
        } else {
            builder.addCollapsedUris(ResourceType.ALL);
        }
    }

    private void addTags(WebResourcesBuilder builder, Supplier<WebResourceSet> webResourcesSet, Expansions wrExpansion) {
        Expansions tagExpansions = wrExpansion.getSubExpansions("tags");
        Map<ResourceType, Iterable<String>> customUris = this.confluenceWebResourceService.calculateConfluenceResourceUris(null, null);
        this.addTagsAll(builder, tagExpansions, webResourcesSet, customUris);
        this.addTagsByType(builder, tagExpansions, webResourcesSet, customUris);
    }

    private void addTagsByType(WebResourcesBuilder builder, Expansions tagExpansions, Supplier<WebResourceSet> webResourcesSet, Map<ResourceType, Iterable<String>> customUris) {
        for (Map.Entry tagType : TYPES.entrySet()) {
            ResourceType tag = (ResourceType)tagType.getValue();
            Class tagClass = (Class)tagType.getKey();
            if (tagExpansions.canExpand(tag.serialise())) {
                StringBuilderWriter writer = new StringBuilderWriter();
                webResourcesSet.get().writeHtmlTags((Writer)writer, DEFAULT_URL_MODE, t -> tagClass.isAssignableFrom(t.getClass()));
                for (String uri : customUris.get(tag)) {
                    writer.append((CharSequence)this.getFormatter(tag).formatResource(uri, Collections.emptyMap()));
                }
                builder.tag(tag, new HtmlString(writer.toString()));
                continue;
            }
            builder.addCollapsedTag(tag);
        }
    }

    private void addTagsAll(WebResourcesBuilder builder, Expansions tagExpansions, Supplier<WebResourceSet> webResourcesSet, Map<ResourceType, Iterable<String>> customUris) {
        if (tagExpansions.canExpand(ResourceType.ALL.serialise())) {
            try (StringBuilderWriter writer = new StringBuilderWriter();){
                for (Map.Entry tagType : TYPES.entrySet()) {
                    ResourceType type = (ResourceType)tagType.getValue();
                    Class tagClass = (Class)tagType.getKey();
                    webResourcesSet.get().writeHtmlTags((Writer)writer, DEFAULT_URL_MODE, t -> tagClass.isAssignableFrom(t.getClass()));
                    for (String uri : customUris.get(type)) {
                        writer.append((CharSequence)this.getFormatter(type).formatResource(uri, Collections.emptyMap()));
                    }
                }
                builder.tag(ResourceType.ALL, new HtmlString(writer.toString()));
            }
        } else {
            builder.addCollapsedTag(ResourceType.ALL);
        }
    }

    private boolean isResourceTypeExpandable(WebResource resource, Expansions uriExpand) {
        return TYPES.entrySet().stream().map(type -> (uriExpand.canExpand(((ResourceType)type.getValue()).serialise()) || uriExpand.canExpand(ResourceType.ALL.serialise()) && !uriExpand.canExpand(ResourceType.DATA.serialise())) && ((Class)type.getKey()).isAssignableFrom(resource.getClass())).reduce(false, Boolean::logicalOr);
    }

    private WebResourceFormatter getFormatter(ResourceType tag) {
        return FORMATTERS.containsKey((Object)tag) ? (WebResourceFormatter)FORMATTERS.get((Object)tag) : new NoopFormatter();
    }

    private static final class NoopFormatter
    implements WebResourceFormatter {
        private NoopFormatter() {
        }

        public String formatResource(String url, Map<String, String> parameters) {
            return "";
        }

        public boolean matches(String resourceName) {
            return false;
        }
    }
}

