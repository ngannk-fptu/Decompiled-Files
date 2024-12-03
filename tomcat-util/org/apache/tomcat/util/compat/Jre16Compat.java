/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.Jre9Compat;
import org.apache.tomcat.util.res.StringManager;

class Jre16Compat
extends Jre9Compat {
    private static final Log log = LogFactory.getLog(Jre16Compat.class);
    private static final StringManager sm = StringManager.getManager(Jre16Compat.class);
    private static final Class<?> unixDomainSocketAddressClazz;
    private static final Method openServerSocketChannelFamilyMethod;
    private static final Method unixDomainSocketAddressOfMethod;
    private static final Method openSocketChannelFamilyMethod;

    Jre16Compat() {
    }

    static boolean isSupported() {
        return unixDomainSocketAddressClazz != null;
    }

    @Override
    public SocketAddress getUnixDomainSocketAddress(String path) {
        try {
            return (SocketAddress)unixDomainSocketAddressOfMethod.invoke(null, path);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public ServerSocketChannel openUnixDomainServerSocketChannel() {
        try {
            return (ServerSocketChannel)openServerSocketChannelFamilyMethod.invoke(null, StandardProtocolFamily.valueOf("UNIX"));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public SocketChannel openUnixDomainSocketChannel() {
        try {
            return (SocketChannel)openSocketChannelFamilyMethod.invoke(null, StandardProtocolFamily.valueOf("UNIX"));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static {
        Class<?> c1 = null;
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        try {
            c1 = Class.forName("java.net.UnixDomainSocketAddress");
            m1 = ServerSocketChannel.class.getMethod("open", ProtocolFamily.class);
            m2 = c1.getMethod("of", String.class);
            m3 = SocketChannel.class.getMethod("open", ProtocolFamily.class);
        }
        catch (ClassNotFoundException e) {
            log.debug((Object)sm.getString("jre16Compat.javaPre16"), (Throwable)e);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            log.error((Object)sm.getString("jre16Compat.unexpected"), (Throwable)e);
        }
        unixDomainSocketAddressClazz = c1;
        openServerSocketChannelFamilyMethod = m1;
        unixDomainSocketAddressOfMethod = m2;
        openSocketChannelFamilyMethod = m3;
    }
}

