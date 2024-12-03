/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.SessionIdGenerator;

public interface Manager {
    public Context getContext();

    public void setContext(Context var1);

    public SessionIdGenerator getSessionIdGenerator();

    public void setSessionIdGenerator(SessionIdGenerator var1);

    public long getSessionCounter();

    public void setSessionCounter(long var1);

    public int getMaxActive();

    public void setMaxActive(int var1);

    public int getActiveSessions();

    public long getExpiredSessions();

    public void setExpiredSessions(long var1);

    public int getRejectedSessions();

    public int getSessionMaxAliveTime();

    public void setSessionMaxAliveTime(int var1);

    public int getSessionAverageAliveTime();

    public int getSessionCreateRate();

    public int getSessionExpireRate();

    public void add(Session var1);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    @Deprecated
    public void changeSessionId(Session var1);

    default public String rotateSessionId(Session session) {
        String newSessionId = null;
        boolean duplicate = true;
        do {
            newSessionId = this.getSessionIdGenerator().generateSessionId();
            try {
                if (this.findSession(newSessionId) != null) continue;
                duplicate = false;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        } while (duplicate);
        this.changeSessionId(session, newSessionId);
        return newSessionId;
    }

    public void changeSessionId(Session var1, String var2);

    public Session createEmptySession();

    public Session createSession(String var1);

    public Session findSession(String var1) throws IOException;

    public Session[] findSessions();

    public void load() throws ClassNotFoundException, IOException;

    public void remove(Session var1);

    public void remove(Session var1, boolean var2);

    public void removePropertyChangeListener(PropertyChangeListener var1);

    public void unload() throws IOException;

    public void backgroundProcess();

    public boolean willAttributeDistribute(String var1, Object var2);

    default public boolean getNotifyBindingListenerOnUnchangedValue() {
        return false;
    }

    public void setNotifyBindingListenerOnUnchangedValue(boolean var1);

    default public boolean getNotifyAttributeListenerOnUnchangedValue() {
        return true;
    }

    public void setNotifyAttributeListenerOnUnchangedValue(boolean var1);
}

