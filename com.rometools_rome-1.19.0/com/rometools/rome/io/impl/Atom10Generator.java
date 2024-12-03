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
 *  org.jdom2.output.XMLOutputter
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Generator;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import com.rometools.rome.io.impl.BaseWireFeedGenerator;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.utils.Lists;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
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
import org.jdom2.output.XMLOutputter;

public class Atom10Generator
extends BaseWireFeedGenerator {
    private static final String ATOM_10_URI = "http://www.w3.org/2005/Atom";
    private static final Namespace ATOM_NS = Namespace.getNamespace((String)"http://www.w3.org/2005/Atom");
    private final String version;

    public Atom10Generator() {
        this("atom_1.0", "1.0");
    }

    protected Atom10Generator(String type, String version) {
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
        Atom10Generator.purgeUnusedNamespaceDeclarations(root);
        return this.createDocument(root);
    }

    protected Document createDocument(Element root) {
        return new Document(root);
    }

    protected Element createRootElement(Feed feed) {
        Element root = new Element("feed", this.getFeedNamespace());
        root.addNamespaceDeclaration(this.getFeedNamespace());
        String xmlBase = feed.getXmlBase();
        if (xmlBase != null) {
            root.setAttribute("base", xmlBase, Namespace.XML_NAMESPACE);
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
        this.generateForeignMarkup(eFeed, feed.getForeignMarkup());
        this.checkFeedHeaderConstraints(eFeed);
        this.generateFeedModules(feed.getModules(), eFeed);
    }

    protected void addEntries(Feed feed, Element parent) throws FeedException {
        List<Entry> items = feed.getEntries();
        for (Entry entry : items) {
            this.addEntry(entry, parent);
        }
        this.checkEntriesConstraints(parent);
    }

    protected void addEntry(Entry entry, Element parent) throws FeedException {
        Element eEntry = new Element("entry", this.getFeedNamespace());
        String xmlBase = entry.getXmlBase();
        if (xmlBase != null) {
            eEntry.setAttribute("base", xmlBase, Namespace.XML_NAMESPACE);
        }
        this.populateEntry(entry, eEntry);
        this.generateForeignMarkup(eEntry, entry.getForeignMarkup());
        this.checkEntryConstraints(eEntry);
        this.generateItemModules(entry.getModules(), eEntry);
        parent.addContent((Content)eEntry);
    }

    protected void populateFeedHeader(Feed feed, Element eFeed) throws FeedException {
        Date updated;
        String logo;
        String icon;
        String rights;
        Generator generator;
        String id;
        com.rometools.rome.feed.atom.Content content;
        List<SyndPerson> list;
        List<SyndPerson> list2;
        List<Category> list3;
        List<Link> otherLinks;
        List<Link> alternateLinks;
        com.rometools.rome.feed.atom.Content titleEx = feed.getTitleEx();
        if (titleEx != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, titleEx);
            eFeed.addContent((Content)titleElement);
        }
        if ((alternateLinks = feed.getAlternateLinks()) != null) {
            for (Link link : alternateLinks) {
                eFeed.addContent((Content)this.generateLinkElement(link));
            }
        }
        if ((otherLinks = feed.getOtherLinks()) != null) {
            for (Link link : otherLinks) {
                eFeed.addContent((Content)this.generateLinkElement(link));
            }
        }
        if ((list3 = feed.getCategories()) != null) {
            for (Category category : list3) {
                eFeed.addContent((Content)this.generateCategoryElement(category));
            }
        }
        if (Lists.isNotEmpty(list2 = feed.getAuthors())) {
            for (SyndPerson syndPerson : list2) {
                Element authorElement = new Element("author", this.getFeedNamespace());
                this.fillPersonElement(authorElement, syndPerson);
                eFeed.addContent((Content)authorElement);
            }
        }
        if (Lists.isNotEmpty(list = feed.getContributors())) {
            for (SyndPerson contributor : list) {
                Element contributorElement = new Element("contributor", this.getFeedNamespace());
                this.fillPersonElement(contributorElement, contributor);
                eFeed.addContent((Content)contributorElement);
            }
        }
        if ((content = feed.getSubtitle()) != null) {
            Element subtitleElement = new Element("subtitle", this.getFeedNamespace());
            this.fillContentElement(subtitleElement, content);
            eFeed.addContent((Content)subtitleElement);
        }
        if ((id = feed.getId()) != null) {
            eFeed.addContent((Content)this.generateSimpleElement("id", id));
        }
        if ((generator = feed.getGenerator()) != null) {
            eFeed.addContent((Content)this.generateGeneratorElement(generator));
        }
        if ((rights = feed.getRights()) != null) {
            eFeed.addContent((Content)this.generateSimpleElement("rights", rights));
        }
        if ((icon = feed.getIcon()) != null) {
            eFeed.addContent((Content)this.generateSimpleElement("icon", icon));
        }
        if ((logo = feed.getLogo()) != null) {
            eFeed.addContent((Content)this.generateSimpleElement("logo", logo));
        }
        if ((updated = feed.getUpdated()) != null) {
            Element updatedElement = new Element("updated", this.getFeedNamespace());
            updatedElement.addContent(DateParser.formatW3CDateTime(updated, Locale.US));
            eFeed.addContent((Content)updatedElement);
        }
    }

    protected void populateEntry(Entry entry, Element eEntry) throws FeedException {
        String rights;
        Feed source;
        com.rometools.rome.feed.atom.Content summary;
        List<com.rometools.rome.feed.atom.Content> contents;
        Date published;
        Date updated;
        String string;
        List<SyndPerson> list;
        List<SyndPerson> list2;
        List<Category> list3;
        List<Link> otherLinks;
        List<Link> alternateLinks;
        com.rometools.rome.feed.atom.Content titleEx = entry.getTitleEx();
        if (titleEx != null) {
            Element titleElement = new Element("title", this.getFeedNamespace());
            this.fillContentElement(titleElement, titleEx);
            eEntry.addContent((Content)titleElement);
        }
        if ((alternateLinks = entry.getAlternateLinks()) != null) {
            for (Link link : alternateLinks) {
                eEntry.addContent((Content)this.generateLinkElement(link));
            }
        }
        if ((otherLinks = entry.getOtherLinks()) != null) {
            for (Link link : otherLinks) {
                eEntry.addContent((Content)this.generateLinkElement(link));
            }
        }
        if ((list3 = entry.getCategories()) != null) {
            for (Category category : list3) {
                eEntry.addContent((Content)this.generateCategoryElement(category));
            }
        }
        if (Lists.isNotEmpty(list2 = entry.getAuthors())) {
            for (SyndPerson syndPerson : list2) {
                Element authorElement = new Element("author", this.getFeedNamespace());
                this.fillPersonElement(authorElement, syndPerson);
                eEntry.addContent((Content)authorElement);
            }
        }
        if (Lists.isNotEmpty(list = entry.getContributors())) {
            for (SyndPerson contributor : list) {
                Element contributorElement = new Element("contributor", this.getFeedNamespace());
                this.fillPersonElement(contributorElement, contributor);
                eEntry.addContent((Content)contributorElement);
            }
        }
        if ((string = entry.getId()) != null) {
            eEntry.addContent((Content)this.generateSimpleElement("id", string));
        }
        if ((updated = entry.getUpdated()) != null) {
            Element updatedElement = new Element("updated", this.getFeedNamespace());
            updatedElement.addContent(DateParser.formatW3CDateTime(updated, Locale.US));
            eEntry.addContent((Content)updatedElement);
        }
        if ((published = entry.getPublished()) != null) {
            Element publishedElement = new Element("published", this.getFeedNamespace());
            publishedElement.addContent(DateParser.formatW3CDateTime(published, Locale.US));
            eEntry.addContent((Content)publishedElement);
        }
        if (Lists.isNotEmpty(contents = entry.getContents())) {
            Element contentElement = new Element("content", this.getFeedNamespace());
            com.rometools.rome.feed.atom.Content content = contents.get(0);
            this.fillContentElement(contentElement, content);
            eEntry.addContent((Content)contentElement);
        }
        if ((summary = entry.getSummary()) != null) {
            Element summaryElement = new Element("summary", this.getFeedNamespace());
            this.fillContentElement(summaryElement, summary);
            eEntry.addContent((Content)summaryElement);
        }
        if ((source = entry.getSource()) != null) {
            Element sourceElement = new Element("source", this.getFeedNamespace());
            this.populateFeedHeader(source, sourceElement);
            eEntry.addContent((Content)sourceElement);
        }
        if ((rights = entry.getRights()) != null) {
            eEntry.addContent((Content)this.generateSimpleElement("rights", rights));
        }
    }

    protected void checkFeedHeaderConstraints(Element eFeed) throws FeedException {
    }

    protected void checkEntriesConstraints(Element parent) throws FeedException {
    }

    protected void checkEntryConstraints(Element eEntry) throws FeedException {
    }

    protected Element generateCategoryElement(Category cat) {
        String scheme;
        String label;
        Namespace namespace = this.getFeedNamespace();
        Element catElement = new Element("category", namespace);
        String term = cat.getTerm();
        if (term != null) {
            Attribute termAttribute = new Attribute("term", term);
            catElement.setAttribute(termAttribute);
        }
        if ((label = cat.getLabel()) != null) {
            Attribute labelAttribute = new Attribute("label", label);
            catElement.setAttribute(labelAttribute);
        }
        if ((scheme = cat.getScheme()) != null) {
            Attribute schemeAttribute = new Attribute("scheme", scheme);
            catElement.setAttribute(schemeAttribute);
        }
        return catElement;
    }

    protected Element generateLinkElement(Link link) {
        String linkTitle;
        String hreflang;
        String href;
        String type;
        Namespace namespace = this.getFeedNamespace();
        Element linkElement = new Element("link", namespace);
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
        if ((hreflang = link.getHreflang()) != null) {
            Attribute hreflangAttribute = new Attribute("hreflang", hreflang);
            linkElement.setAttribute(hreflangAttribute);
        }
        if ((linkTitle = link.getTitle()) != null) {
            Attribute title = new Attribute("title", linkTitle);
            linkElement.setAttribute(title);
        }
        if (link.getLength() != 0L) {
            Attribute lenght = new Attribute("length", Long.toString(link.getLength()));
            linkElement.setAttribute(lenght);
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
            element.addContent((Content)this.generateSimpleElement("uri", uri));
        }
        if ((email = person.getEmail()) != null) {
            element.addContent((Content)this.generateSimpleElement("email", email));
        }
        this.generatePersonModules(person.getModules(), element);
    }

    protected Element generateTagLineElement(com.rometools.rome.feed.atom.Content tagline) {
        String value;
        Element taglineElement = new Element("subtitle", this.getFeedNamespace());
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
        if ((value = content.getValue()) != null) {
            if (atomType != null && (atomType.equals("xhtml") || atomType.indexOf("/xml") != -1 || atomType.indexOf("+xml") != -1)) {
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
            } else {
                contentElement.addContent(value);
            }
        }
    }

    protected Element generateGeneratorElement(Generator generator) {
        String value;
        String version2;
        Element generatorElement = new Element("generator", this.getFeedNamespace());
        String url = generator.getUrl();
        if (url != null) {
            Attribute urlAttribute = new Attribute("uri", url);
            generatorElement.setAttribute(urlAttribute);
        }
        if ((version2 = generator.getVersion()) != null) {
            Attribute versionAttribute = new Attribute("version", version2);
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

