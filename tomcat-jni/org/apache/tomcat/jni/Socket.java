/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

@Deprecated
public class Socket {
    public static final int SOCK_STREAM = 0;
    public static final int SOCK_DGRAM = 1;
    public static final int APR_SO_LINGER = 1;
    public static final int APR_SO_KEEPALIVE = 2;
    public static final int APR_SO_DEBUG = 4;
    public static final int APR_SO_NONBLOCK = 8;
    public static final int APR_SO_REUSEADDR = 16;
    public static final int APR_SO_SNDBUF = 64;
    public static final int APR_SO_RCVBUF = 128;
    public static final int APR_SO_DISCONNECTED = 256;
    public static final int APR_TCP_NODELAY = 512;
    public static final int APR_TCP_NOPUSH = 1024;
    public static final int APR_RESET_NODELAY = 2048;
    public static final int APR_INCOMPLETE_READ = 4096;
    public static final int APR_INCOMPLETE_WRITE = 8192;
    public static final int APR_IPV6_V6ONLY = 16384;
    public static final int APR_TCP_DEFER_ACCEPT = 32768;
    public static final int APR_SHUTDOWN_READ = 0;
    public static final int APR_SHUTDOWN_WRITE = 1;
    public static final int APR_SHUTDOWN_READWRITE = 2;
    public static final int APR_IPV4_ADDR_OK = 1;
    public static final int APR_IPV6_ADDR_OK = 2;
    public static final int APR_UNSPEC = 0;
    public static final int APR_INET = 1;
    public static final int APR_INET6 = 2;
    public static final int APR_UNIX = 3;
    public static final int APR_PROTO_TCP = 6;
    public static final int APR_PROTO_UDP = 17;
    public static final int APR_PROTO_SCTP = 132;
    public static final int APR_LOCAL = 0;
    public static final int APR_REMOTE = 1;
    public static final int SOCKET_GET_POOL = 0;
    public static final int SOCKET_GET_IMPL = 1;
    public static final int SOCKET_GET_APRS = 2;
    public static final int SOCKET_GET_TYPE = 3;

    public static native long create(int var0, int var1, int var2, long var3) throws Exception;

    public static native int shutdown(long var0, int var2);

    public static native int close(long var0);

    public static native void destroy(long var0);

    public static native int bind(long var0, long var2);

    public static native int listen(long var0, int var2);

    public static native long acceptx(long var0, long var2) throws Exception;

    public static native long accept(long var0) throws Exception;

    public static native int acceptfilter(long var0, String var2, String var3);

    public static native boolean atmark(long var0);

    public static native int connect(long var0, long var2);

    public static native int send(long var0, byte[] var2, int var3, int var4);

    public static native int sendb(long var0, ByteBuffer var2, int var3, int var4);

    public static native int sendib(long var0, ByteBuffer var2, int var3, int var4);

    public static native int sendbb(long var0, int var2, int var3);

    public static native int sendibb(long var0, int var2, int var3);

    public static native int sendv(long var0, byte[][] var2);

    public static native int sendto(long var0, long var2, int var4, byte[] var5, int var6, int var7);

    public static native int recv(long var0, byte[] var2, int var3, int var4);

    public static native int recvt(long var0, byte[] var2, int var3, int var4, long var5);

    public static native int recvb(long var0, ByteBuffer var2, int var3, int var4);

    public static native int recvbb(long var0, int var2, int var3);

    public static native int recvbt(long var0, ByteBuffer var2, int var3, int var4, long var5);

    public static native int recvbbt(long var0, int var2, int var3, long var4);

    public static native int recvfrom(long var0, long var2, int var4, byte[] var5, int var6, int var7);

    public static native int optSet(long var0, int var2, int var3);

    public static native int optGet(long var0, int var2) throws Exception;

    public static native int timeoutSet(long var0, long var2);

    public static native long timeoutGet(long var0) throws Exception;

    public static native long sendfile(long var0, long var2, byte[][] var4, byte[][] var5, long var6, long var8, int var10);

    public static native long sendfilen(long var0, long var2, long var4, long var6, int var8);

    public static native long pool(long var0) throws Exception;

    private static native long get(long var0, int var2);

    public static native void setsbb(long var0, ByteBuffer var2);

    public static native void setrbb(long var0, ByteBuffer var2);

    public static native int dataSet(long var0, String var2, Object var3);

    public static native Object dataGet(long var0, String var2);
}

