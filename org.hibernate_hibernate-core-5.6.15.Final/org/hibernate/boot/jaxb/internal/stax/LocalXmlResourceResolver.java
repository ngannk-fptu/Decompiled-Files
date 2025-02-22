/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.jaxb.internal.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.hibernate.boot.jaxb.internal.stax.LocalSchemaLocator;
import org.hibernate.boot.jaxb.internal.stax.XmlInfrastructureException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.internal.log.DeprecationLogger;
import org.jboss.logging.Logger;

public class LocalXmlResourceResolver
implements XMLResolver {
    private static final Logger log = Logger.getLogger(LocalXmlResourceResolver.class);
    public static final String CLASSPATH_EXTENSION_URL_BASE = "classpath://";
    private final ClassLoaderService classLoaderService;
    public static final NamespaceSchemaMapping INITIAL_JPA_XSD_MAPPING = new NamespaceSchemaMapping("http://java.sun.com/xml/ns/persistence/orm", "org/hibernate/jpa/orm_2_0.xsd");
    public static final NamespaceSchemaMapping JPA_XSD_MAPPING = new NamespaceSchemaMapping("http://xmlns.jcp.org/xml/ns/persistence/orm", "org/hibernate/jpa/orm_2_1.xsd");
    public static final NamespaceSchemaMapping PERSISTENCE_ORM_XSD_MAPPING = new NamespaceSchemaMapping("http://xmlns.jcp.org/xml/ns/persistence/orm", "org/hibernate/jpa/orm_2_2.xsd");
    public static final NamespaceSchemaMapping PERSISTENCE_ORM_XSD_MAPPING2 = new NamespaceSchemaMapping("https://jakarta.ee/xml/ns/persistence/orm", "org/hibernate/jpa/orm_3_0.xsd");
    public static final NamespaceSchemaMapping HBM_XSD_MAPPING = new NamespaceSchemaMapping("http://www.hibernate.org/xsd/orm/hbm", "org/hibernate/xsd/mapping/legacy-mapping-4.0.xsd");
    public static final NamespaceSchemaMapping HBM_XSD_MAPPING2 = new NamespaceSchemaMapping("http://www.hibernate.org/xsd/hibernate-mapping", "org/hibernate/hibernate-mapping-4.0.xsd");
    public static final NamespaceSchemaMapping CFG_XSD_MAPPING = new NamespaceSchemaMapping("http://www.hibernate.org/xsd/orm/cfg", "org/hibernate/xsd/cfg/legacy-configuration-4.0.xsd");
    public static final DtdMapping HBM_DTD_MAPPING = new DtdMapping("www.hibernate.org/dtd/hibernate-mapping", "org/hibernate/hibernate-mapping-3.0.dtd");
    public static final DtdMapping ALTERNATE_MAPPING_DTD = new DtdMapping("hibernate.org/dtd/hibernate-mapping", "org/hibernate/hibernate-mapping-3.0.dtd");
    public static final DtdMapping LEGACY_HBM_DTD_MAPPING = new DtdMapping("hibernate.sourceforge.net/hibernate-mapping", "org/hibernate/hibernate-mapping-3.0.dtd");
    public static final DtdMapping CFG_DTD_MAPPING = new DtdMapping("www.hibernate.org/dtd/hibernate-configuration", "org/hibernate/hibernate-configuration-3.0.dtd");
    public static final DtdMapping ALTERNATE_CFG_DTD = new DtdMapping("hibernate.org/dtd/hibernate-configuration", "org/hibernate/hibernate-configuration-3.0.dtd");
    public static final DtdMapping LEGACY_CFG_DTD_MAPPING = new DtdMapping("hibernate.sourceforge.net/hibernate-configuration", "org/hibernate/hibernate-configuration-3.0.dtd");

    public LocalXmlResourceResolver(ClassLoaderService classLoaderService) {
        this.classLoaderService = classLoaderService;
    }

    @Override
    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        log.tracef("In resolveEntity(%s, %s, %s, %s)", new Object[]{publicID, systemID, baseURI, namespace});
        if (namespace != null) {
            log.debugf("Interpreting namespace : %s", (Object)namespace);
            if (INITIAL_JPA_XSD_MAPPING.matches(namespace)) {
                return this.openUrlStream(INITIAL_JPA_XSD_MAPPING.getMappedLocalUrl());
            }
            if (JPA_XSD_MAPPING.matches(namespace)) {
                return this.openUrlStream(JPA_XSD_MAPPING.getMappedLocalUrl());
            }
            if (PERSISTENCE_ORM_XSD_MAPPING.matches(namespace)) {
                return this.openUrlStream(PERSISTENCE_ORM_XSD_MAPPING.getMappedLocalUrl());
            }
            if (PERSISTENCE_ORM_XSD_MAPPING2.matches(namespace)) {
                return this.openUrlStream(PERSISTENCE_ORM_XSD_MAPPING2.getMappedLocalUrl());
            }
            if (HBM_XSD_MAPPING.matches(namespace)) {
                return this.openUrlStream(HBM_XSD_MAPPING.getMappedLocalUrl());
            }
            if (HBM_XSD_MAPPING2.matches(namespace)) {
                return this.openUrlStream(HBM_XSD_MAPPING2.getMappedLocalUrl());
            }
            if (CFG_XSD_MAPPING.matches(namespace)) {
                return this.openUrlStream(CFG_XSD_MAPPING.getMappedLocalUrl());
            }
        }
        if (publicID != null || systemID != null) {
            log.debugf("Interpreting public/system identifier : [%s] - [%s]", (Object)publicID, (Object)systemID);
            if (HBM_DTD_MAPPING.matches(publicID, systemID)) {
                log.debug((Object)"Recognized hibernate-mapping identifier; attempting to resolve on classpath under org/hibernate/");
                return this.openUrlStream(HBM_DTD_MAPPING.getMappedLocalUrl());
            }
            if (ALTERNATE_MAPPING_DTD.matches(publicID, systemID)) {
                log.debug((Object)"Recognized alternate hibernate-mapping identifier; attempting to resolve on classpath under org/hibernate/");
                return this.openUrlStream(ALTERNATE_MAPPING_DTD.getMappedLocalUrl());
            }
            if (LEGACY_HBM_DTD_MAPPING.matches(publicID, systemID)) {
                DeprecationLogger.DEPRECATION_LOGGER.recognizedObsoleteHibernateNamespace(LEGACY_HBM_DTD_MAPPING.getIdentifierBase(), HBM_DTD_MAPPING.getIdentifierBase());
                log.debug((Object)"Recognized legacy hibernate-mapping identifier; attempting to resolve on classpath under org/hibernate/");
                return this.openUrlStream(HBM_DTD_MAPPING.getMappedLocalUrl());
            }
            if (CFG_DTD_MAPPING.matches(publicID, systemID)) {
                log.debug((Object)"Recognized hibernate-configuration identifier; attempting to resolve on classpath under org/hibernate/");
                return this.openUrlStream(CFG_DTD_MAPPING.getMappedLocalUrl());
            }
            if (ALTERNATE_CFG_DTD.matches(publicID, systemID)) {
                log.debug((Object)"Recognized alternate hibernate-configuration identifier; attempting to resolve on classpath under org/hibernate/");
                return this.openUrlStream(ALTERNATE_CFG_DTD.getMappedLocalUrl());
            }
            if (LEGACY_CFG_DTD_MAPPING.matches(publicID, systemID)) {
                DeprecationLogger.DEPRECATION_LOGGER.recognizedObsoleteHibernateNamespace(LEGACY_CFG_DTD_MAPPING.getIdentifierBase(), CFG_DTD_MAPPING.getIdentifierBase());
                log.debug((Object)"Recognized hibernate-configuration identifier; attempting to resolve on classpath under org/hibernate/");
                return this.openUrlStream(CFG_DTD_MAPPING.getMappedLocalUrl());
            }
        }
        if (systemID != null && systemID.startsWith(CLASSPATH_EXTENSION_URL_BASE)) {
            log.debugf("Recognized `classpath:` identifier; attempting to resolve on classpath [%s]", (Object)systemID);
            String path = systemID.substring(CLASSPATH_EXTENSION_URL_BASE.length());
            InputStream stream = this.resolveInLocalNamespace(path);
            if (stream == null) {
                log.debugf("Unable to resolve [%s] on classpath", (Object)systemID);
            } else {
                log.debugf("Resolved [%s] on classpath", (Object)systemID);
            }
            return stream;
        }
        return null;
    }

    private InputStream openUrlStream(URL url) {
        try {
            return url.openStream();
        }
        catch (IOException e) {
            throw new XmlInfrastructureException("Could not open url stream : " + url.toExternalForm(), e);
        }
    }

    private InputStream resolveInLocalNamespace(String path) {
        try {
            return this.classLoaderService.locateResourceStream(path);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static class DtdMapping {
        private final String httpBase;
        private final String httpsBase;
        private final URL localSchemaUrl;

        public DtdMapping(String identifierBase, String resourceName) {
            this.httpBase = "http://" + identifierBase;
            this.httpsBase = "https://" + identifierBase;
            this.localSchemaUrl = LocalSchemaLocator.resolveLocalSchemaUrl(resourceName);
        }

        public String getIdentifierBase() {
            return this.httpBase;
        }

        public boolean matches(String publicId, String systemId) {
            if (publicId != null && (publicId.startsWith(this.httpBase) || publicId.matches(this.httpsBase))) {
                return true;
            }
            return systemId != null && (systemId.startsWith(this.httpBase) || systemId.matches(this.httpsBase));
        }

        public URL getMappedLocalUrl() {
            return this.localSchemaUrl;
        }
    }

    public static class NamespaceSchemaMapping {
        private final String namespace;
        private final URL localSchemaUrl;

        public NamespaceSchemaMapping(String namespace, String resourceName) {
            this.namespace = namespace;
            this.localSchemaUrl = LocalSchemaLocator.resolveLocalSchemaUrl(resourceName);
        }

        public boolean matches(String namespace) {
            return this.namespace.equals(namespace);
        }

        public URL getMappedLocalUrl() {
            return this.localSchemaUrl;
        }
    }
}

