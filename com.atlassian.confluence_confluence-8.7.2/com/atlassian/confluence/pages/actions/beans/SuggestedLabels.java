/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SuggestedLabels {
    private final LabelManager labelManager;

    public SuggestedLabels(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    public List getSuggestedLabelsForPage(AbstractPage page, User user) {
        String username = this.getUsernameForUser(user);
        return LabelUtil.getRecentAndPopularLabelsForEntity(page, this.labelManager, 20, username);
    }

    public List getSuggestedLabelsForSpace(String spaceKey, User user) {
        String username = this.getUsernameForUser(user);
        return LabelUtil.getRecentAndPopularLabels(spaceKey, this.labelManager, 20, username);
    }

    public Set<LiteLabelSearchResult> getSuggestedLiteLabels(String spaceKey, User user, int maxResults) {
        String username = this.getUsernameForUser(user);
        List<Label> recentlyUsedLabels = user != null ? this.labelManager.getRecentlyUsedPersonalLabels(username, maxResults) : this.labelManager.getRecentlyUsedLabels();
        Set recentlyUsedLabelsSearchResult = recentlyUsedLabels.stream().map(LiteLabelSearchResult::new).collect(Collectors.toCollection(LinkedHashSet::new));
        List<LiteLabelSearchResult> mostPopularLabelSearchResultList = StringUtils.isNotEmpty((CharSequence)spaceKey) ? this.labelManager.getMostPopularLabelsInSpaceLite(spaceKey, maxResults) : this.labelManager.getMostPopularLabelsInSiteLite(maxResults);
        mostPopularLabelSearchResultList.removeAll(recentlyUsedLabelsSearchResult);
        return this.retrieveDataFromTwoCollectionsFairly(mostPopularLabelSearchResultList, recentlyUsedLabelsSearchResult, maxResults);
    }

    @Deprecated(since="8.2")
    public Set<LiteLabelSearchResult> getSuggestedLiteLabels(AbstractPage entity, String spaceKey, User user, int maxResults) {
        String effectiveSpaceKey = entity != null ? entity.getSpaceKey() : spaceKey;
        return this.getSuggestedLiteLabels(effectiveSpaceKey, user, maxResults);
    }

    private Set<LiteLabelSearchResult> retrieveDataFromTwoCollectionsFairly(Collection<LiteLabelSearchResult> collection1, Collection<LiteLabelSearchResult> collection2, int maxResults) {
        HashSet<LiteLabelSearchResult> finalList = new HashSet<LiteLabelSearchResult>(maxResults);
        Iterator<LiteLabelSearchResult> iterator1 = collection1.iterator();
        Iterator<LiteLabelSearchResult> iterator2 = collection2.iterator();
        while (finalList.size() < maxResults && (iterator1.hasNext() || iterator2.hasNext())) {
            if (iterator1.hasNext()) {
                finalList.add(iterator1.next());
            }
            if (!iterator2.hasNext()) continue;
            finalList.add(iterator2.next());
        }
        return finalList;
    }

    private String getUsernameForUser(User user) {
        return user == null ? null : user.getName();
    }
}

