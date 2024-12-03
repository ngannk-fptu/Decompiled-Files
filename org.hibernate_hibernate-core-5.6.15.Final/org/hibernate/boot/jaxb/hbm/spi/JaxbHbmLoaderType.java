/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="loader-type", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmLoaderType
implements Serializable {
    @XmlAttribute(name="query-ref", required=true)
    protected String queryRef;

    public String getQueryRef() {
        return this.queryRef;
    }

    public void setQueryRef(String value) {
        this.queryRef = value;
    }
}

