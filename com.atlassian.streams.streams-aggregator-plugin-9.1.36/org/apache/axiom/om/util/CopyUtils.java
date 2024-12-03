/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPEnvelope;

public class CopyUtils {
    private CopyUtils() {
    }

    public static SOAPEnvelope copy(SOAPEnvelope sourceEnv) {
        SOAPCloneOptions options = new SOAPCloneOptions();
        options.setFetchDataHandlers(true);
        options.setPreserveModel(true);
        options.setCopyOMDataSources(true);
        return (SOAPEnvelope)sourceEnv.clone(options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void reader2writer(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        builder.releaseParserOnClose(true);
        try {
            OMDocument omDocument = builder.getDocument();
            Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                OMNode omNode = (OMNode)it.next();
                omNode.serializeAndConsume(writer);
            }
        }
        finally {
            builder.close();
        }
    }
}

