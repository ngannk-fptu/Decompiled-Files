/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.Fragment;
import com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody.ParagraphFragment;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class InlineMacroFragment
implements Fragment {
    private List<XMLEvent> fragmentEvents = new LinkedList<XMLEvent>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InlineMacroFragment(XMLEventReader fragmentReader, XmlEventReaderFactory xmlEventReaderFactory) {
        while (fragmentReader.hasNext()) {
            try {
                if (fragmentReader.peek().isStartElement() && "p".equals(fragmentReader.peek().asStartElement().getName().getLocalPart())) {
                    XMLEventReader paragraphFragmentReader = xmlEventReaderFactory.createXmlFragmentEventReader(fragmentReader);
                    try {
                        ParagraphFragment p = new ParagraphFragment(paragraphFragmentReader);
                        if (p.isAutoCursorTarget()) continue;
                        this.fragmentEvents.addAll(p.events());
                        continue;
                    }
                    finally {
                        StaxUtils.closeQuietly(paragraphFragmentReader);
                        continue;
                    }
                }
                this.fragmentEvents.add(fragmentReader.nextEvent());
            }
            catch (XMLStreamException e) {
                throw new RuntimeException("Error reading xml stream.");
            }
        }
    }

    @Override
    public List<XMLEvent> events() {
        return Collections.unmodifiableList(this.fragmentEvents);
    }
}

