/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.params.HttpParams
 */
package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpParams;

@Deprecated
public interface SchemeLayeredSocketFactory
extends SchemeSocketFactory {
    public Socket createLayeredSocket(Socket var1, String var2, int var3, HttpParams var4) throws IOException, UnknownHostException;
}

