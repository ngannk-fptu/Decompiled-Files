/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody;

import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.Fragment;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.InlineMacroFragment;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.ParagraphFragment;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.XMLEvent;

public class ParagraphFragmentsBuffer {
    final Deque<ParagraphFragment> paragraphFragments;
    private final XMLEventFactory xmlEventFactory;

    public ParagraphFragmentsBuffer(XMLEventFactory xmlEventFactory) {
        this.xmlEventFactory = xmlEventFactory;
        this.paragraphFragments = new LinkedList<ParagraphFragment>();
    }

    public void add(Fragment fragment) {
        if (fragment instanceof ParagraphFragment) {
            if (!this.paragraphFragments.isEmpty() && this.paragraphFragments.peekLast().canMerge((ParagraphFragment)fragment)) {
                this.paragraphFragments.peekLast().add(fragment);
            } else {
                this.paragraphFragments.add((ParagraphFragment)fragment);
            }
        } else if (fragment instanceof InlineMacroFragment) {
            if (this.paragraphFragments.isEmpty()) {
                this.paragraphFragments.add(new ParagraphFragment(this.xmlEventFactory));
            }
            this.paragraphFragments.peekLast().add(fragment);
        } else {
            throw new UnsupportedOperationException("fragment type not supported: " + fragment.getClass().getName());
        }
    }

    public List<XMLEvent> flush() {
        LinkedList<XMLEvent> result = new LinkedList<XMLEvent>();
        while (this.paragraphFragments.peek() != null) {
            result.addAll(this.paragraphFragments.pollFirst().events());
        }
        return result;
    }
}

