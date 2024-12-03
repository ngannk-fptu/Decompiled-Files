/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.spaces.actions.SpaceAdminAction
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.BaseJsonNode
 *  com.fasterxml.jackson.databind.node.ObjectNode
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.plugins.gatekeeper.concurrent.ManagedThreadPoolExecutor;
import com.atlassian.confluence.plugins.gatekeeper.dto.PermissionExplanations;
import com.atlassian.confluence.plugins.gatekeeper.dto.TinyOwnerDto;
import com.atlassian.confluence.plugins.gatekeeper.dto.TinySpaceDto;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.ExplainingEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.exception.EvaluatorCacheNotInitializedException;
import com.atlassian.confluence.plugins.gatekeeper.export.Exporter;
import com.atlassian.confluence.plugins.gatekeeper.export.ExporterFactory;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.model.Overview;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.Evaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationLevel;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExplainingEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.GlobalEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.SpaceEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.EvaluationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.PreEvaluationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingState;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.GroupFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.SpaceFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.UserFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplainedPermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.RefinedExplanation;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.AddonGlobal;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.ActionUtil;
import com.atlassian.confluence.plugins.gatekeeper.util.FormValidator;
import com.atlassian.confluence.plugins.gatekeeper.util.HttpUtil;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPermissionsAction
extends SpaceAdminAction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPermissionsAction.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static String allPermissionsMetadata;
    private final EvaluationLevel evaluationLevel;
    private boolean licensed;
    private String pluginKey;
    private String spaceFilter;
    private String groupFilter;
    private String userFilter;
    private int maxRows = 10;
    private boolean alwaysShowAnonymous;
    private boolean excludeDisabled;
    private boolean excludeOwnersNoPermissions;
    private boolean excludeSpacesNoPermissions;
    private String type;
    private String spaceKey;
    private String username;
    private String anonymous;
    private String groupname;
    private int start;
    private int count;
    private String pageTitle;
    private long pageId;
    private String exportFormat;
    private String exportCsvDelimiter;
    private String exportCsvCustomDelimiter;
    private String exportSpaceDetailsFormat;
    private String exportCustomSpaceDetailsFormat;
    private boolean hideFixColumnHeaders;
    private List<TinySpace> spaceList;
    private String jobId;
    private AddonGlobal addonGlobal;
    private AddonLicenseManager licenseManager;
    private AccessModeService accessModeService;
    private Overview overview = null;
    private PermissionSet explainedPermissions;
    private TransactionTemplate transactionTemplate;
    private DarkFeatureManager darkFeatureManager;
    private ConfluenceService confluenceService;
    private SpaceLogoManager spaceLogoManager;
    private EvaluatorCacheHolder evaluatorCacheHolder;
    private ExporterFactory exporterFactory;
    private EvaluationExpiryChecker evaluationExpiryChecker;

    public boolean isPermitted() {
        return this.checkLicense() && super.isPermitted();
    }

    protected AbstractPermissionsAction(EvaluationLevel evaluationLevel) {
        this.evaluationLevel = evaluationLevel;
        if (allPermissionsMetadata == null) {
            try {
                allPermissionsMetadata = OBJECT_MAPPER.writeValueAsString(Permissions.ALL_PERMISSIONS);
            }
            catch (JsonProcessingException jsonProcessingException) {
                // empty catch block
            }
        }
    }

    protected boolean checkLicense() {
        this.licensed = this.licenseManager.getLicenseInfo().isValid() && this.licenseManager.getLicenseInfo().isDCFeatureLicensed();
        return this.licensed;
    }

    public String initEvaluator() throws IOException {
        Evaluation evaluation;
        if (!this.checkLicense()) {
            logger.warn("initEvaluator called - invalid license");
            return "none";
        }
        OwnerType ownerType = this.getOwnerType();
        if (ownerType == null) {
            ActionUtil.sendBadResponse();
            return "none";
        }
        FormValidator formValidator = new FormValidator();
        TinyPage tinyPage = null;
        TinySpace tinySpace = null;
        if (this.evaluationLevel == EvaluationLevel.EVALUATE_SPACE) {
            tinySpace = this.confluenceService.getSpace(this.spaceKey);
            if (tinySpace == null) {
                return "none";
            }
            if (this.pageId > 0L) {
                tinyPage = this.confluenceService.getPage(this.spaceKey, this.pageId);
                if (tinyPage == null) {
                    formValidator.addError("page-input", this.getText("com.atlassian.confluence.plugins.gatekeeper.page-not-found.error"));
                }
            } else if (StringUtils.isNotBlank((CharSequence)this.pageTitle) && (tinyPage = this.confluenceService.getPage(this.spaceKey, this.pageTitle)) == null) {
                formValidator.addError("page-input", this.getText("com.atlassian.confluence.plugins.gatekeeper.page-not-found.error"));
            }
        }
        EvaluationOutput evaluationOutput = StringUtils.isBlank((CharSequence)this.exportFormat) ? EvaluationOutput.ADMIN_VIEW : EvaluationOutput.EXPORT;
        ExportSettings exportSettings = null;
        if (evaluationOutput == EvaluationOutput.EXPORT) {
            exportSettings = new ExportSettings(this.exportFormat, this.hideFixColumnHeaders, this.exportCsvDelimiter, this.exportCsvCustomDelimiter, this.exportSpaceDetailsFormat, this.exportCustomSpaceDetailsFormat);
            if ("custom".equals(this.exportCsvDelimiter) && StringUtils.isBlank((CharSequence)this.exportCsvCustomDelimiter)) {
                formValidator.addError("export-csv-delimiter", this.getText("com.atlassian.confluence.plugins.gatekeeper.export-csv-custom-delimiter-empty.error"));
            }
            if ("custom".equals(this.exportSpaceDetailsFormat)) {
                if (StringUtils.isBlank((CharSequence)this.exportCustomSpaceDetailsFormat)) {
                    formValidator.addError("export-space-details-format", this.getText("com.atlassian.confluence.plugins.gatekeeper.export-custom-space-details-format-empty.error"));
                } else if (!this.checkValidVelocitySyntax(this.exportCustomSpaceDetailsFormat)) {
                    formValidator.addError("export-space-details-format", this.getText("com.atlassian.confluence.plugins.gatekeeper.export-custom-space-details-format-invalid.error"));
                }
            }
        }
        if (!formValidator.isEmpty()) {
            ActionUtil.sendJsonResponse((BaseJsonNode)formValidator.toJson());
            return "none";
        }
        if (this.evaluationLevel == EvaluationLevel.EVALUATE_GLOBAL) {
            evaluation = new GlobalEvaluation(ownerType, evaluationOutput);
            ((GlobalEvaluation)evaluation).setSpaceFilter(new SpaceFilter(this.spaceFilter));
        } else {
            evaluation = new SpaceEvaluation(ownerType, evaluationOutput, tinySpace, tinyPage);
        }
        evaluation.setGatekeeperV2Enabled(this.darkFeatureManager.isEnabledForCurrentUser("gatekeeper-ui-v2").orElse(false));
        evaluation.setAlwaysShowAnonymous(this.alwaysShowAnonymous);
        evaluation.setExcludeDisabled(this.excludeDisabled);
        evaluation.setExcludeOwnersNoPermissions(this.excludeOwnersNoPermissions);
        evaluation.setExcludeSpacesNoPermissions(this.excludeSpacesNoPermissions);
        switch (ownerType) {
            case TYPE_USER: {
                evaluation.setUserFilter(new UserFilter(this.userFilter));
                break;
            }
            case TYPE_GROUP: {
                evaluation.setGroupFilter(new GroupFilter(this.groupFilter));
            }
        }
        if (evaluationOutput == EvaluationOutput.EXPORT) {
            logger.debug("Export settings: {}", (Object)exportSettings);
            evaluation.setExportSettings(exportSettings);
        }
        Evaluator evaluator = evaluation.createEvaluator(this.transactionTemplate, this.confluenceService, this.evaluatorCacheHolder, this.evaluationExpiryChecker);
        String id = this.addonGlobal.getEvaluationThreadPoolExecutor().queue(evaluator);
        this.addonGlobal.getEvaluatorJobs().put((Object)id, (Object)evaluator);
        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        result.put("status", "success");
        result.put("jobId", id);
        ActionUtil.sendJsonResponse((BaseJsonNode)result);
        return "none";
    }

    public String poll() throws IOException {
        ManagedThreadPoolExecutor<Evaluator, PreEvaluationResult> threadPool = this.addonGlobal.getEvaluationThreadPoolExecutor();
        Evaluator evaluator = threadPool.getTask(this.jobId);
        if (evaluator == null) {
            logger.error("Job {} is not found!", (Object)this.jobId);
            ActionUtil.sendBadResponse("Evaluation is not found!");
        } else {
            ProcessingState processingState = evaluator.getProcessingState();
            String result = OBJECT_MAPPER.writeValueAsString((Object)processingState);
            ActionUtil.sendJsonResponse(result);
        }
        return "none";
    }

    public String download() throws IOException {
        ManagedThreadPoolExecutor<Evaluator, PreEvaluationResult> threadPool = this.addonGlobal.getEvaluationThreadPoolExecutor();
        Evaluator evaluator = threadPool.getTask(this.jobId);
        if (evaluator == null || !evaluator.getProcessingState().isDone()) {
            logger.error("Job {} is not available for download!", (Object)this.jobId);
            ActionUtil.sendBadResponse("Evaluation is unavailable!");
        } else {
            try {
                FutureTask<PreEvaluationResult> evaluationResultFuture = threadPool.getFuture(this.jobId);
                PreEvaluationResult preEvaluationResult = (PreEvaluationResult)evaluationResultFuture.get();
                ExportSettings exportSettings = evaluator.getExportSettings();
                if (exportSettings != null) {
                    this.sendExportFile(evaluator, preEvaluationResult, exportSettings);
                } else {
                    this.sendEvaluationResult(preEvaluationResult);
                }
                this.addonGlobal.getEvaluationThreadPoolExecutor().remove(this.jobId);
            }
            catch (Exception e) {
                logger.error("Evaluation failed!", (Throwable)e);
                ActionUtil.sendBadResponse();
            }
        }
        return "none";
    }

    private void sendEvaluationResult(PreEvaluationResult preEvaluationResult) throws IOException {
        String result = OBJECT_MAPPER.writeValueAsString((Object)preEvaluationResult);
        ActionUtil.sendJsonResponse(result);
    }

    private void sendExportFile(Evaluator evaluator, PreEvaluationResult preEvaluationResult, ExportSettings exportSettings) throws Exception {
        String contentType = "text/csv";
        String prefix = this.evaluationLevel == EvaluationLevel.EVALUATE_GLOBAL ? "global" : (preEvaluationResult.getPageId() > 0L ? "page" : "space");
        Object filename = prefix + "-permission-overview." + exportSettings.getExportFormat();
        String hash = this.jobId;
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpUtil.setNoCacheHeaders(response);
        response.setContentType(contentType);
        filename = HttpUtil.encodeURIComponent((String)filename);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + (String)filename + ";");
        response.setHeader("Set-Cookie", "upm-export-" + hash + "=success; path=/");
        response.flushBuffer();
        Exporter csvExporter = this.exporterFactory.createExporter(exportSettings);
        csvExporter.export(evaluator, preEvaluationResult, (OutputStream)response.getOutputStream());
    }

    public String fetchPermissions() throws IOException {
        int first = this.start;
        int last = this.start + this.count - 1;
        Evaluator evaluator = (Evaluator)this.addonGlobal.getEvaluatorJobs().get((Object)this.jobId);
        if (evaluator == null) {
            logger.debug("JobId {} not found.");
            ActionUtil.sendBadResponse("Evaluation is unavailable!");
            return "none";
        }
        int max = evaluator.getVisibleOwnerCount() - 1;
        if (last > max) {
            last = max;
        }
        EvaluationResult result = evaluator.evaluateOwners(first, last);
        String s = OBJECT_MAPPER.writeValueAsString((Object)result);
        ActionUtil.sendJsonResponse(s);
        return "none";
    }

    public String showEffectivePermissions() {
        this.checkLicense();
        return "success";
    }

    public String explain() throws IOException {
        ExplainedPermissionSet explainedPermissions;
        if (!this.checkLicense()) {
            ActionUtil.sendBadResponse("The license is invalid");
            return "none";
        }
        logger.debug("Explain permissions for group: {}, user: {}, anonymous: {}", new Object[]{this.groupname, this.username, this.anonymous});
        OwnerType ownerType = this.getOwnerType();
        if (ownerType == null) {
            ActionUtil.sendBadResponse("You must provide an owner type");
            return "none";
        }
        if (ownerType == OwnerType.TYPE_USER && StringUtils.isEmpty((CharSequence)this.username)) {
            ActionUtil.sendBadResponse("You must provide a username");
            return "none";
        }
        if (ownerType == OwnerType.TYPE_GROUP && StringUtils.isEmpty((CharSequence)this.groupname)) {
            ActionUtil.sendBadResponse("You must provide a group name");
            return "none";
        }
        if (StringUtils.isEmpty((CharSequence)this.spaceKey)) {
            ActionUtil.sendBadResponse("You must provide a space key");
            return "none";
        }
        TinyPage tinyPage = null;
        if (this.pageId != 0L && (tinyPage = this.confluenceService.getPage(this.spaceKey, this.pageId)) == null) {
            ActionUtil.sendBadResponse("The page with ID " + this.pageId + " is not found");
            return "none";
        }
        TinySpace tinySpace = this.confluenceService.getSpace(this.spaceKey);
        if (tinySpace == null) {
            ActionUtil.sendBadResponse("The space with key " + this.spaceKey + " is not found");
            return "none";
        }
        TinySpaceDto spaceDto = new TinySpaceDto(tinySpace.getKey(), tinySpace.getName());
        spaceDto.setLogoUrl(this.spaceLogoManager.getLogoUriReference(this.getSpace(), (User)AuthenticatedUserThreadLocal.get()));
        String contextPath = this.getBootstrapManager().getWebAppContextPath();
        String owner = null;
        TinyOwnerDto.Builder ownerDtoBuilder = new TinyOwnerDto.Builder();
        switch (ownerType) {
            case TYPE_GROUP: {
                owner = this.groupname;
                ownerDtoBuilder.name(this.groupname).displayName(this.groupname).avatarUrl("");
                break;
            }
            case TYPE_USER: {
                owner = this.username;
                ConfluenceUser confluenceUser = this.userAccessor.getUserByName(this.username);
                ownerDtoBuilder.name(this.username).displayName(confluenceUser.getFullName()).anonymous(false).avatarUrl(this.userAccessor.getUserProfilePicture((User)confluenceUser).getUriReference());
                break;
            }
            case TYPE_ANONYMOUS: {
                owner = null;
                ownerDtoBuilder.name("").displayName(this.getI18n().getText("anonymous.name")).anonymous(false).avatarUrl(contextPath + "/images/icons/profilepics/default.png");
            }
        }
        ExplainingEvaluation evaluation = new ExplainingEvaluation(ownerType, owner, tinySpace, tinyPage, this.getI18n(), contextPath);
        ExplainingEvaluator evaluator = evaluation.createEvaluator(this.confluenceService, this.evaluatorCacheHolder, this.evaluationExpiryChecker);
        try {
            explainedPermissions = (ExplainedPermissionSet)evaluator.explainPermissions();
        }
        catch (EvaluatorCacheNotInitializedException | InterruptedException | TimeoutException e) {
            logger.warn("{}", (Object)e.getMessage());
            ActionUtil.sendNotFoundResponse();
            return "none";
        }
        HashMap<com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission, RefinedExplanation> explanations = explainedPermissions != null ? explainedPermissions.getRefinedExplanationMap() : new HashMap<com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission, RefinedExplanation>();
        int permissions = explainedPermissions != null ? explainedPermissions.toTransferFormat() : -1;
        ActionUtil.sendJsonResponse(OBJECT_MAPPER.writeValueAsString((Object)new PermissionExplanations(permissions, ownerDtoBuilder.build(), spaceDto, tinyPage, explanations)));
        return "none";
    }

    public String showPermissionExplanations() {
        if (!this.checkLicense()) {
            return "success";
        }
        logger.debug("showPermissionExplanations called group: {}, user: {}, anonymous: {}", new Object[]{this.groupname, this.username, this.anonymous});
        OwnerType ownerType = this.getOwnerType();
        if (ownerType == null) {
            return "error";
        }
        if (ownerType == OwnerType.TYPE_USER && StringUtils.isEmpty((CharSequence)this.username)) {
            return "success";
        }
        if (ownerType == OwnerType.TYPE_GROUP && StringUtils.isEmpty((CharSequence)this.groupname)) {
            return "success";
        }
        TinyPage tinyPage = null;
        if (this.pageId != 0L && (tinyPage = this.confluenceService.getPage(this.spaceKey, this.pageId)) == null) {
            return "success";
        }
        TinySpace tinySpace = this.confluenceService.getSpace(this.spaceKey);
        ExplainingEvaluation evaluation = null;
        I18NBean i18 = this.getI18n();
        String contextPath = this.getBootstrapManager().getWebAppContextPath();
        switch (ownerType) {
            case TYPE_GROUP: {
                evaluation = new ExplainingEvaluation(ownerType, this.groupname, tinySpace, tinyPage, i18, contextPath);
                break;
            }
            case TYPE_USER: {
                evaluation = new ExplainingEvaluation(ownerType, this.username, tinySpace, tinyPage, i18, contextPath);
                break;
            }
            case TYPE_ANONYMOUS: {
                evaluation = new ExplainingEvaluation(ownerType, tinySpace, tinyPage, i18, contextPath);
            }
        }
        ExplainingEvaluator evaluator = evaluation.createEvaluator(this.confluenceService, this.evaluatorCacheHolder, this.evaluationExpiryChecker);
        try {
            this.explainedPermissions = evaluator.explainPermissions();
        }
        catch (EvaluatorCacheNotInitializedException | InterruptedException | TimeoutException e) {
            logger.warn("{}", (Object)e.getMessage());
            return "error";
        }
        return this.explainedPermissions != null ? "success" : "error";
    }

    private OwnerType getOwnerType() {
        if (this.evaluationLevel == EvaluationLevel.EVALUATE_GLOBAL && "group".equals(this.type)) {
            return OwnerType.TYPE_GROUP;
        }
        if (this.evaluationLevel == EvaluationLevel.EVALUATE_SPACE || "user".equals(this.type)) {
            return "true".equals(this.anonymous) ? OwnerType.TYPE_ANONYMOUS : OwnerType.TYPE_USER;
        }
        return null;
    }

    private boolean checkValidVelocitySyntax(String template) {
        try {
            VelocityEngine velocityEngine = VelocityUtils.getVelocityEngine();
            VelocityContext context = new VelocityContext();
            return velocityEngine.evaluate((Context)context, (Writer)new StringWriter(128), "", template);
        }
        catch (Exception e) {
            return false;
        }
    }

    public void setPluginRetrievalService(PluginRetrievalService pluginRetrievalService) {
        this.pluginKey = pluginRetrievalService.getPlugin().getKey();
    }

    public void setAddonGlobal(AddonGlobal addonGlobal) {
        this.addonGlobal = addonGlobal;
    }

    public void setLicenseManager(AddonLicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public void setAccessModeService(AccessModeService accessModeService) {
        this.accessModeService = accessModeService;
    }

    public String getUserFilter() {
        return this.userFilter;
    }

    public void setUserFilter(String userFilter) {
        this.userFilter = userFilter;
    }

    public String getSpaceFilter() {
        return this.spaceFilter;
    }

    public void setSpaceFilter(String spaceFilter) {
        this.spaceFilter = spaceFilter;
    }

    public String getGroupFilter() {
        return this.groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }

    public int getMaxRows() {
        return this.maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public boolean isAlwaysShowAnonymous() {
        return this.alwaysShowAnonymous;
    }

    public void setAlwaysShowAnonymous(boolean alwaysShowAnonymous) {
        this.alwaysShowAnonymous = alwaysShowAnonymous;
    }

    public boolean isExcludeDisabled() {
        return this.excludeDisabled;
    }

    public void setExcludeDisabled(boolean excludeDisabled) {
        this.excludeDisabled = excludeDisabled;
    }

    public boolean isExcludeOwnersNoPermissions() {
        return this.excludeOwnersNoPermissions;
    }

    public void setExcludeOwnersNoPermissions(boolean excludeOwnersNoPermissions) {
        this.excludeOwnersNoPermissions = excludeOwnersNoPermissions;
    }

    public boolean isExcludeSpacesNoPermissions() {
        return this.excludeSpacesNoPermissions;
    }

    public void setExcludeSpacesNoPermissions(boolean excludeSpacesNoPermissions) {
        this.excludeSpacesNoPermissions = excludeSpacesNoPermissions;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }

    public void setExportCsvDelimiter(String exportCsvDelimiter) {
        this.exportCsvDelimiter = exportCsvDelimiter;
    }

    public void setExportCsvCustomDelimiter(String exportCsvCustomDelimiter) {
        this.exportCsvCustomDelimiter = exportCsvCustomDelimiter;
    }

    public void setExportSpaceDetailsFormat(String exportSpaceDetailsFormat) {
        this.exportSpaceDetailsFormat = exportSpaceDetailsFormat;
    }

    public void setExportCustomSpaceDetailsFormat(String exportCustomSpaceDetailsFormat) {
        this.exportCustomSpaceDetailsFormat = exportCustomSpaceDetailsFormat;
    }

    public void setHideFixColumnHeaders(boolean hideFixColumnHeaders) {
        this.hideFixColumnHeaders = hideFixColumnHeaders;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Overview getOverview() {
        return this.overview;
    }

    public List<TinySpace> getSpaceList() {
        if (this.spaceList == null) {
            EvaluatorCache evaluatorCache = this.evaluatorCacheHolder.getEvaluatorCache();
            this.spaceList = evaluatorCache.getSpaces();
        }
        return this.spaceList;
    }

    public PermissionSet getExplainedPermissions() {
        return this.explainedPermissions;
    }

    public boolean isLicensed() {
        return this.licensed;
    }

    public boolean isEditPermissionAllowed() {
        return !this.accessModeService.isReadOnlyAccessModeEnabled() || this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public void setDarkFeatureManager(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    public DarkFeatureManager getDarkFeatureManager() {
        return this.darkFeatureManager;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void setConfluenceService(ConfluenceService confluenceService) {
        this.confluenceService = confluenceService;
    }

    public void setSpaceLogoManager(SpaceLogoManager spaceLogoManager) {
        this.spaceLogoManager = spaceLogoManager;
    }

    public int getAllPermissionsFlag() {
        return Permissions.ALL_FULL_PERMISSIONS_FLAG;
    }

    public String getHelpLink() {
        return this.confluenceService.getHelpLink("gatekeeper.inspect-permissions");
    }

    public String getAllPermissionsMetadata() {
        return allPermissionsMetadata;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setEvaluatorCacheHolder(EvaluatorCacheHolder evaluatorCacheHolder) {
        this.evaluatorCacheHolder = evaluatorCacheHolder;
    }

    public void setExporterFactory(ExporterFactory exporterFactory) {
        this.exporterFactory = exporterFactory;
    }

    public void setEvaluationExpiryChecker(EvaluationExpiryChecker evaluationExpiryChecker) {
        this.evaluationExpiryChecker = evaluationExpiryChecker;
    }
}

