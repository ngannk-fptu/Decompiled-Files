/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.AsyncRenderSafe
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.ResourceAware
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  org.apache.commons.collections.MapUtils
 *  org.apache.commons.lang3.concurrent.ConcurrentUtils
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.jira.ApplicationLinkResolver;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.AsyncJiraIssueBatchService;
import com.atlassian.confluence.extra.jira.api.services.JiraCacheManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssueBatchService;
import com.atlassian.confluence.extra.jira.api.services.JiraIssueSortingManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.extra.jira.api.services.TrustedApplicationConfig;
import com.atlassian.confluence.extra.jira.exception.UnsupportedJiraServerException;
import com.atlassian.confluence.extra.jira.executor.FutureStreamableConverter;
import com.atlassian.confluence.extra.jira.executor.StreamableMacroExecutor;
import com.atlassian.confluence.extra.jira.executor.StreamableMacroFutureTask;
import com.atlassian.confluence.extra.jira.helper.ImagePlaceHolderHelper;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.extra.jira.model.EntityServerCompositeKey;
import com.atlassian.confluence.extra.jira.model.JiraBatchRequestData;
import com.atlassian.confluence.extra.jira.request.JiraRequestData;
import com.atlassian.confluence.extra.jira.request.SingleJiraIssuesThreadLocalAccessor;
import com.atlassian.confluence.extra.jira.util.JiraIssuePredicates;
import com.atlassian.confluence.extra.jira.util.JiraIssueUtil;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.AsyncRenderSafe;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.ResourceAware;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AsyncRenderSafe
public class StreamableJiraIssuesMacro
extends JiraIssuesMacro
implements StreamableMacro,
EditorImagePlaceholder,
ResourceAware {
    public static final int THREAD_POOL_SIZE = Integer.getInteger("jira.executor.threadpool.size", 4);
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamableJiraIssuesMacro.class);
    private final StreamableMacroExecutor executorService;
    private final JiraMacroFinderService jiraMacroFinderService;
    private final JiraIssueBatchService jiraIssueBatchService;
    private final PageBuilderService pageBuilderService;
    private final AsyncJiraIssueBatchService asyncJiraIssueBatchService;

    public StreamableJiraIssuesMacro(I18nResolver i18nResolver, JiraIssuesManager jiraIssuesManager, SettingsManager settingsManager, JiraIssuesColumnManager jiraIssuesColumnManager, TrustedApplicationConfig trustedApplicationConfig, PermissionManager permissionManager, ApplicationLinkResolver applicationLinkResolver, MacroMarshallingFactory macroMarshallingFactory, JiraCacheManager jiraCacheManager, ImagePlaceHolderHelper imagePlaceHolderHelper, FormatSettingsManager formatSettingsManager, JiraIssueSortingManager jiraIssueSortingManager, JiraExceptionHelper jiraExceptionHelper, LocaleManager localeManager, StreamableMacroExecutor executorService, JiraMacroFinderService jiraMacroFinderService, JiraIssueBatchService jiraIssueBatchService, PageBuilderService pageBuilderService, AsyncJiraIssueBatchService asyncJiraIssueBatchService, DarkFeatureManager darkFeatureManager, UserAccessor userAccessor, VelocityHelperService velocityHelperService) {
        super(i18nResolver, jiraIssuesManager, settingsManager, jiraIssuesColumnManager, trustedApplicationConfig, permissionManager, applicationLinkResolver, macroMarshallingFactory, jiraCacheManager, imagePlaceHolderHelper, formatSettingsManager, jiraIssueSortingManager, jiraExceptionHelper, localeManager, asyncJiraIssueBatchService, darkFeatureManager, userAccessor, velocityHelperService);
        this.executorService = executorService;
        this.jiraMacroFinderService = jiraMacroFinderService;
        this.jiraIssueBatchService = jiraIssueBatchService;
        this.pageBuilderService = pageBuilderService;
        this.asyncJiraIssueBatchService = asyncJiraIssueBatchService;
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable body, ConversionContext conversionContext) throws MacroExecutionException {
        ContentEntityObject entity = conversionContext.getEntity();
        if (parameters != null && JiraUtil.getSingleIssueKey(parameters) != null && entity != null) {
            this.trySingleIssuesBatching(conversionContext, entity);
        } else if (this.dynamicRenderModeEnabled(parameters, conversionContext)) {
            this.pageBuilderService.assembler().resources().requireWebResource("confluence.extra.jira:flexigrid-resources");
        }
        Future<String> futureResult = this.marshallMacroInBackground(parameters, conversionContext, entity);
        return new FutureStreamableConverter.Builder(futureResult, conversionContext, this.getI18nResolver(), this.jiraExceptionHelper).executionErrorMsg("jiraissues.error.execution").executionTimeoutErrorMsg("jiraissues.error.timeout.execution").connectionTimeoutErrorMsg("jiraissues.error.timeout.connection").interruptedErrorMsg("jiraissues.error.interrupted").build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void trySingleIssuesBatching(ConversionContext conversionContext, ContentEntityObject entity) throws MacroExecutionException {
        long entityId = entity.getId();
        if (conversionContext.getOutputDeviceType().equals("email")) {
            return;
        }
        if (SingleJiraIssuesThreadLocalAccessor.isBatchProcessed(entityId).booleanValue()) {
            return;
        }
        long batchStart = System.currentTimeMillis();
        try {
            try {
                ListMultimap<String, MacroDefinition> macroDefinitionByServer = this.getSingleIssueMacroDefinitionByServer(entity);
                for (String serverId : macroDefinitionByServer.keySet()) {
                    Set<String> keys = JiraIssueUtil.getIssueKeys(macroDefinitionByServer.get((Object)serverId));
                    JiraBatchRequestData jiraBatchRequestData = new JiraBatchRequestData();
                    try {
                        Map<String, Object> resultsMap;
                        if (this.isAsyncSupport(conversionContext)) {
                            ClientId clientId = ClientId.fromElement(JiraIssuesMacro.JiraIssuesType.SINGLE, serverId, entity.getIdAsString(), JiraIssueUtil.getUserKey(AuthenticatedUserThreadLocal.get()));
                            this.asyncJiraIssueBatchService.processRequest(clientId, serverId, keys, macroDefinitionByServer.get((Object)serverId), conversionContext);
                            resultsMap = this.jiraIssueBatchService.getPlaceHolderBatchResults(clientId, serverId, keys, conversionContext);
                        } else {
                            resultsMap = this.jiraIssueBatchService.getBatchResults(serverId, keys, conversionContext);
                        }
                        if (!MapUtils.isNotEmpty(resultsMap)) continue;
                        Map elementMap = (Map)resultsMap.get("elementMap");
                        String jiraDisplayUrl = (String)resultsMap.get("jiraDisplayUrl");
                        jiraBatchRequestData.setElementMap(elementMap);
                        jiraBatchRequestData.setDisplayUrl(jiraDisplayUrl);
                    }
                    catch (UnsupportedJiraServerException | MacroExecutionException macroExecutionException) {
                        jiraBatchRequestData.setException((Exception)macroExecutionException);
                    }
                    finally {
                        SingleJiraIssuesThreadLocalAccessor.putJiraBatchRequestData(new EntityServerCompositeKey(entityId, serverId), jiraBatchRequestData);
                    }
                }
            }
            catch (XhtmlException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(e.toString());
                }
                throw new MacroExecutionException(e.getCause());
            }
        }
        finally {
            SingleJiraIssuesThreadLocalAccessor.setBatchProcessedMapThreadLocal(entityId, Boolean.TRUE);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("******* batch time = {}", (Object)(System.currentTimeMillis() - batchStart));
            }
        }
    }

    public ListMultimap<String, MacroDefinition> getSingleIssueMacroDefinitionByServer(ContentEntityObject entity) throws XhtmlException {
        List<MacroDefinition> singleIssueMacroDefinitions = this.jiraMacroFinderService.findJiraMacros(entity, JiraIssuePredicates.isSingleIssue);
        ArrayListMultimap macroDefinitionByServer = ArrayListMultimap.create();
        for (MacroDefinition singleIssueMacroDefinition : singleIssueMacroDefinitions) {
            String key = singleIssueMacroDefinition.getParameter("key");
            String serverId = null;
            try {
                serverId = this.getServerIdFromKey(singleIssueMacroDefinition.getParameters(), key, (ConversionContext)new DefaultConversionContext((RenderContext)entity.toPageContext()));
            }
            catch (MacroExecutionException e) {
                LOGGER.warn(e.getMessage(), (Throwable)e);
            }
            if (serverId == null) continue;
            macroDefinitionByServer.put((Object)serverId, (Object)singleIssueMacroDefinition);
        }
        return macroDefinitionByServer;
    }

    private Future<String> marshallMacroInBackground(Map<String, String> parameters, ConversionContext conversionContext, ContentEntityObject entity) throws MacroExecutionException {
        block6: {
            JiraRequestData jiraRequestData = JiraIssueUtil.parseRequestData(parameters, this.getI18nResolver());
            JiraIssuesMacro.JiraIssuesType issuesType = JiraUtil.getJiraIssuesType(parameters, jiraRequestData.getRequestType(), jiraRequestData.getRequestData());
            if (issuesType == JiraIssuesMacro.JiraIssuesType.SINGLE) {
                try {
                    String key = JiraUtil.getSingleIssueKey(parameters);
                    String serverId = this.getServerIdFromKey(parameters, key, conversionContext);
                    if (serverId != null) {
                        long entityId = entity.getId();
                        JiraBatchRequestData jiraBatchRequestData = SingleJiraIssuesThreadLocalAccessor.getJiraBatchRequestData(new EntityServerCompositeKey(entityId, serverId));
                        if (jiraBatchRequestData != null) {
                            Map<String, Element> elementMap = jiraBatchRequestData.getElementMap();
                            Element element = elementMap != null ? elementMap.get(key) : null;
                            String jiraDisplayUrl = jiraBatchRequestData.getDisplayUrl();
                            Exception exception = jiraBatchRequestData.getException();
                            return ConcurrentUtils.constantFuture((Object)new StreamableMacroFutureTask(this.jiraExceptionHelper, parameters, conversionContext, this, element, jiraDisplayUrl, null, exception).renderValue());
                        }
                        break block6;
                    }
                    return this.executorService.submit(new StreamableMacroFutureTask(this.jiraExceptionHelper, parameters, conversionContext, this));
                }
                catch (MacroExecutionException macroExecutionException) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(macroExecutionException.toString());
                    }
                    String exceptionMessage = macroExecutionException.getMessage();
                    return ConcurrentUtils.constantFuture((Object)this.jiraExceptionHelper.renderBatchingJIMExceptionMessage(exceptionMessage, parameters));
                }
            }
        }
        return ConcurrentUtils.constantFuture((Object)new StreamableMacroFutureTask(this.jiraExceptionHelper, parameters, conversionContext, this).renderValue());
    }

    private String getServerIdFromKey(Map<String, String> parameters, String issueKey, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            ReadOnlyApplicationLink applicationLink = this.applicationLinkResolver.resolve(JiraIssuesMacro.Type.KEY, issueKey, parameters);
            if (applicationLink != null) {
                return applicationLink.getId().toString();
            }
        }
        catch (TypeNotInstalledException e) {
            this.jiraExceptionHelper.throwMacroExecutionException((Exception)((Object)e), conversionContext);
        }
        return null;
    }
}

