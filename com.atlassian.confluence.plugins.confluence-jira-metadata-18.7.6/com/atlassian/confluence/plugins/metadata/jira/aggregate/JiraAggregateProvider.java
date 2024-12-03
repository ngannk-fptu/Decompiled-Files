/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.spi.application.ApplicationIdUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.aggregate;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.spi.application.ApplicationIdUtil;
import com.atlassian.confluence.plugins.metadata.jira.exception.JiraMetadataException;
import com.atlassian.confluence.plugins.metadata.jira.helper.CapabilitiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;
import com.atlassian.confluence.plugins.metadata.jira.util.GlobalPageIdUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraAggregateProvider {
    private static final Logger log = LoggerFactory.getLogger(JiraAggregateProvider.class);
    private static final List<String> countedTypes = Arrays.asList("issue", "sprints", "version");
    private static final int AGGREGATE_TIMEOUT_SECS = Integer.getInteger("jira.metadata.aggregate.provider.timeout.secs", 5);
    private static final String THREAD_NAME_PREFIX = "JIRAMetadataPlugin_AggregateProvider";
    private static final int EXECUTOR_POOL_SIZE = Integer.getInteger("jira.metadata.aggregate.provider.executor.pool.size", 5);
    private static final int EXECUTOR_TASK_QUEUE_SIZE = Integer.getInteger("jira.metadata.aggregate.provider.executor.queue.size", 100);
    private final RequestFactory requestFactory;
    private final HostApplication hostApplication;
    private final JsonParser parser;
    private final ExecutorService executorService = new ThreadPoolExecutor(EXECUTOR_POOL_SIZE, EXECUTOR_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(EXECUTOR_TASK_QUEUE_SIZE), ThreadFactories.namedThreadFactory((String)"JIRAMetadataPlugin_AggregateProvider", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    protected final CapabilitiesHelper capabilitiesHelper;

    @Autowired
    public JiraAggregateProvider(RequestFactory requestFactory, CapabilitiesHelper capabilitiesHelper, HostApplication hostApplication) {
        this.requestFactory = requestFactory;
        this.capabilitiesHelper = capabilitiesHelper;
        this.hostApplication = hostApplication;
        this.parser = new JsonParser();
    }

    public JiraAggregate getAggregateData(long pageId, JiraMetadataErrorHelper errorHelper) {
        ArrayList<Callable<JiraAggregate>> aggregateCallables = new ArrayList<Callable<JiraAggregate>>();
        log.debug("My base url {}, my appId {}", (Object)this.hostApplication.getBaseUrl(), (Object)ApplicationIdUtil.generate((URI)this.hostApplication.getBaseUrl()));
        for (ReadOnlyApplicationLink applink : this.capabilitiesHelper.getAggregateCapableJiraLinks()) {
            aggregateCallables.add(() -> {
                String requestUrl = "/rest/remote-link-aggregation/1/aggregation?globalId=" + HtmlUtil.urlEncode((String)GlobalPageIdUtil.generateGlobalPageId(ApplicationIdUtil.generate((URI)this.hostApplication.getBaseUrl()), pageId)) + "&globalId=" + HtmlUtil.urlEncode((String)GlobalPageIdUtil.generateGlobalPageId(this.hostApplication.getId(), pageId));
                return this.getDataForSingleApplink(applink, requestUrl, errorHelper);
            });
        }
        return this.consolidate(this.retrieveFutures(aggregateCallables, errorHelper));
    }

    @PreDestroy
    public void destroy() {
        this.executorService.shutdownNow();
    }

    private JiraAggregate getDataForSingleApplink(ReadOnlyApplicationLink applink, String requestUrl, JiraMetadataErrorHelper errorHelper) {
        try {
            log.debug("Retrieving aggregate data for link {} ; requestURL: {}", (Object)applink.getName(), (Object)requestUrl);
            String response = this.requestFactory.createRequest(Request.MethodType.GET, applink.getRpcUrl() + requestUrl).execute();
            log.debug("The response is:\n {}", (Object)response);
            Iterable aggregates = this.parse(response, errorHelper).stream().filter(jiraAggregate -> jiraAggregate != null && countedTypes.contains(jiraAggregate.getEntityType())).collect(Collectors.toList());
            return this.consolidate(aggregates);
        }
        catch (Exception e) {
            errorHelper.handleException(e, applink);
            return null;
        }
    }

    private List<JiraAggregate> retrieveFutures(List<Callable<JiraAggregate>> callables, JiraMetadataErrorHelper errorHelper) {
        List<Future<JiraAggregate>> futures;
        ArrayList<JiraAggregate> aggregates = new ArrayList<JiraAggregate>();
        try {
            futures = this.executorService.invokeAll(callables, AGGREGATE_TIMEOUT_SECS, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            errorHelper.handleException(e);
            return aggregates;
        }
        for (Future<JiraAggregate> future : futures) {
            if (future.isCancelled()) {
                aggregates.add(null);
                continue;
            }
            try {
                JiraAggregate aggregate = future.get(AGGREGATE_TIMEOUT_SECS, TimeUnit.SECONDS);
                aggregates.add(aggregate);
            }
            catch (Exception e) {
                errorHelper.handleException(e);
            }
        }
        return aggregates;
    }

    JiraAggregate consolidate(Iterable<JiraAggregate> aggregates) {
        int count = 0;
        JiraAggregate singleEntity = null;
        boolean incomplete = false;
        for (JiraAggregate aggregate : aggregates) {
            if (aggregate != null) {
                count += aggregate.getCount();
                if (aggregate.isSingleEntity()) {
                    singleEntity = aggregate;
                }
                if (!aggregate.isIncomplete()) continue;
                incomplete = true;
                continue;
            }
            incomplete = true;
        }
        if (singleEntity != null && count == 1) {
            return new JiraAggregate(count, singleEntity.getEntityType(), singleEntity.getEntityName(), singleEntity.getEntityUrl(), incomplete);
        }
        return new JiraAggregate(count, incomplete);
    }

    List<JiraAggregate> parse(String response, JiraMetadataErrorHelper errorHelper) {
        ArrayList<JiraAggregate> aggregates = new ArrayList<JiraAggregate>();
        try {
            JsonObject responseObject = this.parser.parse(response).getAsJsonObject().getAsJsonObject("targets");
            responseObject.entrySet();
            for (Map.Entry stringJsonElementEntry : responseObject.entrySet()) {
                JsonArray responseArray = (JsonArray)stringJsonElementEntry.getValue();
                if (responseArray == null) continue;
                for (JsonElement result : responseArray) {
                    JsonObject resultObject = result.getAsJsonObject();
                    JsonObject typeDetails = resultObject.getAsJsonObject("type");
                    JsonArray objectsArray = null;
                    JsonElement objectsElement = resultObject.get("objects");
                    if (objectsElement.isJsonArray()) {
                        objectsArray = resultObject.getAsJsonArray("objects");
                    }
                    if (objectsArray != null && objectsArray.size() > 0) {
                        JsonObject entity = objectsArray.get(0).getAsJsonObject();
                        aggregates.add(new JiraAggregate(this.jsonToInt(resultObject.get("count")), this.jsonToString(typeDetails.get("id")), this.jsonToString(entity.get("name")), this.jsonToString(entity.get("url")), false));
                        continue;
                    }
                    aggregates.add(new JiraAggregate(this.jsonToInt(resultObject.get("count")), this.jsonToString(typeDetails.get("id"))));
                }
            }
        }
        catch (Exception e) {
            errorHelper.handleException(new JiraMetadataException(JiraMetadataErrorHelper.Status.RESPONSE_UNPARSABLE, (Throwable)e));
        }
        return aggregates;
    }

    private int jsonToInt(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return 0;
        }
        return element.getAsInt();
    }

    private String jsonToString(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        return element.getAsString();
    }
}

