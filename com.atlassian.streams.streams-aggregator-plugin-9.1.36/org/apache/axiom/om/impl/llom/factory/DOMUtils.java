/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import org.w3c.dom.Attr;

final class DOMUtils {
    private DOMUtils() {
    }

    static boolean isNSDecl(Attr attr) {
        return "http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI());
    }

    static String getNSDeclPrefix(Attr attr) {
        String prefix = attr.getPrefix();
        return prefix == null ? null : attr.getLocalName();
    }
}

