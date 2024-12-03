/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.rpc.server.ServiceLifecycle;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.commons.logging.Log;

public class SimpleSessionHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$SimpleSessionHandler == null ? (class$org$apache$axis$handlers$SimpleSessionHandler = SimpleSessionHandler.class$("org.apache.axis.handlers.SimpleSessionHandler")) : class$org$apache$axis$handlers$SimpleSessionHandler).getName());
    public static final String SESSION_ID = "SimpleSession.id";
    public static final String SESSION_NS = "http://xml.apache.org/axis/session";
    public static final String SESSION_LOCALPART = "sessionID";
    public static final QName sessionHeaderName = new QName("http://xml.apache.org/axis/session", "sessionID");
    private Hashtable activeSessions = new Hashtable();
    private long reapPeriodicity = 30L;
    private long lastReapTime = 0L;
    private int defaultSessionTimeout = 60;
    static /* synthetic */ Class class$org$apache$axis$handlers$SimpleSessionHandler;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invoke(MessageContext context) throws AxisFault {
        long curTime = System.currentTimeMillis();
        boolean reap = false;
        SimpleSessionHandler simpleSessionHandler = this;
        synchronized (simpleSessionHandler) {
            if (curTime > this.lastReapTime + this.reapPeriodicity * 1000L) {
                reap = true;
                this.lastReapTime = curTime;
            }
        }
        if (reap) {
            Object key;
            Set entries = this.activeSessions.entrySet();
            HashSet<Object> victims = new HashSet<Object>();
            Iterator i = entries.iterator();
            while (i.hasNext()) {
                Map.Entry entry = i.next();
                key = entry.getKey();
                SimpleSession session = (SimpleSession)entry.getValue();
                if (curTime - session.getLastAccessTime() <= (long)(session.getTimeout() * 1000)) continue;
                log.debug((Object)Messages.getMessage("timeout00", key.toString()));
                victims.add(key);
            }
            i = victims.iterator();
            while (i.hasNext()) {
                key = i.next();
                SimpleSession session = (SimpleSession)this.activeSessions.get(key);
                this.activeSessions.remove(key);
                Enumeration keys = session.getKeys();
                while (keys != null && keys.hasMoreElements()) {
                    String keystr = (String)keys.nextElement();
                    Object obj = session.get(keystr);
                    if (obj == null || !(obj instanceof ServiceLifecycle)) continue;
                    ((ServiceLifecycle)obj).destroy();
                }
            }
        }
        if (context.isClient()) {
            this.doClient(context);
        } else {
            this.doServer(context);
        }
    }

    public void doClient(MessageContext context) throws AxisFault {
        if (context.getPastPivot()) {
            Message msg = context.getResponseMessage();
            if (msg == null) {
                return;
            }
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPHeaderElement header = env.getHeaderByName(SESSION_NS, SESSION_LOCALPART);
            if (header == null) {
                return;
            }
            try {
                Long id = (Long)header.getValueAsType(Constants.XSD_LONG);
                AxisEngine engine = context.getAxisEngine();
                engine.setOption(SESSION_ID, id);
                header.setProcessed(true);
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
        } else {
            AxisEngine engine = context.getAxisEngine();
            Long id = (Long)engine.getOption(SESSION_ID);
            if (id == null) {
                return;
            }
            Message msg = context.getRequestMessage();
            if (msg == null) {
                throw new AxisFault(Messages.getMessage("noRequest00"));
            }
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPHeaderElement header = new SOAPHeaderElement(SESSION_NS, SESSION_LOCALPART, id);
            env.addHeader(header);
        }
    }

    public void doServer(MessageContext context) throws AxisFault {
        if (context.getPastPivot()) {
            Long id = (Long)context.getProperty(SESSION_ID);
            if (id == null) {
                return;
            }
            Message msg = context.getResponseMessage();
            if (msg == null) {
                return;
            }
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPHeaderElement header = new SOAPHeaderElement(SESSION_NS, SESSION_LOCALPART, id);
            env.addHeader(header);
        } else {
            Long id;
            Message msg = context.getRequestMessage();
            if (msg == null) {
                throw new AxisFault(Messages.getMessage("noRequest00"));
            }
            SOAPEnvelope env = msg.getSOAPEnvelope();
            SOAPHeaderElement header = env.getHeaderByName(SESSION_NS, SESSION_LOCALPART);
            if (header != null) {
                try {
                    id = (Long)header.getValueAsType(Constants.XSD_LONG);
                }
                catch (Exception e) {
                    throw AxisFault.makeFault(e);
                }
            } else {
                id = this.getNewSession();
            }
            SimpleSession session = (SimpleSession)this.activeSessions.get(id);
            if (session == null) {
                id = this.getNewSession();
                session = (SimpleSession)this.activeSessions.get(id);
            }
            session.touch();
            context.setSession(session);
            context.setProperty(SESSION_ID, id);
        }
    }

    private synchronized Long getNewSession() {
        Long id = SessionUtils.generateSession();
        SimpleSession session = new SimpleSession();
        session.setTimeout(this.defaultSessionTimeout);
        this.activeSessions.put(id, session);
        return id;
    }

    public void setReapPeriodicity(long reapTime) {
        this.reapPeriodicity = reapTime;
    }

    public void setDefaultSessionTimeout(int defaultSessionTimeout) {
        this.defaultSessionTimeout = defaultSessionTimeout;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

