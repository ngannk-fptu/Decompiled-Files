/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractOutputProcessor;
import org.jdom2.output.support.DOMOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.Walker;
import org.jdom2.util.NamespaceStack;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractDOMOutputProcessor
extends AbstractOutputProcessor
implements DOMOutputProcessor {
    private static String getXmlnsTagFor(Namespace ns) {
        String attrName = "xmlns";
        if (!ns.getPrefix().equals("")) {
            attrName = attrName + ":";
            attrName = attrName + ns.getPrefix();
        }
        return attrName;
    }

    @Override
    public org.w3c.dom.Document process(org.w3c.dom.Document basedoc, Format format, Document doc) {
        return this.printDocument(new FormatStack(format), new NamespaceStack(), basedoc, doc);
    }

    @Override
    public org.w3c.dom.Element process(org.w3c.dom.Document basedoc, Format format, Element element) {
        return this.printElement(new FormatStack(format), new NamespaceStack(), basedoc, element);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Node> process(org.w3c.dom.Document basedoc, Format format, List<? extends Content> list) {
        ArrayList<Node> ret = new ArrayList<Node>(list.size());
        FormatStack fstack = new FormatStack(format);
        NamespaceStack nstack = new NamespaceStack();
        for (Content content : list) {
            fstack.push();
            try {
                Node node = this.helperContentDispatcher(fstack, nstack, basedoc, content);
                if (node == null) continue;
                ret.add(node);
            }
            finally {
                fstack.pop();
            }
        }
        return ret;
    }

    @Override
    public CDATASection process(org.w3c.dom.Document basedoc, Format format, CDATA cdata) {
        FormatStack fstack = new FormatStack(format);
        List<CDATA> list = Collections.singletonList(cdata);
        Walker walker = this.buildWalker(fstack, list, false);
        if (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                return this.printCDATA(fstack, basedoc, new CDATA(walker.text()));
            }
            if (c.getCType() == Content.CType.CDATA) {
                return this.printCDATA(fstack, basedoc, (CDATA)c);
            }
        }
        return null;
    }

    @Override
    public org.w3c.dom.Text process(org.w3c.dom.Document basedoc, Format format, Text text) {
        FormatStack fstack = new FormatStack(format);
        List<Text> list = Collections.singletonList(text);
        Walker walker = this.buildWalker(fstack, list, false);
        if (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                return this.printText(fstack, basedoc, new Text(walker.text()));
            }
            if (c.getCType() == Content.CType.Text) {
                return this.printText(fstack, basedoc, (Text)c);
            }
        }
        return null;
    }

    @Override
    public org.w3c.dom.Comment process(org.w3c.dom.Document basedoc, Format format, Comment comment) {
        return this.printComment(new FormatStack(format), basedoc, comment);
    }

    @Override
    public org.w3c.dom.ProcessingInstruction process(org.w3c.dom.Document basedoc, Format format, ProcessingInstruction pi) {
        return this.printProcessingInstruction(new FormatStack(format), basedoc, pi);
    }

    @Override
    public EntityReference process(org.w3c.dom.Document basedoc, Format format, EntityRef entity) {
        return this.printEntityRef(new FormatStack(format), basedoc, entity);
    }

    @Override
    public Attr process(org.w3c.dom.Document basedoc, Format format, Attribute attribute) {
        return this.printAttribute(new FormatStack(format), basedoc, attribute);
    }

    protected org.w3c.dom.Document printDocument(FormatStack fstack, NamespaceStack nstack, org.w3c.dom.Document basedoc, Document doc) {
        int sz;
        if (!fstack.isOmitDeclaration()) {
            basedoc.setXmlVersion("1.0");
        }
        if ((sz = doc.getContentSize()) > 0) {
            for (int i = 0; i < sz; ++i) {
                Content c = doc.getContent(i);
                Node n = null;
                switch (c.getCType()) {
                    case Comment: {
                        n = this.printComment(fstack, basedoc, (Comment)c);
                        break;
                    }
                    case DocType: {
                        break;
                    }
                    case Element: {
                        n = this.printElement(fstack, nstack, basedoc, (Element)c);
                        break;
                    }
                    case ProcessingInstruction: {
                        n = this.printProcessingInstruction(fstack, basedoc, (ProcessingInstruction)c);
                        break;
                    }
                }
                if (n == null) continue;
                basedoc.appendChild(n);
            }
        }
        return basedoc;
    }

    protected org.w3c.dom.ProcessingInstruction printProcessingInstruction(FormatStack fstack, org.w3c.dom.Document basedoc, ProcessingInstruction pi) {
        String target = pi.getTarget();
        String rawData = pi.getData();
        if (rawData == null || rawData.trim().length() == 0) {
            rawData = "";
        }
        return basedoc.createProcessingInstruction(target, rawData);
    }

    protected org.w3c.dom.Comment printComment(FormatStack fstack, org.w3c.dom.Document basedoc, Comment comment) {
        return basedoc.createComment(comment.getText());
    }

    protected EntityReference printEntityRef(FormatStack fstack, org.w3c.dom.Document basedoc, EntityRef entity) {
        return basedoc.createEntityReference(entity.getName());
    }

    protected CDATASection printCDATA(FormatStack fstack, org.w3c.dom.Document basedoc, CDATA cdata) {
        return basedoc.createCDATASection(cdata.getText());
    }

    protected org.w3c.dom.Text printText(FormatStack fstack, org.w3c.dom.Document basedoc, Text text) {
        return basedoc.createTextNode(text.getText());
    }

    protected Attr printAttribute(FormatStack fstack, org.w3c.dom.Document basedoc, Attribute attribute) {
        if (!attribute.isSpecified() && fstack.isSpecifiedAttributesOnly()) {
            return null;
        }
        Attr attr = basedoc.createAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName());
        attr.setValue(attribute.getValue());
        return attr;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected org.w3c.dom.Element printElement(FormatStack fstack, NamespaceStack nstack, org.w3c.dom.Document basedoc, Element element) {
        nstack.push(element);
        try {
            List<Content> content;
            Format.TextMode textmode = fstack.getTextMode();
            String space = element.getAttributeValue("space", Namespace.XML_NAMESPACE);
            if ("default".equals(space)) {
                textmode = fstack.getDefaultMode();
            } else if ("preserve".equals(space)) {
                textmode = Format.TextMode.PRESERVE;
            }
            org.w3c.dom.Element ret = basedoc.createElementNS(element.getNamespaceURI(), element.getQualifiedName());
            for (Namespace ns : nstack.addedForward()) {
                if (ns == Namespace.XML_NAMESPACE) continue;
                ret.setAttributeNS("http://www.w3.org/2000/xmlns/", AbstractDOMOutputProcessor.getXmlnsTagFor(ns), ns.getURI());
            }
            if (element.hasAttributes()) {
                for (Attribute att : element.getAttributes()) {
                    Attr a = this.printAttribute(fstack, basedoc, att);
                    if (a == null) continue;
                    ret.setAttributeNodeNS(a);
                }
            }
            if (!(content = element.getContent()).isEmpty()) {
                fstack.push();
                try {
                    org.w3c.dom.Text n;
                    fstack.setTextMode(textmode);
                    Walker walker = this.buildWalker(fstack, content, false);
                    if (!walker.isAllText() && fstack.getPadBetween() != null) {
                        n = basedoc.createTextNode(fstack.getPadBetween());
                        ret.appendChild(n);
                    }
                    this.printContent(fstack, nstack, basedoc, ret, walker);
                    if (!walker.isAllText() && fstack.getPadLast() != null) {
                        n = basedoc.createTextNode(fstack.getPadLast());
                        ret.appendChild(n);
                    }
                }
                finally {
                    fstack.pop();
                }
            }
            org.w3c.dom.Element element2 = ret;
            return element2;
        }
        finally {
            nstack.pop();
        }
    }

    protected void printContent(FormatStack fstack, NamespaceStack nstack, org.w3c.dom.Document basedoc, Node target, Walker walker) {
        while (walker.hasNext()) {
            Content c = walker.next();
            Node n = null;
            if (c == null) {
                String text = walker.text();
                n = walker.isCDATA() ? this.printCDATA(fstack, basedoc, new CDATA(text)) : this.printText(fstack, basedoc, new Text(text));
            } else {
                n = this.helperContentDispatcher(fstack, nstack, basedoc, c);
            }
            if (n == null) continue;
            target.appendChild(n);
        }
    }

    protected Node helperContentDispatcher(FormatStack fstack, NamespaceStack nstack, org.w3c.dom.Document basedoc, Content content) {
        switch (content.getCType()) {
            case CDATA: {
                return this.printCDATA(fstack, basedoc, (CDATA)content);
            }
            case Comment: {
                return this.printComment(fstack, basedoc, (Comment)content);
            }
            case Element: {
                return this.printElement(fstack, nstack, basedoc, (Element)content);
            }
            case EntityRef: {
                return this.printEntityRef(fstack, basedoc, (EntityRef)content);
            }
            case ProcessingInstruction: {
                return this.printProcessingInstruction(fstack, basedoc, (ProcessingInstruction)content);
            }
            case Text: {
                return this.printText(fstack, basedoc, (Text)content);
            }
            case DocType: {
                return null;
            }
        }
        throw new IllegalStateException("Unexpected Content " + (Object)((Object)content.getCType()));
    }
}

