/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface OrderingConstants {
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;
    public static final String ORDERING_TYPE_CUSTOM = "DAV:custom";
    public static final String ORDERING_TYPE_UNORDERED = "DAV:unordered";
    public static final String HEADER_ORDERING_TYPE = "Ordering-Type";
    public static final String HEADER_POSITION = "Position";
    public static final String XML_ORDERPATCH = "orderpatch";
    public static final String XML_ORDERING_TYPE = "ordering-type";
    public static final String XML_ORDER_MEMBER = "order-member";
    public static final String XML_POSITION = "position";
    public static final String XML_SEGMENT = "segment";
    public static final String XML_FIRST = "first";
    public static final String XML_LAST = "last";
    public static final String XML_BEFORE = "before";
    public static final String XML_AFTER = "after";
    public static final DavPropertyName ORDERING_TYPE = DavPropertyName.create("ordering-type", DavConstants.NAMESPACE);
    public static final DavPropertyName SUPPORTED_METHOD_SET = DavPropertyName.create("supported-method-set", DavConstants.NAMESPACE);
    public static final DavPropertyName SUPPORTED_LIVE_PROPERTY_SET = DavPropertyName.create("supported-live-property-set", DavConstants.NAMESPACE);
}

