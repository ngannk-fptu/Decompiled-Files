/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.Namespace
 *  org.jdom.input.SAXBuilder
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.impl.Base64;
import com.sun.syndication.io.impl.BaseWireFeedGenerator;
import com.sun.syndication.io.impl.DateParser;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class Atom03Generator
extends BaseWireFeedGenerator {
    private static final String ATOM_03_URI = "http://purl.org/atom/ns#";
    private static final Namespace ATOM_NS = Namespace.getNamespace((String)"http://purl.org/atom/ns#");
    private String _version;

    public Atom03Generator() {
        this("atom_0.3", "0.3");
    }

    protected Atom03Generator(String type, String version) {
        super(type);
        this._version = version;
    }

    protected String getVersion() {
        return this._version;
    }

    protected Namespace getFeedNamespace() {
        return ATOM_NS;
    }

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
        this.generateForeignMarkup(eFeed, (List)feed.getForeignMarkup());
    }

    protected void addEntries(Feed feed, Element parent) throws FeedException {
        List items = feed.getEntries();
        for (int i = 0; i < items.size(); ++i) {
            this.addEntry((Entry)items.get(i), parent);
        }
        this.checkEntriesConstraints(parent);
    }

    protected void addEntry(Entry entry, Element parent) throws FeedException {
        Element eEntry = new Element("entry", this.getFeedNamespace());
        this.populateEntry(entry, eEntry);
        this.checkEntryConstraints(eEntry);
        this.generateItemModules(entry.getModules(), eEntry);
        parent.addContent((org.jdom.Content)eEntry);
    }

    protected void populateFeedHeader(Feed feed, Element eFeed) throws FeedException {
        int i;
        if (feed.getTitleEx() != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, feed.getTitleEx());
            eFeed.addContent((org.jdom.Content)titleElement);
        }
        List links = feed.getAlternateLinks();
        for (i = 0; i < links.size(); ++i) {
            eFeed.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
        }
        links = feed.getOtherLinks();
        for (i = 0; i < links.size(); ++i) {
            eFeed.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
        }
        if (feed.getAuthors() != null && feed.getAuthors().size() > 0) {
            Element authorElement = new Element("author", this.getFeedNamespace());
            this.fillPersonElement(authorElement, (Person)feed.getAuthors().get(0));
            eFeed.addContent((org.jdom.Content)authorElement);
        }
        List contributors = feed.getContributors();
        for (int i2 = 0; i2 < contributors.size(); ++i2) {
            Element contributorElement = new Element("contributor", this.getFeedNamespace());
            this.fillPersonElement(contributorElement, (Person)contributors.get(i2));
            eFeed.addContent((org.jdom.Content)contributorElement);
        }
        if (feed.getTagline() != null) {
            Element taglineElement = new Element("tagline", this.getFeedNamespace());
            this.fillContentElement(taglineElement, feed.getTagline());
            eFeed.addContent((org.jdom.Content)taglineElement);
        }
        if (feed.getId() != null) {
            eFeed.addContent((org.jdom.Content)this.generateSimpleElement("id", feed.getId()));
        }
        if (feed.getGenerator() != null) {
            eFeed.addContent((org.jdom.Content)this.generateGeneratorElement(feed.getGenerator()));
        }
        if (feed.getCopyright() != null) {
            eFeed.addContent((org.jdom.Content)this.generateSimpleElement("copyright", feed.getCopyright()));
        }
        if (feed.getInfo() != null) {
            Element infoElement = new Element("info", this.getFeedNamespace());
            this.fillContentElement(infoElement, feed.getInfo());
            eFeed.addContent((org.jdom.Content)infoElement);
        }
        if (feed.getModified() != null) {
            Element modifiedElement = new Element("modified", this.getFeedNamespace());
            modifiedElement.addContent(DateParser.formatW3CDateTime(feed.getModified()));
            eFeed.addContent((org.jdom.Content)modifiedElement);
        }
    }

    protected void populateEntry(Entry entry, Element eEntry) throws FeedException {
        int i;
        if (entry.getTitleEx() != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, entry.getTitleEx());
            eEntry.addContent((org.jdom.Content)titleElement);
        }
        List links = entry.getAlternateLinks();
        for (i = 0; i < links.size(); ++i) {
            eEntry.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
        }
        links = entry.getOtherLinks();
        for (i = 0; i < links.size(); ++i) {
            eEntry.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
        }
        if (entry.getAuthors() != null && entry.getAuthors().size() > 0) {
            Element authorElement = new Element("author", this.getFeedNamespace());
            this.fillPersonElement(authorElement, (Person)entry.getAuthors().get(0));
            eEntry.addContent((org.jdom.Content)authorElement);
        }
        List contributors = entry.getContributors();
        for (int i2 = 0; i2 < contributors.size(); ++i2) {
            Element contributorElement = new Element("contributor", this.getFeedNamespace());
            this.fillPersonElement(contributorElement, (Person)contributors.get(i2));
            eEntry.addContent((org.jdom.Content)contributorElement);
        }
        if (entry.getId() != null) {
            eEntry.addContent((org.jdom.Content)this.generateSimpleElement("id", entry.getId()));
        }
        if (entry.getModified() != null) {
            Element modifiedElement = new Element("modified", this.getFeedNamespace());
            modifiedElement.addContent(DateParser.formatW3CDateTime(entry.getModified()));
            eEntry.addContent((org.jdom.Content)modifiedElement);
        }
        if (entry.getIssued() != null) {
            Element issuedElement = new Element("issued", this.getFeedNamespace());
            issuedElement.addContent(DateParser.formatW3CDateTime(entry.getIssued()));
            eEntry.addContent((org.jdom.Content)issuedElement);
        }
        if (entry.getCreated() != null) {
            Element createdElement = new Element("created", this.getFeedNamespace());
            createdElement.addContent(DateParser.formatW3CDateTime(entry.getCreated()));
            eEntry.addContent((org.jdom.Content)createdElement);
        }
        if (entry.getSummary() != null) {
            Element summaryElement = new Element("summary", this.getFeedNamespace());
            this.fillContentElement(summaryElement, entry.getSummary());
            eEntry.addContent((org.jdom.Content)summaryElement);
        }
        List contents = entry.getContents();
        for (int i3 = 0; i3 < contents.size(); ++i3) {
            Element contentElement = new Element("content", this.getFeedNamespace());
            this.fillContentElement(contentElement, (Content)contents.get(i3));
            eEntry.addContent((org.jdom.Content)contentElement);
        }
        this.generateForeignMarkup(eEntry, (List)entry.getForeignMarkup());
    }

    protected void checkFeedHeaderConstraints(Element eFeed) throws FeedException {
    }

    protected void checkEntriesConstraints(Element parent) throws FeedException {
    }

    protected void checkEntryConstraints(Element eEntry) throws FeedException {
    }

    protected Element generateLinkElement(Link link) {
        Element linkElement = new Element("link", this.getFeedNamespace());
        if (link.getRel() != null) {
            Attribute relAttribute = new Attribute("rel", link.getRel().toString());
            linkElement.setAttribute(relAttribute);
        }
        if (link.getType() != null) {
            Attribute typeAttribute = new Attribute("type", link.getType());
            linkElement.setAttribute(typeAttribute);
        }
        if (link.getHref() != null) {
            Attribute hrefAttribute = new Attribute("href", link.getHref());
            linkElement.setAttribute(hrefAttribute);
        }
        return linkElement;
    }

    protected void fillPersonElement(Element element, Person person) {
        if (person.getName() != null) {
            element.addContent((org.jdom.Content)this.generateSimpleElement("name", person.getName()));
        }
        if (person.getUrl() != null) {
            element.addContent((org.jdom.Content)this.generateSimpleElement("url", person.getUrl()));
        }
        if (person.getEmail() != null) {
            element.addContent((org.jdom.Content)this.generateSimpleElement("email", person.getEmail()));
        }
    }

    protected Element generateTagLineElement(Content tagline) {
        Element taglineElement = new Element("tagline", this.getFeedNamespace());
        if (tagline.getType() != null) {
            Attribute typeAttribute = new Attribute("type", tagline.getType());
            taglineElement.setAttribute(typeAttribute);
        }
        if (tagline.getValue() != null) {
            taglineElement.addContent(tagline.getValue());
        }
        return taglineElement;
    }

    protected void fillContentElement(Element contentElement, Content content) throws FeedException {
        String mode;
        if (content.getType() != null) {
            Attribute typeAttribute = new Attribute("type", content.getType());
            contentElement.setAttribute(typeAttribute);
        }
        if ((mode = content.getMode()) != null) {
            Attribute modeAttribute = new Attribute("mode", content.getMode().toString());
            contentElement.setAttribute(modeAttribute);
        }
        if (content.getValue() != null) {
            if (mode == null || mode.equals("escaped")) {
                contentElement.addContent(content.getValue());
            } else if (mode.equals("base64")) {
                contentElement.addContent(Base64.encode(content.getValue()));
            } else if (mode.equals("xml")) {
                Document tmpDoc;
                StringBuffer tmpDocString = new StringBuffer("<tmpdoc>");
                tmpDocString.append(content.getValue());
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
        Element generatorElement = new Element("generator", this.getFeedNamespace());
        if (generator.getUrl() != null) {
            Attribute urlAttribute = new Attribute("url", generator.getUrl());
            generatorElement.setAttribute(urlAttribute);
        }
        if (generator.getVersion() != null) {
            Attribute versionAttribute = new Attribute("version", generator.getVersion());
            generatorElement.setAttribute(versionAttribute);
        }
        if (generator.getValue() != null) {
            generatorElement.addContent(generator.getValue());
        }
        return generatorElement;
    }

    protected Element generateSimpleElement(String name, String value) {
        Element element = new Element(name, this.getFeedNamespace());
        element.addContent(value);
        return element;
    }
}

