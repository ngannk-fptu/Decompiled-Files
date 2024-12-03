/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.JavaScriptUtils
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.DefaultSockJsFrameFormat;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.AbstractHttpSendingTransportHandler;
import org.springframework.web.socket.sockjs.transport.session.AbstractHttpSockJsSession;
import org.springframework.web.socket.sockjs.transport.session.StreamingSockJsSession;
import org.springframework.web.util.JavaScriptUtils;

public class HtmlFileTransportHandler
extends AbstractHttpSendingTransportHandler {
    private static final String PARTIAL_HTML_CONTENT;
    private static final int MINIMUM_PARTIAL_HTML_CONTENT_LENGTH = 1024;

    @Override
    public TransportType getTransportType() {
        return TransportType.HTML_FILE;
    }

    @Override
    protected MediaType getContentType() {
        return new MediaType("text", "html", StandardCharsets.UTF_8);
    }

    @Override
    public boolean checkSessionType(SockJsSession session) {
        return session instanceof HtmlFileStreamingSockJsSession;
    }

    @Override
    public StreamingSockJsSession createSession(String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {
        return new HtmlFileStreamingSockJsSession(sessionId, this.getServiceConfig(), handler, attributes);
    }

    @Override
    public void handleRequestInternal(ServerHttpRequest request, ServerHttpResponse response, AbstractHttpSockJsSession sockJsSession) throws SockJsException {
        String callback = this.getCallbackParam(request);
        if (!StringUtils.hasText((String)callback)) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            try {
                response.getBody().write("\"callback\" parameter required".getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException ex) {
                sockJsSession.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
                throw new SockJsTransportFailureException("Failed to write to response", sockJsSession.getId(), ex);
            }
            return;
        }
        super.handleRequestInternal(request, response, sockJsSession);
    }

    @Override
    protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
        return new DefaultSockJsFrameFormat("<script>\np(\"%s\");\n</script>\r\n"){

            @Override
            protected String preProcessContent(String content) {
                return JavaScriptUtils.javaScriptEscape((String)content);
            }
        };
    }

    static {
        StringBuilder sb = new StringBuilder("<!doctype html>\n<html><head>\n  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n</head><body><h2>Don't panic!</h2>\n  <script>\n    document.domain = document.domain;\n    var c = parent.%s;\n    c.start();\n    function p(d) {c.message(d);};\n    window.onload = function() {c.stop();};\n  </script>");
        while (sb.length() < 1024) {
            sb.append(' ');
        }
        PARTIAL_HTML_CONTENT = sb.toString();
    }

    private class HtmlFileStreamingSockJsSession
    extends StreamingSockJsSession {
        public HtmlFileStreamingSockJsSession(String sessionId, SockJsServiceConfig config, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            super(sessionId, config, wsHandler, attributes);
        }

        @Override
        protected byte[] getPrelude(ServerHttpRequest request) {
            String callback = HtmlFileTransportHandler.this.getCallbackParam(request);
            String html = String.format(PARTIAL_HTML_CONTENT, callback);
            return html.getBytes(StandardCharsets.UTF_8);
        }
    }
}

