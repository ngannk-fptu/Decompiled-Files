/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.migration.agent.entity.CheckOverrideEntity;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentMapper;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionMapper;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictMapper;
import com.atlassian.migration.agent.store.impl.CheckOverrideStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class CheckOverrideService {
    private static final Logger log = ContextLoggerFactory.getLogger(CheckOverrideService.class);
    private static final List<String> overridableTypes = Arrays.asList(CheckType.SPACE_ANONYMOUS_PERMISSIONS.value(), CheckType.MISSING_ATTACHMENTS.value(), CheckType.GLOBAL_DATA_TEMPLATE.value());
    private final CheckOverrideStore checkOverrideStore;
    private final PluginTransactionTemplate ptx;

    public CheckOverrideService(CheckOverrideStore checkOverrideStore, PluginTransactionTemplate ptx) {
        this.checkOverrideStore = checkOverrideStore;
        this.ptx = ptx;
    }

    public List<CheckResultDto> applyAndOverride(String executionId, List<CheckResultDto> results) {
        boolean matchAndSuccessful = results.stream().allMatch(checkResultDto -> Status.SUCCESS.equals((Object)checkResultDto.getStatus()));
        if (matchAndSuccessful) {
            return results;
        }
        List<String> overriddenCheckTypes = this.getOverridesByExecutionIdAndTypes(executionId);
        return results.stream().map(result -> {
            if (overriddenCheckTypes.contains(result.getCheckType())) {
                if (result.getCheckType().equals(CheckType.SPACE_ANONYMOUS_PERMISSIONS.value())) {
                    SpaceAnonymousPermissionMapper.changeStatusToWarning(result);
                } else if (result.getCheckType().equals(CheckType.MISSING_ATTACHMENTS.value())) {
                    MissingAttachmentMapper.changeStatusToWarning(result);
                } else if (result.getCheckType().equals(CheckType.GLOBAL_DATA_TEMPLATE.value())) {
                    GlobalDataTemplateConflictMapper.changeStatusToWarning(result);
                }
            }
            return result;
        }).collect(Collectors.toList());
    }

    public void createOverrides(String executionId, @Nullable List<String> types) {
        if (types != null) {
            this.ptx.write(() -> {
                this.checkOverrideStore.deleteByExecutionIdAndTypes(executionId, CheckType.checkTypeValuesNotFrom(types));
                if (CollectionUtils.isNotEmpty((Collection)types)) {
                    this.checkOverrideStore.createCheckOverrides(executionId, types);
                }
            });
        }
    }

    public void bindCheckIdToPlanId(String checkExecutionId, String planId) {
        this.ptx.write(() -> {
            log.info("Updating executionId of checkOverrides from {} to {}", (Object)checkExecutionId, (Object)planId);
            this.checkOverrideStore.updateExecutionId(checkExecutionId, planId);
        });
    }

    public boolean isOverriddenByExecutionIdAndCheckType(String executionId, String checkType) {
        Optional<CheckOverrideEntity> mayBeEntity = this.checkOverrideStore.findByExecutionIdAndType(executionId, checkType);
        return mayBeEntity.isPresent();
    }

    public List<String> getOverridesByExecutionId(String executionId) {
        return this.ptx.read(() -> this.checkOverrideStore.getByExecutionId(executionId).stream().map(CheckOverrideEntity::getCheckType).collect(Collectors.toList()));
    }

    public List<String> getOverridesByExecutionIdAndTypes(String executionId) {
        return this.ptx.read(() -> this.checkOverrideStore.findByExecutionIdAndTypes(executionId, new ArrayList<String>(overridableTypes)).stream().map(CheckOverrideEntity::getCheckType).collect(Collectors.toList()));
    }
}

