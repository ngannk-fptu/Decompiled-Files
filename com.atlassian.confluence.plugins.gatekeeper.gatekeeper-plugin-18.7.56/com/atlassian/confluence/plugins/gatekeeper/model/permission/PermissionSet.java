/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Explanation;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationDetailType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionSet
implements Cloneable,
Iterable<Permission> {
    public static final PermissionSet EMPTY_PERMISSION_SET = new PermissionSet(0);
    private static final Logger logger = LoggerFactory.getLogger(PermissionSet.class);
    protected boolean viewRestricted = false;
    protected boolean editRestricted = false;
    private int permissions = 0;
    private int partialPermissions = 0;
    private boolean superUser;

    public PermissionSet() {
    }

    public PermissionSet(int permissions) {
        this.permissions = permissions;
        this.partialPermissions = 0;
        if (!Permission.REMOVE_OWN_CONTENT_PERMISSION.isSupported() && this.isPermitted(Permission.REMOVE_OWN_CONTENT_PERMISSION)) {
            this.unsetPermission(Permission.REMOVE_OWN_CONTENT_PERMISSION);
        }
    }

    public PermissionSet(PermissionSet permissionSet) {
        this.permissions = permissionSet.permissions;
        this.partialPermissions = permissionSet.partialPermissions;
    }

    public PermissionSet(Permission ... permissions) {
        for (Permission permission : permissions) {
            this.setPermission(permission);
        }
    }

    public void reset() {
        this.permissions = 0;
        this.partialPermissions = 0;
        this.superUser = false;
        this.viewRestricted = false;
        this.editRestricted = false;
    }

    public void setAnonymousSpacePermission(PermissionSet permissionSet, boolean isGlobalAnonymousAccessEnabled, boolean canLogin) {
        this.setPermissions(permissionSet);
    }

    public void setUserSpacePermission(PermissionSet permissionSet, String sourceUser) {
        this.setPermissions(permissionSet);
    }

    public void setGroupSpacePermission(PermissionSet permissionSet, String sourceGroup) {
        this.setPermissions(permissionSet);
    }

    public void setSuperUser() {
        this.superUser = true;
    }

    void copyPermissions(PermissionSet permissionSet) {
        this.permissions = permissionSet.permissions;
        this.partialPermissions = permissionSet.partialPermissions;
    }

    public boolean setPermission(Permission permission) {
        if (permission.isSupported()) {
            this.permissions |= permission.getFlag();
            this.partialPermissions &= ~permission.getFlag();
            return true;
        }
        return false;
    }

    public void setPermissions(PermissionSet permissionSet) {
        this.permissions |= permissionSet.permissions;
        this.partialPermissions |= permissionSet.partialPermissions;
    }

    private void setPermissions(List<Permission> permissions) {
        for (Permission permission : permissions) {
            this.setPermission(permission);
        }
    }

    void setAllPermissions() {
        this.permissions = Permissions.ALL_FULL_PERMISSIONS_FLAG;
        this.partialPermissions = 0;
    }

    public void unsetPermission(Permission permission) {
        this.permissions &= ~permission.getFlag();
        this.partialPermissions &= ~permission.getFlag();
    }

    public void unsetPermissions(PermissionSet permissionSet) {
        this.permissions &= ~permissionSet.permissions;
        this.partialPermissions &= ~permissionSet.permissions;
    }

    private void unsetPermissions(List<Permission> permissions) {
        for (Permission permission : permissions) {
            this.unsetPermission(permission);
        }
    }

    public void unsetAllPermissions() {
        this.permissions = 0;
        this.partialPermissions = 0;
    }

    private void setPartial(Permission permission) {
        this.permissions |= permission.getFlag();
        this.partialPermissions |= permission.getFlag();
    }

    public void setPageRestrictions(TinyOwner owner, EvaluatorCache evaluatorCache, List<PageRestriction> viewRestrictions, List<PageRestriction> editRestrictions) {
        if (editRestrictions.size() > 1) {
            logger.warn("Multiple edit restrictions found: {}", editRestrictions);
        }
        this.viewRestricted = this.isRestricted(owner, evaluatorCache, viewRestrictions);
        this.editRestricted = this.viewRestricted || this.isRestricted(owner, evaluatorCache, editRestrictions);
    }

    private boolean isRestricted(TinyOwner owner, EvaluatorCache evaluatorCache, List<PageRestriction> restrictionList) {
        String ownerName = owner.getName();
        for (PageRestriction restriction : restrictionList) {
            boolean permitted = false;
            if (owner.isUser()) {
                for (String groupName : restriction.getGroups()) {
                    if (!evaluatorCache.getGroupMembers(groupName).contains(ownerName)) continue;
                    permitted = true;
                }
                for (String username : restriction.getUsers()) {
                    if (!username.equals(ownerName)) continue;
                    permitted = true;
                }
            }
            if (permitted) continue;
            return true;
        }
        return false;
    }

    public void evaluateFinalPermissions(TinyOwner owner, TinyPage evaluatedPage, boolean isGlobalAnonymousAccessEnabled, PermissionSet anonymousPermissions) {
        boolean spaceAdmin;
        boolean isEvaluatingUser = owner.isUser();
        boolean isEvaluatingPage = evaluatedPage != null;
        boolean hasSetPagePermissions = this.isPermitted(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
        boolean hasCreateEditPermission = this.isPermitted(Permission.CREATE_EDIT_PAGE_PERMISSION);
        this.explainNoSpaceLevelPermissions(Permissions.ALL_PERMISSIONS);
        if (this.isPermitted(Permission.REMOVE_OWN_CONTENT_PERMISSION) && (owner.isAnonymous() || !owner.canLogin() && isEvaluatingUser)) {
            this.unsetPermission(Permission.REMOVE_OWN_CONTENT_PERMISSION);
            this.explain(Permission.REMOVE_OWN_CONTENT_PERMISSION, ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION, owner.isAnonymous() ? ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS : ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_NO_LOGIN);
            if (owner.isAnonymous()) {
                if (!this.isPermitted(Permission.REMOVE_PAGE_PERMISSION)) {
                    this.explain(Permission.REMOVE_PAGE_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS);
                }
                if (!this.isPermitted(Permission.REMOVE_BLOG_PERMISSION)) {
                    this.explain(Permission.REMOVE_BLOG_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS);
                }
                if (!this.isPermitted(Permission.REMOVE_COMMENT_PERMISSION)) {
                    this.explain(Permission.REMOVE_COMMENT_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS);
                }
                if (!this.isPermitted(Permission.REMOVE_ATTACHMENT_PERMISSION)) {
                    this.explain(Permission.REMOVE_ATTACHMENT_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS);
                }
            }
        }
        if (isEvaluatingPage) {
            this.unsetPageRestrictedPermissions(owner);
        }
        if (this.isPermitted(Permission.REMOVE_OWN_CONTENT_PERMISSION) && (owner.canLogin() || !isEvaluatingUser)) {
            if (owner.isGroup()) {
                this.explain(Permission.REMOVE_OWN_CONTENT_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_CONTENT);
            }
            if (isEvaluatingPage) {
                if (!this.isPermitted(Permission.REMOVE_PAGE_PERMISSION) && evaluatedPage.isCreatorMatchesUser(owner)) {
                    this.setPermission(Permission.REMOVE_PAGE_PERMISSION);
                    this.explain(Permission.REMOVE_PAGE_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_PAGES_OVERRIDE);
                }
            } else if (!this.isPermitted(Permission.REMOVE_PAGE_PERMISSION)) {
                this.setPartial(Permission.REMOVE_PAGE_PERMISSION);
                this.explain(Permission.REMOVE_PAGE_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_PAGES);
            }
            if (!this.isPermitted(Permission.REMOVE_BLOG_PERMISSION)) {
                this.setPartial(Permission.REMOVE_BLOG_PERMISSION);
                this.explain(Permission.REMOVE_BLOG_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_BLOGS);
            }
            if (!this.isPermitted(Permission.REMOVE_COMMENT_PERMISSION)) {
                this.setPartial(Permission.REMOVE_COMMENT_PERMISSION);
                this.explain(Permission.REMOVE_COMMENT_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_COMMENTS);
            }
            if (!this.isPermitted(Permission.REMOVE_ATTACHMENT_PERMISSION)) {
                this.setPartial(Permission.REMOVE_ATTACHMENT_PERMISSION);
                this.explain(Permission.REMOVE_ATTACHMENT_PERMISSION, ExplanationType.EXPLAIN_PARTIAL_REMOVE, ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ATTACHMENTS);
            }
        }
        if (spaceAdmin = this.isPermitted(Permission.ADMINISTER_SPACE_PERMISSION)) {
            this.explainSpaceAdmin(isEvaluatingPage);
            if (!isEvaluatingPage) {
                this.setPermission(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
            }
        }
        if (this.superUser) {
            if (hasCreateEditPermission && !this.viewRestricted && !this.editRestricted) {
                this.explain(Permissions.SUPER_USER_DEPENDENT_PERMISSIONS, ExplanationType.EXPLAIN_SUPER_USER, ExplanationDetailType.DETAIL_SUPER_USER_IN_GENERAL, ExplanationDetailType.DETAIL_SUPER_USER_HAS_ALL_PERMISSIONS);
                this.setAllPermissions();
            } else {
                this.explainSuperUserCanNotEdit(hasCreateEditPermission);
                this.setPermissions(Permissions.SUPER_USER_DEPENDENT_PERMISSIONS_EXCEPT_EDIT);
                this.setPermission(Permission.CREATE_EDIT_PAGE_PERMISSION);
            }
        }
        if (isEvaluatingUser && !owner.canLogin()) {
            this.explainCannotLogin(owner, isGlobalAnonymousAccessEnabled, anonymousPermissions);
            if (isGlobalAnonymousAccessEnabled) {
                this.copyPermissions(anonymousPermissions);
                this.unsetPermission(Permission.REMOVE_OWN_CONTENT_PERMISSION);
                if (isEvaluatingPage) {
                    this.unsetPageRestrictedPermissions(owner);
                }
            } else {
                this.unsetAllPermissions();
            }
        }
        if (owner.isAnonymous() && !isGlobalAnonymousAccessEnabled) {
            this.unsetAllPermissions();
        }
        if (hasSetPagePermissions && !spaceAdmin && !this.superUser) {
            boolean hasEffectiveEditPermission = this.isPermitted(Permission.CREATE_EDIT_PAGE_PERMISSION);
            this.explain(Permission.SET_PAGE_PERMISSIONS_PERMISSION, hasEffectiveEditPermission ? ExplanationType.EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_PERMITTED : ExplanationType.EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_NOT_PERMITTED, new ExplanationDetailType[0]);
            if (!hasEffectiveEditPermission) {
                this.unsetPermission(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
            }
        }
    }

    private void unsetPageRestrictedPermissions(TinyOwner owner) {
        if (this.viewRestricted) {
            this.unsetPermissions(Permissions.VIEW_RESTRICTION_DEPENDENT_PERMISSIONS);
        } else if (this.editRestricted) {
            boolean treatAsSpaceAdmin = owner.canLogin() && this.isPermitted(Permission.ADMINISTER_SPACE_PERMISSION);
            this.unsetPermissions(treatAsSpaceAdmin ? Permissions.EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN : Permissions.EDIT_RESTRICTION_DEPENDENT_PERMISSIONS);
        }
    }

    public boolean isEmpty() {
        return this.permissions == 0;
    }

    public boolean isPermitted(Permission permission) {
        return (this.permissions & permission.getFlag()) != 0;
    }

    public boolean isPartiallyPermitted(Permission permission) {
        return (this.partialPermissions & permission.getFlag()) != 0;
    }

    public boolean hasAllPermissions() {
        return this.permissions == Permissions.ALL_FULL_PERMISSIONS_FLAG;
    }

    public boolean hasAnyOf(Collection<Permission> permissions) {
        for (Permission permission : permissions) {
            if (!this.isPermitted(permission)) continue;
            return true;
        }
        return false;
    }

    public boolean canView() {
        return this.isPermitted(Permission.VIEW_SPACE_PERMISSION);
    }

    public boolean canCreateEditPage() {
        return this.isPermitted(Permission.CREATE_EDIT_PAGE_PERMISSION);
    }

    public boolean canRemovePage() {
        return this.isPermitted(Permission.REMOVE_PAGE_PERMISSION);
    }

    public boolean canCreateAttachment() {
        return this.isPermitted(Permission.CREATE_ATTACHMENT_PERMISSION);
    }

    public boolean canRemoveAttachment() {
        return this.isPermitted(Permission.REMOVE_ATTACHMENT_PERMISSION);
    }

    public boolean canComment() {
        return this.isPermitted(Permission.COMMENT_PERMISSION);
    }

    public boolean canRemoveComment() {
        return this.isPermitted(Permission.REMOVE_COMMENT_PERMISSION);
    }

    public boolean canEditBlog() {
        return this.isPermitted(Permission.EDIT_BLOG_PERMISSION);
    }

    public boolean canRemoveBlog() {
        return this.isPermitted(Permission.REMOVE_BLOG_PERMISSION);
    }

    public boolean canRemoveMail() {
        return this.isPermitted(Permission.REMOVE_MAIL_PERMISSION);
    }

    public boolean canAdministerSpace() {
        return this.isPermitted(Permission.ADMINISTER_SPACE_PERMISSION);
    }

    public boolean canExportSpace() {
        return this.isPermitted(Permission.EXPORT_SPACE_PERMISSION);
    }

    public boolean canSetPagePermissions() {
        return this.isPermitted(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
    }

    public int toTransferFormat() {
        return this.partialPermissions << 16 | this.permissions;
    }

    @Override
    public Iterator<Permission> iterator() {
        return Permissions.ALL_PERMISSIONS.iterator();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PermissionSet{");
        for (Permission permission : Permissions.ALL_PERMISSIONS) {
            if ((this.permissions & permission.getFlag()) == 0) continue;
            if (sb.length() != 0) {
                sb.append(',');
            }
            sb.append(permission.getType());
            if ((this.partialPermissions & permission.getFlag()) == 0) continue;
            sb.append("-PARTIAL");
        }
        sb.append('}');
        return sb.toString();
    }

    public String toComparisonString() {
        StringBuilder sb = new StringBuilder();
        for (Permission permission : Permissions.ALL_PERMISSIONS) {
            sb.append(permission.getType()).append('=');
            if ((this.permissions & permission.getFlag()) != 0) {
                if ((this.partialPermissions & permission.getFlag()) != 0) {
                    sb.append('2');
                } else {
                    sb.append('1');
                }
            } else {
                sb.append('0');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean hasSamePermissions(PermissionSet other) {
        return this.permissions == other.permissions && this.partialPermissions == other.partialPermissions;
    }

    protected void explain(Permission permission, Explanation explanation) {
    }

    protected void explain(List<Permission> permissions, Explanation explanation) {
    }

    protected void explain(Permission permission, List<Explanation> explanations) {
    }

    protected void explain(List<Permission> permissions, List<Explanation> explanations) {
    }

    protected void explain(Permission permission, ExplanationType explanationType, ExplanationDetailType ... detailTypes) {
    }

    protected void explain(List<Permission> permissions, ExplanationType explanationType, ExplanationDetailType ... detailTypes) {
    }

    protected void explainCannotLogin(TinyOwner owner, boolean isGlobalAnonymousAccessEnabled, PermissionSet anonymousPermissions) {
    }

    protected void explainSpaceAdmin(boolean isEvaluatingPage) {
    }

    protected void explainSuperUserCanNotEdit(boolean hasSpaceCreateEditPermission) {
    }

    protected void explainNoSpaceLevelPermissions(List<Permission> permissions) {
    }
}

