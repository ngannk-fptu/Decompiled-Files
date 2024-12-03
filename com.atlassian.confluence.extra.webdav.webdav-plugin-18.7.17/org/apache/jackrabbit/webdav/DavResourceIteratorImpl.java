/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DavResourceIteratorImpl
implements DavResourceIterator {
    private static Logger log = LoggerFactory.getLogger(DavResourceIteratorImpl.class);
    public static final DavResourceIterator EMPTY = new DavResourceIteratorImpl(Collections.emptyList());
    private Iterator<DavResource> it;
    private int size;

    public DavResourceIteratorImpl(List<DavResource> list) {
        this.it = list.iterator();
        this.size = list.size();
    }

    @Override
    public boolean hasNext() {
        return this.it.hasNext();
    }

    @Override
    public DavResource next() {
        return this.it.next();
    }

    @Override
    public DavResource nextResource() {
        return this.next();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not allowed with DavResourceIteratorImpl");
    }
}

