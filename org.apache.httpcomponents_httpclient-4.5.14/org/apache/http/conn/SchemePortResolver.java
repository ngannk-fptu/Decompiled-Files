/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 */
package org.apache.http.conn;

import org.apache.http.HttpHost;
import org.apache.http.conn.UnsupportedSchemeException;

public interface SchemePortResolver {
    public int resolve(HttpHost var1) throws UnsupportedSchemeException;
}

