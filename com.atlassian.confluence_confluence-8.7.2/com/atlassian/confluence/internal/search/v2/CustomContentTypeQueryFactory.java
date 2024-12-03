/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Internal
public class CustomContentTypeQueryFactory<T> {
    private final Set<String> pluginKeys;
    private final Supplier<BooleanQueryBuilder<T>> boolBuilderSupplier;
    private final Supplier<BiFunction<String, String, T>> termBuilderSupplier;

    public CustomContentTypeQueryFactory(Set<String> pluginKeys, Supplier<BooleanQueryBuilder<T>> boolBuilderSupplier, Supplier<BiFunction<String, String, T>> termBuilderSupplier) {
        this.pluginKeys = pluginKeys;
        this.boolBuilderSupplier = boolBuilderSupplier;
        this.termBuilderSupplier = termBuilderSupplier;
    }

    public T create() {
        Collection pluginKeyQueries = this.pluginKeys.stream().map(x -> this.termBuilderSupplier.get().apply(SearchFieldNames.CONTENT_PLUGIN_KEY, (String)x)).collect(Collectors.toList());
        return this.boolBuilderSupplier.get().addMust(this.termBuilderSupplier.get().apply(SearchFieldNames.TYPE, ContentTypeEnum.CUSTOM.getRepresentation())).addMust(this.boolBuilderSupplier.get().addShould(pluginKeyQueries).build()).build();
    }
}

