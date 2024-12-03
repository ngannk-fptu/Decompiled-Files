/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionableResource;

public interface VersionControlledResource
extends VersionableResource {
    public static final String methods_checkedIn = "CHECKOUT, UPDATE, MERGE, LABEL";
    public static final String methods_checkedOut = "CHECKIN, MERGE";
    public static final DavPropertyName AUTO_VERSION = DavPropertyName.create("auto-version", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName VERSION_HISTORY = DavPropertyName.create("version-history", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKED_IN = DavPropertyName.create("checked-in", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKED_OUT = DavPropertyName.create("checked-out", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName PREDECESSOR_SET = DavPropertyName.create("predecessor-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKIN_FORK = DavPropertyName.create("checkin-fork", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CHECKOUT_FORK = DavPropertyName.create("checkout-fork", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName MERGE_SET = DavPropertyName.create("merge-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName AUTO_MERGE_SET = DavPropertyName.create("auto-merge-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName UNRESERVED = DavPropertyName.create("activity-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName ACTIVITY_SET = DavPropertyName.create("activity-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName ECLIPSED_SET = DavPropertyName.create("eclipsed-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName BASELINE_CONTROLLED_COLLECTION = DavPropertyName.create("baseline-controlled-collection", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName SUBBASELINE_SET = DavPropertyName.create("subbaseline-set", DeltaVConstants.NAMESPACE);

    public String checkin() throws DavException;

    public void checkout() throws DavException;

    public void uncheckout() throws DavException;

    public MultiStatus update(UpdateInfo var1) throws DavException;

    public MultiStatus merge(MergeInfo var1) throws DavException;

    public void label(LabelInfo var1) throws DavException;

    public VersionHistoryResource getVersionHistory() throws DavException;
}

