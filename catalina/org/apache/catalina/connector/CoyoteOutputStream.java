/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import org.apache.catalina.connector.OutputBuffer;
import org.apache.tomcat.util.res.StringManager;

public class CoyoteOutputStream
extends ServletOutputStream {
    protected static final StringManager sm = StringManager.getManager(CoyoteOutputStream.class);
    protected OutputBuffer ob;

    protected CoyoteOutputStream(OutputBuffer ob) {
        this.ob = ob;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    void clear() {
        this.ob = null;
    }

    public void write(int i) throws IOException {
        boolean nonBlocking = this.checkNonBlockingWrite();
        this.ob.writeByte(i);
        if (nonBlocking) {
            this.checkRegisterForWrite();
        }
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        boolean nonBlocking = this.checkNonBlockingWrite();
        this.ob.write(b, off, len);
        if (nonBlocking) {
            this.checkRegisterForWrite();
        }
    }

    public void write(ByteBuffer from) throws IOException {
        Objects.requireNonNull(from);
        boolean nonBlocking = this.checkNonBlockingWrite();
        this.ob.write(from);
        if (nonBlocking) {
            this.checkRegisterForWrite();
        }
    }

    public void flush() throws IOException {
        boolean nonBlocking = this.checkNonBlockingWrite();
        this.ob.flush();
        if (nonBlocking) {
            this.checkRegisterForWrite();
        }
    }

    private boolean checkNonBlockingWrite() {
        boolean nonBlocking;
        boolean bl = nonBlocking = !this.ob.isBlocking();
        if (nonBlocking && !this.ob.isReady()) {
            throw new IllegalStateException(sm.getString("coyoteOutputStream.nbNotready"));
        }
        return nonBlocking;
    }

    private void checkRegisterForWrite() {
        this.ob.checkRegisterForWrite();
    }

    public void close() throws IOException {
        this.ob.close();
    }

    public boolean isReady() {
        if (this.ob == null) {
            throw new IllegalStateException(sm.getString("coyoteOutputStream.null"));
        }
        return this.ob.isReady();
    }

    public void setWriteListener(WriteListener listener) {
        this.ob.setWriteListener(listener);
    }
}

