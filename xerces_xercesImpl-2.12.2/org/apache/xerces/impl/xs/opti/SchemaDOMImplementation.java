/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

final class SchemaDOMImplementation
implements DOMImplementation {
    private static final SchemaDOMImplementation singleton = new SchemaDOMImplementation();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    private SchemaDOMImplementation() {
    }

    @Override
    public Document createDocument(String string, String string2, DocumentType documentType) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public DocumentType createDocumentType(String string, String string2, String string3) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Object getFeature(String string, String string2) {
        if (singleton.hasFeature(string, string2)) {
            return singleton;
        }
        return null;
    }

    @Override
    public boolean hasFeature(String string, String string2) {
        boolean bl = string2 == null || string2.length() == 0;
        return !(!string.equalsIgnoreCase("Core") && !string.equalsIgnoreCase("XML") || !bl && !string2.equals("1.0") && !string2.equals("2.0") && !string2.equals("3.0"));
    }
}

