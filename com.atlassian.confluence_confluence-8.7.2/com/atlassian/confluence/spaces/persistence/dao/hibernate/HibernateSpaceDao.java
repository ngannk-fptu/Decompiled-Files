/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.collect.Collections2
 *  io.atlassian.fugue.Suppliers
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.FlushMode
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.spaces.persistence.dao.hibernate;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.cache.ReadThroughEntityCache;
import com.atlassian.confluence.internal.spaces.SpacesQueryWithPermissionQueryBuilder;
import com.atlassian.confluence.internal.spaces.persistence.SpaceDaoInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.spaces.persistence.dao.hibernate.HibernateSpacesQueryBuilder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.google.common.collect.Collections2;
import io.atlassian.fugue.Suppliers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.FlushMode;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;

@ParametersAreNonnullByDefault
public class HibernateSpaceDao
extends ConfluenceHibernateObjectDao<Space>
implements SpaceDaoInternal {
    @Deprecated
    public static final String CACHE_KEY = HibernateSpaceDao.class.getName() + ".spaceKeyToId";
    private static final Logger log = LoggerFactory.getLogger(HibernateSpaceDao.class);
    private final Supplier<SpaceKeyCache> spaceKeyCache = Suppliers.memoize(() -> new SpaceKeyCache(this.cacheFactory, this::getById));

    @Override
    public @Nullable Space getById(long id) {
        return (Space)this.getByClassId(id);
    }

    @Override
    public @Nullable Space getSpace(@Nullable String spaceKey) {
        if (spaceKey == null) {
            return null;
        }
        Space space = this.spaceKeyCache.get().get(spaceKey, () -> this.queryHibernateForSpace(spaceKey)).orElse(null);
        return space;
    }

    private Space queryHibernateForSpace(String spaceKey) {
        return (Space)this.uniqueResult(this.findNamedQueryStringParam("confluence.space_findBySpaceKey", "spaceKey", spaceKey.toLowerCase(), HibernateObjectDao.Cacheability.NOT_CACHEABLE));
    }

    @Override
    public @Nullable Space getPersonalSpace(@Nullable ConfluenceUser user) {
        if (user == null) {
            return null;
        }
        List personalSpaces = this.findNamedQueryStringParam("confluence.space_findPersonalSpace", "user", user, HibernateObjectDao.Cacheability.CACHEABLE);
        if (personalSpaces.isEmpty()) {
            return null;
        }
        Space space = (Space)personalSpaces.get(0);
        if (personalSpaces.size() > 1) {
            log.warn("Multiple personal spaces were found for the user {}. The first will be returned (key = {}).", (Object)user, (Object)space.getKey());
        }
        return space;
    }

    @Override
    public @NonNull List<Space> getSpacesCreatedByUser(@Nullable String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.space_findByCreator", "creator", user);
    }

    @Override
    public @NonNull List<Space> getSpacesContainingPagesEditedByUser(@Nullable String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.space_findByPageEditor", "user", user);
    }

    @Override
    public @NonNull List<Space> getSpacesContainingCommentsByUser(@Nullable String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        List abstractPages = this.findNamedQueryStringParam("confluence.pages_findByCommentAuthor", "creator", user);
        HashSet<Space> distinctSpaces = new HashSet<Space>();
        for (AbstractPage abstractPage : abstractPages) {
            if (!abstractPage.getSpace().isGlobal()) continue;
            distinctSpaces.add(abstractPage.getSpace());
        }
        return new ArrayList<Space>(distinctSpaces);
    }

    @Override
    public @NonNull List<Space> getSpacesCreatedOrUpdatedSinceDate(Date previousLoginDate) {
        return this.findNamedQueryStringParam("confluence.space_findSpacesCreatedOrUpdatedSinceDate", "date", previousLoginDate, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public int findPageTotal(Space space) {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            Query query = session.getNamedQuery("confluence.space_getPageCount");
            query.setParameter("spaceid", (Object)space.getId());
            return query.list();
        })));
    }

    private void removeSpaceFromCache(Space space) {
        this.spaceKeyCache.get().remove(space);
    }

    @Override
    public void removeSpaceFromCache(String spaceKey) {
        this.spaceKeyCache.get().remove(spaceKey);
    }

    @Override
    public void remove(EntityObject entity) {
        if (entity instanceof Space) {
            this.removeSpaceFromCache((Space)entity);
        }
        super.remove(entity);
    }

    @Override
    public @NonNull Class<Space> getPersistentClass() {
        return Space.class;
    }

    @Override
    public int getNumberOfBlogPosts(Space space) {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            Query query = session.getNamedQuery("confluence.space_getBlogPostCount");
            query.setParameter("spaceid", (Object)space.getId());
            return query.list();
        })));
    }

    @Override
    public @NonNull List<Space> getSpacesCreatedAfter(Date creationDate) {
        return Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createNamedQuery("confluence.space_getSpacesCreatedAfter", Space.class);
            query.setParameter("creationDate", (Object)creationDate);
            query.setCacheable(true);
            return query.list();
        }));
    }

    @Override
    public @NonNull List<Space> getSpaces(SpacesQueryWithPermissionQueryBuilder queryWithPermissionClauseBuilder, int offset, int maxResults) {
        return Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            HibernateSpacesQueryBuilder builder = new HibernateSpacesQueryBuilder(queryWithPermissionClauseBuilder);
            Query query = session.createQuery(builder.getListQuery(), Space.class);
            builder.fillInQueryParameters(query);
            query.setFirstResult(offset);
            if (maxResults >= 0) {
                query.setMaxResults(maxResults);
            }
            query.setCacheable(true);
            return query.list();
        }));
    }

    @Override
    public @NonNull List<Space> getSpaces(SpacesQueryWithPermissionQueryBuilder queryWithPermissionClauseBuilder) {
        return this.getSpaces(queryWithPermissionClauseBuilder, 0, Integer.MAX_VALUE);
    }

    @Override
    public int countSpaces(SpacesQueryWithPermissionQueryBuilder queryWithPermissionClauseBuilder) {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            HibernateSpacesQueryBuilder builder = new HibernateSpacesQueryBuilder(queryWithPermissionClauseBuilder);
            Query query = session.createQuery(builder.getCountQuery());
            builder.fillInQueryParameters(query);
            query.setCacheable(true);
            return query.list();
        })));
    }

    @Override
    public @Nullable Space getSpaceByContentId(long contentId) {
        return (Space)this.findSingleObject(this.findNamedQueryStringParam("confluence.space_findSpaceByContentId", "contentId", contentId));
    }

    @Override
    public @NonNull Collection<String> findSpaceKeysWithStatus(String status) {
        return Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            Query query = session.getNamedQuery("confluence.space_findSpaceKeysByStatus");
            query.setParameter("spaceStatus", (Object)status);
            query.setCacheable(true);
            query.setHibernateFlushMode(FlushMode.MANUAL);
            return query.list();
        }));
    }

    @Override
    public @NonNull String findUniqueVersionOfSpaceKey(String spaceKey) {
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new IllegalArgumentException("A spaceKey must be supplied.");
        }
        String pattern = spaceKey + "%";
        List likeKeys = this.findNamedQueryStringParam("confluence.space_findLikeSpaceKeys", "spacekeypattern", pattern);
        if (likeKeys.isEmpty()) {
            return spaceKey;
        }
        LargestSuffixPredicate predicate = new LargestSuffixPredicate(spaceKey);
        Collection filteredSpaceKeys = Collections2.filter((Collection)likeKeys, predicate::test);
        if (filteredSpaceKeys.size() == 0) {
            return spaceKey;
        }
        int suffix = predicate.getLargestSuffix() + 1;
        return spaceKey + "_" + suffix;
    }

    @Override
    public void performOnAll(Consumer<Space> task) {
        SpacesQuery query = SpacesQuery.newQuery().build();
        SpacesQueryWithPermissionQueryBuilder queryBuilder = SpacesQueryWithPermissionQueryBuilder.spacesQueryWithoutPermissionCheck(query);
        int totalSpaces = this.getTotalOfSpaces();
        boolean maxResults = true;
        for (int index = 0; index < totalSpaces; ++index) {
            List<Space> spaces = this.getSpaces(queryBuilder, index, 1);
            for (Space space : spaces) {
                task.accept(space);
            }
        }
    }

    private int getTotalOfSpaces() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            Query query = session.getNamedQuery("confluence.space_getCount");
            return query.list();
        })));
    }

    @Override
    public List<Long> findSpaceIdListWithIdGreaterOrEqual(Long startingId, int limit) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query;
            if (startingId != null) {
                query = session.createQuery("select id from Space where id >= :spaceId order by id");
                query.setParameter("spaceId", (Object)startingId);
            } else {
                query = session.createQuery("select id from Space order by id");
            }
            query.setMaxResults(limit);
            return query.list();
        });
    }

    @Override
    public List<String> findAllSpaceKeys() {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select key from Space", String.class);
            query.setCacheable(true);
            return query.list();
        });
    }

    private static class SpaceKeyCache {
        private final ReadThroughCache<String, Space> cache;

        SpaceKeyCache(CacheFactory cacheFactory, Function<Long, Space> getSpaceById) {
            this.cache = ReadThroughEntityCache.forConfluenceEntityObjects(ReadThroughAtlassianCache.create(cacheFactory, CoreCache.SPACE_ID_BY_SPACE_KEY), getSpaceById);
        }

        public void remove(Space space) {
            this.cache.remove(SpaceKeyCache.cacheKey(space.getKey()));
        }

        public void remove(String spaceKey) {
            this.cache.remove(SpaceKeyCache.cacheKey(spaceKey));
        }

        public Optional<Space> get(String spaceKey, Supplier<Space> spaceLoader) {
            return Optional.ofNullable(this.cache.get(SpaceKeyCache.cacheKey(spaceKey), spaceLoader, space -> this.isValid(spaceKey, (Space)space)));
        }

        private boolean isValid(String expectedSpaceKey, Space space) {
            boolean valid = expectedSpaceKey.equalsIgnoreCase(space.getKey());
            return valid;
        }

        private static String cacheKey(String spaceKey) {
            return spaceKey.toLowerCase();
        }
    }

    @ParametersAreNonnullByDefault
    static final class LargestSuffixPredicate
    implements Predicate<String> {
        private int largestSuffix = 0;
        private String spaceKey;
        private final Pattern uniqueKeyPattern;

        LargestSuffixPredicate(String spaceKey) {
            this.spaceKey = Objects.requireNonNull(spaceKey);
            this.uniqueKeyPattern = Pattern.compile(spaceKey + "_(\\d+)$");
        }

        @Override
        public boolean test(@NonNull String input) {
            Objects.requireNonNull(input);
            if (this.spaceKey.equals(input)) {
                return true;
            }
            Matcher matcher = this.uniqueKeyPattern.matcher(input);
            if (matcher.matches()) {
                int newSuffix = Integer.parseInt(matcher.group(1));
                this.largestSuffix = Math.max(this.largestSuffix, newSuffix);
                return true;
            }
            return false;
        }

        public int getLargestSuffix() {
            return this.largestSuffix;
        }
    }
}

