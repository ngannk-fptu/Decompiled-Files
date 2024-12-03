/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.WebhookSearchRequest
 *  com.atlassian.webhooks.WebhookSearchRequest$Builder
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.history.InvocationCounts
 *  com.atlassian.webhooks.history.InvocationHistory
 *  com.atlassian.webhooks.history.InvocationHistoryService
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks.analytics;

import com.atlassian.confluence.internal.webhooks.analytics.WebhooksDailySummaryPeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.WebhookSearchRequest;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.history.InvocationCounts;
import com.atlassian.webhooks.history.InvocationHistory;
import com.atlassian.webhooks.history.InvocationHistoryService;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebhooksDailySummaryPeriodicEventSupplier
implements PeriodicEventSupplier {
    private static final String ERRORS_KEY = "errors";
    private static final String FAILURES_KEY = "failures";
    private static final String SUCCESSES_KEY = "successes";
    private final WebhookService webhookService;
    private final InvocationHistoryService invocationHistoryService;

    @Autowired
    public WebhooksDailySummaryPeriodicEventSupplier(@ComponentImport WebhookService webhookService, @ComponentImport InvocationHistoryService invocationHistoryService) {
        this.webhookService = webhookService;
        this.invocationHistoryService = invocationHistoryService;
    }

    public PeriodicEvent call() {
        List hooks = this.webhookService.search(((WebhookSearchRequest.Builder)WebhookSearchRequest.builder().scope(WebhookScope.GLOBAL, new WebhookScope[0])).build());
        Map<String, Integer> todayInvocationStatsSummary = this.getInvocationStatsSummaryForDays(hooks, 0);
        Map<String, Integer> fromYesterdayInvocationStatsSummary = this.getInvocationStatsSummaryForDays(hooks, 1);
        return new WebhooksDailySummaryPeriodicEvent(fromYesterdayInvocationStatsSummary.get(ERRORS_KEY) - todayInvocationStatsSummary.get(ERRORS_KEY), fromYesterdayInvocationStatsSummary.get(FAILURES_KEY) - todayInvocationStatsSummary.get(FAILURES_KEY), fromYesterdayInvocationStatsSummary.get(SUCCESSES_KEY) - todayInvocationStatsSummary.get(SUCCESSES_KEY));
    }

    private Map<String, Integer> getInvocationStatsSummaryForDays(List<Webhook> hooks, int forDays) {
        Set ids = (Set)hooks.stream().map(Webhook::getId).collect(ImmutableSet.toImmutableSet());
        Map hookStats = this.invocationHistoryService.getByWebhookForDays((Collection)ids, forDays);
        int currentCumulativeErrors = 0;
        int currentCumulativeFailures = 0;
        int currentCumulativeSuccesses = 0;
        for (Webhook hook : hooks) {
            InvocationCounts invocationCounts = ((InvocationHistory)hookStats.get(hook.getId())).getCounts();
            currentCumulativeErrors += invocationCounts.getErrors();
            currentCumulativeFailures += invocationCounts.getFailures();
            currentCumulativeSuccesses += invocationCounts.getSuccesses();
        }
        HashMap<String, Integer> invocationStatsSummary = new HashMap<String, Integer>();
        invocationStatsSummary.put(ERRORS_KEY, currentCumulativeErrors);
        invocationStatsSummary.put(FAILURES_KEY, currentCumulativeFailures);
        invocationStatsSummary.put(SUCCESSES_KEY, currentCumulativeSuccesses);
        return invocationStatsSummary;
    }
}

