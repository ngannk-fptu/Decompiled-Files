/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Verifier;
import org.jdom2.internal.ArrayCopy;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.support.AbstractOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.Walker;
import org.jdom2.util.JDOMNamespaceContext;
import org.jdom2.util.NamespaceStack;

public abstract class AbstractStAXStreamReader
extends AbstractOutputProcessor
implements XMLStreamReader {
    private final FormatStack formatstack;
    private final NamespaceStack nsstack = new NamespaceStack();
    private Document document;
    private String curi = null;
    private String clocalname = null;
    private String cprefix = null;
    private String ctext = null;
    private String ctarget = null;
    private String cdata = null;
    private Element[] emtstack = new Element[32];
    private Walker[] stack = new Walker[32];
    private int depth = 0;
    private int currentEvt = 7;

    public AbstractStAXStreamReader(Document document, Format format) {
        this.document = document;
        this.formatstack = new FormatStack(format);
        this.stack[0] = new DocumentWalker(document);
    }

    public AbstractStAXStreamReader(Document document) {
        this(document, Format.getRawFormat());
    }

    public boolean hasNext() throws XMLStreamException {
        return this.depth >= 0;
    }

    public int next() throws XMLStreamException {
        if (this.depth < 0) {
            throw new NoSuchElementException("No more data available.");
        }
        this.curi = null;
        this.clocalname = null;
        this.cprefix = null;
        this.ctext = null;
        this.ctarget = null;
        this.cdata = null;
        if (this.currentEvt == 2) {
            this.nsstack.pop();
            this.formatstack.pop();
            this.emtstack[this.depth + 1] = null;
        }
        if (!this.stack[this.depth].hasNext()) {
            this.stack[this.depth] = null;
            --this.depth;
            this.currentEvt = this.depth < 0 ? 8 : 2;
            return this.currentEvt;
        }
        Content c = this.stack[this.depth].next();
        if (c == null) {
            this.ctext = this.stack[this.depth].text();
            this.currentEvt = this.stack[this.depth].isCDATA() ? 12 : 4;
            return this.currentEvt;
        }
        switch (c.getCType()) {
            case CDATA: {
                this.ctext = c.getValue();
                this.currentEvt = 12;
                return 12;
            }
            case Text: {
                this.ctext = c.getValue();
                this.currentEvt = 4;
                return 4;
            }
            case Comment: {
                this.ctext = c.getValue();
                this.currentEvt = 5;
                return 5;
            }
            case DocType: {
                XMLOutputter xout = new XMLOutputter();
                this.ctext = xout.outputString((DocType)c);
                this.currentEvt = 11;
                return 11;
            }
            case EntityRef: {
                this.clocalname = ((EntityRef)c).getName();
                this.ctext = "";
                this.currentEvt = 9;
                return 9;
            }
            case ProcessingInstruction: {
                ProcessingInstruction pi = (ProcessingInstruction)c;
                this.ctarget = pi.getTarget();
                this.cdata = pi.getData();
                this.currentEvt = 3;
                return 3;
            }
            case Element: {
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected content " + c);
            }
        }
        Element emt = (Element)c;
        this.clocalname = emt.getName();
        this.cprefix = emt.getNamespacePrefix();
        this.curi = emt.getNamespaceURI();
        this.nsstack.push(emt);
        this.formatstack.push();
        String space = emt.getAttributeValue("space", Namespace.XML_NAMESPACE);
        if ("default".equals(space)) {
            this.formatstack.setTextMode(this.formatstack.getDefaultMode());
        } else if ("preserve".equals(space)) {
            this.formatstack.setTextMode(Format.TextMode.PRESERVE);
        }
        ++this.depth;
        if (this.depth >= this.stack.length) {
            this.stack = ArrayCopy.copyOf(this.stack, this.depth + 32);
            this.emtstack = ArrayCopy.copyOf(this.emtstack, this.depth + 32);
        }
        this.emtstack[this.depth] = emt;
        this.stack[this.depth] = this.buildWalker(this.formatstack, emt.getContent(), false);
        this.currentEvt = 1;
        return 1;
    }

    public int getEventType() {
        return this.currentEvt;
    }

    public boolean isStartElement() {
        return this.currentEvt == 1;
    }

    public boolean isEndElement() {
        return this.currentEvt == 2;
    }

    public boolean isCharacters() {
        return this.currentEvt == 4;
    }

    public boolean isWhiteSpace() {
        switch (this.currentEvt) {
            case 6: {
                return true;
            }
            case 4: 
            case 12: {
                return Verifier.isAllXMLWhitespace(this.ctext);
            }
        }
        return false;
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (type != this.getEventType()) {
            throw new XMLStreamException("required event " + type + " but got event " + this.getEventType());
        }
        if (localName != null && !localName.equals(this.clocalname)) {
            throw new XMLStreamException("required name " + localName + " but got name " + this.clocalname);
        }
        if (namespaceURI != null && !namespaceURI.equals(this.curi)) {
            throw new XMLStreamException("required namespace " + namespaceURI + " but got namespace " + this.curi);
        }
    }

    public QName getName() {
        switch (this.currentEvt) {
            case 1: {
                Element emts = this.emtstack[this.depth];
                return new QName(emts.getNamespaceURI(), emts.getName(), emts.getNamespacePrefix());
            }
            case 2: {
                Element emte = this.emtstack[this.depth + 1];
                return new QName(emte.getNamespaceURI(), emte.getName(), emte.getNamespacePrefix());
            }
        }
        throw new IllegalStateException("getName not supported for event " + this.currentEvt);
    }

    public String getLocalName() {
        switch (this.currentEvt) {
            case 1: 
            case 2: 
            case 9: {
                return this.clocalname;
            }
        }
        throw new IllegalStateException("getLocalName not supported for event " + this.currentEvt);
    }

    public boolean hasName() {
        return this.currentEvt == 1 || this.currentEvt == 2;
    }

    public String getNamespaceURI() {
        switch (this.currentEvt) {
            case 1: 
            case 2: {
                return this.curi;
            }
        }
        throw new IllegalStateException("getNamespaceURI not supported for event " + this.currentEvt);
    }

    public String getPrefix() {
        switch (this.currentEvt) {
            case 1: 
            case 2: {
                return this.cprefix;
            }
        }
        throw new IllegalStateException("getPrefix not supported for event " + this.currentEvt);
    }

    public String getPITarget() {
        switch (this.currentEvt) {
            case 3: {
                return this.ctarget;
            }
        }
        throw new IllegalStateException("getPITarget not supported for event " + this.currentEvt);
    }

    public String getPIData() {
        switch (this.currentEvt) {
            case 3: {
                return this.cdata;
            }
        }
        throw new IllegalStateException("getPIData not supported for event " + this.currentEvt);
    }

    public String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text");
        }
        int eventType = this.next();
        StringBuilder buf = new StringBuilder();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                buf.append(this.getText());
            } else if (eventType != 3 && eventType != 5) {
                if (eventType == 8) {
                    throw new XMLStreamException("unexpected end of document when reading element text content", this.getLocation());
                }
                if (eventType == 1) {
                    throw new XMLStreamException("element text content may not contain START_ELEMENT", this.getLocation());
                }
                throw new XMLStreamException("Unexpected event type " + eventType, this.getLocation());
            }
            eventType = this.next();
        }
        return buf.toString();
    }

    public int nextTag() throws XMLStreamException {
        int eventType = this.next();
        while (eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5) {
            eventType = this.next();
        }
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("expected start or end tag", this.getLocation());
        }
        return eventType;
    }

    public void close() throws XMLStreamException {
        this.currentEvt = 8;
        while (this.depth >= 0) {
            this.stack[this.depth] = null;
            this.emtstack[this.depth] = null;
            --this.depth;
        }
        this.cdata = null;
        this.clocalname = null;
        this.cprefix = null;
        this.ctarget = null;
        this.ctext = null;
        this.curi = null;
        this.document = null;
    }

    public String getNamespaceURI(String prefix) {
        Namespace ns = this.nsstack.getNamespaceForPrefix(prefix);
        return ns == null ? null : ns.getURI();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        if (this.currentEvt != 1) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Element e = this.emtstack[this.depth];
        if (!e.hasAttributes()) {
            return null;
        }
        if (namespaceURI != null) {
            return e.getAttributeValue(localName, Namespace.getNamespace(namespaceURI));
        }
        for (Attribute a : e.getAttributes()) {
            if (!a.getName().equalsIgnoreCase(localName)) continue;
            return a.getValue();
        }
        return null;
    }

    public int getAttributeCount() {
        if (this.currentEvt != 1) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        return this.emtstack[this.depth].getAttributesSize();
    }

    public QName getAttributeName(int index) {
        String prefix;
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        String ns = a.getNamespaceURI();
        if ("".equals(ns)) {
            ns = null;
        }
        if ((prefix = a.getNamespacePrefix()) == null || "".equals(prefix)) {
            prefix = "";
        }
        return new QName(ns, a.getName(), prefix);
    }

    public String getAttributeNamespace(int index) {
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        return a.getNamespaceURI();
    }

    public String getAttributeLocalName(int index) {
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        return a.getName();
    }

    public String getAttributePrefix(int index) {
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        return a.getNamespacePrefix();
    }

    public String getAttributeType(int index) {
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        return a.getAttributeType().name();
    }

    public String getAttributeValue(int index) {
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        return a.getValue();
    }

    public boolean isAttributeSpecified(int index) {
        if (this.currentEvt != 1 && this.currentEvt != 10) {
            throw new IllegalStateException("getAttributeCount not supported for event " + this.currentEvt);
        }
        Attribute a = this.emtstack[this.depth].getAttributes().get(index);
        return a.isSpecified();
    }

    public int getNamespaceCount() {
        switch (this.currentEvt) {
            case 1: 
            case 2: {
                Iterator<Namespace> it = this.nsstack.addedForward().iterator();
                int cnt = 0;
                while (it.hasNext()) {
                    ++cnt;
                    it.next();
                }
                return cnt;
            }
        }
        throw new IllegalStateException("getNamespaceCount not supported for event " + this.currentEvt);
    }

    private final Namespace getNamespaceByIndex(int index) {
        Iterator<Namespace> it = this.nsstack.addedForward().iterator();
        int cnt = 0;
        while (it.hasNext()) {
            if (cnt == index) {
                return it.next();
            }
            it.next();
            ++cnt;
        }
        throw new NoSuchElementException("No Namespace with index " + index + " (there are only " + cnt + ").");
    }

    public String getNamespacePrefix(int index) {
        switch (this.currentEvt) {
            case 1: 
            case 2: {
                return this.getNamespaceByIndex(index).getPrefix();
            }
        }
        throw new IllegalStateException("getNamespacePrefix not supported for event " + this.currentEvt);
    }

    public String getNamespaceURI(int index) {
        switch (this.currentEvt) {
            case 1: 
            case 2: 
            case 13: {
                return this.getNamespaceByIndex(index).getURI();
            }
        }
        throw new IllegalStateException("getNamespaceURI not supported for event " + this.currentEvt);
    }

    public NamespaceContext getNamespaceContext() {
        return new JDOMNamespaceContext(this.nsstack.getScope());
    }

    public boolean hasText() {
        switch (this.currentEvt) {
            case 4: 
            case 5: 
            case 9: 
            case 11: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    public String getText() {
        switch (this.currentEvt) {
            case 4: 
            case 5: 
            case 9: 
            case 11: 
            case 12: {
                return this.ctext;
            }
        }
        throw new IllegalStateException("getText not valid for event type " + this.currentEvt);
    }

    public char[] getTextCharacters() {
        return this.getText().toCharArray();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        int i;
        char[] chars = this.getText().toCharArray();
        for (i = 0; i < length; ++i) {
            if (sourceStart > chars.length) {
                return i;
            }
            if (targetStart > target.length) {
                return i;
            }
            target[targetStart++] = chars[sourceStart++];
        }
        return i;
    }

    public int getTextStart() {
        return 0;
    }

    public int getTextLength() {
        return this.getText().length();
    }

    public String getEncoding() {
        Object ret = this.document.getProperty("ENCODING");
        if (ret == null) {
            return null;
        }
        return ret.toString();
    }

    public Location getLocation() {
        return new Location(){

            public int getLineNumber() {
                return -1;
            }

            public int getColumnNumber() {
                return -1;
            }

            public int getCharacterOffset() {
                return -1;
            }

            public String getPublicId() {
                return null;
            }

            public String getSystemId() {
                return null;
            }
        };
    }

    public String getVersion() {
        return null;
    }

    public boolean isStandalone() {
        Object ret = this.document.getProperty("STANDALONE");
        return Boolean.TRUE.equals(ret);
    }

    public boolean standaloneSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCharacterEncodingScheme() {
        Object ret = this.document.getProperty("ENCODING_SCHEME");
        if (ret == null) {
            return null;
        }
        return ret.toString();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Property name is not allowed to be null");
        }
        if ("javax.xml.stream.allocator".equals(name)) {
            return null;
        }
        if ("javax.xml.stream.isCoalescing".equals(name)) {
            return this.formatstack.getDefaultMode() != Format.TextMode.PRESERVE;
        }
        if ("javax.xml.stream.isNamespaceAware".equals(name)) {
            return Boolean.TRUE;
        }
        if ("javax.xml.stream.isReplacingEntityReferences".equals(name)) {
            return Boolean.FALSE;
        }
        if ("javax.xml.stream.isSupportingExternalEntities".equals(name)) {
            return Boolean.FALSE;
        }
        if ("javax.xml.stream.isValidating".equals(name)) {
            return Boolean.TRUE;
        }
        if ("javax.xml.stream.reporter".equals(name)) {
            return null;
        }
        if ("javax.xml.stream.resolver".equals(name)) {
            return null;
        }
        return null;
    }

    private static final class DocumentWalker
    implements Walker {
        private final Content[] data;
        private int pos = 0;

        public DocumentWalker(Document doc) {
            this.data = doc.getContent().toArray(new Content[doc.getContentSize()]);
        }

        public boolean isAllText() {
            return false;
        }

        public boolean isAllWhitespace() {
            return false;
        }

        public boolean hasNext() {
            return this.pos < this.data.length;
        }

        public Content next() {
            return this.data[this.pos++];
        }

        public String text() {
            return null;
        }

        public boolean isCDATA() {
            return false;
        }
    }
}

