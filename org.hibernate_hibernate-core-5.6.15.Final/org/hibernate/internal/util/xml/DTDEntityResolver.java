/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.internal.util.xml;

import java.io.InputStream;
import java.io.Serializable;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ConfigHelper;
import org.jboss.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

@Deprecated
public class DTDEntityResolver
implements EntityResolver,
Serializable {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)DTDEntityResolver.class.getName());
    private static final String HIBERNATE_NAMESPACE = "http://www.hibernate.org/dtd/";
    private static final String OLD_HIBERNATE_NAMESPACE = "http://hibernate.sourceforge.net/";
    private static final String USER_NAMESPACE = "classpath://";

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        InputSource source = null;
        if (systemId != null) {
            LOG.debugf("Trying to resolve system-id [%s]", systemId);
            if (systemId.startsWith(HIBERNATE_NAMESPACE)) {
                LOG.debug("Recognized hibernate namespace; attempting to resolve on classpath under org/hibernate/");
                source = this.resolveOnClassPath(publicId, systemId, HIBERNATE_NAMESPACE);
            } else if (systemId.startsWith(OLD_HIBERNATE_NAMESPACE)) {
                LOG.recognizedObsoleteHibernateNamespace(OLD_HIBERNATE_NAMESPACE, HIBERNATE_NAMESPACE);
                LOG.debug("Attempting to resolve on classpath under org/hibernate/");
                source = this.resolveOnClassPath(publicId, systemId, OLD_HIBERNATE_NAMESPACE);
            } else if (systemId.startsWith(USER_NAMESPACE)) {
                LOG.debug("Recognized local namespace; attempting to resolve on classpath");
                String path = systemId.substring(USER_NAMESPACE.length());
                InputStream stream = this.resolveInLocalNamespace(path);
                if (stream == null) {
                    LOG.debugf("Unable to locate [%s] on classpath", systemId);
                } else {
                    LOG.debugf("Located [%s] in classpath", systemId);
                    source = new InputSource(stream);
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                }
            }
        }
        return source;
    }

    private InputSource resolveOnClassPath(String publicId, String systemId, String namespace) {
        InputSource source = null;
        String path = "org/hibernate/" + systemId.substring(namespace.length());
        InputStream dtdStream = this.resolveInHibernateNamespace(path);
        if (dtdStream == null) {
            LOG.debugf("Unable to locate [%s] on classpath", systemId);
            if (systemId.substring(namespace.length()).indexOf("2.0") > -1) {
                LOG.usingOldDtd();
            }
        } else {
            LOG.debugf("Located [%s] in classpath", systemId);
            source = new InputSource(dtdStream);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
        }
        return source;
    }

    protected InputStream resolveInHibernateNamespace(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    protected InputStream resolveInLocalNamespace(String path) {
        try {
            return ConfigHelper.getUserResourceAsStream(path);
        }
        catch (Throwable t) {
            return null;
        }
    }
}

