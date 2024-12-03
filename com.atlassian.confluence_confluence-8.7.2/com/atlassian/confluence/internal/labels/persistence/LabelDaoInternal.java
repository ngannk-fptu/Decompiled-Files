/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 */
package com.atlassian.confluence.internal.labels.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.persistence.dao.LabelDao;
import java.util.List;

public interface LabelDaoInternal
extends LabelDao,
ObjectDaoInternal<Label> {
    public List<Label> findByDetailsInSpace(String var1, String var2, String var3, String var4, LimitedRequest var5);

    public long getTotalLabelInSpace(String var1, String var2, String var3, String var4);
}

