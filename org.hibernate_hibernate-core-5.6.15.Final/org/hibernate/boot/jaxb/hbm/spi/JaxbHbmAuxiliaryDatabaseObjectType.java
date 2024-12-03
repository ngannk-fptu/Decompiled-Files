/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDialectScopeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="AuxiliaryDatabaseObjectType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"definition", "create", "drop", "dialectScope"})
public class JaxbHbmAuxiliaryDatabaseObjectType
implements Serializable {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmDefinition definition;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String create;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String drop;
    @XmlElement(name="dialect-scope", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmDialectScopeType> dialectScope;

    public JaxbHbmDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(JaxbHbmDefinition value) {
        this.definition = value;
    }

    public String getCreate() {
        return this.create;
    }

    public void setCreate(String value) {
        this.create = value;
    }

    public String getDrop() {
        return this.drop;
    }

    public void setDrop(String value) {
        this.drop = value;
    }

    public List<JaxbHbmDialectScopeType> getDialectScope() {
        if (this.dialectScope == null) {
            this.dialectScope = new ArrayList<JaxbHbmDialectScopeType>();
        }
        return this.dialectScope;
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="")
    public static class JaxbHbmDefinition
    implements Serializable {
        @XmlAttribute(name="class", required=true)
        protected String clazz;

        public String getClazz() {
            return this.clazz;
        }

        public void setClazz(String value) {
            this.clazz = value;
        }
    }
}

