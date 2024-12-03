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
 *  org.jdom.output.XMLOutputter
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import com.sun.syndication.io.impl.BaseWireFeedGenerator;
import com.sun.syndication.io.impl.DateParser;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class Atom10Generator
extends BaseWireFeedGenerator {
    private static final String ATOM_10_URI = "http://www.w3.org/2005/Atom";
    private static final Namespace ATOM_NS = Namespace.getNamespace((String)"http://www.w3.org/2005/Atom");
    private String _version;

    public Atom10Generator() {
        this("atom_1.0", "1.0");
    }

    protected Atom10Generator(String type, String version) {
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
        Atom10Generator.purgeUnusedNamespaceDeclarations(root);
        return this.createDocument(root);
    }

    protected Document createDocument(Element root) {
        return new Document(root);
    }

    protected Element createRootElement(Feed feed) {
        Element root = new Element("feed", this.getFeedNamespace());
        root.addNamespaceDeclaration(this.getFeedNamespace());
        if (feed.getXmlBase() != null) {
            root.setAttribute("base", feed.getXmlBase(), Namespace.XML_NAMESPACE);
        }
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
        this.generateForeignMarkup(eFeed, (List)feed.getForeignMarkup());
        this.checkFeedHeaderConstraints(eFeed);
        this.generateFeedModules(feed.getModules(), eFeed);
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
        if (entry.getXmlBase() != null) {
            eEntry.setAttribute("base", entry.getXmlBase(), Namespace.XML_NAMESPACE);
        }
        this.populateEntry(entry, eEntry);
        this.generateForeignMarkup(eEntry, (List)entry.getForeignMarkup());
        this.checkEntryConstraints(eEntry);
        this.generateItemModules(entry.getModules(), eEntry);
        parent.addContent((org.jdom.Content)eEntry);
    }

    protected void populateFeedHeader(Feed feed, Element eFeed) throws FeedException {
        List contributors;
        List authors;
        List cats;
        List links;
        if (feed.getTitleEx() != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, feed.getTitleEx());
            eFeed.addContent((org.jdom.Content)titleElement);
        }
        if ((links = feed.getAlternateLinks()) != null) {
            for (int i = 0; i < links.size(); ++i) {
                eFeed.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
            }
        }
        if ((links = feed.getOtherLinks()) != null) {
            for (int j = 0; j < links.size(); ++j) {
                eFeed.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(j)));
            }
        }
        if ((cats = feed.getCategories()) != null) {
            Iterator iter = cats.iterator();
            while (iter.hasNext()) {
                eFeed.addContent((org.jdom.Content)this.generateCategoryElement((Category)iter.next()));
            }
        }
        if ((authors = feed.getAuthors()) != null && authors.size() > 0) {
            for (int i = 0; i < authors.size(); ++i) {
                Element authorElement = new Element("author", this.getFeedNamespace());
                this.fillPersonElement(authorElement, (Person)feed.getAuthors().get(i));
                eFeed.addContent((org.jdom.Content)authorElement);
            }
        }
        if ((contributors = feed.getContributors()) != null && contributors.size() > 0) {
            for (int i = 0; i < contributors.size(); ++i) {
                Element contributorElement = new Element("contributor", this.getFeedNamespace());
                this.fillPersonElement(contributorElement, (Person)contributors.get(i));
                eFeed.addContent((org.jdom.Content)contributorElement);
            }
        }
        if (feed.getSubtitle() != null) {
            Element subtitleElement = new Element("subtitle", this.getFeedNamespace());
            this.fillContentElement(subtitleElement, feed.getSubtitle());
            eFeed.addContent((org.jdom.Content)subtitleElement);
        }
        if (feed.getId() != null) {
            eFeed.addContent((org.jdom.Content)this.generateSimpleElement("id", feed.getId()));
        }
        if (feed.getGenerator() != null) {
            eFeed.addContent((org.jdom.Content)this.generateGeneratorElement(feed.getGenerator()));
        }
        if (feed.getRights() != null) {
            eFeed.addContent((org.jdom.Content)this.generateSimpleElement("rights", feed.getRights()));
        }
        if (feed.getIcon() != null) {
            eFeed.addContent((org.jdom.Content)this.generateSimpleElement("icon", feed.getIcon()));
        }
        if (feed.getLogo() != null) {
            eFeed.addContent((org.jdom.Content)this.generateSimpleElement("logo", feed.getLogo()));
        }
        if (feed.getUpdated() != null) {
            Element updatedElement = new Element("updated", this.getFeedNamespace());
            updatedElement.addContent(DateParser.formatW3CDateTime(feed.getUpdated()));
            eFeed.addContent((org.jdom.Content)updatedElement);
        }
    }

    protected void populateEntry(Entry entry, Element eEntry) throws FeedException {
        List contributors;
        List authors;
        List cats;
        int i;
        List links;
        if (entry.getTitleEx() != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, entry.getTitleEx());
            eEntry.addContent((org.jdom.Content)titleElement);
        }
        if ((links = entry.getAlternateLinks()) != null) {
            for (i = 0; i < links.size(); ++i) {
                eEntry.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
            }
        }
        if ((links = entry.getOtherLinks()) != null) {
            for (i = 0; i < links.size(); ++i) {
                eEntry.addContent((org.jdom.Content)this.generateLinkElement((Link)links.get(i)));
            }
        }
        if ((cats = entry.getCategories()) != null) {
            for (int i2 = 0; i2 < cats.size(); ++i2) {
                eEntry.addContent((org.jdom.Content)this.generateCategoryElement((Category)cats.get(i2)));
            }
        }
        if ((authors = entry.getAuthors()) != null && authors.size() > 0) {
            for (int i3 = 0; i3 < authors.size(); ++i3) {
                Element authorElement = new Element("author", this.getFeedNamespace());
                this.fillPersonElement(authorElement, (Person)entry.getAuthors().get(i3));
                eEntry.addContent((org.jdom.Content)authorElement);
            }
        }
        if ((contributors = entry.getContributors()) != null && contributors.size() > 0) {
            for (int i4 = 0; i4 < contributors.size(); ++i4) {
                Element contributorElement = new Element("contributor", this.getFeedNamespace());
                this.fillPersonElement(contributorElement, (Person)contributors.get(i4));
                eEntry.addContent((org.jdom.Content)contributorElement);
            }
        }
        if (entry.getId() != null) {
            eEntry.addContent((org.jdom.Content)this.generateSimpleElement("id", entry.getId()));
        }
        if (entry.getUpdated() != null) {
            Element updatedElement = new Element("updated", this.getFeedNamespace());
            updatedElement.addContent(DateParser.formatW3CDateTime(entry.getUpdated()));
            eEntry.addContent((org.jdom.Content)updatedElement);
        }
        if (entry.getPublished() != null) {
            Element publishedElement = new Element("published", this.getFeedNamespace());
            publishedElement.addContent(DateParser.formatW3CDateTime(entry.getPublished()));
            eEntry.addContent((org.jdom.Content)publishedElement);
        }
        if (entry.getContents() != null && entry.getContents().size() > 0) {
            Element contentElement = new Element("content", this.getFeedNamespace());
            Content content = (Content)entry.getContents().get(0);
            this.fillContentElement(contentElement, content);
            eEntry.addContent((org.jdom.Content)contentElement);
        }
        if (entry.getSummary() != null) {
            Element summaryElement = new Element("summary", this.getFeedNamespace());
            this.fillContentElement(summaryElement, entry.getSummary());
            eEntry.addContent((org.jdom.Content)summaryElement);
        }
        if (entry.getSource() != null) {
            Element sourceElement = new Element("source", this.getFeedNamespace());
            this.populateFeedHeader(entry.getSource(), sourceElement);
            eEntry.addContent((org.jdom.Content)sourceElement);
        }
    }

    protected void checkFeedHeaderConstraints(Element eFeed) throws FeedException {
    }

    protected void checkEntriesConstraints(Element parent) throws FeedException {
    }

    protected void checkEntryConstraints(Element eEntry) throws FeedException {
    }

    protected Element generateCategoryElement(Category cat) {
        Element catElement = new Element("category", this.getFeedNamespace());
        if (cat.getTerm() != null) {
            Attribute termAttribute = new Attribute("term", cat.getTerm());
            catElement.setAttribute(termAttribute);
        }
        if (cat.getLabel() != null) {
            Attribute labelAttribute = new Attribute("label", cat.getLabel());
            catElement.setAttribute(labelAttribute);
        }
        if (cat.getScheme() != null) {
            Attribute schemeAttribute = new Attribute("scheme", cat.getScheme());
            catElement.setAttribute(schemeAttribute);
        }
        return catElement;
    }

    protected Element generateLinkElement(Link link) {
        Element linkElement = new Element("link", this.getFeedNamespace());
        if (link.getRel() != null) {
            Attribute relAttribute = new Attribute("rel", link.getRel());
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
        if (link.getHreflang() != null) {
            Attribute hreflangAttribute = new Attribute("hreflang", link.getHreflang());
            linkElement.setAttribute(hreflangAttribute);
        }
        if (link.getTitle() != null) {
            Attribute title = new Attribute("title", link.getTitle());
            linkElement.setAttribute(title);
        }
        if (link.getLength() != 0L) {
            Attribute lenght = new Attribute("length", Long.toString(link.getLength()));
            linkElement.setAttribute(lenght);
        }
        return linkElement;
    }

    protected void fillPersonElement(Element element, Person person) {
        if (person.getName() != null) {
            element.addContent((org.jdom.Content)this.generateSimpleElement("name", person.getName()));
        }
        if (person.getUri() != null) {
            element.addContent((org.jdom.Content)this.generateSimpleElement("uri", person.getUri()));
        }
        if (person.getEmail() != null) {
            element.addContent((org.jdom.Content)this.generateSimpleElement("email", person.getEmail()));
        }
        this.generatePersonModules(person.getModules(), element);
    }

    protected Element generateTagLineElement(Content tagline) {
        Element taglineElement = new Element("subtitle", this.getFeedNamespace());
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
        String href;
        String type;
        String atomType = type = content.getType();
        if (type != null) {
            if ("text/plain".equals(type)) {
                atomType = "text";
            } else if ("text/html".equals(type)) {
                atomType = "html";
            } else if ("application/xhtml+xml".equals(type)) {
                atomType = "xhtml";
            }
            Attribute typeAttribute = new Attribute("type", atomType);
            contentElement.setAttribute(typeAttribute);
        }
        if ((href = content.getSrc()) != null) {
            Attribute srcAttribute = new Attribute("src", href);
            contentElement.setAttribute(srcAttribute);
        }
        if (content.getValue() != null) {
            if (atomType != null && (atomType.equals("xhtml") || atomType.indexOf("/xml") != -1 || atomType.indexOf("+xml") != -1)) {
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
            } else {
                contentElement.addContent(content.getValue());
            }
        }
    }

    protected Element generateGeneratorElement(Generator generator) {
        Element generatorElement = new Element("generator", this.getFeedNamespace());
        if (generator.getUrl() != null) {
            Attribute urlAttribute = new Attribute("uri", generator.getUrl());
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

    public static void serializeEntry(Entry entry, Writer writer) throws IllegalArgumentException, FeedException, IOException {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        entries.add(entry);
        Feed feed1 = new Feed();
        feed1.setFeedType("atom_1.0");
        feed1.setEntries(entries);
        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        Document feedDoc = wireFeedOutput.outputJDom(feed1);
        Element entryElement = (Element)feedDoc.getRootElement().getChildren().get(0);
        XMLOutputter outputter = new XMLOutputter();
        outputter.output(entryElement, writer);
    }
}

