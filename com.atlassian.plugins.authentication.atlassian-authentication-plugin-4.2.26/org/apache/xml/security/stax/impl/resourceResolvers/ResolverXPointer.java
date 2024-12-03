/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public class ResolverXPointer
implements ResourceResolver,
ResourceResolverLookup {
    private Pattern pattern = Pattern.compile("^#xpointer\\((/)|(id\\([\"']([^\"']*)[\"']\\))\\)");
    private String id;
    private boolean rootNodeOccured = false;

    public ResolverXPointer() {
    }

    public ResolverXPointer(String uri) {
        Matcher matcher = this.pattern.matcher(uri);
        if (matcher.find() && matcher.groupCount() == 3) {
            String slash = matcher.group(1);
            if (slash != null) {
                this.id = null;
                return;
            }
            String id = matcher.group(3);
            if (id != null) {
                this.id = id;
                return;
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public boolean isRootNodeOccured() {
        return this.rootNodeOccured;
    }

    public void setRootNodeOccured(boolean rootNodeOccured) {
        this.rootNodeOccured = rootNodeOccured;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri != null && this.pattern.matcher(uri).find()) {
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverXPointer(uri);
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
        if (this.id == null) {
            if (!this.rootNodeOccured) {
                this.rootNodeOccured = true;
                return true;
            }
            return false;
        }
        Attribute attribute = xmlSecStartElement.getAttributeByName(idAttributeNS);
        return attribute != null && attribute.getValue().equals(this.id);
    }

    @Override
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        return null;
    }
}

