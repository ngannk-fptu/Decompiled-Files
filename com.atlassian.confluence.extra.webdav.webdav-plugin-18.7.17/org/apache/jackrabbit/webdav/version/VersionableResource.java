/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.version.DeltaVResource;

public interface VersionableResource
extends DeltaVResource {
    public static final String METHODS = "VERSION-CONTROL";

    public void addVersionControl() throws DavException;
}

