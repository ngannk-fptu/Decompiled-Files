/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.NeverPermittedContentPermission;

@Deprecated
public class NeverPermittedContentPermissionSet
extends ContentPermissionSet {
    public NeverPermittedContentPermissionSet(String type, ContentEntityObject owningContent) {
        super(type, owningContent);
    }

    public static NeverPermittedContentPermissionSet buildFrom(String type, ContentEntityObject owningContent) {
        NeverPermittedContentPermissionSet contentPermissions = new NeverPermittedContentPermissionSet(type, owningContent);
        contentPermissions.addContentPermission(new NeverPermittedContentPermission(type));
        return contentPermissions;
    }
}

