/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.protocol;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.httpclient.ConnectTimeoutException;

public final class ReflectionSocketFactory {
    private static boolean REFLECTION_FAILED = false;
    private static Constructor INETSOCKETADDRESS_CONSTRUCTOR = null;
    private static Method SOCKETCONNECT_METHOD = null;
    private static Method SOCKETBIND_METHOD = null;
    private static Class SOCKETTIMEOUTEXCEPTION_CLASS = null;

    private ReflectionSocketFactory() {
    }

    public static Socket createSocket(String socketfactoryName, String host, int port, InetAddress localAddress, int localPort, int timeout) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (REFLECTION_FAILED) {
            return null;
        }
        try {
            Class<?> socketfactoryClass = Class.forName(socketfactoryName);
            Method method = socketfactoryClass.getMethod("getDefault", new Class[0]);
            Object socketfactory = method.invoke(null, new Object[0]);
            method = socketfactoryClass.getMethod("createSocket", new Class[0]);
            Socket socket = (Socket)method.invoke(socketfactory, new Object[0]);
            if (INETSOCKETADDRESS_CONSTRUCTOR == null) {
                Class<?> addressClass = Class.forName("java.net.InetSocketAddress");
                INETSOCKETADDRESS_CONSTRUCTOR = addressClass.getConstructor(InetAddress.class, Integer.TYPE);
            }
            Object remoteaddr = INETSOCKETADDRESS_CONSTRUCTOR.newInstance(InetAddress.getByName(host), new Integer(port));
            Object localaddr = INETSOCKETADDRESS_CONSTRUCTOR.newInstance(localAddress, new Integer(localPort));
            if (SOCKETCONNECT_METHOD == null) {
                SOCKETCONNECT_METHOD = Socket.class.getMethod("connect", Class.forName("java.net.SocketAddress"), Integer.TYPE);
            }
            if (SOCKETBIND_METHOD == null) {
                SOCKETBIND_METHOD = Socket.class.getMethod("bind", Class.forName("java.net.SocketAddress"));
            }
            SOCKETBIND_METHOD.invoke((Object)socket, localaddr);
            SOCKETCONNECT_METHOD.invoke((Object)socket, remoteaddr, new Integer(timeout));
            return socket;
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (SOCKETTIMEOUTEXCEPTION_CLASS == null) {
                try {
                    SOCKETTIMEOUTEXCEPTION_CLASS = Class.forName("java.net.SocketTimeoutException");
                }
                catch (ClassNotFoundException ex) {
                    REFLECTION_FAILED = true;
                    return null;
                }
            }
            if (SOCKETTIMEOUTEXCEPTION_CLASS.isInstance(cause)) {
                throw new ConnectTimeoutException("The host did not accept the connection within timeout of " + timeout + " ms", cause);
            }
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            return null;
        }
        catch (Exception e) {
            REFLECTION_FAILED = true;
            return null;
        }
    }
}

