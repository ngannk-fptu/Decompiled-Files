/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.soap.impl.builder;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.builder.OMAttachmentAccessorMimePartProvider;
import org.apache.axiom.om.impl.builder.XOPBuilder;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamReader;

public class MTOMStAXSOAPModelBuilder
extends StAXSOAPModelBuilder
implements XOPBuilder {
    private final Attachments attachments;

    public MTOMStAXSOAPModelBuilder(XMLStreamReader parser, SOAPFactory factory, Attachments attachments, String soapVersion) {
        super(new XOPDecodingStreamReader(parser, new OMAttachmentAccessorMimePartProvider(attachments)), factory, soapVersion);
        this.attachments = attachments;
    }

    public MTOMStAXSOAPModelBuilder(XMLStreamReader reader, Attachments attachments, String soapVersion) {
        super(new XOPDecodingStreamReader(reader, new OMAttachmentAccessorMimePartProvider(attachments)), soapVersion);
        this.attachments = attachments;
    }

    public MTOMStAXSOAPModelBuilder(XMLStreamReader reader, Attachments attachments) {
        super(new XOPDecodingStreamReader(reader, new OMAttachmentAccessorMimePartProvider(attachments)));
        this.attachments = attachments;
    }

    public MTOMStAXSOAPModelBuilder(SOAPFactory soapFactory, XMLStreamReader reader, MimePartProvider mimePartProvider) {
        super(new XOPDecodingStreamReader(reader, mimePartProvider), soapFactory, soapFactory.getSoapVersionURI());
        this.attachments = null;
    }

    public DataHandler getDataHandler(String blobContentID) throws OMException {
        DataHandler dataHandler = this.attachments.getDataHandler(blobContentID);
        return dataHandler;
    }

    public Attachments getAttachments() {
        return this.attachments;
    }
}

