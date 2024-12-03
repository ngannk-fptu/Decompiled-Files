/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.macro.xhtml.MacroManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.collections.MultiMap
 *  org.apache.commons.collections.map.MultiValueMap
 *  org.apache.commons.lang3.StringUtils
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.services;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.StreamableJiraIssuesMacro;
import com.atlassian.confluence.extra.jira.api.services.AsyncJiraIssueBatchService;
import com.atlassian.confluence.extra.jira.api.services.JiraIssueBatchService;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.extra.jira.executor.JiraExecutorFactory;
import com.atlassian.confluence.extra.jira.executor.StreamableMacroFutureTask;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.extra.jira.model.JiraResponseData;
import com.atlassian.confluence.extra.jira.util.JiraIssuePredicates;
import com.atlassian.confluence.extra.jira.util.JiraIssueUtil;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultAsyncJiraIssueBatchService
implements AsyncJiraIssueBatchService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAsyncJiraIssueBatchService.class);
    private static final String CACHE_NAME = "com.atlassian.confluence.extra.jira.services.DefaultAsyncJiraIssueBatchService";
    private static final int THREAD_POOL_SIZE = Integer.getInteger("confluence.jira.issues.executor.poolsize", 5);
    private static final int EXECUTOR_QUEUE_SIZE = Integer.getInteger("confluence.jira.issues.executor.queuesize", 1000);
    private static final int CACHE_EXPIRE_AFTER_WRITE = Integer.getInteger("confluence.extra.jira.cache.async.write.expire", 120);
    private static final int BATCH_SIZE = 25;
    private static final String ISSUE_KEY_TABLE_PREFIX = "issue-table-";
    private static final String ISSUE_KEY_COUNT_PREFIX = "issue-count-";
    private final JiraIssueBatchService jiraIssueBatchService;
    private final MacroManager macroManager;
    private final JiraExceptionHelper jiraExceptionHelper;
    private final Cache<ClientId, JiraResponseData> jiraIssuesCache;
    private final ExecutorService jiraIssueExecutor;
    private final ContentEntityManager contentEntityManager;
    private final JiraMacroFinderService jiraMacroFinderService;

    public DefaultAsyncJiraIssueBatchService(JiraIssueBatchService jiraIssueBatchService, MacroManager macroManager, JiraExecutorFactory executorFactory, JiraExceptionHelper jiraExceptionHelper, CacheFactory cacheFactory, ContentEntityManager contentEntityManager, JiraMacroFinderService jiraMacroFinderService) {
        this.jiraIssueBatchService = jiraIssueBatchService;
        this.macroManager = macroManager;
        this.jiraIssueExecutor = executorFactory.newLimitedThreadPool(THREAD_POOL_SIZE, EXECUTOR_QUEUE_SIZE, "JIM Marshaller");
        this.jiraExceptionHelper = jiraExceptionHelper;
        this.contentEntityManager = contentEntityManager;
        this.jiraMacroFinderService = jiraMacroFinderService;
        this.jiraIssuesCache = cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().local().expireAfterWrite((long)CACHE_EXPIRE_AFTER_WRITE, TimeUnit.SECONDS).maxEntries(500).build());
    }

    @Override
    public boolean reprocessRequest(ClientId clientId) throws XhtmlException, MacroExecutionException {
        if (!StringUtils.equals((CharSequence)clientId.getUserId(), (CharSequence)JiraIssueUtil.getUserKey(AuthenticatedUserThreadLocal.get()))) {
            return false;
        }
        StreamableJiraIssuesMacro jiraIssuesMacro = (StreamableJiraIssuesMacro)this.macroManager.getMacroByName("jira");
        ContentEntityObject entity = this.contentEntityManager.getById(Long.valueOf(clientId.getPageId()).longValue());
        if (clientId.getJiraIssuesType() == JiraIssuesMacro.JiraIssuesType.SINGLE) {
            ListMultimap<String, MacroDefinition> macroDefinitionByServer = jiraIssuesMacro.getSingleIssueMacroDefinitionByServer(entity);
            if (macroDefinitionByServer == null || macroDefinitionByServer.get((Object)clientId.getServerId()).isEmpty()) {
                return false;
            }
            this.processRequest(clientId, clientId.getServerId(), JiraIssueUtil.getIssueKeys(macroDefinitionByServer.get((Object)clientId.getServerId())), macroDefinitionByServer.get((Object)clientId.getServerId()), (ConversionContext)new DefaultConversionContext((RenderContext)entity.toPageContext()));
        } else {
            Predicate<MacroDefinition> predicate = clientId.getJiraIssuesType() == JiraIssuesMacro.JiraIssuesType.COUNT ? JiraIssuePredicates.isCountIssue : JiraIssuePredicates.isTableIssue;
            Predicate jqlPredicate = Predicates.and(predicate, macroDefinition -> StringUtils.equals((CharSequence)String.valueOf(macroDefinition.getParameters().get("jqlQuery")), (CharSequence)clientId.getJqlQuery()));
            List<MacroDefinition> macros = this.jiraMacroFinderService.findJiraMacros(entity, (Predicate<MacroDefinition>)jqlPredicate);
            if (CollectionUtils.isEmpty(macros)) {
                return false;
            }
            for (MacroDefinition macroDefinition2 : macros) {
                if (macroDefinition2.getDefaultParameterValue() != null) {
                    macroDefinition2.getParameters().put("0", macroDefinition2.getDefaultParameterValue());
                }
                jiraIssuesMacro.execute((Map<String, String>)macroDefinition2.getParameters(), "", (ConversionContext)new DefaultConversionContext((RenderContext)entity.toPageContext()));
            }
        }
        return true;
    }

    @Override
    public void processRequest(ClientId clientId, String serverId, Set<String> keys, List<MacroDefinition> macroDefinitions, ConversionContext conversionContext) {
        Optional<JiraResponseData> existingJiraReponseData = this.getExistingJiraReponseData(clientId);
        if (existingJiraReponseData.isPresent()) {
            existingJiraReponseData.get().increaseStackCount();
            return;
        }
        this.jiraIssuesCache.put((Object)clientId, (Object)new JiraResponseData(serverId, keys.size()));
        List batchRequests = Lists.partition((List)Lists.newArrayList(keys), (int)25);
        for (List batchRequest : batchRequests) {
            Callable<Map<String, List<String>>> jiraIssueBatchTask = this.buildBatchTask(clientId, serverId, batchRequest, macroDefinitions, conversionContext);
            try {
                this.jiraIssueExecutor.submit(jiraIssueBatchTask);
                logger.debug("Submitted task to thread pool. {}", (Object)this.jiraIssueExecutor.toString());
            }
            catch (RejectedExecutionException e) {
                logger.error("JIM Marshaller rejected task because there are more than {} tasks queued. {}", new Object[]{EXECUTOR_QUEUE_SIZE, this.jiraIssueExecutor.toString(), e});
                throw e;
            }
        }
    }

    @Override
    public void processRequestWithJql(ClientId clientId, Map<String, String> macroParams, ConversionContext conversionContext, ReadOnlyApplicationLink appLink) {
        Optional<JiraResponseData> existingJiraReponseData = this.getExistingJiraReponseData(clientId);
        if (existingJiraReponseData.isPresent()) {
            existingJiraReponseData.get().increaseStackCount();
            return;
        }
        StreamableMacro jiraIssuesMacro = (StreamableMacro)this.macroManager.getMacroByName("jira");
        JiraResponseData jiraResponseData = new JiraResponseData(appLink.getId().get(), 1);
        this.jiraIssuesCache.put((Object)clientId, (Object)jiraResponseData);
        Callable<Map> jiraTableCallable = () -> {
            DefaultConversionContext newConvertionContext = new DefaultConversionContext((RenderContext)conversionContext.getPageContext());
            newConvertionContext.setProperty("placeholder", (Object)false);
            newConvertionContext.setProperty("clientId", (Object)clientId.toString());
            StreamableMacroFutureTask streamableMacroFutureTask = new StreamableMacroFutureTask(this.jiraExceptionHelper, macroParams, (ConversionContext)newConvertionContext, jiraIssuesMacro);
            MultiValueMap jiraResultMap = new MultiValueMap();
            String asyncKey = (Boolean.parseBoolean((String)macroParams.get("count")) ? ISSUE_KEY_COUNT_PREFIX : ISSUE_KEY_TABLE_PREFIX) + clientId;
            jiraResultMap.put((Object)asyncKey, (Object)streamableMacroFutureTask.renderValue());
            this.getExistingJiraReponseData(clientId).ifPresent(arg_0 -> DefaultAsyncJiraIssueBatchService.lambda$processRequestWithJql$1((MultiMap)jiraResultMap, arg_0));
            return jiraResultMap;
        };
        this.jiraIssueExecutor.submit(jiraTableCallable);
    }

    @Override
    public JiraResponseData getAsyncJiraResults(ClientId clientId) {
        Optional<JiraResponseData> jiraResponseData = this.getExistingJiraReponseData(clientId);
        jiraResponseData.ifPresent(data -> {
            if (data.getStatus() == JiraResponseData.Status.COMPLETED && data.decreaseStackCount() == 0) {
                this.jiraIssuesCache.remove((Object)clientId);
            }
        });
        return jiraResponseData.orElse(null);
    }

    private Callable<Map<String, List<String>>> buildBatchTask(ClientId clientId, String serverId, List<String> batchRequest, List<MacroDefinition> macroDefinitions, ConversionContext conversionContext) {
        StreamableMacro jiraIssuesMacro = (StreamableMacro)this.macroManager.getMacroByName("jira");
        return () -> {
            Map<Object, Object> issueResultsMap;
            Exception exception = null;
            try {
                issueResultsMap = this.jiraIssueBatchService.getBatchResults(serverId, (Set<String>)ImmutableSet.copyOf((Collection)batchRequest), conversionContext);
            }
            catch (Exception e) {
                issueResultsMap = Maps.newHashMap();
                exception = e;
            }
            MultiValueMap jiraResultMap = new MultiValueMap();
            Map elementMap = (Map)issueResultsMap.get("elementMap");
            String jiraDisplayUrl = (String)issueResultsMap.get("jiraDisplayUrl");
            String jiraRpcUrl = (String)issueResultsMap.get("jiraRpcUrl");
            for (MacroDefinition macroDefinition : macroDefinitions) {
                String issueKey = macroDefinition.getParameter("key");
                if (!batchRequest.contains(issueKey)) continue;
                Element issueElement = elementMap == null ? null : (Element)elementMap.get(issueKey);
                jiraResultMap.put((Object)issueKey, (Object)new StreamableMacroFutureTask(this.jiraExceptionHelper, macroDefinition.getParameters(), conversionContext, jiraIssuesMacro, issueElement, jiraDisplayUrl, jiraRpcUrl, exception).renderValue());
            }
            this.getExistingJiraReponseData(clientId).ifPresent(arg_0 -> DefaultAsyncJiraIssueBatchService.lambda$buildBatchTask$4((MultiMap)jiraResultMap, arg_0));
            return jiraResultMap;
        };
    }

    private Optional<JiraResponseData> getExistingJiraReponseData(ClientId clientId) {
        return Optional.ofNullable((JiraResponseData)this.jiraIssuesCache.get((Object)clientId));
    }

    @PreDestroy
    public void destroy() {
        this.jiraIssueExecutor.shutdown();
    }

    private static /* synthetic */ void lambda$buildBatchTask$4(MultiMap jiraResultMap, JiraResponseData data) {
        data.add((Map<String, List<String>>)jiraResultMap);
    }

    private static /* synthetic */ void lambda$processRequestWithJql$1(MultiMap jiraResultMap, JiraResponseData value) {
        value.add((Map<String, List<String>>)jiraResultMap);
    }
}

