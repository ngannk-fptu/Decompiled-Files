/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.apache.xmlgraphics.xmp.Metadata;
import org.xml.sax.SAXException;

public final class XMPSerializer {
    private static final String DEFAULT_ENCODING = "UTF-8";

    private XMPSerializer() {
    }

    public static void writeXML(Metadata meta, Result res) throws TransformerConfigurationException, SAXException {
        XMPSerializer.writeXML(meta, res, false, false);
    }

    public static void writeXMPPacket(Metadata meta, OutputStream out, boolean readOnlyXMP) throws TransformerConfigurationException, SAXException {
        StreamResult res = new StreamResult(out);
        XMPSerializer.writeXML(meta, res, true, readOnlyXMP);
    }

    private static void writeXML(Metadata meta, Result res, boolean asXMPPacket, boolean readOnlyXMP) throws TransformerConfigurationException, SAXException {
        SAXTransformerFactory tFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
        TransformerHandler handler = tFactory.newTransformerHandler();
        Transformer transformer = handler.getTransformer();
        if (asXMPPacket) {
            transformer.setOutputProperty("omit-xml-declaration", "yes");
        }
        transformer.setOutputProperty("encoding", DEFAULT_ENCODING);
        try {
            transformer.setOutputProperty("indent", "yes");
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        handler.setResult(res);
        handler.startDocument();
        if (asXMPPacket) {
            handler.processingInstruction("xpacket", "begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"");
        }
        meta.toSAX(handler);
        if (asXMPPacket) {
            if (readOnlyXMP) {
                handler.processingInstruction("xpacket", "end=\"r\"");
            } else {
                StringBuffer sb = new StringBuffer(101);
                sb.append('\n');
                for (int i = 0; i < 100; ++i) {
                    sb.append(" ");
                }
                char[] padding = sb.toString().toCharArray();
                for (int i = 0; i < 40; ++i) {
                    handler.characters(padding, 0, padding.length);
                }
                handler.characters(new char[]{'\n'}, 0, 1);
                handler.processingInstruction("xpacket", "end=\"w\"");
            }
        }
        handler.endDocument();
    }
}

