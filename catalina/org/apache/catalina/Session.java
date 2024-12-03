/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 */
package org.apache.catalina;

import java.security.Principal;
import java.util.Iterator;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionListener;

public interface Session {
    public static final String SESSION_CREATED_EVENT = "createSession";
    public static final String SESSION_DESTROYED_EVENT = "destroySession";
    public static final String SESSION_ACTIVATED_EVENT = "activateSession";
    public static final String SESSION_PASSIVATED_EVENT = "passivateSession";

    public String getAuthType();

    public void setAuthType(String var1);

    public long getCreationTime();

    public long getCreationTimeInternal();

    public void setCreationTime(long var1);

    public String getId();

    public String getIdInternal();

    public void setId(String var1);

    public void setId(String var1, boolean var2);

    public long getThisAccessedTime();

    public long getThisAccessedTimeInternal();

    public long getLastAccessedTime();

    public long getLastAccessedTimeInternal();

    public long getIdleTime();

    public long getIdleTimeInternal();

    public Manager getManager();

    public void setManager(Manager var1);

    public int getMaxInactiveInterval();

    public void setMaxInactiveInterval(int var1);

    public void setNew(boolean var1);

    public Principal getPrincipal();

    public void setPrincipal(Principal var1);

    public HttpSession getSession();

    public void setValid(boolean var1);

    public boolean isValid();

    public void access();

    public void addSessionListener(SessionListener var1);

    public void endAccess();

    public void expire();

    public Object getNote(String var1);

    public Iterator<String> getNoteNames();

    public void recycle();

    public void removeNote(String var1);

    public void removeSessionListener(SessionListener var1);

    public void setNote(String var1, Object var2);

    public void tellChangedSessionId(String var1, String var2, boolean var3, boolean var4);

    public boolean isAttributeDistributable(String var1, Object var2);
}

