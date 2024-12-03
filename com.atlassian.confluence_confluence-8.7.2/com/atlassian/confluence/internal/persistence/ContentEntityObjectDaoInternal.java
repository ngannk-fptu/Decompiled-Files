/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.base.Predicate
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.internal.persistence.VersionedObjectDaoInternal;
import com.atlassian.core.bean.EntityObject;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ContentEntityObjectDaoInternal<T extends ContentEntityObject>
extends ContentEntityObjectDao<T>,
VersionedObjectDaoInternal<T> {
    @Transactional(readOnly=true)
    default public PageResponse<SpaceContentEntityObject> findContentBySpaceIdAndStatusAndFilter(long spaceId, String status, LimitedRequest limitedRequest, Predicate<? super SpaceContentEntityObject> predicate) {
        return this.findContentBySpaceIdAndStatus(spaceId, status, limitedRequest, (com.google.common.base.Predicate<SpaceContentEntityObject>)((com.google.common.base.Predicate)Optional.ofNullable(predicate).map(p -> p::test).orElse(null)));
    }

    @Transactional(readOnly=true)
    public List<SpaceContentEntityObject> getTrashedEntities(long var1, int var3);

    public void saveRawWithoutReindex(EntityObject var1);
}

