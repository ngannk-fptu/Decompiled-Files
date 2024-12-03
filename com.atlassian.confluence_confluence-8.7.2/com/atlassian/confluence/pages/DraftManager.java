/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.user.User;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface DraftManager {
    @Transactional
    public void saveDraft(Draft var1);

    @Transactional(readOnly=true)
    public Draft findDraft(Long var1, String var2, String var3, String var4);

    public int countDrafts(String var1);

    public Draft getDraft(long var1);

    @Transactional
    public void removeDraft(Draft var1);

    @Transactional(readOnly=true)
    public List<Draft> findDraftsForUser(User var1);

    public boolean isMergeRequired(Draft var1);

    public MergeResult mergeContent(Draft var1);

    @Deprecated
    public Draft getOrCreate(String var1, String var2, String var3);

    @Transactional
    public void removeAllDrafts();

    @Transactional
    public void removeDraftsForUser(String var1);

    @Transactional
    public Draft create(String var1, DraftService.DraftType var2, String var3);

    @Transactional
    default public Draft create(String username, DraftService.DraftType draftType, String spaceKey, long parentPageId) {
        return this.create(username, draftType, spaceKey);
    }
}

