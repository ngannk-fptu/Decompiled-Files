/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.pages.Draft;
import java.util.List;

public interface DraftService {
    default public Draft saveDraftFromEditor(Long draftId, Long parentPageId, String title, DraftType type, String content, Long contentId, String spaceKey, int pageVersion) throws NotValidException {
        return this.saveDraftFromEditor(draftId, title, type, content, contentId, spaceKey, pageVersion);
    }

    @Deprecated
    public Draft saveDraftFromEditor(Long var1, String var2, DraftType var3, String var4, Long var5, String var6, int var7) throws NotValidException;

    @Deprecated
    public boolean isDraftContentChanged(Long var1, String var2, String var3, Long var4);

    public Draft findDraftForEditor(long var1, DraftType var3, String var4) throws NotAuthorizedException, NotValidException;

    public Draft createNewContentDraft(String var1, DraftType var2);

    public Long removeDraft(long var1, long var3);

    public Long removeDraft(long var1);

    public List<Draft> findDrafts(int var1, int var2) throws NotValidException;

    public Draft getDraft(long var1) throws NotAuthorizedException, NotValidException;

    public static enum DraftType {
        PAGE("page"),
        BLOG("blogpost");

        private final String type;

        private DraftType(String type) {
            this.type = type;
        }

        public static DraftType getByRepresentation(String representation) {
            if ("page".equalsIgnoreCase(representation)) {
                return PAGE;
            }
            if ("blogpost".equalsIgnoreCase(representation)) {
                return BLOG;
            }
            return null;
        }

        public String toString() {
            return this.type;
        }
    }
}

