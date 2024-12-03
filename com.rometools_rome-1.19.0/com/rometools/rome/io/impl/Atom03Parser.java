/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  org.jdom2.Content
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 *  org.jdom2.output.XMLOutputter
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Generator;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.Base64;
import com.rometools.rome.io.impl.BaseWireFeedParser;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.utils.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;

public class Atom03Parser
extends BaseWireFeedParser {
    private static final String ATOM_03_URI = "http://purl.org/atom/ns#";
    private static final Namespace ATOM_03_NS = Namespace.getNamespace((String)"http://purl.org/atom/ns#");

    public Atom03Parser() {
        this("atom_0.3", ATOM_03_NS);
    }

    protected Atom03Parser(String type, Namespace ns) {
        super(type, ns);
    }

    protected Namespace getAtomNamespace() {
        return ATOM_03_NS;
    }

    @Override
    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        return defaultNS != null && defaultNS.equals((Object)this.getAtomNamespace());
    }

    @Override
    public WireFeed parse(Document document, boolean validate, Locale locale) throws IllegalArgumentException, FeedException {
        if (validate) {
            this.validateFeed(document);
        }
        Element rssRoot = document.getRootElement();
        return this.parseFeed(rssRoot, locale);
    }

    protected void validateFeed(Document document) throws FeedException {
    }

    protected WireFeed parseFeed(Element eFeed, Locale locale) {
        List<Element> foreignMarkup;
        Element modified;
        Element info;
        Element copyright;
        Element generator;
        Element id;
        Element tagline;
        List contributors;
        String type = this.getType();
        Document document = eFeed.getDocument();
        String styleSheet = this.getStyleSheet(document);
        Feed feed = new Feed(type);
        feed.setStyleSheet(styleSheet);
        Element title = eFeed.getChild("title", this.getAtomNamespace());
        if (title != null) {
            feed.setTitleEx(this.parseContent(title));
        }
        List links = eFeed.getChildren("link", this.getAtomNamespace());
        feed.setAlternateLinks(this.parseAlternateLinks(links));
        feed.setOtherLinks(this.parseOtherLinks(links));
        Element author = eFeed.getChild("author", this.getAtomNamespace());
        if (author != null) {
            ArrayList<SyndPerson> authors = new ArrayList<SyndPerson>();
            authors.add(this.parsePerson(author));
            feed.setAuthors(authors);
        }
        if (!(contributors = eFeed.getChildren("contributor", this.getAtomNamespace())).isEmpty()) {
            feed.setContributors(this.parsePersons(contributors));
        }
        if ((tagline = eFeed.getChild("tagline", this.getAtomNamespace())) != null) {
            feed.setTagline(this.parseContent(tagline));
        }
        if ((id = eFeed.getChild("id", this.getAtomNamespace())) != null) {
            feed.setId(id.getText());
        }
        if ((generator = eFeed.getChild("generator", this.getAtomNamespace())) != null) {
            Generator gen = new Generator();
            gen.setValue(generator.getText());
            String att = this.getAttributeValue(generator, "url");
            if (att != null) {
                gen.setUrl(att);
            }
            if ((att = this.getAttributeValue(generator, "version")) != null) {
                gen.setVersion(att);
            }
            feed.setGenerator(gen);
        }
        if ((copyright = eFeed.getChild("copyright", this.getAtomNamespace())) != null) {
            feed.setCopyright(copyright.getText());
        }
        if ((info = eFeed.getChild("info", this.getAtomNamespace())) != null) {
            feed.setInfo(this.parseContent(info));
        }
        if ((modified = eFeed.getChild("modified", this.getAtomNamespace())) != null) {
            feed.setModified(DateParser.parseDate(modified.getText(), locale));
        }
        feed.setModules(this.parseFeedModules(eFeed, locale));
        List entries = eFeed.getChildren("entry", this.getAtomNamespace());
        if (!entries.isEmpty()) {
            feed.setEntries(this.parseEntries(entries, locale));
        }
        if (!(foreignMarkup = this.extractForeignMarkup(eFeed, feed, this.getAtomNamespace())).isEmpty()) {
            feed.setForeignMarkup(foreignMarkup);
        }
        return feed;
    }

    private Link parseLink(Element eLink) {
        String href;
        String type;
        Link link = new Link();
        String rel = this.getAttributeValue(eLink, "rel");
        if (rel != null) {
            link.setRel(rel);
        }
        if ((type = this.getAttributeValue(eLink, "type")) != null) {
            link.setType(type);
        }
        if ((href = this.getAttributeValue(eLink, "href")) != null) {
            link.setHref(href);
        }
        return link;
    }

    private List<Link> parseLinks(List<Element> eLinks, boolean alternate) {
        ArrayList<Link> links = new ArrayList<Link>();
        for (Element eLink : eLinks) {
            String rel = this.getAttributeValue(eLink, "rel");
            if (alternate) {
                if (!"alternate".equals(rel)) continue;
                links.add(this.parseLink(eLink));
                continue;
            }
            if ("alternate".equals(rel)) continue;
            links.add(this.parseLink(eLink));
        }
        return Lists.emptyToNull(links);
    }

    private List<Link> parseAlternateLinks(List<Element> eLinks) {
        return this.parseLinks(eLinks, true);
    }

    private List<Link> parseOtherLinks(List<Element> eLinks) {
        return this.parseLinks(eLinks, false);
    }

    private Person parsePerson(Element ePerson) {
        Element email;
        Element url;
        Person person = new Person();
        Element name = ePerson.getChild("name", this.getAtomNamespace());
        if (name != null) {
            person.setName(name.getText());
        }
        if ((url = ePerson.getChild("url", this.getAtomNamespace())) != null) {
            person.setUrl(url.getText());
        }
        if ((email = ePerson.getChild("email", this.getAtomNamespace())) != null) {
            person.setEmail(email.getText());
        }
        return person;
    }

    private List<SyndPerson> parsePersons(List<Element> ePersons) {
        ArrayList<Person> persons = new ArrayList<Person>();
        for (Element person : ePersons) {
            persons.add(this.parsePerson(person));
        }
        return Lists.emptyToNull(persons);
    }

    private com.rometools.rome.feed.atom.Content parseContent(Element e) {
        String mode;
        String value = null;
        String type = this.getAttributeValue(e, "type");
        if (type == null) {
            type = "text/plain";
        }
        if ((mode = this.getAttributeValue(e, "mode")) == null) {
            mode = "xml";
        }
        if (mode.equals("escaped")) {
            value = e.getText();
        } else if (mode.equals("base64")) {
            value = Base64.decode(e.getText());
        } else if (mode.equals("xml")) {
            XMLOutputter outputter = new XMLOutputter();
            List contents = e.getContent();
            for (Content content : contents) {
                Element element;
                if (!(content instanceof Element) || !(element = (Element)content).getNamespace().equals((Object)this.getAtomNamespace())) continue;
                element.setNamespace(Namespace.NO_NAMESPACE);
            }
            value = outputter.outputString(contents);
        }
        com.rometools.rome.feed.atom.Content content = new com.rometools.rome.feed.atom.Content();
        content.setType(type);
        content.setMode(mode);
        content.setValue(value);
        return content;
    }

    private List<Entry> parseEntries(List<Element> eEntries, Locale locale) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (Element entry : eEntries) {
            entries.add(this.parseEntry(entry, locale));
        }
        return Lists.emptyToNull(entries);
    }

    private Entry parseEntry(Element eEntry, Locale locale) {
        List contents;
        Element summary;
        Element created;
        Element issued;
        Element modified;
        Element id;
        List contributors;
        Entry entry = new Entry();
        Element title = eEntry.getChild("title", this.getAtomNamespace());
        if (title != null) {
            entry.setTitleEx(this.parseContent(title));
        }
        List links = eEntry.getChildren("link", this.getAtomNamespace());
        entry.setAlternateLinks(this.parseAlternateLinks(links));
        entry.setOtherLinks(this.parseOtherLinks(links));
        Element author = eEntry.getChild("author", this.getAtomNamespace());
        if (author != null) {
            ArrayList<SyndPerson> authors = new ArrayList<SyndPerson>();
            authors.add(this.parsePerson(author));
            entry.setAuthors(authors);
        }
        if (!(contributors = eEntry.getChildren("contributor", this.getAtomNamespace())).isEmpty()) {
            entry.setContributors(this.parsePersons(contributors));
        }
        if ((id = eEntry.getChild("id", this.getAtomNamespace())) != null) {
            entry.setId(id.getText());
        }
        if ((modified = eEntry.getChild("modified", this.getAtomNamespace())) != null) {
            entry.setModified(DateParser.parseDate(modified.getText(), locale));
        }
        if ((issued = eEntry.getChild("issued", this.getAtomNamespace())) != null) {
            entry.setIssued(DateParser.parseDate(issued.getText(), locale));
        }
        if ((created = eEntry.getChild("created", this.getAtomNamespace())) != null) {
            entry.setCreated(DateParser.parseDate(created.getText(), locale));
        }
        if ((summary = eEntry.getChild("summary", this.getAtomNamespace())) != null) {
            entry.setSummary(this.parseContent(summary));
        }
        if (!(contents = eEntry.getChildren("content", this.getAtomNamespace())).isEmpty()) {
            ArrayList<com.rometools.rome.feed.atom.Content> content = new ArrayList<com.rometools.rome.feed.atom.Content>();
            for (Element eContent : contents) {
                content.add(this.parseContent(eContent));
            }
            entry.setContents(content);
        }
        entry.setModules(this.parseItemModules(eEntry, locale));
        List<Element> foreignMarkup = this.extractForeignMarkup(eEntry, entry, this.getAtomNamespace());
        if (!foreignMarkup.isEmpty()) {
            entry.setForeignMarkup(foreignMarkup);
        }
        return entry;
    }
}

