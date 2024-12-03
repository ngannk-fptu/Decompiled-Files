/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.impl.retention.RetentionType;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.RetentionRuleUtils;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.SpaceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class DeletingRetentionPolicyExtractor
implements Extractor2 {
    @Deprecated
    public static final String SEARCH_FIELD_NAME_TRASH = SearchFieldMappings.RETENTION_POLICY_DELETE_TRASH.getName();
    @Deprecated
    public static final String SEARCH_FIELD_NAME_VERSION = SearchFieldMappings.RETENTION_POLICY_DELETE_VERSION.getName();
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;

    public DeletingRetentionPolicyExtractor(SpaceRetentionPolicyManager spaceRetentionPolicyManager) {
        this.spaceRetentionPolicyManager = Objects.requireNonNull(spaceRetentionPolicyManager);
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (searchable instanceof SpaceDescription) {
            SpaceDescription spaceDescription = (SpaceDescription)searchable;
            return this.spaceRetentionPolicyManager.getPolicy(spaceDescription.getSpaceKey()).map(this::getFieldsForRetentionRules).orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }

    private Collection<FieldDescriptor> getFieldsForRetentionRules(RetentionPolicy policy) {
        ArrayList<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
        fields.add(SearchFieldMappings.RETENTION_POLICY_DELETE_VERSION.createField(String.valueOf(RetentionRuleUtils.hasDeletingRules(policy, RetentionType.HISTORICAL_VERSION))));
        fields.add(SearchFieldMappings.RETENTION_POLICY_DELETE_TRASH.createField(String.valueOf(RetentionRuleUtils.hasDeletingRules(policy, RetentionType.TRASH))));
        return Collections.unmodifiableList(fields);
    }
}

