/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FinalArrayList
 *  com.sun.istack.NotNull
 *  com.sun.istack.XMLStreamException2
 */
package com.sun.xml.ws.streaming;

import com.sun.istack.FinalArrayList;
import com.sun.istack.NotNull;
import com.sun.istack.XMLStreamException2;
import com.sun.xml.ws.util.xml.DummyLocation;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMStreamReader
implements XMLStreamReader,
NamespaceContext {
    protected Node _current;
    private Node _start;
    private NamedNodeMap _namedNodeMap;
    protected String wholeText;
    private final FinalArrayList<Attr> _currentAttributes = new FinalArrayList();
    protected Scope[] scopes = new Scope[8];
    protected int depth = 0;
    protected int _state;

    public DOMStreamReader() {
    }

    public DOMStreamReader(Node node) {
        this.setCurrentNode(node);
    }

    public void setCurrentNode(Node node) {
        this.scopes[0] = new Scope(null);
        this.depth = 0;
        this._start = this._current = node;
        this._state = 7;
    }

    @Override
    public void close() throws XMLStreamException {
    }

    protected void splitAttributes() {
        this._currentAttributes.clear();
        Scope scope = this.allocateScope();
        this._namedNodeMap = this._current.getAttributes();
        if (this._namedNodeMap != null) {
            int n = this._namedNodeMap.getLength();
            for (int i = 0; i < n; ++i) {
                Attr attr = (Attr)this._namedNodeMap.item(i);
                String attrName = attr.getNodeName();
                if (attrName.startsWith("xmlns:") || attrName.equals("xmlns")) {
                    scope.currentNamespaces.add((Object)attr);
                    continue;
                }
                this._currentAttributes.add((Object)attr);
            }
        }
        this.ensureNs(this._current);
        for (int i = this._currentAttributes.size() - 1; i >= 0; --i) {
            Attr a = (Attr)this._currentAttributes.get(i);
            if (DOMStreamReader.fixNull(a.getNamespaceURI()).length() <= 0) continue;
            this.ensureNs(a);
        }
    }

    private void ensureNs(Node n) {
        String prefix = DOMStreamReader.fixNull(n.getPrefix());
        String uri = DOMStreamReader.fixNull(n.getNamespaceURI());
        Scope scope = this.scopes[this.depth];
        String currentUri = scope.getNamespaceURI(prefix);
        if (prefix.length() == 0 ? (currentUri = DOMStreamReader.fixNull(currentUri)).equals(uri) : currentUri != null && currentUri.equals(uri)) {
            return;
        }
        if (prefix.equals("xml") || prefix.equals("xmlns")) {
            return;
        }
        scope.additionalNamespaces.add((Object)prefix);
        scope.additionalNamespaces.add((Object)uri);
    }

    private Scope allocateScope() {
        Scope scope;
        if (this.scopes.length == ++this.depth) {
            Scope[] newBuf = new Scope[this.scopes.length * 2];
            System.arraycopy(this.scopes, 0, newBuf, 0, this.scopes.length);
            this.scopes = newBuf;
        }
        if ((scope = this.scopes[this.depth]) == null) {
            scope = this.scopes[this.depth] = new Scope(this.scopes[this.depth - 1]);
        } else {
            scope.reset();
        }
        return scope;
    }

    @Override
    public int getAttributeCount() {
        if (this._state == 1) {
            return this._currentAttributes.size();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeCount() called in illegal state");
    }

    @Override
    public String getAttributeLocalName(int index) {
        if (this._state == 1) {
            String localName = ((Attr)this._currentAttributes.get(index)).getLocalName();
            return localName != null ? localName : QName.valueOf(((Attr)this._currentAttributes.get(index)).getNodeName()).getLocalPart();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeLocalName() called in illegal state");
    }

    @Override
    public QName getAttributeName(int index) {
        if (this._state == 1) {
            Node attr = (Node)this._currentAttributes.get(index);
            String localName = attr.getLocalName();
            if (localName != null) {
                String prefix = attr.getPrefix();
                String uri = attr.getNamespaceURI();
                return new QName(DOMStreamReader.fixNull(uri), localName, DOMStreamReader.fixNull(prefix));
            }
            return QName.valueOf(attr.getNodeName());
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeName() called in illegal state");
    }

    @Override
    public String getAttributeNamespace(int index) {
        if (this._state == 1) {
            String uri = ((Attr)this._currentAttributes.get(index)).getNamespaceURI();
            return DOMStreamReader.fixNull(uri);
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeNamespace() called in illegal state");
    }

    @Override
    public String getAttributePrefix(int index) {
        if (this._state == 1) {
            String prefix = ((Attr)this._currentAttributes.get(index)).getPrefix();
            return DOMStreamReader.fixNull(prefix);
        }
        throw new IllegalStateException("DOMStreamReader: getAttributePrefix() called in illegal state");
    }

    @Override
    public String getAttributeType(int index) {
        if (this._state == 1) {
            return "CDATA";
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeType() called in illegal state");
    }

    @Override
    public String getAttributeValue(int index) {
        if (this._state == 1) {
            return ((Attr)this._currentAttributes.get(index)).getNodeValue();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        if (this._state == 1) {
            if (this._namedNodeMap != null) {
                Node attr = this._namedNodeMap.getNamedItemNS(namespaceURI, localName);
                return attr != null ? attr.getNodeValue() : null;
            }
            return null;
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
    }

    @Override
    public String getCharacterEncodingScheme() {
        return null;
    }

    @Override
    public String getElementText() throws XMLStreamException {
        throw new RuntimeException("DOMStreamReader: getElementText() not implemented");
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public int getEventType() {
        return this._state;
    }

    @Override
    public String getLocalName() {
        if (this._state == 1 || this._state == 2) {
            String localName = this._current.getLocalName();
            return localName != null ? localName : QName.valueOf(this._current.getNodeName()).getLocalPart();
        }
        if (this._state == 9) {
            return this._current.getNodeName();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
    }

    @Override
    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }

    @Override
    public QName getName() {
        if (this._state == 1 || this._state == 2) {
            String localName = this._current.getLocalName();
            if (localName != null) {
                String prefix = this._current.getPrefix();
                String uri = this._current.getNamespaceURI();
                return new QName(DOMStreamReader.fixNull(uri), localName, DOMStreamReader.fixNull(prefix));
            }
            return QName.valueOf(this._current.getNodeName());
        }
        throw new IllegalStateException("DOMStreamReader: getName() called in illegal state");
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this;
    }

    private Scope getCheckedScope() {
        if (this._state == 1 || this._state == 2) {
            return this.scopes[this.depth];
        }
        throw new IllegalStateException("DOMStreamReader: neither on START_ELEMENT nor END_ELEMENT");
    }

    @Override
    public int getNamespaceCount() {
        return this.getCheckedScope().getNamespaceCount();
    }

    @Override
    public String getNamespacePrefix(int index) {
        return this.getCheckedScope().getNamespacePrefix(index);
    }

    @Override
    public String getNamespaceURI(int index) {
        return this.getCheckedScope().getNamespaceURI(index);
    }

    @Override
    public String getNamespaceURI() {
        if (this._state == 1 || this._state == 2) {
            String uri = this._current.getNamespaceURI();
            return DOMStreamReader.fixNull(uri);
        }
        return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        String nsDeclName;
        if (prefix == null) {
            throw new IllegalArgumentException("DOMStreamReader: getNamespaceURI(String) call with a null prefix");
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        String nsUri = this.scopes[this.depth].getNamespaceURI(prefix);
        if (nsUri != null) {
            return nsUri;
        }
        Node node = this.findRootElement();
        String string = nsDeclName = prefix.length() == 0 ? "xmlns" : "xmlns:" + prefix;
        while (node.getNodeType() != 9) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            Attr attr = (Attr)namedNodeMap.getNamedItem(nsDeclName);
            if (attr != null) {
                return attr.getValue();
            }
            node = node.getParentNode();
        }
        return null;
    }

    @Override
    public String getPrefix(String nsUri) {
        if (nsUri == null) {
            throw new IllegalArgumentException("DOMStreamReader: getPrefix(String) call with a null namespace URI");
        }
        if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        String prefix = this.scopes[this.depth].getPrefix(nsUri);
        if (prefix != null) {
            return prefix;
        }
        Node node = this.findRootElement();
        while (node.getNodeType() != 9) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            for (int i = namedNodeMap.getLength() - 1; i >= 0; --i) {
                Attr attr = (Attr)namedNodeMap.item(i);
                prefix = DOMStreamReader.getPrefixForAttr(attr, nsUri);
                if (prefix == null) continue;
                return prefix;
            }
            node = node.getParentNode();
        }
        return null;
    }

    private Node findRootElement() {
        short type;
        Node node = this._start;
        while ((type = node.getNodeType()) != 9 && type != 1) {
            node = node.getParentNode();
        }
        return node;
    }

    private static String getPrefixForAttr(Attr attr, String nsUri) {
        String attrName = attr.getNodeName();
        if (!attrName.startsWith("xmlns:") && !attrName.equals("xmlns")) {
            return null;
        }
        if (attr.getValue().equals(nsUri)) {
            if (attrName.equals("xmlns")) {
                return "";
            }
            String localName = attr.getLocalName();
            return localName != null ? localName : QName.valueOf(attrName).getLocalPart();
        }
        return null;
    }

    public Iterator getPrefixes(String nsUri) {
        String prefix = this.getPrefix(nsUri);
        if (prefix == null) {
            return Collections.emptyList().iterator();
        }
        return Collections.singletonList(prefix).iterator();
    }

    @Override
    public String getPIData() {
        if (this._state == 3) {
            return ((ProcessingInstruction)this._current).getData();
        }
        return null;
    }

    @Override
    public String getPITarget() {
        if (this._state == 3) {
            return ((ProcessingInstruction)this._current).getTarget();
        }
        return null;
    }

    @Override
    public String getPrefix() {
        if (this._state == 1 || this._state == 2) {
            String prefix = this._current.getPrefix();
            return DOMStreamReader.fixNull(prefix);
        }
        return null;
    }

    @Override
    public Object getProperty(String str) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String getText() {
        if (this._state == 4) {
            return this.wholeText;
        }
        if (this._state == 12 || this._state == 5 || this._state == 9) {
            return this._current.getNodeValue();
        }
        throw new IllegalStateException("DOMStreamReader: getTextLength() called in illegal state");
    }

    @Override
    public char[] getTextCharacters() {
        return this.getText().toCharArray();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int targetLength) throws XMLStreamException {
        String text = this.getText();
        int copiedSize = Math.min(targetLength, text.length() - sourceStart);
        text.getChars(sourceStart, sourceStart + copiedSize, target, targetStart);
        return copiedSize;
    }

    @Override
    public int getTextLength() {
        return this.getText().length();
    }

    @Override
    public int getTextStart() {
        if (this._state == 4 || this._state == 12 || this._state == 5 || this._state == 9) {
            return 0;
        }
        throw new IllegalStateException("DOMStreamReader: getTextStart() called in illegal state");
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public boolean hasName() {
        return this._state == 1 || this._state == 2;
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return this._state != 8;
    }

    @Override
    public boolean hasText() {
        if (this._state == 4 || this._state == 12 || this._state == 5 || this._state == 9) {
            return this.getText().trim().length() > 0;
        }
        return false;
    }

    @Override
    public boolean isAttributeSpecified(int param) {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return this._state == 4;
    }

    @Override
    public boolean isEndElement() {
        return this._state == 2;
    }

    @Override
    public boolean isStandalone() {
        return true;
    }

    @Override
    public boolean isStartElement() {
        return this._state == 1;
    }

    @Override
    public boolean isWhiteSpace() {
        if (this._state == 4 || this._state == 12) {
            return this.getText().trim().length() == 0;
        }
        return false;
    }

    private static int mapNodeTypeToState(int nodetype) {
        switch (nodetype) {
            case 4: {
                return 12;
            }
            case 8: {
                return 5;
            }
            case 1: {
                return 1;
            }
            case 6: {
                return 15;
            }
            case 5: {
                return 9;
            }
            case 12: {
                return 14;
            }
            case 7: {
                return 3;
            }
            case 3: {
                return 4;
            }
        }
        throw new RuntimeException("DOMStreamReader: Unexpected node type");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public int next() throws XMLStreamException {
        int r;
        block4: while (true) {
            r = this._next();
            switch (r) {
                case 4: {
                    Node prev = this._current.getPreviousSibling();
                    if (prev != null && prev.getNodeType() == 3) continue block4;
                    Text t = (Text)this._current;
                    this.wholeText = t.getWholeText();
                    if (this.wholeText.length() != 0) return 4;
                    continue block4;
                }
                case 1: {
                    this.splitAttributes();
                    return 1;
                }
            }
            break;
        }
        return r;
    }

    protected int _next() throws XMLStreamException {
        switch (this._state) {
            case 8: {
                throw new IllegalStateException("DOMStreamReader: Calling next() at END_DOCUMENT");
            }
            case 7: {
                if (this._current.getNodeType() == 1) {
                    this._state = 1;
                    return 1;
                }
                Node child = this._current.getFirstChild();
                if (child == null) {
                    this._state = 8;
                    return 8;
                }
                this._current = child;
                this._state = DOMStreamReader.mapNodeTypeToState(this._current.getNodeType());
                return this._state;
            }
            case 1: {
                Node child = this._current.getFirstChild();
                if (child == null) {
                    this._state = 2;
                    return 2;
                }
                this._current = child;
                this._state = DOMStreamReader.mapNodeTypeToState(this._current.getNodeType());
                return this._state;
            }
            case 2: {
                --this.depth;
            }
            case 3: 
            case 4: 
            case 5: 
            case 9: 
            case 12: {
                if (this._current == this._start) {
                    this._state = 8;
                    return 8;
                }
                Node sibling = this._current.getNextSibling();
                if (sibling == null) {
                    this._current = this._current.getParentNode();
                    this._state = this._current == null || this._current.getNodeType() == 9 ? 8 : 2;
                    return this._state;
                }
                this._current = sibling;
                this._state = DOMStreamReader.mapNodeTypeToState(this._current.getNodeType());
                return this._state;
            }
        }
        throw new RuntimeException("DOMStreamReader: Unexpected internal state");
    }

    @Override
    public int nextTag() throws XMLStreamException {
        int eventType = this.next();
        while (eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5) {
            eventType = this.next();
        }
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException2("DOMStreamReader: Expected start or end tag");
        }
        return eventType;
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (type != this._state) {
            throw new XMLStreamException2("DOMStreamReader: Required event type not found");
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException2("DOMStreamReader: Required namespaceURI not found");
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException2("DOMStreamReader: Required localName not found");
        }
    }

    @Override
    public boolean standaloneSet() {
        return true;
    }

    private static void displayDOM(Node node, OutputStream ostream) {
        try {
            System.out.println("\n====\n");
            XmlUtil.newTransformer().transform(new DOMSource(node), new StreamResult(ostream));
            System.out.println("\n====\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void verifyDOMIntegrity(Node node) {
        switch (node.getNodeType()) {
            case 1: 
            case 2: {
                if (node.getLocalName() == null) {
                    System.out.println("WARNING: DOM level 1 node found");
                    System.out.println(" -> node.getNodeName() = " + node.getNodeName());
                    System.out.println(" -> node.getNamespaceURI() = " + node.getNamespaceURI());
                    System.out.println(" -> node.getLocalName() = " + node.getLocalName());
                    System.out.println(" -> node.getPrefix() = " + node.getPrefix());
                }
                if (node.getNodeType() == 2) {
                    return;
                }
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    DOMStreamReader.verifyDOMIntegrity(attrs.item(i));
                }
            }
            case 9: {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); ++i) {
                    DOMStreamReader.verifyDOMIntegrity(children.item(i));
                }
                break;
            }
        }
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    protected static final class Scope {
        final Scope parent;
        final FinalArrayList<Attr> currentNamespaces = new FinalArrayList();
        final FinalArrayList<String> additionalNamespaces = new FinalArrayList();

        Scope(Scope parent) {
            this.parent = parent;
        }

        void reset() {
            this.currentNamespaces.clear();
            this.additionalNamespaces.clear();
        }

        int getNamespaceCount() {
            return this.currentNamespaces.size() + this.additionalNamespaces.size() / 2;
        }

        String getNamespacePrefix(int index) {
            int sz = this.currentNamespaces.size();
            if (index < sz) {
                Attr attr = (Attr)this.currentNamespaces.get(index);
                String result = attr.getLocalName();
                if (result == null) {
                    result = QName.valueOf(attr.getNodeName()).getLocalPart();
                }
                return result.equals("xmlns") ? null : result;
            }
            return (String)this.additionalNamespaces.get((index - sz) * 2);
        }

        String getNamespaceURI(int index) {
            int sz = this.currentNamespaces.size();
            if (index < sz) {
                return ((Attr)this.currentNamespaces.get(index)).getValue();
            }
            return (String)this.additionalNamespaces.get((index - sz) * 2 + 1);
        }

        String getPrefix(String nsUri) {
            Scope sp = this;
            while (sp != null) {
                int i;
                for (i = sp.currentNamespaces.size() - 1; i >= 0; --i) {
                    String result = DOMStreamReader.getPrefixForAttr((Attr)sp.currentNamespaces.get(i), nsUri);
                    if (result == null) continue;
                    return result;
                }
                for (i = sp.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
                    if (!((String)sp.additionalNamespaces.get(i + 1)).equals(nsUri)) continue;
                    return (String)sp.additionalNamespaces.get(i);
                }
                sp = sp.parent;
            }
            return null;
        }

        String getNamespaceURI(@NotNull String prefix) {
            String nsDeclName = prefix.length() == 0 ? "xmlns" : "xmlns:" + prefix;
            Scope sp = this;
            while (sp != null) {
                int i;
                for (i = sp.currentNamespaces.size() - 1; i >= 0; --i) {
                    Attr a = (Attr)sp.currentNamespaces.get(i);
                    if (!a.getNodeName().equals(nsDeclName)) continue;
                    return a.getValue();
                }
                for (i = sp.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
                    if (!((String)sp.additionalNamespaces.get(i)).equals(prefix)) continue;
                    return (String)sp.additionalNamespaces.get(i + 1);
                }
                sp = sp.parent;
            }
            return null;
        }
    }
}

