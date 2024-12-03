/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.core.persistence.SearchableDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.util.JvmSystemResources;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.unit.DataSize;

public enum ReIndexOption {
    CONTENT_ONLY{

        @Override
        public List<HibernateHandle> getHandles(SearchableDao dao, Optional<String> spaceKey) {
            return dao.getLatestSearchableHandlesGroupedByType(spaceKey).stream().filter(x -> {
                Iterator iterator = x.iterator();
                return iterator.hasNext() && this.getClassFilter().test(((HibernateHandle)((Object)((Object)iterator.next()))).getClassName());
            }).flatMap(Collection::stream).collect(Collectors.toList());
        }

        @Override
        public SearchQuery getDeleteQuery() {
            return CONTENT_ONLY_QUERY;
        }

        @Override
        public SearchQuery getDeleteQuery(Optional<String> spaceKey) {
            return spaceKey.isPresent() ? CONTENT_ONLY_IN_SPACE_QUERY.apply(spaceKey.get()) : CONTENT_ONLY_QUERY;
        }

        @Override
        int getThreadCount(JvmSystemResources jvmRuntime) {
            return (Integer)ObjectUtils.firstNonNull((Object[])new Integer[]{Integer.getInteger("reindex.thread.count"), Integer.getInteger("index.queue.thread.count"), jvmRuntime.getAvailableProcessors()});
        }

        @Override
        public Predicate<String> getClassFilter() {
            return CONTENT_CLASSES;
        }
    }
    ,
    ATTACHMENT_ONLY{

        @Override
        public List<HibernateHandle> getHandles(SearchableDao dao, Optional<String> spaceKey) {
            return dao.getLatestSearchableHandles(Attachment.class, spaceKey);
        }

        @Override
        public SearchQuery getDeleteQuery() {
            return ATTACHMENT_ONLY_QUERY;
        }

        @Override
        public SearchQuery getDeleteQuery(Optional<String> spaceKey) {
            return spaceKey.isPresent() ? ATTACHMENT_ONLY_IN_SPACE_QUERY.apply(spaceKey.get()) : ATTACHMENT_ONLY_QUERY;
        }

        @Override
        public int getThreadCount(JvmSystemResources jvmRuntime) {
            int availableProcessors = jvmRuntime.getAvailableProcessors();
            DataSize freeMemory = jvmRuntime.getFreeMemory();
            DataSize availableMemoryRequiredPerThread = DataSize.ofMegabytes((long)Integer.getInteger("reindex.attachments.freeMbPerThread", 500).intValue());
            int threadLimitByAvailableMemory = (int)(freeMemory.toBytes() / availableMemoryRequiredPerThread.toBytes());
            int defaultThreadCount = Math.max(4, Math.min(availableProcessors, threadLimitByAvailableMemory));
            log.info("Available CPUs: {}, available memory: {}, required available memory per attachment reindex thread: {}, attachment reindex threads limited to {}", new Object[]{availableProcessors, FileUtils.byteCountToDisplaySize((long)freeMemory.toBytes()), FileUtils.byteCountToDisplaySize((long)availableMemoryRequiredPerThread.toBytes()), defaultThreadCount});
            return Integer.getInteger("reindex.attachments.thread.count", defaultThreadCount);
        }

        @Override
        public Predicate<String> getClassFilter() {
            return ATTACHMENT_CLASS;
        }
    }
    ,
    USER_ONLY{

        @Override
        public List<HibernateHandle> getHandles(SearchableDao dao, Optional<String> spaceKey) {
            return dao.getLatestSearchableHandles(PersonalInformation.class);
        }

        @Override
        public SearchQuery getDeleteQuery() {
            return USER_ONLY_QUERY;
        }

        @Override
        public SearchQuery getDeleteQuery(Optional<String> spaceKey) {
            return this.getDeleteQuery();
        }

        @Override
        int getThreadCount(JvmSystemResources jvmRuntime) {
            return Integer.getInteger("reindex.thread.count", Integer.getInteger("index.queue.thread.count", 4));
        }

        @Override
        public Predicate<String> getClassFilter() {
            return USER_CLASS;
        }
    };

