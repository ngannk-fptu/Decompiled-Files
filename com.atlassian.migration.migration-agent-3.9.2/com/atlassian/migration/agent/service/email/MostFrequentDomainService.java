/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.email.BlockedDomainUmsService;
import com.atlassian.migration.agent.service.email.DomainId;
import com.atlassian.migration.agent.service.email.EmailsSource;
import com.atlassian.migration.agent.service.email.NoValidEmailsException;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.impl.UserService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MostFrequentDomainService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MostFrequentDomainService.class);
    private static final String MOST_FREQ_DOMAIN = "DOMAIN";
    private final Cache<String, String> cache = CacheBuilder.newBuilder().build();
    private final UserService userService;
    private final BlockedDomainUmsService blockedDomainUmsService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AnalyticsEventService analyticsEventService;

    private String calculateMostFrequentDomainName(Supplier<List<String>> emailsProvider, EmailsSource emailsSource, String cloudId) {
        long start = System.currentTimeMillis();
        Map emailsDomains = emailsProvider.get().stream().map(email -> StringUtils.substringAfterLast((String)email, (String)"@")).filter(domain -> domain != null && !domain.isEmpty()).map(DomainId::new).collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(x -> 1)));
        emailsDomains.keySet().removeIf(domainId -> !IdentityAcceptedEmailValidator.isValidEmailAddress((String)domainId.generateRandomEmail()));
        long startBlockedDomainsLookup = System.currentTimeMillis();
        Set<DomainId> blockedEmailsDomains = this.blockedDomainUmsService.getBlockedDomainsFromUms(cloudId, emailsDomains.keySet());
        long blockedDomainsLookupFinished = System.currentTimeMillis() - startBlockedDomainsLookup;
        Map.Entry domain2 = emailsDomains.entrySet().stream().filter(entry -> !blockedEmailsDomains.contains(entry.getKey())).max(Map.Entry.comparingByValue()).orElse(null);
        log.info("Refreshed most frequent domain name cache using blocked domains");
        this.sendFinishedAnalyticsEvent(cloudId, emailsDomains.values().stream().mapToInt(Integer::intValue).sum(), emailsDomains.size(), blockedEmailsDomains.size(), blockedDomainsLookupFinished, start, emailsSource);
        if (domain2 == null) {
            throw new NoValidEmailsException("No valid domain found for any email");
        }
        return ((DomainId)domain2.getKey()).getStandardizedDomain();
    }

    private void sendFinishedAnalyticsEvent(String cloudId, int emailFetchedCount, int uniqueDomainsFetchedCount, int blockedDomainsCount, long blockedDomainsLookupTime, long start, EmailsSource emailsSource) {
        long timeToComplete = System.currentTimeMillis() - start;
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildNewEmailSuggestingFinishedEvent(cloudId, emailFetchedCount, uniqueDomainsFetchedCount, blockedDomainsCount, blockedDomainsLookupTime, timeToComplete, emailsSource.name()));
    }

    public String getMostFrequentDomainName(Supplier<List<String>> emailsProvider, EmailsSource emailsSource, String cloudId) {
        try {
            return (String)this.cache.get((Object)MOST_FREQ_DOMAIN, () -> this.calculateMostFrequentDomainName(emailsProvider, emailsSource, cloudId));
        }
        catch (ExecutionException e) {
            log.error("Failed to find most frequent domain name", (Throwable)e);
            return "";
        }
    }

    public String getMostFrequentDomainName(String cloudId) {
        return this.getMostFrequentDomainName(() -> this.userService.getAllUsers().stream().map(MigrationUser::getEmail).collect(Collectors.toList()), EmailsSource.FETCHED, cloudId);
    }

    public void refreshMostFrequentDomainName() {
        this.cache.invalidate((Object)MOST_FREQ_DOMAIN);
    }

    public boolean isMostFrequentDomainNameCached() {
        return this.cache.getIfPresent((Object)MOST_FREQ_DOMAIN) != null;
    }

    @Generated
    public MostFrequentDomainService(UserService userService, BlockedDomainUmsService blockedDomainUmsService, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService) {
        this.userService = userService;
        this.blockedDomainUmsService = blockedDomainUmsService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.analyticsEventService = analyticsEventService;
    }
}

