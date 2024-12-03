/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods;

import org.apache.commons.httpclient.HttpMethodBase;

public class TraceMethod
extends HttpMethodBase {
    public TraceMethod(String uri) {
        super(uri);
        this.setFollowRedirects(false);
    }

    @Override
    public String getName() {
        return "TRACE";
    }

    @Override
    public void recycle() {
        super.recycle();
        this.setFollowRedirects(false);
    }
}

