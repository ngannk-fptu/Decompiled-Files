/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.SessionListener
 *  org.apache.catalina.realm.GenericPrincipal
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.session;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import org.apache.catalina.SessionListener;
import org.apache.catalina.ha.session.DeltaSession;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class DeltaRequest
implements Externalizable {
    public static final Log log = LogFactory.getLog(DeltaRequest.class);
    protected static final StringManager sm = StringManager.getManager(DeltaRequest.class);
    public static final int TYPE_ATTRIBUTE = 0;
    public static final int TYPE_PRINCIPAL = 1;
    public static final int TYPE_ISNEW = 2;
    public static final int TYPE_MAXINTERVAL = 3;
    public static final int TYPE_AUTHTYPE = 4;
    public static final int TYPE_LISTENER = 5;
    public static final int TYPE_NOTE = 6;
    public static final int ACTION_SET = 0;
    public static final int ACTION_REMOVE = 1;
    public static final String NAME_PRINCIPAL = "__SET__PRINCIPAL__";
    public static final String NAME_MAXINTERVAL = "__SET__MAXINTERVAL__";
    public static final String NAME_ISNEW = "__SET__ISNEW__";
    public static final String NAME_AUTHTYPE = "__SET__AUTHTYPE__";
    public static final String NAME_LISTENER = "__SET__LISTENER__";
    private String sessionId;
    private final Deque<AttributeInfo> actions = new ArrayDeque<AttributeInfo>();
    private final Deque<AttributeInfo> actionPool = new ArrayDeque<AttributeInfo>();
    private boolean recordAllActions = false;

    public DeltaRequest() {
    }

    public DeltaRequest(String sessionId, boolean recordAllActions) {
        this.recordAllActions = recordAllActions;
        if (sessionId != null) {
            this.setSessionId(sessionId);
        }
    }

    public void setAttribute(String name, Object value) {
        int action = value == null ? 1 : 0;
        this.addAction(0, action, name, value);
    }

    public void removeAttribute(String name) {
        this.addAction(0, 1, name, null);
    }

    public void setNote(String name, Object value) {
        int action = value == null ? 1 : 0;
        this.addAction(6, action, name, value);
    }

    public void removeNote(String name) {
        this.addAction(6, 1, name, null);
    }

    public void setMaxInactiveInterval(int interval) {
        this.addAction(3, 0, NAME_MAXINTERVAL, interval);
    }

    public void setPrincipal(Principal p) {
        int action = p == null ? 1 : 0;
        GenericPrincipal gp = null;
        if (p != null) {
            if (p instanceof GenericPrincipal) {
                gp = (GenericPrincipal)p;
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("deltaRequest.showPrincipal", new Object[]{p.getName(), this.getSessionId()}));
                }
            } else {
                log.error((Object)sm.getString("deltaRequest.wrongPrincipalClass", new Object[]{p.getClass().getName()}));
            }
        }
        this.addAction(1, action, NAME_PRINCIPAL, gp);
    }

    public void setNew(boolean n) {
        int action = 0;
        this.addAction(2, action, NAME_ISNEW, n);
    }

    public void setAuthType(String authType) {
        int action = authType == null ? 1 : 0;
        this.addAction(4, action, NAME_AUTHTYPE, authType);
    }

    public void addSessionListener(SessionListener listener) {
        this.addAction(5, 0, NAME_LISTENER, listener);
    }

    public void removeSessionListener(SessionListener listener) {
        this.addAction(5, 1, NAME_LISTENER, listener);
    }

    protected void addAction(int type, int action, String name, Object value) {
        AttributeInfo info = null;
        if (this.actionPool.size() > 0) {
            try {
                info = this.actionPool.removeFirst();
            }
            catch (Exception x) {
                log.error((Object)sm.getString("deltaRequest.removeUnable"), (Throwable)x);
                info = new AttributeInfo(type, action, name, value);
            }
            info.init(type, action, name, value);
        } else {
            info = new AttributeInfo(type, action, name, value);
        }
        if (!this.recordAllActions) {
            try {
                this.actions.remove(info);
            }
            catch (NoSuchElementException noSuchElementException) {
                // empty catch block
            }
        }
        this.actions.addLast(info);
    }

    public void execute(DeltaSession session, boolean notifyListeners) {
        if (!this.sessionId.equals(session.getId())) {
            throw new IllegalArgumentException(sm.getString("deltaRequest.ssid.mismatch"));
        }
        session.access();
        block9: for (AttributeInfo info : this.actions) {
            switch (info.getType()) {
                case 0: {
                    if (info.getAction() == 0) {
                        if (log.isTraceEnabled()) {
                            log.trace((Object)("Session.setAttribute('" + info.getName() + "', '" + info.getValue() + "')"));
                        }
                        session.setAttribute(info.getName(), info.getValue(), notifyListeners, false);
                        continue block9;
                    }
                    if (log.isTraceEnabled()) {
                        log.trace((Object)("Session.removeAttribute('" + info.getName() + "')"));
                    }
                    session.removeAttribute(info.getName(), notifyListeners, false);
                    continue block9;
                }
                case 2: {
                    if (log.isTraceEnabled()) {
                        log.trace((Object)("Session.setNew('" + info.getValue() + "')"));
                    }
                    session.setNew((Boolean)info.getValue(), false);
                    continue block9;
                }
                case 3: {
                    if (log.isTraceEnabled()) {
                        log.trace((Object)("Session.setMaxInactiveInterval('" + info.getValue() + "')"));
                    }
                    session.setMaxInactiveInterval((Integer)info.getValue(), false);
                    continue block9;
                }
                case 1: {
                    Principal p = null;
                    if (info.getAction() == 0) {
                        p = (Principal)info.getValue();
                    }
                    session.setPrincipal(p, false);
                    continue block9;
                }
                case 4: {
                    String authType = null;
                    if (info.getAction() == 0) {
                        authType = (String)info.getValue();
                    }
                    session.setAuthType(authType, false);
                    continue block9;
                }
                case 5: {
                    SessionListener listener = (SessionListener)info.getValue();
                    if (info.getAction() == 0) {
                        session.addSessionListener(listener, false);
                        continue block9;
                    }
                    session.removeSessionListener(listener, false);
                    continue block9;
                }
                case 6: {
                    if (info.getAction() == 0) {
                        if (log.isTraceEnabled()) {
                            log.trace((Object)("Session.setNote('" + info.getName() + "', '" + info.getValue() + "')"));
                        }
                        session.setNote(info.getName(), info.getValue(), false);
                        continue block9;
                    }
                    if (log.isTraceEnabled()) {
                        log.trace((Object)("Session.removeNote('" + info.getName() + "')"));
                    }
                    session.removeNote(info.getName(), false);
                    continue block9;
                }
            }
            log.warn((Object)sm.getString("deltaRequest.invalidAttributeInfoType", new Object[]{info}));
        }
        session.endAccess();
        this.reset();
    }

    public void reset() {
        while (this.actions.size() > 0) {
            try {
                AttributeInfo info = this.actions.removeFirst();
                info.recycle();
                this.actionPool.addLast(info);
            }
            catch (Exception x) {
                log.error((Object)sm.getString("deltaRequest.removeUnable"), (Throwable)x);
            }
        }
        this.actions.clear();
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        if (sessionId == null) {
            Exception e = new Exception(sm.getString("deltaRequest.ssid.null"));
            log.error((Object)sm.getString("deltaRequest.ssid.null"), e.fillInStackTrace());
        }
    }

    public int getSize() {
        return this.actions.size();
    }

    public void clear() {
        this.actions.clear();
        this.actionPool.clear();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.reset();
        this.sessionId = in.readUTF();
        this.recordAllActions = in.readBoolean();
        int cnt = in.readInt();
        for (int i = 0; i < cnt; ++i) {
            AttributeInfo info = null;
            if (this.actionPool.size() > 0) {
                try {
                    info = this.actionPool.removeFirst();
                }
                catch (Exception x) {
                    log.error((Object)sm.getString("deltaRequest.removeUnable"), (Throwable)x);
                    info = new AttributeInfo();
                }
            } else {
                info = new AttributeInfo();
            }
            info.readExternal(in);
            this.actions.addLast(info);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.getSessionId());
        out.writeBoolean(this.recordAllActions);
        out.writeInt(this.getSize());
        for (AttributeInfo info : this.actions) {
            info.writeExternal(out);
        }
    }

    protected byte[] serialize() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        this.writeExternal(oos);
        oos.flush();
        oos.close();
        return bos.toByteArray();
    }

    private static class AttributeInfo
    implements Externalizable {
        private String name = null;
        private Object value = null;
        private int action;
        private int type;

        AttributeInfo() {
            this(-1, -1, null, null);
        }

        AttributeInfo(int type, int action, String name, Object value) {
            this.init(type, action, name, value);
        }

        public void init(int type, int action, String name, Object value) {
            this.name = name;
            this.value = value;
            this.action = action;
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public int getAction() {
            return this.action;
        }

        public Object getValue() {
            return this.value;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String getName() {
            return this.name;
        }

        public void recycle() {
            this.name = null;
            this.value = null;
            this.type = -1;
            this.action = -1;
        }

        public boolean equals(Object o) {
            if (!(o instanceof AttributeInfo)) {
                return false;
            }
            AttributeInfo other = (AttributeInfo)o;
            return other.getName().equals(this.getName());
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.type = in.readInt();
            this.action = in.readInt();
            this.name = in.readUTF();
            boolean hasValue = in.readBoolean();
            if (hasValue) {
                this.value = in.readObject();
            }
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(this.getType());
            out.writeInt(this.getAction());
            out.writeUTF(this.getName());
            out.writeBoolean(this.getValue() != null);
            if (this.getValue() != null) {
                out.writeObject(this.getValue());
            }
        }

        public String toString() {
            StringBuilder buf = new StringBuilder("AttributeInfo[type=");
            buf.append(this.getType()).append(", action=").append(this.getAction());
            buf.append(", name=").append(this.getName()).append(", value=").append(this.getValue());
            buf.append(", addr=").append(super.toString()).append(']');
            return buf.toString();
        }
    }
}

