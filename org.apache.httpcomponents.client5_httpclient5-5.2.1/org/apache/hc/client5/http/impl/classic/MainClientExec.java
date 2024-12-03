/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.message.RequestLine
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.impl.ConnectionShutdownException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public final class MainClientExec
implements ExecChainHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MainClientExec.class);
    private final HttpClientConnectionManager connectionManager;
    private final HttpProcessor httpProcessor;
    private final ConnectionReuseStrategy reuseStrategy;
    private final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final UserTokenHandler userTokenHandler;

    public MainClientExec(HttpClientConnectionManager connectionManager, HttpProcessor httpProcessor, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, UserTokenHandler userTokenHandler) {
        this.connectionManager = (HttpClientConnectionManager)Args.notNull((Object)connectionManager, (String)"Connection manager");
        this.httpProcessor = (HttpProcessor)Args.notNull((Object)httpProcessor, (String)"HTTP protocol processor");
        this.reuseStrategy = (ConnectionReuseStrategy)Args.notNull((Object)reuseStrategy, (String)"Connection reuse strategy");
        this.keepAliveStrategy = (ConnectionKeepAliveStrategy)Args.notNull((Object)keepAliveStrategy, (String)"Connection keep alive strategy");
        this.userTokenHandler = (UserTokenHandler)Args.notNull((Object)userTokenHandler, (String)"User token handler");
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)scope, (String)"Scope");
        String exchangeId = scope.exchangeId;
        HttpRoute route = scope.route;
        HttpClientContext context = scope.clientContext;
        ExecRuntime execRuntime = scope.execRuntime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} executing {}", (Object)exchangeId, (Object)new RequestLine((HttpRequest)request));
        }
        try {
            context.setAttribute("http.route", route);
            context.setAttribute("http.request", request);
            this.httpProcessor.process((HttpRequest)request, (EntityDetails)request.getEntity(), (HttpContext)context);
            ClassicHttpResponse response = execRuntime.execute(exchangeId, request, context);
            context.setAttribute("http.response", response);
            this.httpProcessor.process((HttpResponse)response, (EntityDetails)response.getEntity(), (HttpContext)context);
            Object userToken = context.getUserToken();
            if (userToken == null) {
                userToken = this.userTokenHandler.getUserToken(route, (HttpRequest)request, (HttpContext)context);
                context.setAttribute("http.user-token", userToken);
            }
            if (this.reuseStrategy.keepAlive((HttpRequest)request, (HttpResponse)response, (HttpContext)context)) {
                TimeValue duration = this.keepAliveStrategy.getKeepAliveDuration((HttpResponse)response, (HttpContext)context);
                if (LOG.isDebugEnabled()) {
                    String s = duration != null ? "for " + duration : "indefinitely";
                    LOG.debug("{} connection can be kept alive {}", (Object)exchangeId, (Object)s);
                }
                execRuntime.markConnectionReusable(userToken, duration);
            } else {
                execRuntime.markConnectionNonReusable();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null || !entity.isStreaming()) {
                execRuntime.releaseEndpoint();
                return new CloseableHttpResponse(response, null);
            }
            return new CloseableHttpResponse(response, execRuntime);
        }
        catch (ConnectionShutdownException ex) {
            InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex);
            execRuntime.discardEndpoint();
            throw ioex;
        }
        catch (IOException | RuntimeException | HttpException ex) {
            execRuntime.discardEndpoint();
            throw ex;
        }
        catch (Error error) {
            this.connectionManager.close(CloseMode.IMMEDIATE);
            throw error;
        }
    }
}

