/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.Verifier;
import org.jdom2.input.stax.DTDParser;
import org.jdom2.input.stax.StAXFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StAXStreamBuilder {
    private JDOMFactory builderfactory = new DefaultJDOMFactory();

    private static final Document process(JDOMFactory factory, XMLStreamReader stream) throws JDOMException {
        try {
            int state = stream.getEventType();
            if (7 != state) {
                throw new JDOMException("JDOM requires that XMLStreamReaders are at their beginning when being processed.");
            }
            Document document = factory.document(null);
            while (state != 8) {
                switch (state) {
                    case 7: {
                        document.setBaseURI(stream.getLocation().getSystemId());
                        document.setProperty("ENCODING_SCHEME", stream.getCharacterEncodingScheme());
                        document.setProperty("STANDALONE", String.valueOf(stream.isStandalone()));
                        document.setProperty("ENCODING", stream.getEncoding());
                        break;
                    }
                    case 11: {
                        document.setDocType(DTDParser.parse(stream.getText(), factory));
                        break;
                    }
                    case 1: {
                        document.setRootElement(StAXStreamBuilder.processElementFragment(factory, stream));
                        break;
                    }
                    case 2: {
                        throw new JDOMException("Unexpected XMLStream event at Document level: END_ELEMENT");
                    }
                    case 9: {
                        throw new JDOMException("Unexpected XMLStream event at Document level: ENTITY_REFERENCE");
                    }
                    case 12: {
                        throw new JDOMException("Unexpected XMLStream event at Document level: CDATA");
                    }
                    case 6: {
                        document.addContent(factory.text(stream.getText()));
                        break;
                    }
                    case 4: {
                        String badtxt = stream.getText();
                        if (Verifier.isAllXMLWhitespace(badtxt)) break;
                        throw new JDOMException("Unexpected XMLStream event at Document level: CHARACTERS (" + badtxt + ")");
                    }
                    case 5: {
                        document.addContent(factory.comment(stream.getText()));
                        break;
                    }
                    case 3: {
                        document.addContent(factory.processingInstruction(stream.getPITarget(), stream.getPIData()));
                        break;
                    }
                    default: {
                        throw new JDOMException("Unexpected XMLStream event " + state);
                    }
                }
                if (stream.hasNext()) {
                    state = stream.next();
                    continue;
                }
                throw new JDOMException("Unexpected end-of-XMLStreamReader");
            }
            return document;
        }
        catch (XMLStreamException xse) {
            throw new JDOMException("Unable to process XMLStream. See Cause.", xse);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<Content> processFragments(JDOMFactory factory, XMLStreamReader stream, StAXFilter filter) throws JDOMException {
        int state = stream.getEventType();
        if (7 != state) {
            throw new JDOMException("JDOM requires that XMLStreamReaders are at their beginning when being processed.");
        }
        ArrayList<Content> ret = new ArrayList<Content>();
        int depth = 0;
        String text = null;
        try {
            block14: while (stream.hasNext()) {
                state = stream.next();
                if (state == 8) return ret;
                switch (state) {
                    case 7: {
                        throw new JDOMException("Illegal state for XMLStreamReader. Cannot get XML Fragment for state START_DOCUMENT");
                    }
                    case 8: {
                        throw new JDOMException("Illegal state for XMLStreamReader. Cannot get XML Fragment for state END_DOCUMENT");
                    }
                    case 2: {
                        throw new JDOMException("Illegal state for XMLStreamReader. Cannot get XML Fragment for state END_ELEMENT");
                    }
                    case 1: {
                        QName qn = stream.getName();
                        if (filter.includeElement(depth, qn.getLocalPart(), Namespace.getNamespace(qn.getPrefix(), qn.getNamespaceURI()))) {
                            ret.add(StAXStreamBuilder.processPrunableElement(factory, stream, depth, filter));
                            continue block14;
                        }
                        int back = depth++;
                        while (true) {
                            if (depth <= back || !stream.hasNext()) continue block14;
                            state = stream.next();
                            if (state == 1) {
                                ++depth;
                                continue;
                            }
                            if (state != 2) continue;
                            --depth;
                        }
                    }
                    case 11: {
                        if (!filter.includeDocType()) continue block14;
                        ret.add(DTDParser.parse(stream.getText(), factory));
                        continue block14;
                    }
                    case 12: {
                        text = filter.includeCDATA(depth, stream.getText());
                        if (text == null) continue block14;
                        ret.add(factory.cdata(text));
                        continue block14;
                    }
                    case 4: 
                    case 6: {
                        text = filter.includeText(depth, stream.getText());
                        if (text == null) continue block14;
                        ret.add(factory.text(text));
                        continue block14;
                    }
                    case 5: {
                        text = filter.includeComment(depth, stream.getText());
                        if (text == null) continue block14;
                        ret.add(factory.comment(text));
                        continue block14;
                    }
                    case 9: {
                        if (!filter.includeEntityRef(depth, stream.getLocalName())) continue block14;
                        ret.add(factory.entityRef(stream.getLocalName()));
                        continue block14;
                    }
                    case 3: {
                        if (!filter.includeProcessingInstruction(depth, stream.getPITarget())) continue block14;
                        ret.add(factory.processingInstruction(stream.getPITarget(), stream.getPIData()));
                        continue block14;
                    }
                }
            }
            return ret;
            throw new JDOMException("Unexpected XMLStream event " + stream.getEventType());
        }
        catch (XMLStreamException e) {
            throw new JDOMException("Unable to process fragments from XMLStreamReader.", e);
        }
    }

    private static final Element processPrunableElement(JDOMFactory factory, XMLStreamReader reader, int topdepth, StAXFilter filter) throws XMLStreamException, JDOMException {
        Element fragment;
        if (1 != reader.getEventType()) {
            throw new JDOMException("JDOM requires that the XMLStreamReader is at the START_ELEMENT state when retrieving an Element Fragment.");
        }
        Element current = fragment = StAXStreamBuilder.processElement(factory, reader);
        int depth = topdepth + 1;
        String text = null;
        block9: while (depth > topdepth && reader.hasNext()) {
            switch (reader.next()) {
                case 1: {
                    QName qn = reader.getName();
                    if (!filter.pruneElement(depth, qn.getLocalPart(), Namespace.getNamespace(qn.getPrefix(), qn.getNamespaceURI()))) {
                        Element tmp = StAXStreamBuilder.processElement(factory, reader);
                        current.addContent(tmp);
                        current = tmp;
                        ++depth;
                        continue block9;
                    }
                    int edepth = depth++;
                    int state = 0;
                    while (depth > edepth && reader.hasNext() && (state = reader.next()) != 8) {
                        if (state == 1) {
                            ++depth;
                            continue;
                        }
                        if (state != 2) continue;
                        --depth;
                    }
                    continue block9;
                }
                case 2: {
                    current = current.getParentElement();
                    --depth;
                    continue block9;
                }
                case 12: {
                    text = filter.pruneCDATA(depth, reader.getText());
                    if (text == null) continue block9;
                    current.addContent(factory.cdata(text));
                    continue block9;
                }
                case 4: 
                case 6: {
                    text = filter.pruneText(depth, reader.getText());
                    if (text == null) continue block9;
                    current.addContent(factory.text(text));
                    continue block9;
                }
                case 5: {
                    text = filter.pruneComment(depth, reader.getText());
                    if (text == null) continue block9;
                    current.addContent(factory.comment(text));
                    continue block9;
                }
                case 9: {
                    if (filter.pruneEntityRef(depth, reader.getLocalName())) continue block9;
                    current.addContent(factory.entityRef(reader.getLocalName()));
                    continue block9;
                }
                case 3: {
                    if (filter.pruneProcessingInstruction(depth, reader.getPITarget())) continue block9;
                    current.addContent(factory.processingInstruction(reader.getPITarget(), reader.getPIData()));
                    continue block9;
                }
            }
            throw new JDOMException("Unexpected XMLStream event " + reader.getEventType());
        }
        return fragment;
    }

    private static final Content processFragment(JDOMFactory factory, XMLStreamReader stream) throws JDOMException {
        try {
            switch (stream.getEventType()) {
                case 7: {
                    throw new JDOMException("Illegal state for XMLStreamReader. Cannot get XML Fragment for state START_DOCUMENT");
                }
                case 8: {
                    throw new JDOMException("Illegal state for XMLStreamReader. Cannot get XML Fragment for state END_DOCUMENT");
                }
                case 2: {
                    throw new JDOMException("Illegal state for XMLStreamReader. Cannot get XML Fragment for state END_ELEMENT");
                }
                case 1: {
                    Element emt = StAXStreamBuilder.processElementFragment(factory, stream);
                    stream.next();
                    return emt;
                }
                case 11: {
                    DocType dt = DTDParser.parse(stream.getText(), factory);
                    stream.next();
                    return dt;
                }
                case 12: {
                    CDATA cd = factory.cdata(stream.getText());
                    stream.next();
                    return cd;
                }
                case 4: 
                case 6: {
                    Text txt = factory.text(stream.getText());
                    stream.next();
                    return txt;
                }
                case 5: {
                    Comment comment = factory.comment(stream.getText());
                    stream.next();
                    return comment;
                }
                case 9: {
                    EntityRef er = factory.entityRef(stream.getLocalName());
                    stream.next();
                    return er;
                }
                case 3: {
                    ProcessingInstruction pi = factory.processingInstruction(stream.getPITarget(), stream.getPIData());
                    stream.next();
                    return pi;
                }
            }
            throw new JDOMException("Unexpected XMLStream event " + stream.getEventType());
        }
        catch (XMLStreamException xse) {
            throw new JDOMException("Unable to process XMLStream. See Cause.", xse);
        }
    }

    private static final Element processElementFragment(JDOMFactory factory, XMLStreamReader reader) throws XMLStreamException, JDOMException {
        Element fragment;
        if (1 != reader.getEventType()) {
            throw new JDOMException("JDOM requires that the XMLStreamReader is at the START_ELEMENT state when retrieving an Element Fragment.");
        }
        Element current = fragment = StAXStreamBuilder.processElement(factory, reader);
        int depth = 1;
        block9: while (depth > 0 && reader.hasNext()) {
            switch (reader.next()) {
                case 1: {
                    Element tmp = StAXStreamBuilder.processElement(factory, reader);
                    current.addContent(tmp);
                    current = tmp;
                    ++depth;
                    continue block9;
                }
                case 2: {
                    current = current.getParentElement();
                    --depth;
                    continue block9;
                }
                case 12: {
                    current.addContent(factory.cdata(reader.getText()));
                    continue block9;
                }
                case 4: 
                case 6: {
                    current.addContent(factory.text(reader.getText()));
                    continue block9;
                }
                case 5: {
                    current.addContent(factory.comment(reader.getText()));
                    continue block9;
                }
                case 9: {
                    current.addContent(factory.entityRef(reader.getLocalName()));
                    continue block9;
                }
                case 3: {
                    current.addContent(factory.processingInstruction(reader.getPITarget(), reader.getPIData()));
                    continue block9;
                }
            }
            throw new JDOMException("Unexpected XMLStream event " + reader.getEventType());
        }
        return fragment;
    }

    private static final Element processElement(JDOMFactory factory, XMLStreamReader reader) {
        int i;
        Element element = factory.element(reader.getLocalName(), Namespace.getNamespace(reader.getPrefix(), reader.getNamespaceURI()));
        int len = reader.getAttributeCount();
        for (i = 0; i < len; ++i) {
            factory.setAttribute(element, factory.attribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i), AttributeType.getAttributeType(reader.getAttributeType(i)), Namespace.getNamespace(reader.getAttributePrefix(i), reader.getAttributeNamespace(i))));
        }
        len = reader.getNamespaceCount();
        for (i = 0; i < len; ++i) {
            element.addNamespaceDeclaration(Namespace.getNamespace(reader.getNamespacePrefix(i), reader.getNamespaceURI(i)));
        }
        return element;
    }

    public JDOMFactory getFactory() {
        return this.builderfactory;
    }

    public void setFactory(JDOMFactory factory) {
        this.builderfactory = factory;
    }

    public Document build(XMLStreamReader reader) throws JDOMException {
        return StAXStreamBuilder.process(this.builderfactory, reader);
    }

    public List<Content> buildFragments(XMLStreamReader reader, StAXFilter filter) throws JDOMException {
        return this.processFragments(this.builderfactory, reader, filter);
    }

    public Content fragment(XMLStreamReader reader) throws JDOMException {
        return StAXStreamBuilder.processFragment(this.builderfactory, reader);
    }
}

