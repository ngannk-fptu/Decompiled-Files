/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.Header;

public interface CookieVersionSupport {
    public int getVersion();

    public Header getVersionHeader();
}

