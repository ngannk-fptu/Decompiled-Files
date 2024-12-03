/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.gatekeeper.dto.TinyOwnerDto;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.exception.EvaluatorCacheNotInitializedException;
import com.atlassian.confluence.plugins.gatekeeper.export.Exporter;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.Evaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.EvaluationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.PreEvaluationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingPhase;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingState;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplainedPermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.space.SpacePermissions;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.BitSetList;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

public abstract class Evaluator
implements Callable<PreEvaluationResult> {
    private static final int SPACE_LIMIT = Integer.getInteger("gatekeeper.space-limit", 100);
    protected final EvaluatorCacheHolder evaluatorCacheHolder;
    protected Logger logger;
    protected final TransactionTemplate transactionTemplate;
    protected final ConfluenceService confluenceService;
    protected ProcessingState processingState = new ProcessingState();
    protected ExportSettings exportSettings;
    protected Evaluation evaluation;
    protected EvaluatorCache evaluatorCache;
    protected OwnerType ownerType;
    protected BitSetList<TinyOwner> owners;
    protected BitSetList<TinySpace> spaces;
    protected TinyPage page;
    private final EvaluationExpiryChecker evaluationExpiryChecker;

    protected Evaluator(Evaluation evaluation, TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        this.evaluation = evaluation;
        this.ownerType = evaluation.getEvaluationOwnerType();
        this.exportSettings = evaluation.getExportSettings();
        this.confluenceService = confluenceService;
        this.transactionTemplate = transactionTemplate;
        this.evaluatorCacheHolder = evaluatorCacheHolder;
        this.evaluationExpiryChecker = evaluationExpiryChecker;
    }

    public ProcessingState getProcessingState() {
        return this.processingState;
    }

    public ExportSettings getExportSettings() {
        return this.exportSettings;
    }

    protected void prepareEvaluation() throws EvaluatorCacheNotInitializedException, InterruptedException, TimeoutException {
        this.processingState.setPhase(ProcessingPhase.INIT);
        this.processingState.setExpectedEndTime(System.currentTimeMillis() + this.evaluationExpiryChecker.getRequestTimeoutMillis());
        this.initEvaluator();
        this.logger.debug("Anonymous access enabled: {}", (Object)this.evaluatorCache.isGlobalAnonymousAccessEnabled());
        boolean excludeOwnersNoPermissions = this.evaluation.isExcludeOwnersNoPermissions();
        boolean excludeSpacesNoPermissions = this.evaluation.isExcludeSpacesNoPermissions();
        if (excludeOwnersNoPermissions || excludeSpacesNoPermissions) {
            StopWatch watch = new StopWatch();
            watch.start();
            this.applyExcludeNoPermissions(excludeOwnersNoPermissions, excludeSpacesNoPermissions);
            watch.stop();
            this.logger.debug("'No permission' filtering done in {} ms", (Object)watch.getTime());
        }
        if (this.evaluation.isAlwaysShowAnonymous() && this.evaluation.getEvaluationOwnerType() == OwnerType.TYPE_USER) {
            this.owners.setMatchesFilters(0);
            this.owners.setHasPermissions(0);
        }
    }

    protected abstract void initEvaluator() throws EvaluatorCacheNotInitializedException;

    @VisibleForTesting
    void applyExcludeNoPermissions(boolean excludeOwnersNoPermissions, boolean excludeSpacesNoPermissions) throws TimeoutException, InterruptedException {
        PermissionSet permissionSet = new PermissionSet();
        boolean quickAccessCheck = this.getViewRestrictions().isEmpty() && this.getEditRestrictions().isEmpty();
        boolean isAdminView = this.evaluation.getEvaluationOutput() == EvaluationOutput.ADMIN_VIEW;
        int ownerIndex = 0;
        while ((ownerIndex = this.owners.nextMatchesFilters(ownerIndex)) != -1) {
            if (isAdminView) {
                this.evaluationExpiryChecker.check(this);
            }
            TinyOwner owner = this.owners.get(ownerIndex);
            int spaceIndex = 0;
            while ((spaceIndex = this.spaces.nextMatchesFilters(spaceIndex)) != -1) {
                TinySpace space = this.spaces.get(spaceIndex);
                permissionSet.reset();
                if (excludeOwnersNoPermissions && !this.owners.hasPermissions(ownerIndex) || excludeSpacesNoPermissions && !this.spaces.hasPermissions(spaceIndex)) {
                    this.evaluateOwner(space, owner, permissionSet, quickAccessCheck);
                    if (this.evaluation.hasAnyRelevantPermissions(permissionSet)) {
                        if (excludeOwnersNoPermissions) {
                            this.owners.setHasPermissions(ownerIndex);
                        }
                        if (!excludeSpacesNoPermissions) break;
                        this.spaces.setHasPermissions(spaceIndex);
                    }
                }
                ++spaceIndex;
            }
            ++ownerIndex;
        }
    }

    @Override
    public PreEvaluationResult call() {
        try {
            return (PreEvaluationResult)this.transactionTemplate.execute(() -> {
                try {
                    this.prepareEvaluation();
                }
                catch (EvaluatorCacheNotInitializedException | InterruptedException | TimeoutException e) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(e.getMessage(), (Throwable)e);
                    } else {
                        this.logger.warn(e.getMessage());
                    }
                    return null;
                }
                int limitedAmountOfSpaces = this.getSpaceLimit();
                ArrayList<TinySpace> spaceList = new ArrayList<TinySpace>(limitedAmountOfSpaces);
                int spaceIndex = 0;
                while ((spaceIndex = this.spaces.nextVisible(spaceIndex)) != -1 && spaceList.size() < limitedAmountOfSpaces) {
                    spaceList.add(this.spaces.get(spaceIndex++));
                }
                this.processingState.setPhase(ProcessingPhase.DONE);
                return new PreEvaluationResult(spaceList, this.owners.getVisibleSize(), this.spaces.getVisibleSize(), this.page);
            });
        }
        catch (Exception e) {
            this.logger.error("Evaluation failed!", (Throwable)e);
            throw e;
        }
    }

    private int getSpaceLimit() {
        boolean exportMode = this.getExportSettings() != null;
        return exportMode || !this.evaluation.isGatekeeperV2Enabled() ? this.spaces.getVisibleSize() : this.spaces.getLimitedVisibleSize(SPACE_LIMIT);
    }

    public BitSetList<TinyOwner> getOwners() {
        return this.owners;
    }

    public int getVisibleOwnerCount() {
        return this.owners.getVisibleSize();
    }

    public BitSetList<TinySpace> getSpaces() {
        return this.spaces;
    }

    public void export(Exporter exporter) throws Exception {
        this.processingState.setPhase(ProcessingPhase.EXPORTING);
        boolean allowPartialPermissions = !this.evaluation.isGatekeeperV2Enabled();
        int ownerIndex = 0;
        int total = this.owners.getVisibleSize();
        for (int i = 0; i < total; ++i) {
            ownerIndex = this.owners.nextVisible(ownerIndex);
            TinyOwner owner = this.owners.get(ownerIndex++);
            PermissionSet[] permissionSets = this.evaluateOwner(owner);
            exporter.processRow(owner, permissionSets, allowPartialPermissions);
            this.processingState.setPercent(i * 100 / total);
        }
        this.processingState.setPercent(100L);
        this.processingState.setPhase(ProcessingPhase.DONE);
    }

    public EvaluationResult evaluateOwners(int from, int to) {
        int i;
        int size = to - from + 1;
        EvaluationResult result = new EvaluationResult(size);
        int ownerIndex = 0;
        for (i = 0; i < from; ++i) {
            ownerIndex = this.owners.nextVisible(ownerIndex) + 1;
        }
        this.logger.debug("First index: {} from: {} to: {}", new Object[]{ownerIndex, from, to});
        for (i = from; i <= to; ++i) {
            ownerIndex = this.owners.nextVisible(ownerIndex);
            TinyOwner owner = this.owners.get(ownerIndex++);
            PermissionSet[] permissionSets = this.evaluateOwner(owner);
            result.add(new TinyOwnerDto(owner, this.confluenceService.getUserAvatarUrl(owner.getName())), permissionSets);
        }
        return result;
    }

    public PermissionSet[] evaluateOwner(TinyOwner owner) {
        PermissionSet[] result = new PermissionSet[this.getSpaceLimit()];
        int spaceIndex = 0;
        for (int i = 0; i < result.length; ++i) {
            spaceIndex = this.spaces.nextVisible(spaceIndex);
            TinySpace space = this.spaces.get(spaceIndex++);
            result[i] = this.evaluateOwner(space, owner);
        }
        return result;
    }

    public PermissionSet evaluateOwner(TinySpace space, TinyOwner owner) {
        PermissionSet effectivePermissions = this.createPermissionSet();
        this.evaluateOwner(space, owner, effectivePermissions, false);
        return effectivePermissions;
    }

    protected PermissionSet createPermissionSet() {
        return new PermissionSet();
    }

    @VisibleForTesting
    void evaluateOwner(TinySpace space, TinyOwner owner, PermissionSet effectivePermissions, boolean quickViewAccessCheck) {
        boolean isSuperUser;
        Map<String, PermissionSet> groupPermissionsMap;
        boolean isGlobalAnonymousAccessEnabled = this.evaluatorCache.isGlobalAnonymousAccessEnabled();
        boolean canLogin = owner.canLogin();
        if (quickViewAccessCheck && this.ownerType == OwnerType.TYPE_USER && !isGlobalAnonymousAccessEnabled && !canLogin) {
            return;
        }
        String ownerName = owner.getName();
        boolean explaining = effectivePermissions instanceof ExplainedPermissionSet;
        SpacePermissions spacePermissions = this.evaluatorCache.getSpacePermissions(space.getKey());
        PermissionSet anonymousPermissions = spacePermissions.getAnonymousPermissions();
        if (!owner.isAnonymous() && canLogin || owner.isAnonymous() && (isGlobalAnonymousAccessEnabled || explaining) || owner.isGroup()) {
            effectivePermissions.setAnonymousSpacePermission(anonymousPermissions, isGlobalAnonymousAccessEnabled, canLogin);
        }
        if (this.ownerType == OwnerType.TYPE_GROUP) {
            groupPermissionsMap = spacePermissions.getGroupPermissionMap();
            PermissionSet groupPermissions = groupPermissionsMap.get(ownerName);
            if (groupPermissions != null) {
                effectivePermissions.setGroupSpacePermission(groupPermissions, ownerName);
            }
        } else if (this.ownerType == OwnerType.TYPE_USER) {
            Map<String, PermissionSet> userPermissionsMap;
            PermissionSet permissionSet;
            groupPermissionsMap = spacePermissions.getGroupPermissionMap();
            for (Map.Entry entry : groupPermissionsMap.entrySet()) {
                String groupName = (String)entry.getKey();
                if (!this.evaluatorCache.getGroupMembers(groupName).contains(ownerName)) continue;
                PermissionSet groupPermissions = (PermissionSet)entry.getValue();
                effectivePermissions.setGroupSpacePermission(groupPermissions, groupName);
                if (!quickViewAccessCheck) continue;
                break;
            }
            if ((permissionSet = (userPermissionsMap = spacePermissions.getUserPermissionMap()).get(ownerName)) != null) {
                effectivePermissions.setUserSpacePermission(permissionSet, ownerName);
            }
        }
        boolean bl = isSuperUser = this.ownerType == OwnerType.TYPE_GROUP && "confluence-administrators".equals(ownerName) || this.ownerType == OwnerType.TYPE_USER && this.evaluatorCache.isUserConfluenceAdministrator(ownerName);
        if (isSuperUser) {
            effectivePermissions.setSuperUser();
        }
        if (quickViewAccessCheck) {
            if (isSuperUser) {
                effectivePermissions.setPermission(Permission.VIEW_SPACE_PERMISSION);
            }
        } else {
            if (this.page != null) {
                List<PageRestriction> viewRestrictions = this.getViewRestrictions();
                List<PageRestriction> list = this.getEditRestrictions();
                effectivePermissions.setPageRestrictions(owner, this.evaluatorCache, viewRestrictions, list);
            }
            effectivePermissions.evaluateFinalPermissions(owner, this.page, isGlobalAnonymousAccessEnabled, anonymousPermissions);
        }
    }

    public abstract List<PageRestriction> getViewRestrictions();

    public abstract List<PageRestriction> getEditRestrictions();
}

