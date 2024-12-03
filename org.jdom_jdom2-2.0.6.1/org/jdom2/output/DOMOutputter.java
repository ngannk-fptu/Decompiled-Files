/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.adapters.DOMAdapter;
import org.jdom2.adapters.JAXPDOMAdapter;
import org.jdom2.internal.ReflectionConstructor;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractDOMOutputProcessor;
import org.jdom2.output.support.DOMOutputProcessor;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DOMOutputter {
    private static final DOMAdapter DEFAULT_ADAPTER = new JAXPDOMAdapter();
    private static final DOMOutputProcessor DEFAULT_PROCESSOR = new DefaultDOMOutputProcessor();
    private DOMAdapter adapter;
    private Format format;
    private DOMOutputProcessor processor;

    public DOMOutputter() {
        this(null, null, null);
    }

    public DOMOutputter(DOMOutputProcessor processor) {
        this(null, null, processor);
    }

    public DOMOutputter(DOMAdapter adapter, Format format, DOMOutputProcessor processor) {
        this.adapter = adapter == null ? DEFAULT_ADAPTER : adapter;
        this.format = format == null ? Format.getRawFormat() : format;
        this.processor = processor == null ? DEFAULT_PROCESSOR : processor;
    }

    @Deprecated
    public DOMOutputter(String adapterClass) {
        this.adapter = adapterClass == null ? DEFAULT_ADAPTER : ReflectionConstructor.construct(adapterClass, DOMAdapter.class);
    }

    public DOMOutputter(DOMAdapter adapter) {
        this.adapter = adapter == null ? DEFAULT_ADAPTER : adapter;
    }

    public DOMAdapter getDOMAdapter() {
        return this.adapter;
    }

    public void setDOMAdapter(DOMAdapter adapter) {
        this.adapter = adapter == null ? DEFAULT_ADAPTER : adapter;
    }

    public Format getFormat() {
        return this.format;
    }

    public void setFormat(Format format) {
        this.format = format == null ? Format.getRawFormat() : format;
    }

    public DOMOutputProcessor getDOMOutputProcessor() {
        return this.processor;
    }

    public void setDOMOutputProcessor(DOMOutputProcessor processor) {
        this.processor = processor == null ? DEFAULT_PROCESSOR : processor;
    }

    @Deprecated
    public void setForceNamespaceAware(boolean flag) {
    }

    @Deprecated
    public boolean getForceNamespaceAware() {
        return true;
    }

    public org.w3c.dom.Document output(Document document) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(document.getDocType()), this.format, document);
    }

    public DocumentType output(DocType doctype) throws JDOMException {
        return this.adapter.createDocument(doctype).getDoctype();
    }

    public org.w3c.dom.Element output(Element element) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, element);
    }

    public org.w3c.dom.Text output(Text text) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, text);
    }

    public CDATASection output(CDATA cdata) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, cdata);
    }

    public org.w3c.dom.ProcessingInstruction output(ProcessingInstruction pi) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, pi);
    }

    public org.w3c.dom.Comment output(Comment comment) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, comment);
    }

    public EntityReference output(EntityRef entity) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, entity);
    }

    public Attr output(Attribute attribute) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, attribute);
    }

    public List<Node> output(List<? extends Content> list) throws JDOMException {
        return this.processor.process(this.adapter.createDocument(), this.format, list);
    }

    public org.w3c.dom.Element output(org.w3c.dom.Document basedoc, Element element) throws JDOMException {
        return this.processor.process(basedoc, this.format, element);
    }

    public org.w3c.dom.Text output(org.w3c.dom.Document basedoc, Text text) throws JDOMException {
        return this.processor.process(basedoc, this.format, text);
    }

    public CDATASection output(org.w3c.dom.Document basedoc, CDATA cdata) throws JDOMException {
        return this.processor.process(basedoc, this.format, cdata);
    }

    public org.w3c.dom.ProcessingInstruction output(org.w3c.dom.Document basedoc, ProcessingInstruction pi) throws JDOMException {
        return this.processor.process(basedoc, this.format, pi);
    }

    public org.w3c.dom.Comment output(org.w3c.dom.Document basedoc, Comment comment) throws JDOMException {
        return this.processor.process(basedoc, this.format, comment);
    }

    public EntityReference output(org.w3c.dom.Document basedoc, EntityRef entity) throws JDOMException {
        return this.processor.process(basedoc, this.format, entity);
    }

    public Attr output(org.w3c.dom.Document basedoc, Attribute attribute) throws JDOMException {
        return this.processor.process(basedoc, this.format, attribute);
    }

    public List<Node> output(org.w3c.dom.Document basedoc, List<? extends Content> list) throws JDOMException {
        return this.processor.process(basedoc, this.format, list);
    }

    private static final class DefaultDOMOutputProcessor
    extends AbstractDOMOutputProcessor {
        private DefaultDOMOutputProcessor() {
        }
    }
}

