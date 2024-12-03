/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.SettableListenableFuture
 */
package org.springframework.web.socket.sockjs.client;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

public abstract class AbstractClientSockJsSession
implements WebSocketSession {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final TransportRequest request;
    private final WebSocketHandler webSocketHandler;
    private final SettableListenableFuture<WebSocketSession> connectFuture;
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    @Nullable
    private volatile State state = State.NEW;
    @Nullable
    private volatile CloseStatus closeStatus;

    protected AbstractClientSockJsSession(TransportRequest request, WebSocketHandler handler, SettableListenableFuture<WebSocketSession> connectFuture) {
        Assert.notNull((Object)request, (String)"'request' is required");
        Assert.notNull((Object)handler, (String)"'handler' is required");
        Assert.notNull(connectFuture, (String)"'connectFuture' is required");
        this.request = request;
        this.webSocketHandler = handler;
        this.connectFuture = connectFuture;
    }

    @Override
    public String getId() {
        return this.request.getSockJsUrlInfo().getSessionId();
    }

    @Override
    public URI getUri() {
        return this.request.getSockJsUrlInfo().getSockJsUrl();
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return this.request.getHandshakeHeaders();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Principal getPrincipal() {
        return this.request.getUser();
    }

    public SockJsMessageCodec getMessageCodec() {
        return this.request.getMessageCodec();
    }

    public WebSocketHandler getWebSocketHandler() {
        return this.webSocketHandler;
    }

    Runnable getTimeoutTask() {
        return new Runnable(){

            @Override
            public void run() {
                block2: {
                    try {
                        AbstractClientSockJsSession.this.closeInternal(new CloseStatus(2007, "Transport timed out"));
                    }
                    catch (Throwable ex) {
                        if (!AbstractClientSockJsSession.this.logger.isWarnEnabled()) break block2;
                        AbstractClientSockJsSession.this.logger.warn((Object)("Failed to close " + this + " after transport timeout"), ex);
                    }
                }
            }
        };
    }

    @Override
    public boolean isOpen() {
        return this.state == State.OPEN;
    }

    public boolean isDisconnected() {
        return this.state == State.CLOSING || this.state == State.CLOSED;
    }

    @Override
    public final void sendMessage(WebSocketMessage<?> message) throws IOException {
        if (!(message instanceof TextMessage)) {
            throw new IllegalArgumentException(this + " supports text messages only.");
        }
        if (this.state != State.OPEN) {
            throw new IllegalStateException(this + " is not open: current state " + (Object)((Object)this.state));
        }
        String payload = (String)((TextMessage)message).getPayload();
        payload = this.getMessageCodec().encode(payload);
        payload = payload.substring(1);
        TextMessage messageToSend = new TextMessage(payload);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Sending message " + messageToSend + " in " + this));
        }
        this.sendInternal(messageToSend);
    }

    protected abstract void sendInternal(TextMessage var1) throws IOException;

    @Override
    public final void close() throws IOException {
        this.close(CloseStatus.NORMAL);
    }

    @Override
    public final void close(CloseStatus status) throws IOException {
        if (!this.isUserSetStatus(status)) {
            throw new IllegalArgumentException("Invalid close status: " + status);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Closing session with " + status + " in " + this));
        }
        this.closeInternal(status);
    }

    private boolean isUserSetStatus(@Nullable CloseStatus status) {
        return status != null && (status.getCode() == 1000 || status.getCode() >= 3000 && status.getCode() <= 4999);
    }

    private void silentClose(CloseStatus status) {
        block2: {
            try {
                this.closeInternal(status);
            }
            catch (Throwable ex) {
                if (!this.logger.isWarnEnabled()) break block2;
                this.logger.warn((Object)("Failed to close " + this), ex);
            }
        }
    }

    protected void closeInternal(CloseStatus status) throws IOException {
        if (this.state == null) {
            this.logger.warn((Object)"Ignoring close since connect() was never invoked");
            return;
        }
        if (this.isDisconnected()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Ignoring close (already closing or closed): current state " + (Object)((Object)this.state)));
            }
            return;
        }
        this.state = State.CLOSING;
        this.closeStatus = status;
        this.disconnect(status);
    }

    protected abstract void disconnect(CloseStatus var1) throws IOException;

    public void handleFrame(String payload) {
        SockJsFrame frame = new SockJsFrame(payload);
        switch (frame.getType()) {
            case OPEN: {
                this.handleOpenFrame();
                break;
            }
            case HEARTBEAT: {
                if (!this.logger.isTraceEnabled()) break;
                this.logger.trace((Object)("Received heartbeat in " + this));
                break;
            }
            case MESSAGE: {
                this.handleMessageFrame(frame);
                break;
            }
            case CLOSE: {
                this.handleCloseFrame(frame);
            }
        }
    }

    private void handleOpenFrame() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Processing SockJS open frame in " + this));
        }
        if (this.state == State.NEW) {
            this.state = State.OPEN;
            try {
                this.webSocketHandler.afterConnectionEstablished(this);
                this.connectFuture.set((Object)this);
            }
            catch (Exception ex) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error((Object)("WebSocketHandler.afterConnectionEstablished threw exception in " + this), (Throwable)ex);
                }
            }
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Open frame received in " + this.getId() + " but we're not connecting (current state " + (Object)((Object)this.state) + "). The server might have been restarted and lost track of the session."));
            }
            this.silentClose(new CloseStatus(1006, "Server lost session"));
        }
    }

    private void handleMessageFrame(SockJsFrame frame) {
        if (!this.isOpen()) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error((Object)("Ignoring received message due to state " + (Object)((Object)this.state) + " in " + this));
            }
            return;
        }
        String[] messages = null;
        String frameData = frame.getFrameData();
        if (frameData != null) {
            try {
                messages = this.getMessageCodec().decode(frameData);
            }
            catch (IOException ex) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error((Object)("Failed to decode data for SockJS \"message\" frame: " + frame + " in " + this), (Throwable)ex);
                }
                this.silentClose(CloseStatus.BAD_DATA);
                return;
            }
        }
        if (messages == null) {
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Processing SockJS message frame " + frame.getContent() + " in " + this));
        }
        for (String message : messages) {
            if (!this.isOpen()) continue;
            try {
                this.webSocketHandler.handleMessage(this, new TextMessage(message));
            }
            catch (Exception ex) {
                this.logger.error((Object)("WebSocketHandler.handleMessage threw an exception on " + frame + " in " + this), (Throwable)ex);
            }
        }
    }

    private void handleCloseFrame(SockJsFrame frame) {
        CloseStatus closeStatus;
        block5: {
            closeStatus = CloseStatus.NO_STATUS_CODE;
            try {
                String frameData = frame.getFrameData();
                if (frameData != null) {
                    String[] data = this.getMessageCodec().decode(frameData);
                    if (data != null && data.length == 2) {
                        closeStatus = new CloseStatus(Integer.parseInt(data[0]), data[1]);
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Processing SockJS close frame with " + closeStatus + " in " + this));
                    }
                }
            }
            catch (IOException ex) {
                if (!this.logger.isErrorEnabled()) break block5;
                this.logger.error((Object)("Failed to decode data for " + frame + " in " + this), (Throwable)ex);
            }
        }
        this.silentClose(closeStatus);
    }

    public void handleTransportError(Throwable error) {
        try {
            if (this.logger.isErrorEnabled()) {
                this.logger.error((Object)("Transport error in " + this), error);
            }
            this.webSocketHandler.handleTransportError(this, error);
        }
        catch (Throwable ex) {
            this.logger.error((Object)"WebSocketHandler.handleTransportError threw an exception", ex);
        }
    }

    public void afterTransportClosed(@Nullable CloseStatus closeStatus) {
        CloseStatus cs = this.closeStatus;
        if (cs == null) {
            cs = closeStatus;
            this.closeStatus = closeStatus;
        }
        Assert.state((cs != null ? 1 : 0) != 0, (String)"CloseStatus not available");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Transport closed with " + cs + " in " + this));
        }
        this.state = State.CLOSED;
        try {
            this.webSocketHandler.afterConnectionClosed(this, cs);
        }
        catch (Throwable ex) {
            this.logger.error((Object)"WebSocketHandler.afterConnectionClosed threw an exception", ex);
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[id='" + this.getId() + ", url=" + this.getUri() + "]";
    }

    private static enum State {
        NEW,
        OPEN,
        CLOSING,
        CLOSED;

    }
}

