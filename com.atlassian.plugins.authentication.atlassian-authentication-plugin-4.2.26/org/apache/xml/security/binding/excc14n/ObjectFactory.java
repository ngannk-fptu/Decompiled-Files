/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElementDecl
 *  javax.xml.bind.annotation.XmlRegistry
 */
package org.apache.xml.security.binding.excc14n;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.binding.excc14n.InclusiveNamespaces;

@XmlRegistry
public class ObjectFactory {
    private static final QName _InclusiveNamespaces_QNAME = new QName("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces");

    public InclusiveNamespaces createInclusiveNamespaces() {
        return new InclusiveNamespaces();
    }

    @XmlElementDecl(namespace="http://www.w3.org/2001/10/xml-exc-c14n#", name="InclusiveNamespaces")
    public JAXBElement<InclusiveNamespaces> createInclusiveNamespaces(InclusiveNamespaces value) {
        return new JAXBElement(_InclusiveNamespaces_QNAME, InclusiveNamespaces.class, null, (Object)value);
    }
}

