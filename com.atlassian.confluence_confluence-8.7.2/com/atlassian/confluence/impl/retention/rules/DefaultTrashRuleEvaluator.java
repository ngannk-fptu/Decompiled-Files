/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RuleScope
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.confluence.api.model.retention.TrashRetentionRule
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.RuleScope;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.EvaluatedTrash;
import com.atlassian.confluence.impl.retention.rules.TrashRuleEvaluator;
import com.atlassian.confluence.internal.pages.TrashManagerInternal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultTrashRuleEvaluator
implements TrashRuleEvaluator {
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;
    private final TrashManagerInternal trashManagerInternal;

    public DefaultTrashRuleEvaluator(SpaceRetentionPolicyManager spaceRetentionPolicyManager, TrashManagerInternal trashManagerInternal) {
        this.spaceRetentionPolicyManager = spaceRetentionPolicyManager;
        this.trashManagerInternal = trashManagerInternal;
    }

    @Override
    public List<EvaluatedTrash> evaluate(TrashRetentionRule globalRule, List<SpaceContentEntityObject> trashedEntities) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return trashedEntities.stream().map(trash -> {
            Optional<TrashRetentionRule> spaceExemption = this.findSpaceRetentionRuleForTrash((SpaceContentEntityObject)trash);
            TrashRetentionRule rule = spaceExemption.orElse(globalRule);
            RuleScope ruleScope = spaceExemption.isPresent() ? RuleScope.SPACE : RuleScope.GLOBAL;
            return this.evaluate((SpaceContentEntityObject)trash, rule, ruleScope, now);
        }).collect(Collectors.toList());
    }

    private EvaluatedTrash evaluate(SpaceContentEntityObject trash, TrashRetentionRule rule, RuleScope ruleScope, OffsetDateTime evaluationTime) {
        boolean shouldBeDeleted = false;
        if (rule.hasDeletedAgeLimit()) {
            OffsetDateTime maxDate = rule.calculateMaxDate(evaluationTime);
            OffsetDateTime trashDate = this.trashManagerInternal.findTrashDate(trash).map(trashInstant -> trashInstant.atOffset(ZoneOffset.UTC)).orElse(evaluationTime);
            shouldBeDeleted = trashDate.isBefore(maxDate);
        }
        return new EvaluatedTrash(trash, shouldBeDeleted, ruleScope);
    }

    private Optional<TrashRetentionRule> findSpaceRetentionRuleForTrash(SpaceContentEntityObject trash) {
        String spaceKey = trash.getSpace().getKey();
        return this.spaceRetentionPolicyManager.getPolicy(spaceKey).map(SpaceRetentionPolicy::getTrashRetentionRule);
    }
}

