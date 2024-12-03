/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

public enum ExplanationType {
    EXPLAIN_SPACE_PERMISSION_NOT_FOUND("space-permission-not-found"),
    EXPLAIN_SPACE_USER_PERMISSION("space-user-permission"),
    EXPLAIN_SPACE_GROUP_PERMISSION("space-group-permission"),
    EXPLAIN_SPACE_ANONYMOUS_PERMISSION("space-anonymous-permission"),
    EXPLAIN_INHERITED_VIEW_RESTRICTION("inherited-view-restriction"),
    EXPLAIN_EXPLICIT_VIEW_RESTRICTION("explicit-view-restriction"),
    EXPLAIN_VIEW_RESTRICTION_NOT_FOUND("view-restriction-not-found"),
    EXPLAIN_EDIT_RESTRICTION("edit-restriction"),
    EXPLAIN_EDIT_RESTRICTION_NOT_FOUND("edit-restriction-not-found"),
    EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_PERMITTED("set-permissions-depends-edit-permitted"),
    EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_NOT_PERMITTED("set-permissions-depends-edit-not-permitted"),
    EXPLAIN_IMPLICIT_SPACE_ADMIN("implicit-space-admin"),
    EXPLAIN_SPACE_ADMIN_CAN_DELETE_EDIT_RESTRICTED_PAGE("space-admin-can-delete-edit-restricted-page"),
    EXPLAIN_SPACE_ADMIN_CAN_OVERRIDE("space-admin-can-override"),
    EXPLAIN_SUPER_USER("super-user"),
    EXPLAIN_PARTIAL_REMOVE("partial-remove"),
    EXPLAIN_LOGIN("login");

    private String i18nKey;

    private ExplanationType(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return this.i18nKey;
    }
}

