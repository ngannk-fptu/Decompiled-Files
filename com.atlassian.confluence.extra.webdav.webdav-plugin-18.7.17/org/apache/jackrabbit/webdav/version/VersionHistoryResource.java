/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.VersionResource;

public interface VersionHistoryResource
extends DeltaVResource {
    public static final String METHODS = "";
    public static final DavPropertyName ROOT_VERSION = DavPropertyName.create("root-version", DeltaVConstants.NAMESPACE);
    public static final DavPropertyName VERSION_SET = DavPropertyName.create("version-set", DeltaVConstants.NAMESPACE);

    public VersionResource[] getVersions() throws DavException;
}

