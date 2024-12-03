/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.atlassian.confluence.content.render.xhtml.ForwardingXmlEventReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.collections.CollectionUtils;

public class ElementTransformingXmlEventReader
extends ForwardingXmlEventReader {
    private final Map<QName, List<ElementTransformer>> elementTransformers;
    private XMLEvent lastPeek;

    public ElementTransformingXmlEventReader(XMLEventReader delegate, List<ElementTransformer> elementTransformers) {
        super(delegate);
        this.elementTransformers = new HashMap<QName, List<ElementTransformer>>(elementTransformers.size());
        for (ElementTransformer transformer : elementTransformers) {
            for (QName name : transformer.getHandledElementNames()) {
                if (!this.elementTransformers.containsKey(name)) {
                    this.elementTransformers.put(name, new ArrayList());
                }
                this.elementTransformers.get(name).add(transformer);
            }
        }
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.lastPeek != null) {
            return this.lastPeek;
        }
        this.lastPeek = this.transform(super.peek());
        return this.lastPeek;
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent next = this.delegate.nextEvent();
        if (this.lastPeek != null) {
            next = this.lastPeek;
            this.lastPeek = null;
        } else {
            next = this.transform(next);
        }
        return next;
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent next = this.delegate.nextTag();
        if (this.lastPeek == null || !this.lastPeek.isStartElement() || !this.lastPeek.isEndElement()) {
            this.lastPeek = null;
            next = this.transform(next);
        } else {
            next = this.lastPeek;
            this.lastPeek = null;
        }
        return next;
    }

    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    private XMLEvent transform(XMLEvent event) {
        block5: {
            List<ElementTransformer> transformers;
            block4: {
                if (event == null) {
                    return null;
                }
                if (!event.isStartElement()) break block4;
                List<ElementTransformer> transformers2 = this.elementTransformers.get(event.asStartElement().getName());
                if (!CollectionUtils.isNotEmpty(transformers2)) break block5;
                for (ElementTransformer transformer : transformers2) {
                    event = transformer.transform(event.asStartElement());
                }
                break block5;
            }
            if (event.isEndElement() && CollectionUtils.isNotEmpty(transformers = this.elementTransformers.get(event.asEndElement().getName()))) {
                for (ElementTransformer transformer : transformers) {
                    event = transformer.transform(event.asEndElement());
                }
            }
        }
        return event;
    }
}

