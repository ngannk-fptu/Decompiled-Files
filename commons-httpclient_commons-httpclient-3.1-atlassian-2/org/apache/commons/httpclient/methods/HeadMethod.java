/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import java.io.IOException;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HeadMethod
extends HttpMethodBase {
    private static final Log LOG = LogFactory.getLog(HeadMethod.class);

    public HeadMethod() {
        this.setFollowRedirects(true);
    }

    public HeadMethod(String uri) {
        super(uri);
        this.setFollowRedirects(true);
    }

    @Override
    public String getName() {
        return "HEAD";
    }

    @Override
    public void recycle() {
        super.recycle();
        this.setFollowRedirects(true);
    }

    @Override
    protected void readResponseBody(HttpState state, HttpConnection conn) throws HttpException, IOException {
        LOG.trace((Object)"enter HeadMethod.readResponseBody(HttpState, HttpConnection)");
        int bodyCheckTimeout = this.getParams().getIntParameter("http.protocol.head-body-timeout", -1);
        if (bodyCheckTimeout < 0) {
            this.responseBodyConsumed();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Check for non-compliant response body. Timeout in " + bodyCheckTimeout + " ms"));
            }
            boolean responseAvailable = false;
            try {
                responseAvailable = conn.isResponseAvailable(bodyCheckTimeout);
            }
            catch (IOException e) {
                LOG.debug((Object)"An IOException occurred while testing if a response was available, we will assume one is not.", (Throwable)e);
                responseAvailable = false;
            }
            if (responseAvailable) {
                if (this.getParams().isParameterTrue("http.protocol.reject-head-body")) {
                    throw new ProtocolException("Body content may not be sent in response to HTTP HEAD request");
                }
                LOG.warn((Object)"Body content returned in response to HTTP HEAD");
                super.readResponseBody(state, conn);
            }
        }
    }

    public int getBodyCheckTimeout() {
        return this.getParams().getIntParameter("http.protocol.head-body-timeout", -1);
    }

    public void setBodyCheckTimeout(int timeout) {
        this.getParams().setIntParameter("http.protocol.head-body-timeout", timeout);
    }
}

