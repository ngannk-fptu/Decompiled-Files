/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;

public interface VersionResource
extends DeltaVResource {
    public static final String METHODS = "LABEL";
    public static final DavPropertyName LABEL_NAME_SET = DavPropertyName.create("label-name-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName PREDECESSOR_SET = DavPropertyName.create("predecessor-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName SUCCESSOR_SET = DavPropertyName.create("successor-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKOUT_SET = DavPropertyName.create("checkout-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName VERSION_NAME = DavPropertyName.create("version-name", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName VERSION_HISTORY = DavPropertyName.create("version-history", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKOUT_FORK = DavPropertyName.create("checkout-fork", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKIN_FORK = DavPropertyName.create("checkin-fork", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName ACTIVITY_SET = DavPropertyName.create("activity-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName VERSION_CONTROLLED_BINDING_SET = DavPropertyName.create("version-controlled-binding-set", DeltaVConstants.NAMESPACE);

    public void label(LabelInfo var1) throws DavException;

    public VersionHistoryResource getVersionHistory() throws DavException;
}

