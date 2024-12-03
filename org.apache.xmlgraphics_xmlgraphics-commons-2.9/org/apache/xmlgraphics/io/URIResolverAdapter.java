/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.XmlSourceUtil;

public class URIResolverAdapter
implements ResourceResolver {
    private final URIResolver resolver;

    public URIResolverAdapter(URIResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        try {
            Source src = this.resolver.resolve(uri.toASCIIString(), null);
            InputStream resourceStream = XmlSourceUtil.getInputStream(src);
            if (resourceStream == null) {
                URL url = new URL(src.getSystemId());
                resourceStream = url.openStream();
            }
            return new Resource(resourceStream);
        }
        catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public OutputStream getOutputStream(URI uri) throws IOException {
        try {
            Source src = this.resolver.resolve(uri.toASCIIString(), null);
            return new URL(src.getSystemId()).openConnection().getOutputStream();
        }
        catch (TransformerException te) {
            throw new IOException(te.getMessage());
        }
    }
}

