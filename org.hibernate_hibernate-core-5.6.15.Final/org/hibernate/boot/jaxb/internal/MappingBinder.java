/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.jaxb.internal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import org.hibernate.boot.UnsupportedOrmXsdVersionException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.internal.AbstractBinder;
import org.hibernate.boot.jaxb.internal.stax.HbmEventReader;
import org.hibernate.boot.jaxb.internal.stax.JpaOrmXmlEventReader;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityMappings;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.xsd.MappingXsdSupport;
import org.hibernate.internal.util.config.ConfigurationException;
import org.jboss.logging.Logger;

public class MappingBinder
extends AbstractBinder {
    private static final Logger log = Logger.getLogger(MappingBinder.class);
    private final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
    private JAXBContext hbmJaxbContext;
    private JAXBContext entityMappingsJaxbContext;

    public MappingBinder(ClassLoaderService classLoaderService, boolean validateXml) {
        super(classLoaderService, validateXml);
    }

    @Override
    protected Binding<?> doBind(XMLEventReader staxEventReader, StartElement rootElementStartEvent, Origin origin) {
        String rootElementLocalName = rootElementStartEvent.getName().getLocalPart();
        if ("hibernate-mapping".equals(rootElementLocalName)) {
            log.debugf("Performing JAXB binding of hbm.xml document : %s", (Object)origin.toString());
            HbmEventReader hbmReader = new HbmEventReader(staxEventReader, this.xmlEventFactory);
            JaxbHbmHibernateMapping hbmBindings = (JaxbHbmHibernateMapping)this.jaxb(hbmReader, MappingXsdSupport.INSTANCE.hbmXsd().getSchema(), this.hbmJaxbContext(), origin);
            return new Binding<JaxbHbmHibernateMapping>(hbmBindings, origin);
        }
        try {
            log.debugf("Performing JAXB binding of orm.xml document : %s", (Object)origin.toString());
            JpaOrmXmlEventReader reader = new JpaOrmXmlEventReader(staxEventReader, this.xmlEventFactory);
            JaxbEntityMappings bindingRoot = (JaxbEntityMappings)this.jaxb(reader, MappingXsdSupport.INSTANCE.latestJpaDescriptor().getSchema(), this.entityMappingsJaxbContext(), origin);
            return new Binding<JaxbEntityMappings>(bindingRoot, origin);
        }
        catch (JpaOrmXmlEventReader.BadVersionException e) {
            throw new UnsupportedOrmXsdVersionException(e.getRequestedVersion(), origin);
        }
    }

    private JAXBContext hbmJaxbContext() {
        if (this.hbmJaxbContext == null) {
            try {
                this.hbmJaxbContext = JAXBContext.newInstance((Class[])new Class[]{JaxbHbmHibernateMapping.class});
            }
            catch (JAXBException e) {
                throw new ConfigurationException("Unable to build hbm.xml JAXBContext", e);
            }
        }
        return this.hbmJaxbContext;
    }

    private JAXBContext entityMappingsJaxbContext() {
        if (this.entityMappingsJaxbContext == null) {
            try {
                this.entityMappingsJaxbContext = JAXBContext.newInstance((Class[])new Class[]{JaxbEntityMappings.class});
            }
            catch (JAXBException e) {
                throw new ConfigurationException("Unable to build orm.xml JAXBContext", e);
            }
        }
        return this.entityMappingsJaxbContext;
    }
}

