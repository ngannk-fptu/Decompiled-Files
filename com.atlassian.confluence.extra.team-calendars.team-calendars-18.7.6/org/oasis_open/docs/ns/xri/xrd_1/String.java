/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyAttribute
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 */
package org.oasis_open.docs.ns.xri.xrd_1;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import org.oasis_open.docs.ns.xri.xrd_1.PropertyType;
import org.oasis_open.docs.ns.xri.xrd_1.TitleType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="string", propOrder={"value"})
@XmlSeeAlso(value={TitleType.class, PropertyType.class})
public class String {
    @XmlValue
    protected java.lang.String value;
    @XmlAnyAttribute
    private Map<QName, java.lang.String> otherAttributes = new HashMap<QName, java.lang.String>();

    public java.lang.String getValue() {
        return this.value;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    public Map<QName, java.lang.String> getOtherAttributes() {
        return this.otherAttributes;
    }
}

