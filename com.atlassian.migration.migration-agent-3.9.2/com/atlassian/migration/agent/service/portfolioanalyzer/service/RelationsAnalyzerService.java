/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.content.service.SpaceService
 *  com.atlassian.confluence.pages.TinyUrl
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.service;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.util.UserMigrationType;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.model.stats.UsersGroupsStats;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.AnalysisMetadata;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.LinkWithSourceSpaceKey;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceNode;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceNodeStats;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceRelations;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceRelationsGraph;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.SpaceKeyResolver;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.WarnLogFileWriter;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationsAnalyzerService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RelationsAnalyzerService.class);
    private static final Expansion[] BODY_AND_SPACE_EXPANSIONS = new Expansion[]{new Expansion("body", new Expansions(new Expansion[]{new Expansion("storage")})), new Expansion("space", new Expansions(new Expansion[]{new Expansion("key")}))};
    private static final Pattern serverIdPattern = Pattern.compile("serverId\">(.*?)</ac:parameter>");
    private static final Pattern keyPattern = Pattern.compile("key\">(.*?)</ac:parameter>");
    private final EntityManagerTemplate tmpl;
    private final String baseUrl;
    private final String serverId;
    private final SpaceManager spaceManager;
    private final StatisticsService statisticsService;
    private final ApplicationLinkService applicationLinkService;
    private final CQLSearchService cqlSearchService;
    private final SpaceKeyResolver spaceKeyResolver;
    private final SpaceService spaceService;
    private final MigrationTimeEstimationUtils migrationTimeEstimationUtils;
    private final WarnLogFileWriter warnLogFileWriter;
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public RelationsAnalyzerService(EntityManagerTemplate tmpl, SystemInformationService systemInformationService, SpaceManager spaceManager, StatisticsService statisticsService, ApplicationLinkService applicationLinkService, CQLSearchService cqlSearchService, SpaceKeyResolver spaceKeyResolver, SpaceService spaceService, MigrationTimeEstimationUtils migrationTimeEstimationUtils, WarnLogFileWriter warnLogFileWriter, UserGroupExtractFacade userGroupExtractFacade, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.tmpl = tmpl;
        this.baseUrl = systemInformationService.getConfluenceInfo().getBaseUrl();
        this.serverId = systemInformationService.getConfluenceInfo().getServerId();
        this.spaceManager = spaceManager;
        this.statisticsService = statisticsService;
        this.applicationLinkService = applicationLinkService;
        this.cqlSearchService = cqlSearchService;
        this.spaceKeyResolver = spaceKeyResolver;
        this.spaceService = spaceService;
        this.migrationTimeEstimationUtils = migrationTimeEstimationUtils;
        this.warnLogFileWriter = warnLogFileWriter;
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public SpaceRelationsGraph getGraph() {
        List<SpaceRelations> relations = Stream.of(this.getNonMacroRelations(), this.getMacroRelations()).flatMap(Collection::stream).collect(Collectors.toList());
        return new SpaceRelationsGraph(new AnalysisMetadata("CONFLUENCE", this.baseUrl, this.serverId), this.getNodes(), relations);
    }

    private Set<SpaceNode> getNodes() {
        List spaceKeys = Arrays.stream(SpaceStatus.values()).map(arg_0 -> ((SpaceManager)this.spaceManager).getAllSpaceKeys(arg_0)).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        return IntStream.range(0, spaceKeys.size()).parallel().mapToObj(spaceKeyIndex -> {
            String spaceKey = (String)spaceKeys.get(spaceKeyIndex);
            int percentages = (int)((double)spaceKeyIndex / (double)spaceKeys.size()) * 100;
            log.info("Fetching spaces finished in {}%.", (Object)percentages);
            Pair<UsersGroupsStats, ContentSummary> stats = this.getSpaceStatistics(spaceKey);
            SpaceNodeStats spaceNodeStats = SpaceNodeStats.from((ContentSummary)stats.getRight());
            long migrationTime = this.migrationTimeEstimationUtils.estimateSpaceMigrationTime((ContentSummary)stats.getRight()).getSeconds() + ((UsersGroupsStats)stats.getLeft()).getTotalMigrationTime().getSeconds();
            Space space = this.spaceService.getKeySpaceLocator(spaceKey).getSpace();
            Set<String> spaceKeySingleton = Collections.singleton(spaceKey);
            return SpaceNode.builder().key(spaceKey).instanceURL(this.baseUrl).estimatedTime(migrationTime).spaceStats(spaceNodeStats).archived(Optional.ofNullable(space).map(Space::isArchived).orElse(false)).lastModified(this.getLastModified(Optional.ofNullable(space).map(EntityObject::getId).orElse(0L)).map(Instant::toEpochMilli).orElse(0L)).users(this.getUserGroupKeys(() -> this.userGroupExtractFacade.getUsersFromSpaces(spaceKeySingleton))).groups(this.getUserGroupKeys(() -> this.userGroupExtractFacade.getGroupsFromSpaces(spaceKeySingleton))).build();
        }).collect(Collectors.toSet());
    }

    private Pair<UsersGroupsStats, ContentSummary> getSpaceStatistics(String key) {
        SpaceStats stats = this.statisticsService.loadSpaceStatistics(key);
        UsersGroupsStats usersGroupsStatistics = this.statisticsService.getUsersGroupsStatistics(UserMigrationType.SCOPED, Collections.singleton(key));
        return Pair.of((Object)usersGroupsStatistics, (Object)stats.getSummary());
    }

    private Optional<Instant> getLastModified(long spaceId) {
        return this.tmpl.query(Date.class, "SELECT MAX(c.lastModDate) FROM Content c LEFT JOIN Content cc ON cc.id = c.container.id WHERE COALESCE(c.spaceId, cc.spaceId) = :spaceId").param("spaceId", (Object)spaceId).stream().filter(Objects::nonNull).findFirst().map(Date::toInstant);
    }

    private List<SpaceRelations> getNonMacroRelations() {
        return this.merge(this.getNonMacroRelationsToLocalSpaces(), this.getNonMacroRelationsToRemoteSpaces());
    }

    private List<SpaceRelations> merge(List<SpaceRelations> a, List<SpaceRelations> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toMap(relation -> Pair.of((Object)relation.getSource(), (Object)relation.getDestination()), SpaceRelations::getCount, Long::sum)).entrySet().stream().map(entry -> new SpaceRelations((String)((Pair)entry.getKey()).getLeft(), (String)((Pair)entry.getKey()).getRight(), (Long)entry.getValue())).collect(Collectors.toList());
    }

    @VisibleForTesting
    List<SpaceRelations> getNonMacroRelationsToLocalSpaces() {
        return this.tmpl.query(SpaceRelations.class, "SELECT NEW com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceRelations(       CONCAT(:baseUrl, ':', s1.key),       CONCAT(:baseUrl, ':', s2.key),       COUNT(*))FROM Link l JOIN SpaceContent sc ON (CASE WHEN l.content.spaceId IS NULL THEN (SELECT c2.id FROM Content c2 WHERE c2.id = l.content.container.id) ELSE l.content.id END) = sc.id JOIN sc.space s1 JOIN Space s2 ON l.destSpaceKey = s2.key WHERE l.destSpaceKey NOT LIKE 'http%'  AND l.content.status = 'current'  AND s1.key != s2.key GROUP BY s1.key, s2.key").param("baseUrl", (Object)this.baseUrl).list();
    }

    private List<SpaceRelations> getNonMacroRelationsToRemoteSpaces() {
        return this.resolveAndMergeRelations(this.getRemoteRelations());
    }

    @NotNull
    private List<SpaceRelations> resolveAndMergeRelations(Map<Pair<String, Destination>, Long> relationsCountBySourceAndDestinationPair) {
        Map pageIdsByUrl = relationsCountBySourceAndDestinationPair.keySet().stream().filter(key -> ((Destination)key.getRight()).getKeyType() == DestinationKeyType.PAGE_ID || ((Destination)key.getRight()).getKeyType() == DestinationKeyType.TINY_URL_IDENTIFIER).map(stringDestinationPair -> {
            Destination destination = (Destination)stringDestinationPair.getRight();
            if (destination.getKeyType() == DestinationKeyType.TINY_URL_IDENTIFIER) {
                return new Destination(destination.url, String.valueOf(new TinyUrl(destination.getKey()).getPageId()), DestinationKeyType.PAGE_ID);
            }
            return destination;
        }).collect(Collectors.groupingBy(Destination::getUrl, HashMap::new, Collectors.mapping(destination -> Long.parseLong(((Destination)destination).key), Collectors.toSet())));
        HashMap resolvedSpacesKeys = new HashMap();
        for (Map.Entry entry2 : pageIdsByUrl.entrySet()) {
            Map<Long, String> resolved = this.spaceKeyResolver.fetchSpaceKeysForPageIds(URI.create((String)entry2.getKey()), (Set)entry2.getValue());
            resolvedSpacesKeys.put(entry2.getKey(), resolved);
        }
        return relationsCountBySourceAndDestinationPair.entrySet().stream().map(entry -> {
            Destination destination = (Destination)((Pair)entry.getKey()).getRight();
            return new SpaceRelations((String)((Pair)entry.getKey()).getLeft(), destination.getUrl() + ":" + this.resolveDestinationKey(destination, resolvedSpacesKeys), (Long)entry.getValue());
        }).collect(Collectors.toList());
    }

    @NotNull
    private Map<Pair<String, Destination>, Long> getRemoteRelations() {
        HashMap<Pair<String, Destination>, Long> relationsCountBySourceAndDestinationPair = new HashMap<Pair<String, Destination>, Long>();
        this.getApplinkedConfluencesUrls().forEach(remoteUrl -> {
            List<LinkWithSourceSpaceKey> links = this.tmpl.query(LinkWithSourceSpaceKey.class, "SELECT NEW com.atlassian.migration.agent.service.portfolioanalyzer.model.LinkWithSourceSpaceKey(l.lowerDestPageTitle, l.lowerDestSpaceKey, s1.key) FROM Link l JOIN SpaceContent sc ON (CASE WHEN l.content.spaceId IS NULL THEN (SELECT c2.id FROM Content c2 WHERE c2.id = l.content.container.id) ELSE l.content.id END) = sc.id JOIN sc.space s1 WHERE l.destSpaceKey like 'http%' AND l.lowerDestPageTitle LIKE '//' || :baseUrlWithoutScheme || '%' AND l.content.status = 'current'").param("baseUrlWithoutScheme", (Object)this.withoutScheme((String)remoteUrl)).list();
            relationsCountBySourceAndDestinationPair.putAll(links.stream().map(l -> this.extractSourceDestinationPair((LinkWithSourceSpaceKey)l).map(sourceDestinationPair -> new AbstractMap.SimpleEntry<Pair, Long>((Pair)sourceDestinationPair, 1L)).orElse(null)).filter(Objects::nonNull).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, Long::sum)));
        });
        return relationsCountBySourceAndDestinationPair;
    }

    private String resolveDestinationKey(Destination destination, Map<String, Map<Long, String>> resolvedSpaceKeys) {
        if (destination.getKeyType() == DestinationKeyType.PAGE_ID) {
            return resolvedSpaceKeys.get(destination.url).get(Long.parseLong(destination.getKey()));
        }
        if (destination.getKeyType() == DestinationKeyType.TINY_URL_IDENTIFIER) {
            return resolvedSpaceKeys.get(destination.url).get(new TinyUrl(destination.getKey()).getPageId());
        }
        return destination.getKey();
    }

    public List<SpaceRelations> getMacroRelations() {
        PageResponse contentList;
        Map<String, String> applicationLinkData = this.getApplicationLinkData();
        int limit = 300;
        int page = 0;
        ArrayList<SpaceRelations> spaceRelations = new ArrayList<SpaceRelations>();
        do {
            SimplePageRequest pageRequest = new SimplePageRequest(300 * page, 300);
            contentList = this.cqlSearchService.searchContent("type IN (page,blogpost,comment) AND macro IN (jira)", (PageRequest)pageRequest, BODY_AND_SPACE_EXPANSIONS);
            spaceRelations.addAll(this.buildSpaceRelationsFromContentData(contentList.getResults(), applicationLinkData));
            ++page;
        } while (contentList.hasMore());
        return spaceRelations;
    }

    private Map<String, String> getApplicationLinkData() {
        return StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).collect(Collectors.toMap(appLink -> appLink.getId().get(), appLink -> appLink.getDisplayUrl().toString()));
    }

    private Collection<SpaceRelations> buildSpaceRelationsFromContentData(List<Content> contentWithBody, Map<String, String> applicationLinkData) {
        HashMap<String, SpaceRelations> spaceRelationsMap = new HashMap<String, SpaceRelations>();
        for (Content content : contentWithBody) {
            this.handleContent(content, applicationLinkData, spaceRelationsMap);
        }
        return spaceRelationsMap.values();
    }

    private void handleContent(Content content, Map<String, String> applicationLinkData, Map<String, SpaceRelations> spaceRelationsMap) {
        try {
            String rawBody = ((ContentBody)content.getBody().get(ContentRepresentation.STORAGE)).getValue();
            Matcher serverIdMatcher = serverIdPattern.matcher(rawBody);
            Matcher keyMatcher = keyPattern.matcher(rawBody);
            while (serverIdMatcher.find() && keyMatcher.find()) {
                String serverId = serverIdMatcher.group(1);
                String issueKey = keyMatcher.group(1);
                String projectKey = issueKey.split("-")[0];
                String serverUrl = applicationLinkData.getOrDefault(serverId, serverId);
                String serverProjectKey = serverUrl + ":" + projectKey;
                String sourceSpaceKey = this.baseUrl + ":" + content.getSpace().getKey();
                String relationKey = sourceSpaceKey + ":" + serverProjectKey;
                spaceRelationsMap.merge(relationKey, new SpaceRelations(sourceSpaceKey, serverProjectKey, 1L), (oldValue, newValue) -> new SpaceRelations(sourceSpaceKey, serverProjectKey, oldValue.getCount() + 1L));
            }
        }
        catch (Exception e) {
            String message = String.format("Encountered an error when handling content: '%s', the process will continue", e.getMessage());
            log.error(message);
            this.warnLogFileWriter.writeError(message);
        }
    }

    private Set<String> getApplinkedConfluencesUrls() {
        return StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).filter(applink -> applink.getType().getI18nKey().equals("applinks.confluence")).map(applink -> StringUtils.stripEnd((String)applink.getDisplayUrl().toString(), (String)"/")).collect(Collectors.toSet());
    }

    private String withoutScheme(String url) {
        return url.replaceFirst("https?://", "");
    }

    private Optional<Pair<String, Destination>> extractSourceDestinationPair(LinkWithSourceSpaceKey link) {
        return this.parseLink(link).map(destination -> Pair.of((Object)(this.baseUrl + ":" + link.getSourceSpaceKey()), (Object)destination));
    }

    private Optional<Destination> parseLink(LinkWithSourceSpaceKey link) {
        ImmutableList parsers = ImmutableList.of(this.parser("//(.+)/display/(.+)/.+", DestinationKeyType.SPACE_KEY), this.parser("//(.+)/pages/viewpage.action\\?pageId=(\\d+)", DestinationKeyType.PAGE_ID), this.parser("//(.+)/pages/viewpage.action\\?spaceKey=(.+)", DestinationKeyType.SPACE_KEY), this.parser("//(.+)/spaces/(.+)/pages/.*", DestinationKeyType.SPACE_KEY), this.parser("//(.+)/download/attachments/(\\d*)", DestinationKeyType.PAGE_ID), this.parser("//(.+)/x/(.+)", DestinationKeyType.TINY_URL_IDENTIFIER));
        Optional<Optional> result = parsers.stream().map(parser -> (Optional)parser.apply(link)).filter(Optional::isPresent).findFirst();
        if (!result.isPresent()) {
            this.warnLogFileWriter.writeError(String.format("Unable to parse the link by any parser - encountered unsupported link: '%s' ", link.getLowerDestPageTitle()));
        }
        return result.orElse(Optional.empty());
    }

    private Function<LinkWithSourceSpaceKey, Optional<Destination>> parser(String pattern, DestinationKeyType destinationKeyType) {
        return link -> {
            Matcher matcher = Pattern.compile(pattern, 2).matcher(link.getLowerDestPageTitle());
            boolean found = matcher.find();
            if (!found) {
                return Optional.empty();
            }
            String scheme = link.getLowerDestSpaceKey() + "://";
            return Optional.of(new Destination(scheme + matcher.group(1), matcher.group(2), destinationKeyType));
        };
    }

    private Set<String> hashed(Collection<String> keys) {
        return keys.stream().map(it -> String.valueOf(it.trim().toLowerCase().hashCode())).collect(Collectors.toSet());
    }

    private Set<String> getUserGroupKeys(Supplier<Set<String>> userGroupKeysSupplier) {
        Set<String> keys = this.migrationDarkFeaturesManager.isRelationsAnalysisUserGroupKeysEnabled() ? userGroupKeysSupplier.get() : Collections.emptySet();
        return this.migrationDarkFeaturesManager.isRelationsAnalysisUserGroupKeysHashingDisabled() ? keys : this.hashed(keys);
    }

    private static final class Destination {
        private final String url;
        private final String key;
        private final DestinationKeyType keyType;

        @Generated
        public Destination(String url, String key, DestinationKeyType keyType) {
            this.url = url;
            this.key = key;
            this.keyType = keyType;
        }

        @Generated
        public String getUrl() {
            return this.url;
        }

        @Generated
        public String getKey() {
            return this.key;
        }

        @Generated
        public DestinationKeyType getKeyType() {
            return this.keyType;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Destination)) {
                return false;
            }
            Destination other = (Destination)o;
            String this$url = this.getUrl();
            String other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
                return false;
            }
            String this$key = this.getKey();
            String other$key = other.getKey();
            if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
                return false;
            }
            DestinationKeyType this$keyType = this.getKeyType();
            DestinationKeyType other$keyType = other.getKeyType();
            return !(this$keyType == null ? other$keyType != null : !((Object)((Object)this$keyType)).equals((Object)other$keyType));
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $url = this.getUrl();
            result = result * 59 + ($url == null ? 43 : $url.hashCode());
            String $key = this.getKey();
            result = result * 59 + ($key == null ? 43 : $key.hashCode());
            DestinationKeyType $keyType = this.getKeyType();
            result = result * 59 + ($keyType == null ? 43 : ((Object)((Object)$keyType)).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "RelationsAnalyzerService.Destination(url=" + this.getUrl() + ", key=" + this.getKey() + ", keyType=" + (Object)((Object)this.getKeyType()) + ")";
        }
    }

    private static enum DestinationKeyType {
        SPACE_KEY,
        PAGE_ID,
        TINY_URL_IDENTIFIER;

    }
}

