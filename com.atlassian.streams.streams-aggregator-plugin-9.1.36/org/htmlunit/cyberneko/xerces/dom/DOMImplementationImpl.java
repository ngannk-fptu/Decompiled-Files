/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDOMImplementationImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DocumentImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;

public class DOMImplementationImpl
extends CoreDOMImplementationImpl {
    private static final DOMImplementationImpl singleton = new DOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public boolean hasFeature(String feature, String version) {
        boolean result = super.hasFeature(feature, version);
        if (!result) {
            boolean anyVersion;
            boolean bl = anyVersion = version == null || version.length() == 0;
            if (feature.startsWith("+")) {
                feature = feature.substring(1);
            }
            return "Events".equalsIgnoreCase(feature) && (anyVersion || "2.0".equals(version)) || "MutationEvents".equalsIgnoreCase(feature) && (anyVersion || "2.0".equals(version)) || "Traversal".equalsIgnoreCase(feature) && (anyVersion || "2.0".equals(version)) || "Range".equalsIgnoreCase(feature) && (anyVersion || "2.0".equals(version)) || "MutationEvents".equalsIgnoreCase(feature) && (anyVersion || "2.0".equals(version));
        }
        return result;
    }

    @Override
    protected CoreDocumentImpl createDocument(DocumentType doctype) {
        return new DocumentImpl(doctype);
    }
}

