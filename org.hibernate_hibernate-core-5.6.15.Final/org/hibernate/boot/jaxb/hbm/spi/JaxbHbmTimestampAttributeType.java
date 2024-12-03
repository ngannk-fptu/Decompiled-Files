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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBaseVersionAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTimestampSourceEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnsavedValueTimestampEnum;
import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TimestampAttributeType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmTimestampAttributeType
extends JaxbHbmBaseVersionAttributeType
implements Serializable,
SingularAttributeInfo,
ToolingHintContainer {
    @XmlAttribute(name="source")
    protected JaxbHbmTimestampSourceEnum source;
    @XmlAttribute(name="unsaved-value")
    protected JaxbHbmUnsavedValueTimestampEnum unsavedValue;

    public JaxbHbmTimestampSourceEnum getSource() {
        if (this.source == null) {
            return JaxbHbmTimestampSourceEnum.VM;
        }
        return this.source;
    }

    public void setSource(JaxbHbmTimestampSourceEnum value) {
        this.source = value;
    }

    public JaxbHbmUnsavedValueTimestampEnum getUnsavedValue() {
        if (this.unsavedValue == null) {
            return JaxbHbmUnsavedValueTimestampEnum.NULL;
        }
        return this.unsavedValue;
    }

    public void setUnsavedValue(JaxbHbmUnsavedValueTimestampEnum value) {
        this.unsavedValue = value;
    }
}

