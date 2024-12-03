/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface CustomContentManager
extends ContentEntityManager {
    @Override
    public void saveContentEntity(ContentEntityObject var1, @Nullable ContentEntityObject var2, @Nullable SaveContext var3);

    public @NonNull CustomContentEntityObject newPluginContentEntityObject(String var1);

    public @NonNull CustomContentEntityObject updatePluginModuleKey(CustomContentEntityObject var1, String var2);

    @Override
    public @Nullable CustomContentEntityObject getById(long var1);

    public <T> @NonNull Iterator<T> findByQuery(ContentQuery<T> var1, int var2, int var3);

    public <T> @NonNull List<T> queryForList(ContentQuery<T> var1, int var2, int var3);

    public <T> @NonNull List<T> queryForList(ContentQuery<T> var1);

    default public <T> @NonNull PageResponse<T> findByQuery(ContentQuery<T> query, LimitedRequest request, Predicate<T> predicate) {
        return this.findByQueryAndFilter(query, true, request, predicate);
    }

    @Deprecated
    default public <T> @NonNull PageResponse<T> findByQuery(ContentQuery<T> query, boolean cacheable, LimitedRequest request, com.google.common.base.Predicate<T> predicate) {
        return this.findByQueryAndFilter(query, cacheable, request, (Predicate<T>)predicate);
    }

    @Deprecated
    public <T> @NonNull PageResponse<T> findByQueryAndFilter(ContentQuery<T> var1, boolean var2, LimitedRequest var3, Predicate<T> var4);

    public <T> @Nullable T findFirstObjectByQuery(ContentQuery<T> var1);

    public int findTotalInSpace(Space var1, String var2);

    public @NonNull Iterator<CustomContentEntityObject> findCurrentInSpace(Space var1, String var2, int var3, int var4, SortField var5, SortOrder var6);

    public long countChildrenOfType(CustomContentEntityObject var1, String var2);

    public @NonNull Iterator<CustomContentEntityObject> findAllContainedOfType(long var1, String var3);

    public @NonNull Iterator<CustomContentEntityObject> findChildrenOfType(CustomContentEntityObject var1, String var2, int var3, int var4, SortField var5, SortOrder var6);

    @Deprecated
    default public @NonNull PageResponse<Content> getChildrenOfType(ContentEntityObject entity, String contentModuleKey, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super CustomContentEntityObject> predicate) {
        return this.getChildrenOfTypeAndFilter(entity, contentModuleKey, limitedRequest, expansions, depth, (Predicate<? super CustomContentEntityObject>)predicate);
    }

    public @NonNull PageResponse<Content> getChildrenOfTypeAndFilter(ContentEntityObject var1, String var2, LimitedRequest var3, Expansions var4, Depth var5, Predicate<? super CustomContentEntityObject> var6);

    public @NonNull Iterator<CustomContentEntityObject> findAllChildren(CustomContentEntityObject var1);

    public void removeAllInSpace(String var1, Space var2);

    public void removeAllPluginContentInSpace(Space var1);

    public void removeAllPluginContent(String var1);

    @Deprecated
    public @NonNull Collection<CustomContentEntityObject> findAllInSpace(Space var1);

    public static enum SortOrder {
        ASC,
        DESC;

    }

    public static enum SortField {
        TITLE,
        CREATED,
        MODIFIED;

    }
}

