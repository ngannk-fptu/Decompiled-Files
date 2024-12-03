/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Content
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.module.DCSubject;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleGenerator;
import com.rometools.rome.io.impl.DateParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class DCModuleGenerator
implements ModuleGenerator {
    private static final String DC_URI = "http://purl.org/dc/elements/1.1/";
    private static final String TAXO_URI = "http://purl.org/rss/1.0/modules/taxonomy/";
    private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final Namespace DC_NS = Namespace.getNamespace((String)"dc", (String)"http://purl.org/dc/elements/1.1/");
    private static final Namespace TAXO_NS = Namespace.getNamespace((String)"taxo", (String)"http://purl.org/rss/1.0/modules/taxonomy/");
    private static final Namespace RDF_NS = Namespace.getNamespace((String)"rdf", (String)"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    private static final Set<Namespace> NAMESPACES;

    @Override
    public final String getNamespaceUri() {
        return DC_URI;
    }

    private final Namespace getDCNamespace() {
        return DC_NS;
    }

    private final Namespace getRDFNamespace() {
        return RDF_NS;
    }

    private final Namespace getTaxonomyNamespace() {
        return TAXO_NS;
    }

    @Override
    public final Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }

    @Override
    public final void generate(Module module, Element element) {
        String rights;
        String coverage;
        String relation;
        String language;
        String source;
        String identifier;
        String format;
        String type;
        Date dcDate;
        List<String> contributors;
        String publisher;
        String creator;
        DCModule dcModule = (DCModule)module;
        String title = dcModule.getTitle();
        if (title != null) {
            element.addContent(this.generateSimpleElementList("title", dcModule.getTitles()));
        }
        if ((creator = dcModule.getCreator()) != null) {
            element.addContent(this.generateSimpleElementList("creator", dcModule.getCreators()));
        }
        List<DCSubject> subjects = dcModule.getSubjects();
        for (DCSubject dcSubject : subjects) {
            element.addContent((Content)this.generateSubjectElement(dcSubject));
        }
        String description = dcModule.getDescription();
        if (description != null) {
            element.addContent(this.generateSimpleElementList("description", dcModule.getDescriptions()));
        }
        if ((publisher = dcModule.getPublisher()) != null) {
            element.addContent(this.generateSimpleElementList("publisher", dcModule.getPublishers()));
        }
        if ((contributors = dcModule.getContributors()) != null) {
            element.addContent(this.generateSimpleElementList("contributor", contributors));
        }
        if ((dcDate = dcModule.getDate()) != null) {
            for (Date date : dcModule.getDates()) {
                element.addContent((Content)this.generateSimpleElement("date", DateParser.formatW3CDateTime(date, Locale.US)));
            }
        }
        if ((type = dcModule.getType()) != null) {
            element.addContent(this.generateSimpleElementList("type", dcModule.getTypes()));
        }
        if ((format = dcModule.getFormat()) != null) {
            element.addContent(this.generateSimpleElementList("format", dcModule.getFormats()));
        }
        if ((identifier = dcModule.getIdentifier()) != null) {
            element.addContent(this.generateSimpleElementList("identifier", dcModule.getIdentifiers()));
        }
        if ((source = dcModule.getSource()) != null) {
            element.addContent(this.generateSimpleElementList("source", dcModule.getSources()));
        }
        if ((language = dcModule.getLanguage()) != null) {
            element.addContent(this.generateSimpleElementList("language", dcModule.getLanguages()));
        }
        if ((relation = dcModule.getRelation()) != null) {
            element.addContent(this.generateSimpleElementList("relation", dcModule.getRelations()));
        }
        if ((coverage = dcModule.getCoverage()) != null) {
            element.addContent(this.generateSimpleElementList("coverage", dcModule.getCoverages()));
        }
        if ((rights = dcModule.getRights()) != null) {
            element.addContent(this.generateSimpleElementList("rights", dcModule.getRightsList()));
        }
    }

    protected final Element generateSubjectElement(DCSubject subject) {
        Element subjectElement = new Element("subject", this.getDCNamespace());
        String taxonomyUri = subject.getTaxonomyUri();
        String value = subject.getValue();
        if (taxonomyUri != null) {
            Attribute resourceAttribute = new Attribute("resource", taxonomyUri, this.getRDFNamespace());
            Element topicElement = new Element("topic", this.getTaxonomyNamespace());
            topicElement.setAttribute(resourceAttribute);
            Element descriptionElement = new Element("Description", this.getRDFNamespace());
            descriptionElement.addContent((Content)topicElement);
            if (value != null) {
                Element valueElement = new Element("value", this.getRDFNamespace());
                valueElement.addContent(value);
                descriptionElement.addContent((Content)valueElement);
            }
            subjectElement.addContent((Content)descriptionElement);
        } else {
            subjectElement.addContent(value);
        }
        return subjectElement;
    }

    protected final Element generateSimpleElement(String name, String value) {
        Element element = new Element(name, this.getDCNamespace());
        element.addContent(value);
        return element;
    }

    protected final List<Element> generateSimpleElementList(String name, List<String> values) {
        ArrayList<Element> elements = new ArrayList<Element>();
        for (String value : values) {
            elements.add(this.generateSimpleElement(name, value));
        }
        return elements;
    }

    static {
        HashSet<Namespace> nss = new HashSet<Namespace>();
        nss.add(DC_NS);
        nss.add(TAXO_NS);
        nss.add(RDF_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }
}

