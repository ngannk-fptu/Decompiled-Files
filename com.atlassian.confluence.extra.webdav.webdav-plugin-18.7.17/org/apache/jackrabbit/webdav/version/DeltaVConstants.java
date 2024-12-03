/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface DeltaVConstants {
    public static final Namespace NAMESPACE = DavConstants.NAMESPACE;
    public static final String HEADER_LABEL = "Label";
    public static final String HEADER_LOCATION = "Location";
    public static final DavPropertyName COMMENT = DavPropertyName.create("comment", NAMESPACE);
    public static final DavPropertyName CREATOR_DISPLAYNAME = DavPropertyName.create("creator-displayname", NAMESPACE);
    public static final DavPropertyName SUPPORTED_METHOD_SET = DavPropertyName.create("supported-method-set", NAMESPACE);
    public static final DavPropertyName SUPPORTED_LIVE_PROPERTY_SET = DavPropertyName.create("supported-live-property-set", NAMESPACE);
    public static final DavPropertyName SUPPORTED_REPORT_SET = DavPropertyName.create("supported-report-set", NAMESPACE);
    public static final DavPropertyName WORKSPACE = DavPropertyName.create("workspace", NAMESPACE);
    public static final DavPropertyName VERSION_CONTROLLED_CONFIGURATION = DavPropertyName.create("version-controlled-configuration", NAMESPACE);
    public static final String XML_ACTIVITY = "activity";
    public static final String XML_BASELINE = "baseline";
    public static final String XML_SUPPORTED_METHOD = "supported-method";
    public static final String XML_VERSION_HISTORY = "version-history";
    public static final String XML_VERSION = "version";
    public static final String XML_WORKSPACE = "workspace";
    public static final String XML_OPTIONS = "options";
    public static final String XML_OPTIONS_RESPONSE = "options-response";
    public static final String XML_VH_COLLECTION_SET = "version-history-collection-set";
    public static final String XML_WSP_COLLECTION_SET = "workspace-collection-set";
    public static final String XML_ACTIVITY_COLLECTION_SET = "activity-collection-set";
    public static final String XML_SUPPORTED_REPORT = "supported-report";
    public static final String XML_REPORT = "report";
    public static final String XML_VERSION_TREE = "version-tree";
    public static final String XML_EXPAND_PROPERTY = "expand-property";
    public static final String XML_PROPERTY = "property";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_NAMESPACE = "namespace";
    public static final String XML_LOCATE_BY_HISTORY = "locate-by-history";
    public static final String XML_VERSION_HISTORY_SET = "version-history-set";
    public static final String XML_LABEL = "label";
    public static final String XML_LABEL_NAME = "label-name";
    public static final String XML_LABEL_ADD = "add";
    public static final String XML_LABEL_REMOVE = "remove";
    public static final String XML_LABEL_SET = "set";
    public static final String XML_UPDATE = "update";
    public static final String XML_CHECKOUT_CHECKIN = "checkout-checkin";
    public static final String XML_CHECKOUT_UNLOCK_CHECKIN = "checkout-unlocked-checkin";
    public static final String XML_CHECKOUT = "checkout";
    public static final String XML_LOCKED_CHECKIN = "locked-checkout";
    public static final String XML_MERGE = "merge";
    public static final String XML_N0_AUTO_MERGE = "no-auto-merge";
    public static final String XML_N0_CHECKOUT = "no-checkout";
}

