/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.Invocable$InvocationType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks5 {
    public static final byte VERSION = 5;
    public static final byte COMMAND_CONNECT = 1;
    public static final byte RESERVED = 0;
    public static final byte ADDRESS_TYPE_IPV4 = 1;
    public static final byte ADDRESS_TYPE_DOMAIN = 3;
    public static final byte ADDRESS_TYPE_IPV6 = 4;

    private Socks5() {
    }

    public static class UsernamePasswordAuthenticationFactory
    implements Authentication.Factory {
        public static final byte METHOD = 2;
        public static final byte VERSION = 1;
        private static final Logger LOG = LoggerFactory.getLogger(UsernamePasswordAuthenticationFactory.class);
        private final String userName;
        private final String password;
        private final Charset charset;

        public UsernamePasswordAuthenticationFactory(String userName, String password) {
            this(userName, password, StandardCharsets.US_ASCII);
        }

        public UsernamePasswordAuthenticationFactory(String userName, String password, Charset charset) {
            this.userName = Objects.requireNonNull(userName);
            this.password = Objects.requireNonNull(password);
            this.charset = Objects.requireNonNull(charset);
        }

        @Override
        public byte getMethod() {
            return 2;
        }

        @Override
        public Authentication newAuthentication() {
            return new UsernamePasswordAuthentication(this);
        }

        private static class UsernamePasswordAuthentication
        implements Authentication,
        Callback {
            private final ByteBuffer byteBuffer = BufferUtil.allocate((int)2);
            private final UsernamePasswordAuthenticationFactory factory;
            private EndPoint endPoint;
            private Callback callback;

            private UsernamePasswordAuthentication(UsernamePasswordAuthenticationFactory factory) {
                this.factory = factory;
            }

            @Override
            public void authenticate(EndPoint endPoint, Callback callback) {
                this.endPoint = endPoint;
                this.callback = callback;
                byte[] userNameBytes = this.factory.userName.getBytes(this.factory.charset);
                byte[] passwordBytes = this.factory.password.getBytes(this.factory.charset);
                ByteBuffer byteBuffer = ByteBuffer.allocate(3 + userNameBytes.length + passwordBytes.length).put((byte)1).put((byte)userNameBytes.length).put(userNameBytes).put((byte)passwordBytes.length).put(passwordBytes).flip();
                endPoint.write(Callback.from(this::authenticationSent, this::failed), new ByteBuffer[]{byteBuffer});
            }

            private void authenticationSent() {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Written SOCKS5 username/password authentication request");
                }
                this.endPoint.fillInterested((Callback)this);
            }

            public void succeeded() {
                try {
                    byte version;
                    int filled = this.endPoint.fill(this.byteBuffer);
                    if (filled < 0) {
                        throw new ClosedChannelException();
                    }
                    if (this.byteBuffer.remaining() < 2) {
                        this.endPoint.fillInterested((Callback)this);
                        return;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Received SOCKS5 username/password authentication response");
                    }
                    if ((version = this.byteBuffer.get()) != 1) {
                        throw new IOException("Unsupported username/password authentication version: " + version);
                    }
                    byte status = this.byteBuffer.get();
                    if (status != 0) {
                        throw new IOException("SOCK5 username/password authentication failure");
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("SOCKS5 username/password authentication succeeded");
                    }
                    this.callback.succeeded();
                }
                catch (Throwable x) {
                    this.failed(x);
                }
            }

            public void failed(Throwable x) {
                this.callback.failed(x);
            }

            public Invocable.InvocationType getInvocationType() {
                return Invocable.InvocationType.NON_BLOCKING;
            }
        }
    }

    public static class NoAuthenticationFactory
    implements Authentication.Factory {
        public static final byte METHOD = 0;

        @Override
        public byte getMethod() {
            return 0;
        }

        @Override
        public Authentication newAuthentication() {
            return (endPoint, callback) -> callback.succeeded();
        }
    }

    public static interface Authentication {
        public void authenticate(EndPoint var1, Callback var2);

        public static interface Factory {
            public byte getMethod();

            public Authentication newAuthentication();
        }
    }
}

