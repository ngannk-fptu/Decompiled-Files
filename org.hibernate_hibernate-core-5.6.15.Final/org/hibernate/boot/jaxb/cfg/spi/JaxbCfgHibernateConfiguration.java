/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.cfg.spi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgCollectionCacheType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgConfigPropertyType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEntityCacheType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerGroupType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgMappingReferenceType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"sessionFactory", "security"})
@XmlRootElement(name="hibernate-configuration", namespace="http://www.hibernate.org/xsd/orm/cfg")
public class JaxbCfgHibernateConfiguration {
    @XmlElement(name="session-factory", namespace="http://www.hibernate.org/xsd/orm/cfg", required=true)
    protected JaxbCfgSessionFactory sessionFactory;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
    protected JaxbCfgSecurity security;

    public JaxbCfgSessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setSessionFactory(JaxbCfgSessionFactory value) {
        this.sessionFactory = value;
    }

    public JaxbCfgSecurity getSecurity() {
        return this.security;
    }

    public void setSecurity(JaxbCfgSecurity value) {
        this.security = value;
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="", propOrder={"property", "mapping", "classCacheOrCollectionCache", "event", "listener"})
    public static class JaxbCfgSessionFactory {
        @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
        protected List<JaxbCfgConfigPropertyType> property;
        @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
        protected List<JaxbCfgMappingReferenceType> mapping;
        @XmlElements(value={@XmlElement(name="class-cache", namespace="http://www.hibernate.org/xsd/orm/cfg", type=JaxbCfgEntityCacheType.class), @XmlElement(name="collection-cache", namespace="http://www.hibernate.org/xsd/orm/cfg", type=JaxbCfgCollectionCacheType.class)})
        protected List<Object> classCacheOrCollectionCache;
        @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
        protected List<JaxbCfgEventListenerGroupType> event;
        @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
        protected List<JaxbCfgEventListenerType> listener;
        @XmlAttribute(name="name")
        protected String name;

        public List<JaxbCfgConfigPropertyType> getProperty() {
            if (this.property == null) {
                this.property = new ArrayList<JaxbCfgConfigPropertyType>();
            }
            return this.property;
        }

        public List<JaxbCfgMappingReferenceType> getMapping() {
            if (this.mapping == null) {
                this.mapping = new ArrayList<JaxbCfgMappingReferenceType>();
            }
            return this.mapping;
        }

        public List<Object> getClassCacheOrCollectionCache() {
            if (this.classCacheOrCollectionCache == null) {
                this.classCacheOrCollectionCache = new ArrayList<Object>();
            }
            return this.classCacheOrCollectionCache;
        }

        public List<JaxbCfgEventListenerGroupType> getEvent() {
            if (this.event == null) {
                this.event = new ArrayList<JaxbCfgEventListenerGroupType>();
            }
            return this.event;
        }

        public List<JaxbCfgEventListenerType> getListener() {
            if (this.listener == null) {
                this.listener = new ArrayList<JaxbCfgEventListenerType>();
            }
            return this.listener;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String value) {
            this.name = value;
        }
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="", propOrder={"grant"})
    public static class JaxbCfgSecurity {
        @XmlElement(namespace="http://www.hibernate.org/xsd/orm/cfg")
        protected List<JaxbCfgGrant> grant;
        @XmlAttribute(name="context", required=true)
        protected String context;

        public List<JaxbCfgGrant> getGrant() {
            if (this.grant == null) {
                this.grant = new ArrayList<JaxbCfgGrant>();
            }
            return this.grant;
        }

        public String getContext() {
            return this.context;
        }

        public void setContext(String value) {
            this.context = value;
        }

        @XmlAccessorType(value=XmlAccessType.FIELD)
        @XmlType(name="")
        public static class JaxbCfgGrant {
            @XmlAttribute(name="actions", required=true)
            protected String actions;
            @XmlAttribute(name="entity-name", required=true)
            protected String entityName;
            @XmlAttribute(name="role", required=true)
            protected String role;

            public String getActions() {
                return this.actions;
            }

            public void setActions(String value) {
                this.actions = value;
            }

            public String getEntityName() {
                return this.entityName;
            }

            public void setEntityName(String value) {
                this.entityName = value;
            }

            public String getRole() {
                return this.role;
            }

            public void setRole(String value) {
                this.role = value;
            }
        }
    }
}

