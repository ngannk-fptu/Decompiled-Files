/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.ExecutionErrorCodes
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.collections.MapUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.ExecutionErrorCodes;
import com.atlassian.migration.agent.entity.CheckExecutionStatus;
import com.atlassian.migration.agent.entity.CheckResultEntity;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.exception.FailedToRetrieveViolationsException;
import com.atlassian.migration.agent.store.impl.CheckResultStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class CheckResultsService {
    public static final String DETAILS_PATH_KEY = "path";
    public static final String VIOLATIONS_COUNT_KEY = "violationsCount";
    private static final Logger log = ContextLoggerFactory.getLogger(CheckResultsService.class);
    private final CheckResultStore checkResultStore;
    private final CheckResultFileManager checkResultFileManager;
    private final PluginTransactionTemplate ptx;

    public CheckResultsService(CheckResultStore checkResultStore, CheckResultFileManager checkResultFileManager, PluginTransactionTemplate ptx) {
        this.checkResultStore = checkResultStore;
        this.checkResultFileManager = checkResultFileManager;
        this.ptx = ptx;
    }

    public Optional<CheckResultEntity> getByExecutionIdAndCheckType(String executionId, CheckType checkType) {
        return this.ptx.read(() -> this.checkResultStore.getByExecutionIdAndCheckType(executionId, checkType));
    }

    public List<CheckResultEntity> getByExecutionId(String executionId) {
        return this.ptx.read(() -> this.checkResultStore.getByExecutionId(executionId));
    }

    public CheckResultEntity getOrCreate(String executionId, CheckType checkType) {
        return this.ptx.write(() -> this.checkResultStore.getByExecutionIdAndCheckType(executionId, checkType).orElseGet(() -> {
            CheckResultEntity entity = new CheckResultEntity(executionId, checkType.value());
            this.checkResultStore.create(entity);
            return entity;
        }));
    }

    public void updateStatusToRunning(CheckResultEntity entity) {
        this.ptx.write(() -> {
            entity.setStatus(CheckExecutionStatus.RUNNING);
            this.checkResultStore.update(entity);
        });
    }

    public void saveCheckResult(String executionId, CheckType checkType, CheckResult checkResult) {
        this.ptx.write(() -> {
            Optional<CheckResultEntity> result = this.checkResultStore.getByExecutionIdAndCheckType(executionId, checkType);
            CheckResultEntity entity = result.orElseThrow(() -> new IllegalArgumentException(String.format("Failed to find checkResult for executionId: %s and checkType: %s", executionId, checkType.value())));
            entity.setStatus(checkResult.success ? CheckExecutionStatus.SUCCESS : CheckExecutionStatus.ERROR);
            String filename = this.createFilename(executionId, checkType);
            entity.setDetails(filename);
            try {
                log.info("save to file, filename: {}, checkResult: {}", (Object)filename, (Object)checkResult);
                this.checkResultFileManager.saveToFile(filename, checkResult);
            }
            catch (RuntimeException ex) {
                log.error(String.format("Failed to save check result details to file for executionId = %s and checkType = %s", executionId, checkResult), (Throwable)ex);
                entity.setStatus(CheckExecutionStatus.ERROR);
            }
            entity.setLastExecutionTime(Instant.now());
            this.checkResultStore.update(entity);
        });
    }

    public Optional<CheckResult> getCheckResult(CheckResultEntity entity) {
        if (StringUtils.isNotEmpty((CharSequence)entity.getDetails())) {
            try {
                String details = entity.getDetails();
                log.debug("getCheckResult details: {}", (Object)details);
                return Optional.of(this.checkResultFileManager.readFromFile(details));
            }
            catch (RuntimeException e) {
                log.error("Couldn't retrieve check result", (Throwable)e);
                return Optional.of(Checker.buildCheckResultWithExecutionError((int)ExecutionErrorCodes.GENERIC.getErrorCode()));
            }
        }
        return Optional.empty();
    }

    private String createFilename(String executionId, CheckType checkType) {
        log.info("create fileName using executionId: {}, checkType: {}", (Object)executionId, (Object)checkType);
        return executionId + "-" + checkType.value() + ".ser";
    }

    public int deleteCheckResultsByExecutionId(String executionId) {
        log.info("Deleting check results by executionId: ", (Object)executionId);
        return this.ptx.write(() -> this.checkResultStore.deleteCheckResultsByExecutionId(executionId));
    }

    public void bindCheckIdToPlanId(String checkExecutionId, String planId) {
        log.info("Binding checkExecutionId: {} to planId: {}", (Object)checkExecutionId, (Object)planId);
        this.ptx.write(() -> this.checkResultStore.updateExecutionId(checkExecutionId, planId));
    }

    public <T> List<T> retrieveStoredViolations(CheckResult checkResult, TypeReference<List<T>> typeReference) {
        Map details = checkResult.details;
        try {
            if (MapUtils.isEmpty((Map)details) || Integer.parseInt(((Object)details.getOrDefault(VIOLATIONS_COUNT_KEY, 0)).toString()) == 0) {
                return Collections.emptyList();
            }
            return this.checkResultFileManager.readFromJsonFile((String)details.get(DETAILS_PATH_KEY), typeReference);
        }
        catch (Exception e) {
            log.error(String.format("Error retrieving the list of violations from details: %s, Type: %s", details, typeReference.getType()), (Throwable)e);
            throw new FailedToRetrieveViolationsException(e.getMessage(), e);
        }
    }

    public void cleanStaleChecks() {
        this.ptx.write(() -> this.checkResultStore.batchUpdateStatus(CheckExecutionStatus.RUNNING, Instant.now().minus(Duration.ofHours(24L)), CheckExecutionStatus.ERROR));
    }

    public boolean hasRunningPreflights() {
        List<CheckResultEntity> runningPreflights = this.checkResultStore.getRunningChecks();
        return !runningPreflights.isEmpty();
    }
}

