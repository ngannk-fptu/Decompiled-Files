/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetMethod
extends HttpMethodBase {
    private static final Log LOG = LogFactory.getLog(GetMethod.class);

    public GetMethod() {
        this.setFollowRedirects(true);
    }

    public GetMethod(String uri) {
        super(uri);
        LOG.trace((Object)"enter GetMethod(String)");
        this.setFollowRedirects(true);
    }

    @Override
    public String getName() {
        return "GET";
    }

    @Override
    public void recycle() {
        LOG.trace((Object)"enter GetMethod.recycle()");
        super.recycle();
        this.setFollowRedirects(true);
    }
}

