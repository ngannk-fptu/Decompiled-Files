/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Internal
public class SpaceCategoryQueryFactory<T> {
    private final Set<SpaceCategoryEnum> spaceCategories;
    private final LabelManager labelManager;
    private final Supplier<BooleanQueryBuilder<T>> boolBuilderSupplier;
    private final Supplier<BiFunction<String, String, T>> termBuilderSupplier;
    private final Supplier<T> matchNoDocsSupplier;
    private final Supplier<T> matchAllDocsSupplier;

    public SpaceCategoryQueryFactory(Set<SpaceCategoryEnum> spaceCategories, LabelManager labelManager, Supplier<BooleanQueryBuilder<T>> boolBuilderSupplier, Supplier<BiFunction<String, String, T>> termBuilderSupplier, Supplier<T> matchNoDocsSupplier, Supplier<T> matchAllDocsSupplier) {
        this.spaceCategories = spaceCategories;
        this.labelManager = labelManager;
        this.boolBuilderSupplier = boolBuilderSupplier;
        this.termBuilderSupplier = termBuilderSupplier;
        this.matchNoDocsSupplier = matchNoDocsSupplier;
        this.matchAllDocsSupplier = matchAllDocsSupplier;
    }

    public T create() {
        BooleanQueryBuilder<T> builder = this.boolBuilderSupplier.get();
        block6: for (SpaceCategoryEnum category : this.spaceCategories) {
            switch (category) {
                case ALL: {
                    return this.matchAllDocsSupplier.get();
                }
                case FAVOURITES: {
                    builder.addShould(this.createFavourites());
                    continue block6;
                }
                case GLOBAL: {
                    builder.addShould(this.termBuilderSupplier.get().apply(SearchFieldNames.SPACE_TYPE, SpaceType.GLOBAL.toString()));
                    continue block6;
                }
                case PERSONAL: {
                    builder.addShould(this.termBuilderSupplier.get().apply(SearchFieldNames.SPACE_TYPE, SpaceType.PERSONAL.toString()));
                    continue block6;
                }
            }
            throw new IllegalArgumentException("Unknown space category: " + category);
        }
        return builder.build();
    }

    private T createFavourites() {
        BooleanQueryBuilder builder = this.boolBuilderSupplier.get();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (currentUser == null) {
            return this.matchNoDocsSupplier.get();
        }
        List<Space> favouriteSpaces = this.labelManager.getFavouriteSpaces(currentUser.getName());
        if (favouriteSpaces.isEmpty()) {
            return this.matchNoDocsSupplier.get();
        }
        List queries = favouriteSpaces.stream().map(x -> this.termBuilderSupplier.get().apply(SearchFieldNames.SPACE_KEY, x.getKey())).collect(Collectors.toList());
        return builder.addShould(queries).build();
    }
}

