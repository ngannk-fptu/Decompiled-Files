/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.processor;

import javax.transaction.xa.Xid;

public class XARequest {
    private final RequestType requestType;
    private final Xid xid;
    private final boolean onePhase;

    public XARequest(RequestType requestType, Xid xid) {
        this(requestType, xid, false);
    }

    public XARequest(RequestType requestType, Xid xid, boolean onePhase) {
        this.requestType = requestType;
        this.xid = xid;
        this.onePhase = onePhase;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public Xid getXid() {
        return this.xid;
    }

    public boolean isOnePhase() {
        return this.onePhase;
    }

    public static enum RequestType {
        PREPARE,
        COMMIT,
        FORGET,
        ROLLBACK;

    }
}

