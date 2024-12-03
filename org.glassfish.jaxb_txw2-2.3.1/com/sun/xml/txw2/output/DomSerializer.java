/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TxwException;
import com.sun.xml.txw2.output.Dom2SaxAdapter;
import com.sun.xml.txw2.output.SaxSerializer;
import com.sun.xml.txw2.output.XmlSerializer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomSerializer
implements XmlSerializer {
    private final SaxSerializer serializer;

    public DomSerializer(Node node) {
        Dom2SaxAdapter adapter = new Dom2SaxAdapter(node);
        this.serializer = new SaxSerializer(adapter, adapter, false);
    }

    public DomSerializer(DOMResult domResult) {
        Node node = domResult.getNode();
        if (node == null) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.newDocument();
                domResult.setNode(doc);
                this.serializer = new SaxSerializer(new Dom2SaxAdapter(doc), null, false);
            }
            catch (ParserConfigurationException pce) {
                throw new TxwException(pce);
            }
        } else {
            this.serializer = new SaxSerializer(new Dom2SaxAdapter(node), null, false);
        }
    }

    @Override
    public void startDocument() {
        this.serializer.startDocument();
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        this.serializer.beginStartTag(uri, localName, prefix);
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        this.serializer.writeAttribute(uri, localName, prefix, value);
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        this.serializer.writeXmlns(prefix, uri);
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        this.serializer.endStartTag(uri, localName, prefix);
    }

    @Override
    public void endTag() {
        this.serializer.endTag();
    }

    @Override
    public void text(StringBuilder text) {
        this.serializer.text(text);
    }

    @Override
    public void cdata(StringBuilder text) {
        this.serializer.cdata(text);
    }

    @Override
    public void comment(StringBuilder comment) {
        this.serializer.comment(comment);
    }

    @Override
    public void endDocument() {
        this.serializer.endDocument();
    }

    @Override
    public void flush() {
    }
}

