/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.jackrabbit.webdav.property.DavProperty;

public interface DavPropertyIterator
extends Iterator<DavProperty<?>> {
    public DavProperty<?> nextProperty() throws NoSuchElementException;
}

