/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.llom;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.impl.llom.NamespaceURIInterningNamespaceContextWrapper;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class NamespaceURIInterningXMLStreamReaderWrapper
extends XMLStreamReaderWrapper
implements OMXMLStreamReader {
    private NamespaceURIInterningNamespaceContextWrapper namespaceContextWrapper;

    public NamespaceURIInterningXMLStreamReaderWrapper(OMXMLStreamReader parent) {
        super(parent);
    }

    private static String intern(String s) {
        return s == null ? null : s.intern();
    }

    public String getAttributeNamespace(int index) {
        return NamespaceURIInterningXMLStreamReaderWrapper.intern(super.getAttributeNamespace(index));
    }

    public String getNamespaceURI() {
        return NamespaceURIInterningXMLStreamReaderWrapper.intern(super.getNamespaceURI());
    }

    public String getNamespaceURI(int index) {
        return NamespaceURIInterningXMLStreamReaderWrapper.intern(super.getNamespaceURI(index));
    }

    public String getNamespaceURI(String prefix) {
        return NamespaceURIInterningXMLStreamReaderWrapper.intern(super.getNamespaceURI(prefix));
    }

    public DataHandler getDataHandler(String blobcid) {
        return ((OMXMLStreamReader)this.getParent()).getDataHandler(blobcid);
    }

    public NamespaceContext getNamespaceContext() {
        NamespaceContext namespaceContext = super.getNamespaceContext();
        if (this.namespaceContextWrapper == null || this.namespaceContextWrapper.getParent() != namespaceContext) {
            this.namespaceContextWrapper = new NamespaceURIInterningNamespaceContextWrapper(namespaceContext);
        }
        return this.namespaceContextWrapper;
    }

    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)this.getParent()).isInlineMTOM();
    }

    public void setInlineMTOM(boolean value) {
        ((OMXMLStreamReader)this.getParent()).setInlineMTOM(value);
    }
}

