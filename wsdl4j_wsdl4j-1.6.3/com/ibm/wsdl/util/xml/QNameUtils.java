/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.util.xml;

import javax.xml.namespace.QName;
import org.w3c.dom.Node;

public class QNameUtils {
    public static boolean matches(QName qname, Node node) {
        return node != null && qname.equals(QNameUtils.newQName(node));
    }

    public static QName newQName(Node node) {
        if (node != null) {
            return new QName(node.getNamespaceURI(), node.getLocalName());
        }
        return new QName(null, null);
    }
}

