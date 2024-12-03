/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.ModelListBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.ContentConvertible
 *  com.atlassian.confluence.pages.TinyUrl
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl.factory;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.ModelListBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.plugins.cql.impl.SearchTypeManager;
import com.atlassian.confluence.plugins.cql.impl.factory.ModelResultFactory;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentSearchResultsFactory
implements ModelResultFactory<Content> {
    private final ContentService contentService;
    private final SearchTypeManager typeManager;
    private Set<String> requiredIndexFields = new HashSet<String>(){
        {
            Collections.addAll(this, SearchFieldNames.TITLE, "display-title", SearchFieldNames.HANDLE, SearchFieldNames.TYPE, SearchFieldNames.CONTENT_STATUS, SearchFieldNames.URL_PATH, SearchFieldNames.SPACE_KEY, SearchFieldNames.SPACE_NAME, SearchFieldNames.CONTENT_VERSION, SearchFieldNames.CONTENT, "contentBody-stored", SearchFieldNames.CONTENT_PLUGIN_KEY, "content.id");
        }
    };
    private Function<SearchResult, Content> transformSearchResult = input -> {
        ContentStatus status = ContentStatus.valueOf((String)input.getStatus());
        ContentType type = this.getType((SearchResult)input);
        ContentId id = this.getId((SearchResult)input);
        Content.ContentBuilder builder = Content.builder((ContentType)this.getType((SearchResult)input)).status(status).id(this.getId((SearchResult)input)).title(input.getDisplayTitle()).addLink(LinkType.WEB_UI, input.getUrlPath());
        if (ContentType.PAGE.equals((Object)type) || ContentType.BLOG_POST.equals((Object)type)) {
            builder.addLink(LinkType.TINY_UI, "/x/" + new TinyUrl(id.asLong()).getIdentifier());
        }
        this.addCollapsed(builder, (SearchResult)input);
        return builder.build();
    };

    @Autowired
    public ContentSearchResultsFactory(@ComponentImport ContentService service, SearchTypeManager typeManager) {
        this.contentService = service;
        this.typeManager = typeManager;
    }

    @Override
    public Map<SearchResult, Content> buildFrom(Iterable<SearchResult> searchResults, Expansions expansions) {
        List<SearchResult> filteredSearchResults = StreamSupport.stream(searchResults.spliterator(), false).filter(input -> this.handles(ContentTypeEnum.getByRepresentation((String)input.getType()))).collect(Collectors.toList());
        if (!expansions.isEmpty()) {
            return this.builderFromService(filteredSearchResults, expansions);
        }
        LinkedHashMap<SearchResult, Content> contentBySearchResult = new LinkedHashMap<SearchResult, Content>();
        for (SearchResult result : filteredSearchResults) {
            contentBySearchResult.put(result, this.transformSearchResult.apply(result));
        }
        return contentBySearchResult;
    }

    private Map<SearchResult, Content> builderFromService(List<SearchResult> searchResults, Expansions expansions) {
        if (searchResults.isEmpty()) {
            return Collections.emptyMap();
        }
        Expansion[] expansion = expansions.toArray();
        ContentType[] contentTypes = (ContentType[])this.typeManager.getContentTypes().keySet().stream().map(ContentType::valueOf).toArray(ContentType[]::new);
        Iterable results = Iterables.concat(this.contentService.find(expansion).withId((Iterable)searchResults.stream().map(this::getId).collect(Collectors.toList())).withType(contentTypes).withAnyStatus().fetchMappedByContentType((PageRequest)new SimplePageRequest(0, Iterables.size(searchResults))).values());
        ImmutableMap contentById = Maps.uniqueIndex((Iterable)results, Content::getId);
        LinkedHashMap contentBySearchResult = Maps.newLinkedHashMap();
        for (SearchResult result : searchResults) {
            Content content = (Content)contentById.get(this.getId(result));
            if (content == null) continue;
            contentBySearchResult.put(result, content);
        }
        return contentBySearchResult;
    }

    @Override
    public boolean handles(ContentTypeEnum contentType) {
        return ContentConvertible.class.isAssignableFrom(contentType.getType());
    }

    @Override
    public Set<String> getRequiredIndexFields() {
        return this.requiredIndexFields;
    }

    private ContentId getId(SearchResult searchResult) {
        HibernateHandle handle = (HibernateHandle)Preconditions.checkNotNull((Object)((HibernateHandle)searchResult.getHandle()));
        ContentType type = this.getType(searchResult);
        return ContentId.of((ContentType)type, (long)handle.getId());
    }

    private ContentType getType(SearchResult searchResult) {
        String type = searchResult.getType();
        if (ContentTypeEnum.CUSTOM.toString().equalsIgnoreCase(type)) {
            type = searchResult.getField(SearchFieldNames.CONTENT_PLUGIN_KEY);
        }
        return ContentType.valueOf((String)type);
    }

    private void addCollapsed(Content.ContentBuilder builder, SearchResult input) {
        builder.history(History.buildReference((Reference)Content.buildReference((ContentSelector)ContentSelector.builder().id(this.getId(input)).build())));
        String spaceKey = input.getSpaceKey();
        Reference spaceReference = Strings.isNullOrEmpty((String)spaceKey) ? Reference.empty(Space.class) : Space.buildReference((String)spaceKey);
        builder.space(spaceReference);
        builder.container(Reference.collapsed(Container.class));
        Integer version = input.getContentVersion();
        builder.version(version == null ? Reference.collapsed(Version.class) : Version.buildReference((int)version));
        builder.ancestors((Iterable)ModelListBuilder.newInstance().build());
        builder.operations((Iterable)ModelListBuilder.newInstance().build());
        builder.children(BuilderUtils.collapsedMap());
        builder.descendants(BuilderUtils.collapsedMap());
        builder.body(BuilderUtils.collapsedMap());
        builder.metadata(BuilderUtils.collapsedMap());
        builder.extensions(BuilderUtils.collapsedMap());
    }
}

