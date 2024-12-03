/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.MimeType
 */
package org.apache.abdera.parser.stax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRIHelper;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.FOMException;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMWriter;
import org.apache.abdera.parser.stax.util.FOMElementIteratorWrapper;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.WriterOptions;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMElementImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMElement
extends OMElementImpl
implements Element,
OMElement,
Constants {
    private static final long serialVersionUID = 8024257594220911953L;

    protected FOMElement(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(parent, name, namespace, null, factory, true);
    }

    protected FOMElement(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(parent, qname.getLocalPart(), FOMElement.getOrCreateNamespace(qname, parent, factory), null, factory, true);
    }

    protected FOMElement(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(parent, localName, null, builder, factory, false);
    }

    private static OMNamespace getOrCreateNamespace(QName qname, OMContainer parent, OMFactory factory) {
        OMNamespace ns;
        String namespace = qname.getNamespaceURI();
        String prefix = qname.getPrefix();
        if (parent != null && parent instanceof OMElement && (ns = ((OMElement)parent).findNamespace(namespace, prefix)) != null) {
            return ns;
        }
        return factory.createOMNamespace(qname.getNamespaceURI(), qname.getPrefix());
    }

    protected Element getWrapped(Element internal) {
        if (internal == null) {
            return null;
        }
        FOMFactory factory = (FOMFactory)this.getFactory();
        return factory.getElementWrapper(internal);
    }

    @Override
    public <T extends Base> T getParentElement() {
        Base parent = (Base)((Object)super.getParent());
        return (T)(parent instanceof Element ? this.getWrapped((Element)parent) : parent);
    }

    protected void setParentDocument(Document parent) {
        super.setParent((OMContainer)((Object)parent));
    }

    @Override
    public <T extends Element> T setParentElement(Element parent) {
        if (parent instanceof ElementWrapper) {
            parent = ((ElementWrapper)parent).getInternal();
        }
        super.setParent((FOMElement)parent);
        return (T)this;
    }

    @Override
    public <T extends Element> T getPreviousSibling() {
        for (OMNode el = this.getPreviousOMSibling(); el != null; el = el.getPreviousOMSibling()) {
            if (!(el instanceof Element)) continue;
            return (T)this.getWrapped((Element)((Object)el));
        }
        return null;
    }

    @Override
    public <T extends Element> T getNextSibling() {
        for (OMNode el = this.getNextOMSibling(); el != null; el = el.getNextOMSibling()) {
            if (!(el instanceof Element)) continue;
            return (T)this.getWrapped((Element)((Object)el));
        }
        return null;
    }

    @Override
    public <T extends Element> T getFirstChild() {
        return (T)this.getWrapped((Element)((Object)this.getFirstElement()));
    }

    @Override
    public <T extends Element> T getPreviousSibling(QName qname) {
        for (T el = this.getPreviousSibling(); el != null; el = el.getPreviousSibling()) {
            OMElement omel = (OMElement)el;
            if (!omel.getQName().equals(qname)) continue;
            return (T)this.getWrapped((Element)((Object)omel));
        }
        return null;
    }

    @Override
    public <T extends Element> T getNextSibling(QName qname) {
        for (T el = this.getNextSibling(); el != null; el = el.getNextSibling()) {
            OMElement omel = (OMElement)el;
            if (!omel.getQName().equals(qname)) continue;
            return (T)this.getWrapped((Element)((Object)omel));
        }
        return null;
    }

    @Override
    public <T extends Element> T getFirstChild(QName qname) {
        return (T)this.getWrapped((Element)((Object)this.getFirstChildWithName(qname)));
    }

    @Override
    public Lang getLanguageTag() {
        String lang = this.getLanguage();
        return lang != null ? new Lang(lang) : null;
    }

    @Override
    public String getLanguage() {
        String lang = this.getAttributeValue(LANG);
        Object parent = this.getParentElement();
        return lang != null ? lang : (parent != null && parent instanceof Element ? ((Element)parent).getLanguage() : (parent != null && parent instanceof Document ? ((Document)parent).getLanguage() : null));
    }

    @Override
    public <T extends Element> T setLanguage(String language) {
        this.setAttributeValue(LANG, language);
        return (T)this;
    }

    @Override
    public IRI getBaseUri() {
        IRI uri = this._getUriValue(this.getAttributeValue(BASE));
        if (IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri)) {
            uri = null;
        }
        if (uri == null) {
            if (this.parent instanceof Element) {
                uri = ((Element)((Object)this.parent)).getBaseUri();
            } else if (this.parent instanceof Document) {
                uri = ((Document)((Object)this.parent)).getBaseUri();
            }
        }
        return uri;
    }

    @Override
    public IRI getResolvedBaseUri() {
        IRI baseUri = null;
        IRI uri = this._getUriValue(this.getAttributeValue(BASE));
        if (IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri)) {
            uri = null;
        }
        if (this.parent instanceof Element) {
            baseUri = ((Element)((Object)this.parent)).getResolvedBaseUri();
        } else if (this.parent instanceof Document) {
            baseUri = ((Document)((Object)this.parent)).getBaseUri();
        }
        if (uri != null && baseUri != null) {
            uri = baseUri.resolve(uri);
        } else if (uri == null) {
            uri = baseUri;
        }
        return uri;
    }

    @Override
    public <T extends Element> T setBaseUri(IRI base) {
        this.complete();
        this.setAttributeValue(BASE, this._getStringValue(base));
        return (T)this;
    }

    @Override
    public <T extends Element> T setBaseUri(String base) {
        this.setBaseUri(base != null ? new IRI(base) : null);
        return (T)this;
    }

    @Override
    public String getAttributeValue(QName qname) {
        OMAttribute attr = this.getAttribute(qname);
        String value = attr != null ? attr.getAttributeValue() : null;
        return this.getMustPreserveWhitespace() || value == null ? value : value.trim();
    }

    @Override
    public <T extends Element> T setAttributeValue(QName qname, String value) {
        OMAttribute attr = this.getAttribute(qname);
        if (attr != null && value != null) {
            attr.setAttributeValue(value);
        } else if (value != null) {
            String uri = qname.getNamespaceURI();
            String prefix = qname.getPrefix();
            if (uri != null) {
                OMNamespace ns = this.findNamespace(uri, prefix);
                if (ns == null) {
                    ns = this.factory.createOMNamespace(uri, prefix);
                }
                attr = this.factory.createOMAttribute(qname.getLocalPart(), ns, value);
            } else {
                attr = this.factory.createOMAttribute(qname.getLocalPart(), null, value);
            }
            if (attr != null) {
                this.addAttribute(attr);
            }
        } else if (attr != null) {
            this.removeAttribute(attr);
        }
        return (T)this;
    }

    protected <E extends Element> List<E> _getChildrenAsSet(QName qname) {
        FOMFactory factory = (FOMFactory)this.getFactory();
        return new FOMList(new FOMElementIteratorWrapper(factory, this.getChildrenWithName(qname)));
    }

    protected void _setChild(QName qname, OMElement element) {
        OMElement e = this.getFirstChildWithName(qname);
        if (e == null && element != null) {
            this.addChild(element);
        } else if (e != null && element != null) {
            e.insertSiblingBefore(element);
            e.discard();
        } else if (e != null && element == null) {
            e.discard();
        }
    }

    protected IRI _getUriValue(String v) {
        return v != null ? new IRI(v) : null;
    }

    protected String _getStringValue(IRI uri) {
        return uri != null ? uri.toString() : null;
    }

    protected IRI _resolve(IRI base, IRI value) {
        return base != null ? base.resolve(value) : value;
    }

    @Override
    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
        org.apache.abdera.writer.Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(Writer out, WriterOptions options) throws IOException {
        org.apache.abdera.writer.Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out) throws IOException {
        writer.writeTo((Base)this, out);
    }

    @Override
    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out) throws IOException {
        writer.writeTo((Base)this, out);
    }

    @Override
    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out, WriterOptions options) throws IOException {
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out, WriterOptions options) throws IOException {
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        Document doc = this.getDocument();
        String charset = doc != null ? doc.getCharset() : "UTF-8";
        org.apache.abdera.writer.Writer writer = this.getFactory().getAbdera().getWriter();
        this.writeTo(writer, (Writer)new OutputStreamWriter(out, charset));
    }

    @Override
    public void writeTo(Writer writer) throws IOException {
        org.apache.abdera.writer.Writer out = this.getFactory().getAbdera().getWriter();
        if (!(out instanceof FOMWriter)) {
            out.writeTo((Base)this, writer);
        } else {
            try {
                OMOutputFormat outputFormat = new OMOutputFormat();
                if (this.getDocument() != null && this.getDocument().getCharset() != null) {
                    outputFormat.setCharSetEncoding(this.getDocument().getCharset());
                }
                this.serialize(writer, outputFormat);
            }
            catch (XMLStreamException e) {
                throw new FOMException(e);
            }
        }
    }

    @Override
    public <T extends Element> Document<T> getDocument() {
        Document document = null;
        if (this.parent != null) {
            if (this.parent instanceof Element) {
                document = ((Element)((Object)this.parent)).getDocument();
            } else if (this.parent instanceof Document) {
                document = (Document)((Object)this.parent);
            }
        }
        return document;
    }

    @Override
    public String getAttributeValue(String name) {
        return this.getAttributeValue(new QName(name));
    }

    @Override
    public <T extends Element> T setAttributeValue(String name, String value) {
        this.setAttributeValue(new QName(name), value);
        return (T)this;
    }

    protected void _setElementValue(QName qname, String value) {
        this.complete();
        OMElement element = this.getFirstChildWithName(qname);
        if (element != null && value != null) {
            element.setText(value);
        } else if (element != null && value == null) {
            Iterator i = element.getChildren();
            while (i.hasNext()) {
                OMNode node = (OMNode)i.next();
                node.discard();
            }
        } else if (element == null && value != null) {
            element = this.factory.createOMElement(qname, this);
            element.setText(value);
            this.addChild(element);
        }
    }

    protected String _getElementValue(QName qname) {
        String value = null;
        OMElement element = this.getFirstChildWithName(qname);
        if (element != null) {
            value = element.getText();
        }
        return this.getMustPreserveWhitespace() || value == null ? value : value.trim();
    }

    protected <T extends Text> T getTextElement(QName qname) {
        return (T)((Text)((Object)this.getFirstChildWithName(qname)));
    }

    protected <T extends Text> void setTextElement(QName qname, T text, boolean many) {
        this.complete();
        if (text != null) {
            this._setChild(qname, (OMElement)((Object)text));
        } else {
            this._removeChildren(qname, false);
        }
    }

    protected Text setTextText(QName qname, String value) {
        if (value == null) {
            this.setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Text text = fomfactory.newText(qname, Text.Type.TEXT);
        text.setValue(value);
        this.setTextElement(qname, text, false);
        return text;
    }

    protected Text setHtmlText(QName qname, String value, IRI baseUri) {
        if (value == null) {
            this.setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Text text = fomfactory.newText(qname, Text.Type.HTML);
        if (baseUri != null) {
            text.setBaseUri(baseUri);
        }
        text.setValue(value);
        this.setTextElement(qname, text, false);
        return text;
    }

    protected Text setXhtmlText(QName qname, String value, IRI baseUri) {
        if (value == null) {
            this.setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Text text = fomfactory.newText(qname, Text.Type.XHTML);
        if (baseUri != null) {
            text.setBaseUri(baseUri);
        }
        text.setValue(value);
        this.setTextElement(qname, text, false);
        return text;
    }

    protected Text setXhtmlText(QName qname, Div value, IRI baseUri) {
        if (value == null) {
            this.setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Text text = fomfactory.newText(qname, Text.Type.XHTML);
        if (baseUri != null) {
            text.setBaseUri(baseUri);
        }
        text.setValueElement(value);
        this.setTextElement(qname, text, false);
        return text;
    }

    @Override
    public void setText(String text) {
        this.complete();
        if (text != null) {
            for (OMNode child = this.getFirstOMChild(); child != null; child = child.getNextOMSibling()) {
                if (child.getType() != 4) continue;
                child.detach();
            }
            this.getOMFactory().createOMText((OMContainer)this, text);
        } else {
            this._removeAllChildren();
        }
    }

    @Override
    public String getText() {
        StringBuilder buf = new StringBuilder();
        Iterator i = this.getChildren();
        while (i.hasNext()) {
            OMNode node = (OMNode)i.next();
            if (!(node instanceof OMText)) continue;
            buf.append(((OMText)node).getText());
        }
        String value = buf.toString();
        return this.getMustPreserveWhitespace() || value == null ? value : value.trim();
    }

    protected String getText(QName qname) {
        Object text = this.getTextElement(qname);
        return text != null ? text.getValue() : null;
    }

    @Override
    public List<QName> getAttributes() {
        ArrayList<QName> list = new ArrayList<QName>();
        Iterator i = this.getAllAttributes();
        while (i.hasNext()) {
            OMAttribute attr = (OMAttribute)i.next();
            list.add(attr.getQName());
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<QName> getExtensionAttributes() {
        ArrayList<QName> list = new ArrayList<QName>();
        Iterator i = this.getAllAttributes();
        while (i.hasNext()) {
            OMAttribute attr = (OMAttribute)i.next();
            String namespace = attr.getNamespace() != null ? attr.getNamespace().getNamespaceURI() : "";
            if (namespace.equals(this.getNamespace().getNamespaceURI()) || namespace.equals("")) continue;
            list.add(attr.getQName());
        }
        return Collections.unmodifiableList(list);
    }

    protected Element _parse(String value, IRI baseUri) throws ParseException, UnsupportedEncodingException {
        if (value == null) {
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Parser parser = fomfactory.newParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setFactory(fomfactory);
        Document doc = parser.parse(new StringReader(value), baseUri != null ? baseUri.toString() : null, options);
        return doc.getRoot();
    }

    @Override
    public <T extends Element> T removeAttribute(QName qname) {
        OMAttribute attr = this.getAttribute(qname);
        if (attr != null) {
            this.removeAttribute(attr);
        }
        return (T)this;
    }

    @Override
    public <T extends Element> T removeAttribute(String name) {
        OMAttribute attr = this.getAttribute(new QName(name));
        if (attr != null) {
            this.getAttribute(new QName(name));
        }
        return (T)this;
    }

    protected void _removeChildren(QName qname, boolean many) {
        this.complete();
        if (many) {
            Iterator i = this.getChildrenWithName(qname);
            while (i.hasNext()) {
                OMElement element = (OMElement)i.next();
                element.discard();
            }
        } else {
            OMElement element = this.getFirstChildWithName(qname);
            if (element != null) {
                element.discard();
            }
        }
    }

    protected void _removeAllChildren() {
        this.complete();
        Iterator i = this.getChildren();
        while (i.hasNext()) {
            OMNode node = (OMNode)i.next();
            node.discard();
        }
    }

    @Override
    public Object clone() {
        OMElement el = this._create(this);
        this._copyElement(this, el);
        return el;
    }

    protected OMElement _copyElement(OMElement src, OMElement dest) {
        Iterator i = src.getAllAttributes();
        while (i.hasNext()) {
            OMAttribute attr = (OMAttribute)i.next();
            dest.addAttribute(attr);
            dest.addAttribute(this.factory.createOMAttribute(attr.getLocalName(), attr.getNamespace(), attr.getAttributeValue()));
        }
        i = src.getChildren();
        while (i.hasNext()) {
            OMText text;
            OMNode node = (OMNode)i.next();
            if (node.getType() == 1) {
                OMElement element = (OMElement)node;
                OMElement child = this._create(element);
                if (child == null) continue;
                this._copyElement(element, child);
                dest.addChild(child);
                continue;
            }
            if (node.getType() == 12) {
                text = (OMText)node;
                this.factory.createOMText((OMContainer)dest, text.getText(), 12);
                continue;
            }
            if (node.getType() == 4) {
                text = (OMText)node;
                this.factory.createOMText((OMContainer)dest, text.getText());
                continue;
            }
            if (node.getType() == 5) {
                OMComment comment = (OMComment)node;
                this.factory.createOMComment(dest, comment.getValue());
                continue;
            }
            if (node.getType() == 3) {
                OMProcessingInstruction pi = (OMProcessingInstruction)node;
                this.factory.createOMProcessingInstruction(dest, pi.getTarget(), pi.getValue());
                continue;
            }
            if (node.getType() == 6) {
                text = (OMText)node;
                this.factory.createOMText((OMContainer)dest, text.getText(), 6);
                continue;
            }
            if (node.getType() != 9) continue;
            text = (OMText)node;
            this.factory.createOMText((OMContainer)dest, text.getText(), 9);
        }
        return dest;
    }

    protected OMElement _create(OMElement src) {
        OMElement el = null;
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Enum obj = null;
        if (src instanceof Content) {
            obj = ((Content)((Object)src)).getContentType();
        }
        if (src instanceof Text) {
            obj = ((Text)((Object)src)).getTextType();
        }
        el = fomfactory.createElement(src.getQName(), (OMContainer)((Object)fomfactory.newDocument()), this.factory, obj);
        return el;
    }

    @Override
    public Factory getFactory() {
        return (Factory)((Object)this.factory);
    }

    @Override
    public <T extends Base> T addComment(String value) {
        this.factory.createOMComment(this, value);
        return (T)this;
    }

    @Override
    public Locale getLocale() {
        String tag = this.getLanguage();
        if (tag == null || tag.length() == 0) {
            return null;
        }
        String[] tokens = tag.split("-");
        Locale locale = null;
        switch (tokens.length) {
            case 0: {
                break;
            }
            case 1: {
                locale = new Locale(tokens[0]);
                break;
            }
            case 2: {
                locale = new Locale(tokens[0], tokens[1]);
                break;
            }
            default: {
                locale = new Locale(tokens[0], tokens[1], tokens[2]);
            }
        }
        return locale;
    }

    protected Link selectLink(List<Link> links, String type, String hreflang) {
        for (Link link : links) {
            boolean langmatch;
            MimeType mt = link.getMimeType();
            boolean typematch = MimeTypeHelper.isMatch(mt != null ? mt.toString() : null, type);
            boolean bl = "*".equals(hreflang) || (hreflang != null ? hreflang.equals(link.getHrefLang()) : link.getHrefLang() == null) ? true : (langmatch = false);
            if (!typematch || !langmatch) continue;
            return link;
        }
        return null;
    }

    @Override
    public <T extends Element> T declareNS(String uri, String prefix) {
        if (!this.isDeclared(uri, prefix)) {
            super.declareNamespace(uri, prefix);
        }
        return (T)this;
    }

    protected boolean isDeclared(String ns, String prefix) {
        Iterator i = this.getAllDeclaredNamespaces();
        while (i.hasNext()) {
            OMNamespace omn = (OMNamespace)i.next();
            if (!omn.getNamespaceURI().equals(ns) || omn.getPrefix() == null || !omn.getPrefix().equals(prefix)) continue;
            return true;
        }
        Object parent = this.getParentElement();
        if (parent != null && parent instanceof FOMElement) {
            return ((FOMElement)parent).isDeclared(ns, prefix);
        }
        return false;
    }

    protected void declareIfNecessary(String ns, String prefix) {
        if (prefix != null && !prefix.equals("") && !this.isDeclared(ns, prefix)) {
            this.declareNS(ns, prefix);
        }
    }

    @Override
    public Map<String, String> getNamespaces() {
        HashMap<String, String> namespaces = new HashMap<String, String>();
        OMElement current = this;
        while (current != null) {
            Iterator i = current.getAllDeclaredNamespaces();
            while (i.hasNext()) {
                OMNamespace ns = (OMNamespace)i.next();
                String prefix = ns.getPrefix();
                String uri = ns.getNamespaceURI();
                if (namespaces.containsKey(prefix)) continue;
                namespaces.put(prefix, uri);
            }
            OMContainer parent = current.getParent();
            current = (OMElement)(parent != null && parent instanceof OMElement ? parent : null);
        }
        return Collections.unmodifiableMap(namespaces);
    }

    @Override
    public <T extends Element> List<T> getElements() {
        return new FOMList(new FOMElementIteratorWrapper((FOMFactory)this.factory, this.getChildElements()));
    }

    @Override
    public boolean getMustPreserveWhitespace() {
        OMAttribute attr = this.getAttribute(SPACE);
        String space = attr != null ? attr.getAttributeValue() : null;
        Object parent = this.getParentElement();
        return space != null && space.equalsIgnoreCase("preserve") ? true : (parent != null && parent instanceof Element ? ((Element)parent).getMustPreserveWhitespace() : (parent != null && parent instanceof Document ? ((Document)parent).getMustPreserveWhitespace() : true));
    }

    @Override
    public <T extends Element> T setMustPreserveWhitespace(boolean preserve) {
        if (preserve && !this.getMustPreserveWhitespace()) {
            this.setAttributeValue(SPACE, "preserve");
        } else if (!preserve && this.getMustPreserveWhitespace()) {
            this.setAttributeValue(SPACE, "default");
        }
        return (T)this;
    }

    @Override
    public <T extends Element> T setText(DataHandler handler) {
        this._removeAllChildren();
        this.addChild(this.factory.createOMText(handler, true));
        return (T)this;
    }

    @Override
    public WriterOptions getDefaultWriterOptions() {
        return new FOMWriter().getDefaultWriterOptions();
    }

    @Override
    public <T extends Base> T complete() {
        if (!this.isComplete() && this.builder != null) {
            super.build();
        }
        return (T)this;
    }

    @Override
    public Iterator<Element> iterator() {
        return this.getElements().iterator();
    }

    @Override
    public void writeTo(String writer, OutputStream out) throws IOException {
        this.writeTo((org.apache.abdera.writer.Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    @Override
    public void writeTo(String writer, Writer out) throws IOException {
        this.writeTo((org.apache.abdera.writer.Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    @Override
    public void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException {
        this.writeTo((org.apache.abdera.writer.Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    @Override
    public void writeTo(String writer, Writer out, WriterOptions options) throws IOException {
        this.writeTo((org.apache.abdera.writer.Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    public String toFormattedString() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            this.writeTo("prettyxml", (OutputStream)out);
            return new String(out.toByteArray(), "UTF-8");
        }
        catch (Exception e) {
            return this.toString();
        }
    }
}

