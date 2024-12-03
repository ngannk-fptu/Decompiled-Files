/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.atlassian.confluence.user.UserAccessor
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.migration.agent.dto.UserDomainCountDto;
import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import com.atlassian.migration.agent.dto.UserDomainRulesetDto;
import com.atlassian.migration.agent.entity.UserDomainRule;
import com.atlassian.migration.agent.entity.UserDomainRuleset;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.store.UserDomainRuleStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.net.IDN;
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class UserDomainService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserDomainService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(?<localPart>[^\\p{Z}@]+)@(?<domain>([^\\p{Z}@.]+\\.)+[^\\p{Z}@.]+)");
    private final UserAccessor userAccessor;
    private final UserDomainRuleStore userDomainRuleStore;
    private final PluginTransactionTemplate ptx;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AnalyticsEventService analyticsEventService;
    private final Clock clock;
    private final Supplier<List<UserDomainCountDto>> domainCountCache = Suppliers.memoizeWithExpiration(this::getUserDomainCountsInternal, (long)15L, (TimeUnit)TimeUnit.SECONDS);

    public UserDomainService(UserAccessor userAccessor, UserDomainRuleStore userDomainRuleStore, PluginTransactionTemplate ptx, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService) {
        this(userAccessor, userDomainRuleStore, ptx, analyticsEventBuilder, analyticsEventService, Clock.systemUTC());
    }

    public UserDomainService(UserAccessor userAccessor, UserDomainRuleStore userDomainRuleStore, PluginTransactionTemplate ptx, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService, Clock clock) {
        this.userAccessor = userAccessor;
        this.userDomainRuleStore = userDomainRuleStore;
        this.ptx = ptx;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.analyticsEventService = analyticsEventService;
        this.clock = clock;
    }

    public List<MigrationUser> getUntrustedUsers(List<MigrationUser> users, boolean trustInvalidEmails) {
        UserDomainRuleset ruleset = this.ptx.read(() -> new UserDomainRuleset(this.userDomainRuleStore.getRules()));
        return users.stream().filter(user -> UserDomainService.getDomain(user.getEmail()).map(domain -> !ruleset.isTrusted((String)domain)).orElse(!trustInvalidEmails)).collect(Collectors.toList());
    }

    public UserDomainRulesetDto getDomainRules() {
        return this.toDto(this.userDomainRuleStore.getRules());
    }

    public void upsertDomainRule(UserDomainRuleDto ruleDto) {
        UserDomainRule rule = new UserDomainRule(ruleDto.getDomainName(), ruleDto.getRule());
        try {
            this.ptx.write(() -> {
                Optional<UserDomainRule> existingRule = this.userDomainRuleStore.getRule(ruleDto.getDomainName());
                if (existingRule.isPresent()) {
                    this.userDomainRuleStore.updateRule(rule);
                } else {
                    this.userDomainRuleStore.createRule(rule);
                }
            });
            this.saveRuleUpdatedEvent(rule, true);
        }
        catch (Exception e) {
            log.error("Failed to upsert user domain rule", (Throwable)e);
            this.saveRuleUpdatedEvent(rule, false);
            throw e;
        }
    }

    public boolean deleteDomainRule(String domain) {
        try {
            boolean removed = this.ptx.write(() -> this.userDomainRuleStore.deleteRule(domain));
            this.saveRuleDeletedEvent(removed);
            return removed;
        }
        catch (Exception e) {
            log.error("Failed to delete user domain rule", (Throwable)e);
            this.saveRuleDeletedEvent(false);
            throw e;
        }
    }

    public int deleteDomainRules(List<String> domains) {
        try {
            int numRemoved = this.ptx.write(() -> this.userDomainRuleStore.deleteRules(domains));
            this.saveRulesDeletedEvent(true, numRemoved);
            return numRemoved;
        }
        catch (Exception e) {
            log.error("Failed to delete user domain rules", (Throwable)e);
            this.saveRulesDeletedEvent(false, 0);
            throw e;
        }
    }

    public int deleteAllDomainRules() {
        try {
            int numRemoved = this.ptx.write(this.userDomainRuleStore::deleteAllRules);
            this.saveAllRulesDeletedEvent(true, numRemoved);
            return numRemoved;
        }
        catch (Exception e) {
            log.error("Failed to delete user domain rules", (Throwable)e);
            this.saveAllRulesDeletedEvent(false, 0);
            throw e;
        }
    }

    public int deleteAllUserModifiedDomainRules() {
        try {
            int numRemoved = this.ptx.write(this.userDomainRuleStore::deleteUserModifiedDomainRules);
            this.saveAllRulesDeletedEvent(true, numRemoved);
            return numRemoved;
        }
        catch (Exception e) {
            log.error("Failed to delete user modified domain rules", (Throwable)e);
            this.saveAllRulesDeletedEvent(false, 0);
            throw e;
        }
    }

    public void createAllDomainRules(List<UserDomainRuleDto> rules) {
        List userDomainRules = rules.stream().map(entry -> new UserDomainRule(entry.getDomainName(), entry.getRule())).collect(Collectors.toList());
        try {
            this.ptx.write(() -> this.userDomainRuleStore.batchCreateAllRules(userDomainRules));
            this.saveUserDomainRulesCreatedEvent(rules.size(), true);
        }
        catch (Exception e) {
            this.saveUserDomainRulesCreatedEvent(rules.size(), false);
            log.error("Failed to batch create user domain rules", (Throwable)e);
            throw e;
        }
    }

    public List<String> getBlockedDomainsFromStore() {
        return this.userDomainRuleStore.getDomainsWithBlockedRuleBehaviour().stream().map(UserDomainRule::getDomainName).collect(Collectors.toList());
    }

    private void saveRuleUpdatedEvent(UserDomainRule rule, boolean success) {
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserDomainRuleUpdatedEvent(rule.getRuleBehaviour(), success));
        }
        catch (Exception e) {
            log.error("Failed to save RuleUpdated analytics event.", (Throwable)e);
        }
    }

    private void saveRuleDeletedEvent(boolean success) {
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserDomainRuleDeletedEvent(success));
        }
        catch (Exception e) {
            log.error("Failed to save RuleDeleted analytics event.", (Throwable)e);
        }
    }

    private void saveAllRulesDeletedEvent(boolean success, int count) {
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildAllUserDomainRuleDeletedEvent(success, count));
        }
        catch (Exception e) {
            log.error("Failed to save AllRulesDeleted analytics event.", (Throwable)e);
        }
    }

    private void saveRulesDeletedEvent(boolean success, int count) {
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserDomainRulesDeletedEvent(success, count));
        }
        catch (Exception e) {
            log.error("Failed to save RulesDeleted analytics event.", (Throwable)e);
        }
    }

    private UserDomainRulesetDto toDto(Set<UserDomainRule> rules) {
        return new UserDomainRulesetDto(this.toUserDomainRuleDtos(rules));
    }

    private Set<UserDomainRuleDto> toUserDomainRuleDtos(Set<UserDomainRule> userDomainRules) {
        if (CollectionUtils.isNotEmpty(userDomainRules)) {
            return userDomainRules.stream().map(rule -> new UserDomainRuleDto(rule.getDomainName(), rule.getRuleBehaviour())).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public List<UserDomainCountDto> getUserDomainCounts() {
        return (List)this.domainCountCache.get();
    }

    private List<UserDomainCountDto> getUserDomainCountsInternal() {
        long startTime = this.clock.millis();
        try {
            List domainCounts = StreamSupport.stream(this.userAccessor.getUsers().spliterator(), false).map(user -> UserDomainService.getDomain(user.getEmail())).filter(Optional::isPresent).collect(Collectors.collectingAndThen(Collectors.groupingBy(Optional::get, Collectors.counting()), this::toUserDomains));
            this.saveUserDomainCountsFetchedEvent(this.clock.millis() - startTime, domainCounts.stream().mapToLong(UserDomainCountDto::getUserCount).sum(), domainCounts.size(), true);
            return domainCounts;
        }
        catch (Exception e) {
            log.error("Failed to retrieve user domain counts", (Throwable)e);
            this.saveUserDomainCountsFetchedEvent(this.clock.millis() - startTime, -1L, -1, false);
            throw e;
        }
    }

    public static Optional<String> getDomain(String emailAddress) {
        String domain;
        Matcher matcher;
        if (IdentityAcceptedEmailValidator.isValidEmailAddress((String)emailAddress) && (matcher = EMAIL_PATTERN.matcher(emailAddress = IdentityAcceptedEmailValidator.cleanse((String)emailAddress))).matches() && StringUtils.isNotEmpty((String)(domain = matcher.group("domain")))) {
            String lowerCaseDomain = domain.toLowerCase(Locale.ROOT);
            return Optional.of(IDN.toUnicode(lowerCaseDomain));
        }
        return Optional.empty();
    }

    private List<UserDomainCountDto> toUserDomains(Map<String, Long> domainFrequencies) {
        return domainFrequencies.entrySet().stream().map(entry -> new UserDomainCountDto((String)entry.getKey(), (Long)entry.getValue())).collect(Collectors.toList());
    }

    private void saveUserDomainCountsFetchedEvent(long timeTaken, long userCount, int domainCount, boolean success) {
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserDomainCountsFetchedEvent(timeTaken, userCount, domainCount, success));
        }
        catch (Exception e) {
            log.error("Failed to save UserDomainCountsFetched analytics event.", (Throwable)e);
        }
    }

    private void saveUserDomainRulesCreatedEvent(int numRules, boolean success) {
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserDomainRulesCreatedEvent(numRules, success));
        }
        catch (Exception e) {
            log.error("Failed to save batch create user domain rules analytics event.", (Throwable)e);
        }
    }
}

