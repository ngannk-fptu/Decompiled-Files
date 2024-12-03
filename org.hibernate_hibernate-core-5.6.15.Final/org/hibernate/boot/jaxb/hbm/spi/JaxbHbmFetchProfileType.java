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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="FetchProfileType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"fetch"})
public class JaxbHbmFetchProfileType
implements Serializable {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFetch> fetch;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public List<JaxbHbmFetch> getFetch() {
        if (this.fetch == null) {
            this.fetch = new ArrayList<JaxbHbmFetch>();
        }
        return this.fetch;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    @XmlType(name="")
    public static class JaxbHbmFetch
    implements Serializable {
        @XmlAttribute(name="association", required=true)
        protected String association;
        @XmlAttribute(name="entity")
        protected String entity;
        @XmlAttribute(name="style")
        protected JaxbHbmFetchStyleEnum style;

        public String getAssociation() {
            return this.association;
        }

        public void setAssociation(String value) {
            this.association = value;
        }

        public String getEntity() {
            return this.entity;
        }

        public void setEntity(String value) {
            this.entity = value;
        }

        public JaxbHbmFetchStyleEnum getStyle() {
            if (this.style == null) {
                return JaxbHbmFetchStyleEnum.JOIN;
            }
            return this.style;
        }

        public void setStyle(JaxbHbmFetchStyleEnum value) {
            this.style = value;
        }
    }
}