    private static final Logger log;
    private static final SearchQuery ATTACHMENT_ONLY_QUERY;
    private static final Function<String, SearchQuery> ATTACHMENT_ONLY_IN_SPACE_QUERY;
    private static final SearchQuery USER_ONLY_QUERY;
    private static final SearchQuery CONTENT_ONLY_QUERY;
    private static final Function<String, SearchQuery> CONTENT_ONLY_IN_SPACE_QUERY;
    private static final Predicate<String> USER_CLASS;
    private static final Predicate<String> ATTACHMENT_CLASS;
    private static final Predicate<String> CONTENT_CLASSES;

    public abstract SearchQuery getDeleteQuery();

    public abstract SearchQuery getDeleteQuery(Optional<String> var1);

    public Integer getThreadCount() {
        return this.getThreadCount(JvmSystemResources.getRuntime());
    }

    abstract int getThreadCount(JvmSystemResources var1);

    public abstract Predicate<String> getClassFilter();

    public List<HibernateHandle> getHandles(SearchableDao dao) {
        return this.getHandles(dao, Optional.empty());
    }

    public abstract List<HibernateHandle> getHandles(SearchableDao var1, Optional<String> var2);

    public static boolean isFullReindex(Set<ReIndexOption> options) {
        return options == null || options.containsAll(ReIndexOption.fullReindex()) && ReIndexOption.fullReindex().containsAll(options);
    }

    public static EnumSet<ReIndexOption> fullReindex() {
        return ReIndexOption.fullReindex(true);
    }

    public static EnumSet<ReIndexOption> fullReindex(boolean reindexingSite) {
        return reindexingSite ? EnumSet.of(CONTENT_ONLY, ATTACHMENT_ONLY, USER_ONLY) : EnumSet.of(CONTENT_ONLY, ATTACHMENT_ONLY);
    }

    public static EnumSet<ReIndexOption> deserialise(Collection<String> options, boolean reindexingSite) {
        if (options.isEmpty()) {
            return ReIndexOption.fullReindex(reindexingSite);
        }
        return options.stream().filter(StringUtils::isNotBlank).map(option -> {
            try {
                return ReIndexOption.valueOf(option);
            }
            catch (IllegalArgumentException ex) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(() -> EnumSet.noneOf(ReIndexOption.class)));
    }

    static {
        log = LoggerFactory.getLogger(ReIndexOption.class);
        ATTACHMENT_ONLY_QUERY = new TermQuery(SearchFieldNames.CLASS_NAME, Attachment.class.getName());
        ATTACHMENT_ONLY_IN_SPACE_QUERY = spaceKey -> (SearchQuery)BooleanQuery.builder().addMust((U[])new SearchQuery[]{new TermQuery(SearchFieldNames.CLASS_NAME, Attachment.class.getName()), new InSpaceQuery((String)spaceKey)}).build();
        USER_ONLY_QUERY = new TermQuery(SearchFieldNames.CLASS_NAME, PersonalInformation.class.getName());
        CONTENT_ONLY_QUERY = (SearchQuery)BooleanQuery.builder().addMustNot((U[])new SearchQuery[]{ATTACHMENT_ONLY_QUERY, USER_ONLY_QUERY}).build();
        CONTENT_ONLY_IN_SPACE_QUERY = spaceKey -> (SearchQuery)BooleanQuery.builder().addMustNot((U[])new SearchQuery[]{ATTACHMENT_ONLY_QUERY, USER_ONLY_QUERY}).addMust(new InSpaceQuery((String)spaceKey)).build();
        USER_CLASS = x -> PersonalInformation.class.getName().equals(x);
        ATTACHMENT_CLASS = x -> Attachment.class.getName().equals(x);
        CONTENT_CLASSES = USER_CLASS.negate().and(ATTACHMENT_CLASS.negate());
    }
}

