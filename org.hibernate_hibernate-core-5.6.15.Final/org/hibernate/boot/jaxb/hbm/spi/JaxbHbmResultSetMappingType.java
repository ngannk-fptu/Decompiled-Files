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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryCollectionLoadReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryJoinReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryScalarReturnType;
import org.hibernate.boot.jaxb.hbm.spi.ResultSetMappingBindingDefinition;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ResultSetMappingType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"valueMappingSources"})
public class JaxbHbmResultSetMappingType
implements Serializable,
ResultSetMappingBindingDefinition {
    @XmlElements(value={@XmlElement(name="return-scalar", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmNativeQueryScalarReturnType.class), @XmlElement(name="return", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmNativeQueryReturnType.class), @XmlElement(name="return-join", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmNativeQueryJoinReturnType.class), @XmlElement(name="load-collection", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmNativeQueryCollectionLoadReturnType.class)})
    protected List<Serializable> valueMappingSources;
    @XmlAttribute(name="name", required=true)
    protected String name;

    @Override
    public List<Serializable> getValueMappingSources() {
        if (this.valueMappingSources == null) {
            this.valueMappingSources = new ArrayList<Serializable>();
        }
        return this.valueMappingSources;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

