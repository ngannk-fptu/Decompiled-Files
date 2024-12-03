/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.content.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public interface CustomContentDao
extends ContentEntityObjectDao<CustomContentEntityObject> {
    public <T> Iterator<T> findByQuery(ContentQuery<T> var1, int var2, int var3);

    public <T> List<T> queryForList(ContentQuery<T> var1, int var2, int var3);

    public <T> List<T> queryForList(ContentQuery<T> var1);

    public <T> List<T> findByQuery(ContentQuery<T> var1, boolean var2, LimitedRequest var3);

    @Deprecated
    default public <T> PageResponse<T> findByQuery(ContentQuery<T> query, boolean cacheable, LimitedRequest request, com.google.common.base.Predicate<T> predicate) {
        return PageResponseImpl.filteredResponse((LimitedRequest)request, this.findByQuery(query, cacheable, request), predicate);
    }

    @Deprecated
    default public <T> PageResponse<T> findByQueryAndFilter(ContentQuery<T> query, boolean cacheable, LimitedRequest request, Predicate<T> predicate) {
        return this.findByQuery(query, cacheable, request, predicate::test);
    }

    public int findTotalInSpace(long var1, String var3);

    public Iterator<CustomContentEntityObject> findCurrentInSpace(long var1, String var3, int var4, int var5, CustomContentManager.SortField var6, CustomContentManager.SortOrder var7);

    public Iterator<CustomContentEntityObject> findAllInSpaceWithAttachments(String var1, long var2);

    public Iterator<CustomContentEntityObject> findAllInSpace(String var1, long var2);

    public Iterator<CustomContentEntityObject> findAllInSpaceWithAttachments(long var1);

    public Iterator<CustomContentEntityObject> findAllInSpace(long var1);

    public Iterator<CustomContentEntityObject> findAll(String var1);

    public Iterator<CustomContentEntityObject> findAllWithAttachments(String var1);

    public Iterator<CustomContentEntityObject> findAllChildren(long var1);

    public Iterator<CustomContentEntityObject> findChildrenOfType(long var1, String var3, int var4, int var5, CustomContentManager.SortField var6, CustomContentManager.SortOrder var7);

    public long countChildrenOfType(long var1, String var3);

    public Iterator<CustomContentEntityObject> findAllContainedOfType(long var1, String var3);
}

