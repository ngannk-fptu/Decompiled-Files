/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.auth.AuthScope;

public class HttpAuthRealm
extends AuthScope {
    public HttpAuthRealm(String domain, String realm) {
        super(domain, -1, realm, ANY_SCHEME);
    }
}

