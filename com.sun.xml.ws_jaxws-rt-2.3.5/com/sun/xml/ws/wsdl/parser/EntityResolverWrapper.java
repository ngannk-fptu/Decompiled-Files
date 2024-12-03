/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class EntityResolverWrapper
implements XMLEntityResolver {
    private final EntityResolver core;
    private boolean useStreamFromEntityResolver = false;

    public EntityResolverWrapper(EntityResolver core) {
        this.core = core;
    }

    public EntityResolverWrapper(EntityResolver core, boolean useStreamFromEntityResolver) {
        this.core = core;
        this.useStreamFromEntityResolver = useStreamFromEntityResolver;
    }

    @Override
    public XMLEntityResolver.Parser resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputSource source = this.core.resolveEntity(publicId, systemId);
        if (source == null) {
            return null;
        }
        if (source.getSystemId() != null) {
            systemId = source.getSystemId();
        }
        URL url = new URL(systemId);
        InputStream stream = this.useStreamFromEntityResolver ? source.getByteStream() : url.openStream();
        return new XMLEntityResolver.Parser(url, new TidyXMLStreamReader(XMLStreamReaderFactory.create(url.toExternalForm(), stream, true), stream));
    }
}

