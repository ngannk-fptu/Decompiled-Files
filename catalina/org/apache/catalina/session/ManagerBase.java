/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.session;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.TooManyActiveSessionsException;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.SessionIdGeneratorBase;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public abstract class ManagerBase
extends LifecycleMBeanBase
implements Manager {
    private final Log log = LogFactory.getLog(ManagerBase.class);
    private Context context;
    private static final String name = "ManagerBase";
    protected String secureRandomClass = null;
    protected String secureRandomAlgorithm = SessionIdGeneratorBase.DEFAULT_SECURE_RANDOM_ALGORITHM;
    protected String secureRandomProvider = null;
    protected SessionIdGenerator sessionIdGenerator = null;
    protected Class<? extends SessionIdGenerator> sessionIdGeneratorClass = null;
    protected volatile int sessionMaxAliveTime;
    private final Object sessionMaxAliveTimeUpdateLock = new Object();
    protected static final int TIMING_STATS_CACHE_SIZE = 100;
    protected final Deque<SessionTiming> sessionCreationTiming = new LinkedList<SessionTiming>();
    protected final Deque<SessionTiming> sessionExpirationTiming = new LinkedList<SessionTiming>();
    protected final AtomicLong expiredSessions = new AtomicLong(0L);
    protected Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
    protected long sessionCounter = 0L;
    protected volatile int maxActive = 0;
    private final Object maxActiveUpdateLock = new Object();
    protected int maxActiveSessions = -1;
    protected int rejectedSessions = 0;
    protected volatile int duplicates = 0;
    protected long processingTime = 0L;
    private int count = 0;
    protected int processExpiresFrequency = 6;
    protected static final StringManager sm = StringManager.getManager(ManagerBase.class);
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private Pattern sessionAttributeNamePattern;
    private Pattern sessionAttributeValueClassNamePattern;
    private boolean warnOnSessionAttributeFilterFailure;
    private boolean notifyBindingListenerOnUnchangedValue;
    private boolean notifyAttributeListenerOnUnchangedValue = true;
    private boolean persistAuthentication = false;
    private boolean persistAuthenticationNotes = false;

    public ManagerBase() {
        if (Globals.IS_SECURITY_ENABLED) {
            this.setSessionAttributeValueClassNameFilter("java\\.lang\\.(?:Boolean|Integer|Long|Number|String)|org\\.apache\\.catalina\\.realm\\.GenericPrincipal\\$SerializablePrincipal|\\[Ljava.lang.String;");
            this.setWarnOnSessionAttributeFilterFailure(true);
        }
    }

    @Override
    public boolean getNotifyAttributeListenerOnUnchangedValue() {
        return this.notifyAttributeListenerOnUnchangedValue;
    }

    @Override
    public void setNotifyAttributeListenerOnUnchangedValue(boolean notifyAttributeListenerOnUnchangedValue) {
        this.notifyAttributeListenerOnUnchangedValue = notifyAttributeListenerOnUnchangedValue;
    }

    @Override
    public boolean getNotifyBindingListenerOnUnchangedValue() {
        return this.notifyBindingListenerOnUnchangedValue;
    }

    @Override
    public void setNotifyBindingListenerOnUnchangedValue(boolean notifyBindingListenerOnUnchangedValue) {
        this.notifyBindingListenerOnUnchangedValue = notifyBindingListenerOnUnchangedValue;
    }

    public String getSessionAttributeNameFilter() {
        if (this.sessionAttributeNamePattern == null) {
            return null;
        }
        return this.sessionAttributeNamePattern.toString();
    }

    public void setSessionAttributeNameFilter(String sessionAttributeNameFilter) throws PatternSyntaxException {
        this.sessionAttributeNamePattern = sessionAttributeNameFilter == null || sessionAttributeNameFilter.length() == 0 ? null : Pattern.compile(sessionAttributeNameFilter);
    }

    protected Pattern getSessionAttributeNamePattern() {
        return this.sessionAttributeNamePattern;
    }

    public String getSessionAttributeValueClassNameFilter() {
        if (this.sessionAttributeValueClassNamePattern == null) {
            return null;
        }
        return this.sessionAttributeValueClassNamePattern.toString();
    }

    protected Pattern getSessionAttributeValueClassNamePattern() {
        return this.sessionAttributeValueClassNamePattern;
    }

    public void setSessionAttributeValueClassNameFilter(String sessionAttributeValueClassNameFilter) throws PatternSyntaxException {
        this.sessionAttributeValueClassNamePattern = sessionAttributeValueClassNameFilter == null || sessionAttributeValueClassNameFilter.length() == 0 ? null : Pattern.compile(sessionAttributeValueClassNameFilter);
    }

    public boolean getWarnOnSessionAttributeFilterFailure() {
        return this.warnOnSessionAttributeFilterFailure;
    }

    public void setWarnOnSessionAttributeFilterFailure(boolean warnOnSessionAttributeFilterFailure) {
        this.warnOnSessionAttributeFilterFailure = warnOnSessionAttributeFilterFailure;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setContext(Context context) {
        if (this.context == context) {
            return;
        }
        if (!this.getState().equals((Object)LifecycleState.NEW)) {
            throw new IllegalStateException(sm.getString("managerBase.setContextNotNew"));
        }
        Context oldContext = this.context;
        this.context = context;
        this.support.firePropertyChange("context", oldContext, this.context);
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public SessionIdGenerator getSessionIdGenerator() {
        if (this.sessionIdGenerator != null) {
            return this.sessionIdGenerator;
        }
        if (this.sessionIdGeneratorClass != null) {
            try {
                this.sessionIdGenerator = this.sessionIdGeneratorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                return this.sessionIdGenerator;
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
        return null;
    }

    @Override
    public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
        this.sessionIdGeneratorClass = sessionIdGenerator.getClass();
    }

    public String getName() {
        return name;
    }

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        String oldSecureRandomClass = this.secureRandomClass;
        this.secureRandomClass = secureRandomClass;
        this.support.firePropertyChange("secureRandomClass", oldSecureRandomClass, this.secureRandomClass);
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    @Override
    public int getRejectedSessions() {
        return this.rejectedSessions;
    }

    @Override
    public long getExpiredSessions() {
        return this.expiredSessions.get();
    }

    @Override
    public void setExpiredSessions(long expiredSessions) {
        this.expiredSessions.set(expiredSessions);
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public int getProcessExpiresFrequency() {
        return this.processExpiresFrequency;
    }

    public void setProcessExpiresFrequency(int processExpiresFrequency) {
        if (processExpiresFrequency <= 0) {
            return;
        }
        int oldProcessExpiresFrequency = this.processExpiresFrequency;
        this.processExpiresFrequency = processExpiresFrequency;
        this.support.firePropertyChange("processExpiresFrequency", (Object)oldProcessExpiresFrequency, (Object)this.processExpiresFrequency);
    }

    public boolean getPersistAuthentication() {
        return this.persistAuthentication;
    }

    public void setPersistAuthentication(boolean persistAuthentication) {
        this.persistAuthentication = persistAuthentication;
    }

    @Deprecated
    public boolean getPersistAuthenticationNotes() {
        return this.persistAuthenticationNotes;
    }

    @Deprecated
    public void setPersistAuthenticationNotes(boolean persistAuthenticationNotes) {
        this.persistAuthenticationNotes = persistAuthenticationNotes;
    }

    @Override
    public void backgroundProcess() {
        this.count = (this.count + 1) % this.processExpiresFrequency;
        if (this.count == 0) {
            this.processExpires();
        }
    }

    public void processExpires() {
        long timeNow = System.currentTimeMillis();
        Session[] sessions = this.findSessions();
        int expireHere = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Start expire sessions " + this.getName() + " at " + timeNow + " sessioncount " + sessions.length));
        }
        for (Session session : sessions) {
            if (session == null || session.isValid()) continue;
            ++expireHere;
        }
        long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("End expire sessions " + this.getName() + " processingTime " + (timeEnd - timeNow) + " expired sessions: " + expireHere));
        }
        this.processingTime += timeEnd - timeNow;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.context == null) {
            throw new LifecycleException(sm.getString("managerBase.contextNull"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void startInternal() throws LifecycleException {
        Deque<SessionTiming> deque = this.sessionCreationTiming;
        synchronized (deque) {
            while (this.sessionCreationTiming.size() < 100) {
                this.sessionCreationTiming.add(null);
            }
        }
        deque = this.sessionExpirationTiming;
        synchronized (deque) {
            while (this.sessionExpirationTiming.size() < 100) {
                this.sessionExpirationTiming.add(null);
            }
        }
        SessionIdGenerator sessionIdGenerator = this.getSessionIdGenerator();
        if (sessionIdGenerator == null) {
            sessionIdGenerator = new StandardSessionIdGenerator();
            this.setSessionIdGenerator(sessionIdGenerator);
        }
        sessionIdGenerator.setJvmRoute(this.getJvmRoute());
        if (sessionIdGenerator instanceof SessionIdGeneratorBase) {
            SessionIdGeneratorBase sig = (SessionIdGeneratorBase)sessionIdGenerator;
            sig.setSecureRandomAlgorithm(this.getSecureRandomAlgorithm());
            sig.setSecureRandomClass(this.getSecureRandomClass());
            sig.setSecureRandomProvider(this.getSecureRandomProvider());
        }
        if (sessionIdGenerator instanceof Lifecycle) {
            ((Lifecycle)((Object)sessionIdGenerator)).start();
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Force random number initialization starting");
            }
            sessionIdGenerator.generateSessionId();
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Force random number initialization completed");
            }
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        if (this.sessionIdGenerator instanceof Lifecycle) {
            ((Lifecycle)((Object)this.sessionIdGenerator)).stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Session session) {
        this.sessions.put(session.getIdInternal(), session);
        int size = this.getActiveSessions();
        if (size > this.maxActive) {
            Object object = this.maxActiveUpdateLock;
            synchronized (object) {
                if (size > this.maxActive) {
                    this.maxActive = size;
                }
            }
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Session createSession(String sessionId) {
        if (this.maxActiveSessions >= 0 && this.getActiveSessions() >= this.maxActiveSessions) {
            ++this.rejectedSessions;
            throw new TooManyActiveSessionsException(sm.getString("managerBase.createSession.ise"), this.maxActiveSessions);
        }
        Session session = this.createEmptySession();
        session.setNew(true);
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(this.getContext().getSessionTimeout() * 60);
        String id = sessionId;
        if (id == null) {
            id = this.generateSessionId();
        }
        session.setId(id);
        ++this.sessionCounter;
        SessionTiming timing = new SessionTiming(session.getCreationTime(), 0);
        Deque<SessionTiming> deque = this.sessionCreationTiming;
        synchronized (deque) {
            this.sessionCreationTiming.add(timing);
            this.sessionCreationTiming.poll();
        }
        return session;
    }

    @Override
    public Session createEmptySession() {
        return this.getNewSession();
    }

    @Override
    public Session findSession(String id) throws IOException {
        if (id == null) {
            return null;
        }
        return this.sessions.get(id);
    }

    @Override
    public Session[] findSessions() {
        return this.sessions.values().toArray(new Session[0]);
    }

    @Override
    public void remove(Session session) {
        this.remove(session, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(Session session, boolean update) {
        if (update) {
            long timeNow = System.currentTimeMillis();
            int timeAlive = (int)(timeNow - session.getCreationTimeInternal()) / 1000;
            this.updateSessionMaxAliveTime(timeAlive);
            this.expiredSessions.incrementAndGet();
            SessionTiming timing = new SessionTiming(timeNow, timeAlive);
            Deque<SessionTiming> deque = this.sessionExpirationTiming;
            synchronized (deque) {
                this.sessionExpirationTiming.add(timing);
                this.sessionExpirationTiming.poll();
            }
        }
        if (session.getIdInternal() != null) {
            this.sessions.remove(session.getIdInternal());
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    @Override
    public void changeSessionId(Session session) {
        this.rotateSessionId(session);
    }

    @Override
    public String rotateSessionId(Session session) {
        String newId = this.generateSessionId();
        this.changeSessionId(session, newId, true, true);
        return newId;
    }

    @Override
    public void changeSessionId(Session session, String newId) {
        this.changeSessionId(session, newId, true, true);
    }

    protected void changeSessionId(Session session, String newId, boolean notifySessionListeners, boolean notifyContainerListeners) {
        String oldId = session.getIdInternal();
        session.setId(newId, false);
        session.tellChangedSessionId(newId, oldId, notifySessionListeners, notifyContainerListeners);
    }

    @Override
    public boolean willAttributeDistribute(String name, Object value) {
        Pattern sessionAttributeNamePattern = this.getSessionAttributeNamePattern();
        if (sessionAttributeNamePattern != null && !sessionAttributeNamePattern.matcher(name).matches()) {
            if (this.getWarnOnSessionAttributeFilterFailure() || this.log.isDebugEnabled()) {
                String msg = sm.getString("managerBase.sessionAttributeNameFilter", new Object[]{name, sessionAttributeNamePattern});
                if (this.getWarnOnSessionAttributeFilterFailure()) {
                    this.log.warn((Object)msg);
                } else {
                    this.log.debug((Object)msg);
                }
            }
            return false;
        }
        Pattern sessionAttributeValueClassNamePattern = this.getSessionAttributeValueClassNamePattern();
        if (value != null && sessionAttributeValueClassNamePattern != null && !sessionAttributeValueClassNamePattern.matcher(value.getClass().getName()).matches()) {
            if (this.getWarnOnSessionAttributeFilterFailure() || this.log.isDebugEnabled()) {
                String msg = sm.getString("managerBase.sessionAttributeValueClassNameFilter", new Object[]{name, value.getClass().getName(), sessionAttributeValueClassNamePattern});
                if (this.getWarnOnSessionAttributeFilterFailure()) {
                    this.log.warn((Object)msg);
                } else {
                    this.log.debug((Object)msg);
                }
            }
            return false;
        }
        return true;
    }

    protected StandardSession getNewSession() {
        return new StandardSession(this);
    }

    protected String generateSessionId() {
        String result = null;
        do {
            if (result == null) continue;
            ++this.duplicates;
        } while (this.sessions.containsKey(result = this.sessionIdGenerator.generateSessionId()));
        return result;
    }

    public Engine getEngine() {
        Engine e = null;
        for (Container c = this.getContext(); e == null && c != null; c = c.getParent()) {
            if (!(c instanceof Engine)) continue;
            e = (Engine)c;
        }
        return e;
    }

    public String getJvmRoute() {
        Engine e = this.getEngine();
        return e == null ? null : e.getJvmRoute();
    }

    @Override
    public void setSessionCounter(long sessionCounter) {
        this.sessionCounter = sessionCounter;
    }

    @Override
    public long getSessionCounter() {
        return this.sessionCounter;
    }

    public int getDuplicates() {
        return this.duplicates;
    }

    public void setDuplicates(int duplicates) {
        this.duplicates = duplicates;
    }

    @Override
    public int getActiveSessions() {
        return this.sessions.size();
    }

    @Override
    public int getMaxActive() {
        return this.maxActive;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaxActive(int maxActive) {
        Object object = this.maxActiveUpdateLock;
        synchronized (object) {
            this.maxActive = maxActive;
        }
    }

    public int getMaxActiveSessions() {
        return this.maxActiveSessions;
    }

    public void setMaxActiveSessions(int max) {
        int oldMaxActiveSessions = this.maxActiveSessions;
        this.maxActiveSessions = max;
        this.support.firePropertyChange("maxActiveSessions", (Object)oldMaxActiveSessions, (Object)this.maxActiveSessions);
    }

    @Override
    public int getSessionMaxAliveTime() {
        return this.sessionMaxAliveTime;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSessionMaxAliveTime(int sessionMaxAliveTime) {
        Object object = this.sessionMaxAliveTimeUpdateLock;
        synchronized (object) {
            this.sessionMaxAliveTime = sessionMaxAliveTime;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSessionMaxAliveTime(int sessionAliveTime) {
        if (sessionAliveTime > this.sessionMaxAliveTime) {
            Object object = this.sessionMaxAliveTimeUpdateLock;
            synchronized (object) {
                if (sessionAliveTime > this.sessionMaxAliveTime) {
                    this.sessionMaxAliveTime = sessionAliveTime;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSessionAverageAliveTime() {
        ArrayList<SessionTiming> copy;
        Deque<SessionTiming> deque = this.sessionExpirationTiming;
        synchronized (deque) {
            copy = new ArrayList<SessionTiming>(this.sessionExpirationTiming);
        }
        int counter = 0;
        int result = 0;
        for (SessionTiming timing : copy) {
            if (timing == null) continue;
            int timeAlive = timing.getDuration();
            result = result * ((++counter - 1) / counter) + timeAlive / counter;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSessionCreateRate() {
        ArrayList<SessionTiming> copy;
        Deque<SessionTiming> deque = this.sessionCreationTiming;
        synchronized (deque) {
            copy = new ArrayList<SessionTiming>(this.sessionCreationTiming);
        }
        return ManagerBase.calculateRate(copy);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSessionExpireRate() {
        ArrayList<SessionTiming> copy;
        Deque<SessionTiming> deque = this.sessionExpirationTiming;
        synchronized (deque) {
            copy = new ArrayList<SessionTiming>(this.sessionExpirationTiming);
        }
        return ManagerBase.calculateRate(copy);
    }

    private static int calculateRate(List<SessionTiming> sessionTiming) {
        long now;
        long oldest = now = System.currentTimeMillis();
        int counter = 0;
        int result = 0;
        for (SessionTiming timing : sessionTiming) {
            if (timing == null) continue;
            ++counter;
            if (timing.getTimestamp() >= oldest) continue;
            oldest = timing.getTimestamp();
        }
        if (counter > 0) {
            result = oldest < now ? 60000 * counter / (int)(now - oldest) : Integer.MAX_VALUE;
        }
        return result;
    }

    public String listSessionIds() {
        StringBuilder sb = new StringBuilder();
        for (String s : this.sessions.keySet()) {
            sb.append(s).append(' ');
        }
        return sb.toString();
    }

    public String getSessionAttribute(String sessionId, String key) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return null;
        }
        Object o = s.getSession().getAttribute(key);
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    public HashMap<String, String> getSession(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return null;
        }
        Enumeration ee = s.getSession().getAttributeNames();
        if (ee == null || !ee.hasMoreElements()) {
            return null;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        while (ee.hasMoreElements()) {
            String attrName = (String)ee.nextElement();
            map.put(attrName, this.getSessionAttribute(sessionId, attrName));
        }
        return map;
    }

    public void expireSession(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return;
        }
        s.expire();
    }

    public long getThisAccessedTimestamp(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return -1L;
        }
        return s.getThisAccessedTime();
    }

    public String getThisAccessedTime(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return "";
        }
        return new Date(s.getThisAccessedTime()).toString();
    }

    public long getLastAccessedTimestamp(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return -1L;
        }
        return s.getLastAccessedTime();
    }

    public String getLastAccessedTime(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return "";
        }
        return new Date(s.getLastAccessedTime()).toString();
    }

    public String getCreationTime(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return "";
        }
        return new Date(s.getCreationTime()).toString();
    }

    public long getCreationTimestamp(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)sm.getString("managerBase.sessionNotFound", new Object[]{sessionId}));
            }
            return -1L;
        }
        return s.getCreationTime();
    }

    public String toString() {
        return ToStringUtil.toString((Object)this, this.context);
    }

    @Override
    public String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Manager");
        name.append(",host=");
        name.append(this.context.getParent().getName());
        name.append(",context=");
        String contextName = this.context.getName();
        if (!contextName.startsWith("/")) {
            name.append('/');
        }
        name.append(contextName);
        return name.toString();
    }

    @Override
    public String getDomainInternal() {
        return this.context.getDomain();
    }

    protected static final class SessionTiming {
        private final long timestamp;
        private final int duration;

        public SessionTiming(long timestamp, int duration) {
            this.timestamp = timestamp;
            this.duration = duration;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public int getDuration() {
            return this.duration;
        }
    }
}

