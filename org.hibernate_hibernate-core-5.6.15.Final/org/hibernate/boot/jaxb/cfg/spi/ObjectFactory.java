/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlRegistry;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgCollectionCacheType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgConfigPropertyType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEntityCacheType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerGroupType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgHibernateConfiguration;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgMappingReferenceType;

@XmlRegistry
public class ObjectFactory {
    public JaxbCfgHibernateConfiguration createJaxbCfgHibernateConfiguration() {
        return new JaxbCfgHibernateConfiguration();
    }

    public JaxbCfgHibernateConfiguration.JaxbCfgSecurity createJaxbCfgHibernateConfigurationJaxbCfgSecurity() {
        return new JaxbCfgHibernateConfiguration.JaxbCfgSecurity();
    }

    public JaxbCfgHibernateConfiguration.JaxbCfgSessionFactory createJaxbCfgHibernateConfigurationJaxbCfgSessionFactory() {
        return new JaxbCfgHibernateConfiguration.JaxbCfgSessionFactory();
    }

    public JaxbCfgConfigPropertyType createJaxbCfgConfigPropertyType() {
        return new JaxbCfgConfigPropertyType();
    }

    public JaxbCfgMappingReferenceType createJaxbCfgMappingReferenceType() {
        return new JaxbCfgMappingReferenceType();
    }

    public JaxbCfgEntityCacheType createJaxbCfgEntityCacheType() {
        return new JaxbCfgEntityCacheType();
    }

    public JaxbCfgCollectionCacheType createJaxbCfgCollectionCacheType() {
        return new JaxbCfgCollectionCacheType();
    }

    public JaxbCfgEventListenerGroupType createJaxbCfgEventListenerGroupType() {
        return new JaxbCfgEventListenerGroupType();
    }

    public JaxbCfgEventListenerType createJaxbCfgEventListenerType() {
        return new JaxbCfgEventListenerType();
    }

    public JaxbCfgHibernateConfiguration.JaxbCfgSecurity.JaxbCfgGrant createJaxbCfgHibernateConfigurationJaxbCfgSecurityJaxbCfgGrant() {
        return new JaxbCfgHibernateConfiguration.JaxbCfgSecurity.JaxbCfgGrant();
    }
}

