/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.labels;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface LabelManagerInternal
extends LabelManager {
    public PageResponse<Label> findGlobalLabelsByNamePrefix(String var1, LimitedRequest var2);

    default public PageResponse<Label> findTeamLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    default public PartialList<Label> findGlobalLabelsByNamePrefix(int offset, int maxResults, String namePrefix) {
        LimitedRequest request = LimitedRequestImpl.create((int)offset, (int)maxResults, (int)50);
        PageResponse<Label> response = this.findGlobalLabelsByNamePrefix(namePrefix, request);
        return PartialList.forAll(response.getResults());
    }

    @Override
    default public PartialList<Label> findTeamLabelsByNamePrefix(int offset, int maxResults, String namePrefix) {
        LimitedRequest request = LimitedRequestImpl.create((int)offset, (int)maxResults, (int)50);
        PageResponse<Label> response = this.findTeamLabelsByNamePrefix(namePrefix, request);
        return PartialList.forAll(response.getResults());
    }

    public List<Label> getLabelsInSpace(String var1, LimitedRequest var2);

    public long getTotalLabelInSpace(String var1);
}

