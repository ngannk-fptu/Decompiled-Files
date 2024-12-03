/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public abstract class NodeImpl
implements Node,
NodeList,
EventTarget,
Cloneable,
Serializable {
    public static final short TREE_POSITION_PRECEDING = 1;
    public static final short TREE_POSITION_FOLLOWING = 2;
    public static final short TREE_POSITION_ANCESTOR = 4;
    public static final short TREE_POSITION_DESCENDANT = 8;
    public static final short TREE_POSITION_EQUIVALENT = 16;
    public static final short TREE_POSITION_SAME_NODE = 32;
    public static final short TREE_POSITION_DISCONNECTED = 0;
    public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
    public static final short DOCUMENT_POSITION_PRECEDING = 2;
    public static final short DOCUMENT_POSITION_FOLLOWING = 4;
    public static final short DOCUMENT_POSITION_CONTAINS = 8;
    public static final short DOCUMENT_POSITION_IS_CONTAINED = 16;
    public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
    static final long serialVersionUID = -6316591992167219696L;
    public static final short ELEMENT_DEFINITION_NODE = 21;
    protected NodeImpl ownerNode;
    protected short flags;
    protected static final short READONLY = 1;
    protected static final short SYNCDATA = 2;
    protected static final short SYNCCHILDREN = 4;
    protected static final short OWNED = 8;
    protected static final short FIRSTCHILD = 16;
    protected static final short SPECIFIED = 32;
    protected static final short IGNORABLEWS = 64;
    protected static final short HASSTRING = 128;
    protected static final short NORMALIZED = 256;
    protected static final short ID = 512;

    protected NodeImpl(CoreDocumentImpl coreDocumentImpl) {
        this.ownerNode = coreDocumentImpl;
    }

    public NodeImpl() {
    }

    @Override
    public abstract short getNodeType();

    @Override
    public abstract String getNodeName();

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public void setNodeValue(String string) throws DOMException {
    }

    @Override
    public Node appendChild(Node node) throws DOMException {
        return this.insertBefore(node, null);
    }

    @Override
    public Node cloneNode(boolean bl) {
        NodeImpl nodeImpl;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        try {
            nodeImpl = (NodeImpl)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new RuntimeException("**Internal Error**" + cloneNotSupportedException);
        }
        nodeImpl.ownerNode = this.ownerDocument();
        nodeImpl.isOwned(false);
        nodeImpl.isReadOnly(false);
        this.ownerDocument().callUserDataHandlers(this, nodeImpl, (short)1);
        return nodeImpl;
    }

    @Override
    public Document getOwnerDocument() {
        if (this.isOwned()) {
            return this.ownerNode.ownerDocument();
        }
        return (Document)((Object)this.ownerNode);
    }

    CoreDocumentImpl ownerDocument() {
        if (this.isOwned()) {
            return this.ownerNode.ownerDocument();
        }
        return (CoreDocumentImpl)this.ownerNode;
    }

    protected void setOwnerDocument(CoreDocumentImpl coreDocumentImpl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (!this.isOwned()) {
            this.ownerNode = coreDocumentImpl;
        }
    }

    protected int getNodeNumber() {
        CoreDocumentImpl coreDocumentImpl = (CoreDocumentImpl)this.getOwnerDocument();
        int n = coreDocumentImpl.getNodeNumber(this);
        return n;
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    NodeImpl parentNode() {
        return null;
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public Node getPreviousSibling() {
        return null;
    }

    ChildNode previousSibling() {
        return null;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public NodeList getChildNodes() {
        return this;
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public Node getLastChild() {
        return null;
    }

    @Override
    public Node insertBefore(Node node, Node node2) throws DOMException {
        throw new DOMException(3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }

    @Override
    public Node removeChild(Node node) throws DOMException {
        throw new DOMException(8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
    }

    @Override
    public Node replaceChild(Node node, Node node2) throws DOMException {
        throw new DOMException(3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Node item(int n) {
        return null;
    }

    @Override
    public void normalize() {
    }

    @Override
    public boolean isSupported(String string, String string2) {
        return this.ownerDocument().getImplementation().hasFeature(string, string2);
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public void setPrefix(String string) throws DOMException {
        throw new DOMException(14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public void addEventListener(String string, EventListener eventListener, boolean bl) {
        this.ownerDocument().addEventListener(this, string, eventListener, bl);
    }

    @Override
    public void removeEventListener(String string, EventListener eventListener, boolean bl) {
        this.ownerDocument().removeEventListener(this, string, eventListener, bl);
    }

    @Override
    public boolean dispatchEvent(Event event) {
        return this.ownerDocument().dispatchEvent(this, event);
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    public short compareTreePosition(Node node) {
        int n;
        Node node2;
        if (this == node) {
            return 48;
        }
        short s = this.getNodeType();
        short s2 = node.getNodeType();
        if (s == 6 || s == 12 || s2 == 6 || s2 == 12) {
            return 0;
        }
        Node node3 = this;
        Node node4 = node;
        int n2 = 0;
        int n3 = 0;
        for (node2 = this; node2 != null; node2 = node2.getParentNode()) {
            ++n2;
            if (node2 == node) {
                return 5;
            }
            node3 = node2;
        }
        for (node2 = node; node2 != null; node2 = node2.getParentNode()) {
            ++n3;
            if (node2 == this) {
                return 10;
            }
            node4 = node2;
        }
        Node node5 = this;
        Node node6 = node;
        short s3 = node3.getNodeType();
        short s4 = node4.getNodeType();
        if (s3 == 2) {
            node5 = ((AttrImpl)node3).getOwnerElement();
        }
        if (s4 == 2) {
            node6 = ((AttrImpl)node4).getOwnerElement();
        }
        if (s3 == 2 && s4 == 2 && node5 == node6) {
            return 16;
        }
        if (s3 == 2) {
            n2 = 0;
            for (node2 = node5; node2 != null; node2 = node2.getParentNode()) {
                ++n2;
                if (node2 == node6) {
                    return 1;
                }
                node3 = node2;
            }
        }
        if (s4 == 2) {
            n3 = 0;
            for (node2 = node6; node2 != null; node2 = node2.getParentNode()) {
                ++n3;
                if (node2 == node5) {
                    return 2;
                }
                node4 = node2;
            }
        }
        if (node3 != node4) {
            return 0;
        }
        if (n2 > n3) {
            for (n = 0; n < n2 - n3; ++n) {
                node5 = node5.getParentNode();
            }
            if (node5 == node6) {
                return 1;
            }
        } else {
            for (n = 0; n < n3 - n2; ++n) {
                node6 = node6.getParentNode();
            }
            if (node6 == node5) {
                return 2;
            }
        }
        Node node7 = node5.getParentNode();
        for (Node node8 = node6.getParentNode(); node7 != node8; node7 = node7.getParentNode(), node8 = node8.getParentNode()) {
            node5 = node7;
            node6 = node8;
        }
        for (Node node9 = node7.getFirstChild(); node9 != null; node9 = node9.getNextSibling()) {
            if (node9 == node6) {
                return 1;
            }
            if (node9 != node5) continue;
            return 2;
        }
        return 0;
    }

    @Override
    public short compareDocumentPosition(Node node) throws DOMException {
        int n;
        DocumentType documentType;
        Node node2;
        Document document;
        if (this == node) {
            return 0;
        }
        if (node != null && !(node instanceof NodeImpl)) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string);
        }
        Document document2 = this.getNodeType() == 9 ? (Document)((Object)this) : this.getOwnerDocument();
        if (document2 != (document = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument()) && document2 != null && document != null) {
            int n2;
            int n3 = ((CoreDocumentImpl)document).getNodeNumber();
            if (n3 > (n2 = ((CoreDocumentImpl)document2).getNodeNumber())) {
                return 37;
            }
            return 35;
        }
        Node node3 = this;
        Node node4 = node;
        int n4 = 0;
        int n5 = 0;
        for (node2 = this; node2 != null; node2 = node2.getParentNode()) {
            ++n4;
            if (node2 == node) {
                return 10;
            }
            node3 = node2;
        }
        for (node2 = node; node2 != null; node2 = node2.getParentNode()) {
            ++n5;
            if (node2 == this) {
                return 20;
            }
            node4 = node2;
        }
        short s = node3.getNodeType();
        short s2 = node4.getNodeType();
        Node node5 = this;
        Node node6 = node;
        switch (s) {
            case 6: 
            case 12: {
                documentType = document2.getDoctype();
                if (documentType == node4) {
                    return 10;
                }
                switch (s2) {
                    case 6: 
                    case 12: {
                        if (s != s2) {
                            return s > s2 ? (short)2 : 4;
                        }
                        if (s == 12) {
                            if (((NamedNodeMapImpl)documentType.getNotations()).precedes(node4, node3)) {
                                return 34;
                            }
                            return 36;
                        }
                        if (((NamedNodeMapImpl)documentType.getEntities()).precedes(node4, node3)) {
                            return 34;
                        }
                        return 36;
                    }
                }
                node3 = document2;
                node5 = node3;
                break;
            }
            case 10: {
                if (node6 == document2) {
                    return 10;
                }
                if (document2 == null || document2 != document) break;
                return 4;
            }
            case 2: {
                node5 = ((AttrImpl)node3).getOwnerElement();
                if (s2 == 2 && (node6 = ((AttrImpl)node4).getOwnerElement()) == node5) {
                    if (((NamedNodeMapImpl)node5.getAttributes()).precedes(node, this)) {
                        return 34;
                    }
                    return 36;
                }
                n4 = 0;
                for (node2 = node5; node2 != null; node2 = node2.getParentNode()) {
                    ++n4;
                    if (node2 == node6) {
                        return 10;
                    }
                    node3 = node2;
                }
                break;
            }
        }
        switch (s2) {
            case 6: 
            case 12: {
                documentType = document2.getDoctype();
                if (documentType == this) {
                    return 20;
                }
                node6 = node4 = document2;
                break;
            }
            case 10: {
                if (node5 == document) {
                    return 20;
                }
                if (document == null || document2 != document) break;
                return 2;
            }
            case 2: {
                n5 = 0;
                for (node2 = node6 = ((AttrImpl)node4).getOwnerElement(); node2 != null; node2 = node2.getParentNode()) {
                    ++n5;
                    if (node2 == node5) {
                        return 20;
                    }
                    node4 = node2;
                }
                break;
            }
        }
        if (node3 != node4) {
            int n6;
            int n7 = node3.getNodeNumber();
            if (n7 > (n6 = ((NodeImpl)node4).getNodeNumber())) {
                return 37;
            }
            return 35;
        }
        if (n4 > n5) {
            for (n = 0; n < n4 - n5; ++n) {
                node5 = node5.getParentNode();
            }
            if (node5 == node6) {
                return 2;
            }
        } else {
            for (n = 0; n < n5 - n4; ++n) {
                node6 = node6.getParentNode();
            }
            if (node6 == node5) {
                return 4;
            }
        }
        Node node7 = node5.getParentNode();
        for (Node node8 = node6.getParentNode(); node7 != node8; node7 = node7.getParentNode(), node8 = node8.getParentNode()) {
            node5 = node7;
            node6 = node8;
        }
        for (Node node9 = node7.getFirstChild(); node9 != null; node9 = node9.getNextSibling()) {
            if (node9 == node6) {
                return 2;
            }
            if (node9 != node5) continue;
            return 4;
        }
        return 0;
    }

    @Override
    public String getTextContent() throws DOMException {
        return this.getNodeValue();
    }

    void getTextContent(StringBuffer stringBuffer) throws DOMException {
        String string = this.getNodeValue();
        if (string != null) {
            stringBuffer.append(string);
        }
    }

    @Override
    public void setTextContent(String string) throws DOMException {
        this.setNodeValue(string);
    }

    @Override
    public boolean isSameNode(Node node) {
        return this == node;
    }

    @Override
    public boolean isDefaultNamespace(String string) {
        short s = this.getNodeType();
        switch (s) {
            case 1: {
                NodeImpl nodeImpl;
                NodeImpl nodeImpl2;
                String string2 = this.getNamespaceURI();
                String string3 = this.getPrefix();
                if (string3 == null || string3.length() == 0) {
                    if (string == null) {
                        return string2 == string;
                    }
                    return string.equals(string2);
                }
                if (this.hasAttributes() && (nodeImpl2 = (NodeImpl)((Object)((ElementImpl)(nodeImpl = (ElementImpl)this)).getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns"))) != null) {
                    String string4 = nodeImpl2.getNodeValue();
                    if (string == null) {
                        return string2 == string4;
                    }
                    return string.equals(string4);
                }
                nodeImpl = (NodeImpl)this.getElementAncestor(this);
                if (nodeImpl != null) {
                    return nodeImpl.isDefaultNamespace(string);
                }
                return false;
            }
            case 9: {
                Element element = ((Document)((Object)this)).getDocumentElement();
                if (element != null) {
                    return element.isDefaultNamespace(string);
                }
                return false;
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return false;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.isDefaultNamespace(string);
                }
                return false;
            }
        }
        NodeImpl nodeImpl = (NodeImpl)this.getElementAncestor(this);
        if (nodeImpl != null) {
            return nodeImpl.isDefaultNamespace(string);
        }
        return false;
    }

    @Override
    public String lookupPrefix(String string) {
        if (string == null) {
            return null;
        }
        short s = this.getNodeType();
        switch (s) {
            case 1: {
                this.getNamespaceURI();
                return this.lookupNamespacePrefix(string, (ElementImpl)this);
            }
            case 9: {
                Element element = ((Document)((Object)this)).getDocumentElement();
                if (element != null) {
                    return element.lookupPrefix(string);
                }
                return null;
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return null;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.lookupPrefix(string);
                }
                return null;
            }
        }
        NodeImpl nodeImpl = (NodeImpl)this.getElementAncestor(this);
        if (nodeImpl != null) {
            return nodeImpl.lookupPrefix(string);
        }
        return null;
    }

    @Override
    public String lookupNamespaceURI(String string) {
        short s = this.getNodeType();
        switch (s) {
            case 1: {
                Object object;
                String string2 = this.getNamespaceURI();
                String string3 = this.getPrefix();
                if (string2 != null) {
                    if (string == null && string3 == string) {
                        return string2;
                    }
                    if (string3 != null && string3.equals(string)) {
                        return string2;
                    }
                }
                if (this.hasAttributes()) {
                    object = this.getAttributes();
                    int n = object.getLength();
                    for (int i = 0; i < n; ++i) {
                        Node node = object.item(i);
                        string2 = node.getNamespaceURI();
                        if (string2 == null || !string2.equals("http://www.w3.org/2000/xmlns/")) continue;
                        String string4 = node.getPrefix();
                        String string5 = node.getNodeValue();
                        if (string == null && node.getNodeName().equals("xmlns")) {
                            return string5.length() > 0 ? string5 : null;
                        }
                        if (string4 == null || !string4.equals("xmlns") || !node.getLocalName().equals(string)) continue;
                        return string5.length() > 0 ? string5 : null;
                    }
                }
                if ((object = (NodeImpl)this.getElementAncestor(this)) != null) {
                    return ((NodeImpl)object).lookupNamespaceURI(string);
                }
                return null;
            }
            case 9: {
                Element element = ((Document)((Object)this)).getDocumentElement();
                if (element != null) {
                    return element.lookupNamespaceURI(string);
                }
                return null;
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return null;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.lookupNamespaceURI(string);
                }
                return null;
            }
        }
        NodeImpl nodeImpl = (NodeImpl)this.getElementAncestor(this);
        if (nodeImpl != null) {
            return nodeImpl.lookupNamespaceURI(string);
        }
        return null;
    }

    Node getElementAncestor(Node node) {
        for (Node node2 = node.getParentNode(); node2 != null; node2 = node2.getParentNode()) {
            short s = node2.getNodeType();
            if (s != 1) continue;
            return node2;
        }
        return null;
    }

    String lookupNamespacePrefix(String string, ElementImpl elementImpl) {
        Object object;
        String string2 = this.getNamespaceURI();
        String string3 = this.getPrefix();
        if (string2 != null && string2.equals(string) && string3 != null && (object = elementImpl.lookupNamespaceURI(string3)) != null && ((String)object).equals(string)) {
            return string3;
        }
        if (this.hasAttributes()) {
            object = this.getAttributes();
            int n = object.getLength();
            for (int i = 0; i < n; ++i) {
                String string4;
                String string5;
                Node node = object.item(i);
                string2 = node.getNamespaceURI();
                if (string2 == null || !string2.equals("http://www.w3.org/2000/xmlns/")) continue;
                String string6 = node.getPrefix();
                String string7 = node.getNodeValue();
                if (!node.getNodeName().equals("xmlns") && (string6 == null || !string6.equals("xmlns") || !string7.equals(string)) || (string5 = elementImpl.lookupNamespaceURI(string4 = node.getLocalName())) == null || !string5.equals(string)) continue;
                return string4;
            }
        }
        if ((object = (NodeImpl)this.getElementAncestor(this)) != null) {
            return ((NodeImpl)object).lookupNamespacePrefix(string, elementImpl);
        }
        return null;
    }

    @Override
    public boolean isEqualNode(Node node) {
        if (node == this) {
            return true;
        }
        if (node.getNodeType() != this.getNodeType()) {
            return false;
        }
        if (this.getNodeName() == null ? node.getNodeName() != null : !this.getNodeName().equals(node.getNodeName())) {
            return false;
        }
        if (this.getLocalName() == null ? node.getLocalName() != null : !this.getLocalName().equals(node.getLocalName())) {
            return false;
        }
        if (this.getNamespaceURI() == null ? node.getNamespaceURI() != null : !this.getNamespaceURI().equals(node.getNamespaceURI())) {
            return false;
        }
        if (this.getPrefix() == null ? node.getPrefix() != null : !this.getPrefix().equals(node.getPrefix())) {
            return false;
        }
        return !(this.getNodeValue() == null ? node.getNodeValue() != null : !this.getNodeValue().equals(node.getNodeValue()));
    }

    @Override
    public Object getFeature(String string, String string2) {
        return this.isSupported(string, string2) ? this : null;
    }

    @Override
    public Object setUserData(String string, Object object, UserDataHandler userDataHandler) {
        return this.ownerDocument().setUserData(this, string, object, userDataHandler);
    }

    @Override
    public Object getUserData(String string) {
        return this.ownerDocument().getUserData(this, string);
    }

    protected Hashtable getUserDataRecord() {
        return this.ownerDocument().getUserDataRecord(this);
    }

    public void setReadOnly(boolean bl, boolean bl2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isReadOnly(bl);
    }

    public boolean getReadOnly() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.isReadOnly();
    }

    public void setUserData(Object object) {
        this.ownerDocument().setUserData(this, object);
    }

    public Object getUserData() {
        return this.ownerDocument().getUserData(this);
    }

    protected void changed() {
        this.ownerDocument().changed();
    }

    protected int changes() {
        return this.ownerDocument().changes();
    }

    protected void synchronizeData() {
        this.needsSyncData(false);
    }

    protected Node getContainer() {
        return null;
    }

    final boolean isReadOnly() {
        return (this.flags & 1) != 0;
    }

    final void isReadOnly(boolean bl) {
        this.flags = (short)(bl ? this.flags | 1 : this.flags & 0xFFFFFFFE);
    }

    final boolean needsSyncData() {
        return (this.flags & 2) != 0;
    }

    final void needsSyncData(boolean bl) {
        this.flags = (short)(bl ? this.flags | 2 : this.flags & 0xFFFFFFFD);
    }

    final boolean needsSyncChildren() {
        return (this.flags & 4) != 0;
    }

    public final void needsSyncChildren(boolean bl) {
        this.flags = (short)(bl ? this.flags | 4 : this.flags & 0xFFFFFFFB);
    }

    final boolean isOwned() {
        return (this.flags & 8) != 0;
    }

    final void isOwned(boolean bl) {
        this.flags = (short)(bl ? this.flags | 8 : this.flags & 0xFFFFFFF7);
    }

    final boolean isFirstChild() {
        return (this.flags & 0x10) != 0;
    }

    final void isFirstChild(boolean bl) {
        this.flags = (short)(bl ? this.flags | 0x10 : this.flags & 0xFFFFFFEF);
    }

    final boolean isSpecified() {
        return (this.flags & 0x20) != 0;
    }

    final void isSpecified(boolean bl) {
        this.flags = (short)(bl ? this.flags | 0x20 : this.flags & 0xFFFFFFDF);
    }

    final boolean internalIsIgnorableWhitespace() {
        return (this.flags & 0x40) != 0;
    }

    final void isIgnorableWhitespace(boolean bl) {
        this.flags = (short)(bl ? this.flags | 0x40 : this.flags & 0xFFFFFFBF);
    }

    final boolean hasStringValue() {
        return (this.flags & 0x80) != 0;
    }

    final void hasStringValue(boolean bl) {
        this.flags = (short)(bl ? this.flags | 0x80 : this.flags & 0xFFFFFF7F);
    }

    final boolean isNormalized() {
        return (this.flags & 0x100) != 0;
    }

    final void isNormalized(boolean bl) {
        if (!bl && this.isNormalized() && this.ownerNode != null) {
            this.ownerNode.isNormalized(false);
        }
        this.flags = (short)(bl ? this.flags | 0x100 : this.flags & 0xFFFFFEFF);
    }

    final boolean isIdAttribute() {
        return (this.flags & 0x200) != 0;
    }

    final void isIdAttribute(boolean bl) {
        this.flags = (short)(bl ? this.flags | 0x200 : this.flags & 0xFFFFFDFF);
    }

    public String toString() {
        return "[" + this.getNodeName() + ": " + this.getNodeValue() + "]";
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        objectOutputStream.defaultWriteObject();
    }
}

