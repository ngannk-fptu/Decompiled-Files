/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationDetailType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationFormatter;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Source;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect
public class Explanation {
    @JsonProperty
    private ExplanationType type;
    @JsonProperty
    private TinyPage restrictingPage;
    @JsonProperty
    private List<Source> sourceList = new ArrayList<Source>();
    @JsonProperty
    private List<ExplanationDetailType> detailTypeList = new ArrayList<ExplanationDetailType>();
    @JsonProperty
    private String description;
    @JsonProperty
    private List<String> details;
    @JsonProperty
    private boolean permitted;
    @JsonProperty
    private boolean partial;

    public Explanation() {
    }

    public Explanation(ExplanationType type) {
        this.type = type;
        this.restrictingPage = null;
    }

    public Explanation(ExplanationType type, ExplanationDetailType ... detailTypes) {
        this.type = type;
        if (detailTypes != null) {
            Collections.addAll(this.detailTypeList, detailTypes);
        }
    }

    public Explanation(PageRestriction restriction) {
        switch (restriction.getType()) {
            case EDIT_RESTRICTION: {
                this.type = ExplanationType.EXPLAIN_EDIT_RESTRICTION;
                break;
            }
            case EXPLICIT_VIEW_RESTRICTION: {
                this.type = ExplanationType.EXPLAIN_EXPLICIT_VIEW_RESTRICTION;
                break;
            }
            case INHERITED_VIEW_RESTRICTION: {
                this.type = ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION;
            }
        }
        this.restrictingPage = restriction.getPage();
    }

    void addAnonymousSource(boolean isGlobalAnonymousAccessEnabled, boolean canLogin) {
        this.addSource(new Source(OwnerType.TYPE_ANONYMOUS, isGlobalAnonymousAccessEnabled, canLogin));
    }

    void addUserSource(String userName) {
        this.addSource(new Source(OwnerType.TYPE_USER, userName));
    }

    public void addGroupSource(String groupName) {
        this.addSource(new Source(OwnerType.TYPE_GROUP, groupName));
    }

    public void addDetailType(ExplanationDetailType detailType) {
        this.detailTypeList.add(detailType);
    }

    private void addSource(Source source) {
        if (!this.sourceList.contains(source)) {
            this.sourceList.add(source);
        }
    }

