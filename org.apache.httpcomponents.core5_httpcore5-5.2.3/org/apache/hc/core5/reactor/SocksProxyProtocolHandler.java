/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.nio.command.CommandSupport;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.SocketTimeoutExceptionFactory;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionRequest;
import org.apache.hc.core5.reactor.InternalDataChannel;
import org.apache.hc.core5.util.Timeout;

final class SocksProxyProtocolHandler
implements IOEventHandler {
    private static final int MAX_DNS_NAME_LENGTH = 255;
    private static final int MAX_COMMAND_CONNECT_LENGTH = 262;
    private static final byte CLIENT_VERSION = 5;
    private static final byte NO_AUTHENTICATION_REQUIRED = 0;
    private static final byte USERNAME_PASSWORD = 2;
    private static final byte USERNAME_PASSWORD_VERSION = 1;
    private static final byte SUCCESS = 0;
    private static final byte COMMAND_CONNECT = 1;
    private static final byte ATYP_DOMAINNAME = 3;
    private final InternalDataChannel dataChannel;
    private final IOSessionRequest sessionRequest;
    private final IOEventHandlerFactory eventHandlerFactory;
    private final IOReactorConfig reactorConfig;
    private ByteBuffer buffer = ByteBuffer.allocate(512);
    private State state = State.SEND_AUTH;

    SocksProxyProtocolHandler(InternalDataChannel dataChannel, IOSessionRequest sessionRequest, IOEventHandlerFactory eventHandlerFactory, IOReactorConfig reactorConfig) {
        this.dataChannel = dataChannel;
        this.sessionRequest = sessionRequest;
        this.eventHandlerFactory = eventHandlerFactory;
        this.reactorConfig = reactorConfig;
    }

    @Override
    public void connected(IOSession session) throws IOException {
        this.buffer.put((byte)5);
        if (this.reactorConfig.getSocksProxyUsername() != null && this.reactorConfig.getSocksProxyPassword() != null) {
            this.buffer.put((byte)2);
            this.buffer.put((byte)0);
            this.buffer.put((byte)2);
        } else {
            this.buffer.put((byte)1);
            this.buffer.put((byte)0);
        }
        this.buffer.flip();
        session.setEventMask(4);
    }

    @Override
    public void outputReady(IOSession session) throws IOException {
        switch (this.state) {
            case SEND_AUTH: {
                if (!this.writeAndPrepareRead(session, 2)) break;
                session.setEventMask(1);
                this.state = State.RECEIVE_AUTH_METHOD;
                break;
            }
            case SEND_USERNAME_PASSWORD: {
                if (!this.writeAndPrepareRead(session, 2)) break;
                session.setEventMask(1);
                this.state = State.RECEIVE_AUTH;
                break;
            }
            case SEND_CONNECT: {
                if (!this.writeAndPrepareRead(session, 2)) break;
                session.setEventMask(1);
                this.state = State.RECEIVE_RESPONSE_CODE;
                break;
            }
            case RECEIVE_AUTH_METHOD: 
            case RECEIVE_AUTH: 
            case RECEIVE_ADDRESS: 
            case RECEIVE_ADDRESS_TYPE: 
            case RECEIVE_RESPONSE_CODE: {
                session.setEventMask(1);
                break;
            }
        }
    }

    private byte[] cred(String cred) throws IOException {
        if (cred == null) {
            return new byte[0];
        }
        byte[] bytes = cred.getBytes(StandardCharsets.ISO_8859_1);
        if (bytes.length >= 255) {
            throw new IOException("SOCKS username / password are too long");
        }
        return bytes;
    }

    @Override
    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        if (src != null) {
            try {
                this.buffer.put(src);
            }
            catch (BufferOverflowException ex) {
                throw new IOException("Unexpected input data");
            }
        }
        switch (this.state) {
            case RECEIVE_AUTH_METHOD: {
                if (!this.fillBuffer(session)) break;
                this.buffer.flip();
                byte serverVersion = this.buffer.get();
                byte serverMethod = this.buffer.get();
                if (serverVersion != 5) {
                    throw new IOException("SOCKS server returned unsupported version: " + serverVersion);
                }
                if (serverMethod == 2) {
                    this.buffer.clear();
                    byte[] username = this.cred(this.reactorConfig.getSocksProxyUsername());
                    byte[] password = this.cred(this.reactorConfig.getSocksProxyPassword());
                    this.setBufferLimit(username.length + password.length + 3);
                    this.buffer.put((byte)1);
                    this.buffer.put((byte)username.length);
                    this.buffer.put(username);
                    this.buffer.put((byte)password.length);
                    this.buffer.put(password);
                    this.buffer.flip();
                    session.setEventMask(4);
                    this.state = State.SEND_USERNAME_PASSWORD;
                    break;
                }
                if (serverMethod == 0) {
                    this.prepareConnectCommand();
                    session.setEventMask(4);
                    this.state = State.SEND_CONNECT;
                    break;
                }
                throw new IOException("SOCKS server return unsupported authentication method: " + serverMethod);
            }
            case RECEIVE_AUTH: {
                if (!this.fillBuffer(session)) break;
                this.buffer.flip();
                this.buffer.get();
                byte status = this.buffer.get();
                if (status != 0) {
                    throw new IOException("Authentication failed for external SOCKS proxy");
                }
                this.prepareConnectCommand();
                session.setEventMask(4);
                this.state = State.SEND_CONNECT;
                break;
            }
            case RECEIVE_RESPONSE_CODE: {
                if (!this.fillBuffer(session)) break;
                this.buffer.flip();
                byte serverVersion = this.buffer.get();
                byte responseCode = this.buffer.get();
                if (serverVersion != 5) {
                    throw new IOException("SOCKS server returned unsupported version: " + serverVersion);
                }
                switch (responseCode) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        throw new IOException("SOCKS: General SOCKS server failure");
                    }
                    case 2: {
                        throw new IOException("SOCKS5: Connection not allowed by ruleset");
                    }
                    case 3: {
                        throw new IOException("SOCKS5: Network unreachable");
                    }
                    case 4: {
                        throw new IOException("SOCKS5: Host unreachable");
                    }
                    case 5: {
                        throw new IOException("SOCKS5: Connection refused");
                    }
                    case 6: {
                        throw new IOException("SOCKS5: TTL expired");
                    }
                    case 7: {
                        throw new IOException("SOCKS5: Command not supported");
                    }
                    case 8: {
                        throw new IOException("SOCKS5: Address type not supported");
                    }
                    default: {
                        throw new IOException("SOCKS5: Unexpected SOCKS response code " + responseCode);
                    }
                }
                this.buffer.compact();
                this.buffer.limit(3);
                this.state = State.RECEIVE_ADDRESS_TYPE;
            }
            case RECEIVE_ADDRESS_TYPE: {
                int addressSize;
                if (!this.fillBuffer(session)) break;
                this.buffer.flip();
                this.buffer.get();
                byte aType = this.buffer.get();
                if (aType == 1) {
                    addressSize = 4;
                } else if (aType == 4) {
                    addressSize = 16;
                } else if (aType == 3) {
                    addressSize = this.buffer.get() & 0xFF;
                } else {
                    throw new IOException("SOCKS server returned unsupported address type: " + aType);
                }
                int remainingResponseSize = addressSize + 2;
                this.buffer.compact();
                this.buffer.limit(remainingResponseSize);
                this.state = State.RECEIVE_ADDRESS;
            }
            case RECEIVE_ADDRESS: {
                if (!this.fillBuffer(session)) break;
                this.buffer.clear();
                this.state = State.COMPLETE;
                IOEventHandler newHandler = this.eventHandlerFactory.createHandler(this.dataChannel, this.sessionRequest.attachment);
                this.dataChannel.upgrade(newHandler);
                this.sessionRequest.completed(this.dataChannel);
                this.dataChannel.handleIOEvent(8);
                break;
            }
            case SEND_AUTH: 
            case SEND_USERNAME_PASSWORD: 
            case SEND_CONNECT: {
                session.setEventMask(4);
                break;
            }
        }
    }

    private void prepareConnectCommand() throws IOException {
        this.buffer.clear();
        this.setBufferLimit(262);
        this.buffer.put((byte)5);
        this.buffer.put((byte)1);
        this.buffer.put((byte)0);
        if (!(this.sessionRequest.remoteAddress instanceof InetSocketAddress)) {
            throw new IOException("Unsupported address class: " + this.sessionRequest.remoteAddress.getClass());
        }
        InetSocketAddress targetAddress = (InetSocketAddress)this.sessionRequest.remoteAddress;
        if (targetAddress.isUnresolved()) {
            this.buffer.put((byte)3);
            String hostName = targetAddress.getHostName();
            byte[] hostnameBytes = hostName.getBytes(StandardCharsets.US_ASCII);
            if (hostnameBytes.length > 255) {
                throw new IOException("Host name exceeds 255 bytes");
            }
            this.buffer.put((byte)hostnameBytes.length);
            this.buffer.put(hostnameBytes);
        } else {
            InetAddress address = targetAddress.getAddress();
            if (address instanceof Inet4Address) {
                this.buffer.put((byte)1);
            } else if (address instanceof Inet6Address) {
                this.buffer.put((byte)4);
            } else {
                throw new IOException("Unsupported remote address class: " + address.getClass().getName());
            }
            this.buffer.put(address.getAddress());
        }
        int port = targetAddress.getPort();
        this.buffer.putShort((short)port);
        this.buffer.flip();
    }

    private void setBufferLimit(int newLimit) {
        if (this.buffer.capacity() < newLimit) {
            ByteBuffer newBuffer = ByteBuffer.allocate(newLimit);
            this.buffer.flip();
            newBuffer.put(this.buffer);
            this.buffer = newBuffer;
        } else {
            this.buffer.limit(newLimit);
        }
    }

    private boolean writeAndPrepareRead(ByteChannel channel, int readSize) throws IOException {
        if (this.writeBuffer(channel)) {
            this.buffer.clear();
            this.setBufferLimit(readSize);
            return true;
        }
        return false;
    }

    private boolean writeBuffer(ByteChannel channel) throws IOException {
        if (this.buffer.hasRemaining()) {
            channel.write(this.buffer);
        }
        return !this.buffer.hasRemaining();
    }

    private boolean fillBuffer(ByteChannel channel) throws IOException {
        if (this.buffer.hasRemaining()) {
            channel.read(this.buffer);
        }
        return !this.buffer.hasRemaining();
    }

    @Override
    public void timeout(IOSession session, Timeout timeout) throws IOException {
        this.exception(session, SocketTimeoutExceptionFactory.create(timeout));
    }

    @Override
    public void exception(IOSession session, Exception cause) {
        try {
            this.sessionRequest.failed(cause);
        }
        finally {
            session.close(CloseMode.IMMEDIATE);
            CommandSupport.failCommands(session, cause);
        }
    }

    @Override
    public void disconnected(IOSession session) {
        this.sessionRequest.cancel();
        CommandSupport.cancelCommands(session);
    }

    private static enum State {
        SEND_AUTH,
        RECEIVE_AUTH_METHOD,
        SEND_USERNAME_PASSWORD,
        RECEIVE_AUTH,
        SEND_CONNECT,
        RECEIVE_RESPONSE_CODE,
        RECEIVE_ADDRESS_TYPE,
        RECEIVE_ADDRESS,
        COMPLETE;

    }
}

