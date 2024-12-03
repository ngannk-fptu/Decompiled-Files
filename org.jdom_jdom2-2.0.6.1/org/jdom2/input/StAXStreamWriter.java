/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input;

import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMFactory;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.Text;
import org.jdom2.util.JDOMNamespaceContext;
import org.jdom2.util.NamespaceStack;

public class StAXStreamWriter
implements XMLStreamWriter {
    private static final DefaultJDOMFactory DEFFAC = new DefaultJDOMFactory();
    private NamespaceContext globalcontext = null;
    private NamespaceStack usednsstack = new NamespaceStack();
    private NamespaceStack boundstack = new NamespaceStack();
    private LinkedList<Namespace> pendingns = new LinkedList();
    private final boolean repairnamespace;
    private Document document = null;
    private boolean done = false;
    private Parent parent = null;
    private Element activeelement = null;
    private boolean isempty = false;
    private Text activetext = null;
    private int genprefix = 0;
    private final JDOMFactory factory;

    public StAXStreamWriter() {
        this(DEFFAC, true);
    }

    public StAXStreamWriter(JDOMFactory fac, boolean repairnamespace) {
        this.factory = fac;
        this.repairnamespace = repairnamespace;
        this.boundstack.push(new Namespace[0]);
    }

    public Document getDocument() {
        if (this.done && this.document != null) {
            return this.document;
        }
        if (this.done) {
            throw new IllegalStateException("Writer is closed");
        }
        throw new IllegalStateException("Cannot get Document until writer has ended the document");
    }

    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(null, null);
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument(null, version);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        if (this.done || this.document != null) {
            throw new IllegalStateException("Cannot write start document twice.");
        }
        this.document = this.factory.document(null);
        this.parent = this.document;
        if (encoding != null && !"".equals(encoding)) {
            this.document.setProperty("ENCODING", encoding);
        }
        this.activeelement = null;
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        if (this.document == null || this.document.hasRootElement()) {
            throw new XMLStreamException("Can only set the NamespaceContext at the Document start");
        }
        this.globalcontext = context;
    }

    public String getPrefix(String uri) throws XMLStreamException {
        if (this.document == null) {
            return null;
        }
        Namespace n = this.boundstack.getFirstNamespaceForURI(uri);
        if (n != null) {
            return n.getPrefix();
        }
        if (this.globalcontext != null) {
            return this.globalcontext.getPrefix(uri);
        }
        return null;
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix may not be null");
        }
        if (prefix.equals("xmlns")) {
            return;
        }
        if (this.document == null || this.done) {
            throw new IllegalStateException("Attempt to set prefix at an illegal stream state.");
        }
        Namespace ns = Namespace.getNamespace(prefix, uri);
        if (!this.boundstack.isInScope(ns)) {
            ArrayList<Namespace> al = new ArrayList<Namespace>();
            for (Namespace n : this.boundstack) {
                if (n.getPrefix().equals(prefix)) continue;
                al.add(n);
            }
            al.add(ns);
            this.boundstack.pop();
            this.boundstack.push(al);
        }
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.setPrefix("", uri);
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        throw new UnsupportedOperationException("not supported yet");
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        int pos = localName.indexOf(58);
        if (pos >= 0) {
            this.writeStartElement(localName.substring(0, pos), localName.substring(pos + 1));
        } else {
            this.writeStartElement("", localName);
        }
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        if (namespaceURI == null) {
            throw new XMLStreamException("Cannot have a null namespaceURI");
        }
        if (localName == null) {
            throw new XMLStreamException("Cannot have a null localname");
        }
        int pos = localName.indexOf(58);
        if (pos >= 0) {
            this.buildElement(localName.substring(0, pos), localName.substring(pos + 1), namespaceURI, false, false);
        } else {
            this.buildElement("", localName, namespaceURI, false, false);
        }
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        if (prefix == null) {
            throw new XMLStreamException("Cannot have a null prefix");
        }
        if (localName == null) {
            throw new XMLStreamException("Cannot have a null localName");
        }
        if (namespaceURI == null) {
            throw new XMLStreamException("Cannot have a null namespaceURI");
        }
        this.buildElement(prefix, localName, namespaceURI, true, false);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        if (localName == null) {
            throw new XMLStreamException("Cannot have a null localname");
        }
        this.buildElement("", localName, "", false, true);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        if (namespaceURI == null) {
            throw new XMLStreamException("Cannot have a null namespaceURI");
        }
        if (localName == null) {
            throw new XMLStreamException("Cannot have a null localname");
        }
        this.buildElement("", localName, namespaceURI, false, true);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        if (prefix == null) {
            throw new XMLStreamException("Cannot have a null prefix");
        }
        if (localName == null) {
            throw new XMLStreamException("Cannot have a null localname");
        }
        if (namespaceURI == null) {
            throw new XMLStreamException("Cannot have a null namespaceURI");
        }
        this.buildElement(prefix, localName, namespaceURI, true, true);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.writeNamespace("", namespaceURI);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if (this.activeelement == null) {
            throw new IllegalStateException("Can only write a Namespace after starting an Element and before adding content to that Element.");
        }
        if (prefix == null || "xmlns".equals(prefix)) {
            if ("".equals(this.activeelement.getNamespacePrefix())) {
                this.activeelement.setNamespace(Namespace.getNamespace("", namespaceURI));
            }
            this.writeNamespace("", namespaceURI);
        } else {
            this.pendingns.add(Namespace.getNamespace(prefix, namespaceURI));
        }
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.buildAttribute("", "", localName, value, false);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.buildAttribute("", namespaceURI == null ? "" : namespaceURI, localName, value, false);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.buildAttribute(prefix == null ? "" : prefix, namespaceURI == null ? "" : namespaceURI, localName, value, true);
    }

    public void writeComment(String data) throws XMLStreamException {
        if (this.document == null || this.done) {
            throw new XMLStreamException("Can only add a Comment to the Document or an Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.factory.addContent(this.parent, this.factory.comment(data));
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        if (this.document == null || this.done) {
            throw new XMLStreamException("Can only add a ProcessingInstruction to the Document or an Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.factory.addContent(this.parent, this.factory.processingInstruction(target));
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        if (this.document == null || this.done) {
            throw new XMLStreamException("Can only add a ProcessingInstruction to the Document or an Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.factory.addContent(this.parent, this.factory.processingInstruction(target, data));
    }

    public void writeCData(String data) throws XMLStreamException {
        if (!(this.parent instanceof Element)) {
            throw new XMLStreamException("Can only writeCDATA() inside an Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.factory.addContent(this.parent, this.factory.cdata(data));
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        if (!(this.parent instanceof Element)) {
            throw new XMLStreamException("Can only writeEntityRef() inside an Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.factory.addContent(this.parent, this.factory.entityRef(name));
    }

    public void writeCharacters(String chars) throws XMLStreamException {
        if (this.document == null || this.done) {
            throw new XMLStreamException("Unable to add Characters at this point in the stream.");
        }
        this.flushActiveElement();
        if (chars == null) {
            return;
        }
        if (this.parent instanceof Element) {
            if (this.activetext != null) {
                this.activetext.append(chars);
            } else if (chars.length() > 0) {
                this.activetext = this.factory.text(chars);
                this.factory.addContent(this.parent, this.activetext);
            }
        }
    }

    public void writeCharacters(char[] chars, int start, int len) throws XMLStreamException {
        this.writeCharacters(new String(chars, start, len));
    }

    public void writeEndElement() throws XMLStreamException {
        if (!(this.parent instanceof Element)) {
            throw new XMLStreamException("Cannot end an Element unless you are in an Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.usednsstack.pop();
        this.boundstack.pop();
        this.boundstack.pop();
        this.boundstack.pop();
        this.parent = this.parent.getParent();
    }

    public void writeEndDocument() throws XMLStreamException {
        if (this.document == null || this.done || this.parent instanceof Element) {
            throw new IllegalStateException("Cannot write end document before writing the end of root element");
        }
        this.flushActiveElement();
        this.done = true;
    }

    public void close() throws XMLStreamException {
        this.document = null;
        this.parent = null;
        this.activeelement = null;
        this.activetext = null;
        this.boundstack = null;
        this.usednsstack = null;
        this.done = true;
    }

    public void flush() throws XMLStreamException {
    }

    public NamespaceContext getNamespaceContext() {
        if (this.document == null) {
            return new JDOMNamespaceContext(new Namespace[0]);
        }
        return new JDOMNamespaceContext(this.boundstack.getScope());
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private final void buildElement(String prefix, String localName, String namespaceURI, boolean withpfx, boolean empty) throws XMLStreamException {
        if (this.document == null || this.done) {
            throw new XMLStreamException("Cannot write new element when in current state.");
        }
        if (this.parent == this.document && this.document.hasRootElement()) {
            throw new XMLStreamException("Document can have only one root Element.");
        }
        this.flushActiveElement();
        this.flushActiveText();
        this.boundstack.push(new Namespace[0]);
        Namespace ns = this.resolveElementNamespace(prefix, namespaceURI, withpfx);
        Element e = this.factory.element(localName, ns);
        this.factory.addContent(this.parent, e);
        this.activeelement = e;
        if (empty) {
            this.isempty = true;
        } else {
            this.isempty = false;
            this.parent = e;
        }
    }

    private Namespace resolveElementNamespace(String prefix, String namespaceURI, boolean withpfx) throws XMLStreamException {
        Namespace defns;
        if ("".equals(namespaceURI) && Namespace.NO_NAMESPACE != (defns = this.boundstack.getNamespaceForPrefix(""))) {
            if (this.repairnamespace) {
                return Namespace.NO_NAMESPACE;
            }
            throw new XMLStreamException("This attempt to use the empty URI \"\" as an Element Namespace is illegal because the default Namespace is already bound to the URI '" + defns.getURI() + "'. You must call setPrefix(\"\", \"\") prior to this call.");
        }
        Namespace ns = Namespace.getNamespace(prefix, namespaceURI);
        if (withpfx) {
            Namespace bnd = this.boundstack.getNamespaceForPrefix(prefix);
            if (bnd == null || bnd == ns) {
                return ns;
            }
            if (this.repairnamespace) {
                return Namespace.getNamespace(this.generatePrefix(), namespaceURI);
            }
            throw new XMLStreamException("Namespace prefix " + prefix + " in this scope is bound to a different URI '" + bnd.getURI() + "' (repairing not set for this XMLStreamWriter).");
        }
        if (this.boundstack.getNamespaceForPrefix("") == ns) {
            return ns;
        }
        Namespace bound = this.boundstack.getFirstNamespaceForURI(namespaceURI);
        if (bound != null) {
            return bound;
        }
        if (this.repairnamespace) {
            return Namespace.getNamespace(this.generatePrefix(), namespaceURI);
        }
        throw new XMLStreamException("Namespace URI " + namespaceURI + " is not bound in this scope (repairing not set for this XMLStreamWriter).");
    }

    private Namespace resolveAttributeNamespace(String prefix, String namespaceURI, boolean withpfx) throws XMLStreamException {
        Namespace[] bound;
        Namespace ns = Namespace.getNamespace(prefix, namespaceURI);
        if (ns == Namespace.NO_NAMESPACE) {
            return ns;
        }
        if (withpfx && !"".equals(prefix)) {
            Namespace bnd = this.boundstack.getNamespaceForPrefix(prefix);
            if (bnd == null || bnd == ns) {
                return ns;
            }
            if (this.repairnamespace) {
                Namespace gen = Namespace.getNamespace(this.generatePrefix(), namespaceURI);
                this.setPrefix(gen.getPrefix(), gen.getURI());
                return gen;
            }
            throw new XMLStreamException("Namespace prefix " + prefix + " in this scope is bound to a different URI '" + bnd.getURI() + "' (repairing not set for this XMLStreamWriter).");
        }
        for (Namespace b : bound = this.boundstack.getAllNamespacesForURI(namespaceURI)) {
            if ("".equals(b.getPrefix())) continue;
            return b;
        }
        if (this.repairnamespace || bound.length > 0) {
            return Namespace.getNamespace(this.generatePrefix(), namespaceURI);
        }
        throw new XMLStreamException("Namespace URI " + namespaceURI + " is not bound in this attribute scope (repairing not set for this XMLStreamWriter).");
    }

    private String generatePrefix() {
        String pfx = String.format("ns%03d", ++this.genprefix);
        while (this.boundstack.getNamespaceForPrefix(pfx) != null) {
            pfx = String.format("ns%03d", ++this.genprefix);
        }
        return pfx;
    }

    private final void buildAttribute(String prefix, String namespaceURI, String localName, String value, boolean withpfx) throws XMLStreamException {
        if (!(this.parent instanceof Element)) {
            throw new IllegalStateException("Cannot write attribute unless inside an Element.");
        }
        if (localName == null) {
            throw new XMLStreamException("localName is not allowed to be null");
        }
        if (value == null) {
            throw new XMLStreamException("value is not allowed to be null");
        }
        if (this.activeelement == null) {
            throw new IllegalStateException("Cannot add Attributes to an Element after other content was added.");
        }
        Namespace ns = this.resolveAttributeNamespace(prefix, namespaceURI, withpfx);
        this.factory.setAttribute(this.activeelement, this.factory.attribute(localName, value, ns));
    }

    private final void flushActiveElement() {
        if (this.activeelement != null) {
            boolean mod = false;
            this.usednsstack.push(this.activeelement);
            for (Namespace ns : this.pendingns) {
                if (this.usednsstack.isInScope(ns)) continue;
                this.activeelement.addNamespaceDeclaration(ns);
                mod = true;
            }
            this.pendingns.clear();
            if (mod) {
                this.usednsstack.pop();
                if (this.isempty) {
                    this.boundstack.pop();
                    this.activeelement = null;
                    return;
                }
                this.usednsstack.push(this.activeelement);
            }
            if (this.isempty) {
                this.boundstack.pop();
                this.activeelement = null;
                return;
            }
            this.boundstack.push(this.activeelement);
            this.boundstack.push(new Namespace[0]);
            this.activeelement = null;
        }
    }

    private final void flushActiveText() {
        this.activetext = null;
    }
}

