/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageChannel
 *  org.springframework.messaging.MessageHeaders
 *  org.springframework.messaging.simp.SimpAttributes
 *  org.springframework.messaging.simp.SimpAttributesContextHolder
 *  org.springframework.messaging.simp.SimpMessageHeaderAccessor
 *  org.springframework.messaging.simp.SimpMessageType
 *  org.springframework.messaging.simp.broker.OrderedMessageChannelDecorator
 *  org.springframework.messaging.simp.stomp.BufferingStompDecoder
 *  org.springframework.messaging.simp.stomp.StompCommand
 *  org.springframework.messaging.simp.stomp.StompDecoder
 *  org.springframework.messaging.simp.stomp.StompEncoder
 *  org.springframework.messaging.simp.stomp.StompHeaderAccessor
 *  org.springframework.messaging.support.AbstractMessageChannel
 *  org.springframework.messaging.support.ChannelInterceptor
 *  org.springframework.messaging.support.ImmutableMessageChannelInterceptor
 *  org.springframework.messaging.support.MessageBuilder
 *  org.springframework.messaging.support.MessageHeaderAccessor
 *  org.springframework.messaging.support.MessageHeaderInitializer
 *  org.springframework.util.Assert
 *  org.springframework.util.MimeTypeUtils
 */
package org.springframework.web.socket.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.broker.OrderedMessageChannelDecorator;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.SessionLimitExceededException;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.sockjs.transport.SockJsSession;

