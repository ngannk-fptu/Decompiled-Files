/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.pages;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.TrashManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TrashManagerInternal
extends TrashManager {
    public void migrateTrashDate(Instant var1);

    public void migrateTrashDate(String var1, Instant var2);

    @Transactional(readOnly=true)
    public Optional<Instant> findTrashDate(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public Optional<Instant> findTrashDate(Content var1);

    @Transactional(readOnly=true)
    public List<SpaceContentEntityObject> getTrashedEntities(long var1, int var3);

    public void purge(List<SpaceContentEntityObject> var1);
}

