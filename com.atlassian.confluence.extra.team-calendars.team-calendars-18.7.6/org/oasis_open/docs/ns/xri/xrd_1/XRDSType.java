/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ns.xri.xrd_1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ns.xri.xrd_1.XRDType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="XRDSType", propOrder={"xrd"})
public class XRDSType {
    @XmlElement(name="XRD")
    protected List<XRDType> xrd;
    @XmlAttribute
    @XmlSchemaType(name="anyURI")
    protected String ref;

    public List<XRDType> getXRD() {
        if (this.xrd == null) {
            this.xrd = new ArrayList<XRDType>();
        }
        return this.xrd;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String value) {
        this.ref = value;
    }
}

