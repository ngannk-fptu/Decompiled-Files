/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.staxex.NamespaceContextEx
 *  org.jvnet.staxex.XMLStreamReaderEx
 */
package com.sun.xml.ws.util.xml;

import com.sun.xml.ws.encoding.TagInfoset;
import com.sun.xml.ws.util.xml.NamespaceContextExAdaper;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.NamespaceContextEx;
import org.jvnet.staxex.XMLStreamReaderEx;

public class XMLReaderComposite
implements XMLStreamReaderEx {
    protected State state = State.StartTag;
    protected ElemInfo elemInfo;
    protected TagInfoset tagInfo;
    protected XMLStreamReader[] children;
    protected int payloadIndex = -1;
    protected XMLStreamReader payloadReader;

    public XMLReaderComposite(ElemInfo elem, XMLStreamReader[] wrapees) {
        this.elemInfo = elem;
        this.tagInfo = elem.tagInfo;
        this.children = (XMLStreamReader[])wrapees.clone();
        if (this.children != null && this.children.length > 0) {
            this.payloadIndex = 0;
            this.payloadReader = this.children[this.payloadIndex];
        }
    }

    public int next() throws XMLStreamException {
        switch (this.state) {
            case StartTag: {
                if (this.payloadReader != null) {
                    this.state = State.Payload;
                    return this.payloadReader.getEventType();
                }
                this.state = State.EndTag;
                return 2;
            }
            case EndTag: {
                return 8;
            }
        }
        int next = 8;
        if (this.payloadReader != null && this.payloadReader.hasNext()) {
            next = this.payloadReader.next();
        }
        if (next != 8) {
            return next;
        }
        if (this.payloadIndex + 1 < this.children.length) {
            ++this.payloadIndex;
            this.payloadReader = this.children[this.payloadIndex];
            return this.payloadReader.getEventType();
        }
        this.state = State.EndTag;
        return 2;
    }

    public boolean hasNext() throws XMLStreamException {
        switch (this.state) {
            case EndTag: {
                return false;
            }
        }
        return true;
    }

    public String getElementText() throws XMLStreamException {
        switch (this.state) {
            case StartTag: {
                if (this.payloadReader.isCharacters()) {
                    return this.payloadReader.getText();
                }
                return "";
            }
        }
        return this.payloadReader.getElementText();
    }

    public int nextTag() throws XMLStreamException {
        int e = this.next();
        if (e == 8) {
            return e;
        }
        while (e != 8) {
            if (e == 1) {
                return e;
            }
            if (e == 2) {
                return e;
            }
            e = this.next();
        }
        return e;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.payloadReader != null ? this.payloadReader.getProperty(name) : null;
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (this.payloadReader != null) {
            this.payloadReader.require(type, namespaceURI, localName);
        }
    }

    public void close() throws XMLStreamException {
        if (this.payloadReader != null) {
            this.payloadReader.close();
        }
    }

    public String getNamespaceURI(String prefix) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.elemInfo.getNamespaceURI(prefix);
            }
        }
        return this.payloadReader.getNamespaceURI(prefix);
    }

    public boolean isStartElement() {
        switch (this.state) {
            case StartTag: {
                return true;
            }
            case EndTag: {
                return false;
            }
        }
        return this.payloadReader.isStartElement();
    }

    public boolean isEndElement() {
        switch (this.state) {
            case StartTag: {
                return false;
            }
            case EndTag: {
                return true;
            }
        }
        return this.payloadReader.isEndElement();
    }

    public boolean isCharacters() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return false;
            }
        }
        return this.payloadReader.isCharacters();
    }

    public boolean isWhiteSpace() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return false;
            }
        }
        return this.payloadReader.isWhiteSpace();
    }

    public String getAttributeValue(String uri, String localName) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.atts.getValue(uri, localName);
            }
        }
        return this.payloadReader.getAttributeValue(uri, localName);
    }

    public int getAttributeCount() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.atts.getLength();
            }
        }
        return this.payloadReader.getAttributeCount();
    }

    public QName getAttributeName(int i) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return new QName(this.tagInfo.atts.getURI(i), this.tagInfo.atts.getLocalName(i), XMLReaderComposite.getPrfix(this.tagInfo.atts.getQName(i)));
            }
        }
        return this.payloadReader.getAttributeName(i);
    }

    public String getAttributeNamespace(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.atts.getURI(index);
            }
        }
        return this.payloadReader.getAttributeNamespace(index);
    }

    public String getAttributeLocalName(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.atts.getLocalName(index);
            }
        }
        return this.payloadReader.getAttributeLocalName(index);
    }

    public String getAttributePrefix(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return XMLReaderComposite.getPrfix(this.tagInfo.atts.getQName(index));
            }
        }
        return this.payloadReader.getAttributePrefix(index);
    }

    private static String getPrfix(String qName) {
        if (qName == null) {
            return null;
        }
        int i = qName.indexOf(":");
        return i > 0 ? qName.substring(0, i) : "";
    }

    public String getAttributeType(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.atts.getType(index);
            }
        }
        return this.payloadReader.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.atts.getValue(index);
            }
        }
        return this.payloadReader.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return index < this.tagInfo.atts.getLength() ? this.tagInfo.atts.getLocalName(index) != null : false;
            }
        }
        return this.payloadReader.isAttributeSpecified(index);
    }

    public int getNamespaceCount() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.ns.length / 2;
            }
        }
        return this.payloadReader.getNamespaceCount();
    }

    public String getNamespacePrefix(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.ns[2 * index];
            }
        }
        return this.payloadReader.getNamespacePrefix(index);
    }

    public String getNamespaceURI(int index) {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.ns[2 * index + 1];
            }
        }
        return this.payloadReader.getNamespaceURI(index);
    }

    public NamespaceContextEx getNamespaceContext() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return new NamespaceContextExAdaper(this.elemInfo);
            }
        }
        return this.isPayloadReaderEx() ? this.payloadReaderEx().getNamespaceContext() : new NamespaceContextExAdaper(this.payloadReader.getNamespaceContext());
    }

    private boolean isPayloadReaderEx() {
        return this.payloadReader instanceof XMLStreamReaderEx;
    }

    private XMLStreamReaderEx payloadReaderEx() {
        return (XMLStreamReaderEx)this.payloadReader;
    }

    public int getEventType() {
        switch (this.state) {
            case StartTag: {
                return 1;
            }
            case EndTag: {
                return 2;
            }
        }
        return this.payloadReader.getEventType();
    }

    public String getText() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getText();
    }

    public char[] getTextCharacters() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getTextCharacters();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return -1;
            }
        }
        return this.payloadReader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public int getTextStart() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return 0;
            }
        }
        return this.payloadReader.getTextStart();
    }

    public int getTextLength() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return 0;
            }
        }
        return this.payloadReader.getTextLength();
    }

    public String getEncoding() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getEncoding();
    }

    public boolean hasText() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return false;
            }
        }
        return this.payloadReader.hasText();
    }

    public Location getLocation() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return new Location(){

                    @Override
                    public int getLineNumber() {
                        return 0;
                    }

                    @Override
                    public int getColumnNumber() {
                        return 0;
                    }

                    @Override
                    public int getCharacterOffset() {
                        return 0;
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
        }
        return this.payloadReader.getLocation();
    }

    public QName getName() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return new QName(this.tagInfo.nsUri, this.tagInfo.localName, this.tagInfo.prefix);
            }
        }
        return this.payloadReader.getName();
    }

    public String getLocalName() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.localName;
            }
        }
        return this.payloadReader.getLocalName();
    }

    public boolean hasName() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return true;
            }
        }
        return this.payloadReader.hasName();
    }

    public String getNamespaceURI() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.nsUri;
            }
        }
        return this.payloadReader.getNamespaceURI();
    }

    public String getPrefix() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return this.tagInfo.prefix;
            }
        }
        return this.payloadReader.getPrefix();
    }

    public String getVersion() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getVersion();
    }

    public boolean isStandalone() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return true;
            }
        }
        return this.payloadReader.isStandalone();
    }

    public boolean standaloneSet() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return true;
            }
        }
        return this.payloadReader.standaloneSet();
    }

    public String getCharacterEncodingScheme() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getCharacterEncodingScheme();
    }

    public String getPITarget() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getPITarget();
    }

    public String getPIData() {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.payloadReader.getPIData();
    }

    public String getElementTextTrim() throws XMLStreamException {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.isPayloadReaderEx() ? this.payloadReaderEx().getElementTextTrim() : this.payloadReader.getElementText().trim();
    }

    public CharSequence getPCDATA() throws XMLStreamException {
        switch (this.state) {
            case StartTag: 
            case EndTag: {
                return null;
            }
        }
        return this.isPayloadReaderEx() ? this.payloadReaderEx().getPCDATA() : this.payloadReader.getElementText();
    }

    public static class ElemInfo
    implements NamespaceContext {
        ElemInfo ancestor;
        TagInfoset tagInfo;

        public ElemInfo(TagInfoset tag, ElemInfo parent) {
            this.tagInfo = tag;
            this.ancestor = parent;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            String n = this.tagInfo.getNamespaceURI(prefix);
            return n != null ? n : (this.ancestor != null ? this.ancestor.getNamespaceURI(prefix) : null);
        }

        @Override
        public String getPrefix(String uri) {
            String p = this.tagInfo.getPrefix(uri);
            return p != null ? p : (this.ancestor != null ? this.ancestor.getPrefix(uri) : null);
        }

        public List<String> allPrefixes(String namespaceURI) {
            List<String> l = this.tagInfo.allPrefixes(namespaceURI);
            if (this.ancestor != null) {
                List<String> p = this.ancestor.allPrefixes(namespaceURI);
                p.addAll(l);
                return p;
            }
            return l;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return this.allPrefixes(namespaceURI).iterator();
        }
    }

    public static enum State {
        StartTag,
        Payload,
        EndTag;

    }
}

