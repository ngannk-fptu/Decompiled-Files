/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.impl.retention.RetentionType;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.RetentionRuleUtils;
import com.atlassian.confluence.impl.retention.rules.RetentionRulesChecker;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRetentionRulesChecker
implements RetentionRulesChecker {
    private static final Logger log = LoggerFactory.getLogger(DefaultRetentionRulesChecker.class);
    private static final String HAS_DELETING_RULES_TERM = "true";
    private final GlobalRetentionPolicyManager globalRetentionPolicyManager;
    private final SearchManager searchManager;

    public DefaultRetentionRulesChecker(GlobalRetentionPolicyManager globalRetentionPolicyManager, SearchManager searchManager) {
        this.globalRetentionPolicyManager = globalRetentionPolicyManager;
        this.searchManager = searchManager;
    }

    @Override
    public boolean hasDeletingRule(RetentionType type) {
        return RetentionRuleUtils.hasDeletingRules((RetentionPolicy)this.globalRetentionPolicyManager.getPolicy(), type) || this.hasSpaceDeletingRules(type);
    }

    private boolean hasSpaceDeletingRules(RetentionType type) {
        ContentSearch search = new ContentSearch(this.getSearchQuery(type), null, 0, 1);
        try {
            return this.searchManager.search(search).size() > 0;
        }
        catch (InvalidSearchException e) {
            log.error("Cannot search for retention rules that may delete {}", (Object)type.name(), (Object)e);
            return true;
        }
    }

    private SearchQuery getSearchQuery(RetentionType type) {
        if (type == RetentionType.HISTORICAL_VERSION) {
            return new TermQuery(SearchFieldNames.RETENTION_POLICY_DELETE_VERSION, HAS_DELETING_RULES_TERM);
        }
        if (type == RetentionType.TRASH) {
            return new TermQuery(SearchFieldNames.RETENTION_POLICY_DELETE_TRASH, HAS_DELETING_RULES_TERM);
        }
        throw new IllegalArgumentException("Unrecognised retention type " + type.name());
    }
}

