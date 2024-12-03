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
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumnResult;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="constructor-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"column"})
public class JaxbConstructorResult
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", required=true)
    protected List<JaxbColumnResult> column;
    @XmlAttribute(name="target-class", required=true)
    protected String targetClass;

    public List<JaxbColumnResult> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbColumnResult>();
        }
        return this.column;
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(String value) {
        this.targetClass = value;
    }
}

