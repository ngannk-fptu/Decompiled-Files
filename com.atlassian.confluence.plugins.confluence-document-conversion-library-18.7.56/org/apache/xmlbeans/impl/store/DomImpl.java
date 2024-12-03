/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.soap.Detail;
import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.soap.MimeHeader;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.Node;
import org.apache.xmlbeans.impl.soap.SOAPBody;
import org.apache.xmlbeans.impl.soap.SOAPBodyElement;
import org.apache.xmlbeans.impl.soap.SOAPElement;
import org.apache.xmlbeans.impl.soap.SOAPEnvelope;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.SOAPFault;
import org.apache.xmlbeans.impl.soap.SOAPHeader;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;
import org.apache.xmlbeans.impl.soap.SOAPPart;
import org.apache.xmlbeans.impl.store.AttrXobj;
import org.apache.xmlbeans.impl.store.CdataNode;
import org.apache.xmlbeans.impl.store.CharNode;
import org.apache.xmlbeans.impl.store.CharUtil;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.Cursor;
import org.apache.xmlbeans.impl.store.DocumentXobj;
import org.apache.xmlbeans.impl.store.ElementXobj;
import org.apache.xmlbeans.impl.store.Jsr173;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NodeXobj;
import org.apache.xmlbeans.impl.store.TextNode;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public final class DomImpl {
    static final int ELEMENT = 1;
    static final int ATTR = 2;
    static final int TEXT = 3;
    static final int CDATA = 4;
    static final int ENTITYREF = 5;
    static final int ENTITY = 6;
    static final int PROCINST = 7;
    static final int COMMENT = 8;
    static final int DOCUMENT = 9;
    static final int DOCTYPE = 10;
    static final int DOCFRAG = 11;
    static final int NOTATION = 12;
    public static final NodeList _emptyNodeList = new EmptyNodeList();

    static org.w3c.dom.Node parent(Dom d) {
        return DomImpl.node_getParentNode(d);
    }

    static org.w3c.dom.Node firstChild(Dom d) {
        return DomImpl.node_getFirstChild(d);
    }

    static org.w3c.dom.Node nextSibling(Dom d) {
        return DomImpl.node_getNextSibling(d);
    }

    static org.w3c.dom.Node prevSibling(Dom d) {
        return DomImpl.node_getPreviousSibling(d);
    }

    public static org.w3c.dom.Node append(Dom n, Dom p) {
        return DomImpl.node_insertBefore(p, n, null);
    }

    public static org.w3c.dom.Node insert(Dom n, Dom b) {
        assert (b != null);
        return DomImpl.node_insertBefore((Dom)((Object)DomImpl.parent(b)), n, b);
    }

    public static org.w3c.dom.Node remove(Dom n) {
        org.w3c.dom.Node p = DomImpl.parent(n);
        if (p != null) {
            DomImpl.node_removeChild((Dom)((Object)p), n);
        }
        return (org.w3c.dom.Node)((Object)n);
    }

    static String nodeKindName(int t) {
        switch (t) {
            case 2: {
                return "attribute";
            }
            case 4: {
                return "cdata section";
            }
            case 8: {
                return "comment";
            }
            case 11: {
                return "document fragment";
            }
            case 9: {
                return "document";
            }
            case 10: {
                return "document type";
            }
            case 1: {
                return "element";
            }
            case 6: {
                return "entity";
            }
            case 5: {
                return "entity reference";
            }
            case 12: {
                return "notation";
            }
            case 7: {
                return "processing instruction";
            }
            case 3: {
                return "text";
            }
        }
        throw new RuntimeException("Unknown node type");
    }

    private static String isValidChild(Dom parent, Dom child) {
        int pk = parent.nodeType();
        int ck = child.nodeType();
        switch (pk) {
            case 9: {
                switch (ck) {
                    case 1: {
                        if (DomImpl.document_getDocumentElement(parent) != null) {
                            return "Documents may only have a maximum of one document element";
                        }
                        return null;
                    }
                    case 10: {
                        if (DomImpl.document_getDoctype(parent) != null) {
                            return "Documents may only have a maximum of one document type node";
                        }
                        return null;
                    }
                    case 7: 
                    case 8: {
                        return null;
                    }
                }
                break;
            }
            case 2: {
                if (ck != 3 && ck != 5) break;
                return null;
            }
            case 1: 
            case 5: 
            case 6: 
            case 11: {
                switch (ck) {
                    case 1: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 7: 
                    case 8: {
                        return null;
                    }
                }
                break;
            }
            case 3: 
            case 4: 
            case 7: 
            case 8: 
            case 10: 
            case 12: {
                return DomImpl.nodeKindName(pk) + " nodes may not have any children";
            }
        }
        return DomImpl.nodeKindName(pk) + " nodes may not have " + DomImpl.nodeKindName(ck) + " nodes as children";
    }

    private static void validateNewChild(Dom parent, Dom child) {
        String msg = DomImpl.isValidChild(parent, child);
        if (msg != null) {
            throw new HierarchyRequestErr(msg);
        }
        if (parent == child) {
            throw new HierarchyRequestErr("New child and parent are the same node");
        }
        org.w3c.dom.Node p = (org.w3c.dom.Node)((Object)parent);
        while ((p = DomImpl.parent((Dom)((Object)p))) != null) {
            if (child.nodeType() == 5) {
                throw new NoModificationAllowedErr("Entity reference trees may not be modified");
            }
            if (child != p) continue;
            throw new HierarchyRequestErr("New child is an ancestor node of the parent node");
        }
    }

    private static String validatePrefix(String prefix, String uri, String local, boolean isAttr) {
        if (prefix != null && prefix.contains(":")) {
            throw new NamespaceErr("Invalid prefix - contains ':' character");
        }
        DomImpl.validateNcName(prefix);
        if (prefix == null) {
            prefix = "";
        }
        if (uri == null) {
            uri = "";
        }
        if (prefix.length() > 0 && uri.length() == 0) {
            throw new NamespaceErr("Attempt to give a prefix for no namespace");
        }
        if (prefix.equals("xml") && !uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new NamespaceErr("Invalid prefix - begins with 'xml'");
        }
        if (isAttr) {
            if (prefix.length() > 0) {
                if (local.equals("xmlns")) {
                    throw new NamespaceErr("Invalid namespace - attr is default namespace already");
                }
                if (Locale.beginsWithXml(local)) {
                    throw new NamespaceErr("Invalid namespace - attr prefix begins with 'xml'");
                }
                if (prefix.equals("xmlns") && !uri.equals("http://www.w3.org/2000/xmlns/")) {
                    throw new NamespaceErr("Invalid namespace - uri is not 'http://www.w3.org/2000/xmlns/;");
                }
            } else if (local.equals("xmlns") && !uri.equals("http://www.w3.org/2000/xmlns/")) {
                throw new NamespaceErr("Invalid namespace - uri is not 'http://www.w3.org/2000/xmlns/;");
            }
        } else if (Locale.beginsWithXml(prefix)) {
            throw new NamespaceErr("Invalid prefix - begins with 'xml'");
        }
        return prefix;
    }

    private static void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Name is empty");
        }
        if (!XMLChar.isValidName(name)) {
            throw new InvalidCharacterError("Name has an invalid character");
        }
    }

    private static void validateNcName(String name) {
        if (name != null && name.length() > 0 && !XMLChar.isValidNCName(name)) {
            throw new InvalidCharacterError();
        }
    }

    private static void validateQualifiedName(String name, String uri, boolean isAttr) {
        String local;
        int i;
        assert (name != null);
        if (uri == null) {
            uri = "";
        }
        if ((i = name.indexOf(58)) < 0) {
            local = name;
            DomImpl.validateNcName(local);
            if (isAttr && local.equals("xmlns") && !uri.equals("http://www.w3.org/2000/xmlns/")) {
                throw new NamespaceErr("Default xmlns attribute does not have namespace: http://www.w3.org/2000/xmlns/");
            }
        } else {
            if (i == 0) {
                throw new NamespaceErr("Invalid qualified name, no prefix specified");
            }
            String prefix = name.substring(0, i);
            DomImpl.validateNcName(prefix);
            if (uri.length() == 0) {
                throw new NamespaceErr("Attempt to give a prefix for no namespace");
            }
            local = name.substring(i + 1);
            if (local.indexOf(58) >= 0) {
                throw new NamespaceErr("Invalid qualified name, more than one colon");
            }
            DomImpl.validateNcName(local);
            if (prefix.equals("xml") && !uri.equals("http://www.w3.org/XML/1998/namespace")) {
                throw new NamespaceErr("Invalid prefix - begins with 'xml'");
            }
        }
        if (local.length() == 0) {
            throw new NamespaceErr("Invalid qualified name, no local part specified");
        }
    }

    private static void removeNode(Dom n) {
        CharNode fromNodes;
        assert (n.nodeType() != 3 && n.nodeType() != 4);
        Cur cFrom = n.tempCur();
        cFrom.toEnd();
        if (cFrom.next() && (fromNodes = cFrom.getCharNodes()) != null) {
            cFrom.setCharNodes(null);
            Cur cTo = n.tempCur();
            cTo.setCharNodes(CharNode.appendNodes(cTo.getCharNodes(), fromNodes));
            cTo.release();
        }
        cFrom.release();
        Cur.moveNode((Xobj)((Object)n), null);
    }

    public static Document _domImplementation_createDocument(Locale l, String u, String n, DocumentType t) {
        return DomImpl.syncWrapHelper(l, true, () -> DomImpl.domImplementation_createDocument(l, u, n, t));
    }

    public static Document domImplementation_createDocument(Locale l, String namespaceURI, String qualifiedName, DocumentType doctype) {
        DomImpl.validateQualifiedName(qualifiedName, namespaceURI, false);
        Cur c = l.tempCur();
        c.createDomDocumentRoot();
        Document doc = (Document)((Object)c.getDom());
        c.next();
        c.createElement(l.makeQualifiedQName(namespaceURI, qualifiedName));
        if (doctype != null) {
            throw new RuntimeException("Not impl");
        }
        c.toParent();
        try {
            Locale.autoTypeDocument(c, null, null);
        }
        catch (XmlException e) {
            throw new XmlRuntimeException(e);
        }
        c.release();
        return doc;
    }

    public static boolean _domImplementation_hasFeature(Locale l, String feature, String version) {
        if (feature == null) {
            return false;
        }
        if (version != null && version.length() > 0 && !version.equals("1.0") && !version.equals("2.0")) {
            return false;
        }
        if (feature.equalsIgnoreCase("core")) {
            return true;
        }
        return feature.equalsIgnoreCase("xml");
    }

    public static Element _document_getDocumentElement(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::document_getDocumentElement);
    }

    public static Element document_getDocumentElement(Dom d) {
        org.w3c.dom.Node n = DomImpl.firstChild(d);
        while (n != null) {
            if (((Dom)((Object)n)).nodeType() == 1) {
                return (Element)n;
            }
            n = DomImpl.nextSibling((Dom)((Object)n));
        }
        return null;
    }

    public static DocumentFragment _document_createDocumentFragment(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::document_createDocumentFragment);
    }

    public static DocumentFragment document_createDocumentFragment(Dom d) {
        Cur c = d.locale().tempCur();
        c.createDomDocFragRoot();
        Dom f = c.getDom();
        c.release();
        return (DocumentFragment)((Object)f);
    }

    public static Element _document_createElement(Dom d, String name) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_createElement(p, name));
    }

    public static Element document_createElement(Dom d, String name) {
        DomImpl.validateName(name);
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.createElement(l.makeQualifiedQName("", name));
        ElementXobj e = (ElementXobj)c.getDom();
        c.release();
        e._canHavePrefixUri = false;
        return e;
    }

    public static Element _document_createElementNS(Dom d, String uri, String qname) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_createElementNS(p, uri, qname));
    }

    public static Element document_createElementNS(Dom d, String uri, String qname) {
        DomImpl.validateQualifiedName(qname, uri, false);
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.createElement(l.makeQualifiedQName(uri, qname));
        Dom e = c.getDom();
        c.release();
        return (Element)((Object)e);
    }

    public static Attr _document_createAttribute(Dom d, String name) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_createAttribute(p, name));
    }

    public static Attr document_createAttribute(Dom d, String name) {
        DomImpl.validateName(name);
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.createAttr(l.makeQualifiedQName("", name));
        AttrXobj e = (AttrXobj)c.getDom();
        c.release();
        e._canHavePrefixUri = false;
        return e;
    }

    public static Attr _document_createAttributeNS(Dom d, String uri, String qname) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_createAttributeNS(p, uri, qname));
    }

    public static Attr document_createAttributeNS(Dom d, String uri, String qname) {
        DomImpl.validateQualifiedName(qname, uri, true);
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.createAttr(l.makeQualifiedQName(uri, qname));
        Dom e = c.getDom();
        c.release();
        return (Attr)((Object)e);
    }

    public static Comment _document_createComment(Dom d, String data) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_createComment(p, data));
    }

    public static Comment document_createComment(Dom d, String data) {
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.createComment();
        Dom comment = c.getDom();
        if (data != null) {
            c.next();
            c.insertString(data);
        }
        c.release();
        return (Comment)((Object)comment);
    }

    public static ProcessingInstruction _document_createProcessingInstruction(Dom d, String target, String data) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_createProcessingInstruction(p, target, data));
    }

    public static ProcessingInstruction document_createProcessingInstruction(Dom d, String target, String data) {
        if (target == null) {
            throw new IllegalArgumentException("Target is null");
        }
        if (target.length() == 0) {
            throw new IllegalArgumentException("Target is empty");
        }
        if (!XMLChar.isValidName(target)) {
            throw new InvalidCharacterError("Target has an invalid character");
        }
        if (Locale.beginsWithXml(target) && target.length() == 3) {
            throw new InvalidCharacterError("Invalid target - is 'xml'");
        }
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.createProcinst(target);
        Dom pi = c.getDom();
        if (data != null) {
            c.next();
            c.insertString(data);
        }
        c.release();
        return (ProcessingInstruction)((Object)pi);
    }

    public static CDATASection _document_createCDATASection(Dom d, String data) {
        return DomImpl.document_createCDATASection(d, data);
    }

    public static CDATASection document_createCDATASection(Dom d, String data) {
        CdataNode t = d.locale().createCdataNode();
        if (data == null) {
            data = "";
        }
        t.setChars(data, 0, data.length());
        return t;
    }

    public static Text _document_createTextNode(Dom d, String data) {
        return DomImpl.document_createTextNode(d, data);
    }

    public static Text document_createTextNode(Dom d, String data) {
        TextNode t = d.locale().createTextNode();
        if (data == null) {
            data = "";
        }
        t.setChars(data, 0, data.length());
        return t;
    }

    public static EntityReference _document_createEntityReference(Dom d, String name) {
        throw new RuntimeException("Not implemented");
    }

    public static Element _document_getElementById(Dom d, String elementId) {
        throw new RuntimeException("Not implemented");
    }

    public static NodeList _document_getElementsByTagName(Dom d, String name) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_getElementsByTagName(p, name));
    }

    public static NodeList document_getElementsByTagName(Dom d, String name) {
        return new ElementsByTagNameNodeList(d, name);
    }

    public static NodeList _document_getElementsByTagNameNS(Dom d, String uri, String local) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_getElementsByTagNameNS(p, uri, local));
    }

    public static NodeList document_getElementsByTagNameNS(Dom d, String uri, String local) {
        return new ElementsByTagNameNSNodeList(d, uri, local);
    }

    public static DOMImplementation _document_getImplementation(Dom d) {
        return d.locale();
    }

    public static org.w3c.dom.Node _document_importNode(Dom d, org.w3c.dom.Node n, boolean deep) {
        return DomImpl.syncWrap(d, p -> DomImpl.document_importNode(p, n, deep));
    }

    public static org.w3c.dom.Node document_importNode(Dom d, org.w3c.dom.Node n, boolean deep) {
        org.w3c.dom.Node i;
        if (n == null) {
            return null;
        }
        boolean copyChildren = false;
        switch (n.getNodeType()) {
            case 9: {
                throw new NotSupportedError("Document nodes may not be imported");
            }
            case 10: {
                throw new NotSupportedError("Document type nodes may not be imported");
            }
            case 1: {
                String uri;
                String prefix;
                String local = n.getLocalName();
                if (local == null || local.length() == 0) {
                    i = DomImpl.document_createElement(d, n.getNodeName());
                } else {
                    prefix = n.getPrefix();
                    String name = prefix == null || prefix.length() == 0 ? local : prefix + ":" + local;
                    uri = n.getNamespaceURI();
                    i = uri == null || uri.length() == 0 ? DomImpl.document_createElement(d, name) : DomImpl.document_createElementNS(d, uri, name);
                }
                NamedNodeMap attrs = n.getAttributes();
                for (int a = 0; a < attrs.getLength(); ++a) {
                    DomImpl.attributes_setNamedItem((Dom)((Object)i), (Dom)((Object)DomImpl.document_importNode(d, attrs.item(a), true)));
                }
                copyChildren = deep;
                break;
            }
            case 2: {
                String uri;
                String prefix;
                String local = n.getLocalName();
                if (local == null || local.length() == 0) {
                    i = DomImpl.document_createAttribute(d, n.getNodeName());
                } else {
                    prefix = n.getPrefix();
                    String name = prefix == null || prefix.length() == 0 ? local : prefix + ":" + local;
                    uri = n.getNamespaceURI();
                    i = uri == null || uri.length() == 0 ? DomImpl.document_createAttribute(d, name) : DomImpl.document_createAttributeNS(d, uri, name);
                }
                copyChildren = true;
                break;
            }
            case 11: {
                i = DomImpl.document_createDocumentFragment(d);
                copyChildren = deep;
                break;
            }
            case 7: {
                i = DomImpl.document_createProcessingInstruction(d, n.getNodeName(), n.getNodeValue());
                break;
            }
            case 8: {
                i = DomImpl.document_createComment(d, n.getNodeValue());
                break;
            }
            case 3: {
                i = DomImpl.document_createTextNode(d, n.getNodeValue());
                break;
            }
            case 4: {
                i = DomImpl.document_createCDATASection(d, n.getNodeValue());
                break;
            }
            case 5: 
            case 6: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        if (copyChildren) {
            NodeList children = n.getChildNodes();
            for (int c = 0; c < children.getLength(); ++c) {
                DomImpl.node_insertBefore((Dom)((Object)i), (Dom)((Object)DomImpl.document_importNode(d, children.item(c), true)), null);
            }
        }
        return i;
    }

    public static DocumentType _document_getDoctype(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::document_getDoctype);
    }

    public static DocumentType document_getDoctype(Dom d) {
        return null;
    }

    public static Document _node_getOwnerDocument(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::node_getOwnerDocument);
    }

    public static Document node_getOwnerDocument(Dom n) {
        if (n.nodeType() == 9) {
            return null;
        }
        Locale l = n.locale();
        if (l._ownerDoc == null) {
            Cur c = l.tempCur();
            c.createDomDocumentRoot();
            l._ownerDoc = c.getDom();
            c.release();
        }
        return (Document)((Object)l._ownerDoc);
    }

    public static org.w3c.dom.Node _node_getParentNode(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::node_getParentNode);
    }

    public static org.w3c.dom.Node node_getParentNode(Dom n) {
        Cur c = null;
        switch (n.nodeType()) {
            case 2: 
            case 9: 
            case 11: {
                break;
            }
            case 1: 
            case 7: 
            case 8: {
                c = n.tempCur();
                if (c.toParentRaw()) break;
                c.release();
                c = null;
                break;
            }
            case 3: 
            case 4: {
                c = n.tempCur();
                if (c == null) break;
                c.toParent();
                break;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        if (c == null) {
            return null;
        }
        Dom d = c.getDom();
        c.release();
        return (org.w3c.dom.Node)((Object)d);
    }

    public static org.w3c.dom.Node _node_getFirstChild(Dom n) {
        assert (n instanceof Xobj);
        Xobj node = (Xobj)((Object)n);
        if (!node.isVacant()) {
            if (node.isFirstChildPtrDomUsable()) {
                return (org.w3c.dom.Node)((Object)node._firstChild);
            }
            Xobj lastAttr = node.lastAttr();
            if (lastAttr != null && lastAttr.isNextSiblingPtrDomUsable()) {
                return (NodeXobj)lastAttr._nextSibling;
            }
            if (node.isExistingCharNodesValueUsable()) {
                return node._charNodesValue;
            }
        }
        return DomImpl.syncWrapNoEnter(n, DomImpl::node_getFirstChild);
    }

    public static org.w3c.dom.Node node_getFirstChild(Dom n) {
        Object fc = null;
        switch (n.nodeType()) {
            case 3: 
            case 4: 
            case 7: 
            case 8: {
                break;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
            case 1: 
            case 2: 
            case 9: 
            case 11: {
                Xobj node = (Xobj)((Object)n);
                node.ensureOccupancy();
                if (node.isFirstChildPtrDomUsable()) {
                    return (NodeXobj)node._firstChild;
                }
                Xobj lastAttr = node.lastAttr();
                if (lastAttr != null) {
                    if (lastAttr.isNextSiblingPtrDomUsable()) {
                        return (NodeXobj)lastAttr._nextSibling;
                    }
                    if (lastAttr.isCharNodesAfterUsable()) {
                        return lastAttr._charNodesAfter;
                    }
                }
                if (!node.isCharNodesValueUsable()) break;
                return node._charNodesValue;
            }
        }
        return fc;
    }

    public static org.w3c.dom.Node _node_getLastChild(Dom n) {
        return DomImpl.syncWrap(n, DomImpl::node_getLastChild);
    }

    public static org.w3c.dom.Node node_getLastChild(Dom n) {
        CharNode nodes;
        switch (n.nodeType()) {
            case 3: 
            case 4: 
            case 7: 
            case 8: {
                return null;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
        }
        Dom lc = null;
        Cur c = n.tempCur();
        if (c.toLastChild()) {
            lc = c.getDom();
            c.skip();
            nodes = c.getCharNodes();
            if (nodes != null) {
                lc = null;
            }
        } else {
            c.next();
            nodes = c.getCharNodes();
        }
        if (lc == null && nodes != null) {
            while (nodes._next != null) {
                nodes = nodes._next;
            }
            lc = nodes;
        }
        c.release();
        return (org.w3c.dom.Node)((Object)lc);
    }

    public static org.w3c.dom.Node _node_getNextSibling(Dom n) {
        return DomImpl.syncWrapNoEnter(n, DomImpl::node_getNextSibling);
    }

    public static org.w3c.dom.Node node_getNextSibling(Dom n) {
        Dom ns = null;
        switch (n.nodeType()) {
            case 2: 
            case 9: 
            case 11: {
                break;
            }
            case 3: 
            case 4: {
                CharNode cn = (CharNode)n;
                if (!(cn.getObject() instanceof Xobj)) {
                    return null;
                }
                Xobj src = (Xobj)cn.getObject();
                src._charNodesAfter = Cur.updateCharNodes(src._locale, src, src._charNodesAfter, src._cchAfter);
                src._charNodesValue = Cur.updateCharNodes(src._locale, src, src._charNodesValue, src._cchValue);
                if (cn._next != null) {
                    ns = cn._next;
                    break;
                }
                boolean isThisNodeAfterText = cn.isNodeAftertext();
                if (isThisNodeAfterText) {
                    ns = (NodeXobj)src._nextSibling;
                    break;
                }
                ns = (NodeXobj)src._firstChild;
                break;
            }
            case 1: 
            case 7: 
            case 8: {
                assert (n instanceof Xobj) : "PI, Comments and Elements always backed up by Xobj";
                Xobj node = (Xobj)((Object)n);
                node.ensureOccupancy();
                if (node.isNextSiblingPtrDomUsable()) {
                    return (NodeXobj)node._nextSibling;
                }
                if (!node.isCharNodesAfterUsable()) break;
                return node._charNodesAfter;
            }
            case 5: 
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not implemented");
            }
        }
        return ns;
    }

    public static org.w3c.dom.Node _node_getPreviousSibling(Dom n) {
        return DomImpl.syncWrapNoEnter(n, DomImpl::node_getPreviousSibling);
    }

    public static org.w3c.dom.Node node_getPreviousSibling(Dom n) {
        org.w3c.dom.Node prev;
        switch (n.nodeType()) {
            case 3: 
            case 4: {
                assert (n instanceof CharNode) : "Text/CData should be a CharNode";
                Dom node = (CharNode)n;
                if (!(((CharNode)node).getObject() instanceof Xobj)) {
                    return null;
                }
                NodeXobj src = (NodeXobj)((CharNode)node).getObject();
                src.ensureOccupancy();
                boolean isThisNodeAfterText = ((CharNode)node).isNodeAftertext();
                prev = ((CharNode)node)._prev;
                if (prev != null) break;
                prev = isThisNodeAfterText ? src : src._charNodesValue;
                break;
            }
            default: {
                assert (n instanceof NodeXobj);
                Dom node = (NodeXobj)n;
                prev = (NodeXobj)((NodeXobj)node)._prevSibling;
                if (prev != null && (node instanceof AttrXobj || !(prev instanceof AttrXobj)) || ((NodeXobj)node)._parent == null) break;
                prev = DomImpl.node_getFirstChild((Dom)((Object)((NodeXobj)node)._parent));
            }
        }
        org.w3c.dom.Node temp = prev;
        while (temp != null && (temp = DomImpl.node_getNextSibling(temp)) != n) {
            prev = temp;
        }
        return prev;
    }

    public static boolean _node_hasAttributes(Dom n) {
        return DomImpl.syncWrap(n, DomImpl::node_hasAttributes);
    }

    public static boolean node_hasAttributes(Dom n) {
        boolean hasAttrs = false;
        if (n.nodeType() == 1) {
            Cur c = n.tempCur();
            hasAttrs = c.hasAttrs();
            c.release();
        }
        return hasAttrs;
    }

    public static boolean _node_isSupported(Dom n, String feature, String version) {
        return DomImpl._domImplementation_hasFeature(n.locale(), feature, version);
    }

    public static void _node_normalize(Dom n) {
        DomImpl.syncWrapVoid(n, DomImpl::node_normalize);
    }

    public static void node_normalize(Dom n) {
        switch (n.nodeType()) {
            case 3: 
            case 4: 
            case 7: 
            case 8: {
                return;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
        }
        Cur c = n.tempCur();
        c.push();
        do {
            c.nextWithAttrs();
            CharNode cn = c.getCharNodes();
            if (cn == null) continue;
            if (!c.isText()) {
                while (cn != null) {
                    cn.setChars(null, 0, 0);
                    cn = CharNode.remove(cn, cn);
                }
            } else if (cn._next != null) {
                while (cn._next != null) {
                    cn.setChars(null, 0, 0);
                    cn = CharNode.remove(cn, cn._next);
                }
                cn._cch = Integer.MAX_VALUE;
            }
            c.setCharNodes(cn);
        } while (!c.isAtEndOfLastPush());
        c.release();
        n.locale().invalidateDomCaches(n);
    }

    public static boolean _node_hasChildNodes(Dom n) {
        return n instanceof Xobj && DomImpl._node_getFirstChild(n) != null;
    }

    public static org.w3c.dom.Node _node_appendChild(Dom p, org.w3c.dom.Node newChild) {
        return DomImpl._node_insertBefore(p, newChild, null);
    }

    public static org.w3c.dom.Node _node_replaceChild(Dom p, org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) {
        Dom oc;
        Dom nc;
        Locale l = p.locale();
        if (newChild == null) {
            throw new IllegalArgumentException("Child to add is null");
        }
        if (oldChild == null) {
            throw new NotFoundErr("Child to replace is null");
        }
        if (!(newChild instanceof Dom) || (nc = (Dom)((Object)newChild)).locale() != l) {
            throw new WrongDocumentErr("Child to add is from another document");
        }
        if (!(oldChild instanceof Dom) || (oc = (Dom)((Object)oldChild)).locale() != l) {
            throw new WrongDocumentErr("Child to replace is from another document");
        }
        Dom oc2 = oc;
        return DomImpl.syncWrap(p, x -> DomImpl.node_replaceChild(x, nc, oc2));
    }

    public static org.w3c.dom.Node node_replaceChild(Dom p, Dom newChild, Dom oldChild) {
        org.w3c.dom.Node nextNode = DomImpl.node_getNextSibling(oldChild);
        DomImpl.node_removeChild(p, oldChild);
        try {
            DomImpl.node_insertBefore(p, newChild, (Dom)((Object)nextNode));
        }
        catch (DOMException e) {
            DomImpl.node_insertBefore(p, oldChild, (Dom)((Object)nextNode));
            throw e;
        }
        return (org.w3c.dom.Node)((Object)oldChild);
    }

    public static org.w3c.dom.Node _node_insertBefore(Dom p, org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) {
        Dom nc;
        Locale l = p.locale();
        if (newChild == null) {
            throw new IllegalArgumentException("Child to add is null");
        }
        if (!(newChild instanceof Dom) || (nc = (Dom)((Object)newChild)).locale() != l) {
            throw new WrongDocumentErr("Child to add is from another document");
        }
        Dom rc = null;
        if (!(refChild == null || refChild instanceof Dom && (rc = (Dom)((Object)refChild)).locale() == l)) {
            throw new WrongDocumentErr("Reference child is from another document");
        }
        Dom rc2 = rc;
        return DomImpl.syncWrap(p, x -> DomImpl.node_insertBefore(x, nc, rc2));
    }

    public static org.w3c.dom.Node node_insertBefore(Dom p, Dom nc, Dom rc) {
        assert (nc != null);
        if (nc == rc) {
            return (org.w3c.dom.Node)((Object)nc);
        }
        if (rc != null && DomImpl.parent(rc) != p) {
            throw new NotFoundErr("RefChild is not a child of this node");
        }
        int nck = nc.nodeType();
        if (nck == 11) {
            org.w3c.dom.Node c = DomImpl.firstChild(nc);
            while (c != null) {
                DomImpl.validateNewChild(p, (Dom)((Object)c));
                c = DomImpl.nextSibling((Dom)((Object)c));
            }
            c = DomImpl.firstChild(nc);
            while (c != null) {
                org.w3c.dom.Node n = DomImpl.nextSibling((Dom)((Object)c));
                if (rc == null) {
                    DomImpl.append((Dom)((Object)c), p);
                } else {
                    DomImpl.insert((Dom)((Object)c), rc);
                }
                c = n;
            }
            return (org.w3c.dom.Node)((Object)nc);
        }
        DomImpl.validateNewChild(p, nc);
        DomImpl.remove(nc);
        int pk = p.nodeType();
        assert (pk == 2 || pk == 11 || pk == 9 || pk == 1);
        switch (nck) {
            case 1: 
            case 7: 
            case 8: {
                if (rc == null) {
                    Cur cTo = p.tempCur();
                    cTo.toEnd();
                    Cur.moveNode((Xobj)((Object)nc), cTo);
                    cTo.release();
                    break;
                }
                int rck = rc.nodeType();
                if (rck == 3 || rck == 4) {
                    ArrayList<Dom> charNodes = new ArrayList<Dom>();
                    Dom rc2 = rc;
                    while (rc2 != null && (rc2.nodeType() == 3 || rc2.nodeType() == 4)) {
                        org.w3c.dom.Node next = DomImpl.nextSibling(rc2);
                        charNodes.add((Dom)((Object)DomImpl.remove(rc2)));
                        rc2 = (Dom)((Object)next);
                    }
                    if (rc2 == null) {
                        DomImpl.append(nc, p);
                    } else {
                        DomImpl.insert(nc, rc2);
                    }
                    rc2 = (Dom)((Object)DomImpl.nextSibling(nc));
                    for (Object e : charNodes) {
                        Dom n = (Dom)e;
                        if (rc2 == null) {
                            DomImpl.append(n, p);
                            continue;
                        }
                        DomImpl.insert(n, rc2);
                    }
                    break;
                }
                if (rck == 5) {
                    throw new RuntimeException("Not implemented");
                }
                assert (rck == 1 || rck == 7 || rck == 8);
                Cur cTo = rc.tempCur();
                Cur.moveNode((Xobj)((Object)nc), cTo);
                cTo.release();
                break;
            }
            case 3: 
            case 4: {
                CharNode n = (CharNode)nc;
                assert (n._prev == null && n._next == null);
                CharNode refCharNode = null;
                Cur c = p.tempCur();
                if (rc == null) {
                    c.toEnd();
                } else {
                    int rck = rc.nodeType();
                    if (rck == 3 || rck == 4) {
                        refCharNode = (CharNode)rc;
                        c.moveToCharNode(refCharNode);
                    } else {
                        if (rck == 5) {
                            throw new RuntimeException("Not implemented");
                        }
                        c.moveToDom(rc);
                    }
                }
                CharNode nodes = c.getCharNodes();
                nodes = CharNode.insertNode(nodes, n, refCharNode);
                c.insertChars(n.getObject(), n._off, n._cch);
                c.setCharNodes(nodes);
                c.release();
                break;
            }
            case 5: {
                throw new RuntimeException("Not implemented");
            }
            case 10: {
                throw new RuntimeException("Not implemented");
            }
            default: {
                throw new RuntimeException("Unexpected child node type");
            }
        }
        return (org.w3c.dom.Node)((Object)nc);
    }

    public static org.w3c.dom.Node _node_removeChild(Dom p, org.w3c.dom.Node child) {
        Dom c;
        Locale l = p.locale();
        if (child == null) {
            throw new NotFoundErr("Child to remove is null");
        }
        if (!(child instanceof Dom) || (c = (Dom)((Object)child)).locale() != l) {
            throw new WrongDocumentErr("Child to remove is from another document");
        }
        return DomImpl.syncWrap(p, x -> DomImpl.node_removeChild(x, c));
    }

    public static org.w3c.dom.Node node_removeChild(Dom parent, Dom child) {
        if (DomImpl.parent(child) != parent) {
            throw new NotFoundErr("Child to remove is not a child of given parent");
        }
        switch (child.nodeType()) {
            case 2: 
            case 9: 
            case 11: {
                throw new IllegalStateException();
            }
            case 1: 
            case 7: 
            case 8: {
                DomImpl.removeNode(child);
                break;
            }
            case 3: 
            case 4: {
                Cur c = child.tempCur();
                CharNode nodes = c.getCharNodes();
                CharNode cn = (CharNode)child;
                assert (cn.getDom() != null);
                cn.setChars(c.moveChars(null, cn._cch), c._offSrc, c._cchSrc);
                c.setCharNodes(CharNode.remove(nodes, cn));
                c.release();
                break;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        return (org.w3c.dom.Node)((Object)child);
    }

    public static org.w3c.dom.Node _node_cloneNode(Dom n, boolean deep) {
        return DomImpl.syncWrap(n, p -> DomImpl.node_cloneNode(p, deep));
    }

    public static org.w3c.dom.Node node_cloneNode(Dom n, boolean deep) {
        Locale l = n.locale();
        Dom clone = null;
        if (!deep) {
            Cur shallow = null;
            switch (n.nodeType()) {
                case 9: {
                    shallow = l.tempCur();
                    shallow.createDomDocumentRoot();
                    break;
                }
                case 11: {
                    shallow = l.tempCur();
                    shallow.createDomDocFragRoot();
                    break;
                }
                case 1: {
                    shallow = l.tempCur();
                    shallow.createElement(n.getQName());
                    Element elem = (Element)((Object)shallow.getDom());
                    NamedNodeMap attrs = ((Element)((Object)n)).getAttributes();
                    for (int i = 0; i < attrs.getLength(); ++i) {
                        elem.setAttributeNodeNS((Attr)attrs.item(i).cloneNode(true));
                    }
                    break;
                }
                case 2: {
                    shallow = l.tempCur();
                    shallow.createAttr(n.getQName());
                    break;
                }
            }
            if (shallow != null) {
                clone = shallow.getDom();
                shallow.release();
            }
        }
        if (clone == null) {
            switch (n.nodeType()) {
                case 1: 
                case 2: 
                case 7: 
                case 8: 
                case 9: 
                case 11: {
                    Cur cClone = l.tempCur();
                    Cur cSrc = n.tempCur();
                    cSrc.copyNode(cClone);
                    clone = cClone.getDom();
                    cClone.release();
                    cSrc.release();
                    break;
                }
                case 3: 
                case 4: {
                    Cur c = n.tempCur();
                    TextNode cn = n.nodeType() == 3 ? l.createTextNode() : l.createCdataNode();
                    cn.setChars(c.getChars(((CharNode)n)._cch), c._offSrc, c._cchSrc);
                    clone = cn;
                    c.release();
                    break;
                }
                case 5: 
                case 6: 
                case 10: 
                case 12: {
                    throw new RuntimeException("Not impl");
                }
                default: {
                    throw new RuntimeException("Unknown kind");
                }
            }
        }
        return (org.w3c.dom.Node)((Object)clone);
    }

    public static String _node_getLocalName(Dom n) {
        if (!n.nodeCanHavePrefixUri()) {
            return null;
        }
        QName name = n.getQName();
        return name == null ? "" : name.getLocalPart();
    }

    public static String _node_getNamespaceURI(Dom n) {
        if (!n.nodeCanHavePrefixUri()) {
            return null;
        }
        QName name = n.getQName();
        return name == null ? "" : name.getNamespaceURI();
    }

    public static void _node_setPrefix(Dom n, String prefix) {
        DomImpl.syncWrapVoid(n, p -> DomImpl.node_setPrefix(p, prefix));
    }

    public static void node_setPrefix(Dom n, String prefix) {
        if (n.nodeType() == 1 || n.nodeType() == 2) {
            Cur c = n.tempCur();
            QName name = c.getName();
            String uri = name.getNamespaceURI();
            String local = name.getLocalPart();
            prefix = DomImpl.validatePrefix(prefix, uri, local, n.nodeType() == 2);
            c.setName(n.locale().makeQName(uri, local, prefix));
            c.release();
        } else {
            DomImpl.validatePrefix(prefix, "", "", false);
        }
    }

    public static String _node_getPrefix(Dom n) {
        if (!n.nodeCanHavePrefixUri()) {
            return null;
        }
        QName name = n.getQName();
        return name == null ? "" : name.getPrefix();
    }

    public static String _node_getNodeName(Dom n) {
        switch (n.nodeType()) {
            case 4: {
                return "#cdata-section";
            }
            case 8: {
                return "#comment";
            }
            case 11: {
                return "#document-fragment";
            }
            case 9: {
                return "#document";
            }
            case 7: {
                return n.getQName().getLocalPart();
            }
            case 3: {
                return "#text";
            }
            case 1: 
            case 2: {
                QName name = n.getQName();
                String prefix = name.getPrefix();
                return prefix.length() == 0 ? name.getLocalPart() : prefix + ":" + name.getLocalPart();
            }
            case 5: 
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
        }
        throw new RuntimeException("Unknown node type");
    }

    public static short _node_getNodeType(Dom n) {
        return (short)n.nodeType();
    }

    public static void _node_setNodeValue(Dom n, String nodeValue) {
        DomImpl.syncWrapVoid(n, p -> DomImpl.node_setNodeValue(p, nodeValue));
    }

    public static void node_setNodeValue(Dom n, String nodeValue) {
        if (nodeValue == null) {
            nodeValue = "";
        }
        switch (n.nodeType()) {
            case 3: 
            case 4: {
                CharNode cn = (CharNode)n;
                Cur c = cn.tempCur();
                if (c != null) {
                    c.moveChars(null, cn._cch);
                    cn._cch = nodeValue.length();
                    c.insertString(nodeValue);
                    c.release();
                    break;
                }
                cn.setChars(nodeValue, 0, nodeValue.length());
                break;
            }
            case 2: {
                NodeList children = ((org.w3c.dom.Node)((Object)n)).getChildNodes();
                while (children.getLength() > 1) {
                    DomImpl.node_removeChild(n, (Dom)((Object)children.item(1)));
                }
                if (children.getLength() == 0) {
                    TextNode tn = n.locale().createTextNode();
                    tn.setChars(nodeValue, 0, nodeValue.length());
                    DomImpl.node_insertBefore(n, tn, null);
                } else {
                    assert (children.getLength() == 1);
                    children.item(0).setNodeValue(nodeValue);
                }
                if (!((AttrXobj)n).isId()) break;
                Document d = DomImpl.node_getOwnerDocument(n);
                String val = DomImpl.node_getNodeValue(n);
                if (!(d instanceof DocumentXobj)) break;
                DocumentXobj dox = (DocumentXobj)d;
                dox.removeIdElement(val);
                dox.addIdElement(nodeValue, (Dom)((Object)DomImpl.attr_getOwnerElement(n)));
                break;
            }
            case 7: 
            case 8: {
                Cur c = n.tempCur();
                c.next();
                c.getChars(-1);
                c.moveChars(null, c._cchSrc);
                c.insertString(nodeValue);
                c.release();
                break;
            }
        }
    }

    public static String _node_getNodeValue(Dom n) {
        return DomImpl.syncWrapNoEnter(n, DomImpl::node_getNodeValue);
    }

    public static String node_getNodeValue(Dom n) {
        String s = null;
        switch (n.nodeType()) {
            case 2: 
            case 7: 
            case 8: {
                s = ((Xobj)((Object)n)).getValueAsString();
                break;
            }
            case 3: 
            case 4: {
                assert (n instanceof CharNode) : "Text/CData should be a CharNode";
                CharNode node = (CharNode)n;
                if (!(node.getObject() instanceof Xobj)) {
                    s = CharUtil.getString(node.getObject(), node._off, node._cch);
                    break;
                }
                Xobj src = (Xobj)node.getObject();
                src.ensureOccupancy();
                boolean isThisNodeAfterText = node.isNodeAftertext();
                if (isThisNodeAfterText) {
                    src._charNodesAfter = Cur.updateCharNodes(src._locale, src, src._charNodesAfter, src._cchAfter);
                    s = src.getCharsAfterAsString(node._off, node._cch);
                    break;
                }
                src._charNodesValue = Cur.updateCharNodes(src._locale, src, src._charNodesValue, src._cchValue);
                s = src.getCharsValueAsString(node._off, node._cch);
                break;
            }
        }
        return s;
    }

    public static Object _node_getUserData(Dom n, String key) {
        throw new DomLevel3NotImplemented();
    }

    public static Object _node_setUserData(Dom n, String key, Object data, UserDataHandler handler) {
        throw new DomLevel3NotImplemented();
    }

    public static Object _node_getFeature(Dom n, String feature, String version) {
        throw new DomLevel3NotImplemented();
    }

    public static boolean _node_isEqualNode(Dom n, org.w3c.dom.Node arg) {
        throw new DomLevel3NotImplemented();
    }

    public static boolean _node_isSameNode(Dom n, org.w3c.dom.Node arg) {
        boolean ret;
        if (n instanceof CharNode) {
            ret = n.equals(arg);
        } else if (n instanceof NodeXobj) {
            ret = ((NodeXobj)n).getDom().equals(arg);
        } else {
            throw new DomLevel3NotImplemented();
        }
        return ret;
    }

    public static String _node_lookupNamespaceURI(Dom n, String prefix) {
        throw new DomLevel3NotImplemented();
    }

    public static boolean _node_isDefaultNamespace(Dom n, String namespaceURI) {
        throw new DomLevel3NotImplemented();
    }

    public static String _node_lookupPrefix(Dom n, String namespaceURI) {
        throw new DomLevel3NotImplemented();
    }

    public static void _node_setTextContent(Dom n, String textContent) {
        throw new DomLevel3NotImplemented();
    }

    public static String _node_getTextContent(Dom n) {
        throw new DomLevel3NotImplemented();
    }

    public static short _node_compareDocumentPosition(Dom n, org.w3c.dom.Node other) {
        org.w3c.dom.Node oAnc;
        org.w3c.dom.Node nAnc;
        boolean isEqual;
        if (!(n instanceof org.w3c.dom.Node)) {
            return 32;
        }
        Iterator<org.w3c.dom.Node> nAncIter = DomImpl.ancestorAndSelf((org.w3c.dom.Node)((Object)n)).iterator();
        Iterator<org.w3c.dom.Node> oAncIter = DomImpl.ancestorAndSelf(other).iterator();
        boolean isFirst = true;
        do {
            nAnc = nAncIter.next();
            oAnc = oAncIter.next();
            isEqual = Objects.equals(nAnc, oAnc);
            if (isFirst && !isEqual) {
                return 1;
            }
            isFirst = false;
        } while (isEqual && nAncIter.hasNext() && oAncIter.hasNext());
        if (isEqual) {
            return (short)(nAncIter.hasNext() ? 10 : (oAncIter.hasNext() ? 20 : 32));
        }
        org.w3c.dom.Node prevSib = nAnc;
        while ((prevSib = prevSib.getPreviousSibling()) != null) {
            if (!Objects.equals(prevSib, oAnc)) continue;
            return 2;
        }
        return 4;
    }

    private static List<org.w3c.dom.Node> ancestorAndSelf(org.w3c.dom.Node node) {
        LinkedList<org.w3c.dom.Node> nodes = new LinkedList<org.w3c.dom.Node>();
        org.w3c.dom.Node n = node;
        do {
            nodes.addFirst(n);
        } while ((n = n.getParentNode()) != null);
        return nodes;
    }

    public static String _node_getBaseURI(Dom n) {
        throw new DomLevel3NotImplemented();
    }

    public static org.w3c.dom.Node _childNodes_item(Dom n, int i) {
        return i == 0 ? DomImpl._node_getFirstChild(n) : DomImpl.syncWrapNoEnter(n, p -> DomImpl.childNodes_item(p, i));
    }

    public static org.w3c.dom.Node childNodes_item(Dom n, int i) {
        if (i < 0) {
            return null;
        }
        switch (n.nodeType()) {
            case 3: 
            case 4: 
            case 7: 
            case 8: {
                return null;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
        }
        if (i == 0) {
            return DomImpl.node_getFirstChild(n);
        }
        return (org.w3c.dom.Node)((Object)n.locale().findDomNthChild(n, i));
    }

    public static int _childNodes_getLength(Dom n) {
        int count;
        assert (n instanceof Xobj);
        Xobj node = (Xobj)((Object)n);
        if (!node.isVacant() && (count = node.getDomZeroOneChildren()) < 2) {
            return count;
        }
        return DomImpl.syncWrapNoEnter(n, DomImpl::childNodes_getLength);
    }

    public static int childNodes_getLength(Dom n) {
        switch (n.nodeType()) {
            case 3: 
            case 4: 
            case 7: 
            case 8: {
                return 0;
            }
            case 5: {
                throw new RuntimeException("Not impl");
            }
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
        }
        assert (n instanceof Xobj);
        Xobj node = (Xobj)((Object)n);
        node.ensureOccupancy();
        int count = node.getDomZeroOneChildren();
        if (count < 2) {
            return count;
        }
        return n.locale().domLength(n);
    }

    public static String _element_getTagName(Dom e) {
        return DomImpl._node_getNodeName(e);
    }

    public static Attr _element_getAttributeNode(Dom e, String name) {
        return (Attr)DomImpl._attributes_getNamedItem(e, name);
    }

    public static Attr _element_getAttributeNodeNS(Dom e, String uri, String local) {
        return (Attr)DomImpl._attributes_getNamedItemNS(e, uri, local);
    }

    public static Attr _element_setAttributeNode(Dom e, Attr newAttr) {
        return (Attr)DomImpl._attributes_setNamedItem(e, newAttr);
    }

    public static Attr _element_setAttributeNodeNS(Dom e, Attr newAttr) {
        return (Attr)DomImpl._attributes_setNamedItemNS(e, newAttr);
    }

    public static String _element_getAttribute(Dom e, String name) {
        org.w3c.dom.Node a = DomImpl._attributes_getNamedItem(e, name);
        return a == null ? "" : a.getNodeValue();
    }

    public static String _element_getAttributeNS(Dom e, String uri, String local) {
        org.w3c.dom.Node a = DomImpl._attributes_getNamedItemNS(e, uri, local);
        return a == null ? "" : a.getNodeValue();
    }

    public static boolean _element_hasAttribute(Dom e, String name) {
        return DomImpl._attributes_getNamedItem(e, name) != null;
    }

    public static boolean _element_hasAttributeNS(Dom e, String uri, String local) {
        return DomImpl._attributes_getNamedItemNS(e, uri, local) != null;
    }

    public static void _element_removeAttribute(Dom e, String name) {
        try {
            DomImpl._attributes_removeNamedItem(e, name);
        }
        catch (NotFoundErr notFoundErr) {
            // empty catch block
        }
    }

    public static void _element_removeAttributeNS(Dom e, String uri, String local) {
        try {
            DomImpl._attributes_removeNamedItemNS(e, uri, local);
        }
        catch (NotFoundErr notFoundErr) {
            // empty catch block
        }
    }

    public static Attr _element_removeAttributeNode(Dom e, Attr oldAttr) {
        if (oldAttr == null) {
            throw new NotFoundErr("Attribute to remove is null");
        }
        if (oldAttr.getOwnerElement() != e) {
            throw new NotFoundErr("Attribute to remove does not belong to this element");
        }
        return (Attr)DomImpl._attributes_removeNamedItem(e, oldAttr.getNodeName());
    }

    public static void _element_setAttribute(Dom e, String name, String value) {
        DomImpl.syncWrapVoid(e, p -> DomImpl.element_setAttribute(p, name, value));
    }

    public static void element_setAttribute(Dom e, String name, String value) {
        org.w3c.dom.Node a = DomImpl.attributes_getNamedItem(e, name);
        if (a == null) {
            Dom e2 = (Dom)((Object)DomImpl.node_getOwnerDocument(e));
            if (e2 == null) {
                throw new NotFoundErr("Document element can't be determined.");
            }
            a = DomImpl.document_createAttribute(e2, name);
            DomImpl.attributes_setNamedItem(e, (Dom)((Object)a));
        }
        DomImpl.node_setNodeValue((Dom)((Object)a), value);
    }

    public static void _element_setAttributeNS(Dom e, String uri, String qname, String value) {
        DomImpl.syncWrapVoid(e, p -> DomImpl.element_setAttributeNS(p, uri, qname, value));
    }

    public static void element_setAttributeNS(Dom e, String uri, String qname, String value) {
        DomImpl.validateQualifiedName(qname, uri, true);
        QName name = e.locale().makeQualifiedQName(uri, qname);
        String local = name.getLocalPart();
        String prefix = DomImpl.validatePrefix(name.getPrefix(), uri, local, true);
        org.w3c.dom.Node a = DomImpl.attributes_getNamedItemNS(e, uri, local);
        if (a == null) {
            a = DomImpl.document_createAttributeNS((Dom)((Object)DomImpl.node_getOwnerDocument(e)), uri, local);
            DomImpl.attributes_setNamedItemNS(e, (Dom)((Object)a));
        }
        DomImpl.node_setPrefix((Dom)((Object)a), prefix);
        DomImpl.node_setNodeValue((Dom)((Object)a), value);
    }

    public static NodeList _element_getElementsByTagName(Dom e, String name) {
        return DomImpl.syncWrap(e, p -> DomImpl.element_getElementsByTagName(p, name));
    }

    public static NodeList element_getElementsByTagName(Dom e, String name) {
        return new ElementsByTagNameNodeList(e, name);
    }

    public static NodeList _element_getElementsByTagNameNS(Dom e, String uri, String local) {
        return DomImpl.syncWrap(e, p -> DomImpl.element_getElementsByTagNameNS(p, uri, local));
    }

    public static NodeList element_getElementsByTagNameNS(Dom e, String uri, String local) {
        return new ElementsByTagNameNSNodeList(e, uri, local);
    }

    public static int _attributes_getLength(Dom e) {
        return DomImpl.syncWrap(e, DomImpl::attributes_getLength);
    }

    public static int attributes_getLength(Dom e) {
        int n = 0;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            ++n;
        }
        c.release();
        return n;
    }

    public static org.w3c.dom.Node _attributes_setNamedItem(Dom e, org.w3c.dom.Node attr) {
        Dom a;
        Locale l = e.locale();
        if (attr == null) {
            throw new IllegalArgumentException("Attr to set is null");
        }
        if (!(attr instanceof Dom) || (a = (Dom)((Object)attr)).locale() != l) {
            throw new WrongDocumentErr("Attr to set is from another document");
        }
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_setNamedItem(p, a));
    }

    public static org.w3c.dom.Node attributes_setNamedItem(Dom e, Dom a) {
        if (DomImpl.attr_getOwnerElement(a) != null) {
            throw new InuseAttributeError();
        }
        if (a.nodeType() != 2) {
            throw new HierarchyRequestErr("Node is not an attribute");
        }
        String name = DomImpl._node_getNodeName(a);
        Dom oldAttr = null;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            Dom aa = c.getDom();
            if (!DomImpl._node_getNodeName(aa).equals(name)) continue;
            if (oldAttr == null) {
                oldAttr = aa;
                continue;
            }
            DomImpl.removeNode(aa);
            c.toPrevAttr();
        }
        if (oldAttr == null) {
            c.moveToDom(e);
            c.next();
            Cur.moveNode((Xobj)((Object)a), c);
        } else {
            c.moveToDom(oldAttr);
            Cur.moveNode((Xobj)((Object)a), c);
            DomImpl.removeNode(oldAttr);
        }
        c.release();
        return (org.w3c.dom.Node)((Object)oldAttr);
    }

    public static org.w3c.dom.Node _attributes_getNamedItem(Dom e, String name) {
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_getNamedItem(e, name));
    }

    public static org.w3c.dom.Node attributes_getNamedItem(Dom e, String name) {
        Dom a = null;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            Dom d = c.getDom();
            if (!DomImpl._node_getNodeName(d).equals(name)) continue;
            a = d;
            break;
        }
        c.release();
        return (org.w3c.dom.Node)((Object)a);
    }

    public static org.w3c.dom.Node _attributes_getNamedItemNS(Dom e, String uri, String local) {
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_getNamedItemNS(p, uri, local));
    }

    public static org.w3c.dom.Node attributes_getNamedItemNS(Dom e, String uri, String local) {
        if (uri == null) {
            uri = "";
        }
        Dom a = null;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            Dom d = c.getDom();
            QName n = d.getQName();
            if (!n.getNamespaceURI().equals(uri) || !n.getLocalPart().equals(local)) continue;
            a = d;
            break;
        }
        c.release();
        return (org.w3c.dom.Node)((Object)a);
    }

    public static org.w3c.dom.Node _attributes_removeNamedItem(Dom e, String name) {
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_removeNamedItem(p, name));
    }

    public static org.w3c.dom.Node attributes_removeNamedItem(Dom e, String name) {
        Dom oldAttr = null;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            Dom aa = c.getDom();
            if (!DomImpl._node_getNodeName(aa).equals(name)) continue;
            if (oldAttr == null) {
                oldAttr = aa;
            }
            if (((AttrXobj)aa).isId()) {
                Document d = DomImpl.node_getOwnerDocument(aa);
                String val = DomImpl.node_getNodeValue(aa);
                if (d instanceof DocumentXobj) {
                    ((DocumentXobj)d).removeIdElement(val);
                }
            }
            DomImpl.removeNode(aa);
            c.toPrevAttr();
        }
        c.release();
        if (oldAttr == null) {
            throw new NotFoundErr("Named item not found: " + name);
        }
        return (org.w3c.dom.Node)((Object)oldAttr);
    }

    public static org.w3c.dom.Node _attributes_removeNamedItemNS(Dom e, String uri, String local) {
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_removeNamedItemNS(p, uri, local));
    }

    public static org.w3c.dom.Node attributes_removeNamedItemNS(Dom e, String uri, String local) {
        if (uri == null) {
            uri = "";
        }
        Dom oldAttr = null;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            Dom aa = c.getDom();
            QName qn = aa.getQName();
            if (!qn.getNamespaceURI().equals(uri) || !qn.getLocalPart().equals(local)) continue;
            if (oldAttr == null) {
                oldAttr = aa;
            }
            if (((AttrXobj)aa).isId()) {
                Document d = DomImpl.node_getOwnerDocument(aa);
                String val = DomImpl.node_getNodeValue(aa);
                if (d instanceof DocumentXobj) {
                    ((DocumentXobj)d).removeIdElement(val);
                }
            }
            DomImpl.removeNode(aa);
            c.toPrevAttr();
        }
        c.release();
        if (oldAttr == null) {
            throw new NotFoundErr("Named item not found: uri=" + uri + ", local=" + local);
        }
        return (org.w3c.dom.Node)((Object)oldAttr);
    }

    public static org.w3c.dom.Node _attributes_setNamedItemNS(Dom e, org.w3c.dom.Node attr) {
        Dom a;
        Locale l = e.locale();
        if (attr == null) {
            throw new IllegalArgumentException("Attr to set is null");
        }
        if (!(attr instanceof Dom) || (a = (Dom)((Object)attr)).locale() != l) {
            throw new WrongDocumentErr("Attr to set is from another document");
        }
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_setNamedItemNS(p, a));
    }

    public static org.w3c.dom.Node attributes_setNamedItemNS(Dom e, Dom a) {
        Element owner = DomImpl.attr_getOwnerElement(a);
        if (owner == e) {
            return (org.w3c.dom.Node)((Object)a);
        }
        if (owner != null) {
            throw new InuseAttributeError();
        }
        if (a.nodeType() != 2) {
            throw new HierarchyRequestErr("Node is not an attribute");
        }
        QName name = a.getQName();
        Dom oldAttr = null;
        Cur c = e.tempCur();
        while (c.toNextAttr()) {
            Dom aa = c.getDom();
            if (!aa.getQName().equals(name)) continue;
            if (oldAttr == null) {
                oldAttr = aa;
                continue;
            }
            DomImpl.removeNode(aa);
            c.toPrevAttr();
        }
        if (oldAttr == null) {
            c.moveToDom(e);
            c.next();
            Cur.moveNode((Xobj)((Object)a), c);
        } else {
            c.moveToDom(oldAttr);
            Cur.moveNode((Xobj)((Object)a), c);
            DomImpl.removeNode(oldAttr);
        }
        c.release();
        return (org.w3c.dom.Node)((Object)oldAttr);
    }

    public static org.w3c.dom.Node _attributes_item(Dom e, int index) {
        return DomImpl.syncWrap(e, p -> DomImpl.attributes_item(p, index));
    }

    public static org.w3c.dom.Node attributes_item(Dom e, int index) {
        if (index < 0) {
            return null;
        }
        Cur c = e.tempCur();
        Dom a = null;
        while (c.toNextAttr()) {
            if (index-- != 0) continue;
            a = c.getDom();
            break;
        }
        c.release();
        return (org.w3c.dom.Node)((Object)a);
    }

    public static String _processingInstruction_getData(Dom p) {
        return DomImpl._node_getNodeValue(p);
    }

    public static String _processingInstruction_getTarget(Dom p) {
        return DomImpl._node_getNodeName(p);
    }

    public static void _processingInstruction_setData(Dom p, String data) {
        DomImpl._node_setNodeValue(p, data);
    }

    public static boolean _attr_getSpecified(Dom a) {
        return true;
    }

    public static Element _attr_getOwnerElement(Dom a) {
        return DomImpl.syncWrap(a, DomImpl::attr_getOwnerElement);
    }

    public static Element attr_getOwnerElement(Dom n) {
        Cur c = n.tempCur();
        if (!c.toParentRaw()) {
            c.release();
            return null;
        }
        Dom p = c.getDom();
        c.release();
        return (Element)((Object)p);
    }

    public static void _characterData_appendData(Dom cd, String arg) {
        if (arg != null && arg.length() != 0) {
            DomImpl._node_setNodeValue(cd, DomImpl._node_getNodeValue(cd) + arg);
        }
    }

    public static void _characterData_deleteData(Dom c, int offset, int count) {
        String s = DomImpl._characterData_getData(c);
        if (offset < 0 || offset > s.length() || count < 0) {
            throw new IndexSizeError();
        }
        if (offset + count > s.length()) {
            count = s.length() - offset;
        }
        if (count > 0) {
            DomImpl._characterData_setData(c, s.substring(0, offset) + s.substring(offset + count));
        }
    }

    public static String _characterData_getData(Dom c) {
        return DomImpl._node_getNodeValue(c);
    }

    public static int _characterData_getLength(Dom c) {
        return DomImpl._characterData_getData(c).length();
    }

    public static void _characterData_insertData(Dom c, int offset, String arg) {
        String s = DomImpl._characterData_getData(c);
        if (offset < 0 || offset > s.length()) {
            throw new IndexSizeError();
        }
        if (arg != null && arg.length() > 0) {
            DomImpl._characterData_setData(c, s.substring(0, offset) + arg + s.substring(offset));
        }
    }

    public static void _characterData_replaceData(Dom c, int offset, int count, String arg) {
        String s = DomImpl._characterData_getData(c);
        if (offset < 0 || offset > s.length() || count < 0) {
            throw new IndexSizeError();
        }
        if (offset + count > s.length()) {
            count = s.length() - offset;
        }
        if (count > 0) {
            DomImpl._characterData_setData(c, s.substring(0, offset) + (arg == null ? "" : arg) + s.substring(offset + count));
        }
    }

    public static void _characterData_setData(Dom c, String data) {
        DomImpl._node_setNodeValue(c, data);
    }

    public static String _characterData_substringData(Dom c, int offset, int count) {
        String s = DomImpl._characterData_getData(c);
        if (offset < 0 || offset > s.length() || count < 0) {
            throw new IndexSizeError();
        }
        if (offset + count > s.length()) {
            count = s.length() - offset;
        }
        return s.substring(offset, offset + count);
    }

    public static Text _text_splitText(Dom t, int offset) {
        assert (t.nodeType() == 3);
        String s = DomImpl._characterData_getData(t);
        if (offset < 0 || offset > s.length()) {
            throw new IndexSizeError();
        }
        DomImpl._characterData_deleteData(t, offset, s.length() - offset);
        Dom t2 = (Dom)((Object)DomImpl._document_createTextNode(t, s.substring(offset)));
        Dom p = (Dom)((Object)DomImpl._node_getParentNode(t));
        if (p != null) {
            DomImpl._node_insertBefore(p, (Text)((Object)t2), DomImpl._node_getNextSibling(t));
            t.locale().invalidateDomCaches(p);
        }
        return (Text)((Object)t2);
    }

    public static String _text_getWholeText(Dom t) {
        throw new DomLevel3NotImplemented();
    }

    public static boolean _text_isElementContentWhitespace(Dom t) {
        throw new DomLevel3NotImplemented();
    }

    public static Text _text_replaceWholeText(Dom t, String content) {
        throw new DomLevel3NotImplemented();
    }

    public static XMLStreamReader _getXmlStreamReader(Dom n) {
        return DomImpl.syncWrap(n, DomImpl::getXmlStreamReader);
    }

    public static XMLStreamReader getXmlStreamReader(Dom n) {
        XMLStreamReader xs;
        switch (n.nodeType()) {
            case 1: 
            case 2: 
            case 7: 
            case 8: 
            case 9: 
            case 11: {
                Cur c = n.tempCur();
                xs = Jsr173.newXmlStreamReader(c, null);
                c.release();
                break;
            }
            case 3: 
            case 4: {
                CharNode cn = (CharNode)n;
                Cur c = cn.tempCur();
                if (c == null) {
                    c = n.locale().tempCur();
                    xs = Jsr173.newXmlStreamReader(c, cn.getObject(), cn._off, cn._cch);
                } else {
                    xs = Jsr173.newXmlStreamReader(c, c.getChars(cn._cch), c._offSrc, c._cchSrc);
                }
                c.release();
                break;
            }
            case 5: 
            case 6: 
            case 10: 
            case 12: {
                throw new RuntimeException("Not impl");
            }
            default: {
                throw new RuntimeException("Unknown kind");
            }
        }
        return xs;
    }

    public static XmlCursor _getXmlCursor(Dom n) {
        return DomImpl.syncWrap(n, DomImpl::getXmlCursor);
    }

    public static XmlCursor getXmlCursor(Dom n) {
        Cur c = n.tempCur();
        Cursor xc = new Cursor(c);
        c.release();
        return xc;
    }

    public static XmlObject _getXmlObject(Dom n) {
        return DomImpl.syncWrap(n, DomImpl::getXmlObject);
    }

    public static XmlObject getXmlObject(Dom n) {
        Cur c = n.tempCur();
        XmlObject x = c.getObject();
        c.release();
        return x;
    }

    public static boolean _soapText_isComment(Dom n) {
        org.apache.xmlbeans.impl.soap.Text text = (org.apache.xmlbeans.impl.soap.Text)((Object)n);
        return DomImpl.syncWrap(n, p -> p.locale()._saaj.soapText_isComment(text));
    }

    public static void _soapNode_detachNode(Dom n) {
        Node node = (Node)((Object)n);
        DomImpl.syncWrapVoid(n, p -> p.locale()._saaj.soapNode_detachNode(node));
    }

    public static void _soapNode_recycleNode(Dom n) {
        Node node = (Node)((Object)n);
        DomImpl.syncWrapVoid(n, p -> p.locale()._saaj.soapNode_recycleNode(node));
    }

    public static String _soapNode_getValue(Dom n) {
        Node node = (Node)((Object)n);
        return DomImpl.syncWrap(n, p -> p.locale()._saaj.soapNode_getValue(node));
    }

    public static void _soapNode_setValue(Dom n, String value) {
        Node node = (Node)((Object)n);
        DomImpl.syncWrapVoid(n, p -> p.locale()._saaj.soapNode_setValue(node, value));
    }

    public static SOAPElement _soapNode_getParentElement(Dom n) {
        Node node = (Node)((Object)n);
        return DomImpl.syncWrap(n, p -> p.locale()._saaj.soapNode_getParentElement(node));
    }

    public static void _soapNode_setParentElement(Dom n, SOAPElement p) {
        Node node = (Node)((Object)n);
        DomImpl.syncWrapVoid(n, x -> x.locale()._saaj.soapNode_setParentElement(node, p));
    }

    public static void _soapElement_removeContents(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        DomImpl.syncWrapVoid(d, x -> x.locale()._saaj.soapElement_removeContents(se));
    }

    public static String _soapElement_getEncodingStyle(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getEncodingStyle(se));
    }

    public static void _soapElement_setEncodingStyle(Dom d, String encodingStyle) {
        SOAPElement se = (SOAPElement)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapElement_setEncodingStyle(se, encodingStyle));
    }

    public static boolean _soapElement_removeNamespaceDeclaration(Dom d, String prefix) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_removeNamespaceDeclaration(se, prefix));
    }

    public static Iterator<Name> _soapElement_getAllAttributes(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getAllAttributes(se));
    }

    public static Iterator<SOAPElement> _soapElement_getChildElements(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getChildElements(se));
    }

    public static Iterator<String> _soapElement_getNamespacePrefixes(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getNamespacePrefixes(se));
    }

    public static SOAPElement _soapElement_addAttribute(Dom d, Name name, String value) throws SOAPException {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapElement_addAttribute(se, name, value));
    }

    public static SOAPElement _soapElement_addChildElement(Dom d, SOAPElement oldChild) throws SOAPException {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapElement_addChildElement(se, oldChild));
    }

    public static SOAPElement _soapElement_addChildElement(Dom d, Name name) throws SOAPException {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapElement_addChildElement(se, name));
    }

    public static SOAPElement _soapElement_addChildElement(Dom d, String localName) throws SOAPException {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapElement_addChildElement(se, localName));
    }

    public static SOAPElement _soapElement_addChildElement(Dom d, String localName, String prefix) throws SOAPException {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapElement_addChildElement(se, localName, prefix));
    }

    public static SOAPElement _soapElement_addChildElement(Dom d, String localName, String prefix, String uri) throws SOAPException {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapElement_addChildElement(se, localName, prefix, uri));
    }

    public static SOAPElement _soapElement_addNamespaceDeclaration(Dom d, String prefix, String uri) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_addNamespaceDeclaration(se, prefix, uri));
    }

    public static SOAPElement _soapElement_addTextNode(Dom d, String data) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_addTextNode(se, data));
    }

    public static String _soapElement_getAttributeValue(Dom d, Name name) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getAttributeValue(se, name));
    }

    public static Iterator<SOAPElement> _soapElement_getChildElements(Dom d, Name name) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getChildElements(se, name));
    }

    public static Name _soapElement_getElementName(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getElementName(se));
    }

    public static String _soapElement_getNamespaceURI(Dom d, String prefix) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getNamespaceURI(se, prefix));
    }

    public static Iterator<String> _soapElement_getVisibleNamespacePrefixes(Dom d) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_getVisibleNamespacePrefixes(se));
    }

    public static boolean _soapElement_removeAttribute(Dom d, Name name) {
        SOAPElement se = (SOAPElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapElement_removeAttribute(se, name));
    }

    public static SOAPBody _soapEnvelope_addBody(Dom d) throws SOAPException {
        SOAPEnvelope se = (SOAPEnvelope)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapEnvelope_addBody(se));
    }

    public static SOAPBody _soapEnvelope_getBody(Dom d) throws SOAPException {
        SOAPEnvelope se = (SOAPEnvelope)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapEnvelope_getBody(se));
    }

    public static SOAPHeader _soapEnvelope_getHeader(Dom d) throws SOAPException {
        SOAPEnvelope se = (SOAPEnvelope)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapEnvelope_getHeader(se));
    }

    public static SOAPHeader _soapEnvelope_addHeader(Dom d) throws SOAPException {
        SOAPEnvelope se = (SOAPEnvelope)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapEnvelope_addHeader(se));
    }

    public static Name _soapEnvelope_createName(Dom d, String localName) {
        SOAPEnvelope se = (SOAPEnvelope)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapEnvelope_createName(se, localName));
    }

    public static Name _soapEnvelope_createName(Dom d, String localName, String prefix, String namespaceURI) {
        SOAPEnvelope se = (SOAPEnvelope)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapEnvelope_createName(se, localName, prefix, namespaceURI));
    }

    public static Iterator<SOAPHeaderElement> soapHeader_examineAllHeaderElements(Dom d) {
        SOAPHeader sh = (SOAPHeader)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeader_examineAllHeaderElements(sh));
    }

    public static Iterator<SOAPHeaderElement> soapHeader_extractAllHeaderElements(Dom d) {
        SOAPHeader sh = (SOAPHeader)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeader_extractAllHeaderElements(sh));
    }

    public static Iterator<SOAPHeaderElement> soapHeader_examineHeaderElements(Dom d, String actor) {
        SOAPHeader sh = (SOAPHeader)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeader_examineHeaderElements(sh, actor));
    }

    public static Iterator<SOAPHeaderElement> soapHeader_examineMustUnderstandHeaderElements(Dom d, String mustUnderstandString) {
        SOAPHeader sh = (SOAPHeader)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeader_examineMustUnderstandHeaderElements(sh, mustUnderstandString));
    }

    public static Iterator<SOAPHeaderElement> soapHeader_extractHeaderElements(Dom d, String actor) {
        SOAPHeader sh = (SOAPHeader)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeader_extractHeaderElements(sh, actor));
    }

    public static SOAPHeaderElement soapHeader_addHeaderElement(Dom d, Name name) {
        SOAPHeader sh = (SOAPHeader)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeader_addHeaderElement(sh, name));
    }

    public static boolean soapBody_hasFault(Dom d) {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapBody_hasFault(sb));
    }

    public static SOAPFault soapBody_addFault(Dom d) throws SOAPException {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapBody_addFault(sb));
    }

    public static SOAPFault soapBody_getFault(Dom d) {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapBody_getFault(sb));
    }

    public static SOAPBodyElement soapBody_addBodyElement(Dom d, Name name) {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapBody_addBodyElement(sb, name));
    }

    public static SOAPBodyElement soapBody_addDocument(Dom d, Document document) {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapBody_addDocument(sb, document));
    }

    public static SOAPFault soapBody_addFault(Dom d, Name name, String s) throws SOAPException {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapBody_addFault(sb, name, s));
    }

    public static SOAPFault soapBody_addFault(Dom d, Name faultCode, String faultString, java.util.Locale locale) throws SOAPException {
        SOAPBody sb = (SOAPBody)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapBody_addFault(sb, faultCode, faultString, locale));
    }

    public static void soapFault_setFaultString(Dom d, String faultString) {
        SOAPFault sf = (SOAPFault)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapFault_setFaultString(sf, faultString));
    }

    public static void soapFault_setFaultString(Dom d, String faultString, java.util.Locale locale) {
        SOAPFault sf = (SOAPFault)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapFault_setFaultString(sf, faultString, locale));
    }

    public static void soapFault_setFaultCode(Dom d, Name faultCodeName) throws SOAPException {
        SOAPFault sf = (SOAPFault)((Object)d);
        DomImpl.syncWrapEx(d, () -> {
            d.locale()._saaj.soapFault_setFaultCode(sf, faultCodeName);
            return null;
        });
    }

    public static void soapFault_setFaultActor(Dom d, String faultActorString) {
        SOAPFault sf = (SOAPFault)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapFault_setFaultActor(sf, faultActorString));
    }

    public static String soapFault_getFaultActor(Dom d) {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapFault_getFaultActor(sf));
    }

    public static String soapFault_getFaultCode(Dom d) {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapFault_getFaultCode(sf));
    }

    public static void soapFault_setFaultCode(Dom d, String faultCode) throws SOAPException {
        SOAPFault sf = (SOAPFault)((Object)d);
        DomImpl.syncWrapEx(d, () -> {
            d.locale()._saaj.soapFault_setFaultCode(sf, faultCode);
            return null;
        });
    }

    public static java.util.Locale soapFault_getFaultStringLocale(Dom d) {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapFault_getFaultStringLocale(sf));
    }

    public static Name soapFault_getFaultCodeAsName(Dom d) {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapFault_getFaultCodeAsName(sf));
    }

    public static String soapFault_getFaultString(Dom d) {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapFault_getFaultString(sf));
    }

    public static Detail soapFault_addDetail(Dom d) throws SOAPException {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrapEx(d, () -> d.locale()._saaj.soapFault_addDetail(sf));
    }

    public static Detail soapFault_getDetail(Dom d) {
        SOAPFault sf = (SOAPFault)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapFault_getDetail(sf));
    }

    public static void soapHeaderElement_setMustUnderstand(Dom d, boolean mustUnderstand) {
        SOAPHeaderElement she = (SOAPHeaderElement)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapHeaderElement_setMustUnderstand(she, mustUnderstand));
    }

    public static boolean soapHeaderElement_getMustUnderstand(Dom d) {
        SOAPHeaderElement she = (SOAPHeaderElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeaderElement_getMustUnderstand(she));
    }

    public static void soapHeaderElement_setActor(Dom d, String actor) {
        SOAPHeaderElement she = (SOAPHeaderElement)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapHeaderElement_setActor(she, actor));
    }

    public static String soapHeaderElement_getActor(Dom d) {
        SOAPHeaderElement she = (SOAPHeaderElement)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapHeaderElement_getActor(she));
    }

    public static DetailEntry detail_addDetailEntry(Dom d, Name name) {
        Detail detail = (Detail)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.detail_addDetailEntry(detail, name));
    }

    public static Iterator<DetailEntry> detail_getDetailEntries(Dom d) {
        Detail detail = (Detail)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.detail_getDetailEntries(detail));
    }

    public static void _soapPart_removeAllMimeHeaders(Dom d) {
        SOAPPart sp = (SOAPPart)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapPart_removeAllMimeHeaders(sp));
    }

    public static void _soapPart_removeMimeHeader(Dom d, String name) {
        SOAPPart sp = (SOAPPart)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapPart_removeMimeHeader(sp, name));
    }

    public static Iterator<MimeHeader> _soapPart_getAllMimeHeaders(Dom d) {
        SOAPPart sp = (SOAPPart)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapPart_getAllMimeHeaders(sp));
    }

    public static SOAPEnvelope _soapPart_getEnvelope(Dom d) {
        SOAPPart sp = (SOAPPart)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapPart_getEnvelope(sp));
    }

    public static Source _soapPart_getContent(Dom d) {
        SOAPPart sp = (SOAPPart)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapPart_getContent(sp));
    }

    public static void _soapPart_setContent(Dom d, Source source) {
        SOAPPart sp = (SOAPPart)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapPart_setContent(sp, source));
    }

    public static String[] _soapPart_getMimeHeader(Dom d, String name) {
        SOAPPart sp = (SOAPPart)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapPart_getMimeHeader(sp, name));
    }

    public static void _soapPart_addMimeHeader(Dom d, String name, String value) {
        SOAPPart sp = (SOAPPart)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapPart_addMimeHeader(sp, name, value));
    }

    public static void _soapPart_setMimeHeader(Dom d, String name, String value) {
        SOAPPart sp = (SOAPPart)((Object)d);
        DomImpl.syncWrapVoid(d, p -> p.locale()._saaj.soapPart_setMimeHeader(sp, name, value));
    }

    public static Iterator<MimeHeader> _soapPart_getMatchingMimeHeaders(Dom d, String[] names) {
        SOAPPart sp = (SOAPPart)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapPart_getMatchingMimeHeaders(sp, names));
    }

    public static Iterator<MimeHeader> _soapPart_getNonMatchingMimeHeaders(Dom d, String[] names) {
        SOAPPart sp = (SOAPPart)((Object)d);
        return DomImpl.syncWrap(d, p -> p.locale()._saaj.soapPart_getNonMatchingMimeHeaders(sp, names));
    }

    public static void saajCallback_setSaajData(Dom d, Object o) {
        DomImpl.syncWrapVoid(d, p -> DomImpl.impl_saajCallback_setSaajData(p, o));
    }

    public static void impl_saajCallback_setSaajData(Dom d, Object o) {
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.moveToDom(d);
        SaajData sd = null;
        if (o != null) {
            sd = (SaajData)c.getBookmark(SaajData.class);
            if (sd == null) {
                sd = new SaajData();
            }
            sd._obj = o;
        }
        c.setBookmark(SaajData.class, sd);
        c.release();
    }

    public static Object saajCallback_getSaajData(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::impl_saajCallback_getSaajData);
    }

    public static Object impl_saajCallback_getSaajData(Dom d) {
        Locale l = d.locale();
        Cur c = l.tempCur();
        c.moveToDom(d);
        SaajData sd = (SaajData)c.getBookmark(SaajData.class);
        Object o = sd == null ? null : sd._obj;
        c.release();
        return o;
    }

    public static Element saajCallback_createSoapElement(Dom d, QName name, QName parentName) {
        return DomImpl.syncWrap(d, p -> DomImpl.impl_saajCallback_createSoapElement(p, name, parentName));
    }

    public static Element impl_saajCallback_createSoapElement(Dom d, QName name, QName parentName) {
        Cur c = d.locale().tempCur();
        c.createElement(name, parentName);
        Dom e = c.getDom();
        c.release();
        return (Element)((Object)e);
    }

    public static Element saajCallback_importSoapElement(Dom d, Element elem, boolean deep, QName parentName) {
        return DomImpl.syncWrap(d, p -> DomImpl.impl_saajCallback_importSoapElement(p, elem, deep, parentName));
    }

    public static Element impl_saajCallback_importSoapElement(Dom d, Element elem, boolean deep, QName parentName) {
        throw new RuntimeException("Not impl");
    }

    public static Text saajCallback_ensureSoapTextNode(Dom d) {
        return DomImpl.syncWrap(d, DomImpl::impl_saajCallback_ensureSoapTextNode);
    }

    public static Text impl_saajCallback_ensureSoapTextNode(Dom d) {
        return null;
    }

    private static <T> T syncWrap(Dom d, Function<Dom, T> inner) {
        return (T)DomImpl.syncWrapHelper(d.locale(), true, () -> inner.apply(d));
    }

    private static <T> T syncWrapNoEnter(Dom d, Function<Dom, T> inner) {
        return (T)DomImpl.syncWrapHelper(d.locale(), false, () -> inner.apply(d));
    }

    private static void syncWrapVoid(Dom d, Consumer<Dom> inner) {
        DomImpl.syncWrapHelper(d.locale(), true, () -> {
            inner.accept(d);
            return null;
        });
    }

    private static <T> T syncWrapEx(Dom d, WrapSoapEx<T> inner) throws SOAPException {
        return DomImpl.syncWrapHelperEx(d.locale(), true, inner);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <T> T syncWrapHelper(Locale l, boolean enter, Supplier<T> inner) {
        if (l.noSync()) {
            return DomImpl.syncWrapHelper2(l, enter, inner);
        }
        Locale locale = l;
        synchronized (locale) {
            return DomImpl.syncWrapHelper2(l, enter, inner);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <T> T syncWrapHelper2(Locale l, boolean enter, Supplier<T> inner) {
        if (enter) {
            l.enter();
        }
        try {
            T t = inner.get();
            return t;
        }
        finally {
            if (enter) {
                l.exit();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <T> T syncWrapHelperEx(Locale l, boolean enter, WrapSoapEx<T> inner) throws SOAPException {
        if (l.noSync()) {
            return DomImpl.syncWrapHelperEx2(l, enter, inner);
        }
        Locale locale = l;
        synchronized (locale) {
            return DomImpl.syncWrapHelperEx2(l, enter, inner);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <T> T syncWrapHelperEx2(Locale l, boolean enter, WrapSoapEx<T> inner) throws SOAPException {
        if (enter) {
            l.enter();
        }
        try {
            T t = inner.get();
            return t;
        }
        finally {
            if (enter) {
                l.exit();
            }
        }
    }

    private static interface WrapSoapEx<T> {
        public T get() throws SOAPException;
    }

    public static class DomLevel3NotImplemented
    extends RuntimeException {
        DomLevel3NotImplemented() {
            super("DOM Level 3 Not implemented");
        }
    }

    private static class SaajData {
        Object _obj;

        private SaajData() {
        }
    }

    private static class ElementsByTagNameNSNodeList
    extends ElementsNodeList {
        private final String _uri;
        private final String _local;

        ElementsByTagNameNSNodeList(Dom root, String uri, String local) {
            super(root);
            this._uri = uri == null ? "" : uri;
            this._local = local;
            assert (local != null);
        }

        @Override
        protected boolean match(Dom element) {
            if (!this._uri.equals("*") && !this._uri.equals(DomImpl._node_getNamespaceURI(element))) {
                return false;
            }
            return this._local.equals("*") || this._local.equals(DomImpl._node_getLocalName(element));
        }
    }

    private static class ElementsByTagNameNodeList
    extends ElementsNodeList {
        private final String _name;

        ElementsByTagNameNodeList(Dom root, String name) {
            super(root);
            this._name = name;
            assert (this._name != null);
        }

        @Override
        protected boolean match(Dom element) {
            return this._name.equals("*") || this._name.equals(DomImpl._node_getNodeName(element));
        }
    }

    private static abstract class ElementsNodeList
    implements NodeList {
        private final Dom _root;
        private final Locale _locale;
        private long _version;
        private ArrayList<Dom> _elements;

        ElementsNodeList(Dom root) {
            assert (root.nodeType() == 9 || root.nodeType() == 1);
            this._root = root;
            this._locale = this._root.locale();
            this._version = 0L;
        }

        @Override
        public int getLength() {
            this.ensureElements();
            return this._elements.size();
        }

        @Override
        public org.w3c.dom.Node item(int i) {
            this.ensureElements();
            return i < 0 || i >= this._elements.size() ? null : (org.w3c.dom.Node)((Object)this._elements.get(i));
        }

        private void ensureElements() {
            if (this._version == this._locale.version()) {
                return;
            }
            this._version = this._locale.version();
            this._elements = new ArrayList();
            DomImpl.syncWrapHelper(this._locale, true, () -> {
                this.addElements(this._root);
                return null;
            });
        }

        private void addElements(Dom node) {
            org.w3c.dom.Node c = DomImpl.firstChild(node);
            while (c != null) {
                if (((Dom)((Object)c)).nodeType() == 1) {
                    if (this.match((Dom)((Object)c))) {
                        this._elements.add((Dom)((Object)c));
                    }
                    this.addElements((Dom)((Object)c));
                }
                c = DomImpl.nextSibling((Dom)((Object)c));
            }
        }

        protected abstract boolean match(Dom var1);
    }

    private static final class EmptyNodeList
    implements NodeList {
        private EmptyNodeList() {
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public org.w3c.dom.Node item(int i) {
            return null;
        }
    }

    static class InvalidCharacterError
    extends DOMException {
        InvalidCharacterError() {
            this("The name contains an invalid character");
        }

        InvalidCharacterError(String message) {
            super((short)5, message);
        }
    }

    static class NotSupportedError
    extends DOMException {
        NotSupportedError(String message) {
            super((short)9, message);
        }
    }

    static class IndexSizeError
    extends DOMException {
        IndexSizeError() {
            this("Index Size Error");
        }

        IndexSizeError(String message) {
            super((short)1, message);
        }
    }

    static class InuseAttributeError
    extends DOMException {
        InuseAttributeError() {
            this("Attribute currently in use error");
        }

        InuseAttributeError(String message) {
            super((short)10, message);
        }
    }

    static class NoModificationAllowedErr
    extends DOMException {
        NoModificationAllowedErr(String message) {
            super((short)7, message);
        }
    }

    static class NamespaceErr
    extends DOMException {
        NamespaceErr(String message) {
            super((short)14, message);
        }
    }

    static class NotFoundErr
    extends DOMException {
        NotFoundErr(String message) {
            super((short)8, message);
        }
    }

    static class WrongDocumentErr
    extends DOMException {
        WrongDocumentErr(String message) {
            super((short)4, message);
        }
    }

    static class HierarchyRequestErr
    extends DOMException {
        HierarchyRequestErr(String message) {
            super((short)3, message);
        }
    }

    public static interface Dom {
        public Locale locale();

        public int nodeType();

        public Cur tempCur();

        public QName getQName();

        public boolean nodeCanHavePrefixUri();

        public void dump();

        public void dump(PrintStream var1);

        public void dump(PrintStream var1, Object var2);
    }
}

