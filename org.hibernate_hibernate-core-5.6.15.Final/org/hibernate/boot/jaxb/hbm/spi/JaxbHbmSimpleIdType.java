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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmGeneratorSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.TypeContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SimpleIdType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column", "type", "generator"})
public class JaxbHbmSimpleIdType
extends JaxbHbmToolingHintContainer
implements Serializable,
SingularAttributeInfo,
ToolingHintContainer,
TypeContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmColumnType> column;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmTypeSpecificationType type;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmGeneratorSpecificationType generator;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="length")
    protected Integer length;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="type")
    protected String typeAttribute;
    @XmlAttribute(name="unsaved-value")
    protected String unsavedValue;

    public List<JaxbHbmColumnType> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbHbmColumnType>();
        }
        return this.column;
    }

    @Override
    public JaxbHbmTypeSpecificationType getType() {
        return this.type;
    }

    public void setType(JaxbHbmTypeSpecificationType value) {
        this.type = value;
    }

    public JaxbHbmGeneratorSpecificationType getGenerator() {
        return this.generator;
    }

    public void setGenerator(JaxbHbmGeneratorSpecificationType value) {
        this.generator = value;
    }

    @Override
    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
    }

    public String getColumnAttribute() {
        return this.columnAttribute;
    }

    public void setColumnAttribute(String value) {
        this.columnAttribute = value;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    @Override
    public String getTypeAttribute() {
        return this.typeAttribute;
    }

    public void setTypeAttribute(String value) {
        this.typeAttribute = value;
    }

    public String getUnsavedValue() {
        return this.unsavedValue;
    }

    public void setUnsavedValue(String value) {
        this.unsavedValue = value;
    }
}

