/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.felix.scr.annotations.Activate
 *  org.apache.felix.scr.annotations.Component
 *  org.apache.felix.scr.annotations.Properties
 *  org.apache.felix.scr.annotations.Property
 *  org.apache.felix.scr.annotations.Reference
 *  org.apache.felix.scr.annotations.ReferenceCardinality
 *  org.apache.felix.scr.annotations.ReferencePolicy
 *  org.apache.felix.scr.annotations.Service
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.server.remoting.davex.JcrRemotingServlet;

@Component(metatype=true, label="%dav.name", description="%dav.description")
@Service(value={Servlet.class})
@Properties(value={@Property(name="service.description", value={"Apache Jackrabbit JcrRemoting Servlet"}), @Property(name="authenticate-header", value={"Basic realm=\"Jackrabbit Webdav Server\""}), @Property(name="csrf-protection", value={"disabled"}), @Property(name="missing-auth-mapping", value={""}), @Property(name="contextId", value={""})})
@Reference(name="providers", referenceInterface=SessionProvider.class, policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE, bind="addSessionProvider", unbind="removeSessionProvider")
public class DavexServletService
extends JcrRemotingServlet
implements SessionProvider {
    private static final long serialVersionUID = -901601294536148635L;
    private static final String DEFAULT_ALIAS = "/server";
    @Property(value={"/server"})
    private static final String PARAM_ALIAS = "alias";
    @Reference
    private Repository repository;
    private String alias;
    private final Map<SessionProvider, Set<Session>> providers = new LinkedHashMap<SessionProvider, Set<Session>>();
    private final Map<Session, SessionProvider> sessions = new HashMap<Session, SessionProvider>();

    @Override
    protected Repository getRepository() {
        return this.repository;
    }

    @Override
    protected String getResourcePathPrefix() {
        return this.alias;
    }

    @Activate
    public void activate(Map<String, ?> config) {
        Object object = config.get(PARAM_ALIAS);
        String string = "";
        if (object != null) {
            string = object.toString();
        }
        this.alias = string.length() > 0 ? string : DEFAULT_ALIAS;
    }

    @Override
    protected SessionProvider getSessionProvider() {
        return this;
    }

    public synchronized void addSessionProvider(SessionProvider provider) {
        this.providers.put(provider, new HashSet());
    }

    public synchronized void removeSessionProvider(SessionProvider provider) {
        Set<Session> sessions = this.providers.remove(provider);
        if (sessions != null) {
            for (Session session : sessions) {
                this.releaseSession(session);
            }
        }
    }

    @Override
    public synchronized Session getSession(HttpServletRequest request, Repository repository, String workspace) throws LoginException, ServletException, RepositoryException {
        SessionProvider provider = null;
        Session session = null;
        for (Map.Entry<SessionProvider, Set<Session>> entry : this.providers.entrySet()) {
            provider = entry.getKey();
            session = provider.getSession(request, repository, workspace);
            if (session == null) continue;
            entry.getValue().add(session);
            break;
        }
        if (session == null) {
            provider = super.getSessionProvider();
            session = provider.getSession(request, repository, workspace);
        }
        if (session != null) {
            this.sessions.put(session, provider);
        }
        return session;
    }

    @Override
    public synchronized void releaseSession(Session session) {
        SessionProvider provider = this.sessions.remove(session);
        if (provider != null) {
            provider.releaseSession(session);
        }
    }

    protected void bindRepository(Repository repository) {
        this.repository = repository;
    }

    protected void unbindRepository(Repository repository) {
        if (this.repository == repository) {
            this.repository = null;
        }
    }
}

