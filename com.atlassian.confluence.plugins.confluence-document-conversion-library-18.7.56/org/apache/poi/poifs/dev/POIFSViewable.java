/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.dev;

import java.util.Iterator;

public interface POIFSViewable {
    public Object[] getViewableArray();

    public Iterator<Object> getViewableIterator();

    public boolean preferArray();

    public String getShortDescription();
}

