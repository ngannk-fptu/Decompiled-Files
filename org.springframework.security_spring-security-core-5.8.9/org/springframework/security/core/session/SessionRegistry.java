/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.session;

import java.util.List;
import org.springframework.security.core.session.SessionInformation;

public interface SessionRegistry {
    public List<Object> getAllPrincipals();

    public List<SessionInformation> getAllSessions(Object var1, boolean var2);

    public SessionInformation getSessionInformation(String var1);

    public void refreshLastRequest(String var1);

    public void registerNewSession(String var1, Object var2);

    public void removeSessionInformation(String var1);
}

