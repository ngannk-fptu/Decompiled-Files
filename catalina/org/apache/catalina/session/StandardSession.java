/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionActivationListener
 *  javax.servlet.http.HttpSessionAttributeListener
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 *  javax.servlet.http.HttpSessionContext
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionIdListener
 *  javax.servlet.http.HttpSessionListener
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.session;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.WriteAbortedException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.authenticator.SavedRequest;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.session.Constants;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardSessionContext;
import org.apache.catalina.session.StandardSessionFacade;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class StandardSession
implements HttpSession,
Session,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    protected static final boolean ACTIVITY_CHECK;
    protected static final boolean LAST_ACCESS_AT_START;
    protected static final String[] EMPTY_ARRAY;
    protected ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    protected transient String authType = null;
    protected long creationTime = 0L;
    protected volatile transient boolean expiring = false;
    protected transient StandardSessionFacade facade = null;
    protected String id = null;
    protected volatile long lastAccessedTime = this.creationTime;
    protected transient ArrayList<SessionListener> listeners = new ArrayList();
    protected transient Manager manager = null;
    protected volatile int maxInactiveInterval = -1;
    protected volatile boolean isNew = false;
    protected volatile boolean isValid = false;
    protected transient Map<String, Object> notes = new ConcurrentHashMap<String, Object>();
    protected transient Principal principal = null;
    protected static final StringManager sm;
    @Deprecated
    protected static volatile HttpSessionContext sessionContext;
    protected final transient PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected volatile long thisAccessedTime = this.creationTime;
    protected transient AtomicInteger accessCount = null;

    public StandardSession(Manager manager) {
        this.manager = manager;
        if (ACTIVITY_CHECK) {
            this.accessCount = new AtomicInteger();
        }
    }

    @Override
    public String getAuthType() {
        return this.authType;
    }

    @Override
    public void setAuthType(String authType) {
        String oldAuthType = this.authType;
        this.authType = authType;
        this.support.firePropertyChange("authType", oldAuthType, this.authType);
    }

    @Override
    public void setCreationTime(long time) {
        this.creationTime = time;
        this.lastAccessedTime = time;
        this.thisAccessedTime = time;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getIdInternal() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.setId(id, true);
    }

    @Override
    public void setId(String id, boolean notify) {
        if (this.id != null && this.manager != null) {
            this.manager.remove(this);
        }
        this.id = id;
        if (this.manager != null) {
            this.manager.add(this);
        }
        if (notify) {
            this.tellNew();
        }
    }

    public void tellNew() {
        this.fireSessionEvent("createSession", null);
        Context context = this.manager.getContext();
        Object[] listeners = context.getApplicationLifecycleListeners();
        if (listeners != null && listeners.length > 0) {
            HttpSessionEvent event = new HttpSessionEvent(this.getSession());
            for (Object o : listeners) {
                if (!(o instanceof HttpSessionListener)) continue;
                HttpSessionListener listener = (HttpSessionListener)o;
                try {
                    context.fireContainerEvent("beforeSessionCreated", listener);
                    listener.sessionCreated(event);
                    context.fireContainerEvent("afterSessionCreated", listener);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    try {
                        context.fireContainerEvent("afterSessionCreated", listener);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.sessionEvent"), t);
                }
            }
        }
    }

    @Override
    public void tellChangedSessionId(String newId, String oldId, boolean notifySessionListeners, boolean notifyContainerListeners) {
        Object[] listeners;
        Context context = this.manager.getContext();
        if (notifyContainerListeners) {
            context.fireContainerEvent("changeSessionId", new String[]{oldId, newId});
        }
        if (notifySessionListeners && (listeners = context.getApplicationEventListeners()) != null && listeners.length > 0) {
            HttpSessionEvent event = new HttpSessionEvent(this.getSession());
            for (Object listener : listeners) {
                if (!(listener instanceof HttpSessionIdListener)) continue;
                HttpSessionIdListener idListener = (HttpSessionIdListener)listener;
                try {
                    idListener.sessionIdChanged(event, oldId);
                }
                catch (Throwable t) {
                    this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.sessionEvent"), t);
                }
            }
        }
    }

    @Override
    public long getThisAccessedTime() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getThisAccessedTime.ise"));
        }
        return this.thisAccessedTime;
    }

    @Override
    public long getThisAccessedTimeInternal() {
        return this.thisAccessedTime;
    }

    @Override
    public long getLastAccessedTime() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getLastAccessedTime.ise"));
        }
        return this.lastAccessedTime;
    }

    @Override
    public long getLastAccessedTimeInternal() {
        return this.lastAccessedTime;
    }

    @Override
    public long getIdleTime() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getIdleTime.ise"));
        }
        return this.getIdleTimeInternal();
    }

    @Override
    public long getIdleTimeInternal() {
        long timeNow = System.currentTimeMillis();
        long timeIdle = LAST_ACCESS_AT_START ? timeNow - this.lastAccessedTime : timeNow - this.thisAccessedTime;
        return timeIdle;
    }

    @Override
    public Manager getManager() {
        return this.manager;
    }

    @Override
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public Principal getPrincipal() {
        return this.principal;
    }

    @Override
    public void setPrincipal(Principal principal) {
        Principal oldPrincipal = this.principal;
        this.principal = principal;
        this.support.firePropertyChange("principal", oldPrincipal, this.principal);
    }

    @Override
    public HttpSession getSession() {
        if (this.facade == null) {
            this.facade = SecurityUtil.isPackageProtectionEnabled() ? AccessController.doPrivileged(new PrivilegedNewSessionFacade(this)) : new StandardSessionFacade(this);
        }
        return this.facade;
    }

    @Override
    public boolean isValid() {
        int timeIdle;
        if (!this.isValid) {
            return false;
        }
        if (this.expiring) {
            return true;
        }
        if (ACTIVITY_CHECK && this.accessCount.get() > 0) {
            return true;
        }
        if (this.maxInactiveInterval > 0 && (timeIdle = (int)(this.getIdleTimeInternal() / 1000L)) >= this.maxInactiveInterval) {
            this.expire(true);
        }
        return this.isValid;
    }

    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public void access() {
        this.thisAccessedTime = System.currentTimeMillis();
        if (ACTIVITY_CHECK) {
            this.accessCount.incrementAndGet();
        }
    }

    @Override
    public void endAccess() {
        this.isNew = false;
        if (LAST_ACCESS_AT_START) {
            this.lastAccessedTime = this.thisAccessedTime;
            this.thisAccessedTime = System.currentTimeMillis();
        } else {
            this.lastAccessedTime = this.thisAccessedTime = System.currentTimeMillis();
        }
        if (ACTIVITY_CHECK) {
            this.accessCount.decrementAndGet();
        }
    }

    @Override
    public void addSessionListener(SessionListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void expire() {
        this.expire(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void expire(boolean notify) {
        if (!this.isValid) {
            return;
        }
        StandardSession standardSession = this;
        synchronized (standardSession) {
            Context context;
            block24: {
                if (this.expiring || !this.isValid) {
                    return;
                }
                if (this.manager == null) {
                    return;
                }
                this.expiring = true;
                context = this.manager.getContext();
                if (notify) {
                    ClassLoader oldContextClassLoader = null;
                    try {
                        oldContextClassLoader = context.bind(Globals.IS_SECURITY_ENABLED, null);
                        Object[] listeners = context.getApplicationLifecycleListeners();
                        if (listeners == null || listeners.length <= 0) break block24;
                        HttpSessionEvent event = new HttpSessionEvent(this.getSession());
                        for (int i = 0; i < listeners.length; ++i) {
                            int j = listeners.length - 1 - i;
                            if (!(listeners[j] instanceof HttpSessionListener)) continue;
                            HttpSessionListener listener = (HttpSessionListener)listeners[j];
                            try {
                                context.fireContainerEvent("beforeSessionDestroyed", listener);
                                listener.sessionDestroyed(event);
                                context.fireContainerEvent("afterSessionDestroyed", listener);
                                continue;
                            }
                            catch (Throwable t) {
                                ExceptionUtils.handleThrowable((Throwable)t);
                                try {
                                    context.fireContainerEvent("afterSessionDestroyed", listener);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.sessionEvent"), t);
                            }
                        }
                    }
                    finally {
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldContextClassLoader);
                    }
                }
            }
            if (ACTIVITY_CHECK) {
                this.accessCount.set(0);
            }
            this.manager.remove(this, true);
            if (notify) {
                this.fireSessionEvent("destroySession", null);
            }
            if (this.principal instanceof TomcatPrincipal) {
                TomcatPrincipal gp = (TomcatPrincipal)this.principal;
                try {
                    gp.logout();
                }
                catch (Exception e) {
                    this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.logoutfail"), (Throwable)e);
                }
            }
            this.setValid(false);
            this.expiring = false;
            String[] keys = this.keys();
            ClassLoader oldContextClassLoader = null;
            try {
                oldContextClassLoader = context.bind(Globals.IS_SECURITY_ENABLED, null);
                for (String key : keys) {
                    this.removeAttributeInternal(key, notify);
                }
            }
            finally {
                context.unbind(Globals.IS_SECURITY_ENABLED, oldContextClassLoader);
            }
        }
    }

    public void passivate() {
        String[] keys;
        this.fireSessionEvent("passivateSession", null);
        HttpSessionEvent event = null;
        for (String key : keys = this.keys()) {
            Object attribute = this.attributes.get(key);
            if (!(attribute instanceof HttpSessionActivationListener)) continue;
            if (event == null) {
                event = new HttpSessionEvent(this.getSession());
            }
            try {
                ((HttpSessionActivationListener)attribute).sessionWillPassivate(event);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.attributeEvent"), t);
            }
        }
    }

    public void activate() {
        String[] keys;
        if (ACTIVITY_CHECK) {
            this.accessCount = new AtomicInteger();
        }
        this.fireSessionEvent("activateSession", null);
        HttpSessionEvent event = null;
        for (String key : keys = this.keys()) {
            Object attribute = this.attributes.get(key);
            if (!(attribute instanceof HttpSessionActivationListener)) continue;
            if (event == null) {
                event = new HttpSessionEvent(this.getSession());
            }
            try {
                ((HttpSessionActivationListener)attribute).sessionDidActivate(event);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.attributeEvent"), t);
            }
        }
    }

    @Override
    public Object getNote(String name) {
        return this.notes.get(name);
    }

    @Override
    public Iterator<String> getNoteNames() {
        return this.notes.keySet().iterator();
    }

    @Override
    public void recycle() {
        this.attributes.clear();
        this.setAuthType(null);
        this.creationTime = 0L;
        this.expiring = false;
        this.id = null;
        this.lastAccessedTime = 0L;
        this.maxInactiveInterval = -1;
        this.notes.clear();
        this.setPrincipal(null);
        this.isNew = false;
        this.isValid = false;
        this.manager = null;
    }

    @Override
    public void removeNote(String name) {
        this.notes.remove(name);
    }

    @Override
    public void removeSessionListener(SessionListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void setNote(String name, Object value) {
        this.notes.put(name, value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StandardSession[");
        sb.append(this.id);
        sb.append(']');
        return sb.toString();
    }

    public void readObjectData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        this.doReadObject(stream);
    }

    public void writeObjectData(ObjectOutputStream stream) throws IOException {
        this.doWriteObject(stream);
    }

    @Override
    public long getCreationTime() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getCreationTime.ise"));
        }
        return this.creationTime;
    }

    @Override
    public long getCreationTimeInternal() {
        return this.creationTime;
    }

    public ServletContext getServletContext() {
        if (this.manager == null) {
            return null;
        }
        Context context = this.manager.getContext();
        return context.getServletContext();
    }

    @Deprecated
    public HttpSessionContext getSessionContext() {
        if (sessionContext == null) {
            sessionContext = new StandardSessionContext();
        }
        return sessionContext;
    }

    public Object getAttribute(String name) {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getAttribute.ise"));
        }
        if (name == null) {
            return null;
        }
        return this.attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getAttributeNames.ise"));
        }
        HashSet names = new HashSet(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    @Deprecated
    public Object getValue(String name) {
        return this.getAttribute(name);
    }

    @Deprecated
    public String[] getValueNames() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getValueNames.ise"));
        }
        return this.keys();
    }

    public void invalidate() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.invalidate.ise"));
        }
        this.expire();
    }

    public boolean isNew() {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.isNew.ise"));
        }
        return this.isNew;
    }

    @Deprecated
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        this.removeAttribute(name, true);
    }

    public void removeAttribute(String name, boolean notify) {
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.removeAttribute.ise"));
        }
        this.removeAttributeInternal(name, notify);
    }

    @Deprecated
    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        this.setAttribute(name, value, true);
    }

    public void setAttribute(String name, Object value, boolean notify) {
        Object oldValue;
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("standardSession.setAttribute.namenull"));
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        if (!this.isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.setAttribute.ise", new Object[]{this.getIdInternal()}));
        }
        Context context = this.manager.getContext();
        if (context.getDistributable() && !this.isAttributeDistributable(name, value) && !this.exclude(name, value)) {
            throw new IllegalArgumentException(sm.getString("standardSession.setAttribute.iae", new Object[]{name}));
        }
        HttpSessionBindingEvent event = null;
        if (notify && value instanceof HttpSessionBindingListener && (value != (oldValue = this.attributes.get(name)) || this.manager.getNotifyBindingListenerOnUnchangedValue())) {
            event = new HttpSessionBindingEvent(this.getSession(), name, value);
            try {
                ((HttpSessionBindingListener)value).valueBound(event);
            }
            catch (Throwable t) {
                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.bindingEvent"), t);
            }
        }
        Object unbound = this.attributes.put(name, value);
        if (notify && unbound instanceof HttpSessionBindingListener && (unbound != value || this.manager.getNotifyBindingListenerOnUnchangedValue())) {
            try {
                ((HttpSessionBindingListener)unbound).valueUnbound(new HttpSessionBindingEvent(this.getSession(), name));
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.bindingEvent"), t);
            }
        }
        if (!notify) {
            return;
        }
        Object[] listeners = context.getApplicationEventListeners();
        if (listeners == null) {
            return;
        }
        for (Object o : listeners) {
            if (!(o instanceof HttpSessionAttributeListener)) continue;
            HttpSessionAttributeListener listener = (HttpSessionAttributeListener)o;
            try {
                if (unbound != null) {
                    if (unbound == value && !this.manager.getNotifyAttributeListenerOnUnchangedValue()) continue;
                    context.fireContainerEvent("beforeSessionAttributeReplaced", listener);
                    if (event == null) {
                        event = new HttpSessionBindingEvent(this.getSession(), name, unbound);
                    }
                    listener.attributeReplaced(event);
                    context.fireContainerEvent("afterSessionAttributeReplaced", listener);
                    continue;
                }
                context.fireContainerEvent("beforeSessionAttributeAdded", listener);
                if (event == null) {
                    event = new HttpSessionBindingEvent(this.getSession(), name, value);
                }
                listener.attributeAdded(event);
                context.fireContainerEvent("afterSessionAttributeAdded", listener);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                try {
                    if (unbound != null) {
                        if (unbound != value || this.manager.getNotifyAttributeListenerOnUnchangedValue()) {
                            context.fireContainerEvent("afterSessionAttributeReplaced", listener);
                        }
                    } else {
                        context.fireContainerEvent("afterSessionAttributeAdded", listener);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.attributeEvent"), t);
            }
        }
    }

    protected boolean isValidInternal() {
        return this.isValid;
    }

    @Override
    public boolean isAttributeDistributable(String name, Object value) {
        return value instanceof Serializable;
    }

    protected void doReadObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        Object nextObject;
        this.authType = null;
        this.creationTime = (Long)stream.readObject();
        this.lastAccessedTime = (Long)stream.readObject();
        this.maxInactiveInterval = (Integer)stream.readObject();
        this.isNew = (Boolean)stream.readObject();
        this.isValid = (Boolean)stream.readObject();
        this.thisAccessedTime = (Long)stream.readObject();
        this.principal = null;
        this.id = (String)stream.readObject();
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)("readObject() loading session " + this.id));
        }
        if (this.notes == null) {
            this.notes = new ConcurrentHashMap<String, Object>();
        }
        if (!((nextObject = stream.readObject()) instanceof Integer)) {
            this.setAuthType((String)nextObject);
            try {
                this.setPrincipal((Principal)stream.readObject());
            }
            catch (ObjectStreamException | ClassNotFoundException e) {
                String msg = sm.getString("standardSession.principalNotDeserializable", new Object[]{this.id});
                if (this.manager.getContext().getLogger().isDebugEnabled()) {
                    this.manager.getContext().getLogger().debug((Object)msg, (Throwable)e);
                } else {
                    this.manager.getContext().getLogger().warn((Object)msg);
                }
                throw e;
            }
            nextObject = stream.readObject();
            if (!(nextObject instanceof Integer)) {
                if (nextObject != null) {
                    this.notes.put("org.apache.catalina.authenticator.SESSION_ID", nextObject);
                }
                if ((nextObject = stream.readObject()) != null) {
                    this.notes.put("org.apache.catalina.authenticator.REQUEST", nextObject);
                }
                nextObject = stream.readObject();
            }
        }
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap<String, Object>();
        }
        int n = (Integer)nextObject;
        boolean isValidSave = this.isValid;
        this.isValid = true;
        for (int i = 0; i < n; ++i) {
            Object value;
            String name = (String)stream.readObject();
            try {
                value = stream.readObject();
            }
            catch (WriteAbortedException wae) {
                if (wae.getCause() instanceof NotSerializableException) {
                    String msg = sm.getString("standardSession.notDeserializable", new Object[]{name, this.id});
                    if (this.manager.getContext().getLogger().isDebugEnabled()) {
                        this.manager.getContext().getLogger().debug((Object)msg, (Throwable)wae);
                        continue;
                    }
                    this.manager.getContext().getLogger().warn((Object)msg);
                    continue;
                }
                throw wae;
            }
            if (this.manager.getContext().getLogger().isDebugEnabled()) {
                this.manager.getContext().getLogger().debug((Object)("  loading attribute '" + name + "' with value '" + value + "'"));
            }
            if (this.exclude(name, value) || null == value) continue;
            this.attributes.put(name, value);
        }
        this.isValid = isValidSave;
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
    }

    protected void doWriteObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(this.creationTime);
        stream.writeObject(this.lastAccessedTime);
        stream.writeObject(this.maxInactiveInterval);
        stream.writeObject(this.isNew);
        stream.writeObject(this.isValid);
        stream.writeObject(this.thisAccessedTime);
        stream.writeObject(this.id);
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)("writeObject() storing session " + this.id));
        }
        String sessionAuthType = null;
        Principal sessionPrincipal = null;
        String expectedSessionId = null;
        SavedRequest savedRequest = null;
        if (this.getPersistAuthentication()) {
            sessionAuthType = this.getAuthType();
            sessionPrincipal = this.getPrincipal();
            if (sessionPrincipal != null && !(sessionPrincipal instanceof Serializable)) {
                sessionPrincipal = null;
                this.manager.getContext().getLogger().warn((Object)sm.getString("standardSession.principalNotSerializable", new Object[]{this.id}));
            }
            expectedSessionId = (String)this.notes.get("org.apache.catalina.authenticator.SESSION_ID");
            savedRequest = (SavedRequest)this.notes.get("org.apache.catalina.authenticator.REQUEST");
        }
        stream.writeObject(sessionAuthType);
        try {
            stream.writeObject(sessionPrincipal);
        }
        catch (NotSerializableException e) {
            this.manager.getContext().getLogger().warn((Object)sm.getString("standardSession.principalNotSerializable", new Object[]{this.id}), (Throwable)e);
        }
        if (this.manager instanceof ManagerBase && ((ManagerBase)this.manager).getPersistAuthenticationNotes()) {
            stream.writeObject(expectedSessionId);
            stream.writeObject(savedRequest);
        }
        String[] keys = this.keys();
        ArrayList<String> saveNames = new ArrayList<String>();
        ArrayList saveValues = new ArrayList();
        for (String key : keys) {
            Object value = this.attributes.get(key);
            if (value == null) continue;
            if (this.isAttributeDistributable(key, value) && !this.exclude(key, value)) {
                saveNames.add(key);
                saveValues.add(value);
                continue;
            }
            this.removeAttributeInternal(key, true);
        }
        int n = saveNames.size();
        stream.writeObject(n);
        for (int i = 0; i < n; ++i) {
            stream.writeObject(saveNames.get(i));
            try {
                stream.writeObject(saveValues.get(i));
                if (!this.manager.getContext().getLogger().isDebugEnabled()) continue;
                this.manager.getContext().getLogger().debug((Object)("  storing attribute '" + (String)saveNames.get(i) + "' with value '" + saveValues.get(i) + "'"));
                continue;
            }
            catch (NotSerializableException e) {
                this.manager.getContext().getLogger().warn((Object)sm.getString("standardSession.notSerializable", new Object[]{saveNames.get(i), this.id}), (Throwable)e);
            }
        }
    }

    private boolean getPersistAuthentication() {
        if (this.manager instanceof ManagerBase) {
            return ((ManagerBase)this.manager).getPersistAuthentication();
        }
        return false;
    }

    protected boolean exclude(String name, Object value) {
        if (Constants.excludedAttributeNames.contains(name)) {
            return true;
        }
        Manager manager = this.getManager();
        if (manager == null) {
            return false;
        }
        return !manager.willAttributeDistribute(name, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireSessionEvent(String type, Object data) {
        if (this.listeners.size() < 1) {
            return;
        }
        SessionEvent event = new SessionEvent(this, type, data);
        SessionListener[] list = new SessionListener[]{};
        SessionListener[] sessionListenerArray = this.listeners;
        synchronized (this.listeners) {
            list = this.listeners.toArray(list);
            // ** MonitorExit[var5_5] (shouldn't be in output)
            for (SessionListener sessionListener : list) {
                sessionListener.sessionEvent(event);
            }
            return;
        }
    }

    protected String[] keys() {
        return this.attributes.keySet().toArray(EMPTY_ARRAY);
    }

    protected void removeAttributeInternal(String name, boolean notify) {
        Context context;
        Object[] listeners;
        if (name == null) {
            return;
        }
        Object value = this.attributes.remove(name);
        if (!notify || value == null) {
            return;
        }
        HttpSessionBindingEvent event = null;
        if (value instanceof HttpSessionBindingListener) {
            event = new HttpSessionBindingEvent(this.getSession(), name, value);
            ((HttpSessionBindingListener)value).valueUnbound(event);
        }
        if ((listeners = (context = this.manager.getContext()).getApplicationEventListeners()) == null) {
            return;
        }
        for (Object o : listeners) {
            if (!(o instanceof HttpSessionAttributeListener)) continue;
            HttpSessionAttributeListener listener = (HttpSessionAttributeListener)o;
            try {
                context.fireContainerEvent("beforeSessionAttributeRemoved", listener);
                if (event == null) {
                    event = new HttpSessionBindingEvent(this.getSession(), name, value);
                }
                listener.attributeRemoved(event);
                context.fireContainerEvent("afterSessionAttributeRemoved", listener);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                try {
                    context.fireContainerEvent("afterSessionAttributeRemoved", listener);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.manager.getContext().getLogger().error((Object)sm.getString("standardSession.attributeEvent"), t);
            }
        }
    }

    static {
        String activityCheck = System.getProperty("org.apache.catalina.session.StandardSession.ACTIVITY_CHECK");
        ACTIVITY_CHECK = activityCheck == null ? STRICT_SERVLET_COMPLIANCE : Boolean.parseBoolean(activityCheck);
        String lastAccessAtStart = System.getProperty("org.apache.catalina.session.StandardSession.LAST_ACCESS_AT_START");
        LAST_ACCESS_AT_START = lastAccessAtStart == null ? STRICT_SERVLET_COMPLIANCE : Boolean.parseBoolean(lastAccessAtStart);
        EMPTY_ARRAY = new String[0];
        sm = StringManager.getManager(StandardSession.class);
        sessionContext = null;
    }

    private static class PrivilegedNewSessionFacade
    implements PrivilegedAction<StandardSessionFacade> {
        private final HttpSession session;

        PrivilegedNewSessionFacade(HttpSession session) {
            this.session = session;
        }

        @Override
        public StandardSessionFacade run() {
            return new StandardSessionFacade(this.session);
        }
    }
}

