/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public interface RelatedContentRefactorer {
    public void updateReferrers(SpaceContentEntityObject var1, Space var2, String var3);

    public void updateReferrersForMovingPage(SpaceContentEntityObject var1, Space var2, String var3, List<Page> var4);

    public void updateReferrers(Attachment var1, Attachment var2);

    public void updateReferences(SpaceContentEntityObject var1, Space var2, String var3);

    public void updateReferencesForMovingPage(SpaceContentEntityObject var1, Space var2, String var3);

    public String refactorReferencesToBeRelative(SpaceContentEntityObject var1);

    @Deprecated
    public void contractAbsoluteReferencesInContent(List<Page> var1, Space var2);

    public void contractAbsoluteReferencesInContent(List<Page> var1);
}

