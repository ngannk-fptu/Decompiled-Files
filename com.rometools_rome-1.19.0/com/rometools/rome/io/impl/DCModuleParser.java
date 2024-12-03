/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.DCModuleImpl;
import com.rometools.rome.feed.module.DCSubject;
import com.rometools.rome.feed.module.DCSubjectImpl;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleParser;
import com.rometools.rome.io.impl.DateParser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class DCModuleParser
implements ModuleParser {
    private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String TAXO_URI = "http://purl.org/rss/1.0/modules/taxonomy/";
    private static final Namespace DC_NS = Namespace.getNamespace((String)"http://purl.org/dc/elements/1.1/");
    private static final Namespace RDF_NS = Namespace.getNamespace((String)"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    private static final Namespace TAXO_NS = Namespace.getNamespace((String)"http://purl.org/rss/1.0/modules/taxonomy/");

    @Override
    public final String getNamespaceUri() {
        return "http://purl.org/dc/elements/1.1/";
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
    public Module parse(Element dcRoot, Locale locale) {
        List rights;
        List coverages;
        List relations;
        List languages;
        List sources;
        List identifiers;
        List formats;
        List types;
        List dates;
        List contributors;
        List publishers;
        List descriptions;
        List subjects;
        List creators;
        boolean foundSomething = false;
        DCModuleImpl dcm = new DCModuleImpl();
        List titles = dcRoot.getChildren("title", this.getDCNamespace());
        if (!titles.isEmpty()) {
            foundSomething = true;
            dcm.setTitles(this.parseElementList(titles));
        }
        if (!(creators = dcRoot.getChildren("creator", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setCreators(this.parseElementList(creators));
        }
        if (!(subjects = dcRoot.getChildren("subject", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setSubjects(this.parseSubjects(subjects));
        }
        if (!(descriptions = dcRoot.getChildren("description", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setDescriptions(this.parseElementList(descriptions));
        }
        if (!(publishers = dcRoot.getChildren("publisher", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setPublishers(this.parseElementList(publishers));
        }
        if (!(contributors = dcRoot.getChildren("contributor", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setContributors(this.parseElementList(contributors));
        }
        if (!(dates = dcRoot.getChildren("date", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setDates(this.parseElementListDate(dates, locale));
        }
        if (!(types = dcRoot.getChildren("type", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setTypes(this.parseElementList(types));
        }
        if (!(formats = dcRoot.getChildren("format", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setFormats(this.parseElementList(formats));
        }
        if (!(identifiers = dcRoot.getChildren("identifier", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setIdentifiers(this.parseElementList(identifiers));
        }
        if (!(sources = dcRoot.getChildren("source", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setSources(this.parseElementList(sources));
        }
        if (!(languages = dcRoot.getChildren("language", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setLanguages(this.parseElementList(languages));
        }
        if (!(relations = dcRoot.getChildren("relation", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setRelations(this.parseElementList(relations));
        }
        if (!(coverages = dcRoot.getChildren("coverage", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setCoverages(this.parseElementList(coverages));
        }
        if (!(rights = dcRoot.getChildren("rights", this.getDCNamespace())).isEmpty()) {
            foundSomething = true;
            dcm.setRightsList(this.parseElementList(rights));
        }
        if (foundSomething) {
            return dcm;
        }
        return null;
    }

    protected final String getTaxonomy(Element desc) {
        Attribute resource;
        String taxonomy = null;
        Element topic = desc.getChild("topic", this.getTaxonomyNamespace());
        if (topic != null && (resource = topic.getAttribute("resource", this.getRDFNamespace())) != null) {
            taxonomy = resource.getValue();
        }
        return taxonomy;
    }

    protected final List<DCSubject> parseSubjects(List<Element> eList) {
        ArrayList<DCSubject> subjects = new ArrayList<DCSubject>();
        for (Element eSubject : eList) {
            Element description = eSubject.getChild("Description", this.getRDFNamespace());
            if (description != null) {
                String taxonomy = this.getTaxonomy(description);
                List values = description.getChildren("value", this.getRDFNamespace());
                for (Element value : values) {
                    DCSubjectImpl subject = new DCSubjectImpl();
                    subject.setTaxonomyUri(taxonomy);
                    subject.setValue(value.getText());
                    subjects.add(subject);
                }
                continue;
            }
            DCSubjectImpl subject = new DCSubjectImpl();
            subject.setValue(eSubject.getText());
            subjects.add(subject);
        }
        return subjects;
    }

    protected final List<String> parseElementList(List<Element> elements) {
        ArrayList<String> values = new ArrayList<String>();
        for (Element element : elements) {
            values.add(element.getText());
        }
        return values;
    }

    protected final List<Date> parseElementListDate(List<Element> elements, Locale locale) {
        ArrayList<Date> values = new ArrayList<Date>();
        for (Element element : elements) {
            values.add(DateParser.parseDate(element.getText(), locale));
        }
        return values;
    }
}

