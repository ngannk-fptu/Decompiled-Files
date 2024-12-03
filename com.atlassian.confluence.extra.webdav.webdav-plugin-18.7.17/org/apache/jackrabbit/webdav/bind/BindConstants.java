/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.bind;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface BindConstants {
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;
    public static final String XML_BIND = "bind";
    public static final String XML_REBIND = "rebind";
    public static final String XML_UNBIND = "unbind";
    public static final String XML_SEGMENT = "segment";
    public static final String XML_HREF = "href";
    public static final String XML_PARENT = "parent";
    public static final String METHODS = "BIND, REBIND, UNBIND";
    public static final DavPropertyName RESOURCEID = DavPropertyName.create("resource-id");
    public static final DavPropertyName PARENTSET = DavPropertyName.create("parent-set");
}

