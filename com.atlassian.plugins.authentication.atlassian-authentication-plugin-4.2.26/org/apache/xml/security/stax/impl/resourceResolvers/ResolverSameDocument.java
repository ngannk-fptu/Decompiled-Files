/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public class ResolverSameDocument
implements ResourceResolver,
ResourceResolverLookup {
    private String id;
    private boolean firstElementOccured = false;

    public ResolverSameDocument() {
    }

    public ResolverSameDocument(String uri) {
        this.id = XMLSecurityUtils.dropReferenceMarker(uri);
    }

    public String getId() {
        return this.id;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri != null && (uri.isEmpty() || uri.charAt(0) == '#')) {
            if (uri.startsWith("#xpointer")) {
                return null;
            }
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverSameDocument(uri);
    }

    @Override
    public boolean isSameDocumentReference() {
        return true;
    }

    @Override
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        return this.matches(xmlSecStartElement, XMLSecurityConstants.ATT_NULL_Id);
    }

    public boolean matches(XMLSecStartElement xmlSecStartElement, QName idAttributeNS) {
        if (this.id.isEmpty()) {
            if (this.firstElementOccured) {
                return false;
            }
            this.firstElementOccured = true;
            return true;
        }
        Attribute attribute = xmlSecStartElement.getAttributeByName(idAttributeNS);
        return attribute != null && attribute.getValue().equals(this.id);
    }

    @Override
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        return null;
    }
}

