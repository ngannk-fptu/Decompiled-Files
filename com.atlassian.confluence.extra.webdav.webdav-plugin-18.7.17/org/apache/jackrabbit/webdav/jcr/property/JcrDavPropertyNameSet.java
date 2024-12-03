/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.property;

import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JcrDavPropertyNameSet
implements ItemResourceConstants {
    private static final Logger log = LoggerFactory.getLogger(JcrDavPropertyNameSet.class);
    public static final DavPropertyNameSet BASE_SET = new DavPropertyNameSet();
    public static final DavPropertyNameSet WORKSPACE_SET;
    public static final DavPropertyNameSet ITEM_BASE_SET;
    public static final DavPropertyNameSet EXISTING_ITEM_BASE_SET;
    public static final DavPropertyNameSet PROPERTY_SET;
    public static final DavPropertyNameSet PROPERTY_MV_SET;
    public static final DavPropertyNameSet NODE_SET;
    public static final DavPropertyNameSet VERSIONABLE_SET;
    public static final DavPropertyNameSet VERSION_SET;
    public static final DavPropertyNameSet VERSIONHISTORY_SET;

    static {
        BASE_SET.add(DavPropertyName.DISPLAYNAME);
        BASE_SET.add(DavPropertyName.RESOURCETYPE);
        BASE_SET.add(DavPropertyName.ISCOLLECTION);
        BASE_SET.add(DavPropertyName.GETLASTMODIFIED);
        BASE_SET.add(DavPropertyName.CREATIONDATE);
        BASE_SET.add(DavPropertyName.SUPPORTEDLOCK);
        BASE_SET.add(DavPropertyName.LOCKDISCOVERY);
        BASE_SET.add(DeltaVConstants.SUPPORTED_METHOD_SET);
        BASE_SET.add(DeltaVConstants.SUPPORTED_REPORT_SET);
        BASE_SET.add(DeltaVConstants.CREATOR_DISPLAYNAME);
        BASE_SET.add(DeltaVConstants.COMMENT);
        BASE_SET.add(JCR_WORKSPACE_NAME);
        WORKSPACE_SET = new DavPropertyNameSet();
        WORKSPACE_SET.add(DeltaVConstants.WORKSPACE);
        WORKSPACE_SET.add(JCR_NAMESPACES);
        WORKSPACE_SET.add(JCR_NODETYPES_CND);
        ITEM_BASE_SET = new DavPropertyNameSet();
        ITEM_BASE_SET.add(DavPropertyName.GETCONTENTTYPE);
        ITEM_BASE_SET.add(DeltaVConstants.WORKSPACE);
        ITEM_BASE_SET.add(ObservationConstants.SUBSCRIPTIONDISCOVERY);
        EXISTING_ITEM_BASE_SET = new DavPropertyNameSet(ITEM_BASE_SET);
        EXISTING_ITEM_BASE_SET.add(JCR_NAME);
        EXISTING_ITEM_BASE_SET.add(JCR_PATH);
        EXISTING_ITEM_BASE_SET.add(JCR_DEPTH);
        EXISTING_ITEM_BASE_SET.add(JCR_DEFINITION);
        PROPERTY_SET = new DavPropertyNameSet();
        PROPERTY_SET.add(JCR_TYPE);
        PROPERTY_SET.add(JCR_VALUE);
        PROPERTY_SET.add(JCR_LENGTH);
        PROPERTY_MV_SET = new DavPropertyNameSet();
        PROPERTY_MV_SET.add(JCR_TYPE);
        PROPERTY_MV_SET.add(JCR_VALUES);
        PROPERTY_MV_SET.add(JCR_LENGTHS);
        NODE_SET = new DavPropertyNameSet();
        NODE_SET.add(JCR_PRIMARYNODETYPE);
        NODE_SET.add(JCR_MIXINNODETYPES);
        NODE_SET.add(JCR_INDEX);
        NODE_SET.add(JCR_REFERENCES);
        NODE_SET.add(JCR_WEAK_REFERENCES);
        VERSIONABLE_SET = new DavPropertyNameSet();
        VERSIONABLE_SET.add(VersionControlledResource.VERSION_HISTORY);
        VERSIONABLE_SET.add(VersionControlledResource.AUTO_VERSION);
        VERSION_SET = new DavPropertyNameSet();
        VERSION_SET.add(VersionResource.VERSION_NAME);
        VERSION_SET.add(VersionResource.LABEL_NAME_SET);
        VERSION_SET.add(VersionResource.PREDECESSOR_SET);
        VERSION_SET.add(VersionResource.SUCCESSOR_SET);
        VERSION_SET.add(VersionResource.VERSION_HISTORY);
        VERSION_SET.add(VersionResource.CHECKOUT_SET);
        VERSIONHISTORY_SET = new DavPropertyNameSet();
        VERSIONHISTORY_SET.add(VersionHistoryResource.ROOT_VERSION);
        VERSIONHISTORY_SET.add(VersionHistoryResource.VERSION_SET);
        VERSIONHISTORY_SET.add(JCR_VERSIONABLEUUID);
    }
}

