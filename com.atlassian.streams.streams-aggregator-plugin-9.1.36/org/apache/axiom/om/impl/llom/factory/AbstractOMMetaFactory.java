/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.SAXOMBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.DOMXMLStreamReader;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.builder.MTOMStAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.builder.OMMetaFactoryEx;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.XMLFragmentStreamReader;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public abstract class AbstractOMMetaFactory
implements OMMetaFactoryEx {
    private static XMLStreamReader createXMLStreamReader(StAXParserConfiguration configuration, InputSource is) {
        try {
            if (is.getByteStream() != null) {
                String encoding = is.getEncoding();
                if (encoding == null) {
                    return StAXUtils.createXMLStreamReader(configuration, is.getByteStream());
                }
                return StAXUtils.createXMLStreamReader(configuration, is.getByteStream(), encoding);
            }
            if (is.getCharacterStream() != null) {
                return StAXUtils.createXMLStreamReader(configuration, is.getCharacterStream());
            }
            String systemId = is.getSystemId();
            return StAXUtils.createXMLStreamReader(configuration, systemId, new URL(systemId).openConnection().getInputStream());
        }
        catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
        catch (IOException ex) {
            throw new OMException(ex);
        }
    }

    private static XMLStreamReader getXMLStreamReader(XMLStreamReader originalReader) {
        int eventType = originalReader.getEventType();
        switch (eventType) {
            case 7: {
                return originalReader;
            }
            case 1: {
                return new XMLFragmentStreamReader(originalReader);
            }
        }
        throw new OMException("The supplied XMLStreamReader is in an unexpected state (" + XMLEventUtils.getEventTypeString(eventType) + ")");
    }

    private static OMXMLParserWrapper internalCreateStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        StAXOMBuilder builder = new StAXOMBuilder(omFactory, parser);
        builder.releaseParserOnClose(true);
        return builder;
    }

    public OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        return AbstractOMMetaFactory.internalCreateStAXOMBuilder(omFactory, AbstractOMMetaFactory.getXMLStreamReader(parser));
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputSource is) {
        return AbstractOMMetaFactory.internalCreateStAXOMBuilder(omFactory, AbstractOMMetaFactory.createXMLStreamReader(configuration, is));
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source) {
        if (source instanceof SAXSource) {
            return this.createOMBuilder(omFactory, (SAXSource)source, true);
        }
        if (source instanceof DOMSource) {
            return this.createOMBuilder(omFactory, ((DOMSource)source).getNode(), true);
        }
        try {
            return new StAXOMBuilder(omFactory, StAXUtils.getXMLInputFactory().createXMLStreamReader(source));
        }
        catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Node node, boolean expandEntityReferences) {
        return new StAXOMBuilder(omFactory, new DOMXMLStreamReader(node, expandEntityReferences));
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, SAXSource source, boolean expandEntityReferences) {
        return new SAXOMBuilder(omFactory, source, expandEntityReferences);
    }

    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, OMFactory omFactory, InputSource rootPart, MimePartProvider mimePartProvider) {
        XOPAwareStAXOMBuilder builder = new XOPAwareStAXOMBuilder(omFactory, AbstractOMMetaFactory.createXMLStreamReader(configuration, rootPart), mimePartProvider);
        builder.releaseParserOnClose(true);
        return builder;
    }

    private SOAPModelBuilder internalCreateStAXSOAPModelBuilder(XMLStreamReader parser) {
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(this, parser);
        builder.releaseParserOnClose(true);
        return builder;
    }

    public SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return this.internalCreateStAXSOAPModelBuilder(AbstractOMMetaFactory.getXMLStreamReader(parser));
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration, InputSource is) {
        return this.internalCreateStAXSOAPModelBuilder(AbstractOMMetaFactory.createXMLStreamReader(configuration, is));
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration, SOAPFactory soapFactory, InputSource rootPart, MimePartProvider mimePartProvider) {
        MTOMStAXSOAPModelBuilder builder = new MTOMStAXSOAPModelBuilder(soapFactory, AbstractOMMetaFactory.createXMLStreamReader(configuration, rootPart), mimePartProvider);
        builder.releaseParserOnClose(true);
        return builder;
    }
}

