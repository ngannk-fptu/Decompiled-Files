/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.resourceResolvers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.ResourceResolverLookup;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public class ResolverHttp
implements ResourceResolver,
ResourceResolverLookup {
    private static Proxy proxy;
    private String uri;
    private String baseURI;
    private Pattern pattern = Pattern.compile("^http[s]?://.*");

    public ResolverHttp() {
    }

    public ResolverHttp(String uri, String baseURI) {
        this.uri = uri;
        this.baseURI = baseURI;
    }

    public static void setProxy(Proxy proxy) {
        ResolverHttp.proxy = proxy;
    }

    @Override
    public ResourceResolverLookup canResolve(String uri, String baseURI) {
        if (uri == null) {
            return null;
        }
        if (this.pattern.matcher(uri).matches() || baseURI != null && this.pattern.matcher(baseURI).matches()) {
            return this;
        }
        return null;
    }

    @Override
    public ResourceResolver newInstance(String uri, String baseURI) {
        return new ResolverHttp(uri, baseURI);
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
            URL url = tmp.toURL();
            HttpURLConnection urlConnection = proxy != null ? (HttpURLConnection)url.openConnection(proxy) : (HttpURLConnection)url.openConnection();
            return urlConnection.getInputStream();
        }
        catch (IOException | URISyntaxException e) {
            throw new XMLSecurityException(e);
        }
    }
}

