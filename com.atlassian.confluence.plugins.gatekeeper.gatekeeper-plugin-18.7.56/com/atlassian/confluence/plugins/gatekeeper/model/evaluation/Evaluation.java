/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationLevel;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.ActiveFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.Filter;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.GroupFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.UserFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public abstract class Evaluation {
    private EvaluationLevel evaluationLevel;
    private OwnerType evaluationOwnerType;
    private int maxRows;
    private boolean alwaysShowAnonymous;
    private boolean excludeDisabled;
    private boolean excludeOwnersNoPermissions;
    private boolean excludeSpacesNoPermissions;
    @Nullable
    private List<Permission> evaluatedPermissions;
    private UserFilter userFilter;
    private GroupFilter groupFilter;
    private EvaluationOutput evaluationOutput;
    private ExportSettings exportSettings;
    private boolean gatekeeperV2Enabled;

    protected Evaluation(EvaluationLevel evaluationLevel, OwnerType evaluationOwnerType, EvaluationOutput evaluationOutput) {
        this.evaluationLevel = evaluationLevel;
        this.evaluationOwnerType = evaluationOwnerType;
        this.evaluationOutput = evaluationOutput;
    }

    public abstract Evaluator createEvaluator(TransactionTemplate var1, ConfluenceService var2, EvaluatorCacheHolder var3, EvaluationExpiryChecker var4);

    public OwnerType getEvaluationOwnerType() {
        return this.evaluationOwnerType;
    }

    public boolean isEvaluatingSingleSpace() {
        return this.evaluationLevel == EvaluationLevel.EVALUATE_SPACE;
    }

    public boolean isEvaluatingAnonymous() {
        return this.evaluationOwnerType == OwnerType.TYPE_ANONYMOUS;
    }

    public boolean isEvaluatingGroups() {
        return this.evaluationOwnerType == OwnerType.TYPE_GROUP;
    }

    public boolean isEvaluatingUsers() {
        return this.evaluationOwnerType == OwnerType.TYPE_USER;
    }

    public boolean isExport() {
        return this.exportSettings != null;
    }

    public UserFilter getUserFilter() {
        return this.userFilter;
    }

    public void setUserFilter(UserFilter userFilter) {
        this.userFilter = userFilter;
    }

    public List<Filter<TinyOwner>> getUserFilters() {
        boolean hasUserFilter;
        boolean bl = hasUserFilter = this.userFilter != null && !this.userFilter.isEmptyFilter();
        if (this.excludeDisabled || hasUserFilter) {
            ArrayList<Filter<TinyOwner>> filters = new ArrayList<Filter<TinyOwner>>(0);
            if (this.excludeDisabled) {
                filters.add(new ActiveFilter());
            }
            if (hasUserFilter) {
                filters.add(this.userFilter);
            }
            return filters;
        }
        return null;
    }

    public GroupFilter getGroupFilter() {
        return this.groupFilter;
    }

    public void setGroupFilter(GroupFilter groupFilter) {
        this.groupFilter = groupFilter;
    }

    public EvaluationOutput getEvaluationOutput() {
        return this.evaluationOutput;
    }

    public void setEvaluationOutput(EvaluationOutput evaluationOutput) {
        this.evaluationOutput = evaluationOutput;
    }

    public ExportSettings getExportSettings() {
        return this.exportSettings;
    }

    public void setExportSettings(ExportSettings exportSettings) {
        this.exportSettings = exportSettings;
    }

    void setEvaluatedPermissions(Permission ... evaluatedPermissions) {
        this.setEvaluatedPermissions(Arrays.asList(evaluatedPermissions));
    }

    private void setEvaluatedPermissions(List<Permission> allowedPermissions) {
        this.evaluatedPermissions = Collections.unmodifiableList(allowedPermissions);
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

    public boolean isGatekeeperV2Enabled() {
        return this.gatekeeperV2Enabled;
    }

    public void setGatekeeperV2Enabled(boolean gatekeeperV2Enabled) {
        this.gatekeeperV2Enabled = gatekeeperV2Enabled;
    }

    public boolean hasAnyRelevantPermissions(PermissionSet permissionSet) {
        if (permissionSet.isEmpty()) {
            return false;
        }
        if (this.evaluatedPermissions == null) {
            return true;
        }
        return permissionSet.hasAnyOf(this.evaluatedPermissions);
    }
}

