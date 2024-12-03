/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.Verifier;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.Walker;
import org.jdom2.output.support.XMLOutputProcessor;
import org.jdom2.util.NamespaceStack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractXMLOutputProcessor
extends AbstractOutputProcessor
implements XMLOutputProcessor {
    protected static final String CDATAPRE = "<![CDATA[";
    protected static final String CDATAPOST = "]]>";

    @Override
    public void process(Writer out, Format format, Document doc) throws IOException {
        this.printDocument(out, new FormatStack(format), new NamespaceStack(), doc);
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, DocType doctype) throws IOException {
        this.printDocType(out, new FormatStack(format), doctype);
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, Element element) throws IOException {
        this.printElement(out, new FormatStack(format), new NamespaceStack(), element);
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, List<? extends Content> list) throws IOException {
        FormatStack fstack = new FormatStack(format);
        Walker walker = this.buildWalker(fstack, list, true);
        this.printContent(out, fstack, new NamespaceStack(), walker);
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, CDATA cdata) throws IOException {
        FormatStack fstack = new FormatStack(format);
        List<CDATA> list = Collections.singletonList(cdata);
        Walker walker = this.buildWalker(fstack, list, true);
        if (walker.hasNext()) {
            this.printContent(out, fstack, new NamespaceStack(), walker);
        }
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, Text text) throws IOException {
        FormatStack fstack = new FormatStack(format);
        List<Text> list = Collections.singletonList(text);
        Walker walker = this.buildWalker(fstack, list, true);
        if (walker.hasNext()) {
            this.printContent(out, fstack, new NamespaceStack(), walker);
        }
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, Comment comment) throws IOException {
        this.printComment(out, new FormatStack(format), comment);
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, ProcessingInstruction pi) throws IOException {
        FormatStack fstack = new FormatStack(format);
        fstack.setIgnoreTrAXEscapingPIs(true);
        this.printProcessingInstruction(out, fstack, pi);
        out.flush();
    }

    @Override
    public void process(Writer out, Format format, EntityRef entity) throws IOException {
        this.printEntityRef(out, new FormatStack(format), entity);
        out.flush();
    }

    protected void write(Writer out, String str) throws IOException {
        if (str == null) {
            return;
        }
        out.write(str);
    }

    protected void write(Writer out, char c) throws IOException {
        out.write(c);
    }

    protected void attributeEscapedEntitiesFilter(Writer out, FormatStack fstack, String value) throws IOException {
        if (!fstack.getEscapeOutput()) {
            this.write(out, value);
            return;
        }
        this.write(out, Format.escapeAttribute(fstack.getEscapeStrategy(), value));
    }

    protected void textRaw(Writer out, String str) throws IOException {
        this.write(out, str);
    }

    protected void textRaw(Writer out, char ch) throws IOException {
        this.write(out, ch);
    }

    protected void textEntityRef(Writer out, String name) throws IOException {
        this.textRaw(out, '&');
        this.textRaw(out, name);
        this.textRaw(out, ';');
    }

    protected void textCDATA(Writer out, String text) throws IOException {
        this.textRaw(out, CDATAPRE);
        this.textRaw(out, text);
        this.textRaw(out, CDATAPOST);
    }

    protected void printDocument(Writer out, FormatStack fstack, NamespaceStack nstack, Document doc) throws IOException {
        ArrayList<Content> list;
        ArrayList<Content> arrayList = list = doc.hasRootElement() ? doc.getContent() : new ArrayList<Content>(doc.getContentSize());
        if (list.isEmpty()) {
            int sz = doc.getContentSize();
            for (int i = 0; i < sz; ++i) {
                list.add(doc.getContent(i));
            }
        }
        this.printDeclaration(out, fstack);
        Walker walker = this.buildWalker(fstack, list, true);
        if (walker.hasNext()) {
            while (walker.hasNext()) {
                String padding;
                Content c = walker.next();
                if (c == null) {
                    padding = walker.text();
                    if (padding == null || !Verifier.isAllXMLWhitespace(padding) || walker.isCDATA()) continue;
                    this.write(out, padding);
                    continue;
                }
                switch (c.getCType()) {
                    case Comment: {
                        this.printComment(out, fstack, (Comment)c);
                        break;
                    }
                    case DocType: {
                        this.printDocType(out, fstack, (DocType)c);
                        break;
                    }
                    case Element: {
                        this.printElement(out, fstack, nstack, (Element)c);
                        break;
                    }
                    case ProcessingInstruction: {
                        this.printProcessingInstruction(out, fstack, (ProcessingInstruction)c);
                        break;
                    }
                    case Text: {
                        padding = ((Text)c).getText();
                        if (padding == null || !Verifier.isAllXMLWhitespace(padding)) break;
                        this.write(out, padding);
                    }
                }
            }
            if (fstack.getLineSeparator() != null) {
                this.write(out, fstack.getLineSeparator());
            }
        }
    }

    protected void printDeclaration(Writer out, FormatStack fstack) throws IOException {
        if (fstack.isOmitDeclaration()) {
            return;
        }
        if (fstack.isOmitEncoding()) {
            this.write(out, "<?xml version=\"1.0\"?>");
        } else {
            this.write(out, "<?xml version=\"1.0\"");
            this.write(out, " encoding=\"");
            this.write(out, fstack.getEncoding());
            this.write(out, "\"?>");
        }
        this.write(out, fstack.getLineSeparator());
    }

    protected void printDocType(Writer out, FormatStack fstack, DocType docType) throws IOException {
        String publicID = docType.getPublicID();
        String systemID = docType.getSystemID();
        String internalSubset = docType.getInternalSubset();
        boolean hasPublic = false;
        this.write(out, "<!DOCTYPE ");
        this.write(out, docType.getElementName());
        if (publicID != null) {
            this.write(out, " PUBLIC \"");
            this.write(out, publicID);
            this.write(out, "\"");
            hasPublic = true;
        }
        if (systemID != null) {
            if (!hasPublic) {
                this.write(out, " SYSTEM");
            }
            this.write(out, " \"");
            this.write(out, systemID);
            this.write(out, "\"");
        }
        if (internalSubset != null && !internalSubset.equals("")) {
            this.write(out, " [");
            this.write(out, fstack.getLineSeparator());
            this.write(out, docType.getInternalSubset());
            this.write(out, "]");
        }
        this.write(out, ">");
    }

    protected void printProcessingInstruction(Writer out, FormatStack fstack, ProcessingInstruction pi) throws IOException {
        String target = pi.getTarget();
        boolean piProcessed = false;
        if (!fstack.isIgnoreTrAXEscapingPIs()) {
            if (target.equals("javax.xml.transform.disable-output-escaping")) {
                fstack.setEscapeOutput(false);
                piProcessed = true;
            } else if (target.equals("javax.xml.transform.enable-output-escaping")) {
                fstack.setEscapeOutput(true);
                piProcessed = true;
            }
        }
        if (!piProcessed) {
            String rawData = pi.getData();
            if (!"".equals(rawData)) {
                this.write(out, "<?");
                this.write(out, target);
                this.write(out, " ");
                this.write(out, rawData);
                this.write(out, "?>");
            } else {
                this.write(out, "<?");
                this.write(out, target);
                this.write(out, "?>");
            }
        }
    }

    protected void printComment(Writer out, FormatStack fstack, Comment comment) throws IOException {
        this.write(out, "<!--");
        this.write(out, comment.getText());
        this.write(out, "-->");
    }

    protected void printEntityRef(Writer out, FormatStack fstack, EntityRef entity) throws IOException {
        this.textEntityRef(out, entity.getName());
    }

    protected void printCDATA(Writer out, FormatStack fstack, CDATA cdata) throws IOException {
        this.textCDATA(out, cdata.getText());
    }

    protected void printText(Writer out, FormatStack fstack, Text text) throws IOException {
        if (fstack.getEscapeOutput()) {
            this.textRaw(out, Format.escapeText(fstack.getEscapeStrategy(), fstack.getLineSeparator(), text.getText()));
            return;
        }
        this.textRaw(out, text.getText());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void printElement(Writer out, FormatStack fstack, NamespaceStack nstack, Element element) throws IOException {
        nstack.push(element);
        try {
            List<Content> content = element.getContent();
            this.write(out, "<");
            this.write(out, element.getQualifiedName());
            for (Namespace ns : nstack.addedForward()) {
                this.printNamespace(out, fstack, ns);
            }
            if (element.hasAttributes()) {
                for (Attribute attribute : element.getAttributes()) {
                    this.printAttribute(out, fstack, attribute);
                }
            }
            if (content.isEmpty()) {
                if (fstack.isExpandEmptyElements()) {
                    this.write(out, "></");
                    this.write(out, element.getQualifiedName());
                    this.write(out, ">");
                } else {
                    this.write(out, " />");
                }
                return;
            }
            fstack.push();
            try {
                String space = element.getAttributeValue("space", Namespace.XML_NAMESPACE);
                if ("default".equals(space)) {
                    fstack.setTextMode(fstack.getDefaultMode());
                } else if ("preserve".equals(space)) {
                    fstack.setTextMode(Format.TextMode.PRESERVE);
                }
                Walker walker = this.buildWalker(fstack, content, true);
                if (!walker.hasNext()) {
                    if (fstack.isExpandEmptyElements()) {
                        this.write(out, "></");
                        this.write(out, element.getQualifiedName());
                        this.write(out, ">");
                    } else {
                        this.write(out, " />");
                    }
                    return;
                }
                this.write(out, ">");
                if (!walker.isAllText()) {
                    this.textRaw(out, fstack.getPadBetween());
                }
                this.printContent(out, fstack, nstack, walker);
                if (!walker.isAllText()) {
                    this.textRaw(out, fstack.getPadLast());
                }
                this.write(out, "</");
                this.write(out, element.getQualifiedName());
                this.write(out, ">");
            }
            finally {
                fstack.pop();
            }
        }
        finally {
            nstack.pop();
        }
    }

    protected void printContent(Writer out, FormatStack fstack, NamespaceStack nstack, Walker walker) throws IOException {
        while (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                String t = walker.text();
                if (walker.isCDATA()) {
                    this.textCDATA(out, t);
                    continue;
                }
                this.textRaw(out, t);
                continue;
            }
            switch (c.getCType()) {
                case CDATA: {
                    this.printCDATA(out, fstack, (CDATA)c);
                    break;
                }
                case Comment: {
                    this.printComment(out, fstack, (Comment)c);
                    break;
                }
                case DocType: {
                    this.printDocType(out, fstack, (DocType)c);
                    break;
                }
                case Element: {
                    this.printElement(out, fstack, nstack, (Element)c);
                    break;
                }
                case EntityRef: {
                    this.printEntityRef(out, fstack, (EntityRef)c);
                    break;
                }
                case ProcessingInstruction: {
                    this.printProcessingInstruction(out, fstack, (ProcessingInstruction)c);
                    break;
                }
                case Text: {
                    this.printText(out, fstack, (Text)c);
                }
            }
        }
    }

    protected void printNamespace(Writer out, FormatStack fstack, Namespace ns) throws IOException {
        String prefix = ns.getPrefix();
        String uri = ns.getURI();
        this.write(out, " xmlns");
        if (!prefix.equals("")) {
            this.write(out, ":");
            this.write(out, prefix);
        }
        this.write(out, "=\"");
        this.attributeEscapedEntitiesFilter(out, fstack, uri);
        this.write(out, "\"");
    }

    protected void printAttribute(Writer out, FormatStack fstack, Attribute attribute) throws IOException {
        if (!attribute.isSpecified() && fstack.isSpecifiedAttributesOnly()) {
            return;
        }
        this.write(out, " ");
        this.write(out, attribute.getQualifiedName());
        this.write(out, "=");
        this.write(out, "\"");
        this.attributeEscapedEntitiesFilter(out, fstack, attribute.getValue());
        this.write(out, "\"");
    }
}

