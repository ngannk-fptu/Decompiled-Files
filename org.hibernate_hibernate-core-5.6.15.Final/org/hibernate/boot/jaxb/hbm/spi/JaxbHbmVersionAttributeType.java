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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBaseVersionAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnsavedValueVersionEnum;
import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="VersionAttributeType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"column"})
public class JaxbHbmVersionAttributeType
extends JaxbHbmBaseVersionAttributeType
implements Serializable,
SingularAttributeInfo,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmColumnType> column;
    @XmlAttribute(name="insert")
    protected Boolean insert;
    @XmlAttribute(name="type")
    protected String type;
    @XmlAttribute(name="unsaved-value")
    protected JaxbHbmUnsavedValueVersionEnum unsavedValue;

    public List<JaxbHbmColumnType> getColumn() {
        if (this.column == null) {
            this.column = new ArrayList<JaxbHbmColumnType>();
        }
        return this.column;
    }

    public Boolean isInsert() {
        return this.insert;
    }

    public void setInsert(Boolean value) {
        this.insert = value;
    }

    public String getType() {
        if (this.type == null) {
            return "integer";
        }
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public JaxbHbmUnsavedValueVersionEnum getUnsavedValue() {
        if (this.unsavedValue == null) {
            return JaxbHbmUnsavedValueVersionEnum.UNDEFINED;
        }
        return this.unsavedValue;
    }

    public void setUnsavedValue(JaxbHbmUnsavedValueVersionEnum value) {
        this.unsavedValue = value;
    }
}

