/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.webhooks.internal.rest.RestWebhook
 *  com.atlassian.webhooks.internal.rest.history.RestHistoricalInvocation
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.plugins.restapi.examples;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.webhooks.internal.rest.RestWebhook;
import com.atlassian.webhooks.internal.rest.history.RestHistoricalInvocation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebhookExamples {
    public static final Map<String, Object> INVOCATION_ERROR = WebhookExamples.makeExampleInvocationError();
    public static final Map<String, Object> INVOCATION_FAILURE = WebhookExamples.makeExampleInvocationFailure();
    public static final Map<String, Object> INVOCATION_SUCCESS = WebhookExamples.makeExampleInvocationSuccess();
    public static final Map<String, Object> INVOCATION_HISTORY = WebhookExamples.makeExampleHistory();
    public static final PageResponse<Object> PAGE_OF_WEBHOOKS = WebhookExamples.makeExampleWebhooksPage();
    public static final Map<String, Object> WEBHOOK = WebhookExamples.makeExampleWebhook();

    private static Map<String, Object> makeExampleInvocationError() {
        LinkedHashMap<String, Object> example = new LinkedHashMap<String, Object>((Map<String, Object>)RestHistoricalInvocation.EXAMPLE_ERROR);
        example.put("event", "page_updated");
        return example;
    }

    private static Map<String, Object> makeExampleInvocationFailure() {
        LinkedHashMap<String, Object> example = new LinkedHashMap<String, Object>((Map<String, Object>)RestHistoricalInvocation.EXAMPLE_FAILURE);
        example.put("event", "page_updated");
        return example;
    }

    private static Map<String, Object> makeExampleHistory() {
        return ImmutableMap.of((Object)"lastSuccess", WebhookExamples.makeExampleInvocationSuccess(), (Object)"lastError", WebhookExamples.makeExampleInvocationError(), (Object)"lastFailure", WebhookExamples.makeExampleInvocationFailure(), (Object)"count", (Object)143);
    }

    private static Map<String, Object> makeExampleInvocationSuccess() {
        LinkedHashMap<String, Object> example = new LinkedHashMap<String, Object>((Map<String, Object>)RestHistoricalInvocation.EXAMPLE_SUCCESS);
        example.put("event", "page_updated");
        return example;
    }

    private static Map<String, Object> makeExampleWebhook() {
        LinkedHashMap<String, Object> example = new LinkedHashMap<String, Object>((Map<String, Object>)RestWebhook.EXAMPLE);
        example.put("events", ImmutableList.of((Object)"page_created", (Object)"page_removed", (Object)"page_updated"));
        return example;
    }

    private static PageResponse<Object> makeExampleWebhooksPage() {
        return RestList.newRestList((PageRequest)null).results((List)ImmutableList.of(WebhookExamples.makeExampleWebhook()), false).build();
    }
}