    void format(Permission permission, OwnerType ownerType, ExplanationFormatter explanationFormatter) {
        Source source;
        boolean isSpacePermission = this.type == ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND || this.type == ExplanationType.EXPLAIN_SPACE_USER_PERMISSION || this.type == ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION || this.type == ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION;
        boolean isDeleteOwnDisabled = this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS) || this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_PARTIAL_REMOVE_OWN_NO_LOGIN);
        boolean isViewRestriction = this.type == ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION || this.type == ExplanationType.EXPLAIN_EXPLICIT_VIEW_RESTRICTION;
        boolean isEditRestriction = this.type == ExplanationType.EXPLAIN_EDIT_RESTRICTION;
        boolean isPageRestriction = isViewRestriction || isEditRestriction;
        boolean isNoPageRestriction = this.type == ExplanationType.EXPLAIN_VIEW_RESTRICTION_NOT_FOUND || this.type == ExplanationType.EXPLAIN_EDIT_RESTRICTION_NOT_FOUND;
        boolean hasSources = !this.sourceList.isEmpty();
        boolean hasPermittingSources = hasSources;
        if (hasPermittingSources && (source = this.sourceList.get(0)).isAnonymous() && !source.isGlobalAnonymousAccessEnabled()) {
            if (ownerType == OwnerType.TYPE_ANONYMOUS) {
                hasPermittingSources = false;
            }
            if (ownerType != OwnerType.TYPE_ANONYMOUS && !source.canLogin()) {
                hasPermittingSources = false;
            }
        }
        if (isDeleteOwnDisabled) {
            hasPermittingSources = false;
        }
        this.permitted = isSpacePermission && hasPermittingSources || isNoPageRestriction || isPageRestriction && hasPermittingSources || this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_ENABLED) || this.type == ExplanationType.EXPLAIN_PARTIAL_REMOVE || this.type == ExplanationType.EXPLAIN_SUPER_USER || this.type == ExplanationType.EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_PERMITTED || this.type == ExplanationType.EXPLAIN_SPACE_ADMIN_CAN_DELETE_EDIT_RESTRICTED_PAGE || this.type == ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN && !this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_SPACE_ADMIN_REMOVE_ATTACHMENT_VERSIONS_RESTRICTED);
        boolean bl = this.partial = this.type == ExplanationType.EXPLAIN_SPACE_ADMIN_CAN_OVERRIDE;
        if (permission == Permission.CREATE_EDIT_PAGE_PERMISSION && this.type == ExplanationType.EXPLAIN_SUPER_USER) {
            boolean hasSpaceEditPermission = !this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_SUPER_USER_NO_SPACE_EDIT_PERMISSION);
            boolean hasViewRestriction = this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_SUPER_USER_VIEW_RESTRICTION);
            boolean hasEditRestriction = this.detailTypeList.contains((Object)ExplanationDetailType.DETAIL_SUPER_USER_EDIT_RESTRICTION);
            boolean bl2 = this.permitted = hasSpaceEditPermission && !hasViewRestriction && !hasEditRestriction;
            if (!this.permitted) {
                this.partial = true;
            }
        }
        String permissionName = explanationFormatter.getPermissionName(permission.getLabel());
        this.description = explanationFormatter.getDescription(this.type.getI18nKey());
        this.details = new ArrayList<String>();
        if (isPageRestriction) {
            this.details.add(explanationFormatter.getDetail(this.type.getI18nKey(), this.restrictingPage));
        }
        if (isSpacePermission || isPageRestriction) {
            if (isViewRestriction) {
                permissionName = explanationFormatter.getPermissionName("restriction-view-access");
            } else if (isEditRestriction) {
                permissionName = explanationFormatter.getPermissionName("restriction-edit-access");
            }
            if (hasSources) {
                for (Source source2 : this.sourceList) {
                    if (source2.isGroup()) {
                        this.details.add(explanationFormatter.getDetail("permitted-group", permissionName, source2.getName()));
                        continue;
                    }
                    if (source2.isUser()) {
                        this.details.add(explanationFormatter.getDetail("permitted-individually", permissionName));
                        continue;
                    }
                    if (!source2.isAnonymous()) continue;
                    this.details.add(explanationFormatter.getDetail("permitted-anonymously", permissionName));
                    if (ownerType == OwnerType.TYPE_ANONYMOUS) {
                        this.details.add(explanationFormatter.getDetail(source2.isGlobalAnonymousAccessEnabled() ? "anonymous-globally-enabled" : "anonymous-globally-disabled", permissionName));
                        continue;
                    }
                    this.details.add(explanationFormatter.getDetail(source2.canLogin() ? "anonymous-can-login" : "anonymous-can-not-login", permissionName));
                }
            } else {
                if (ownerType == OwnerType.TYPE_USER) {
                    this.details.add(explanationFormatter.getDetail("not-permitted-individually", permissionName));
                    this.details.add(explanationFormatter.getDetail("not-permitted-user-groups", permissionName));
                } else if (ownerType == OwnerType.TYPE_GROUP) {
                    this.details.add(explanationFormatter.getDetail("not-permitted-group", permissionName));
                }
                if (Permissions.ANONYMOUS_PERMISSIONS.contains(permission)) {
                    this.details.add(explanationFormatter.getDetail("not-permitted-anonymously", permissionName));
                } else {
                    this.details.add(explanationFormatter.getDetail("anonymous-invalid", permissionName));
                }
            }
        } else if (this.detailTypeList.isEmpty()) {
            this.details.add(explanationFormatter.getDetail(this.type.getI18nKey(), permissionName));
        }
        for (ExplanationDetailType detailType : this.detailTypeList) {
            this.details.add(explanationFormatter.getDetail(detailType.getI18nKey(), permissionName));
        }
    }

    public boolean isPermitted() {
        return this.permitted;
    }

    public boolean isPartial() {
        return this.partial;
    }

    public ExplanationType getType() {
        return this.type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getDetails() {
        return this.details;
    }

    public List<Source> getSourceList() {
        return this.sourceList;
    }

    public List<ExplanationDetailType> getDetailTypeList() {
        return this.detailTypeList;
    }

    @VisibleForTesting
    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    @VisibleForTesting
    public void setRestrictingPage(TinyPage restrictingPage) {
        this.restrictingPage = restrictingPage;
    }

    public TinyPage getRestrictingPage() {
        return this.restrictingPage;
    }
}

