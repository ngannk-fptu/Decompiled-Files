/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionResource;

public interface BaselineResource
extends VersionResource {
    public static final DavPropertyName BASELINE_COLLECTION = DavPropertyName.create("baseline-collection", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName SUBBASELINE_SET = DavPropertyName.create("subbaseline-set", DeltaVConstants.NAMESPACE);

    public DavResource getBaselineCollection() throws DavException;
}

