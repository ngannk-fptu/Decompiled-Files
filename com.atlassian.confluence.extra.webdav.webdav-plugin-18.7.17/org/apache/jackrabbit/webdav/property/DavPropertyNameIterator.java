/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import java.util.Iterator;
import org.apache.jackrabbit.webdav.property.DavPropertyName;

public interface DavPropertyNameIterator
extends Iterator<DavPropertyName> {
    public DavPropertyName nextPropertyName();
}

