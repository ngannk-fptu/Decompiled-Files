/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferResult
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.util.JAXWSUtils;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public final class MexEntityResolver
implements XMLEntityResolver {
    private final Map<String, SDDocumentSource> wsdls = new HashMap<String, SDDocumentSource>();

    public MexEntityResolver(List<? extends Source> wsdls) throws IOException {
        Transformer transformer = XmlUtil.newTransformer();
        for (Source source : wsdls) {
            XMLStreamBufferResult xsbr = new XMLStreamBufferResult();
            try {
                transformer.transform(source, (Result)xsbr);
            }
            catch (TransformerException e) {
                throw new WebServiceException((Throwable)e);
            }
            String systemId = source.getSystemId();
            if (systemId == null) continue;
            SDDocumentSource doc = SDDocumentSource.create(JAXWSUtils.getFileOrURL(systemId), (XMLStreamBuffer)xsbr.getXMLStreamBuffer());
            this.wsdls.put(systemId, doc);
        }
    }

    @Override
    public XMLEntityResolver.Parser resolveEntity(String publicId, String systemId) throws SAXException, IOException, XMLStreamException {
        SDDocumentSource src;
        if (systemId != null && (src = this.wsdls.get(systemId)) != null) {
            return new XMLEntityResolver.Parser(src);
        }
        return null;
    }
}

