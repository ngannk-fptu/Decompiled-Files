/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

public enum ExplanationDetailType {
    DETAIL_GROUP_PERMITTED("group-permitted"),
    DETAIL_INDIVIDUALLY_PERMITTED("individually-permitted"),
    DETAIL_ANONYMOUSLY_PERMITTED("anonymously-permitted"),
    DETAIL_ANONYMOUS_GLOBALLY_DISABLED("anonymous-globally-disabled"),
    DETAIL_LOGIN_ANONYMOUS_NOT_PERMITTED_GLOBALLY_DISABLED("login-anonymous-not-permitted-globally-disabled"),
    DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_DISABLED("login-anonymous-permitted-globally-disabled"),
    DETAIL_LOGIN_ANONYMOUS_NOT_PERMITTED_GLOBALLY_ENABLED("login-anonymous-not-permitted-globally-enabled"),
    DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_ENABLED("login-anonymous-permitted-globally-enabled"),
    DETAIL_LOGIN_ANONYMOUS_VIEW_RESTRICTED("login-anonymous-view-restricted"),
    DETAIL_LOGIN_ANONYMOUS_EDIT_RESTRICTED("login-anonymous-edit-restricted"),
    DETAIL_LOGIN_ANONYMOUS_INVALID("login-anonymous-invalid"),
    DETAIL_NO_CAN_USE("no-can-use"),
    DETAIL_USER_DISABLED("user-disabled"),
    DETAIL_SPACE_ADMIN_RESTRICTIONS_IN_GENERAL("space-admin-restrictions-in-general"),
    DETAIL_SPACE_ADMIN_RESTRICTIONS_VIEW_PERMITTED("space-admin-restrictions-view-permitted"),
    DETAIL_SPACE_ADMIN_RESTRICTIONS_VIEW_NOT_PERMITTED("space-admin-restrictions-view-not-permitted"),
    DETAIL_SPACE_ADMIN_REMOVE_ATTACHMENT_VERSIONS("space-admin-remove-attachment-versions"),
    DETAIL_SPACE_ADMIN_REMOVE_ATTACHMENT_VERSIONS_RESTRICTED("space-admin-remove-attachment-versions-restricted"),
    DETAIL_PARTIAL_REMOVE_OWN_ANONYMOUS("partial-remove-own-anonymous"),
    DETAIL_PARTIAL_REMOVE_OWN_NO_LOGIN("partial-remove-own-no-login"),
    DETAIL_PARTIAL_REMOVE_OWN_PAGES("partial-remove-own-pages"),
    DETAIL_PARTIAL_REMOVE_OWN_CONTENT("partial-remove-own-content"),
    DETAIL_PARTIAL_REMOVE_OWN_PAGES_OVERRIDE("partial-remove-own-pages-override"),
    DETAIL_PARTIAL_REMOVE_OWN_BLOGS("partial-remove-own-blogs"),
    DETAIL_PARTIAL_REMOVE_OWN_COMMENTS("partial-remove-own-comments"),
    DETAIL_PARTIAL_REMOVE_OWN_ATTACHMENTS("partial-remove-own-attachments"),
    DETAIL_SUPER_USER_IN_GENERAL("super-user-in-general"),
    DETAIL_SUPER_USER_HAS_ALL_PERMISSIONS("super-user-has-all-permissions"),
    DETAIL_SUPER_USER_NO_SPACE_EDIT_PERMISSION("super-user-no-space-edit-permission"),
    DETAIL_SUPER_USER_VIEW_RESTRICTION("super-user-view-restrictions"),
    DETAIL_SUPER_USER_EDIT_RESTRICTION("super-user-edit-restrictions");

    private String i18nKey;

    private ExplanationDetailType(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return this.i18nKey;
    }
}

