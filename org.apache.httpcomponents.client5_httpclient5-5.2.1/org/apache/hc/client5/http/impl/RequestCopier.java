/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.message.BasicHttpRequest
 */
package org.apache.hc.client5.http.impl;

import java.util.Iterator;
import org.apache.hc.client5.http.impl.MessageCopier;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.message.BasicHttpRequest;

@Deprecated
public final class RequestCopier
implements MessageCopier<HttpRequest> {
    public static final RequestCopier INSTANCE = new RequestCopier();

    @Override
    public HttpRequest copy(HttpRequest original) {
        if (original == null) {
            return null;
        }
        BasicHttpRequest copy = new BasicHttpRequest(original.getMethod(), null, original.getPath());
        copy.setScheme(original.getScheme());
        copy.setAuthority(original.getAuthority());
        copy.setVersion(original.getVersion());
        Iterator it = original.headerIterator();
        while (it.hasNext()) {
            copy.addHeader((Header)it.next());
        }
        return copy;
    }
}

