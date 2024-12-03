/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ToolingHintContainer", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"toolingHints"})
public abstract class JaxbHbmToolingHintContainer
implements Serializable,
ToolingHintContainer {
    @XmlElement(name="meta", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmToolingHintType> toolingHints;

    @Override
    public List<JaxbHbmToolingHintType> getToolingHints() {
        if (this.toolingHints == null) {
            this.toolingHints = new ArrayList<JaxbHbmToolingHintType>();
        }
        return this.toolingHints;
    }
}

