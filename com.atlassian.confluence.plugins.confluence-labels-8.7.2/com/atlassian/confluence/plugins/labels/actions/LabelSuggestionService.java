/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.PartialList
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.dto.LiteLabelSearchResult
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.actions.beans.SuggestedLabels
 *  com.atlassian.confluence.search.actions.json.ContentNameMatch
 *  com.atlassian.confluence.search.actions.json.ContentNameSearchResult
 *  com.atlassian.confluence.search.contentnames.QueryToken
 *  com.atlassian.confluence.search.contentnames.QueryToken$Type
 *  com.atlassian.confluence.search.contentnames.QueryTokenizer
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.beans.SuggestedLabels;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.QueryTokenizer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LabelSuggestionService {
    private static final Collator collator = Collator.getInstance();
    private static final QueryTokenizer tokenizer = query1 -> {
        ArrayList<QueryToken> result = new ArrayList<QueryToken>();
        result.add(new QueryToken(query1, QueryToken.Type.FULL));
        for (String q : query1.split("[\\s,]")) {
            result.add(new QueryToken(q, QueryToken.Type.PARTIAL));
        }
        return result;
    };
    private final LabelManager labelManager;

    public LabelSuggestionService(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    ContentNameSearchResult findSuggestedLabels(String query, boolean isTeamLabel, int maxResults, String spaceKey, boolean ignoreRelated, AbstractPage page) {
        int missing;
        ContentNameSearchResult result = new ContentNameSearchResult(query);
        List queryTokens = tokenizer.tokenize(query);
        result.setQueryTokens(queryTokens);
        HashSet<LiteLabelSearchResult> suggestedLabels = new HashSet();
        if (!ignoreRelated) {
            suggestedLabels = this.getSuggestedLabels(queryTokens, page, maxResults, spaceKey);
            result.addMatchGroup(this.convertToContentNameMatchList(this.sortLabels(suggestedLabels)));
        }
        if ((missing = maxResults - suggestedLabels.size()) > 0 && queryTokens.size() == 2) {
            String prefix = ((QueryToken)queryTokens.get(0)).getText();
            Set<LiteLabelSearchResult> otherLabels = this.getLabelsStartingWithPrefix(prefix, missing, isTeamLabel);
            suggestedLabels.forEach(otherLabels::remove);
            if (otherLabels.size() > 0) {
                result.addMatchGroup(this.convertToContentNameMatchList(otherLabels));
            }
        }
        return result;
    }

    private Set<LiteLabelSearchResult> getLabelsStartingWithPrefix(String prefix, int limit, boolean isTeamLabel) {
        PartialList labelsPartial = isTeamLabel ? this.labelManager.findTeamLabelsByNamePrefix(0, limit, prefix) : this.labelManager.findGlobalLabelsByNamePrefix(0, limit, prefix);
        return labelsPartial.getList().stream().map(LiteLabelSearchResult::new).collect(Collectors.toSet());
    }

    private Set<LiteLabelSearchResult> getSuggestedLabels(List<QueryToken> queryTokens, AbstractPage entity, int maxResults, String spaceKey) {
        List<String> partialTokenStrings = this.getPartialTokenStrings(queryTokens);
        Set tokenExactMatchLabels = this.labelManager.getLabels(partialTokenStrings).stream().map(LiteLabelSearchResult::new).collect(Collectors.toSet());
        int limit = maxResults + (entity != null ? entity.getLabelCount() : 0);
        Set recentAndPopularLabels = new SuggestedLabels(this.labelManager).getSuggestedLiteLabels(entity != null ? entity.getSpaceKey() : spaceKey, (User)AuthenticatedUserThreadLocal.get(), limit);
        HashSet<LiteLabelSearchResult> suggestedLabels = new HashSet<LiteLabelSearchResult>();
        suggestedLabels.addAll(tokenExactMatchLabels);
        suggestedLabels.addAll(recentAndPopularLabels);
        return this.filterLabelsByQuery(suggestedLabels, queryTokens, maxResults);
    }

    private List<LiteLabelSearchResult> sortLabels(Set<LiteLabelSearchResult> labels) {
        ArrayList<LiteLabelSearchResult> labelList = new ArrayList<LiteLabelSearchResult>(labels);
        labelList.sort((o1, o2) -> collator.compare(o1.getName(), o2.getName()));
        return labelList;
    }

    private List<String> getPartialTokenStrings(List<QueryToken> queryTokens) {
        return queryTokens.stream().filter(input -> input.getType() == QueryToken.Type.PARTIAL).map(QueryToken::getText).collect(Collectors.toList());
    }

    private List<ContentNameMatch> convertToContentNameMatchList(Collection<LiteLabelSearchResult> labels) {
        return labels.stream().map(label -> new ContentNameMatch("label-suggestion", label.getName(), label.getUrlPath())).collect(Collectors.toList());
    }

    private Set<LiteLabelSearchResult> filterLabelsByQuery(Set<LiteLabelSearchResult> suggestedLabels, List<QueryToken> queryTokens, int maxResults) {
        HashSet<LiteLabelSearchResult> labels = new HashSet<LiteLabelSearchResult>();
        block0: for (LiteLabelSearchResult label : suggestedLabels) {
            if (labels.size() >= maxResults) break;
            for (QueryToken t : queryTokens) {
                if (!label.getName().startsWith(t.getText())) continue;
                labels.add(label);
                continue block0;
            }
        }
        return labels;
    }
}

