/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.configuration.PropertyType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropertiesType", namespace="http://www.xmlsecurity.org/NS/configuration", propOrder={"property"})
public class PropertiesType {
    @XmlElement(name="Property", namespace="http://www.xmlsecurity.org/NS/configuration")
    protected List<PropertyType> property;

    public List<PropertyType> getProperty() {
        if (this.property == null) {
            this.property = new ArrayList<PropertyType>();
        }
        return this.property;
    }
}

