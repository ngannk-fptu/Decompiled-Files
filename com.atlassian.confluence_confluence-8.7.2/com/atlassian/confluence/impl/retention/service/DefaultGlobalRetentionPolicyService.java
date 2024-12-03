/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.retention.service;

import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.retention.GlobalRetentionPolicyService;
import com.atlassian.confluence.retention.RetentionPolicyPermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultGlobalRetentionPolicyService
implements GlobalRetentionPolicyService {
    private final GlobalRetentionPolicyManager globalRetentionPolicyManager;
    private final RetentionPolicyPermissionManager retentionPolicyPermissionManager;
    private final RetentionFeatureChecker featureChecker;

    public DefaultGlobalRetentionPolicyService(GlobalRetentionPolicyManager globalRetentionPolicyManager, RetentionPolicyPermissionManager retentionPolicyPermissionManager, RetentionFeatureChecker featureChecker) {
        this.globalRetentionPolicyManager = Objects.requireNonNull(globalRetentionPolicyManager);
        this.retentionPolicyPermissionManager = Objects.requireNonNull(retentionPolicyPermissionManager);
        this.featureChecker = Objects.requireNonNull(featureChecker);
    }

    @Override
    @Transactional(readOnly=true)
    public GlobalRetentionPolicy getPolicy() {
        if (!this.featureChecker.isFeatureAvailable()) {
            throw new NotFoundException("Not implemented yet");
        }
        if (!this.retentionPolicyPermissionManager.canViewGlobalPolicy(AuthenticatedUserThreadLocal.get())) {
            throw new PermissionException("Not permitted");
        }
        return this.globalRetentionPolicyManager.getPolicy();
    }

    @Override
    public void savePolicy(GlobalRetentionPolicy newPolicy) {
        if (!this.featureChecker.isFeatureAvailable()) {
            return;
        }
        if (!this.retentionPolicyPermissionManager.canEditGlobalPolicy(AuthenticatedUserThreadLocal.get())) {
            throw new PermissionException("Not permitted");
        }
        this.globalRetentionPolicyManager.savePolicy(newPolicy);
    }
}

