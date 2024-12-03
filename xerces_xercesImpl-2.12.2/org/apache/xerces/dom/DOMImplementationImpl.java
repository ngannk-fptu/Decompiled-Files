/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDOMImplementationImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

public class DOMImplementationImpl
extends CoreDOMImplementationImpl
implements DOMImplementation {
    static final DOMImplementationImpl singleton = new DOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public boolean hasFeature(String string, String string2) {
        boolean bl = super.hasFeature(string, string2);
        if (!bl) {
            boolean bl2;
            boolean bl3 = bl2 = string2 == null || string2.length() == 0;
            if (string.startsWith("+")) {
                string = string.substring(1);
            }
            return string.equalsIgnoreCase("Events") && (bl2 || string2.equals("2.0")) || string.equalsIgnoreCase("MutationEvents") && (bl2 || string2.equals("2.0")) || string.equalsIgnoreCase("Traversal") && (bl2 || string2.equals("2.0")) || string.equalsIgnoreCase("Range") && (bl2 || string2.equals("2.0")) || string.equalsIgnoreCase("MutationEvents") && (bl2 || string2.equals("2.0"));
        }
        return bl;
    }

    @Override
    protected CoreDocumentImpl createDocument(DocumentType documentType) {
        return new DocumentImpl(documentType);
    }
}

