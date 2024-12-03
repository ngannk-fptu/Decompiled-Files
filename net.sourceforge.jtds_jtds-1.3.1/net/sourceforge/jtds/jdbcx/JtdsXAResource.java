/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.XASupport;
import net.sourceforge.jtds.jdbcx.JtdsXAConnection;
import net.sourceforge.jtds.util.Logger;

public class JtdsXAResource
implements XAResource {
    private final Connection connection;
    private final JtdsXAConnection xaConnection;
    private final String rmHost;

    public JtdsXAResource(JtdsXAConnection xaConnection, Connection connection) {
        this.xaConnection = xaConnection;
        this.connection = connection;
        this.rmHost = ((JtdsConnection)connection).getRmHost();
        Logger.println("JtdsXAResource created");
    }

    protected JtdsXAConnection getResourceManager() {
        return this.xaConnection;
    }

    protected String getRmHost() {
        return this.rmHost;
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        Logger.println("XAResource.getTransactionTimeout()");
        return 0;
    }

    @Override
    public boolean setTransactionTimeout(int arg0) throws XAException {
        Logger.println("XAResource.setTransactionTimeout(" + arg0 + ')');
        return false;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        Logger.println("XAResource.isSameRM(" + xares.toString() + ')');
        return xares instanceof JtdsXAResource && ((JtdsXAResource)xares).getRmHost().equals(this.rmHost);
    }

    @Override
    public Xid[] recover(int flags) throws XAException {
        Logger.println("XAResource.recover(" + flags + ')');
        return XASupport.xa_recover(this.connection, this.xaConnection.getXAConnectionID(), flags);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        Logger.println("XAResource.prepare(" + xid.toString() + ')');
        return XASupport.xa_prepare(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        Logger.println("XAResource.forget(" + xid + ')');
        XASupport.xa_forget(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        Logger.println("XAResource.rollback(" + xid.toString() + ')');
        XASupport.xa_rollback(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        Logger.println("XAResource.end(" + xid.toString() + ')');
        XASupport.xa_end(this.connection, this.xaConnection.getXAConnectionID(), xid, flags);
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        Logger.println("XAResource.start(" + xid.toString() + ',' + flags + ')');
        XASupport.xa_start(this.connection, this.xaConnection.getXAConnectionID(), xid, flags);
    }

    @Override
    public void commit(Xid xid, boolean commit) throws XAException {
        Logger.println("XAResource.commit(" + xid.toString() + ',' + commit + ')');
        XASupport.xa_commit(this.connection, this.xaConnection.getXAConnectionID(), xid, commit);
    }
}

