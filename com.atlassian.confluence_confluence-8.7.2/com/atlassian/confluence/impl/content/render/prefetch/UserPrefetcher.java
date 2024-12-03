/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Stopwatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.impl.content.render.prefetch.PersonalInformationBulkDao;
import com.atlassian.confluence.impl.content.render.prefetch.ResourcePrefetcher;
import com.atlassian.confluence.impl.content.render.prefetch.event.UserPrefetchEvent;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Stopwatch;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPrefetcher
implements ResourcePrefetcher<UserResourceIdentifier> {
    private static final Logger log = LoggerFactory.getLogger(UserPrefetcher.class);
    private final PersonalInformationBulkDao personalInformationBulkDao;
    private final EventPublisher eventPublisher;
    private final CrowdUserPrefetcher crowdUserPrefetcher;

    public UserPrefetcher(PersonalInformationBulkDao personalInformationBulkDao, EventPublisher eventPublisher, ApplicationFactory crowdApplicationFactory, PrefetchDao crowdUserDao) {
        this.personalInformationBulkDao = personalInformationBulkDao;
        this.eventPublisher = eventPublisher;
        this.crowdUserPrefetcher = new CrowdUserPrefetcher(crowdApplicationFactory, crowdUserDao);
    }

    @Override
    public Class<UserResourceIdentifier> getResourceItentifierType() {
        return UserResourceIdentifier.class;
    }

    @Override
    public void prefetch(Set<UserResourceIdentifier> resourceIdentifiers, ConversionContext conversionContext) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<UserKey> userKeys = UserPrefetcher.extractUserKeys(resourceIdentifiers);
        log.debug("Pre-fetching {} PersonalInfo and ConfluenceUser entities for user keys: {}", (Object)userKeys.size(), userKeys);
        Collection<PersonalInformation> personalInfos = this.personalInformationBulkDao.bulkFetchPersonalInformation(userKeys);
        log.debug("Pre-fetched {} PersonalInformation and ConfluenceUser entities", (Object)personalInfos.size());
        Set<String> usernames = UserPrefetcher.extractUsernames(personalInfos);
        log.debug("Pre-fetching {} Crowd users for user names: {}", (Object)usernames.size(), usernames);
        int crowdUserCount = this.crowdUserPrefetcher.fetchCrowdUsers(usernames);
        log.debug("Pre-fetched {} Crowd users for user names", (Object)crowdUserCount);
        UserPrefetchEvent event = UserPrefetchEvent.builder(conversionContext.getEntity()).userResourceCount(resourceIdentifiers.size()).userKeyCount(userKeys.size()).confluenceUserCount(personalInfos.size()).crowdUserCount(crowdUserCount).elapsedTime(Duration.ofMillis(stopwatch.elapsed(TimeUnit.MILLISECONDS))).build();
        this.eventPublisher.publish((Object)event);
    }

    private static Set<String> extractUsernames(Collection<PersonalInformation> personalInfos) {
        return personalInfos.stream().map(info -> info.getUser().getName()).collect(Collectors.toSet());
    }

    private static Set<UserKey> extractUserKeys(Collection<UserResourceIdentifier> resourceIdentifiers) {
        return resourceIdentifiers.stream().map(UserResourceIdentifier::getUserKey).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static interface PrefetchDao {
        public int prefetchAndCacheUsers(long var1, Collection<String> var3);
    }

    private static class CrowdUserPrefetcher {
        private final ApplicationFactory crowdApplicationFactory;
        private final PrefetchDao crowdUserDao;

        CrowdUserPrefetcher(ApplicationFactory crowdApplicationFactory, PrefetchDao crowdUserDao) {
            this.crowdApplicationFactory = crowdApplicationFactory;
            this.crowdUserDao = crowdUserDao;
        }

        public int fetchCrowdUsers(Collection<String> usernames) {
            if (usernames.isEmpty()) {
                return 0;
            }
            List<Directory> directories = this.getDirectories();
            if (directories.isEmpty()) {
                log.debug("No Crowd directories present, cannot prefetch");
                return 0;
            }
            Directory firstDirectory = directories.get(0);
            if (directories.size() > 1) {
                log.debug("{} Crowd directories present: {}. Prefetcher will only query the first one: {}", new Object[]{directories.size(), directories, firstDirectory});
            }
            return this.crowdUserDao.prefetchAndCacheUsers(firstDirectory.getId(), usernames);
        }

        private List<Directory> getDirectories() {
            return this.crowdApplicationFactory.getApplication().getDirectoryMappings().stream().map(mapping -> mapping.getDirectory()).filter(Directory::isActive).collect(Collectors.toList());
        }
    }
}

