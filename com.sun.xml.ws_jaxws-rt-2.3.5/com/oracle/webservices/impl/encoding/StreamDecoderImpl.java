/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.impl.encoding;

import com.oracle.webservices.impl.internalspi.encoding.StreamDecoder;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.encoding.StreamSOAPCodec;
import com.sun.xml.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public class StreamDecoderImpl
implements StreamDecoder {
    @Override
    public Message decode(InputStream in, String charset, AttachmentSet att, SOAPVersion soapVersion) throws IOException {
        XMLStreamReader reader = XMLStreamReaderFactory.create(null, in, charset, true);
        reader = new TidyXMLStreamReader(reader, in);
        return StreamSOAPCodec.decode(soapVersion, reader, att);
    }
}

