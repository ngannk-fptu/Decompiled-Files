/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11.upgrade;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import org.apache.coyote.Request;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class UpgradeServletOutputStream
extends ServletOutputStream {
    private static final Log log = LogFactory.getLog(UpgradeServletOutputStream.class);
    private static final StringManager sm = StringManager.getManager(UpgradeServletOutputStream.class);
    private final UpgradeProcessorBase processor;
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private final Object registeredLock = new Object();
    private final Object writeLock = new Object();
    private volatile boolean flushing = false;
    private volatile boolean closed = false;
    private volatile WriteListener listener = null;
    private boolean registered = false;

    public UpgradeServletOutputStream(UpgradeProcessorBase processor, SocketWrapperBase<?> socketWrapper, UpgradeInfo upgradeInfo) {
        this.processor = processor;
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean isReady() {
        if (this.listener == null) {
            throw new IllegalStateException(sm.getString("upgrade.sos.canWrite.ise"));
        }
        if (this.closed) {
            return false;
        }
        Object object = this.registeredLock;
        synchronized (object) {
            if (this.flushing) {
                this.registered = true;
                return false;
            }
            if (this.registered) {
                return false;
            }
            boolean result = this.socketWrapper.isReadyForWrite();
            this.registered = !result;
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setWriteListener(WriteListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sos.writeListener.null"));
        }
        if (this.listener != null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sos.writeListener.set"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sos.write.closed"));
        }
        this.listener = listener;
        Object object = this.registeredLock;
        synchronized (object) {
            this.registered = true;
            Request request = this.processor.getRequest();
            if (request != null && request.isRequestThread()) {
                this.processor.addDispatch(DispatchType.NON_BLOCKING_WRITE);
            } else {
                this.socketWrapper.registerWriteInterest();
            }
        }
    }

    final boolean isClosed() {
        return this.closed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(int b) throws IOException {
        Object object = this.writeLock;
        synchronized (object) {
            this.preWriteChecks();
            this.writeInternal(new byte[]{(byte)b}, 0, 1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(byte[] b, int off, int len) throws IOException {
        Object object = this.writeLock;
        synchronized (object) {
            this.preWriteChecks();
            this.writeInternal(b, off, len);
        }
    }

    public void flush() throws IOException {
        this.preWriteChecks();
        this.flushInternal(this.listener == null, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void flushInternal(boolean block, boolean updateFlushing) throws IOException {
        try {
            Object object = this.writeLock;
            synchronized (object) {
                if (updateFlushing) {
                    this.flushing = this.socketWrapper.flush(block);
                    if (this.flushing) {
                        this.socketWrapper.registerWriteInterest();
                    }
                } else {
                    this.socketWrapper.flush(block);
                }
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.onError(t);
            if (t instanceof IOException) {
                throw (IOException)t;
            }
            throw new IOException(t);
        }
    }

    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.flushInternal(this.listener == null, false);
    }

    private void preWriteChecks() {
        if (this.listener != null && !this.socketWrapper.canWrite()) {
            throw new IllegalStateException(sm.getString("upgrade.sos.write.ise"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sos.write.closed"));
        }
    }

    private void writeInternal(byte[] b, int off, int len) throws IOException {
        if (this.listener == null) {
            this.socketWrapper.write(true, b, off, len);
        } else {
            this.socketWrapper.write(false, b, off, len);
        }
        this.upgradeInfo.addBytesSent(len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void onWritePossible() {
        try {
            if (this.flushing) {
                this.flushInternal(false, true);
                if (this.flushing) {
                    return;
                }
            } else {
                this.flushInternal(false, false);
            }
        }
        catch (IOException ioe) {
            this.onError(ioe);
            return;
        }
        boolean fire = false;
        Object object = this.registeredLock;
        synchronized (object) {
            if (this.socketWrapper.isReadyForWrite()) {
                this.registered = false;
                fire = true;
            } else {
                this.registered = true;
            }
        }
        if (fire) {
            ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
            try {
                this.listener.onWritePossible();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.onError(t);
            }
            finally {
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onError(Throwable t) {
        block9: {
            if (this.listener == null) {
                return;
            }
            ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
            try {
                this.listener.onError(t);
            }
            catch (Throwable t2) {
                ExceptionUtils.handleThrowable((Throwable)t2);
                log.warn((Object)sm.getString("upgrade.sos.onErrorFail"), t2);
            }
            finally {
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            }
            try {
                this.close();
            }
            catch (IOException ioe) {
                if (!log.isDebugEnabled()) break block9;
                log.debug((Object)sm.getString("upgrade.sos.errorCloseFail"), (Throwable)ioe);
            }
        }
    }
}

