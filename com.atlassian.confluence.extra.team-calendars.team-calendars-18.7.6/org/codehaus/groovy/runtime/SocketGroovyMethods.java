/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class SocketGroovyMethods
extends DefaultGroovyMethodsSupport {
    private static final Logger LOG = Logger.getLogger(SocketGroovyMethods.class.getName());

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T withStreams(Socket socket, @ClosureParams(value=SimpleType.class, options={"java.io.InputStream", "java.io.OutputStream"}) Closure<T> closure) throws IOException {
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        try {
            T result = closure.call(input, output);
            InputStream temp1 = input;
            input = null;
            temp1.close();
            OutputStream temp2 = output;
            output = null;
            temp2.close();
            T t = result;
            return t;
        }
        finally {
            SocketGroovyMethods.closeWithWarning(input);
            SocketGroovyMethods.closeWithWarning(output);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T withObjectStreams(Socket socket, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream", "java.io.ObjectOutputStream"}) Closure<T> closure) throws IOException {
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        ObjectInputStream ois = new ObjectInputStream(input);
        try {
            T result = closure.call(ois, oos);
            InputStream temp1 = ois;
            ois = null;
            temp1.close();
            temp1 = input;
            input = null;
            temp1.close();
            OutputStream temp2 = oos;
            oos = null;
            temp2.close();
            temp2 = output;
            output = null;
            temp2.close();
            T t = result;
            return t;
        }
        finally {
            SocketGroovyMethods.closeWithWarning(ois);
            SocketGroovyMethods.closeWithWarning(input);
            SocketGroovyMethods.closeWithWarning(oos);
            SocketGroovyMethods.closeWithWarning(output);
        }
    }

    public static Writer leftShift(Socket self, Object value) throws IOException {
        return IOGroovyMethods.leftShift(self.getOutputStream(), value);
    }

    public static OutputStream leftShift(Socket self, byte[] value) throws IOException {
        return IOGroovyMethods.leftShift(self.getOutputStream(), value);
    }

    public static Socket accept(ServerSocket serverSocket, @ClosureParams(value=SimpleType.class, options={"java.net.Socket"}) Closure closure) throws IOException {
        return SocketGroovyMethods.accept(serverSocket, true, closure);
    }

    public static Socket accept(ServerSocket serverSocket, boolean runInANewThread, final @ClosureParams(value=SimpleType.class, options={"java.net.Socket"}) Closure closure) throws IOException {
        final Socket socket = serverSocket.accept();
        if (runInANewThread) {
            new Thread(new Runnable(){

                @Override
                public void run() {
                    SocketGroovyMethods.invokeClosureWithSocket(socket, closure);
                }
            }).start();
        } else {
            SocketGroovyMethods.invokeClosureWithSocket(socket, closure);
        }
        return socket;
    }

    private static void invokeClosureWithSocket(Socket socket, Closure closure) {
        try {
            closure.call((Object)socket);
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    LOG.warning("Caught exception closing socket: " + e);
                }
            }
        }
    }
}

