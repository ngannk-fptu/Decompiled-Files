/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Permission {
    private static int currentFlag = 1;
    public static final Permission VIEW_SPACE_PERMISSION = new Permission("VIEWSPACE", "view-space");
    public static final Permission REMOVE_OWN_CONTENT_PERMISSION = new Permission("REMOVEOWNCONTENT", "remove-own");
    public static final Permission CREATE_EDIT_PAGE_PERMISSION = new Permission("EDITSPACE", "page-add");
    public static final Permission REMOVE_PAGE_PERMISSION = new Permission("REMOVEPAGE", "page-remove");
    public static final Permission EDIT_BLOG_PERMISSION = new Permission("EDITBLOG", "blog-add");
    public static final Permission REMOVE_BLOG_PERMISSION = new Permission("REMOVEBLOG", "blog-remove");
    public static final Permission COMMENT_PERMISSION = new Permission("COMMENT", "comment-add");
    public static final Permission REMOVE_COMMENT_PERMISSION = new Permission("REMOVECOMMENT", "comment-remove");
    public static final Permission CREATE_ATTACHMENT_PERMISSION = new Permission("CREATEATTACHMENT", "attachment-add");
    public static final Permission REMOVE_ATTACHMENT_PERMISSION = new Permission("REMOVEATTACHMENT", "attachment-remove");
    public static final Permission SET_PAGE_PERMISSIONS_PERMISSION = new Permission("SETPAGEPERMISSIONS", "page-restrict");
    public static final Permission REMOVE_MAIL_PERMISSION = new Permission("REMOVEMAIL", "mail-remove");
    public static final Permission EXPORT_SPACE_PERMISSION = new Permission("EXPORTSPACE", "space-export");
    public static final Permission ADMINISTER_SPACE_PERMISSION = new Permission("SETSPACEPERMISSIONS", "space-admin");
    @JsonIgnore
    private String type;
    private int flag;
    private String label;
    private boolean supported;
    private boolean allowedForAnonymous;

    private Permission(String type, String label) {
        this.type = type;
        this.flag = currentFlag;
        this.label = label;
        this.supported = true;
        if ("remove-own".equals(label)) {
            this.supported = ConfluenceVersion.isDeleteOwnPermissionSupported();
        }
        this.allowedForAnonymous = !"space-admin".equals(label) && !"page-restrict".equals(label) && !"remove-own".equals(label);
        currentFlag <<= 1;
    }

    public String getType() {
        return this.type;
    }

    public int getFlag() {
        return this.flag;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean equals(String type) {
        return this.type.equals(type);
    }

    public boolean isSupported() {
        return this.supported;
    }

    public boolean isAllowedForAnonymous() {
        return this.allowedForAnonymous;
    }

    public String toString() {
        return this.type;
    }
}

