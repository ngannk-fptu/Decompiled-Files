/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.RowCallbackHandler
 */
package com.atlassian.confluence.upgrade.upgradetask;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

public abstract class SessionClearingRowCallbackHandler
implements RowCallbackHandler {
    private static final Logger log = LoggerFactory.getLogger(SessionClearingRowCallbackHandler.class);
    private final int flushAndClearEvery;
    private final int expectedTotal;
    private final Session session;

    public SessionClearingRowCallbackHandler(Session session, int flushAndClearEvery, int expectedTotal) {
        this.flushAndClearEvery = flushAndClearEvery;
        this.expectedTotal = expectedTotal;
        this.session = session;
    }

    public final void processRow(ResultSet rs) throws SQLException {
        this.processRowInternal(rs);
        if (rs.getRow() % this.flushAndClearEvery == 0) {
            log.info("Processed {} of {}...", (Object)rs.getRow(), (Object)this.expectedTotal);
            SessionClearingRowCallbackHandler.flushAndClear(this.session);
        }
    }

    protected static void flushAndClear(Session session) {
        session.flush();
        session.clear();
    }

    protected abstract void processRowInternal(ResultSet var1) throws SQLException;
}

