/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jndi.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.event.EventContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingListener;
import org.hibernate.engine.jndi.JndiException;
import org.hibernate.engine.jndi.JndiNameException;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class JndiServiceImpl
implements JndiService {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)JndiServiceImpl.class.getName());
    private final Hashtable initialContextSettings;

    public JndiServiceImpl(Map configurationValues) {
        this.initialContextSettings = JndiServiceImpl.extractJndiProperties(configurationValues);
    }

    public static Properties extractJndiProperties(Map configurationValues) {
        Properties jndiProperties = new Properties();
        for (Map.Entry entry : configurationValues.entrySet()) {
            if (!String.class.isInstance(entry.getKey())) continue;
            String propertyName = (String)entry.getKey();
            Object propertyValue = entry.getValue();
            if (!propertyName.startsWith("hibernate.jndi")) continue;
            if ("hibernate.jndi.class".equals(propertyName)) {
                if (propertyValue == null) continue;
                jndiProperties.put("java.naming.factory.initial", propertyValue);
                continue;
            }
            if ("hibernate.jndi.url".equals(propertyName)) {
                if (propertyValue == null) continue;
                jndiProperties.put("java.naming.provider.url", propertyValue);
                continue;
            }
            String passThruPropertyname = propertyName.substring("hibernate.jndi".length() + 1);
            jndiProperties.put(passThruPropertyname, propertyValue);
        }
        return jndiProperties;
    }

    @Override
    public Object locate(String jndiName) {
        InitialContext initialContext = this.buildInitialContext();
        Name name = this.parseName(jndiName, initialContext);
        try {
            Object object = initialContext.lookup(name);
            return object;
        }
        catch (NamingException e) {
            throw new JndiException("Unable to lookup JNDI name [" + jndiName + "]", e);
        }
        finally {
            this.cleanUp(initialContext);
        }
    }

    private InitialContext buildInitialContext() {
        try {
            return this.initialContextSettings.size() == 0 ? new InitialContext() : new InitialContext(this.initialContextSettings);
        }
        catch (NamingException e) {
            throw new JndiException("Unable to open InitialContext", e);
        }
    }

    private Name parseName(String jndiName, Context context) {
        try {
            URI uri = new URI(jndiName);
            String scheme = uri.getScheme();
            if (scheme != null && !JndiServiceImpl.allowedScheme(scheme)) {
                throw new JndiException("JNDI lookups for scheme '" + scheme + "' are not allowed");
            }
        }
        catch (URISyntaxException uri) {
            // empty catch block
        }
        try {
            return context.getNameParser("").parse(jndiName);
        }
        catch (InvalidNameException e) {
            throw new JndiNameException("JNDI name [" + jndiName + "] was not valid", e);
        }
        catch (NamingException e) {
            throw new JndiException("Error parsing JNDI name [" + jndiName + "]", e);
        }
    }

    private static boolean allowedScheme(String scheme) {
        switch (scheme) {
            case "java": 
            case "osgi": {
                return true;
            }
        }
        return false;
    }

    private void cleanUp(InitialContext initialContext) {
        try {
            initialContext.close();
        }
        catch (NamingException e) {
            LOG.unableToCloseInitialContext(e.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void bind(String jndiName, Object value) {
        InitialContext initialContext = this.buildInitialContext();
        Name name = this.parseName(jndiName, initialContext);
        try {
            this.bind(name, value, initialContext);
        }
        finally {
            this.cleanUp(initialContext);
        }
    }

    private void bind(Name name, Object value, Context context) {
        try {
            LOG.tracef("Binding : %s", name);
            context.rebind(name, value);
        }
        catch (Exception initialException) {
            if (name.size() == 1) {
                throw new JndiException("Error performing bind [" + name + "]", initialException);
            }
            Context intermediateContextBase = context;
            while (name.size() > 1) {
                String intermediateContextName = name.get(0);
                Context intermediateContext = null;
                try {
                    LOG.tracev("Intermediate lookup: {0}", intermediateContextName);
                    intermediateContext = (Context)intermediateContextBase.lookup(intermediateContextName);
                }
                catch (NameNotFoundException nameNotFoundException) {
                }
                catch (NamingException e) {
                    throw new JndiException("Unanticipated error doing intermediate lookup", e);
                }
                if (intermediateContext != null) {
                    LOG.tracev("Found intermediate context: {0}", intermediateContextName);
                } else {
                    LOG.tracev("Creating sub-context: {0}", intermediateContextName);
                    try {
                        intermediateContext = intermediateContextBase.createSubcontext(intermediateContextName);
                    }
                    catch (NamingException e) {
                        throw new JndiException("Error creating intermediate context [" + intermediateContextName + "]", e);
                    }
                }
                intermediateContextBase = intermediateContext;
                name = name.getSuffix(1);
            }
            LOG.tracev("Binding : {0}", name);
            try {
                intermediateContextBase.rebind(name, value);
            }
            catch (NamingException e) {
                throw new JndiException("Error performing intermediate bind [" + name + "]", e);
            }
        }
        LOG.debugf("Bound name: %s", name);
    }

    @Override
    public void unbind(String jndiName) {
        InitialContext initialContext = this.buildInitialContext();
        Name name = this.parseName(jndiName, initialContext);
        try {
            initialContext.unbind(name);
        }
        catch (Exception e) {
            throw new JndiException("Error performing unbind [" + name + "]", e);
        }
        finally {
            this.cleanUp(initialContext);
        }
    }

    @Override
    public void addListener(String jndiName, NamespaceChangeListener listener) {
        InitialContext initialContext = this.buildInitialContext();
        Name name = this.parseName(jndiName, initialContext);
        try {
            ((EventContext)((Object)initialContext)).addNamingListener(name, 0, (NamingListener)listener);
        }
        catch (Exception e) {
            throw new JndiException("Unable to bind listener to namespace [" + name + "]", e);
        }
        finally {
            this.cleanUp(initialContext);
        }
    }
}

