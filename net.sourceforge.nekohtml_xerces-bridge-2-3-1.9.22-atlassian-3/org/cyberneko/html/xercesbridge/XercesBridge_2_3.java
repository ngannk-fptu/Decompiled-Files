/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.NamespaceContext
 *  org.cyberneko.html.xercesbridge.XercesBridge_2_2
 */
package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.NamespaceContext;
import org.cyberneko.html.xercesbridge.XercesBridge_2_2;

public class XercesBridge_2_3
extends XercesBridge_2_2 {
    public XercesBridge_2_3() throws InstantiationException {
        try {
            Class[] args = new Class[]{String.class, String.class};
            NamespaceContext.class.getMethod("declarePrefix", args);
        }
        catch (NoSuchMethodException e) {
            throw new InstantiationException(e.getMessage());
        }
    }

    public void NamespaceContext_declarePrefix(NamespaceContext namespaceContext, String ns, String avalue) {
        namespaceContext.declarePrefix(ns, avalue);
    }
}

