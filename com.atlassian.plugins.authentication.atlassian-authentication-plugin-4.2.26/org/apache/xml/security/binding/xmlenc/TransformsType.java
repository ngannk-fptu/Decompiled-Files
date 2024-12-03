/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmlenc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmldsig.TransformType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TransformsType", namespace="http://www.w3.org/2001/04/xmlenc#", propOrder={"transform"})
public class TransformsType {
    @XmlElement(name="Transform", namespace="http://www.w3.org/2000/09/xmldsig#", required=true)
    protected List<TransformType> transform;

    public List<TransformType> getTransform() {
        if (this.transform == null) {
            this.transform = new ArrayList<TransformType>();
        }
        return this.transform;
    }
}

