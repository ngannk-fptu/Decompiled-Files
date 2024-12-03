/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.search.util.OrderedResultsConstrainer
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.directory.rest;

import com.atlassian.crowd.directory.query.ODataTop;
import com.atlassian.crowd.directory.rest.AzureAdRestClient;
import com.atlassian.crowd.directory.rest.delta.GraphDeltaQueryResult;
import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import com.atlassian.crowd.directory.rest.entity.delta.PageableDeltaQueryGraphList;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.util.OrderedResultsConstrainer;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class AzureAdPagingWrapper {
    private final AzureAdRestClient azureAdRestClient;

    public AzureAdPagingWrapper(AzureAdRestClient azureAdRestClient) {
        this.azureAdRestClient = azureAdRestClient;
    }

    public <V extends PageableGraphList<T>, T> List<T> fetchAppropriateAmountOfResults(V firstPage, int startIndex, int maxResults) throws OperationFailedException {
        return this.fetchResults(firstPage, null, startIndex, maxResults);
    }

    public <V extends PageableGraphList<T>, T> List<T> fetchAllMatchingResults(V firstPage, Predicate<T> filter) throws OperationFailedException {
        return this.fetchResults(firstPage, filter, 0, -1);
    }

    public <V extends PageableGraphList<T>, T> List<T> fetchAllResults(V firstPage) throws OperationFailedException {
        return this.fetchResults(firstPage, null, 0, -1);
    }

    public <V extends PageableGraphList<T>, T> Optional<T> pageForElement(V firstPage, Predicate<T> predicate) throws OperationFailedException {
        List<T> results = this.fetchResults(firstPage, predicate, 0, 1);
        return Optional.ofNullable(Iterables.getFirst(results, null));
    }

    public <V extends PageableDeltaQueryGraphList<T>, T> GraphDeltaQueryResult<T> fetchAllDeltaQueryResults(V firstPage) throws OperationFailedException {
        Pair<List<T>, V> result = this.fetchResultsAndLastPage(firstPage, null, 0, -1);
        return new GraphDeltaQueryResult((List)result.getLeft(), ((PageableDeltaQueryGraphList)result.getRight()).getDeltaLink());
    }

    private <V extends PageableGraphList<T>, T> List<T> fetchResults(V firstPage, Predicate<T> filter, int startIndex, int maxResults) throws OperationFailedException {
        return (List)this.fetchResultsAndLastPage(firstPage, filter, startIndex, maxResults).getLeft();
    }

    private <V extends PageableGraphList<T>, T> Pair<List<T>, V> fetchResultsAndLastPage(V firstPage, Predicate<T> filter, int startIndex, int maxResults) throws OperationFailedException {
        boolean fullPageQuery = filter != null || maxResults == -1;
        OrderedResultsConstrainer constrainer = new OrderedResultsConstrainer(filter, startIndex, maxResults);
        Object currentPage = firstPage;
        while (true) {
            constrainer.addAll(currentPage.getEntries());
            if (constrainer.getRemainingCount() == 0 || StringUtils.isBlank((CharSequence)currentPage.getNextLink())) break;
            ODataTop top = fullPageQuery ? ODataTop.FULL_PAGE : ODataTop.forSize(constrainer.getRemainingCount());
            currentPage = this.azureAdRestClient.getNextPage(currentPage.getNextLink(), currentPage.getClass(), top);
        }
        return Pair.of((Object)constrainer.toList(), currentPage);
    }
}

