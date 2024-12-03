/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.spi.ObjectFactory;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jndi.JndiException;
import org.hibernate.engine.jndi.JndiNameException;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;

public class SessionFactoryRegistry {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SessionFactoryRegistry.class);
    public static final SessionFactoryRegistry INSTANCE = new SessionFactoryRegistry();
    private final ConcurrentHashMap<String, SessionFactory> sessionFactoryMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, String> nameUuidXref = new ConcurrentHashMap();
    private final NamespaceChangeListener listener = new NamespaceChangeListener(){

        @Override
        public void objectAdded(NamingEvent evt) {
            LOG.debugf("A factory was successfully bound to name: %s", evt.getNewBinding().getName());
        }

        @Override
        public void objectRemoved(NamingEvent evt) {
            String jndiName = evt.getOldBinding().getName();
            LOG.factoryUnboundFromName(jndiName);
            String uuid = (String)SessionFactoryRegistry.this.nameUuidXref.remove(jndiName);
            if (uuid == null) {
                // empty if block
            }
            SessionFactoryRegistry.this.sessionFactoryMap.remove(uuid);
        }

        @Override
        public void objectRenamed(NamingEvent evt) {
            String oldJndiName = evt.getOldBinding().getName();
            String newJndiName = evt.getNewBinding().getName();
            LOG.factoryJndiRename(oldJndiName, newJndiName);
            String uuid = (String)SessionFactoryRegistry.this.nameUuidXref.remove(oldJndiName);
            SessionFactoryRegistry.this.nameUuidXref.put(newJndiName, uuid);
        }

        @Override
        public void namingExceptionThrown(NamingExceptionEvent evt) {
            LOG.namingExceptionAccessingFactory(evt.getException());
        }
    };

    private SessionFactoryRegistry() {
        LOG.debugf("Initializing SessionFactoryRegistry : %s", this);
    }

    public void addSessionFactory(String uuid, String name, boolean isNameAlsoJndiName, SessionFactory instance, JndiService jndiService) {
        if (uuid == null) {
            throw new IllegalArgumentException("SessionFactory UUID cannot be null");
        }
        LOG.debugf("Registering SessionFactory: %s (%s)", uuid, name == null ? "<unnamed>" : name);
        this.sessionFactoryMap.put(uuid, instance);
        if (name != null) {
            this.nameUuidXref.put(name, uuid);
        }
        if (name == null || !isNameAlsoJndiName) {
            LOG.debug("Not binding SessionFactory to JNDI, no JNDI name configured");
            return;
        }
        LOG.debugf("Attempting to bind SessionFactory [%s] to JNDI", name);
        try {
            jndiService.bind(name, instance);
            LOG.factoryBoundToJndiName(name);
            try {
                jndiService.addListener(name, this.listener);
            }
            catch (Exception e) {
                LOG.couldNotBindJndiListener();
            }
        }
        catch (JndiNameException e) {
            LOG.invalidJndiName(name, e);
        }
        catch (JndiException e) {
            LOG.unableToBindFactoryToJndi(e);
        }
    }

    public void removeSessionFactory(String uuid, String name, boolean isNameAlsoJndiName, JndiService jndiService) {
        if (name != null) {
            this.nameUuidXref.remove(name);
            if (isNameAlsoJndiName) {
                try {
                    LOG.tracef("Unbinding SessionFactory from JNDI : %s", name);
                    jndiService.unbind(name);
                    LOG.factoryUnboundFromJndiName(name);
                }
                catch (JndiNameException e) {
                    LOG.invalidJndiName(name, e);
                }
                catch (JndiException e) {
                    LOG.unableToUnbindFactoryFromJndi(e);
                }
            }
        }
        this.sessionFactoryMap.remove(uuid);
    }

    public SessionFactory getNamedSessionFactory(String name) {
        LOG.debugf("Lookup: name=%s", name);
        String uuid = this.nameUuidXref.get(name);
        return uuid == null ? null : this.getSessionFactory(uuid);
    }

    public SessionFactory getSessionFactory(String uuid) {
        LOG.debugf("Lookup: uid=%s", uuid);
        SessionFactory sessionFactory = this.sessionFactoryMap.get(uuid);
        if (sessionFactory == null && LOG.isDebugEnabled()) {
            LOG.debugf("Not found: %s", uuid);
            LOG.debug(this.sessionFactoryMap.toString());
        }
        return sessionFactory;
    }

    public SessionFactory findSessionFactory(String uuid, String name) {
        SessionFactory sessionFactory = this.getSessionFactory(uuid);
        if (sessionFactory == null && StringHelper.isNotEmpty(name)) {
            sessionFactory = this.getNamedSessionFactory(name);
        }
        return sessionFactory;
    }

    public boolean hasRegistrations() {
        return !this.sessionFactoryMap.isEmpty();
    }

    public void clearRegistrations() {
        this.nameUuidXref.clear();
        for (SessionFactory factory : this.sessionFactoryMap.values()) {
            try {
                factory.close();
            }
            catch (Exception exception) {}
        }
        this.sessionFactoryMap.clear();
    }

    public static class ObjectFactoryImpl
    implements ObjectFactory {
        @Override
        public Object getObjectInstance(Object reference, Name name, Context nameCtx, Hashtable<?, ?> environment) {
            LOG.debugf("JNDI lookup: %s", name);
            String uuid = (String)((Reference)reference).get(0).getContent();
            LOG.tracef("Resolved to UUID = %s", uuid);
            return INSTANCE.getSessionFactory(uuid);
        }
    }
}

