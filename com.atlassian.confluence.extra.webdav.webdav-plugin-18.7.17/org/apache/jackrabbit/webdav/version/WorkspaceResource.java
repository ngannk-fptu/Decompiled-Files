/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;

public interface WorkspaceResource
extends DeltaVResource {
    public static final DavPropertyName WORKSPACE_CHECKOUT_SET = DavPropertyName.create("workspace-checkout-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CUURENT_ACTIVITY_SET = DavPropertyName.create("current-activity-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CURRENT_ACTIVITY_SET = DavPropertyName.create("current-activity-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName BASELINE_CONTROLLED_COLLECTION_SET = DavPropertyName.create("baseline-controlled-collection-set", DeltaVConstants.NAMESPACE);
}

