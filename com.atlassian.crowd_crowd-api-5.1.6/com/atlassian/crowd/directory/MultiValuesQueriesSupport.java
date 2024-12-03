/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.google.common.collect.ListMultimap
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ListMultimap;
import java.util.List;
import javax.annotation.Nonnull;

public interface MultiValuesQueriesSupport {
    @Nonnull
    public <T> List<T> searchGroupRelationships(MembershipQuery<T> var1) throws OperationFailedException;

    public <T> ListMultimap<String, T> searchGroupRelationshipsGroupedByName(MembershipQuery<T> var1);
}

