/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.createcontent.api.services;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintPage;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.annotation.Nonnull;

@Deprecated
@PublicApi
public interface ContentBlueprintService {
    public static final String PAGE_TITLE = "title";
    public static final String LABELS = "labelsString";

    public BlueprintPage createPage(CreateBlueprintPageEntity var1, ConfluenceUser var2) throws BlueprintIllegalArgumentException;

    @Deprecated
    public Draft createDraft(CreateBlueprintPageEntity var1, ConfluenceUser var2) throws BlueprintIllegalArgumentException;

    public ContentEntityObject createContentDraft(CreateBlueprintPageEntity var1, ConfluenceUser var2) throws BlueprintIllegalArgumentException;

    public void deleteContentBlueprintsForSpace(@Nonnull String var1);
}

