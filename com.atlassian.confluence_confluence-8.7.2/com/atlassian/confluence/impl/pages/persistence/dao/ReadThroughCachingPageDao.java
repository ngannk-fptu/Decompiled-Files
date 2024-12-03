/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.core.bean.EntityObject
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.pages.persistence.dao;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.cache.ReadThroughEntityCache;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.DelegatingPageDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.bean.EntityObject;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ReadThroughCachingPageDao
extends DelegatingPageDao {
    private final ReadThroughCache<String, Long> pageIdCache;

    ReadThroughCachingPageDao(PageDaoInternal delegateDao, ReadThroughCache<String, Long> pageIdCache) {
        super(delegateDao);
        this.pageIdCache = pageIdCache;
    }

    public static ReadThroughCachingPageDao create(PageDaoInternal delegateDao, CacheFactory cacheFactory) {
        return new ReadThroughCachingPageDao(delegateDao, ReadThroughAtlassianCache.create(cacheFactory, CoreCache.PAGE_ID_BY_SPACE_KEY_AND_TITLE));
    }

    @Override
    public Page getPage(Space space, String pageTitle) {
        return this.loadPage(space, pageTitle, (x$0, x$1) -> super.getPage((Space)x$0, (String)x$1), x$0 -> super.getPageById((long)x$0));
    }

    @Override
    public Page getPageWithComments(Space space, String pageTitle) {
        return this.loadPage(space, pageTitle, (x$0, x$1) -> super.getPageWithComments((Space)x$0, (String)x$1), x$0 -> super.getPageByIdWithComments((long)x$0));
    }

    private Page loadPage(Space space, String pageTitle, BiFunction<Space, String, Page> getBySpaceAndTitle, Function<Long, Page> getById) {
        if (space == null) {
            return null;
        }
        String cacheKey = ReadThroughCachingPageDao.cacheKey(space.getKey(), pageTitle);
        Predicate<Page> pageTester = page -> page.isCurrent() && page.getTitle().equals(pageTitle) && page.getSpaceKey().equals(space.getKey());
        return ReadThroughEntityCache.forConfluenceEntityObjects(this.pageIdCache, getById).get(cacheKey, () -> (Page)getBySpaceAndTitle.apply(space, pageTitle), pageTester);
    }

    private static String cacheKey(String spaceKey, String title) {
        return spaceKey + "-" + title;
    }

    static String cacheKey(Page page) {
        return ReadThroughCachingPageDao.cacheKey(page.getSpaceKey(), page.getTitle());
    }

    @Override
    public void remove(EntityObject object) {
        super.remove(object);
        if (object instanceof Page) {
            this.removeFromCache((Page)object);
        }
    }

    @Override
    public void removeEntity(Page objectToRemove) {
        super.removeEntity(objectToRemove);
        this.removeFromCache(objectToRemove);
    }

    @Override
    public void save(EntityObject objectToSave, EntityObject originalObject) {
        super.save(objectToSave, originalObject);
        if (objectToSave instanceof Page) {
            this.removeFromCache((Page)objectToSave);
        }
    }

    @Override
    public void saveEntity(Page currentObject, @Nullable Page originalObject) {
        super.saveEntity(currentObject, originalObject);
        this.removeFromCache(currentObject);
    }

    @Override
    public void save(EntityObject objectToSave) {
        super.save(objectToSave);
        if (objectToSave instanceof Page) {
            this.removeFromCache((Page)objectToSave);
        }
    }

    @Override
    public void saveEntity(Page objectToSave) {
        super.saveEntity(objectToSave);
        this.removeFromCache(objectToSave);
    }

    private void removeFromCache(Page page) {
        this.pageIdCache.remove(ReadThroughCachingPageDao.cacheKey(page));
    }

    @Override
    public void saveRaw(EntityObject objectToSave) {
        super.saveRaw(objectToSave);
        if (objectToSave instanceof Page) {
            this.removeFromCache((Page)objectToSave);
        }
    }

    @Override
    public void saveRawEntity(Page objectToSave) {
        super.saveRawEntity(objectToSave);
        this.removeFromCache(objectToSave);
    }
}

