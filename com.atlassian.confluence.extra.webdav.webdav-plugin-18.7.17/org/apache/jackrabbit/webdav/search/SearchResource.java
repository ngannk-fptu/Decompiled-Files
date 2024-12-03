/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.search;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.search.QueryGrammerSet;
import org.apache.jackrabbit.webdav.search.SearchInfo;

public interface SearchResource {
    public static final String METHODS = "SEARCH";

    public QueryGrammerSet getQueryGrammerSet();

    public MultiStatus search(SearchInfo var1) throws DavException;
}

