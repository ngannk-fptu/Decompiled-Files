/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;

public interface ActivityResource
extends DeltaVResource {
    public static final DavPropertyName ACTIVITY_VERSION_SET = DavPropertyName.create("activity-version-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName ACTIVITY_CHECKOUT_SET = DavPropertyName.create("activity-checkout-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName SUBACTIVITY_SET = DavPropertyName.create("subactivity-set", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName CURRENT_WORKSPACE_SET = DavPropertyName.create("current-workspace-set", DeltaVConstants.NAMESPACE);
}

