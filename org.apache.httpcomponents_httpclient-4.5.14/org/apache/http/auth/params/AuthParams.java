/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HTTP
 *  org.apache.http.util.Args
 */
package org.apache.http.auth.params;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class AuthParams {
    private AuthParams() {
    }

    public static String getCredentialCharset(HttpParams params) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        String charset = (String)params.getParameter("http.auth.credential-charset");
        if (charset == null) {
            charset = HTTP.DEF_PROTOCOL_CHARSET.name();
        }
        return charset;
    }

    public static void setCredentialCharset(HttpParams params, String charset) {
        Args.notNull((Object)params, (String)"HTTP parameters");
        params.setParameter("http.auth.credential-charset", (Object)charset);
    }
}

