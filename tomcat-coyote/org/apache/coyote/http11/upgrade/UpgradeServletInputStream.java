/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletInputStream
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11.upgrade;

import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.apache.coyote.Request;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class UpgradeServletInputStream
extends ServletInputStream {
    private static final Log log = LogFactory.getLog(UpgradeServletInputStream.class);
    private static final StringManager sm = StringManager.getManager(UpgradeServletInputStream.class);
    private final UpgradeProcessorBase processor;
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private volatile boolean closed = false;
    private volatile boolean eof = false;
    private volatile Boolean ready = Boolean.TRUE;
    private volatile ReadListener listener = null;

    public UpgradeServletInputStream(UpgradeProcessorBase processor, SocketWrapperBase<?> socketWrapper, UpgradeInfo upgradeInfo) {
        this.processor = processor;
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
    }

    public final boolean isFinished() {
        if (this.listener == null) {
            throw new IllegalStateException(sm.getString("upgrade.sis.isFinished.ise"));
        }
        return this.eof;
    }

    public final boolean isReady() {
        if (this.listener == null) {
            throw new IllegalStateException(sm.getString("upgrade.sis.isReady.ise"));
        }
        if (this.eof || this.closed) {
            return false;
        }
        if (this.ready != null) {
            return this.ready;
        }
        try {
            this.ready = this.socketWrapper.isReadyForRead();
        }
        catch (IOException e) {
            this.onError(e);
        }
        return this.ready;
    }

    public final void setReadListener(ReadListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sis.readListener.null"));
        }
        if (this.listener != null) {
            throw new IllegalArgumentException(sm.getString("upgrade.sis.readListener.set"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sis.read.closed"));
        }
        this.listener = listener;
        Request request = this.processor.getRequest();
        if (request != null && request.isRequestThread()) {
            this.processor.addDispatch(DispatchType.NON_BLOCKING_READ);
        } else {
            this.socketWrapper.registerReadInterest();
        }
        this.ready = null;
    }

    public final int read() throws IOException {
        this.preReadChecks();
        return this.readInternal();
    }

    public final int readLine(byte[] b, int off, int len) throws IOException {
        int c;
        this.preReadChecks();
        if (len <= 0) {
            return 0;
        }
        int count = 0;
        while ((c = this.readInternal()) != -1) {
            b[off++] = (byte)c;
            if (c != 10 && ++count != len) continue;
        }
        if (count > 0) {
            this.upgradeInfo.addBytesReceived(count);
            return count;
        }
        return -1;
    }

    public final int read(byte[] b, int off, int len) throws IOException {
        this.preReadChecks();
        try {
            int result = this.socketWrapper.read(this.listener == null, b, off, len);
            if (result == -1) {
                this.eof = true;
            } else {
                this.upgradeInfo.addBytesReceived(result);
            }
            return result;
        }
        catch (IOException ioe) {
            this.close();
            throw ioe;
        }
    }

    public void close() throws IOException {
        this.eof = true;
        this.closed = true;
    }

    private void preReadChecks() {
        if (!(this.listener == null || this.ready != null && this.ready.booleanValue())) {
            throw new IllegalStateException(sm.getString("upgrade.sis.read.ise"));
        }
        if (this.closed) {
            throw new IllegalStateException(sm.getString("upgrade.sis.read.closed"));
        }
        this.ready = null;
    }

    private int readInternal() throws IOException {
        int result;
        byte[] b = new byte[1];
        try {
            result = this.socketWrapper.read(this.listener == null, b, 0, 1);
        }
        catch (IOException ioe) {
            this.close();
            throw ioe;
        }
        if (result == 0) {
            return -1;
        }
        if (result == -1) {
            this.eof = true;
            return -1;
        }
        this.upgradeInfo.addBytesReceived(1L);
        return b[0] & 0xFF;
    }

    final void onDataAvailable() {
        try {
            if (this.listener == null || !this.socketWrapper.isReadyForRead()) {
                return;
            }
        }
        catch (IOException e) {
            this.onError(e);
        }
        this.ready = Boolean.TRUE;
        ClassLoader oldCL = this.processor.getUpgradeToken().getContextBind().bind(false, null);
        try {
            if (!this.eof) {
                this.listener.onDataAvailable();
            }
            if (this.eof) {
                this.listener.onAllDataRead();
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.onError(t);
        }
        finally {
            this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
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
                log.warn((Object)sm.getString("upgrade.sis.onErrorFail"), t2);
            }
            finally {
                this.processor.getUpgradeToken().getContextBind().unbind(false, oldCL);
            }
            try {
                this.close();
            }
            catch (IOException ioe) {
                if (!log.isDebugEnabled()) break block9;
                log.debug((Object)sm.getString("upgrade.sis.errorCloseFail"), (Throwable)ioe);
            }
        }
        this.ready = Boolean.FALSE;
    }

    final boolean isClosed() {
        return this.closed;
    }
}

