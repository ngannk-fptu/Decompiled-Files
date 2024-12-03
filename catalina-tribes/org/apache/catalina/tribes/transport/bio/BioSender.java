/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import org.apache.catalina.tribes.RemoteProcessException;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.Constants;
import org.apache.catalina.tribes.transport.SenderState;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

@Deprecated
public class BioSender
extends AbstractSender {
    private static final Log log = LogFactory.getLog(BioSender.class);
    protected static final StringManager sm = StringManager.getManager(BioSender.class);
    private Socket socket = null;
    private OutputStream soOut = null;
    private InputStream soIn = null;
    protected final XByteBuffer ackbuf = new XByteBuffer(Constants.ACK_COMMAND.length, true);

    @Override
    public void connect() throws IOException {
        this.openSocket();
    }

    @Override
    public void disconnect() {
        boolean connect = this.isConnected();
        this.closeSocket();
        if (connect && log.isDebugEnabled()) {
            log.debug((Object)sm.getString("bioSender.disconnect", this.getAddress().getHostAddress(), this.getPort(), 0L));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendMessage(byte[] data, boolean waitForAck) throws IOException {
        block9: {
            IOException exception = null;
            this.setAttempt(0);
            try {
                this.pushMessage(data, false, waitForAck);
            }
            catch (IOException x) {
                SenderState.getSenderState(this.getDestination()).setSuspect();
                exception = x;
                if (log.isTraceEnabled()) {
                    log.trace((Object)sm.getString("bioSender.send.again", this.getAddress().getHostAddress(), this.getPort()), (Throwable)x);
                }
                while (this.getAttempt() < this.getMaxRetryAttempts()) {
                    try {
                        this.setAttempt(this.getAttempt() + 1);
                        this.pushMessage(data, true, waitForAck);
                        exception = null;
                    }
                    catch (IOException xx) {
                        exception = xx;
                        this.closeSocket();
                    }
                }
            }
            finally {
                this.setRequestCount(this.getRequestCount() + 1);
                this.keepalive();
                if (exception == null) break block9;
                throw exception;
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("DataSender[(");
        buf.append(super.toString()).append(')');
        buf.append(this.getAddress()).append(':').append(this.getPort()).append(']');
        return buf.toString();
    }

    protected void openSocket() throws IOException {
        if (this.isConnected()) {
            return;
        }
        try {
            this.socket = new Socket();
            InetSocketAddress sockaddr = new InetSocketAddress(this.getAddress(), this.getPort());
            this.socket.connect(sockaddr, (int)this.getTimeout());
            this.socket.setSendBufferSize(this.getTxBufSize());
            this.socket.setReceiveBufferSize(this.getRxBufSize());
            this.socket.setSoTimeout((int)this.getTimeout());
            this.socket.setTcpNoDelay(this.getTcpNoDelay());
            this.socket.setKeepAlive(this.getSoKeepAlive());
            this.socket.setReuseAddress(this.getSoReuseAddress());
            this.socket.setOOBInline(this.getOoBInline());
            this.socket.setSoLinger(this.getSoLingerOn(), this.getSoLingerTime());
            this.socket.setTrafficClass(this.getSoTrafficClass());
            this.setConnected(true);
            this.soOut = this.socket.getOutputStream();
            this.soIn = this.socket.getInputStream();
            this.setRequestCount(0);
            this.setConnectTime(System.currentTimeMillis());
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("bioSender.openSocket", this.getAddress().getHostAddress(), this.getPort(), 0L));
            }
        }
        catch (IOException ex1) {
            SenderState.getSenderState(this.getDestination()).setSuspect();
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("bioSender.openSocket.failure", this.getAddress().getHostAddress(), this.getPort(), 0L), (Throwable)ex1);
            }
            throw ex1;
        }
    }

    protected void closeSocket() {
        if (this.isConnected()) {
            if (this.socket != null) {
                try {
                    this.socket.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    this.socket = null;
                    this.soOut = null;
                    this.soIn = null;
                }
            }
            this.setRequestCount(0);
            this.setConnected(false);
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("bioSender.closeSocket", this.getAddress().getHostAddress(), this.getPort(), 0L));
            }
        }
    }

    protected void pushMessage(byte[] data, boolean reconnect, boolean waitForAck) throws IOException {
        this.keepalive();
        if (reconnect) {
            this.closeSocket();
        }
        if (!this.isConnected()) {
            this.openSocket();
        }
        this.soOut.write(data);
        this.soOut.flush();
        if (waitForAck) {
            this.waitForAck();
        }
        SenderState.getSenderState(this.getDestination()).setReady();
    }

    protected void waitForAck() throws IOException {
        try {
            boolean ackReceived = false;
            boolean failAckReceived = false;
            this.ackbuf.clear();
            int i = this.soIn.read();
            for (int bytesRead = 0; i != -1 && bytesRead < Constants.ACK_COMMAND.length; ++bytesRead) {
                byte d = (byte)i;
                this.ackbuf.append(d);
                if (this.ackbuf.doesPackageExist()) {
                    byte[] ackcmd = this.ackbuf.extractDataPackage(true).getBytes();
                    ackReceived = Arrays.equals(ackcmd, Constants.ACK_DATA);
                    failAckReceived = Arrays.equals(ackcmd, Constants.FAIL_ACK_DATA);
                    ackReceived = ackReceived || failAckReceived;
                    break;
                }
                i = this.soIn.read();
            }
            if (!ackReceived) {
                if (i == -1) {
                    throw new IOException(sm.getString("bioSender.ack.eof", this.getAddress(), this.socket.getLocalPort()));
                }
                throw new IOException(sm.getString("bioSender.ack.wrong", this.getAddress(), this.socket.getLocalPort()));
            }
            if (failAckReceived && this.getThrowOnFailedAck()) {
                throw new RemoteProcessException(sm.getString("bioSender.fail.AckReceived"));
            }
        }
        catch (IOException x) {
            String errmsg = sm.getString("bioSender.ack.missing", this.getAddress(), this.socket.getLocalPort(), this.getTimeout());
            if (SenderState.getSenderState(this.getDestination()).isReady()) {
                SenderState.getSenderState(this.getDestination()).setSuspect();
                if (log.isWarnEnabled()) {
                    log.warn((Object)errmsg, (Throwable)x);
                }
            } else if (log.isDebugEnabled()) {
                log.debug((Object)errmsg, (Throwable)x);
            }
            throw x;
        }
        finally {
            this.ackbuf.clear();
        }
    }
}

