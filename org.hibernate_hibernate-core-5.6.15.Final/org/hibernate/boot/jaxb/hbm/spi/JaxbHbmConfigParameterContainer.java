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
import org.hibernate.boot.jaxb.hbm.spi.ConfigParameterContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmConfigParameterType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ConfigParameterContainer", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"configParameters"})
public abstract class JaxbHbmConfigParameterContainer
implements Serializable,
ConfigParameterContainer {
    @XmlElement(name="param", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmConfigParameterType> configParameters;

    @Override
    public List<JaxbHbmConfigParameterType> getConfigParameters() {
        if (this.configParameters == null) {
            this.configParameters = new ArrayList<JaxbHbmConfigParameterType>();
        }
        return this.configParameters;
    }
}

