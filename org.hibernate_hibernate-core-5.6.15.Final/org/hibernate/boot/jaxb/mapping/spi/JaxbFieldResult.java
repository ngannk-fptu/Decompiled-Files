/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="field-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbFieldResult
implements Serializable {
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="column", required=true)
    protected String column;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String value) {
        this.column = value;
    }
}

