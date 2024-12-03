/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.retention.service;

import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.retention.RetentionPolicyPermissionManager;
import com.atlassian.confluence.retention.SpaceRetentionPolicyService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultSpaceRetentionPolicyService
implements SpaceRetentionPolicyService {
    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;
    private final SpaceManagerInternal spaceManagerInternal;
    private final RetentionPolicyPermissionManager retentionPolicyPermissionManager;
    private final RetentionFeatureChecker featureChecker;

    public DefaultSpaceRetentionPolicyService(SpaceRetentionPolicyManager spaceRetentionPolicyManager, SpaceManagerInternal spaceManagerInternal, RetentionPolicyPermissionManager retentionPolicyPermissionManager, RetentionFeatureChecker featureChecker) {
        this.spaceRetentionPolicyManager = Objects.requireNonNull(spaceRetentionPolicyManager);
        this.spaceManagerInternal = Objects.requireNonNull(spaceManagerInternal);
        this.retentionPolicyPermissionManager = Objects.requireNonNull(retentionPolicyPermissionManager);
        this.featureChecker = Objects.requireNonNull(featureChecker);
    }

    @Override
    public void deletePolicy(String spaceKey) throws NotFoundException, PermissionException {
        if (!this.featureChecker.isFeatureAvailable()) {
            return;
        }
        Space space = this.failIfSpaceNotFound(spaceKey);
        this.failIfNotAllowedToEditSpacePolicy(space);
        this.spaceRetentionPolicyManager.deletePolicy(spaceKey);
    }

    @Override
    public void savePolicy(String spaceKey, SpaceRetentionPolicy newPolicy) throws NotFoundException, PermissionException {
        if (!this.featureChecker.isFeatureAvailable()) {
            return;
        }
        Space space = this.failIfSpaceNotFound(spaceKey);
        this.failIfNotAllowedToEditSpacePolicy(space);
        if (!this.retentionPolicyPermissionManager.canEditGlobalPolicy(AuthenticatedUserThreadLocal.get())) {
            this.failIfNotAllowedToEditSpaceAdminCanEdit(this.spaceRetentionPolicyManager.getPolicy(spaceKey).orElse(null), newPolicy);
        }
        this.spaceRetentionPolicyManager.savePolicy(spaceKey, newPolicy);
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<SpaceRetentionPolicy> getPolicy(String spaceKey) {
        return this.getPolicy(() -> this.spaceManagerInternal.getSpace(spaceKey));
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<SpaceRetentionPolicy> getPolicy(long spaceId) {
        return this.getPolicy(() -> this.spaceManagerInternal.getSpace(spaceId));
    }

    private Optional<SpaceRetentionPolicy> getPolicy(Supplier<Space> spaceSupplier) {
        if (!this.featureChecker.isFeatureAvailable()) {
            return Optional.empty();
        }
        Space space = spaceSupplier.get();
        if (space == null) {
            return Optional.empty();
        }
        if (!this.retentionPolicyPermissionManager.canViewSpacePolicy(AuthenticatedUserThreadLocal.get(), space)) {
            return Optional.empty();
        }
        return this.spaceRetentionPolicyManager.getPolicy(space.getKey());
    }

    private Space failIfSpaceNotFound(String spaceKey) {
        Space space = this.spaceManagerInternal.getSpace(spaceKey);
        if (space == null) {
            throw new NotFoundException("Space not found: " + spaceKey);
        }
        return space;
    }

    private void failIfNotAllowedToEditSpacePolicy(Space space) {
        if (!this.retentionPolicyPermissionManager.canEditSpacePolicy(AuthenticatedUserThreadLocal.get(), space)) {
            throw new PermissionException(UNAUTHORIZED_MESSAGE);
        }
    }

    private void failIfNotAllowedToEditSpaceAdminCanEdit(@Nullable SpaceRetentionPolicy currentPolicy, SpaceRetentionPolicy newPolicy) {
        if (currentPolicy == null && newPolicy.getSpaceAdminCanEdit()) {
            return;
        }
        if (currentPolicy != null && currentPolicy.getSpaceAdminCanEdit() == newPolicy.getSpaceAdminCanEdit()) {
            return;
        }
        throw new PermissionException("spaceAdminCanEdit cannot be set by a space admin");
    }
}

