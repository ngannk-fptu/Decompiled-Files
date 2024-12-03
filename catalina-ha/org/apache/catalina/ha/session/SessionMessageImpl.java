/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ha.session;

import org.apache.catalina.ha.ClusterMessageBase;
import org.apache.catalina.ha.session.SessionMessage;

public class SessionMessageImpl
extends ClusterMessageBase
implements SessionMessage {
    private static final long serialVersionUID = 2L;
    private final int mEvtType;
    private final byte[] mSession;
    private final String mSessionID;
    private final String mContextName;
    private long serializationTimestamp;
    private boolean timestampSet = false;
    private String uniqueId;

    private SessionMessageImpl(String contextName, int eventtype, byte[] session, String sessionID) {
        this.mEvtType = eventtype;
        this.mSession = session;
        this.mSessionID = sessionID;
        this.mContextName = contextName;
        this.uniqueId = sessionID;
    }

    public SessionMessageImpl(String contextName, int eventtype, byte[] session, String sessionID, String uniqueID) {
        this(contextName, eventtype, session, sessionID);
        this.uniqueId = uniqueID;
    }

    @Override
    public int getEventType() {
        return this.mEvtType;
    }

    @Override
    public byte[] getSession() {
        return this.mSession;
    }

    @Override
    public String getSessionID() {
        return this.mSessionID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTimestamp(long time) {
        SessionMessageImpl sessionMessageImpl = this;
        synchronized (sessionMessageImpl) {
            if (!this.timestampSet) {
                this.serializationTimestamp = time;
                this.timestampSet = true;
            }
        }
    }

    @Override
    public long getTimestamp() {
        return this.serializationTimestamp;
    }

    @Override
    public String getEventTypeString() {
        switch (this.mEvtType) {
            case 1: {
                return "SESSION-MODIFIED";
            }
            case 2: {
                return "SESSION-EXPIRED";
            }
            case 3: {
                return "SESSION-ACCESSED";
            }
            case 4: {
                return "SESSION-GET-ALL";
            }
            case 13: {
                return "SESSION-DELTA";
            }
            case 12: {
                return "ALL-SESSION-DATA";
            }
            case 14: {
                return "SESSION-STATE-TRANSFERRED";
            }
            case 15: {
                return "SESSION-ID-CHANGED";
            }
            case 16: {
                return "NO-CONTEXT-MANAGER";
            }
        }
        return "UNKNOWN-EVENT-TYPE";
    }

    @Override
    public String getContextName() {
        return this.mContextName;
    }

    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }

    public String toString() {
        return this.getEventTypeString() + "#" + this.getContextName() + "#" + this.getSessionID();
    }
}

