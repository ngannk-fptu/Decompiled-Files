/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletInputStream
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.apache.catalina.connector.InputBuffer;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

public class CoyoteInputStream
extends ServletInputStream {
    protected static final StringManager sm = StringManager.getManager(CoyoteInputStream.class);
    protected InputBuffer ib;

    protected CoyoteInputStream(InputBuffer ib) {
        this.ib = ib;
    }

    void clear() {
        this.ib = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public int read() throws IOException {
        this.checkNonBlockingRead();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = AccessController.doPrivileged(new PrivilegedRead(this.ib));
                return result;
            }
            catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.readByte();
    }

    public int available() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = AccessController.doPrivileged(new PrivilegedAvailable(this.ib));
                return result;
            }
            catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.available();
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        this.checkNonBlockingRead();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = AccessController.doPrivileged(new PrivilegedReadArray(this.ib, b, off, len));
                return result;
            }
            catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.read(b, off, len);
    }

    public int read(ByteBuffer b) throws IOException {
        Objects.requireNonNull(b);
        this.checkNonBlockingRead();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                Integer result = AccessController.doPrivileged(new PrivilegedReadBuffer(this.ib, b));
                return result;
            }
            catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return this.ib.read(b);
    }

    public void close() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedClose(this.ib));
            }
            catch (PrivilegedActionException pae) {
                Exception e = pae.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            this.ib.close();
        }
    }

    public boolean isFinished() {
        return this.ib.isFinished();
    }

    public boolean isReady() {
        if (this.ib == null) {
            throw new IllegalStateException(sm.getString("coyoteInputStream.null"));
        }
        return this.ib.isReady();
    }

    public void setReadListener(ReadListener listener) {
        this.ib.setReadListener(listener);
    }

    private void checkNonBlockingRead() {
        if (!this.ib.isBlocking() && !this.ib.isReady()) {
            throw new IllegalStateException(sm.getString("coyoteInputStream.nbNotready"));
        }
    }

    private static class PrivilegedRead
    implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;

        PrivilegedRead(InputBuffer inputBuffer) {
            this.inputBuffer = inputBuffer;
        }

        @Override
        public Integer run() throws IOException {
            Integer integer = this.inputBuffer.readByte();
            return integer;
        }
    }

    private static class PrivilegedAvailable
    implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;

        PrivilegedAvailable(InputBuffer inputBuffer) {
            this.inputBuffer = inputBuffer;
        }

        @Override
        public Integer run() throws IOException {
            return this.inputBuffer.available();
        }
    }

    private static class PrivilegedReadArray
    implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;
        private final byte[] buf;
        private final int off;
        private final int len;

        PrivilegedReadArray(InputBuffer inputBuffer, byte[] buf, int off, int len) {
            this.inputBuffer = inputBuffer;
            this.buf = buf;
            this.off = off;
            this.len = len;
        }

        @Override
        public Integer run() throws IOException {
            Integer integer = this.inputBuffer.read(this.buf, this.off, this.len);
            return integer;
        }
    }

    private static class PrivilegedReadBuffer
    implements PrivilegedExceptionAction<Integer> {
        private final InputBuffer inputBuffer;
        private final ByteBuffer bb;

        PrivilegedReadBuffer(InputBuffer inputBuffer, ByteBuffer bb) {
            this.inputBuffer = inputBuffer;
            this.bb = bb;
        }

        @Override
        public Integer run() throws IOException {
            Integer integer = this.inputBuffer.read(this.bb);
            return integer;
        }
    }

    private static class PrivilegedClose
    implements PrivilegedExceptionAction<Void> {
        private final InputBuffer inputBuffer;

        PrivilegedClose(InputBuffer inputBuffer) {
            this.inputBuffer = inputBuffer;
        }

        @Override
        public Void run() throws IOException {
            this.inputBuffer.close();
            return null;
        }
    }
}

