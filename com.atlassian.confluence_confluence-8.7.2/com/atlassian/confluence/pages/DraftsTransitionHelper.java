/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.pages;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;

@Internal
public interface DraftsTransitionHelper {
    public ContentEntityObject getDraftForPage(AbstractPage var1);

    public ContentEntityObject createDraft(String var1, String var2);

    public ContentEntityObject createDraft(String var1, String var2, long var3);

    public ContentEntityObject getDraft(long var1);

    public void transitionContentObjects(ContentEntityObject var1, ContentEntityObject var2);

    public boolean isSharedDraftsFeatureEnabled(String var1);

    @Deprecated
    public boolean isLimitedModeEnabled(String var1);

    public String getEditMode(String var1);

    @Deprecated
    public boolean isFallbackModeEnabled(String var1);

    public static boolean isLegacyDraft(ContentEntityObject draft) {
        return draft instanceof Draft;
    }

    public static boolean isSharedDraft(ContentEntityObject draft) {
        return draft instanceof AbstractPage && draft.isDraft();
    }

    public static String getSpaceKey(ContentEntityObject draft) {
        if (draft instanceof Draft) {
            return ((Draft)draft).getDraftSpaceKey();
        }
        if (draft instanceof Spaced) {
            Space space = ((Spaced)((Object)draft)).getSpace();
            return space == null ? null : space.getKey();
        }
        throw new IllegalArgumentException("CEO is neither a draft or a Spaced instance, cannot resolve spaceKey");
    }

    public static String getContentType(ContentEntityObject draft) {
        if (draft instanceof Draft) {
            return ((Draft)draft).getDraftType();
        }
        return draft.getType();
    }

    public static Long getContentId(ContentEntityObject draft) {
        if (draft instanceof Draft) {
            return ((Draft)draft).getPageIdAsLong();
        }
        return ((ContentEntityObject)draft.getLatestVersion()).getId();
    }
}

