/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.contributors.analytics.ContributorsMacroMetricsEvent;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.contributors.search.PageSearcher;
import com.atlassian.confluence.contributors.util.AuthorRanking;
import com.atlassian.confluence.contributors.util.AuthorRankingSystem;
import com.atlassian.confluence.contributors.util.DefaultPageProcessor;
import com.atlassian.confluence.contributors.util.PageProcessor;
import com.atlassian.fugue.Either;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContributorsMacroHelper {
    private final PageProcessor pageProcessor;
    private final PageSearcher pageSearcher;

    public ContributorsMacroHelper(PageProcessor pageProcessor, PageSearcher pageSearcher) {
        this.pageProcessor = pageProcessor;
        this.pageSearcher = pageSearcher;
    }

    public Either<String, Map<String, Object>> getAuthorRankingsModel(ContributorsMacroMetricsEvent.Builder metrics, MacroParameterModel parameterModel) {
        List<AuthorRanking> authorRankings = this.getAuthorRankings(metrics, parameterModel);
        if (authorRankings.isEmpty()) {
            return Either.left((Object)parameterModel.getNoContributorsErrorMessage());
        }
        return Either.right(this.buildContributorsModel(authorRankings, metrics, parameterModel));
    }

    private List<AuthorRanking> getAuthorRankings(ContributorsMacroMetricsEvent.Builder metrics, MacroParameterModel params) {
        metrics.documentFetchStart();
        Iterable<Doc> documents = this.pageSearcher.getDocuments(params);
        metrics.documentFetchFinish(Iterables.size(documents));
        Set<MacroParameterModel.ContributorsMacroInclude> includes = params.getIncludeParams();
        metrics.userFetchStart(includes);
        AuthorRankingSystem rankingSystem = this.pageProcessor.process(documents, params.getRankType(AuthorRankingSystem.RankType.TOTAL_COUNT), PageProcessor.GroupBy.CONTRIBUTORS, includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.AUTHORS), includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.COMMENTS), includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.LABELS), includes.contains((Object)MacroParameterModel.ContributorsMacroInclude.WATCHES));
        List<AuthorRanking> rankedAuthors = rankingSystem.getRankedAuthors(params.isReverse());
        if (!params.isShowAnonymousContributions()) {
            rankedAuthors.remove(rankingSystem.getAuthorRanking(DefaultPageProcessor.ANONYMOUS_USER.getName()));
        }
        metrics.userFetchFinish(rankedAuthors.size());
        return rankedAuthors;
    }

    private Map<String, Object> buildContributorsModel(List<AuthorRanking> authorRankings, ContributorsMacroMetricsEvent.Builder metrics, MacroParameterModel params) {
        List<Object> hiddenUsers;
        List<AuthorRanking> visibleUsers;
        metrics.buildTemplateModelStart();
        MacroParameterModel.LayoutStyle layoutStyle = params.getLayoutStyle();
        int limit = params.getLimit();
        if (!MacroParameterModel.isLimitLess(limit) && limit < authorRankings.size()) {
            visibleUsers = authorRankings.subList(0, limit);
            hiddenUsers = authorRankings.subList(limit, authorRankings.size());
        } else {
            visibleUsers = authorRankings;
            hiddenUsers = Collections.emptyList();
        }
        ImmutableMap model = ImmutableMap.of((Object)"visibleContributors", visibleUsers, (Object)"hiddenContributors", hiddenUsers, (Object)"layoutStyle", (Object)layoutStyle.name(), (Object)"showCount", (Object)params.isShowCount(), (Object)"showTime", (Object)params.isShowTime());
        metrics.buildTemplateModelFinish(layoutStyle);
        return model;
    }
}

