/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

public class AttributeHelper {
    public static void importOMAttribute(OMAttribute omAttribute, OMElement omElement) {
        if (omAttribute.getOMFactory().getClass().isInstance(omElement.getOMFactory())) {
            omElement.addAttribute(omAttribute);
        } else {
            OMNamespace ns = omAttribute.getNamespace();
            omElement.addAttribute(omAttribute.getLocalName(), omAttribute.getAttributeValue(), omElement.getOMFactory().createOMNamespace(ns.getNamespaceURI(), ns.getPrefix()));
        }
    }
}

