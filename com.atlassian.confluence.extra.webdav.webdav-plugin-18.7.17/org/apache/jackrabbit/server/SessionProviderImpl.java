/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.server.SessionProvider;

public class SessionProviderImpl
implements SessionProvider {
    private CredentialsProvider cp;
    private final Map<Session, SessionProvider> externalSessions = Collections.synchronizedMap(new HashMap());

    public SessionProviderImpl(CredentialsProvider cp) {
        this.cp = cp;
    }

    @Override
    public Session getSession(HttpServletRequest request, Repository repository, String workspace) throws LoginException, RepositoryException, ServletException {
        SessionProvider provider;
        Session s = null;
        Object object = request.getAttribute(SessionProvider.class.getName());
        if (object instanceof SessionProvider && (s = (provider = (SessionProvider)object).getSession(request, repository, workspace)) != null) {
            this.externalSessions.put(s, provider);
        }
        if (s == null) {
            Credentials creds = this.cp.getCredentials(request);
            s = creds == null ? repository.login(workspace) : repository.login(creds, workspace);
        }
        return s;
    }

    @Override
    public void releaseSession(Session session) {
        SessionProvider provider = this.externalSessions.remove(session);
        if (provider != null) {
            provider.releaseSession(session);
        } else {
            session.logout();
        }
    }
}

