/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.AttributeWrapper;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.Fragment;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.InlineMacroFragment;
import com.google.common.base.CharMatcher;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ParagraphFragment
implements Fragment {
    private static final QName PARAGRAPH_QNAME = new QName("p");
    private static final QName LINE_BREAK_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "br");
    private final StartElement paragraphStartEvent;
    private final EndElement paragraphEndEvent;
    private final Set<Attribute> attributes;
    final Deque<XMLEvent> paragraphBodyEvents;
    private boolean lastAddedFragmentIsInlineBodyMacro = false;

    public ParagraphFragment(XMLEventFactory xmlEventFactory) {
        this.paragraphStartEvent = xmlEventFactory.createStartElement(PARAGRAPH_QNAME, null, null);
        this.paragraphEndEvent = xmlEventFactory.createEndElement(PARAGRAPH_QNAME, null);
        this.attributes = Collections.emptySet();
        this.paragraphBodyEvents = new LinkedList<XMLEvent>();
    }

    public ParagraphFragment(XMLEventReader xmlEventReader) throws XMLStreamException {
        this.paragraphBodyEvents = new LinkedList<XMLEvent>();
        while (xmlEventReader.hasNext()) {
            this.paragraphBodyEvents.add(xmlEventReader.nextEvent());
        }
        if (!this.paragraphBodyEvents.peekFirst().isStartElement() || !this.paragraphBodyEvents.peekLast().isEndElement()) {
            throw new IllegalArgumentException("xmlEventReader provided to this constructor must be a reader over a paragraph fragment");
        }
        this.paragraphStartEvent = this.paragraphBodyEvents.pollFirst().asStartElement();
        this.paragraphEndEvent = this.paragraphBodyEvents.pollLast().asEndElement();
        this.attributes = this.createAttributes(this.paragraphStartEvent.getAttributes());
    }

    public void add(Fragment fragment) {
        if (fragment instanceof ParagraphFragment) {
            ParagraphFragment paragraphFragment = (ParagraphFragment)fragment;
            if (!this.canMerge(paragraphFragment)) {
                throw new IllegalStateException("Called add() with paragraph fragment that cannot and should not be merged.");
            }
            this.paragraphBodyEvents.addAll(paragraphFragment.bodyEvents());
        } else {
            this.paragraphBodyEvents.addAll(fragment.events());
        }
        this.lastAddedFragmentIsInlineBodyMacro = fragment instanceof InlineMacroFragment;
    }

    public boolean canMerge(ParagraphFragment paragraphFragment) {
        return this.lastAddedFragmentIsInlineBodyMacro && paragraphFragment.attributes().equals(this.attributes);
    }

    public List<XMLEvent> bodyEvents() {
        return Collections.unmodifiableList(new LinkedList<XMLEvent>(this.paragraphBodyEvents));
    }

    @Override
    public List<XMLEvent> events() {
        LinkedList<XMLEvent> result = new LinkedList<XMLEvent>();
        result.add(this.paragraphStartEvent);
        result.addAll(this.paragraphBodyEvents);
        result.add(this.paragraphEndEvent);
        return Collections.unmodifiableList(result);
    }

    public boolean isAutoCursorTarget() {
        return StaxUtils.hasClass(this.paragraphStartEvent, "auto-cursor-target") && this.isEmpty();
    }

    private boolean isEmpty() {
        for (XMLEvent xmlEvent : this.paragraphBodyEvents) {
            boolean empty = xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().equals(LINE_BREAK_ELEMENT) || xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().equals(LINE_BREAK_ELEMENT) || xmlEvent.isCharacters() && CharMatcher.whitespace().matchesAllOf((CharSequence)xmlEvent.asCharacters().getData());
            if (empty) continue;
            return false;
        }
        return true;
    }

    public Set<Attribute> attributes() {
        return Collections.unmodifiableSet(this.attributes);
    }

    private Set<Attribute> createAttributes(Iterator<Attribute> attributeIterator) {
        HashSet<Attribute> result = new HashSet<Attribute>();
        while (attributeIterator.hasNext()) {
            result.add(new AttributeWrapper(attributeIterator.next()));
        }
        return result;
    }
}

