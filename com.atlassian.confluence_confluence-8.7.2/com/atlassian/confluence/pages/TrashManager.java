/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.spaces.Space;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TrashManager {
    public void emptyTrash(Space var1);

    @Transactional(readOnly=true)
    public int getNumberOfItemsInTrash(Space var1);

    public boolean purge(String var1, long var2);

    @Transactional(readOnly=true)
    public List<ContentEntityObject> getTrashContents(Space var1, int var2, int var3);

    public PageResponse<Content> getTrashContents(Space var1, LimitedRequest var2, Expansion ... var3);

    @Transactional(readOnly=true)
    public Optional<Instant> getTrashDateMigrationTime();
}

