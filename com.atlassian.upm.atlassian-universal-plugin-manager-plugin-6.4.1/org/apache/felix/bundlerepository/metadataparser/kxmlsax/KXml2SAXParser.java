/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser.kxmlsax;

import java.io.Reader;
import java.util.Properties;
import org.apache.felix.bundlerepository.metadataparser.kxmlsax.KXml2SAXHandler;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

public class KXml2SAXParser
extends KXmlParser {
    public String m_uri = "uri";

    public KXml2SAXParser(Reader reader) throws XmlPullParserException {
        this.setInput(reader);
    }

    public void parseXML(KXml2SAXHandler handler) throws Exception {
        while (this.next() != 1) {
            handler.setLineNumber(this.getLineNumber());
            handler.setColumnNumber(this.getColumnNumber());
            if (this.getEventType() == 2) {
                Properties props = new Properties();
                for (int i = 0; i < this.getAttributeCount(); ++i) {
                    props.put(this.getAttributeName(i), this.getAttributeValue(i));
                }
                handler.startElement(this.getNamespace(), this.getName(), this.getName(), props);
                continue;
            }
            if (this.getEventType() == 3) {
                handler.endElement(this.getNamespace(), this.getName(), this.getName());
                continue;
            }
            if (this.getEventType() == 4) {
                String text = this.getText();
                handler.characters(text.toCharArray(), 0, text.length());
                continue;
            }
            if (this.getEventType() != 8) continue;
            handler.processingInstruction(null, this.getText());
        }
    }
}

