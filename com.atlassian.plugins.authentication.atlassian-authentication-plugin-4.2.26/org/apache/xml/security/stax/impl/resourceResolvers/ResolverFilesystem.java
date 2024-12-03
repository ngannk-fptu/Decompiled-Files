/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.InputStream;
import java.net.URI;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public class ResolverFilesystem
implements ResourceResolver,
ResourceResolverLookup {
    private String uri;
    private String baseURI;

    public ResolverFilesystem() {
    }

    public ResolverFilesystem(String uri, String baseURI) {
        this.uri = uri;
        this.baseURI = baseURI;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri == null) {
            return null;
        }
        if (uri.startsWith("file:") || baseURI != null && baseURI.startsWith("file:")) {
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverFilesystem(uri, baseURI);
    }

    @Override
    public boolean isSameDocumentReference() {
        return false;
    }

    @Override
    public boolean matches(XMLSecStartElement xmlSecStartElement) {
        return false;
    }

    @Override
    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException {
        try {
            URI tmp = this.baseURI == null || this.baseURI.length() == 0 ? new URI(this.uri) : new URI(this.baseURI).resolve(this.uri);
            if (tmp.getFragment() != null) {
                tmp = new URI(tmp.getScheme(), tmp.getSchemeSpecificPart(), null);
            }
            return tmp.toURL().openStream();
        }
        catch (Exception e) {
            throw new XMLSecurityException(e);
        }
    }
}

