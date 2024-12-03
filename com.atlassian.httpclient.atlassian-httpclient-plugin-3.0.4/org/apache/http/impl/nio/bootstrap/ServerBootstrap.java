/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.bootstrap;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpServerConnectionFactory;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.protocol.HttpAsyncExpectationVerifier;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerMapper;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

public class ServerBootstrap {
    private int listenerPort;
    private InetAddress localAddress;
    private IOReactorConfig ioReactorConfig;
    private ConnectionConfig connectionConfig;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private String serverInfo;
    private HttpProcessor httpProcessor;
    private ConnectionReuseStrategy connStrategy;
    private HttpResponseFactory responseFactory;
    private HttpAsyncRequestHandlerMapper handlerMapper;
    private Map<String, HttpAsyncRequestHandler<?>> handlerMap;
    private HttpAsyncExpectationVerifier expectationVerifier;
    private SSLContext sslContext;
    private SSLSetupHandler sslSetupHandler;
    private NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connectionFactory;
    private ExceptionLogger exceptionLogger;

    private ServerBootstrap() {
    }

    public static ServerBootstrap bootstrap() {
        return new ServerBootstrap();
    }

    public final ServerBootstrap setListenerPort(int listenerPort) {
        this.listenerPort = listenerPort;
        return this;
    }

    public final ServerBootstrap setLocalAddress(InetAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    public final ServerBootstrap setIOReactorConfig(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
        return this;
    }

    public final ServerBootstrap setConnectionConfig(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
        return this;
    }

    public final ServerBootstrap setHttpProcessor(HttpProcessor httpProcessor) {
        this.httpProcessor = httpProcessor;
        return this;
    }

    public final ServerBootstrap addInterceptorFirst(HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.responseFirst == null) {
            this.responseFirst = new LinkedList();
        }
        this.responseFirst.addFirst(itcp);
        return this;
    }

    public final ServerBootstrap addInterceptorLast(HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.responseLast == null) {
            this.responseLast = new LinkedList();
        }
        this.responseLast.addLast(itcp);
        return this;
    }

    public final ServerBootstrap addInterceptorFirst(HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.requestFirst == null) {
            this.requestFirst = new LinkedList();
        }
        this.requestFirst.addFirst(itcp);
        return this;
    }

    public final ServerBootstrap addInterceptorLast(HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.requestLast == null) {
            this.requestLast = new LinkedList();
        }
        this.requestLast.addLast(itcp);
        return this;
    }

    public final ServerBootstrap setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }

    public final ServerBootstrap setConnectionReuseStrategy(ConnectionReuseStrategy connStrategy) {
        this.connStrategy = connStrategy;
        return this;
    }

    public final ServerBootstrap setResponseFactory(HttpResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
        return this;
    }

    public final ServerBootstrap setHandlerMapper(HttpAsyncRequestHandlerMapper handlerMapper) {
        this.handlerMapper = handlerMapper;
        return this;
    }

    public final ServerBootstrap registerHandler(String pattern, HttpAsyncRequestHandler<?> handler) {
        if (pattern == null || handler == null) {
            return this;
        }
        if (this.handlerMap == null) {
            this.handlerMap = new HashMap();
        }
        this.handlerMap.put(pattern, handler);
        return this;
    }

    public final ServerBootstrap setExpectationVerifier(HttpAsyncExpectationVerifier expectationVerifier) {
        this.expectationVerifier = expectationVerifier;
        return this;
    }

    public final ServerBootstrap setConnectionFactory(NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    public final ServerBootstrap setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public ServerBootstrap setSslSetupHandler(SSLSetupHandler sslSetupHandler) {
        this.sslSetupHandler = sslSetupHandler;
        return this;
    }

    public final ServerBootstrap setExceptionLogger(ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
        return this;
    }

    /*
     * WARNING - void declaration
     */
    public HttpServer create() {
        void var5_24;
        void var4_15;
        ExceptionLogger exceptionLoggerCopy;
        NHttpConnectionFactory<? extends DefaultNHttpServerConnection> nHttpConnectionFactory;
        HttpResponseFactory httpResponseFactory;
        ConnectionReuseStrategy connStrategyCopy;
        HttpAsyncRequestHandlerMapper handlerMapperCopy;
        HttpProcessor httpProcessorCopy = this.httpProcessor;
        if (httpProcessorCopy == null) {
            String serverInfoCopy;
            HttpProcessorBuilder b = HttpProcessorBuilder.create();
            if (this.requestFirst != null) {
                for (HttpRequestInterceptor httpRequestInterceptor : this.requestFirst) {
                    b.addFirst(httpRequestInterceptor);
                }
            }
            if (this.responseFirst != null) {
                for (HttpResponseInterceptor httpResponseInterceptor : this.responseFirst) {
                    b.addFirst(httpResponseInterceptor);
                }
            }
            if ((serverInfoCopy = this.serverInfo) == null) {
                serverInfoCopy = "Apache-HttpCore-NIO/1.1";
            }
            b.addAll(new ResponseDate(), new ResponseServer(serverInfoCopy), new ResponseContent(), new ResponseConnControl());
            if (this.requestLast != null) {
                for (HttpRequestInterceptor httpRequestInterceptor : this.requestLast) {
                    b.addLast(httpRequestInterceptor);
                }
            }
            if (this.responseLast != null) {
                for (HttpResponseInterceptor httpResponseInterceptor : this.responseLast) {
                    b.addLast(httpResponseInterceptor);
                }
            }
            httpProcessorCopy = b.build();
        }
        if ((handlerMapperCopy = this.handlerMapper) == null) {
            UriHttpAsyncRequestHandlerMapper reqistry = new UriHttpAsyncRequestHandlerMapper();
            if (this.handlerMap != null) {
                for (Map.Entry<String, HttpAsyncRequestHandler<?>> entry : this.handlerMap.entrySet()) {
                    reqistry.register(entry.getKey(), entry.getValue());
                }
            }
            handlerMapperCopy = reqistry;
        }
        if ((connStrategyCopy = this.connStrategy) == null) {
            connStrategyCopy = DefaultConnectionReuseStrategy.INSTANCE;
        }
        if ((httpResponseFactory = this.responseFactory) == null) {
            DefaultHttpResponseFactory defaultHttpResponseFactory = DefaultHttpResponseFactory.INSTANCE;
        }
        if ((nHttpConnectionFactory = this.connectionFactory) == null) {
            if (this.sslContext != null) {
                SSLNHttpServerConnectionFactory sSLNHttpServerConnectionFactory = new SSLNHttpServerConnectionFactory(this.sslContext, this.sslSetupHandler, this.connectionConfig);
            } else {
                DefaultNHttpServerConnectionFactory defaultNHttpServerConnectionFactory = new DefaultNHttpServerConnectionFactory(this.connectionConfig);
            }
        }
        if ((exceptionLoggerCopy = this.exceptionLogger) == null) {
            exceptionLoggerCopy = ExceptionLogger.NO_OP;
        }
        HttpAsyncService httpService = new HttpAsyncService(httpProcessorCopy, connStrategyCopy, (HttpResponseFactory)var4_15, handlerMapperCopy, this.expectationVerifier, exceptionLoggerCopy);
        return new HttpServer(this.listenerPort, this.localAddress, this.ioReactorConfig, httpService, (NHttpConnectionFactory<? extends DefaultNHttpServerConnection>)var5_24, exceptionLoggerCopy);
    }
}

