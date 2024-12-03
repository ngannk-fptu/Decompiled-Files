/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  org.jdom2.Attribute
 *  org.jdom2.Content
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 *  org.jdom2.input.SAXBuilder
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Generator;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.Base64;
import com.rometools.rome.io.impl.BaseWireFeedGenerator;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.utils.Lists;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

public class Atom03Generator
extends BaseWireFeedGenerator {
    private static final String ATOM_03_URI = "http://purl.org/atom/ns#";
    private static final Namespace ATOM_NS = Namespace.getNamespace((String)"http://purl.org/atom/ns#");
    private final String version;

    public Atom03Generator() {
        this("atom_0.3", "0.3");
    }

    protected Atom03Generator(String type, String version) {
        super(type);
        this.version = version;
    }

    protected String getVersion() {
        return this.version;
    }

    protected Namespace getFeedNamespace() {
        return ATOM_NS;
    }

    @Override
    public Document generate(WireFeed wFeed) throws FeedException {
        Feed feed = (Feed)wFeed;
        Element root = this.createRootElement(feed);
        this.populateFeed(feed, root);
        Atom03Generator.purgeUnusedNamespaceDeclarations(root);
        return this.createDocument(root);
    }

    protected Document createDocument(Element root) {
        return new Document(root);
    }

    protected Element createRootElement(Feed feed) {
        Element root = new Element("feed", this.getFeedNamespace());
        root.addNamespaceDeclaration(this.getFeedNamespace());
        Attribute version = new Attribute("version", this.getVersion());
        root.setAttribute(version);
        this.generateModuleNamespaceDefs(root);
        return root;
    }

    protected void populateFeed(Feed feed, Element parent) throws FeedException {
        this.addFeed(feed, parent);
        this.addEntries(feed, parent);
    }

    protected void addFeed(Feed feed, Element parent) throws FeedException {
        Element eFeed = parent;
        this.populateFeedHeader(feed, eFeed);
        this.checkFeedHeaderConstraints(eFeed);
        this.generateFeedModules(feed.getModules(), eFeed);
        this.generateForeignMarkup(eFeed, feed.getForeignMarkup());
    }

    protected void addEntries(Feed feed, Element parent) throws FeedException {
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            this.addEntry(entry, parent);
        }
        this.checkEntriesConstraints(parent);
    }

    protected void addEntry(Entry entry, Element parent) throws FeedException {
        Element eEntry = new Element("entry", this.getFeedNamespace());
        this.populateEntry(entry, eEntry);
        this.checkEntryConstraints(eEntry);
        this.generateItemModules(entry.getModules(), eEntry);
        parent.addContent((Content)eEntry);
    }

    protected void populateFeedHeader(Feed feed, Element eFeed) throws FeedException {
        Date modified;
        com.rometools.rome.feed.atom.Content info;
        String copyright;
        Generator generator;
        String id;
        com.rometools.rome.feed.atom.Content titleEx = feed.getTitleEx();
        if (titleEx != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, titleEx);
            eFeed.addContent((Content)titleElement);
        }
        List<Link> links = feed.getAlternateLinks();
        for (Link link : links) {
            eFeed.addContent((Content)this.generateLinkElement(link));
        }
        links = feed.getOtherLinks();
        for (Link link : links) {
            eFeed.addContent((Content)this.generateLinkElement(link));
        }
        List<SyndPerson> authors = feed.getAuthors();
        if (Lists.isNotEmpty(authors)) {
            Element authorElement = new Element("author", this.getFeedNamespace());
            this.fillPersonElement(authorElement, authors.get(0));
            eFeed.addContent((Content)authorElement);
        }
        List<SyndPerson> contributors = feed.getContributors();
        for (SyndPerson contributor : contributors) {
            Element contributorElement = new Element("contributor", this.getFeedNamespace());
            this.fillPersonElement(contributorElement, contributor);
            eFeed.addContent((Content)contributorElement);
        }
        com.rometools.rome.feed.atom.Content tagline = feed.getTagline();
        if (tagline != null) {
            Element taglineElement = new Element("tagline", this.getFeedNamespace());
            this.fillContentElement(taglineElement, tagline);
            eFeed.addContent((Content)taglineElement);
        }
        if ((id = feed.getId()) != null) {
            eFeed.addContent((Content)this.generateSimpleElement("id", id));
        }
        if ((generator = feed.getGenerator()) != null) {
            eFeed.addContent((Content)this.generateGeneratorElement(generator));
        }
        if ((copyright = feed.getCopyright()) != null) {
            eFeed.addContent((Content)this.generateSimpleElement("copyright", copyright));
        }
        if ((info = feed.getInfo()) != null) {
            Element infoElement = new Element("info", this.getFeedNamespace());
            this.fillContentElement(infoElement, info);
            eFeed.addContent((Content)infoElement);
        }
        if ((modified = feed.getModified()) != null) {
            Element modifiedElement = new Element("modified", this.getFeedNamespace());
            modifiedElement.addContent(DateParser.formatW3CDateTime(modified, Locale.US));
            eFeed.addContent((Content)modifiedElement);
        }
    }

    protected void populateEntry(Entry entry, Element eEntry) throws FeedException {
        com.rometools.rome.feed.atom.Content summary;
        Date created;
        Date issued;
        Date modified;
        com.rometools.rome.feed.atom.Content titleEx = entry.getTitleEx();
        if (titleEx != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, titleEx);
            eEntry.addContent((Content)titleElement);
        }
        List<Link> alternateLinks = entry.getAlternateLinks();
        for (Link link : alternateLinks) {
            eEntry.addContent((Content)this.generateLinkElement(link));
        }
        List<Link> otherLinks = entry.getOtherLinks();
        for (Link link : otherLinks) {
            eEntry.addContent((Content)this.generateLinkElement(link));
        }
        List<SyndPerson> list = entry.getAuthors();
        if (Lists.isNotEmpty(list)) {
            Element authorElement = new Element("author", this.getFeedNamespace());
            this.fillPersonElement(authorElement, list.get(0));
            eEntry.addContent((Content)authorElement);
        }
        List<SyndPerson> contributors = entry.getContributors();
        for (SyndPerson contributor : contributors) {
            Element contributorElement = new Element("contributor", this.getFeedNamespace());
            this.fillPersonElement(contributorElement, contributor);
            eEntry.addContent((Content)contributorElement);
        }
        String id = entry.getId();
        if (id != null) {
            eEntry.addContent((Content)this.generateSimpleElement("id", id));
        }
        if ((modified = entry.getModified()) != null) {
            Element modifiedElement = new Element("modified", this.getFeedNamespace());
            modifiedElement.addContent(DateParser.formatW3CDateTime(modified, Locale.US));
            eEntry.addContent((Content)modifiedElement);
        }
        if ((issued = entry.getIssued()) != null) {
            Element issuedElement = new Element("issued", this.getFeedNamespace());
            issuedElement.addContent(DateParser.formatW3CDateTime(issued, Locale.US));
            eEntry.addContent((Content)issuedElement);
        }
        if ((created = entry.getCreated()) != null) {
            Element createdElement = new Element("created", this.getFeedNamespace());
            createdElement.addContent(DateParser.formatW3CDateTime(created, Locale.US));
            eEntry.addContent((Content)createdElement);
        }
        if ((summary = entry.getSummary()) != null) {
            Element summaryElement = new Element("summary", this.getFeedNamespace());
            this.fillContentElement(summaryElement, summary);
            eEntry.addContent((Content)summaryElement);
        }
        List<com.rometools.rome.feed.atom.Content> contents = entry.getContents();
        for (com.rometools.rome.feed.atom.Content content : contents) {
            Element contentElement = new Element("content", this.getFeedNamespace());
            this.fillContentElement(contentElement, content);
            eEntry.addContent((Content)contentElement);
        }
        this.generateForeignMarkup(eEntry, entry.getForeignMarkup());
    }

    protected void checkFeedHeaderConstraints(Element eFeed) throws FeedException {
    }

    protected void checkEntriesConstraints(Element parent) throws FeedException {
    }

    protected void checkEntryConstraints(Element eEntry) throws FeedException {
    }

    protected Element generateLinkElement(Link link) {
        String href;
        String type;
        Element linkElement = new Element("link", this.getFeedNamespace());
        String rel = link.getRel();
        if (rel != null) {
            Attribute relAttribute = new Attribute("rel", rel);
            linkElement.setAttribute(relAttribute);
        }
        if ((type = link.getType()) != null) {
            Attribute typeAttribute = new Attribute("type", type);
            linkElement.setAttribute(typeAttribute);
        }
        if ((href = link.getHref()) != null) {
            Attribute hrefAttribute = new Attribute("href", href);
            linkElement.setAttribute(hrefAttribute);
        }
        return linkElement;
    }

    protected void fillPersonElement(Element element, SyndPerson person) {
        String email;
        String uri;
        String name = person.getName();
        if (name != null) {
            element.addContent((Content)this.generateSimpleElement("name", name));
        }
        if ((uri = person.getUri()) != null) {
            element.addContent((Content)this.generateSimpleElement("url", uri));
        }
        if ((email = person.getEmail()) != null) {
            element.addContent((Content)this.generateSimpleElement("email", email));
        }
    }

    protected Element generateTagLineElement(com.rometools.rome.feed.atom.Content tagline) {
        String value;
        Element taglineElement = new Element("tagline", this.getFeedNamespace());
        String type = tagline.getType();
        if (type != null) {
            Attribute typeAttribute = new Attribute("type", type);
            taglineElement.setAttribute(typeAttribute);
        }
        if ((value = tagline.getValue()) != null) {
            taglineElement.addContent(value);
        }
        return taglineElement;
    }

    protected void fillContentElement(Element contentElement, com.rometools.rome.feed.atom.Content content) throws FeedException {
        String value;
        String mode;
        String type = content.getType();
        if (type != null) {
            Attribute typeAttribute = new Attribute("type", type);
            contentElement.setAttribute(typeAttribute);
        }
        if ((mode = content.getMode()) != null) {
            Attribute modeAttribute = new Attribute("mode", mode);
            contentElement.setAttribute(modeAttribute);
        }
        if ((value = content.getValue()) != null) {
            if (mode == null || mode.equals("escaped")) {
                contentElement.addContent(value);
            } else if (mode.equals("base64")) {
                contentElement.addContent(Base64.encode(value));
            } else if (mode.equals("xml")) {
                Document tmpDoc;
                StringBuffer tmpDocString = new StringBuffer("<tmpdoc>");
                tmpDocString.append(value);
                tmpDocString.append("</tmpdoc>");
                StringReader tmpDocReader = new StringReader(tmpDocString.toString());
                try {
                    SAXBuilder saxBuilder = new SAXBuilder();
                    tmpDoc = saxBuilder.build((Reader)tmpDocReader);
                }
                catch (Exception ex) {
                    throw new FeedException("Invalid XML", ex);
                }
                List children = tmpDoc.getRootElement().removeContent();
                contentElement.addContent((Collection)children);
            }
        }
    }

    protected Element generateGeneratorElement(Generator generator) {
        String value;
        String version;
        Element generatorElement = new Element("generator", this.getFeedNamespace());
        String url = generator.getUrl();
        if (url != null) {
            Attribute urlAttribute = new Attribute("url", url);
            generatorElement.setAttribute(urlAttribute);
        }
        if ((version = generator.getVersion()) != null) {
            Attribute versionAttribute = new Attribute("version", version);
            generatorElement.setAttribute(versionAttribute);
        }
        if ((value = generator.getValue()) != null) {
            generatorElement.addContent(value);
        }
        return generatorElement;
    }

    protected Element generateSimpleElement(String name, String value) {
        Element element = new Element(name, this.getFeedNamespace());
        element.addContent(value);
        return element;
    }
}

