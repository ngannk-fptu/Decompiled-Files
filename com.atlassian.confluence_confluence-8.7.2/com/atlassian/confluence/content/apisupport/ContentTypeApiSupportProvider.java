/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentTypeApiSupportProvider
implements ApiSupportProvider {
    private final List<Supplier<ContentTypeApiSupport>> bundledBindings;
    private final ContentTypeManager customContentTypeManager;
    private static Function<Supplier<ContentTypeApiSupport>, ContentTypeApiSupport> referenceResolver = Supplier::get;

    public ContentTypeApiSupportProvider(List<Supplier<ContentTypeApiSupport>> bundledBindings, ContentTypeManager customContentTypeManager) {
        this.bundledBindings = bundledBindings;
        this.customContentTypeManager = customContentTypeManager;
    }

    @Override
    public ContentTypeApiSupport getForType(ContentType type) {
        Map<ContentType, ContentTypeApiSupport> providers = this.getMap();
        if (!providers.containsKey(type)) {
            throw new IllegalArgumentException(String.format("No ContentTypeBinding found for type: %s", type));
        }
        return providers.get(type);
    }

    public Map<ContentType, ContentTypeApiSupport> getMap() {
        List<ContentTypeApiSupport> bindings = this.getList();
        HashMap bindingsMap = Maps.newHashMap();
        for (ContentTypeApiSupport binding : bindings) {
            bindingsMap.put(binding.getHandledType(), binding);
        }
        return bindingsMap;
    }

    public List<ContentTypeApiSupport> getList() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Iterators.transform(this.bundledBindings.iterator(), referenceResolver));
        builder.addAll(Iterables.transform(this.customContentTypeManager.getEnabledCustomContentTypes(), input -> input.getApiSupport()));
        return builder.build();
    }
}