public class StompSubProtocolHandler
implements SubProtocolHandler,
ApplicationEventPublisherAware {
    public static final int MINIMUM_WEBSOCKET_MESSAGE_SIZE = 16640;
    public static final String CONNECTED_USER_HEADER = "user-name";
    private static final String[] SUPPORTED_VERSIONS = new String[]{"1.2", "1.1", "1.0"};
    private static final Log logger = LogFactory.getLog(StompSubProtocolHandler.class);
    private static final byte[] EMPTY_PAYLOAD = new byte[0];
    @Nullable
    private StompSubProtocolErrorHandler errorHandler;
    private int messageSizeLimit = 65536;
    private StompEncoder stompEncoder = new StompEncoder();
    private StompDecoder stompDecoder = new StompDecoder();
    private final Map<String, BufferingStompDecoder> decoders = new ConcurrentHashMap<String, BufferingStompDecoder>();
    @Nullable
    private MessageHeaderInitializer headerInitializer;
    private final Map<String, Principal> stompAuthentications = new ConcurrentHashMap<String, Principal>();
    @Nullable
    private Boolean immutableMessageInterceptorPresent;
    @Nullable
    private ApplicationEventPublisher eventPublisher;
    private final DefaultStats stats = new DefaultStats();

    public void setErrorHandler(StompSubProtocolErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Nullable
    public StompSubProtocolErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setMessageSizeLimit(int messageSizeLimit) {
        this.messageSizeLimit = messageSizeLimit;
    }

    public int getMessageSizeLimit() {
        return this.messageSizeLimit;
    }

    public void setEncoder(StompEncoder encoder) {
        this.stompEncoder = encoder;
    }

    public void setDecoder(StompDecoder decoder) {
        this.stompDecoder = decoder;
    }

    public void setHeaderInitializer(@Nullable MessageHeaderInitializer headerInitializer) {
        this.headerInitializer = headerInitializer;
        this.stompDecoder.setHeaderInitializer(headerInitializer);
    }

    @Nullable
    public MessageHeaderInitializer getHeaderInitializer() {
        return this.headerInitializer;
    }

    @Override
    public List<String> getSupportedProtocols() {
        return Arrays.asList("v10.stomp", "v11.stomp", "v12.stomp");
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public String getStatsInfo() {
        return this.stats.toString();
    }

    public Stats getStats() {
        return this.stats;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleMessageFromClient(WebSocketSession session, WebSocketMessage<?> webSocketMessage, MessageChannel outputChannel) {
        List messages;
        try {
            ByteBuffer byteBuffer;
            if (webSocketMessage instanceof TextMessage) {
                byteBuffer = ByteBuffer.wrap(((TextMessage)webSocketMessage).asBytes());
            } else if (webSocketMessage instanceof BinaryMessage) {
                byteBuffer = (ByteBuffer)((BinaryMessage)webSocketMessage).getPayload();
            } else {
                return;
            }
            BufferingStompDecoder decoder = this.decoders.get(session.getId());
            if (decoder == null) {
                if (!session.isOpen()) {
                    logger.trace((Object)"Dropped inbound WebSocket message due to closed session");
                    return;
                }
                throw new IllegalStateException("No decoder for session id '" + session.getId() + "'");
            }
            messages = decoder.decode(byteBuffer);
            if (messages.isEmpty()) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Incomplete STOMP frame content received in session " + session + ", bufferSize=" + decoder.getBufferSize() + ", bufferSizeLimit=" + decoder.getBufferSizeLimit() + "."));
                }
                return;
            }
        }
        catch (Throwable ex) {
            if (logger.isErrorEnabled()) {
                logger.error((Object)("Failed to parse " + webSocketMessage + " in session " + session.getId() + ". Sending STOMP ERROR to client."), ex);
            }
            this.handleError(session, ex, null);
            return;
        }
        for (Message message : messages) {
            StompHeaderAccessor headerAccessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor((Message)message, StompHeaderAccessor.class);
            Assert.state((headerAccessor != null ? 1 : 0) != 0, (String)"No StompHeaderAccessor");
            StompCommand command = headerAccessor.getCommand();
            boolean isConnect = StompCommand.CONNECT.equals((Object)command) || StompCommand.STOMP.equals((Object)command);
            boolean sent = false;
            try {
                headerAccessor.setSessionId(session.getId());
                headerAccessor.setSessionAttributes(session.getAttributes());
                headerAccessor.setUser(this.getUser(session));
                if (isConnect) {
                    headerAccessor.setUserChangeCallback(user -> {
                        if (user != null && user != session.getPrincipal()) {
                            this.stompAuthentications.put(session.getId(), (Principal)user);
                        }
                    });
                }
                headerAccessor.setHeader("simpHeartbeat", (Object)headerAccessor.getHeartbeat());
                if (!this.detectImmutableMessageInterceptor(outputChannel)) {
                    headerAccessor.setImmutable();
                }
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("From client: " + headerAccessor.getShortLogMessage(message.getPayload())));
                }
                if (isConnect) {
                    this.stats.incrementConnectCount();
                } else if (StompCommand.DISCONNECT.equals((Object)command)) {
                    this.stats.incrementDisconnectCount();
                }
                try {
                    SimpAttributesContextHolder.setAttributesFromMessage((Message)message);
                    sent = outputChannel.send(message);
                    if (!sent || this.eventPublisher == null) continue;
                    Principal user2 = this.getUser(session);
                    if (isConnect) {
                        this.publishEvent(this.eventPublisher, new SessionConnectEvent(this, (Message<byte[]>)message, user2));
                        continue;
                    }
                    if (StompCommand.SUBSCRIBE.equals((Object)command)) {
                        this.publishEvent(this.eventPublisher, new SessionSubscribeEvent(this, (Message<byte[]>)message, user2));
                        continue;
                    }
                    if (!StompCommand.UNSUBSCRIBE.equals((Object)command)) continue;
                    this.publishEvent(this.eventPublisher, new SessionUnsubscribeEvent(this, (Message<byte[]>)message, user2));
                }
                finally {
                    SimpAttributesContextHolder.resetAttributes();
                }
            }
            catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Failed to send message to MessageChannel in session " + session.getId()), ex);
                } else if (logger.isErrorEnabled() && (!isConnect || sent)) {
                    logger.error((Object)("Failed to send message to MessageChannel in session " + session.getId() + ":" + ex.getMessage()));
                }
                this.handleError(session, ex, (Message<byte[]>)message);
            }
        }
    }

    @Nullable
    private Principal getUser(WebSocketSession session) {
        Principal user = this.stompAuthentications.get(session.getId());
        return user != null ? user : session.getPrincipal();
    }

    private void handleError(WebSocketSession session, Throwable ex, @Nullable Message<byte[]> clientMessage) {
        if (this.getErrorHandler() == null) {
            this.sendErrorMessage(session, ex);
            return;
        }
        Message<byte[]> message = this.getErrorHandler().handleClientMessageProcessingError(clientMessage, ex);
        if (message == null) {
            return;
        }
        StompHeaderAccessor accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.state((accessor != null ? 1 : 0) != 0, (String)"No StompHeaderAccessor");
        this.sendToClient(session, accessor, (byte[])message.getPayload());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendErrorMessage(WebSocketSession session, Throwable error) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create((StompCommand)StompCommand.ERROR);
        headerAccessor.setMessage(error.getMessage());
        byte[] bytes = this.stompEncoder.encode((Map)headerAccessor.getMessageHeaders(), EMPTY_PAYLOAD);
        try {
            session.sendMessage(new TextMessage(bytes));
        }
        catch (Throwable ex) {
            logger.debug((Object)"Failed to send STOMP ERROR to client", ex);
        }
        finally {
            try {
                session.close(CloseStatus.PROTOCOL_ERROR);
            }
            catch (IOException iOException) {}
        }
    }

    private boolean detectImmutableMessageInterceptor(MessageChannel channel) {
        if (this.immutableMessageInterceptorPresent != null) {
            return this.immutableMessageInterceptorPresent;
        }
        if (channel instanceof AbstractMessageChannel) {
            for (ChannelInterceptor interceptor : ((AbstractMessageChannel)channel).getInterceptors()) {
                if (!(interceptor instanceof ImmutableMessageChannelInterceptor)) continue;
                this.immutableMessageInterceptorPresent = true;
                return true;
            }
        }
        this.immutableMessageInterceptorPresent = false;
        return false;
    }

    private void publishEvent(ApplicationEventPublisher publisher, ApplicationEvent event) {
        block2: {
            try {
                publisher.publishEvent(event);
            }
            catch (Throwable ex) {
                if (!logger.isErrorEnabled()) break block2;
                logger.error((Object)("Error publishing " + event), ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleMessageToClient(WebSocketSession session, Message<?> message) {
        Runnable task;
        Message<byte[]> errorMessage;
        if (!(message.getPayload() instanceof byte[])) {
            if (logger.isErrorEnabled()) {
                logger.error((Object)("Expected byte[] payload. Ignoring " + message + "."));
            }
            return;
        }
        StompHeaderAccessor accessor = this.getStompHeaderAccessor(message);
        StompCommand command = accessor.getCommand();
        if (StompCommand.MESSAGE.equals((Object)command)) {
            String origDestination;
            if (accessor.getSubscriptionId() == null && logger.isWarnEnabled()) {
                logger.warn((Object)("No STOMP \"subscription\" header in " + message));
            }
            if ((origDestination = accessor.getFirstNativeHeader("simpOrigDestination")) != null) {
                accessor = this.toMutableAccessor(accessor, message);
                accessor.removeNativeHeader("simpOrigDestination");
                accessor.setDestination(origDestination);
            }
        } else if (StompCommand.CONNECTED.equals((Object)command)) {
            this.stats.incrementConnectedCount();
            accessor = this.afterStompSessionConnected(message, accessor, session);
            if (this.eventPublisher != null) {
                try {
                    SimpAttributes simpAttributes = new SimpAttributes(session.getId(), session.getAttributes());
                    SimpAttributesContextHolder.setAttributes((SimpAttributes)simpAttributes);
                    Principal user = this.getUser(session);
                    this.publishEvent(this.eventPublisher, new SessionConnectedEvent(this, message, user));
                }
                finally {
                    SimpAttributesContextHolder.resetAttributes();
                }
            }
        }
        byte[] payload = (byte[])message.getPayload();
        if (StompCommand.ERROR.equals((Object)command) && this.getErrorHandler() != null && (errorMessage = this.getErrorHandler().handleErrorMessageToClient((Message<byte[]>)message)) != null) {
            accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(errorMessage, StompHeaderAccessor.class);
            Assert.state((accessor != null ? 1 : 0) != 0, (String)"No StompHeaderAccessor");
            payload = (byte[])errorMessage.getPayload();
        }
        if ((task = OrderedMessageChannelDecorator.getNextMessageTask(message)) != null) {
            Assert.isInstanceOf(ConcurrentWebSocketSessionDecorator.class, (Object)session);
            ((ConcurrentWebSocketSessionDecorator)session).setMessageCallback(m -> task.run());
        }
        this.sendToClient(session, accessor, payload);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendToClient(WebSocketSession session, StompHeaderAccessor stompAccessor, byte[] payload) {
        StompCommand command = stompAccessor.getCommand();
        try {
            boolean useBinary;
            byte[] bytes = this.stompEncoder.encode((Map)stompAccessor.getMessageHeaders(), payload);
            boolean bl = useBinary = payload.length > 0 && !(session instanceof SockJsSession) && MimeTypeUtils.APPLICATION_OCTET_STREAM.isCompatibleWith(stompAccessor.getContentType());
            if (useBinary) {
                session.sendMessage(new BinaryMessage(bytes));
            } else {
                session.sendMessage(new TextMessage(bytes));
            }
        }
        catch (SessionLimitExceededException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Failed to send WebSocket message to client in session " + session.getId()), ex);
            }
            command = StompCommand.ERROR;
        }
        finally {
            if (StompCommand.ERROR.equals((Object)command)) {
                try {
                    session.close(CloseStatus.PROTOCOL_ERROR);
                }
                catch (IOException iOException) {}
            }
        }
    }

    private StompHeaderAccessor getStompHeaderAccessor(Message<?> message) {
        MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
        if (accessor instanceof StompHeaderAccessor) {
            return (StompHeaderAccessor)accessor;
        }
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(message);
        SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType((Map)message.getHeaders());
        if (SimpMessageType.CONNECT_ACK.equals((Object)messageType)) {
            stompAccessor = this.convertConnectAcktoStompConnected(stompAccessor);
        } else if (SimpMessageType.DISCONNECT_ACK.equals((Object)messageType)) {
            String receipt = this.getDisconnectReceipt((SimpMessageHeaderAccessor)stompAccessor);
            if (receipt != null) {
                stompAccessor = StompHeaderAccessor.create((StompCommand)StompCommand.RECEIPT);
                stompAccessor.setReceiptId(receipt);
            } else {
                stompAccessor = StompHeaderAccessor.create((StompCommand)StompCommand.ERROR);
                stompAccessor.setMessage("Session closed.");
            }
        } else if (SimpMessageType.HEARTBEAT.equals((Object)messageType)) {
            stompAccessor = StompHeaderAccessor.createForHeartbeat();
        } else if (stompAccessor.getCommand() == null || StompCommand.SEND.equals((Object)stompAccessor.getCommand())) {
            stompAccessor.updateStompCommandAsServerMessage();
        }
        return stompAccessor;
    }

    private StompHeaderAccessor convertConnectAcktoStompConnected(StompHeaderAccessor connectAckHeaders) {
        long[] heartbeat;
        String name = "simpConnectMessage";
        Message message = (Message)connectAckHeaders.getHeader(name);
        if (message == null) {
            throw new IllegalStateException("Original STOMP CONNECT not found in " + connectAckHeaders);
        }
        StompHeaderAccessor connectHeaders = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor((Message)message, StompHeaderAccessor.class);
        StompHeaderAccessor connectedHeaders = StompHeaderAccessor.create((StompCommand)StompCommand.CONNECTED);
        if (connectHeaders != null) {
            Set acceptVersions = connectHeaders.getAcceptVersion();
            connectedHeaders.setVersion(Arrays.stream(SUPPORTED_VERSIONS).filter(acceptVersions::contains).findAny().orElseThrow(() -> new IllegalArgumentException("Unsupported STOMP version '" + acceptVersions + "'")));
        }
        if ((heartbeat = (long[])connectAckHeaders.getHeader("simpHeartbeat")) != null) {
            connectedHeaders.setHeartbeat(heartbeat[0], heartbeat[1]);
        } else {
            connectedHeaders.setHeartbeat(0L, 0L);
        }
        return connectedHeaders;
    }

    @Nullable
    private String getDisconnectReceipt(SimpMessageHeaderAccessor simpHeaders) {
        StompHeaderAccessor accessor;
        String name = "simpDisconnectMessage";
        Message message = (Message)simpHeaders.getHeader(name);
        if (message != null && (accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor((Message)message, StompHeaderAccessor.class)) != null) {
            return accessor.getReceipt();
        }
        return null;
    }

    protected StompHeaderAccessor toMutableAccessor(StompHeaderAccessor headerAccessor, Message<?> message) {
        return headerAccessor.isMutable() ? headerAccessor : StompHeaderAccessor.wrap(message);
    }

    private StompHeaderAccessor afterStompSessionConnected(Message<?> message, StompHeaderAccessor accessor, WebSocketSession session) {
        long[] heartbeat;
        Principal principal = this.getUser(session);
        if (principal != null) {
            accessor = this.toMutableAccessor(accessor, message);
            accessor.setNativeHeader(CONNECTED_USER_HEADER, principal.getName());
        }
        if ((heartbeat = accessor.getHeartbeat())[1] > 0L && (session = WebSocketSessionDecorator.unwrap(session)) instanceof SockJsSession) {
            ((SockJsSession)session).disableHeartbeat();
        }
        return accessor;
    }

    @Override
    @Nullable
    public String resolveSessionId(Message<?> message) {
        return SimpMessageHeaderAccessor.getSessionId((Map)message.getHeaders());
    }

    @Override
    public void afterSessionStarted(WebSocketSession session, MessageChannel outputChannel) {
        if (session.getTextMessageSizeLimit() < 16640) {
            session.setTextMessageSizeLimit(16640);
        }
        this.decoders.put(session.getId(), new BufferingStompDecoder(this.stompDecoder, this.getMessageSizeLimit()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void afterSessionEnded(WebSocketSession session, CloseStatus closeStatus, MessageChannel outputChannel) {
        this.decoders.remove(session.getId());
        Message<byte[]> message = this.createDisconnectMessage(session);
        SimpAttributes simpAttributes = SimpAttributes.fromMessage(message);
        try {
            SimpAttributesContextHolder.setAttributes((SimpAttributes)simpAttributes);
            if (this.eventPublisher != null) {
                Principal user = this.getUser(session);
                this.publishEvent(this.eventPublisher, new SessionDisconnectEvent(this, message, session.getId(), closeStatus, user));
            }
            outputChannel.send(message);
        }
        finally {
            this.stompAuthentications.remove(session.getId());
            SimpAttributesContextHolder.resetAttributes();
            simpAttributes.sessionCompleted();
        }
    }

    private Message<byte[]> createDisconnectMessage(WebSocketSession session) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create((StompCommand)StompCommand.DISCONNECT);
        if (this.getHeaderInitializer() != null) {
            this.getHeaderInitializer().initHeaders((MessageHeaderAccessor)headerAccessor);
        }
        headerAccessor.setSessionId(session.getId());
        headerAccessor.setSessionAttributes(session.getAttributes());
        Principal user = this.getUser(session);
        if (user != null) {
            headerAccessor.setUser(user);
        }
        return MessageBuilder.createMessage((Object)EMPTY_PAYLOAD, (MessageHeaders)headerAccessor.getMessageHeaders());
    }

    public String toString() {
        return "StompSubProtocolHandler" + this.getSupportedProtocols();
    }

    private static class DefaultStats
    implements Stats {
        private final AtomicInteger connect = new AtomicInteger();
        private final AtomicInteger connected = new AtomicInteger();
        private final AtomicInteger disconnect = new AtomicInteger();

        private DefaultStats() {
        }

        public void incrementConnectCount() {
            this.connect.incrementAndGet();
        }

        public void incrementConnectedCount() {
            this.connected.incrementAndGet();
        }

        public void incrementDisconnectCount() {
            this.disconnect.incrementAndGet();
        }

        @Override
        public int getTotalConnect() {
            return this.connect.get();
        }

        @Override
        public int getTotalConnected() {
            return this.connected.get();
        }

        @Override
        public int getTotalDisconnect() {
            return this.disconnect.get();
        }

        public String toString() {
            return "processed CONNECT(" + this.connect.get() + ")-CONNECTED(" + this.connected.get() + ")-DISCONNECT(" + this.disconnect.get() + ")";
        }
    }

    public static interface Stats {
        public int getTotalConnect();

        public int getTotalConnected();

        public int getTotalDisconnect();
    }
}

