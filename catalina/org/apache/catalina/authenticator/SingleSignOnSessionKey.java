/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.authenticator;

import java.io.Serializable;
import org.apache.catalina.Context;
import org.apache.catalina.Session;

public class SingleSignOnSessionKey
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String sessionId;
    private final String contextName;
    private final String hostName;

    public SingleSignOnSessionKey(Session session) {
        this.sessionId = session.getId();
        Context context = session.getManager().getContext();
        this.contextName = context.getName();
        this.hostName = context.getParent().getName();
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getContextName() {
        return this.contextName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.sessionId == null ? 0 : this.sessionId.hashCode());
        result = 31 * result + (this.contextName == null ? 0 : this.contextName.hashCode());
        result = 31 * result + (this.hostName == null ? 0 : this.hostName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SingleSignOnSessionKey other = (SingleSignOnSessionKey)obj;
        if (this.sessionId == null ? other.sessionId != null : !this.sessionId.equals(other.sessionId)) {
            return false;
        }
        if (this.contextName == null ? other.contextName != null : !this.contextName.equals(other.contextName)) {
            return false;
        }
        return !(this.hostName == null ? other.hostName != null : !this.hostName.equals(other.hostName));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Host: [");
        sb.append(this.hostName);
        sb.append("], Context: [");
        sb.append(this.contextName);
        sb.append("], SessionID: [");
        sb.append(this.sessionId);
        sb.append(']');
        return sb.toString();
    }
}

