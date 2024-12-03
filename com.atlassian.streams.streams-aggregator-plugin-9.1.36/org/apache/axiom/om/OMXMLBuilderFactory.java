/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.ParseException
 */
package org.apache.axiom.om;

import java.io.InputStream;
import java.io.Reader;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.OMAttachmentAccessorMimePartProvider;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class OMXMLBuilderFactory {
    private OMXMLBuilderFactory() {
    }

    public static OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createStAXOMBuilder(metaFactory.getOMFactory(), parser);
    }

    public static OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        return omFactory.getMetaFactory().createStAXOMBuilder(omFactory, parser);
    }

    public static OMXMLParserWrapper createOMBuilder(InputStream in) {
        return OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, in);
    }

    public static OMXMLParserWrapper createOMBuilder(InputStream in, String encoding) {
        return OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, in, encoding);
    }

    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputStream in) {
        return OMXMLBuilderFactory.createOMBuilder(configuration, in, null);
    }

    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputStream in, String encoding) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), configuration, is);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, InputStream in) {
        return OMXMLBuilderFactory.createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, InputStream in, String encoding) {
        return OMXMLBuilderFactory.createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in, encoding);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputStream in) {
        return OMXMLBuilderFactory.createOMBuilder(omFactory, configuration, in, null);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputStream in, String encoding) {
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return omFactory.getMetaFactory().createOMBuilder(omFactory, configuration, is);
    }

    public static OMXMLParserWrapper createOMBuilder(Reader in) {
        return OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, in);
    }

    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, Reader in) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), configuration, new InputSource(in));
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Reader in) {
        return OMXMLBuilderFactory.createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, Reader in) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, configuration, new InputSource(in));
    }

    public static OMXMLParserWrapper createOMBuilder(Source source) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), source);
    }

    public static OMXMLParserWrapper createOMBuilder(Node node, boolean expandEntityReferences) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), node, expandEntityReferences);
    }

    public static OMXMLParserWrapper createOMBuilder(SAXSource source, boolean expandEntityReferences) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), source, expandEntityReferences);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Node node, boolean expandEntityReferences) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, node, expandEntityReferences);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, SAXSource source, boolean expandEntityReferences) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source, expandEntityReferences);
    }

    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, Attachments attachments) {
        return OMXMLBuilderFactory.createOMBuilder(OMAbstractFactory.getMetaFactory().getOMFactory(), configuration, attachments);
    }

    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, Attachments attachments) {
        ContentType contentType;
        try {
            contentType = new ContentType(attachments.getRootPartContentType());
        }
        catch (ParseException ex) {
            throw new OMException(ex);
        }
        InputSource rootPart = OMXMLBuilderFactory.getRootPartInputSource(attachments, contentType);
        return omFactory.getMetaFactory().createOMBuilder(configuration, omFactory, rootPart, new OMAttachmentAccessorMimePartProvider(attachments));
    }

    public static SOAPModelBuilder createStAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser) {
        return metaFactory.createStAXSOAPModelBuilder(parser);
    }

    public static SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return OMAbstractFactory.getMetaFactory().createStAXSOAPModelBuilder(parser);
    }

    public static SOAPModelBuilder createSOAPModelBuilder(InputStream in, String encoding) {
        return OMXMLBuilderFactory.createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), in, encoding);
    }

    public static SOAPModelBuilder createSOAPModelBuilder(OMMetaFactory metaFactory, InputStream in, String encoding) {
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, is);
    }

    public static SOAPModelBuilder createSOAPModelBuilder(Reader in) {
        return OMXMLBuilderFactory.createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), in);
    }

    public static SOAPModelBuilder createSOAPModelBuilder(OMMetaFactory metaFactory, Reader in) {
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, new InputSource(in));
    }

    public static SOAPModelBuilder createSOAPModelBuilder(Attachments attachments) {
        return OMXMLBuilderFactory.createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), attachments);
    }

    public static SOAPModelBuilder createSOAPModelBuilder(OMMetaFactory metaFactory, Attachments attachments) {
        SOAPFactory soapFactory;
        ContentType contentType;
        try {
            contentType = new ContentType(attachments.getRootPartContentType());
        }
        catch (ParseException ex) {
            throw new OMException(ex);
        }
        String type = contentType.getParameter("type");
        if ("text/xml".equalsIgnoreCase(type)) {
            soapFactory = metaFactory.getSOAP11Factory();
        } else if ("application/soap+xml".equalsIgnoreCase(type)) {
            soapFactory = metaFactory.getSOAP12Factory();
        } else {
            throw new OMException("Unable to determine SOAP version");
        }
        InputSource rootPart = OMXMLBuilderFactory.getRootPartInputSource(attachments, contentType);
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, soapFactory, rootPart, new OMAttachmentAccessorMimePartProvider(attachments));
    }

    private static InputSource getRootPartInputSource(Attachments attachments, ContentType contentType) {
        InputSource rootPart = new InputSource(attachments.getRootPartInputStream(false));
        rootPart.setEncoding(contentType.getParameter("charset"));
        return rootPart;
    }
}

