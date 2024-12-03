/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmGeneratorSpecificationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnsavedValueCompositeIdEnum;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CompositeIdType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"keyPropertyOrKeyManyToOne", "generator"})
public class JaxbHbmCompositeIdType
extends JaxbHbmToolingHintContainer
implements Serializable,
ToolingHintContainer {
    @XmlElements(value={@XmlElement(name="key-property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeKeyBasicAttributeType.class), @XmlElement(name="key-many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeKeyManyToOneType.class)})
    protected List<JaxbHbmToolingHintContainer> keyPropertyOrKeyManyToOne;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmGeneratorSpecificationType generator;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="mapped")
    protected Boolean mapped;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="unsaved-value")
    protected JaxbHbmUnsavedValueCompositeIdEnum unsavedValue;

    public List<JaxbHbmToolingHintContainer> getKeyPropertyOrKeyManyToOne() {
        if (this.keyPropertyOrKeyManyToOne == null) {
            this.keyPropertyOrKeyManyToOne = new ArrayList<JaxbHbmToolingHintContainer>();
        }
        return this.keyPropertyOrKeyManyToOne;
    }

    public JaxbHbmGeneratorSpecificationType getGenerator() {
        return this.generator;
    }

    public void setGenerator(JaxbHbmGeneratorSpecificationType value) {
        this.generator = value;
    }

    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public boolean isMapped() {
        if (this.mapped == null) {
            return false;
        }
        return this.mapped;
    }

    public void setMapped(Boolean value) {
        this.mapped = value;
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

    public JaxbHbmUnsavedValueCompositeIdEnum getUnsavedValue() {
        if (this.unsavedValue == null) {
            return JaxbHbmUnsavedValueCompositeIdEnum.UNDEFINED;
        }
        return this.unsavedValue;
    }

    public void setUnsavedValue(JaxbHbmUnsavedValueCompositeIdEnum value) {
        this.unsavedValue = value;
    }
}

