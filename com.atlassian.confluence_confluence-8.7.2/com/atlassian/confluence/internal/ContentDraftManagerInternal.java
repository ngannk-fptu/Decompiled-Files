/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import java.util.List;

public interface ContentDraftManagerInternal {
    public <T extends ContentEntityObject> T createDraft(T var1, SaveContext var2);

    public <T extends ContentEntityObject> T findDraftFor(T var1);

    public <T extends ContentEntityObject> List<T> findAllDraftsFor(long var1);

    public <T extends ContentEntityObject> T findDraftFor(long var1);

    public List<ContentEntityObject> findUnpublishedContentWithUserContributions(String var1);

    public List<ContentEntityObject> findAllDraftsWithUnpublishedChangesForUser(String var1);
}

