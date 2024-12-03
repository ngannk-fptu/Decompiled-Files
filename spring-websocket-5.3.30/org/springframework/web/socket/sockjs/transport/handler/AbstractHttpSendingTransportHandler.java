/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.UriComponentsBuilder
 *  org.springframework.web.util.UriUtils
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.SockJsSessionFactory;
import org.springframework.web.socket.sockjs.transport.handler.AbstractTransportHandler;
import org.springframework.web.socket.sockjs.transport.session.AbstractHttpSockJsSession;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public abstract class AbstractHttpSendingTransportHandler
extends AbstractTransportHandler
implements SockJsSessionFactory {
    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_.]*");

    @Override
    public final void handleRequest(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, SockJsSession wsSession) throws SockJsException {
        AbstractHttpSockJsSession sockJsSession = (AbstractHttpSockJsSession)wsSession;
        response.getHeaders().setContentType(this.getContentType());
        this.handleRequestInternal(request, response, sockJsSession);
    }

    protected void handleRequestInternal(ServerHttpRequest request, ServerHttpResponse response, AbstractHttpSockJsSession sockJsSession) throws SockJsException {
        if (sockJsSession.isNew()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)(request.getMethod() + " " + request.getURI()));
            }
            sockJsSession.handleInitialRequest(request, response, this.getFrameFormat(request));
        } else if (sockJsSession.isClosed()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Connection already closed (but not removed yet) for " + sockJsSession));
            }
            this.writeFrame(SockJsFrame.closeFrameGoAway(), request, response, sockJsSession);
        } else if (!sockJsSession.isActive()) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Starting " + (Object)((Object)this.getTransportType()) + " async request."));
            }
            sockJsSession.handleSuccessiveRequest(request, response, this.getFrameFormat(request));
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Another " + (Object)((Object)this.getTransportType()) + " connection still open for " + sockJsSession));
            }
            this.writeFrame(SockJsFrame.closeFrameAnotherConnectionOpen(), request, response, sockJsSession);
        }
    }

    private void writeFrame(SockJsFrame frame, ServerHttpRequest request, ServerHttpResponse response, AbstractHttpSockJsSession sockJsSession) {
        String formattedFrame = this.getFrameFormat(request).format(frame);
        try {
            response.getBody().write(formattedFrame.getBytes(SockJsFrame.CHARSET));
        }
        catch (IOException ex) {
            throw new SockJsException("Failed to send " + formattedFrame, sockJsSession.getId(), ex);
        }
    }

    protected abstract MediaType getContentType();

    protected abstract SockJsFrameFormat getFrameFormat(ServerHttpRequest var1);

    @Nullable
    protected final String getCallbackParam(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        MultiValueMap params = UriComponentsBuilder.newInstance().query(query).build().getQueryParams();
        String value = (String)params.getFirst((Object)"c");
        if (!StringUtils.hasLength((String)value)) {
            return null;
        }
        String result = UriUtils.decode((String)value, (Charset)StandardCharsets.UTF_8);
        return CALLBACK_PARAM_PATTERN.matcher(result).matches() ? result : null;
    }
}

