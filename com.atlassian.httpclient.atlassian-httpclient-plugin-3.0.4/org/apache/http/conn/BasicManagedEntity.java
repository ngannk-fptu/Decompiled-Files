/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.http.HttpEntity;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Deprecated
public class BasicManagedEntity
extends HttpEntityWrapper
implements ConnectionReleaseTrigger,
EofSensorWatcher {
    protected ManagedClientConnection managedConn;
    protected final boolean attemptReuse;

    public BasicManagedEntity(HttpEntity entity, ManagedClientConnection conn, boolean reuse) {
        super(entity);
        Args.notNull(conn, "Connection");
        this.managedConn = conn;
        this.attemptReuse = reuse;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public InputStream getContent() throws IOException {
        return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureConsumed() throws IOException {
        if (this.managedConn == null) {
            return;
        }
        try {
            if (this.attemptReuse) {
                EntityUtils.consume(this.wrappedEntity);
                this.managedConn.markReusable();
            } else {
                this.managedConn.unmarkReusable();
            }
        }
        finally {
            this.releaseManagedConnection();
        }
    }

    @Override
    @Deprecated
    public void consumeContent() throws IOException {
        this.ensureConsumed();
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        super.writeTo(outStream);
        this.ensureConsumed();
    }

    @Override
    public void releaseConnection() throws IOException {
        this.ensureConsumed();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void abortConnection() throws IOException {
        if (this.managedConn != null) {
            try {
                this.managedConn.abortConnection();
            }
            finally {
                this.managedConn = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean eofDetected(InputStream wrapped) throws IOException {
        try {
            if (this.managedConn != null) {
                if (this.attemptReuse) {
                    wrapped.close();
                    this.managedConn.markReusable();
                } else {
                    this.managedConn.unmarkReusable();
                }
            }
        }
        finally {
            this.releaseManagedConnection();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean streamClosed(InputStream wrapped) throws IOException {
        block7: {
            try {
                if (this.managedConn == null) break block7;
                if (this.attemptReuse) {
                    boolean valid = this.managedConn.isOpen();
                    try {
                        wrapped.close();
                        this.managedConn.markReusable();
                        break block7;
                    }
                    catch (SocketException ex) {
                        if (valid) {
                            throw ex;
                        }
                        break block7;
                    }
                }
                this.managedConn.unmarkReusable();
            }
            finally {
                this.releaseManagedConnection();
            }
        }
        return false;
    }

    @Override
    public boolean streamAbort(InputStream wrapped) throws IOException {
        if (this.managedConn != null) {
            this.managedConn.abortConnection();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void releaseManagedConnection() throws IOException {
        if (this.managedConn != null) {
            try {
                this.managedConn.releaseConnection();
            }
            finally {
                this.managedConn = null;
            }
        }
    }
}

