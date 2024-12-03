/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface SecurityConstants {
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;
    public static final int PRINCIPAL_RESOURCETYPE = ResourceType.registerResourceType("principal", NAMESPACE);
    public static final DavPropertyName PRINCIPAL_URL = DavPropertyName.create("principal-URL", NAMESPACE);
    public static final DavPropertyName ALTERNATE_URI_SET = DavPropertyName.create("alternate-URI-set", NAMESPACE);
    public static final DavPropertyName GROUP_MEMBER_SET = DavPropertyName.create("group-member-set", NAMESPACE);
    public static final DavPropertyName GROUP_MEMBERSHIP = DavPropertyName.create("group-membership", NAMESPACE);
    public static final DavPropertyName OWNER = DavPropertyName.create("owner", NAMESPACE);
    public static final DavPropertyName GROUP = DavPropertyName.create("group", NAMESPACE);
    public static final DavPropertyName SUPPORTED_PRIVILEGE_SET = DavPropertyName.create("supported-privilege-set", NAMESPACE);
    public static final DavPropertyName CURRENT_USER_PRIVILEGE_SET = DavPropertyName.create("current-user-privilege-set", NAMESPACE);
    public static final DavPropertyName ACL = DavPropertyName.create("acl", NAMESPACE);
    public static final DavPropertyName ACL_RESTRICTIONS = DavPropertyName.create("acl-restrictions", NAMESPACE);
    public static final DavPropertyName INHERITED_ACL_SET = DavPropertyName.create("inherited-acl-set", NAMESPACE);
    public static final DavPropertyName PRINCIPAL_COLLECTION_SET = DavPropertyName.create("principal-collection-set", NAMESPACE);
}

