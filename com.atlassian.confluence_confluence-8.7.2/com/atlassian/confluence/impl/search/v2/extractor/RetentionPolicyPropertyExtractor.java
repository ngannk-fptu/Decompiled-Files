/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.SpaceDescription;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class RetentionPolicyPropertyExtractor
implements Extractor2 {
    @Deprecated
    public static final String SEARCH_FIELD_NAME = SearchFieldMappings.RETENTION_POLICY.getName();
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;

    public RetentionPolicyPropertyExtractor(SpaceRetentionPolicyManager spaceRetentionPolicyManager) {
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
            String value = this.spaceRetentionPolicyManager.getPolicy(spaceDescription.getSpaceKey()).isPresent() ? "true" : "false";
            return Collections.singletonList(SearchFieldMappings.RETENTION_POLICY.createField(value));
        }
        return Collections.emptyList();
    }
}

