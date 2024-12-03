/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
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
public class ServiceUnavailableRetryExec
implements ClientExecChain {
    private final Log log = LogFactory.getLog(this.getClass());
    private final ClientExecChain requestExecutor;
    private final ServiceUnavailableRetryStrategy retryStrategy;

    public ServiceUnavailableRetryExec(ClientExecChain requestExecutor, ServiceUnavailableRetryStrategy retryStrategy) {
        Args.notNull((Object)requestExecutor, (String)"HTTP request executor");
        Args.notNull((Object)retryStrategy, (String)"Retry strategy");
        this.requestExecutor = requestExecutor;
        this.retryStrategy = retryStrategy;
    }

    @Override
    public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
        Header[] origheaders = request.getAllHeaders();
        int c = 1;
        while (true) {
            CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
            try {
                if (this.retryStrategy.retryRequest(response, c, (HttpContext)context) && RequestEntityProxy.isRepeatable(request)) {
                    response.close();
                    long nextInterval = this.retryStrategy.getRetryInterval();
                    if (nextInterval > 0L) {
                        try {
                            this.log.trace((Object)("Wait for " + nextInterval));
                            Thread.sleep(nextInterval);
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new InterruptedIOException();
                        }
                    }
                } else {
                    return response;
                }
                request.setHeaders(origheaders);
            }
            catch (RuntimeException ex) {
                response.close();
                throw ex;
            }
            ++c;
        }
    }
}

