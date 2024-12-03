/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmpReader {
    private Document domDocument;

    public XmpReader(byte[] bytes) throws SAXException, IOException {
        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            fact.setNamespaceAware(true);
            DocumentBuilder db = fact.newDocumentBuilder();
            db.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            this.domDocument = db.parse(bais);
        }
        catch (ParserConfigurationException e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean replace(String namespaceURI, String localName, String value) {
        NodeList nodes = this.domDocument.getElementsByTagNameNS(namespaceURI, localName);
        if (nodes.getLength() == 0) {
            return false;
        }
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            this.setNodeText(this.domDocument, node, value);
        }
        return true;
    }

    public boolean add(String parent, String namespaceURI, String localName, String value) {
        NodeList nodes = this.domDocument.getElementsByTagName(parent);
        if (nodes.getLength() == 0) {
            return false;
        }
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node pNode = nodes.item(i);
            NamedNodeMap attrs = pNode.getAttributes();
            for (int j = 0; j < attrs.getLength(); ++j) {
                Node node = attrs.item(j);
                if (!namespaceURI.equals(node.getNodeValue())) continue;
                node = this.domDocument.createElement(localName);
                node.appendChild(this.domDocument.createTextNode(value));
                pNode.appendChild(node);
                return true;
            }
        }
        return false;
    }

    public boolean setNodeText(Document domDocument, Node n, String value) {
        if (n == null) {
            return false;
        }
        Node nc = null;
        while ((nc = n.getFirstChild()) != null) {
            n.removeChild(nc);
        }
        n.appendChild(domDocument.createTextNode(value));
        return true;
    }

    public byte[] serializeDoc() throws IOException {
        XmlDomWriter xw = new XmlDomWriter();
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        fout.write("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n".getBytes(StandardCharsets.UTF_8));
        fout.flush();
        NodeList xmpmeta = this.domDocument.getElementsByTagName("x:xmpmeta");
        xw.write(xmpmeta.item(0));
        fout.flush();
        for (int i = 0; i < 20; ++i) {
            fout.write("                                                                                                   \n".getBytes());
        }
        fout.write("<?xpacket end=\"w\"?>".getBytes());
        fout.close();
        return fout.toByteArray();
    }
}

