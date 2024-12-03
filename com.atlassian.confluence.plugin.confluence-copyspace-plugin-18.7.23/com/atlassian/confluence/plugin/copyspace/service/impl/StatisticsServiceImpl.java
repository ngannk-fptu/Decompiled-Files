/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.DefaultSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.plugin.copyspace.hibernate.CopySpaceContentQueryFactory;
import com.atlassian.confluence.plugin.copyspace.service.StatisticsService;
import com.atlassian.confluence.plugin.copyspace.util.ConfluenceApiUtils;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.DefaultSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="statisticsServiceImpl")
public class StatisticsServiceImpl
implements StatisticsService {
    private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);
    private static final String SPACEKEY_FIELD = "spacekey";
    private static final String PAGES_COUNT_PER_SPACE_CACHE_NAME = StatisticsService.class.getName() + ".pages.count";
    private static final String BLOGS_COUNT_PER_SPACE_CACHE_NAME = StatisticsService.class.getName() + ".blogs.count";
    private static final String COMMENTS_COUNT_PER_SPACE_CACHE_NAME = StatisticsService.class.getName() + ".comments.count";
    private static final String ATTACHMENTS_COUNT_PER_SPACE_CACHE_NAME = StatisticsService.class.getName() + ".attachments.count";
    private static final int CACHE_SIZE = Integer.getInteger("confluence.plugin.space.copy.statistics.cache.size", 1000);
    private static final int ENTITIES_THRESHOLD = Integer.getInteger("confluence.plugin.space.copy.statistics.approximation.threshold", 2000);
    private final Cache<String, Integer> pagesCountPerSpaceCache;
    private final Cache<String, Integer> blogsCountPerSpaceCache;
    private final Cache<String, Integer> commentsCountPerSpaceCache;
    private final Cache<String, Integer> attachmentsCountPerSpaceCache;
    private final CustomContentManager customContentManager;
    private final SearchManager searchManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public StatisticsServiceImpl(@ComponentImport CacheManager cacheManager, @ComponentImport CustomContentManager customContentManager, @ComponentImport SearchManager searchManager, @ComponentImport TransactionTemplate transactionTemplate) {
        this.pagesCountPerSpaceCache = this.createCache((CacheFactory)cacheManager, PAGES_COUNT_PER_SPACE_CACHE_NAME, ContentTypeEnum.PAGE);
        this.blogsCountPerSpaceCache = this.createCache((CacheFactory)cacheManager, BLOGS_COUNT_PER_SPACE_CACHE_NAME, ContentTypeEnum.BLOG);
        this.commentsCountPerSpaceCache = this.createCache((CacheFactory)cacheManager, COMMENTS_COUNT_PER_SPACE_CACHE_NAME, ContentTypeEnum.COMMENT);
        this.attachmentsCountPerSpaceCache = this.createCache((CacheFactory)cacheManager, ATTACHMENTS_COUNT_PER_SPACE_CACHE_NAME, ContentTypeEnum.ATTACHMENT);
        this.customContentManager = customContentManager;
        this.searchManager = searchManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public int getTotalAmountOfBlogs(String spaceKey) {
        return (Integer)this.blogsCountPerSpaceCache.get((Object)spaceKey);
    }

    @Override
    public int getTotalAmountOfPages(String spaceKey) {
        return (Integer)this.pagesCountPerSpaceCache.get((Object)spaceKey);
    }

    @Override
    public int getTotalAmountOfComments(String spaceKey) {
        return (Integer)this.commentsCountPerSpaceCache.get((Object)spaceKey);
    }

    @Override
    public int getTotalAmountOfAttachments(String spaceKey) {
        return (Integer)this.attachmentsCountPerSpaceCache.get((Object)spaceKey);
    }

    private Cache<String, Integer> createCache(CacheFactory cacheFactory, String cacheName, ContentTypeEnum entityType) {
        return cacheFactory.getCache(cacheName, this.createCacheLoader(entityType), new CacheSettingsBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).local().flushable().maxEntries(CACHE_SIZE).build());
    }

    private CacheLoader<String, Integer> createCacheLoader(final ContentTypeEnum entityType) {
        return new CacheLoader<String, Integer>(){

            @Nonnull
            public Integer load(@Nonnull String spaceKey) {
                return (Integer)StatisticsServiceImpl.this.transactionTemplate.execute(() -> {
                    int approximateCountOfEntities = StatisticsServiceImpl.this.getApproximateCountOfEntities(spaceKey, entityType);
                    return approximateCountOfEntities < ENTITIES_THRESHOLD ? StatisticsServiceImpl.this.getPreciseCountOfEntities(spaceKey, entityType) : approximateCountOfEntities;
                });
            }
        };
    }

    private int getPreciseCountOfEntities(String spaceKey, ContentTypeEnum entityType) {
        int total = 0;
        if (entityType == ContentTypeEnum.PAGE) {
            total = (Integer)ObjectUtils.defaultIfNull((Object)((Integer)this.customContentManager.findFirstObjectByQuery(CopySpaceContentQueryFactory.findTotalPagesCountBySpace(spaceKey))), (Object)0);
        } else if (entityType == ContentTypeEnum.BLOG) {
            total = (Integer)ObjectUtils.defaultIfNull((Object)((Integer)this.customContentManager.findFirstObjectByQuery(CopySpaceContentQueryFactory.findTotalBlogPostsCountBySpace(spaceKey))), (Object)0);
        } else if (entityType == ContentTypeEnum.ATTACHMENT) {
            total = (Integer)ObjectUtils.defaultIfNull((Object)((Integer)this.customContentManager.findFirstObjectByQuery(CopySpaceContentQueryFactory.findTotalAttachmentsCountBySpace(spaceKey))), (Object)0);
        } else if (entityType == ContentTypeEnum.COMMENT) {
            total = (Integer)ObjectUtils.defaultIfNull((Object)((Integer)this.customContentManager.findFirstObjectByQuery(CopySpaceContentQueryFactory.findTotalCommentsCountBySpace(spaceKey))), (Object)0);
        }
        return total;
    }

    private int getApproximateCountOfEntities(String spaceKey, ContentTypeEnum entityType) {
        TextFieldQuery spaceKeyQuery = new TextFieldQuery(SPACEKEY_FIELD, spaceKey, BooleanOperator.AND);
        ContentTypeQuery contentTypeQuery = new ContentTypeQuery(entityType);
        try {
            ISearch search = this.createISearchInstance(BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{spaceKeyQuery, contentTypeQuery}));
            return this.searchManager.search(search).size();
        }
        catch (InvalidSearchException e) {
            log.error("Error searching total number of entities in the space using search manager", (Throwable)e);
        }
        catch (Exception e) {
            log.error("General error searching total number of entities in the space using search manager", (Throwable)e);
        }
        return 0;
    }

    private ISearch createISearchInstance(SearchQuery searchQuery) throws Exception {
        if (ConfluenceApiUtils.isLuceneMigrated()) {
            return (ISearch)DefaultSearch.class.getConstructor(EnumSet.class, SearchQuery.class, SearchSort.class, Integer.TYPE, Integer.TYPE).newInstance(EnumSet.of(SearchIndex.CONTENT), searchQuery, null, 0, 20);
        }
        Class<?> searchFilterClass = Class.forName("com.atlassian.confluence.search.v2.SearchFilter");
        return (ISearch)DefaultSearch.class.getConstructor(EnumSet.class, SearchQuery.class, SearchSort.class, searchFilterClass, Integer.TYPE, Integer.TYPE).newInstance(EnumSet.of(SearchIndex.CONTENT), searchQuery, null, null, 0, 20);
    }
}

