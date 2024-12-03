/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jettison.Node;

public abstract class AbstractXMLStreamReader
implements XMLStreamReader {
    protected int event;
    protected Node node;

    @Override
    public boolean isAttributeSpecified(int index) {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return this.event == 4;
    }

    @Override
    public boolean isEndElement() {
        return this.event == 2;
    }

    @Override
    public boolean isStandalone() {
        return false;
    }

    @Override
    public boolean isStartElement() {
        return this.event == 1;
    }

    @Override
    public boolean isWhiteSpace() {
        return false;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        int event = this.next();
        while (event != 1 && event != 2) {
            event = this.next();
        }
        return event;
    }

    @Override
    public int getEventType() {
        return this.event;
    }

    @Override
    public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
    }

    @Override
    public int getAttributeCount() {
        return this.node.getAttributes().size();
    }

    @Override
    public String getAttributeLocalName(int n) {
        return this.getAttributeName(n).getLocalPart();
    }

    @Override
    public QName getAttributeName(int n) {
        Iterator itr = this.node.getAttributes().keySet().iterator();
        QName name = null;
        for (int i = 0; i <= n; ++i) {
            name = (QName)itr.next();
        }
        return name;
    }

    @Override
    public String getAttributeNamespace(int n) {
        return this.getAttributeName(n).getNamespaceURI();
    }

    @Override
    public String getAttributePrefix(int n) {
        return this.getAttributeName(n).getPrefix();
    }

    @Override
    public String getAttributeValue(int n) {
        Iterator itr = this.node.getAttributes().values().iterator();
        String name = null;
        for (int i = 0; i <= n; ++i) {
            name = (String)itr.next();
        }
        return name;
    }

    @Override
    public String getAttributeValue(String ns, String local) {
        return (String)this.node.getAttributes().get(new QName(ns, local));
    }

    @Override
    public String getAttributeType(int index) {
        return "CDATA";
    }

    @Override
    public String getLocalName() {
        return this.getName().getLocalPart();
    }

    @Override
    public QName getName() {
        return this.node.getName();
    }

    @Override
    public String getNamespaceURI() {
        return this.getName().getNamespaceURI();
    }

    @Override
    public int getNamespaceCount() {
        return this.node.getNamespaceCount();
    }

    @Override
    public String getNamespacePrefix(int n) {
        return this.node.getNamespacePrefix(n);
    }

    @Override
    public String getNamespaceURI(int n) {
        return this.node.getNamespaceURI(n);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return this.node.getNamespaceURI(prefix);
    }

    @Override
    public boolean hasName() {
        return false;
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return this.event != 8;
    }

    @Override
    public boolean hasText() {
        return this.event == 4;
    }

    @Override
    public boolean standaloneSet() {
        return false;
    }

    @Override
    public String getCharacterEncodingScheme() {
        return null;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public Location getLocation() {
        return new Location(){

            @Override
            public int getCharacterOffset() {
                return 0;
            }

            @Override
            public int getColumnNumber() {
                return 0;
            }

            @Override
            public int getLineNumber() {
                return -1;
            }

            @Override
            public String getPublicId() {
                return null;
            }

            @Override
            public String getSystemId() {
                return null;
            }
        };
    }

    @Override
    public String getPIData() {
        return null;
    }

    @Override
    public String getPITarget() {
        return null;
    }

    @Override
    public String getPrefix() {
        return this.getName().getPrefix();
    }

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public char[] getTextCharacters() {
        String text = this.getText();
        return text != null ? text.toCharArray() : new char[]{};
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        String text = this.getText();
        if (text != null) {
            text.getChars(sourceStart, sourceStart + length, target, targetStart);
            return length;
        }
        return 0;
    }

    @Override
    public int getTextLength() {
        String text = this.getText();
        return text != null ? text.length() : 0;
    }

    @Override
    public int getTextStart() {
        return 0;
    }
}

