/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.io.InputStream;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.xml.DTDEntityResolver;
import org.jboss.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class EJB3DTDEntityResolver
extends DTDEntityResolver {
    public static final EntityResolver INSTANCE = new EJB3DTDEntityResolver();
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)EJB3DTDEntityResolver.class.getName());
    boolean resolved = false;

    public boolean isResolved() {
        return this.resolved;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        InputSource source;
        LOG.tracev("Resolving XML entity {0} : {1}", publicId, systemId);
        if (systemId != null) {
            InputSource source2;
            InputStream dtdStream;
            if (systemId.endsWith("orm_3_0.xsd")) {
                dtdStream = this.getStreamFromClasspath("orm_3_0.xsd");
                InputSource source3 = this.buildInputSource(publicId, systemId, dtdStream, false);
                if (source3 != null) {
                    return source3;
                }
            } else if (systemId.endsWith("orm_2_1.xsd")) {
                dtdStream = this.getStreamFromClasspath("orm_2_1.xsd");
                InputSource source4 = this.buildInputSource(publicId, systemId, dtdStream, false);
                if (source4 != null) {
                    return source4;
                }
            } else if (systemId.endsWith("orm_2_2.xsd")) {
                dtdStream = this.getStreamFromClasspath("orm_2_2.xsd");
                InputSource source5 = this.buildInputSource(publicId, systemId, dtdStream, false);
                if (source5 != null) {
                    return source5;
                }
            } else if (systemId.endsWith("orm_2_0.xsd")) {
                dtdStream = this.getStreamFromClasspath("orm_2_0.xsd");
                InputSource source6 = this.buildInputSource(publicId, systemId, dtdStream, false);
                if (source6 != null) {
                    return source6;
                }
            } else if (systemId.endsWith("orm_1_0.xsd")) {
                dtdStream = this.getStreamFromClasspath("orm_1_0.xsd");
                InputSource source7 = this.buildInputSource(publicId, systemId, dtdStream, false);
                if (source7 != null) {
                    return source7;
                }
            } else if (systemId.endsWith("persistence_3_0.xsd")) {
                dtdStream = this.getStreamFromClasspath("persistence_3_0.xsd");
                InputSource source8 = this.buildInputSource(publicId, systemId, dtdStream, true);
                if (source8 != null) {
                    return source8;
                }
            } else if (systemId.endsWith("persistence_2_2.xsd")) {
                dtdStream = this.getStreamFromClasspath("persistence_2_2.xsd");
                InputSource source9 = this.buildInputSource(publicId, systemId, dtdStream, true);
                if (source9 != null) {
                    return source9;
                }
            } else if (systemId.endsWith("persistence_2_1.xsd")) {
                dtdStream = this.getStreamFromClasspath("persistence_2_1.xsd");
                InputSource source10 = this.buildInputSource(publicId, systemId, dtdStream, true);
                if (source10 != null) {
                    return source10;
                }
            } else if (systemId.endsWith("persistence_2_0.xsd")) {
                dtdStream = this.getStreamFromClasspath("persistence_2_0.xsd");
                InputSource source11 = this.buildInputSource(publicId, systemId, dtdStream, true);
                if (source11 != null) {
                    return source11;
                }
            } else if (systemId.endsWith("persistence_1_0.xsd") && (source2 = this.buildInputSource(publicId, systemId, dtdStream = this.getStreamFromClasspath("persistence_1_0.xsd"), true)) != null) {
                return source2;
            }
        }
        if ((source = super.resolveEntity(publicId, systemId)) != null) {
            this.resolved = true;
        }
        return source;
    }

    private InputSource buildInputSource(String publicId, String systemId, InputStream dtdStream, boolean resolved) {
        if (dtdStream == null) {
            LOG.tracev("Unable to locate [{0}] on classpath", systemId);
            return null;
        }
        LOG.tracev("Located [{0}] in classpath", systemId);
        InputSource source = new InputSource(dtdStream);
        source.setPublicId(publicId);
        source.setSystemId(systemId);
        this.resolved = resolved;
        return source;
    }

    private InputStream getStreamFromClasspath(String fileName) {
        LOG.trace("Recognized JPA ORM namespace; attempting to resolve on classpath under org/hibernate/jpa");
        String path = "org/hibernate/jpa/" + fileName;
        InputStream dtdStream = this.resolveInHibernateNamespace(path);
        return dtdStream;
    }
}

