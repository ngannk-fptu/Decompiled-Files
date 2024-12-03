/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.OMAttachmentAccessorMimePartProvider;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.builder.XOPBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamReader;

public class XOPAwareStAXOMBuilder
extends StAXOMBuilder
implements XOPBuilder {
    Attachments attachments;

    public XOPAwareStAXOMBuilder(OMFactory ombuilderFactory, XMLStreamReader parser, Attachments attachments) {
        super(ombuilderFactory, new XOPDecodingStreamReader(parser, new OMAttachmentAccessorMimePartProvider(attachments)));
        this.attachments = attachments;
    }

    public XOPAwareStAXOMBuilder(OMFactory factory, XMLStreamReader parser, OMElement element, Attachments attachments) {
        super(factory, (XMLStreamReader)new XOPDecodingStreamReader(parser, new OMAttachmentAccessorMimePartProvider(attachments)), element);
        this.attachments = attachments;
    }

    public XOPAwareStAXOMBuilder(String filePath, Attachments attachments) throws XMLStreamException, FileNotFoundException {
        super(new XOPDecodingStreamReader(StAXUtils.createXMLStreamReader(new FileInputStream(filePath)), new OMAttachmentAccessorMimePartProvider(attachments)));
        this.attachments = attachments;
    }

    public XOPAwareStAXOMBuilder(InputStream inStream, Attachments attachments) throws XMLStreamException {
        super(new XOPDecodingStreamReader(StAXUtils.createXMLStreamReader(inStream), new OMAttachmentAccessorMimePartProvider(attachments)));
        this.attachments = attachments;
    }

    public XOPAwareStAXOMBuilder(XMLStreamReader parser, Attachments attachments) {
        super(new XOPDecodingStreamReader(parser, new OMAttachmentAccessorMimePartProvider(attachments)));
        this.attachments = attachments;
    }

    public XOPAwareStAXOMBuilder(OMFactory omFactory, XMLStreamReader reader, MimePartProvider mimePartProvider) {
        super(omFactory, new XOPDecodingStreamReader(reader, mimePartProvider));
        this.attachments = null;
    }

    public DataHandler getDataHandler(String blobContentID) throws OMException {
        return this.attachments.getDataHandler(blobContentID);
    }

    public Attachments getAttachments() {
        return this.attachments;
    }
}

