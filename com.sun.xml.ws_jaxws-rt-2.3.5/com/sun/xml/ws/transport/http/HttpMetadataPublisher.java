/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.transport.http;

import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import java.io.IOException;

public abstract class HttpMetadataPublisher {
    public abstract boolean handleMetadataRequest(@NotNull HttpAdapter var1, @NotNull WSHTTPConnection var2) throws IOException;
}

