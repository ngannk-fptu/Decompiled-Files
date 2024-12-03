/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.util;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.search.CommentScanner;
import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.contributors.search.SpaceWatcherScanner;
import com.atlassian.confluence.contributors.util.AuthorRankingSystem;
import com.atlassian.confluence.contributors.util.PageProcessor;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Internal
public class DefaultPageProcessor
implements PageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPageProcessor.class);
    public static final User ANONYMOUS_USER = new User(){

        public String getFullName() {
            return "Anonymous";
        }

        public String getEmail() {
            return "";
        }

        public String getName() {
            return "Anonymous";
        }
    };
    private static final String GLOBAL_LABEL_PREFIX = Namespace.GLOBAL.getPrefix() + ":";
    private final UserAccessor userAccessor;
    private final SpaceWatcherScanner spaceWatcherScanner;
    private final CommentScanner commentScanner;

    public static void readDocumentField(String[] fieldValues, String logErrorMessage, Consumer<String> consumer) {
        try {
            for (String fieldValue : fieldValues) {
                try (BufferedReader reader = new BufferedReader(new StringReader(StringUtils.defaultString((String)fieldValue)));){
                    String line;
                    while (null != (line = reader.readLine())) {
                        consumer.accept(line);
                    }
                }
            }
        }
        catch (IOException ioe) {
            logger.error(logErrorMessage, (Throwable)ioe);
        }
    }

    @Autowired
    public DefaultPageProcessor(@Qualifier(value="spaceWatcherScanner") SpaceWatcherScanner spaceWatcherScanner, @Qualifier(value="commentScanner") CommentScanner commentScanner, @ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
        this.spaceWatcherScanner = spaceWatcherScanner;
        this.commentScanner = commentScanner;
    }

    @Override
    public AuthorRankingSystem process(Iterable<Doc> documents, AuthorRankingSystem.RankType rankType, PageProcessor.GroupBy groupBy) {
        return this.process(documents, rankType, groupBy, true, true, true, true);
    }

    @Override
    public AuthorRankingSystem process(Iterable<Doc> documents, AuthorRankingSystem.RankType rankType, PageProcessor.GroupBy groupBy, boolean includeContributors, boolean includeComments, boolean includeLabels, boolean includeWatches) {
        return this.processUsingBulkUserLookup(documents, rankType, groupBy, includeContributors, includeComments, includeLabels, includeWatches);
    }

    private AuthorRankingSystem processUsingBulkUserLookup(Iterable<Doc> documents, AuthorRankingSystem.RankType rankType, PageProcessor.GroupBy groupBy, boolean includeContributors, boolean includeComments, boolean includeLabels, boolean includeWatches) {
        AuthorRankingSystem rankingSystem = new AuthorRankingSystem(rankType);
        HashSet<String> spaceKeys = new HashSet<String>();
        HashSet<Long> pageIds = new HashSet<Long>();
        ArrayList<RankingTask> rankingTasks = new ArrayList<RankingTask>();
        UserSupplier userSupplier = new UserSupplier();
        documents.forEach(doc -> {
            if (includeContributors) {
                rankingTasks.addAll(this.prepareContributorRankingTasks((Doc)doc, rankingSystem, groupBy, userSupplier));
            }
            if (includeLabels) {
                rankingTasks.addAll(this.prepareLabelRankingTasks((Doc)doc, rankingSystem, groupBy, userSupplier));
            }
            if (includeWatches) {
                spaceKeys.add(doc.getSpaceKey());
            }
            if (includeComments) {
                pageIds.add(doc.getPageId());
            }
        });
        if (includeWatches) {
            ArrayListMultimap watchersBySpaceKey = ArrayListMultimap.create();
            this.spaceWatcherScanner.scan(spaceKeys, (arg_0, arg_1) -> ((Multimap)watchersBySpaceKey).put(arg_0, arg_1));
            documents.forEach(arg_0 -> this.lambda$processUsingBulkUserLookup$1(rankingTasks, rankingSystem, groupBy, userSupplier, (Multimap)watchersBySpaceKey, arg_0));
        }
        if (includeComments) {
            this.commentScanner.scan(pageIds, doc -> rankingTasks.add(this.prepareCommentRankingTask((Doc)doc, rankingSystem, groupBy, userSupplier)));
        }
        this.prefetchUsers(rankingTasks, userSupplier);
        rankingTasks.forEach(RankingTask::execute);
        return rankingSystem;
    }

    private void prefetchUsers(List<RankingTask> rankingTasks, UserSupplier userSupplier) {
        Set userKeys = rankingTasks.stream().map(RankingTask::getUserKey).filter(userKey -> !Strings.isNullOrEmpty((String)userKey)).map(userKey -> new UserKey(StringEscapeUtils.unescapeHtml4((String)userKey))).collect(Collectors.toSet());
        if (userKeys.size() == 1) {
            UserKey userKey2 = (UserKey)userKeys.iterator().next();
            ConfluenceUser user2 = this.userAccessor.getUserByKey(userKey2);
            if (user2 != null) {
                userSupplier.setUserMap((Map<String, User>)ImmutableMap.of((Object)userKey2.getStringValue(), (Object)user2));
            }
        } else if (!userKeys.isEmpty()) {
            Map<String, User> userMap = this.userAccessor.getUsersByUserKeys(new ArrayList(userKeys)).stream().collect(Collectors.toMap(user -> user.getKey().getStringValue(), Function.identity()));
            userSupplier.setUserMap(userMap);
        }
    }

    private RankingTask prepareCommentRankingTask(Doc doc, AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupBy, Supplier<Map<String, User>> userSupplier) {
        long lastModifiedTime = LuceneUtils.stringToDate((String)doc.getModified()).getTime();
        String pageId = String.valueOf(doc.getPageId());
        String pageTitle = doc.getPageDisplayTitle();
        String userKey = doc.getLastModifier();
        return new CommentRankingTask(rankingSystem, groupBy, userSupplier, pageId, pageTitle, userKey, lastModifiedTime);
    }

    private List<RankingTask> prepareWatcherRankingTasks(Doc doc, AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier, Collection<String> spaceWatchers) {
        ArrayList<RankingTask> tasks = new ArrayList<RankingTask>();
        DefaultPageProcessor.readDocumentField(doc.getWatchers(), "Error collecting information to calculate watchers of " + doc.getTitle(), line -> {
            if (StringUtils.isNotBlank((CharSequence)line)) {
                tasks.add(new WatcherRankingTask(rankingSystem, groupingType, userSupplier, String.valueOf(doc.getPageId()), doc.getUrlPath(), (String)line));
            }
        });
        for (String watcherKey : spaceWatchers) {
            tasks.add(new WatcherRankingTask(rankingSystem, groupingType, userSupplier, String.valueOf(doc.getPageId()), doc.getTitle(), watcherKey));
        }
        return tasks;
    }

    private List<RankingTask> prepareContributorRankingTasks(Doc doc, AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier) {
        ArrayList<RankingTask> tasks = new ArrayList<RankingTask>();
        DefaultPageProcessor.readDocumentField(doc.getAuthorContributions(), "Error collecting information to calculate edit contributions of " + doc.getTitle(), line -> {
            Object[] authorContributionTokens = StringUtils.splitByWholeSeparator((String)line, (String)"<>");
            if (authorContributionTokens.length == 2 && !ArrayUtils.contains((Object[])authorContributionTokens, (Object)"")) {
                Object userKey = authorContributionTokens[0];
                long lastModifiedTime = Long.parseLong((String)authorContributionTokens[1]);
                tasks.add(new ContributorRankingTask(rankingSystem, groupingType, userSupplier, String.valueOf(doc.getPageId()), doc.getTitle(), (String)userKey, lastModifiedTime));
            }
        });
        return tasks;
    }

    private List<RankingTask> prepareLabelRankingTasks(Doc doc, AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier) {
        ArrayList<RankingTask> tasks = new ArrayList<RankingTask>();
        DefaultPageProcessor.readDocumentField(doc.getLabelContributions(), "Error collecting information to calculate label contributions of " + doc.getTitle(), line -> {
            String labelStr;
            ParsedLabelName parsedLabel;
            Object[] labelContributionTokens = StringUtils.splitByWholeSeparator((String)line, (String)"<>");
            if (labelContributionTokens.length == 3 && !ArrayUtils.contains((Object[])labelContributionTokens, (Object)"") && (parsedLabel = LabelParser.parse((String)((labelStr = StringEscapeUtils.unescapeHtml4((String)labelContributionTokens[0])).startsWith(GLOBAL_LABEL_PREFIX) ? labelStr.substring(GLOBAL_LABEL_PREFIX.length()) : labelStr))) != null) {
                Object userKey = labelContributionTokens[2];
                long lastModifiedTime = Long.parseLong((String)labelContributionTokens[1]);
                tasks.add(new LabelRankingTask(rankingSystem, groupingType, userSupplier, String.valueOf(doc.getPageId()), doc.getTitle(), parsedLabel, (String)userKey, lastModifiedTime));
            }
        });
        return tasks;
    }

    private /* synthetic */ void lambda$processUsingBulkUserLookup$1(List rankingTasks, AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupBy, UserSupplier userSupplier, Multimap watchersBySpaceKey, Doc doc) {
        rankingTasks.addAll(this.prepareWatcherRankingTasks(doc, rankingSystem, groupBy, userSupplier, watchersBySpaceKey.get((Object)doc.getSpaceKey())));
    }

    private static class UserSupplier
    implements Supplier<Map<String, User>> {
        private Map<String, User> userMap = Collections.emptyMap();

        private UserSupplier() {
        }

        @Override
        public Map<String, User> get() {
            return this.userMap;
        }

        protected void setUserMap(Map<String, User> userMap) {
            this.userMap = userMap;
        }
    }

    private static class CommentRankingTask
    extends RankingTask {
        private final long lastModifiedTime;

        public CommentRankingTask(AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier, String pageId, String pageTitle, String userKey, long lastModifiedTime) {
            super(rankingSystem, groupingType, userSupplier, pageId, pageTitle, userKey);
            this.lastModifiedTime = lastModifiedTime;
        }

        @Override
        public void execute() {
            User author = this.getUser(this.getUserKey());
            if (this.getGroupingType() == PageProcessor.GroupBy.CONTRIBUTORS) {
                this.getRankingSystem().computeAuthorRanking(author.getName(), author.getFullName()).incrementComments(this.getPageId(), this.getPageTitle(), this.lastModifiedTime);
            }
            if (this.getGroupingType() == PageProcessor.GroupBy.PAGES) {
                this.getRankingSystem().computeAuthorRanking(this.getPageId(), this.getPageTitle()).incrementComments(author.getName(), author.getFullName(), this.lastModifiedTime);
            }
        }
    }

    private static class ContributorRankingTask
    extends RankingTask {
        private final long lastModifiedTime;

        public ContributorRankingTask(AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier, String pageId, String pageTitle, String userKey, long lastModifiedTime) {
            super(rankingSystem, groupingType, userSupplier, pageId, pageTitle, userKey);
            this.lastModifiedTime = lastModifiedTime;
        }

        @Override
        public void execute() {
            User author = this.getUser(this.getUserKey());
            if (this.getGroupingType() == PageProcessor.GroupBy.CONTRIBUTORS) {
                this.getRankingSystem().computeAuthorRanking(author.getName(), author.getFullName()).incrementEdits(this.getPageId(), this.getPageTitle(), this.lastModifiedTime);
            }
            if (this.getGroupingType() == PageProcessor.GroupBy.PAGES) {
                this.getRankingSystem().computeAuthorRanking(this.getPageId(), this.getPageTitle()).incrementEdits(author.getName(), author.getFullName(), this.lastModifiedTime);
            }
        }
    }

    private static class LabelRankingTask
    extends RankingTask {
        private final long lastModifiedTime;
        private final ParsedLabelName parsedLabel;

        public LabelRankingTask(AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier, String pageId, String pageTitle, ParsedLabelName parsedLabel, String userKey, long lastModifiedTime) {
            super(rankingSystem, groupingType, userSupplier, pageId, pageTitle, userKey);
            this.parsedLabel = parsedLabel;
            this.lastModifiedTime = lastModifiedTime;
        }

        @Override
        void execute() {
            User author = this.getUser(this.getUserKey());
            if (this.getGroupingType() == PageProcessor.GroupBy.CONTRIBUTORS) {
                this.getRankingSystem().computeAuthorRanking(author.getName(), author.getFullName()).incrementLabels(this.getPageId(), this.getPageTitle(), this.lastModifiedTime).addLabel(this.parsedLabel.toLabel().toString());
            }
            if (this.getGroupingType() == PageProcessor.GroupBy.PAGES) {
                this.getRankingSystem().computeAuthorRanking(this.getPageId(), this.getPageTitle()).incrementLabels(author.getName(), author.getFullName(), this.lastModifiedTime).addLabel(this.parsedLabel.toLabel().toString());
            }
        }
    }

    private static class WatcherRankingTask
    extends RankingTask {
        public WatcherRankingTask(AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier, String pageId, String pageTitle, String userKey) {
            super(rankingSystem, groupingType, userSupplier, pageId, pageTitle, userKey);
        }

        @Override
        void execute() {
            User watcher = this.getUser(this.getUserKey());
            if (this.getGroupingType() == PageProcessor.GroupBy.CONTRIBUTORS) {
                this.getRankingSystem().computeAuthorRanking(watcher.getName(), watcher.getFullName()).incrementWatches(this.getPageId(), this.getPageTitle());
            }
            if (this.getGroupingType() == PageProcessor.GroupBy.PAGES) {
                this.getRankingSystem().computeAuthorRanking(this.getPageId(), this.getPageTitle()).incrementWatches(watcher.getName(), watcher.getFullName());
            }
        }
    }

    private static abstract class RankingTask {
        private final AuthorRankingSystem rankingSystem;
        private final PageProcessor.GroupBy groupingType;
        private final String pageId;
        private final String pageTitle;
        private final String userKey;
        private final Supplier<Map<String, User>> userSupplier;

        public RankingTask(AuthorRankingSystem rankingSystem, PageProcessor.GroupBy groupingType, Supplier<Map<String, User>> userSupplier, String pageId, String pageTitle, String userKey) {
            this.rankingSystem = rankingSystem;
            this.groupingType = groupingType;
            this.userSupplier = userSupplier;
            this.pageId = pageId;
            this.pageTitle = pageTitle;
            this.userKey = userKey;
        }

        protected User getUser(String userKey) {
            User author;
            if (!Strings.isNullOrEmpty((String)userKey) && (author = this.userSupplier.get().get(StringEscapeUtils.unescapeHtml4((String)userKey))) != null) {
                return author;
            }
            return ANONYMOUS_USER;
        }

        abstract void execute();

        public AuthorRankingSystem getRankingSystem() {
            return this.rankingSystem;
        }

        public PageProcessor.GroupBy getGroupingType() {
            return this.groupingType;
        }

        public String getPageId() {
            return this.pageId;
        }

        public String getPageTitle() {
            return this.pageTitle;
        }

        public String getUserKey() {
            return this.userKey;
        }
    }
}

