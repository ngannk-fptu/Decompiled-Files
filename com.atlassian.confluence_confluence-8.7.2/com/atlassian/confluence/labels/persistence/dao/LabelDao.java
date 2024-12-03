/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.labels.persistence.dao;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LabelDao
extends ObjectDao {
    public static final int UNLIMITED = 0;

    public Label findById(long var1);

    public Label findByLabel(Label var1);

    public List<Label> findByDetails(String var1, String var2, String var3);

    public List<Label> findByDetailsInSpace(String var1, String var2, String var3, String var4);

    public List<Label> findByDetailsInSpaces(String var1, String var2, String var3, Collection<Space> var4);

    public List<Label> findBySpace(String var1, String var2);

    public List<LabelSearchResult> findMostPopular(String var1, int var2);

    public List<LabelSearchResult> findMostPopularBySpace(String var1, String var2, int var3);

    public List<Label> findRecentlyUsedBySpace(String var1, int var2);

    public List<Labelling> findRecentlyUsedLabellingsBySpace(String var1, int var2);

    public List<Label> findRecentlyUsed(int var1);

    public List<Labelling> findRecentlyUsedLabelling(int var1);

    public List<Label> findRecentlyUsedUserLabels(String var1, int var2);

    public List<Labelling> findRecentlyUsedUserLabellings(String var1, int var2);

    public List<Label> findBySingleDegreeSeparation(EditableLabelable var1, int var2);

    public List<Label> findBySingleDegreeSeparation(EditableLabelable var1, String var2, int var3);

    public List<Label> findBySingleDegreeSeparation(Label var1, int var2);

    public List<Label> findBySingleDegreeSeparation(Label var1, String var2, int var3);

    @Deprecated
    public List<? extends EditableLabelable> findCurrentContentForLabel(Label var1);

    @Deprecated
    public List<? extends EditableLabelable> findCurrentContentForLabelAndSpace(Label var1, String var2);

    public int findContentCountForLabel(Label var1);

    public List<Space> findSpacesContainingContentWithLabel(Label var1);

    public List<Space> findSpacesWithLabel(Label var1);

    public Labelling findLabellingByContentAndLabel(EditableLabelable var1, Label var2);

    public Labelling findLabellingById(long var1);

    public void deleteLabellingBySpace(String var1);

    public List<Label> findUnusedLabels();

    public List<Space> getFavouriteSpaces(String var1);

    public List<? extends EditableLabelable> findAllUserLabelledContent(String var1);

    @Deprecated
    public List<? extends EditableLabelable> findContentForLabel(Label var1, int var2);

    public <T extends EditableLabelable> PartialList<T> findForAllLabels(Class<T> var1, int var2, int var3, Label ... var4);

    public PartialList<EditableLabelable> findForAllLabels(int var1, int var2, Label ... var3);

    public PartialList<ContentEntityObject> findContentInSpaceForAllLabels(int var1, int var2, String var3, Label ... var4);

    public PartialList<ContentEntityObject> findContentInSpacesForAllLabels(int var1, int var2, Set<String> var3, Label ... var4);

    public PartialList<ContentEntityObject> findAllContentForAllLabels(int var1, int var2, Label ... var3);

    public PageResponse<Label> findGlobalLabelsByNamePrefix(String var1, LimitedRequest var2);

    default public PageResponse<Label> findTeamLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        return PageResponseImpl.empty((boolean)false);
    }

    public List<Labelling> getFavouriteLabellingsByContentIds(Collection<Long> var1, UserKey var2);
}

