/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.PeekingIterator
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.excerpt;

import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.XMLNodeSkipper;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

class ExcerptState {
    private int blocks;
    private int chars;
    private String lastTag;
    private LinkedList<XMLEvent> containerStack = new LinkedList();

    ExcerptState() {
    }

    void setLastTag(String lastTag) {
        this.lastTag = lastTag;
    }

    public void pushContainerElement(XMLEvent element) {
        this.containerStack.push(element);
    }

    public XMLEvent popContainerElement() {
        return this.containerStack.pop();
    }

    public void clearContainerStack() {
        this.containerStack.clear();
    }

    public boolean isContainerStackEmpty() {
        return this.containerStack.isEmpty();
    }

    public String getLastTag() {
        return this.lastTag;
    }

    void addBlocks(int blocks) {
        this.blocks += blocks;
    }

    void addChars(int chars) {
        this.chars += chars;
    }

    public int getChars() {
        return this.chars;
    }

    public int getBlocks() {
        return this.blocks;
    }

    public boolean hasContent() {
        return this.blocks > 0 || this.chars > 0;
    }

    private void updateStateForEvent(XMLEvent event, ExcerptConfig config) {
        if (event.isEndElement() && config.getBlockElementSet().contains(event.asEndElement().getName().getLocalPart())) {
            this.addBlocks(1);
            this.setLastTag(event.asEndElement().getName().getLocalPart());
        } else if (event.isCharacters() && !StringUtils.isBlank((CharSequence)event.asCharacters().getData())) {
            this.addChars(event.asCharacters().getData().length());
        }
    }

    public void writeEvent(XMLEventWriter writer, ExcerptConfig config, XMLEvent event) throws XMLStreamException {
        this.writeEventList(writer, config, Collections.singletonList(event));
    }

    public void writeEventList(XMLEventWriter writer, ExcerptConfig config, List<XMLEvent> events) throws XMLStreamException {
        this.writeEventsAndUpdateState(writer, config, Lists.reverse(this.containerStack));
        this.containerStack.clear();
        this.writeEventsAndUpdateState(writer, config, events);
    }

    private void writeEventsAndUpdateState(XMLEventWriter writer, ExcerptConfig config, Iterable<XMLEvent> events) throws XMLStreamException {
        PeekingIterator xmlIt = Iterators.peekingIterator(events.iterator());
        while (xmlIt.hasNext()) {
            XMLEvent event = (XMLEvent)xmlIt.peek();
            if (config.canContinue(this) || !event.isStartElement() || event.isStartElement() && !config.getBlockElementSet().contains(event.asStartElement().getName().getLocalPart())) {
                writer.add((XMLEvent)xmlIt.next());
                this.updateStateForEvent(event, config);
                continue;
            }
            if (!event.isStartElement()) continue;
            XMLNodeSkipper.skipCurrentNode((Iterator<XMLEvent>)xmlIt);
        }
    }
}

