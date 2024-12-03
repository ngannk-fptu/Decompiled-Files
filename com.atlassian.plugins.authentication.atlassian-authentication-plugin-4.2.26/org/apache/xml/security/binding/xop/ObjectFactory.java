/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.apache.xml.security.binding.xop;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.xop.Include;

@XmlRegistry
public class ObjectFactory {
    private static final QName _Include_QNAME = new QName("http://www.w3.org/2004/08/xop/include", "Include");

    public Include createInclude() {
        return new Include();
    }

    @XmlElementDecl(namespace="http://www.w3.org/2004/08/xop/include", name="Include")
    public JAXBElement<Include> createInclude(Include value) {
        return new JAXBElement(_Include_QNAME, Include.class, null, (Object)value);
    }
}

