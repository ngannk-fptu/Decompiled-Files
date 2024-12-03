/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathDeletedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.path.CustomExportPathUpdateSucceededAnalyticEvent;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineConfigDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineConfig;
import com.atlassian.business.insights.core.audit.AuditEventFactory;
import com.atlassian.business.insights.core.service.ExportPathHolder;
import com.atlassian.business.insights.core.service.api.ConfigService;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.sal.api.ApplicationProperties;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigService
implements ConfigService {
    @VisibleForTesting
    static final String EXPORT_PATH_KEY = "export.path";
    @VisibleForTesting
    static final String DATASET_DIR_NAME = "data-pipeline";
    @VisibleForTesting
    static final String EXPORT_DIR_NAME = "export";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigService.class);
    private final AoDataPipelineConfigDao aoDataPipelineConfigDao;
    private final ApplicationProperties applicationProperties;
    private final EventPublisherService eventPublisherService;
    private final AuditService auditService;

    public DefaultConfigService(@Nonnull AoDataPipelineConfigDao aoDataPipelineConfigDao, @Nonnull ApplicationProperties applicationProperties, @Nonnull EventPublisherService eventPublisherService, @Nonnull AuditService auditService) {
        this.aoDataPipelineConfigDao = Objects.requireNonNull(aoDataPipelineConfigDao, "aoDataPipelineConfigDao must not be null");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties must not be null");
        this.eventPublisherService = Objects.requireNonNull(eventPublisherService, "eventPublisherService must not be null");
        this.auditService = Objects.requireNonNull(auditService, "auditService must not be null");
    }

    @Override
    @Nonnull
    public ExportPathHolder getRootExportPathHolder() {
        Optional<Path> customPath = this.getCustomExportPath();
        Path rootExportPath = customPath.orElseGet(() -> this.applicationProperties.getSharedHomeDirectory().orElseGet(() -> (Path)this.applicationProperties.getLocalHomeDirectory().orElseThrow(() -> new InternalError("Could not resolve export root location")))).resolve(DATASET_DIR_NAME).resolve(EXPORT_DIR_NAME);
        return new ExportPathHolder(rootExportPath, customPath.isPresent());
    }

    @Nonnull
    private Optional<Path> getCustomExportPath() {
        Optional<String> path = this.aoDataPipelineConfigDao.get(EXPORT_PATH_KEY).map(AoDataPipelineConfig::getValue);
        return path.map(x$0 -> Paths.get(x$0, new String[0]));
    }

    @Override
    public void setCustomExportPath(@Nullable String rootDirPath) throws NotDirectoryException {
        String previousPath = this.getRootExportPathHolder().getAbsolutePathString();
        if (StringUtils.isBlank((CharSequence)rootDirPath)) {
            this.aoDataPipelineConfigDao.delete(EXPORT_PATH_KEY);
            this.eventPublisherService.publish(new CustomExportPathDeletedAnalyticEvent(this.eventPublisherService.getPluginVersion()));
            this.auditService.audit(AuditEventFactory.createCustomExportPathDeletedAuditEvent(previousPath, this.getRootExportPathHolder().getAbsolutePathString()));
        } else {
            String sanitisedPath = this.getSanitisedPath(rootDirPath);
            if (!this.isValidDirectoryPath(sanitisedPath)) {
                throw new NotDirectoryException("Invalid directory path");
            }
            this.aoDataPipelineConfigDao.put(EXPORT_PATH_KEY, sanitisedPath);
            this.eventPublisherService.publish(new CustomExportPathUpdateSucceededAnalyticEvent(this.eventPublisherService.getPluginVersion()));
            this.auditService.audit(AuditEventFactory.createCustomExportPathSetAuditEvent(this.getRootExportPathHolder().getAbsolutePathString(), previousPath));
        }
    }

    private String getSanitisedPath(String rootDirPath) {
        String withoutNewLineChars = rootDirPath.replaceAll("\\r|\\n", "").trim();
        return this.trimDoubleQuotes(withoutNewLineChars);
    }

    private String trimDoubleQuotes(String text) {
        int textLength = text.length();
        if (textLength >= 2 && text.charAt(0) == '\"' && text.charAt(textLength - 1) == '\"') {
            return text.substring(1, textLength - 1);
        }
        return text;
    }

    private boolean isValidDirectoryPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean isDirectoryWritable = Files.isWritable(file.toPath());
            if (!isDirectoryWritable) {
                LOGGER.error("Unable to set custom export path. Directory exists but not writable: " + path);
            }
            return isDirectoryWritable;
        }
        return this.canCreateDirectoryAtDestination(file);
    }

    private boolean canCreateDirectoryAtDestination(File file) {
        try {
            file.createNewFile();
            Files.delete(file.toPath());
            return true;
        }
        catch (Exception e) {
            LOGGER.error("Unable to set custom export path. Directory is not writable: " + file.getPath(), (Throwable)e);
            return false;
        }
    }
}

