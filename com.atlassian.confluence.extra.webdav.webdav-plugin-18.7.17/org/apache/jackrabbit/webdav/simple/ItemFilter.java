/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.simple;

import javax.jcr.Item;
import javax.jcr.Session;

public interface ItemFilter {
    public void setFilteredURIs(String[] var1);

    public void setFilteredPrefixes(String[] var1);

    public void setFilteredNodetypes(String[] var1);

    public boolean isFilteredItem(Item var1);

    public boolean isFilteredItem(String var1, Session var2);
}

