/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.hbm.spi.Adapter7;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.tuple.GenerationTiming;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BaseVersionAttributeType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public abstract class JaxbHbmBaseVersionAttributeType
extends JaxbHbmToolingHintContainer
implements Serializable {
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="generated")
    @XmlJavaTypeAdapter(value=Adapter7.class)
    protected GenerationTiming generated;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="node")
    protected String node;

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

    public GenerationTiming getGenerated() {
        if (this.generated == null) {
            return new Adapter7().unmarshal("never");
        }
        return this.generated;
    }

    public void setGenerated(GenerationTiming value) {
        this.generated = value;
    }

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
}

