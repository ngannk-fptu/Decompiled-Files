/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;

public interface BlueprintSorter {
    public List<CreateDialogWebItemEntity> sortContentBlueprintItems(@Nonnull Collection<CreateDialogWebItemEntity> var1, @Nonnull Space var2, ConfluenceUser var3);

    public List<CreateDialogWebItemEntity> sortSpaceBlueprintItems(@Nonnull List<CreateDialogWebItemEntity> var1, ConfluenceUser var2);
}

