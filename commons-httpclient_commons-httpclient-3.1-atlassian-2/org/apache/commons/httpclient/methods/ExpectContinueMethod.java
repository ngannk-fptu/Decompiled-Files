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
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ExpectContinueMethod
extends HttpMethodBase {
    private static final Log LOG = LogFactory.getLog(ExpectContinueMethod.class);

    public ExpectContinueMethod() {
    }

    public ExpectContinueMethod(String uri) {
        super(uri);
    }

    public boolean getUseExpectHeader() {
        return this.getParams().getBooleanParameter("http.protocol.expect-continue", false);
    }

    public void setUseExpectHeader(boolean value) {
        this.getParams().setBooleanParameter("http.protocol.expect-continue", value);
    }

    protected abstract boolean hasRequestContent();

    @Override
    protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        boolean headerPresent;
        LOG.trace((Object)"enter ExpectContinueMethod.addRequestHeaders(HttpState, HttpConnection)");
        super.addRequestHeaders(state, conn);
        boolean bl = headerPresent = this.getRequestHeader("Expect") != null;
        if (this.getParams().isParameterTrue("http.protocol.expect-continue") && this.getEffectiveVersion().greaterEquals(HttpVersion.HTTP_1_1) && this.hasRequestContent()) {
            if (!headerPresent) {
                this.setRequestHeader("Expect", "100-continue");
            }
        } else if (headerPresent) {
            this.removeRequestHeader("Expect");
        }
    }
}

