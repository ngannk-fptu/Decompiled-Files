/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.pages.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.persistence.dao.AbstractPageDao;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface AbstractPageDaoInternal
extends AbstractPageDao {
    public int countStaleSharedDrafts();

    public List<ContentEntityObject> getStaleSharedDrafts(LimitedRequest var1);
}

