/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpException
 *  org.apache.http.NoHttpResponseException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.execchain;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.RequestEntityProxy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RetryExec
implements ClientExecChain {
    private final Log log = LogFactory.getLog(this.getClass());
    private final ClientExecChain requestExecutor;
    private final HttpRequestRetryHandler retryHandler;

    public RetryExec(ClientExecChain requestExecutor, HttpRequestRetryHandler retryHandler) {
        Args.notNull((Object)requestExecutor, (String)"HTTP request executor");
        Args.notNull((Object)retryHandler, (String)"HTTP request retry handler");
        this.requestExecutor = requestExecutor;
        this.retryHandler = retryHandler;
    }

    @Override
    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull((Object)route, (String)"HTTP route");
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)((Object)context), (String)"HTTP context");
        Header[] origheaders = request.getAllHeaders();
        int execCount = 1;
        while (true) {
            try {
                return this.requestExecutor.execute(route, request, context, execAware);
            }
            catch (IOException ex) {
                if (execAware != null && execAware.isAborted()) {
                    this.log.debug((Object)"Request has been aborted");
                    throw ex;
                }
                if (this.retryHandler.retryRequest(ex, execCount, (HttpContext)context)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info((Object)("I/O exception (" + ex.getClass().getName() + ") caught when processing request to " + route + ": " + ex.getMessage()));
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)ex.getMessage(), (Throwable)ex);
                    }
                    if (!RequestEntityProxy.isRepeatable(request)) {
                        this.log.debug((Object)"Cannot retry non-repeatable request");
                        throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity", ex);
                    }
                    request.setHeaders(origheaders);
                    if (this.log.isInfoEnabled()) {
                        this.log.info((Object)("Retrying request to " + route));
                    }
                } else {
                    if (ex instanceof NoHttpResponseException) {
                        NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
                        updatedex.setStackTrace(ex.getStackTrace());
                        throw updatedex;
                    }
                    throw ex;
                }
                ++execCount;
                continue;
            }
            break;
        }
    }
}

