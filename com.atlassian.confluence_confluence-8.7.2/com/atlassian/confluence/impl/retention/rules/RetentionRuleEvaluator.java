/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionRule
 *  com.atlassian.confluence.api.model.retention.RuleScope
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.RuleScope;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.ContentType;
import com.atlassian.confluence.impl.retention.rules.EvaluatedHistoricalVersion;
import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RetentionRuleEvaluator {
    private final Map<Long, SpaceRetentionPolicy> spaceRetentionPolicyMap = new HashMap<Long, SpaceRetentionPolicy>();
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;

    public RetentionRuleEvaluator(SpaceRetentionPolicyManager spaceRetentionPolicyManager) {
        this.spaceRetentionPolicyManager = spaceRetentionPolicyManager;
    }

    public List<EvaluatedHistoricalVersion> evaluate(RetentionRule globalRetentionRule, Map<Long, List<HistoricalVersion>> versionsByOriginalId) {
        ArrayList<EvaluatedHistoricalVersion> evaluated = new ArrayList<EvaluatedHistoricalVersion>();
        versionsByOriginalId.forEach((currentId, versions) -> {
            EvaluatedRule evaluatedRule = this.determineRuleToUse((List<HistoricalVersion>)versions, globalRetentionRule);
            int firstVersionToKeep = this.determineFirstVersionToKeep(evaluatedRule, (List<HistoricalVersion>)versions);
            evaluated.addAll(versions.stream().map(version -> this.evaluate((HistoricalVersion)version, evaluatedRule, firstVersionToKeep)).collect(Collectors.toList()));
        });
        return evaluated;
    }

    private EvaluatedRule determineRuleToUse(List<HistoricalVersion> versions, RetentionRule globalRetentionRule) {
        HistoricalVersion historicalVersion;
        SpaceRetentionPolicy spaceRetentionPolicy;
        if (!versions.isEmpty() && (spaceRetentionPolicy = this.getSpaceRetentionPolicy(historicalVersion = versions.get(0))) != null) {
            if (historicalVersion.getContentType().equals((Object)ContentType.PAGE)) {
                return new EvaluatedRule(spaceRetentionPolicy.getPageVersionRule(), RuleScope.SPACE);
            }
            return new EvaluatedRule(spaceRetentionPolicy.getAttachmentRetentionRule(), RuleScope.SPACE);
        }
        return new EvaluatedRule(globalRetentionRule, RuleScope.GLOBAL);
    }

    private SpaceRetentionPolicy getSpaceRetentionPolicy(HistoricalVersion historicalVersion) {
        if (historicalVersion.getSpaceId() != null) {
            return this.getSpaceRetentionPolicy(historicalVersion.getSpaceId());
        }
        return null;
    }

    private int determineFirstVersionToKeep(EvaluatedRule evaluatedRule, List<HistoricalVersion> historicalVersionList) {
        int firstVersionToKeep = 0;
        RetentionRule retentionRule = evaluatedRule.getRule();
        if (retentionRule.hasVersionLimit()) {
            int maxNumberOfVersions = retentionRule.getMaxNumberOfVersions();
            if (historicalVersionList.size() > maxNumberOfVersions) {
                List versionNumbers = historicalVersionList.stream().map(HistoricalVersion::getVersion).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                firstVersionToKeep = (Integer)versionNumbers.get(maxNumberOfVersions - 1);
            }
        }
        return firstVersionToKeep;
    }

    private EvaluatedHistoricalVersion evaluate(HistoricalVersion historicalVersion, EvaluatedRule evaluatedRule, int firstVersionToKeep) {
        RetentionRule retentionRule = evaluatedRule.getRule();
        boolean removedByGlobalPolicy = !retentionRule.getKeepAll() && this.filterForVersionNumber(retentionRule, firstVersionToKeep).test(historicalVersion) && this.filterForMaximumDate(retentionRule, this.determineMaxDate(retentionRule)).test(historicalVersion);
        return new EvaluatedHistoricalVersion(historicalVersion, evaluatedRule.getRuleScope(), removedByGlobalPolicy);
    }

    private Predicate<? super HistoricalVersion> filterForVersionNumber(RetentionRule rule, int finalFirstVersionToKeep) {
        return rule.hasVersionLimit() ? content -> content.getVersion() < finalFirstVersionToKeep : c -> true;
    }

    private Predicate<? super HistoricalVersion> filterForMaximumDate(RetentionRule rule, LocalDate finalMaxDate) {
        return rule.hasAgeLimit() ? content -> this.isBefore(finalMaxDate, (HistoricalVersion)content) : c -> true;
    }

    private boolean isBefore(LocalDate finalMaxDate, HistoricalVersion content) {
        LocalDate localDate = LocalDateTime.ofInstant(content.getLastModificationDate(), ZoneOffset.UTC).toLocalDate();
        return localDate.isBefore(finalMaxDate);
    }

    private LocalDate determineMaxDate(RetentionRule retentionRule) {
        LocalDate maxDate = null;
        if (retentionRule.hasAgeLimit()) {
            maxDate = retentionRule.calculateMaxDate(LocalDate.now());
        }
        return maxDate;
    }

    private SpaceRetentionPolicy getSpaceRetentionPolicy(long spaceId) {
        if (this.spaceRetentionPolicyMap.containsKey(spaceId)) {
            return this.spaceRetentionPolicyMap.get(spaceId);
        }
        Optional<SpaceRetentionPolicy> result = this.spaceRetentionPolicyManager.getPolicy(spaceId);
        SpaceRetentionPolicy spaceRetentionPolicy = result.orElse(null);
        this.spaceRetentionPolicyMap.put(spaceId, spaceRetentionPolicy);
        return spaceRetentionPolicy;
    }

    private static class EvaluatedRule {
        private final RetentionRule rule;
        private final RuleScope ruleScope;

        public EvaluatedRule(RetentionRule rule, RuleScope ruleScope) {
            this.rule = rule;
            this.ruleScope = ruleScope;
        }

        public RetentionRule getRule() {
            return this.rule;
        }

        public RuleScope getRuleScope() {
            return this.ruleScope;
        }
    }
}

