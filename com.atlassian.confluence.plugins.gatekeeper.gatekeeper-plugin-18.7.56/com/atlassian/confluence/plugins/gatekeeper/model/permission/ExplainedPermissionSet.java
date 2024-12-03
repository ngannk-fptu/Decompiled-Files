/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Explanation;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationDetailType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationFormatter;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.RefinedExplanation;
import com.atlassian.confluence.plugins.gatekeeper.util.ExplanationMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplainedPermissionSet
extends PermissionSet {
    private static final Logger logger = LoggerFactory.getLogger(ExplainedPermissionSet.class);
    private static List<Explanation> EMPTY_EXPLANATIONS = new ArrayList<Explanation>();
    private Map<Permission, Map<ExplanationType, List<Explanation>>> globalExplanationMap;
    private Map<Permission, List<Explanation>> finalExplanationMap;
    private OwnerType ownerType;
    private ExplanationFormatter explanationFormatter;
    private boolean isGlobalAnonymousAccessEnabled;
    private TinyPage page;
    private ExplanationMapper explanationMapper;
    private boolean groupHasParents;

    public ExplainedPermissionSet(OwnerType ownerType, ExplanationFormatter explanationFormatter, ExplanationMapper explanationMapper) {
        this(ownerType, explanationFormatter, null, explanationMapper);
    }

    public ExplainedPermissionSet(OwnerType ownerType, ExplanationFormatter explanationFormatter, TinyPage page, ExplanationMapper explanationMapper) {
        this.ownerType = ownerType;
        this.explanationFormatter = explanationFormatter;
        this.explanationMapper = explanationMapper;
        this.globalExplanationMap = new HashMap<Permission, Map<ExplanationType, List<Explanation>>>();
        this.finalExplanationMap = new HashMap<Permission, List<Explanation>>();
        this.isGlobalAnonymousAccessEnabled = false;
        this.page = page;
    }

    public List<Explanation> getExplanationList(Permission permission) {
        return this.finalExplanationMap.get(permission);
    }

    public Map<Permission, RefinedExplanation> getRefinedExplanationMap() {
        HashMap<Permission, RefinedExplanation> refinedExplanationMap = new HashMap<Permission, RefinedExplanation>(this.finalExplanationMap.size());
        for (Map.Entry<Permission, List<Explanation>> entry : this.finalExplanationMap.entrySet()) {
            Permission permission = entry.getKey();
            List<Explanation> finalExplanations = entry.getValue();
            RefinedExplanation refinedExplanation = this.explanationMapper.refine(this.ownerType, finalExplanations, this.isGlobalAnonymousAccessEnabled, permission, this.page, this.groupHasParents);
            refinedExplanationMap.put(permission, refinedExplanation);
        }
        return refinedExplanationMap;
    }

    private List<Explanation> createOrGetExplanationList(Permission permission, ExplanationType type) {
        Map permissionExplanations = this.globalExplanationMap.computeIfAbsent(permission, p -> new HashMap());
        return permissionExplanations.computeIfAbsent(type, t -> new ArrayList());
    }

    private Explanation getFirstExplanationByPermissionAndType(Permission permission, ExplanationType type) {
        List<Explanation> explanationList = this.createOrGetExplanationList(permission, type);
        if (explanationList.isEmpty()) {
            explanationList.add(new Explanation(type));
        }
        return explanationList.get(0);
    }

    private void addDetail(Permission permission, ExplanationType explanationType, ExplanationDetailType detailType) {
        this.getFirstExplanationByPermissionAndType(permission, explanationType).addDetailType(detailType);
    }

    @Override
    public void setAnonymousSpacePermission(PermissionSet permissionSet, boolean isGlobalAnonymousAccessEnabled, boolean canLogin) {
        super.setAnonymousSpacePermission(permissionSet, isGlobalAnonymousAccessEnabled, canLogin);
        for (Permission permission : permissionSet) {
            if (!permissionSet.isPermitted(permission)) continue;
            logger.debug("Adding space permission {}, source: anonymous", (Object)permission);
            Explanation explanation = this.getFirstExplanationByPermissionAndType(permission, ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION);
            explanation.addAnonymousSource(isGlobalAnonymousAccessEnabled, canLogin);
        }
    }

    @Override
    public void setUserSpacePermission(PermissionSet permissionSet, String sourceUser) {
        super.setUserSpacePermission(permissionSet, sourceUser);
        for (Permission permission : permissionSet) {
            if (!permissionSet.isPermitted(permission)) continue;
            logger.debug("Adding space permission {}, source: user", (Object)permission);
            Explanation explanation = this.getFirstExplanationByPermissionAndType(permission, ExplanationType.EXPLAIN_SPACE_USER_PERMISSION);
            explanation.addUserSource(sourceUser);
        }
    }

    @Override
    public void setGroupSpacePermission(PermissionSet permissionSet, String sourceGroup) {
        super.setGroupSpacePermission(permissionSet, sourceGroup);
        for (Permission permission : permissionSet) {
            if (!permissionSet.isPermitted(permission)) continue;
            logger.debug("Adding space permission {}, source: group {}", (Object)permission, (Object)sourceGroup);
            Explanation explanation = this.getFirstExplanationByPermissionAndType(permission, ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION);
            explanation.addGroupSource(sourceGroup);
        }
    }

    @Override
    public void setPageRestrictions(TinyOwner owner, EvaluatorCache evaluatorCache, List<PageRestriction> viewRestrictions, List<PageRestriction> editRestrictions) {
        super.setPageRestrictions(owner, evaluatorCache, viewRestrictions, editRestrictions);
        this.generateRestrictionExplanations(owner, evaluatorCache, viewRestrictions, Permissions.VIEW_RESTRICTION_DEPENDENT_PERMISSIONS);
        this.generateRestrictionExplanations(owner, evaluatorCache, editRestrictions, Permissions.EDIT_RESTRICTION_DEPENDENT_PERMISSIONS);
    }

    private void generateRestrictionExplanations(TinyOwner owner, EvaluatorCache evaluatorCache, List<PageRestriction> restrictionList, List<Permission> explainedPermissions) {
        String ownerName = owner.getName();
        ArrayList<Explanation> restrictionExplanations = new ArrayList<Explanation>();
        for (PageRestriction restriction : restrictionList) {
            logger.debug("Explaining restriction: {}", (Object)restriction);
            Explanation explanation = new Explanation(restriction);
            if (owner.isUser()) {
                for (String groupName : restriction.getGroups()) {
                    if (!evaluatorCache.getGroupMembers(groupName).contains(ownerName)) continue;
                    explanation.addGroupSource(groupName);
                }
                for (String username : restriction.getUsers()) {
                    if (!username.equals(ownerName)) continue;
                    explanation.addUserSource(username);
                }
            }
            restrictionExplanations.add(explanation);
        }
        if (restrictionExplanations.isEmpty()) {
            if (explainedPermissions == Permissions.VIEW_RESTRICTION_DEPENDENT_PERMISSIONS) {
                this.explain(explainedPermissions, new Explanation(ExplanationType.EXPLAIN_VIEW_RESTRICTION_NOT_FOUND));
            } else {
                this.explain(explainedPermissions, new Explanation(ExplanationType.EXPLAIN_EDIT_RESTRICTION_NOT_FOUND));
            }
        } else {
            this.explain(explainedPermissions, restrictionExplanations);
        }
    }

    @Override
    protected void explain(Permission permission, Explanation explanation) {
        ExplanationType type = explanation.getType();
        this.createOrGetExplanationList(permission, type).add(explanation);
    }

    @Override
    protected void explain(List<Permission> permissions, Explanation explanation) {
        for (Permission permission : permissions) {
            this.explain(permission, explanation);
        }
    }

    @Override
    protected void explain(Permission permission, List<Explanation> explanations) {
        for (Explanation explanation : explanations) {
            this.explain(permission, explanation);
        }
    }

    @Override
    protected void explain(List<Permission> permissions, List<Explanation> explanations) {
        for (Permission permission : permissions) {
            this.explain(permission, explanations);
        }
    }

    @Override
    protected void explain(Permission permission, ExplanationType explanationType, ExplanationDetailType ... detailTypes) {
        Explanation explanation = new Explanation(explanationType, detailTypes);
        this.createOrGetExplanationList(permission, explanationType).add(explanation);
    }

    @Override
    protected void explain(List<Permission> permissions, ExplanationType explanationType, ExplanationDetailType ... detailTypes) {
        for (Permission permission : permissions) {
            this.explain(permission, explanationType, detailTypes);
        }
    }

    @Override
    protected void explainNoSpaceLevelPermissions(List<Permission> permissions) {
        for (Permission permission : Permissions.ALL_PERMISSIONS) {
            if (!this.hasNoSpaceLevelPermissions(permission)) continue;
            this.explain(permission, ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND, new ExplanationDetailType[0]);
        }
    }

    private boolean hasNoSpaceLevelPermissions(Permission permission) {
        return this.createOrGetExplanationList(permission, ExplanationType.EXPLAIN_SPACE_USER_PERMISSION).size() == 0 && this.createOrGetExplanationList(permission, ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION).size() == 0 && this.createOrGetExplanationList(permission, ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION).size() == 0;
    }

    @Override
    protected void explainCannotLogin(TinyOwner owner, boolean isGlobalAnonymousAccessEnabled, PermissionSet anonymousPermissions) {
        for (Permission permission : Permissions.ANONYMOUS_PERMISSIONS) {
            boolean isAnonymousPermitted = anonymousPermissions.isPermitted(permission);
            boolean isAnonymousViewRestricted = this.viewRestricted && Permissions.VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.contains(permission);
            boolean isAnonymousEditRestricted = this.editRestricted && Permissions.EDIT_RESTRICTION_DEPENDENT_PERMISSIONS.contains(permission);
            this.explain(permission, ExplanationType.EXPLAIN_LOGIN, new ExplanationDetailType[0]);
            if (!owner.hasCanUse()) {
                this.addDetail(permission, ExplanationType.EXPLAIN_LOGIN, ExplanationDetailType.DETAIL_NO_CAN_USE);
            }
            if (!owner.isActive()) {
                this.addDetail(permission, ExplanationType.EXPLAIN_LOGIN, ExplanationDetailType.DETAIL_USER_DISABLED);
            }
            if (permission == Permission.REMOVE_OWN_CONTENT_PERMISSION) continue;
            if (isAnonymousEditRestricted) {
                this.addDetail(permission, ExplanationType.EXPLAIN_LOGIN, ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_EDIT_RESTRICTED);
                continue;
            }
            if (isAnonymousViewRestricted) {
                this.addDetail(permission, ExplanationType.EXPLAIN_LOGIN, ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_VIEW_RESTRICTED);
                continue;
            }
            if (isAnonymousPermitted) {
                this.addDetail(permission, ExplanationType.EXPLAIN_LOGIN, isGlobalAnonymousAccessEnabled ? ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_ENABLED : ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_DISABLED);
                continue;
            }
            this.addDetail(permission, ExplanationType.EXPLAIN_LOGIN, isGlobalAnonymousAccessEnabled ? ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_NOT_PERMITTED_GLOBALLY_ENABLED : ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_NOT_PERMITTED_GLOBALLY_DISABLED);
        }
        ExplanationDetailType[] details = this.createLoginExplanationDetails(owner, ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_INVALID);
        this.explain(Permission.ADMINISTER_SPACE_PERMISSION, ExplanationType.EXPLAIN_LOGIN, details);
        this.explain(Permission.SET_PAGE_PERMISSIONS_PERMISSION, ExplanationType.EXPLAIN_LOGIN, details);
    }

    private ExplanationDetailType[] createLoginExplanationDetails(TinyOwner owner, ExplanationDetailType additionalDetail) {
        ArrayList<ExplanationDetailType> detailsArray = new ArrayList<ExplanationDetailType>();
        if (!owner.hasCanUse()) {
            detailsArray.add(ExplanationDetailType.DETAIL_NO_CAN_USE);
        }
        if (!owner.isActive()) {
            detailsArray.add(ExplanationDetailType.DETAIL_USER_DISABLED);
        }
        if (additionalDetail != null) {
            detailsArray.add(additionalDetail);
        }
        ExplanationDetailType[] result = new ExplanationDetailType[detailsArray.size()];
        detailsArray.toArray(result);
        return result;
    }

    @Override
    protected void explainSpaceAdmin(boolean isEvaluatingPage) {
        if (isEvaluatingPage) {
            if (!this.viewRestricted && this.editRestricted && this.isPermitted(Permission.REMOVE_PAGE_PERMISSION)) {
                this.explain(Permission.REMOVE_PAGE_PERMISSION, ExplanationType.EXPLAIN_SPACE_ADMIN_CAN_DELETE_EDIT_RESTRICTED_PAGE, new ExplanationDetailType[0]);
            }
            this.explain(Permission.REMOVE_ATTACHMENT_PERMISSION, ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN, this.isPermitted(Permission.VIEW_SPACE_PERMISSION) ? ExplanationDetailType.DETAIL_SPACE_ADMIN_REMOVE_ATTACHMENT_VERSIONS : ExplanationDetailType.DETAIL_SPACE_ADMIN_REMOVE_ATTACHMENT_VERSIONS_RESTRICTED);
        }
        for (Permission permission : Permissions.SPACE_ADMIN_DEPENDENT_PERMISSIONS) {
            if (this.isPermitted(permission)) continue;
            this.explain(permission, ExplanationType.EXPLAIN_SPACE_ADMIN_CAN_OVERRIDE, new ExplanationDetailType[0]);
        }
        if (isEvaluatingPage) {
            this.explain(Permission.SET_PAGE_PERMISSIONS_PERMISSION, ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN, this.isPermitted(Permission.VIEW_SPACE_PERMISSION) ? ExplanationDetailType.DETAIL_SPACE_ADMIN_RESTRICTIONS_VIEW_PERMITTED : ExplanationDetailType.DETAIL_SPACE_ADMIN_RESTRICTIONS_VIEW_NOT_PERMITTED);
        } else {
            this.explain(Permission.SET_PAGE_PERMISSIONS_PERMISSION, ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN, ExplanationDetailType.DETAIL_SPACE_ADMIN_RESTRICTIONS_IN_GENERAL);
        }
    }

    @Override
    protected void explainSuperUserCanNotEdit(boolean hasSpaceCreateEditPermission) {
        this.explain(Permissions.SUPER_USER_DEPENDENT_PERMISSIONS_EXCEPT_EDIT, ExplanationType.EXPLAIN_SUPER_USER, ExplanationDetailType.DETAIL_SUPER_USER_IN_GENERAL, ExplanationDetailType.DETAIL_SUPER_USER_HAS_ALL_PERMISSIONS);
        this.explain(Permission.CREATE_EDIT_PAGE_PERMISSION, ExplanationType.EXPLAIN_SUPER_USER, ExplanationDetailType.DETAIL_SUPER_USER_IN_GENERAL);
        if (!hasSpaceCreateEditPermission) {
            this.addDetail(Permission.CREATE_EDIT_PAGE_PERMISSION, ExplanationType.EXPLAIN_SUPER_USER, ExplanationDetailType.DETAIL_SUPER_USER_NO_SPACE_EDIT_PERMISSION);
        }
        if (this.viewRestricted) {
            this.addDetail(Permission.CREATE_EDIT_PAGE_PERMISSION, ExplanationType.EXPLAIN_SUPER_USER, ExplanationDetailType.DETAIL_SUPER_USER_VIEW_RESTRICTION);
        } else if (this.editRestricted) {
            this.addDetail(Permission.CREATE_EDIT_PAGE_PERMISSION, ExplanationType.EXPLAIN_SUPER_USER, ExplanationDetailType.DETAIL_SUPER_USER_EDIT_RESTRICTION);
        }
    }

    @Override
    public void evaluateFinalPermissions(TinyOwner owner, TinyPage evaluatedPage, boolean isGlobalAnonymousAccessEnabled, PermissionSet anonymousPermissions) {
        super.evaluateFinalPermissions(owner, evaluatedPage, isGlobalAnonymousAccessEnabled, anonymousPermissions);
        this.isGlobalAnonymousAccessEnabled = isGlobalAnonymousAccessEnabled;
        for (Permission permission : Permissions.ALL_PERMISSIONS) {
            List<Explanation> explanations = permission.isSupported() ? this.getCombinedExplanationList(permission) : EMPTY_EXPLANATIONS;
            for (Explanation explanation : explanations) {
                explanation.format(permission, this.ownerType, this.explanationFormatter);
            }
            this.finalExplanationMap.put(permission, explanations);
            logger.debug("Final explanations for permission: {} are: {}", (Object)permission, explanations);
        }
    }

    private List<Explanation> getCombinedExplanationList(Permission permission) {
        ArrayList<Explanation> result = new ArrayList<Explanation>();
        Map<ExplanationType, List<Explanation>> permissionExplanations = this.globalExplanationMap.get(permission);
        if (permissionExplanations == null) {
            return result;
        }
        for (ExplanationType type : ExplanationType.values()) {
            List<Explanation> explanations = permissionExplanations.get((Object)type);
            if (explanations == null) continue;
            result.addAll(explanations);
        }
        return result;
    }

    public TinyPage getPage() {
        return this.page;
    }

    public boolean isGroupHasParents() {
        return this.groupHasParents;
    }

    public void setGroupHasParents(boolean groupHasParents) {
        this.groupHasParents = groupHasParents;
    }
}

