/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.impl.Version
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 */
package org.cyberneko.html.xercesbridge;

import org.apache.xerces.impl.Version;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class XercesBridge_2_0
extends XercesBridge {
    protected XercesBridge_2_0() {
    }

    @Override
    public String getVersion() {
        return Version.fVersion;
    }

    @Override
    public void XMLDocumentHandler_startPrefixMapping(XMLDocumentHandler documentHandler, String prefix, String uri, Augmentations augs) {
        documentHandler.startPrefixMapping(prefix, uri, augs);
    }

    @Override
    public void XMLDocumentHandler_endPrefixMapping(XMLDocumentHandler documentHandler, String prefix, Augmentations augs) {
        documentHandler.endPrefixMapping(prefix, augs);
    }

    @Override
    public void XMLDocumentHandler_startDocument(XMLDocumentHandler documentHandler, XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) {
        documentHandler.startDocument(locator, encoding, augs);
    }
}

