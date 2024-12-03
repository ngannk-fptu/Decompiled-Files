/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.util.TimeoutController;

public final class ControllerThreadSocketFactory {
    private ControllerThreadSocketFactory() {
    }

    public static Socket createSocket(final ProtocolSocketFactory socketfactory, final String host, final int port, final InetAddress localAddress, final int localPort, int timeout) throws IOException, UnknownHostException, ConnectTimeoutException {
        SocketTask task = new SocketTask(){

            @Override
            public void doit() throws IOException {
                this.setSocket(socketfactory.createSocket(host, port, localAddress, localPort));
            }
        };
        try {
            TimeoutController.execute(task, (long)timeout);
        }
        catch (TimeoutController.TimeoutException e) {
            throw new ConnectTimeoutException("The host did not accept the connection within timeout of " + timeout + " ms");
        }
        Socket socket = task.getSocket();
        if (task.exception != null) {
            throw task.exception;
        }
        return socket;
    }

    public static Socket createSocket(SocketTask task, int timeout) throws IOException, UnknownHostException, ConnectTimeoutException {
        try {
            TimeoutController.execute(task, (long)timeout);
        }
        catch (TimeoutController.TimeoutException e) {
            throw new ConnectTimeoutException("The host did not accept the connection within timeout of " + timeout + " ms");
        }
        Socket socket = task.getSocket();
        if (task.exception != null) {
            throw task.exception;
        }
        return socket;
    }

    public static abstract class SocketTask
    implements Runnable {
        private Socket socket;
        private IOException exception;

        protected void setSocket(Socket newSocket) {
            this.socket = newSocket;
        }

        protected Socket getSocket() {
            return this.socket;
        }

        public abstract void doit() throws IOException;

        @Override
        public void run() {
            try {
                this.doit();
            }
            catch (IOException e) {
                this.exception = e;
            }
        }
    }
}

