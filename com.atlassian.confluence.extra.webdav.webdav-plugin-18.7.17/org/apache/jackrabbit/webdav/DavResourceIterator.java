/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.util.Iterator;
import org.apache.jackrabbit.webdav.DavResource;

public interface DavResourceIterator
extends Iterator<DavResource> {
    public DavResource nextResource();

    public int size();
}

