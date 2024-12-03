/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.internal.search.contentnames.v2.FieldValuesMapper;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

final class FieldValuesTransformers {
    static final Set<String> DEFAULT_FIELD_NAMES = ImmutableSet.of((Object)SearchFieldNames.CONTENT_NAME_UNSTEMMED, (Object)SearchFieldNames.URL_PATH, (Object)SearchFieldNames.TYPE, (Object)SearchFieldNames.HANDLE, (Object)SearchFieldNames.CREATION_DATE, (Object)SearchFieldNames.LAST_MODIFICATION_DATE, (Object[])new String[]{SearchFieldNames.LAST_MODIFIER, SearchFieldNames.CREATOR});
    static final Set<String> SPACED_FIELD_NAMES = ImmutableSet.builder().addAll(DEFAULT_FIELD_NAMES).add((Object)SearchFieldNames.SPACE_KEY).add((Object)SearchFieldNames.SPACE_NAME).build();
    private static Function<Function<String, String>, Optional<SearchResult>> fieldValuesMapper = new FieldValuesMapper();
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> spacedDecorator = (getFieldValue, searchResult) -> {
        searchResult.setSpaceKey((String)getFieldValue.apply(SearchFieldNames.SPACE_KEY));
        searchResult.setSpaceName((String)getFieldValue.apply(SearchFieldNames.SPACE_NAME));
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> attachmentDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.ATTACHMENTS);
        searchResult.setPreviewKey((String)getFieldValue.apply(SearchFieldNames.ATTACHMENT_MIME_TYPE));
        searchResult.setSpaceKey((String)getFieldValue.apply(SearchFieldNames.SPACE_KEY));
        searchResult.setSpaceName((String)getFieldValue.apply(SearchFieldNames.SPACE_NAME));
        ContentTypeEnum type = ContentTypeEnum.getByClassName((String)getFieldValue.apply(SearchFieldNames.ATTACHMENT_OWNER_CONTENT_TYPE));
        searchResult.setOwnerType(type == null ? null : type.getRepresentation());
        searchResult.setParentTitle((String)getFieldValue.apply(SearchFieldNames.ATTACHMENT_OWNER_REAL_TITLE));
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> blogDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.BLOGS);
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> contentDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.CONTENT);
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> customDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.CUSTOM);
        searchResult.setContentPluginKey((String)getFieldValue.apply(SearchFieldNames.CONTENT_PLUGIN_KEY));
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> pageDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.PAGES);
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> peopleDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.PEOPLE);
        String username = (String)getFieldValue.apply(SearchFieldNames.USER_NAME);
        searchResult.setPreviewKey(username);
        searchResult.setUsername(username);
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> personalSpaceDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.PERSONAL_SPACE);
        return searchResult;
    };
    private static final BiFunction<Function<String, String>, SearchResult, SearchResult> spaceDecorator = (getFieldValue, searchResult) -> {
        searchResult.setCategory(Category.SPACES);
        return searchResult;
    };
    private static Function<List<Map<String, String>>, List<SearchResult>> attachmentTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(spacedDecorator, attachmentDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> blogTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(spacedDecorator, blogDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> contentTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(spacedDecorator, contentDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> customTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(customDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> pageTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(spacedDecorator, pageDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> peopleTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(peopleDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> personalSpaceTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(spacedDecorator, personalSpaceDecorator));
    private static Function<List<Map<String, String>>, List<SearchResult>> spaceTransformer = FieldValuesTransformers.listMapper(new FieldValuesMapper(spacedDecorator, spaceDecorator));

    private FieldValuesTransformers() {
    }

    private static Function<List<Map<String, String>>, List<SearchResult>> listMapper(Function<Function<String, String>, Optional<SearchResult>> mapper) {
        return list -> list.stream().map(fieldValues -> (Optional)mapper.apply(fieldValues::get)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    static Function<List<Map<String, String>>, List<SearchResult>> attachmentTransformer() {
        return attachmentTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> blogTransformer() {
        return blogTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> contentTransformer() {
        return contentTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> customTransformer() {
        return customTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> pageTransformer() {
        return pageTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> peopleTransformer() {
        return peopleTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> personalSpaceTransformer() {
        return personalSpaceTransformer;
    }

    static Function<List<Map<String, String>>, List<SearchResult>> spaceTransformer() {
        return spaceTransformer;
    }

    public static Function<Function<String, String>, Optional<SearchResult>> fieldValuesMapper() {
        return fieldValuesMapper;
    }
}

