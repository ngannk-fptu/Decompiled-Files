/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.labels;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.labels.dto.RankedLiteLabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.RankedLabelSearchResult;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.sal.api.user.UserKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface LabelManager {
    public static final List<String> CONTENT_TYPES = Arrays.asList("page", "blogpost", "attachment");
    public static final String FAVOURITE_LABEL = "favourite";
    public static final String FAVOURITE_LABEL_YANKEE = "favorite";
    public static final int DEFAULT_LABEL_COUNT = Integer.getInteger("confluence.labels.most-used.default-limit", 100);
    public static final int NO_CHANGE = 0;
    public static final int LABEL_ADDED = 1;
    public static final int LABEL_CREATED = 2;
    public static final int LABEL_REMOVED = 3;
    public static final int LABEL_DELETED = 4;
    public static final int NO_OFFSET = 0;
    public static final int NO_MAX_RESULTS = -1;

    @Transactional
    public int addLabel(Labelable var1, Label var2);

    @Transactional
    public int removeLabel(Labelable var1, Label var2);

    @Transactional
    public void removeLabels(Labelable var1, List var2);

    @Transactional
    public void removeAllLabels(Labelable var1);

    @Transactional
    public boolean deleteLabel(long var1);

    @Transactional
    public boolean deleteLabel(Label var1);

    public Label getLabel(long var1);

    public Label getLabel(ParsedLabelName var1);

    public Label getLabel(String var1);

    public List<Label> getLabels(Collection<String> var1);

    public Label getLabel(Label var1);

    public Label getLabel(String var1, Namespace var2);

    public List<Label> getLabelsByDetail(String var1, String var2, String var3, String var4);

    public List<Label> getLabelsInSpace(String var1);

    public List<LabelSearchResult> getMostPopularLabels();

    public List<LabelSearchResult> getMostPopularLabels(int var1);

    public List<LabelSearchResult> getMostPopularLabelsInSpace(String var1);

    public List<LabelSearchResult> getMostPopularLabelsInSpace(String var1, int var2);

    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanks(Comparator<? super RankedLabelSearchResult> var1);

    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanks(int var1, Comparator<? super RankedLabelSearchResult> var2);

    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanksInSpace(String var1, int var2, Comparator<? super RankedLabelSearchResult> var3);

    public List<Label> getRecentlyUsedLabels();

    public List<Label> getRecentlyUsedLabels(int var1);

    public List<Labelling> getRecentlyUsedLabellings(int var1);

    public List<Label> getRecentlyUsedLabelsInSpace(String var1);

    public List<Label> getRecentlyUsedLabelsInSpace(String var1, int var2);

    public List<Labelling> getRecentlyUsedLabellingsInSpace(String var1, int var2);

    public List<Label> getSuggestedLabels(Labelable var1);

    public List<Label> getSuggestedLabels(Labelable var1, int var2);

    public List<Label> getSuggestedLabelsInSpace(Labelable var1, String var2);

    public List<Label> getSuggestedLabelsInSpace(Labelable var1, String var2, int var3);

    public List<Label> getRelatedLabels(Label var1);

    public List<Label> getRelatedLabels(Label var1, int var2);

    public List<Label> getRelatedLabels(List<? extends Label> var1, String var2, int var3);

    public List<Label> getRelatedLabelsInSpace(Label var1, String var2);

    public List<Label> getRelatedLabelsInSpace(Label var1, String var2, int var3);

    public List<Space> getSpacesContainingContentWithLabel(Label var1);

    public List<Label> getUsersLabels(String var1);

    public List<Label> getTeamLabels();

    default public List<Label> getTeamLabels(String name) {
        return Collections.emptyList();
    }

    public List<Label> getTeamLabelsForSpace(String var1);

    public List<Label> getTeamLabelsForSpaces(Collection<Space> var1);

    @Deprecated
    public List<? extends Labelable> getCurrentContentForLabel(Label var1);

    @Deprecated
    public List<? extends Labelable> getCurrentContentForLabelAndSpace(Label var1, String var2);

    public List<? extends Labelable> getCurrentContentWithPersonalLabel(String var1);

    public List<Space> getSpacesWithLabel(Label var1);

    public List<Space> getFavouriteSpaces(String var1);

    public List<Labelling> getFavouriteLabellingsByContentIds(Collection<ContentId> var1, UserKey var2);

    public List<Label> getRecentlyUsedPersonalLabels(String var1);

    public List<Label> getRecentlyUsedPersonalLabels(String var1, int var2);

    public List<Labelling> getRecentlyUsedPersonalLabellings(String var1, int var2);

    @Deprecated
    public List getContent(Label var1);

    public int getContentCount(Label var1);

    @Transactional
    public Label createLabel(Label var1);

    @Deprecated
    public List<? extends Labelable> getContentForAllLabels(Collection<Label> var1, int var2, int var3);

    public PartialList<ContentEntityObject> getContentForLabel(int var1, int var2, Label var3);

    public <T extends EditableLabelable> PartialList<T> getForLabel(Class<T> var1, int var2, int var3, Label var4);

    public <T extends EditableLabelable> PartialList<T> getForLabels(Class<T> var1, int var2, int var3, Label ... var4);

    public PartialList<EditableLabelable> getForLabels(int var1, int var2, Label ... var3);

    public PartialList<ContentEntityObject> getContentForAllLabels(int var1, int var2, Label ... var3);

    public PartialList<ContentEntityObject> getContentInSpaceForLabel(int var1, int var2, String var3, Label var4);

    public PartialList<ContentEntityObject> getContentInSpaceForAllLabels(int var1, int var2, String var3, Label ... var4);

    public PartialList<ContentEntityObject> getContentInSpacesForAllLabels(int var1, int var2, Set<String> var3, Label ... var4);

    public PartialList<ContentEntityObject> getAllContentForLabel(int var1, int var2, Label var3);

    public PartialList<ContentEntityObject> getAllContentForAllLabels(int var1, int var2, Label ... var3);

    @ExperimentalApi
    public List<LiteLabelSearchResult> getMostPopularLabelsInSpaceLite(String var1, int var2);

    @ExperimentalApi
    public List<LiteLabelSearchResult> getMostPopularLabelsInSiteLite(int var1);

    @ExperimentalApi
    @Transactional
    public Set<RankedLiteLabelSearchResult> calculateRanksForLiteLabels(List<LiteLabelSearchResult> var1, Comparator<? super RankedLiteLabelSearchResult> var2);

    public PartialList<Label> findGlobalLabelsByNamePrefix(int var1, int var2, String var3);

    public PartialList<Label> findTeamLabelsByNamePrefix(int var1, int var2, String var3);
}

