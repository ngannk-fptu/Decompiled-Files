/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  org.jdom2.Attribute
 *  org.jdom2.Content
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.JDOMException
 *  org.jdom2.Namespace
 *  org.jdom2.Parent
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
import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import com.rometools.rome.io.WireFeedOutput;
import com.rometools.rome.io.impl.BaseWireFeedParser;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.rome.io.impl.NumberParser;
import com.rometools.utils.Lists;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class Atom10Parser
extends BaseWireFeedParser {
    private static final String ATOM_10_URI = "http://www.w3.org/2005/Atom";
    private static final Namespace ATOM_10_NS = Namespace.getNamespace((String)"http://www.w3.org/2005/Atom");
    private static boolean resolveURIs = false;
    static Pattern absoluteURIPattern = Pattern.compile("^[a-z0-9]*:.*$");

    public static void setResolveURIs(boolean resolveURIs) {
        Atom10Parser.resolveURIs = resolveURIs;
    }

    public static boolean getResolveURIs() {
        return resolveURIs;
    }

    public Atom10Parser() {
        this("atom_1.0");
    }

    protected Atom10Parser(String type) {
        super(type, ATOM_10_NS);
    }

    protected Namespace getAtomNamespace() {
        return ATOM_10_NS;
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

    protected WireFeed parseFeed(Element eFeed, Locale locale) throws FeedException {
        List<Element> foreignMarkup;
        String baseURI = null;
        try {
            baseURI = this.findBaseURI(eFeed);
        }
        catch (Exception e) {
            throw new FeedException("ERROR while finding base URI of feed", e);
        }
        Feed feed = this.parseFeedMetadata(baseURI, eFeed, locale);
        feed.setStyleSheet(this.getStyleSheet(eFeed.getDocument()));
        String xmlBase = eFeed.getAttributeValue("base", Namespace.XML_NAMESPACE);
        if (xmlBase != null) {
            feed.setXmlBase(xmlBase);
        }
        feed.setModules(this.parseFeedModules(eFeed, locale));
        List eList = eFeed.getChildren("entry", this.getAtomNamespace());
        if (!eList.isEmpty()) {
            feed.setEntries(this.parseEntries(feed, baseURI, eList, locale));
        }
        if (!(foreignMarkup = this.extractForeignMarkup(eFeed, feed, this.getAtomNamespace())).isEmpty()) {
            feed.setForeignMarkup(foreignMarkup);
        }
        return feed;
    }

    private Feed parseFeedMetadata(String baseURI, Element eFeed, Locale locale) {
        Element updated;
        Element logo;
        Element icon;
        Element rights;
        Element generator;
        Element id;
        Element subtitle;
        List contributors;
        Feed feed = new Feed(this.getType());
        Element title = eFeed.getChild("title", this.getAtomNamespace());
        if (title != null) {
            com.rometools.rome.feed.atom.Content c = new com.rometools.rome.feed.atom.Content();
            c.setValue(this.parseTextConstructToString(title));
            c.setType(this.getAttributeValue(title, "type"));
            feed.setTitleEx(c);
        }
        List links = eFeed.getChildren("link", this.getAtomNamespace());
        feed.setAlternateLinks(this.parseAlternateLinks(feed, null, baseURI, links));
        feed.setOtherLinks(this.parseOtherLinks(feed, null, baseURI, links));
        List categories = eFeed.getChildren("category", this.getAtomNamespace());
        feed.setCategories(this.parseCategories(baseURI, categories));
        List authors = eFeed.getChildren("author", this.getAtomNamespace());
        if (!authors.isEmpty()) {
            feed.setAuthors(this.parsePersons(baseURI, authors, locale));
        }
        if (!(contributors = eFeed.getChildren("contributor", this.getAtomNamespace())).isEmpty()) {
            feed.setContributors(this.parsePersons(baseURI, contributors, locale));
        }
        if ((subtitle = eFeed.getChild("subtitle", this.getAtomNamespace())) != null) {
            com.rometools.rome.feed.atom.Content content = new com.rometools.rome.feed.atom.Content();
            content.setValue(this.parseTextConstructToString(subtitle));
            content.setType(this.getAttributeValue(subtitle, "type"));
            feed.setSubtitle(content);
        }
        if ((id = eFeed.getChild("id", this.getAtomNamespace())) != null) {
            feed.setId(id.getText());
        }
        if ((generator = eFeed.getChild("generator", this.getAtomNamespace())) != null) {
            String version;
            Generator gen = new Generator();
            gen.setValue(generator.getText());
            String uri = this.getAttributeValue(generator, "uri");
            if (uri != null) {
                gen.setUrl(uri);
            }
            if ((version = this.getAttributeValue(generator, "version")) != null) {
                gen.setVersion(version);
            }
            feed.setGenerator(gen);
        }
        if ((rights = eFeed.getChild("rights", this.getAtomNamespace())) != null) {
            feed.setRights(this.parseTextConstructToString(rights));
        }
        if ((icon = eFeed.getChild("icon", this.getAtomNamespace())) != null) {
            feed.setIcon(icon.getText());
        }
        if ((logo = eFeed.getChild("logo", this.getAtomNamespace())) != null) {
            feed.setLogo(logo.getText());
        }
        if ((updated = eFeed.getChild("updated", this.getAtomNamespace())) != null) {
            feed.setUpdated(DateParser.parseDate(updated.getText(), locale));
        }
        return feed;
    }

    private Link parseLink(Feed feed, Entry entry, String baseURI, Element eLink) {
        Long val;
        String length;
        String hrefLang;
        String title;
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
            if (Atom10Parser.isRelativeURI(href)) {
                link.setHrefResolved(Atom10Parser.resolveURI(baseURI, (Parent)eLink, href));
            }
        }
        if ((title = this.getAttributeValue(eLink, "title")) != null) {
            link.setTitle(title);
        }
        if ((hrefLang = this.getAttributeValue(eLink, "hreflang")) != null) {
            link.setHreflang(hrefLang);
        }
        if ((length = this.getAttributeValue(eLink, "length")) != null && (val = NumberParser.parseLong(length)) != null) {
            link.setLength(val);
        }
        return link;
    }

    private List<Link> parseAlternateLinks(Feed feed, Entry entry, String baseURI, List<Element> eLinks) {
        ArrayList<Link> links = new ArrayList<Link>();
        for (Element eLink : eLinks) {
            Link link = this.parseLink(feed, entry, baseURI, eLink);
            if (link.getRel() != null && !"".equals(link.getRel().trim()) && !"alternate".equals(link.getRel())) continue;
            links.add(link);
        }
        return Lists.emptyToNull(links);
    }

    private List<Link> parseOtherLinks(Feed feed, Entry entry, String baseURI, List<Element> eLinks) {
        ArrayList<Link> links = new ArrayList<Link>();
        for (Element eLink : eLinks) {
            Link link = this.parseLink(feed, entry, baseURI, eLink);
            if ("alternate".equals(link.getRel())) continue;
            links.add(link);
        }
        return Lists.emptyToNull(links);
    }

    private Person parsePerson(String baseURI, Element ePerson, Locale locale) {
        Element email;
        Element uri;
        Person person = new Person();
        Element name = ePerson.getChild("name", this.getAtomNamespace());
        if (name != null) {
            person.setName(name.getText());
        }
        if ((uri = ePerson.getChild("uri", this.getAtomNamespace())) != null) {
            person.setUri(uri.getText());
            if (Atom10Parser.isRelativeURI(uri.getText())) {
                person.setUriResolved(Atom10Parser.resolveURI(baseURI, (Parent)ePerson, uri.getText()));
            }
        }
        if ((email = ePerson.getChild("email", this.getAtomNamespace())) != null) {
            person.setEmail(email.getText());
        }
        person.setModules(this.parsePersonModules(ePerson, locale));
        return person;
    }

    private List<SyndPerson> parsePersons(String baseURI, List<Element> ePersons, Locale locale) {
        ArrayList<Person> persons = new ArrayList<Person>();
        for (Element ePerson : ePersons) {
            persons.add(this.parsePerson(baseURI, ePerson, locale));
        }
        return Lists.emptyToNull(persons);
    }

    private com.rometools.rome.feed.atom.Content parseContent(Element e) {
        String value = this.parseTextConstructToString(e);
        String src = this.getAttributeValue(e, "src");
        String type = this.getAttributeValue(e, "type");
        com.rometools.rome.feed.atom.Content content = new com.rometools.rome.feed.atom.Content();
        content.setSrc(src);
        content.setType(type);
        content.setValue(value);
        return content;
    }

    private String parseTextConstructToString(Element e) {
        String type = this.getAttributeValue(e, "type");
        if (type == null) {
            type = "text";
        }
        String value = null;
        if (type.equals("xhtml") || type.indexOf("/xml") != -1 || type.indexOf("+xml") != -1) {
            XMLOutputter outputter = new XMLOutputter();
            List contents = e.getContent();
            for (Content content : contents) {
                Element element;
                if (!(content instanceof Element) || !(element = (Element)content).getNamespace().equals((Object)this.getAtomNamespace())) continue;
                element.setNamespace(Namespace.NO_NAMESPACE);
            }
            value = outputter.outputString(contents);
        } else {
            value = e.getText();
        }
        return value;
    }

    protected List<Entry> parseEntries(Feed feed, String baseURI, List<Element> eEntries, Locale locale) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (Element entry : eEntries) {
            entries.add(this.parseEntry(feed, entry, baseURI, locale));
        }
        return Lists.emptyToNull(entries);
    }

    protected Entry parseEntry(Feed feed, Element eEntry, String baseURI, Locale locale) {
        Element rights;
        Element content;
        Element summary;
        Element published;
        Element updated;
        Element id;
        List contributors;
        Element title;
        Entry entry = new Entry();
        String xmlBase = eEntry.getAttributeValue("base", Namespace.XML_NAMESPACE);
        if (xmlBase != null) {
            entry.setXmlBase(xmlBase);
        }
        if ((title = eEntry.getChild("title", this.getAtomNamespace())) != null) {
            com.rometools.rome.feed.atom.Content c = new com.rometools.rome.feed.atom.Content();
            c.setValue(this.parseTextConstructToString(title));
            c.setType(this.getAttributeValue(title, "type"));
            entry.setTitleEx(c);
        }
        List links = eEntry.getChildren("link", this.getAtomNamespace());
        entry.setAlternateLinks(this.parseAlternateLinks(feed, entry, baseURI, links));
        entry.setOtherLinks(this.parseOtherLinks(feed, entry, baseURI, links));
        List authors = eEntry.getChildren("author", this.getAtomNamespace());
        if (!authors.isEmpty()) {
            entry.setAuthors(this.parsePersons(baseURI, authors, locale));
        }
        if (!(contributors = eEntry.getChildren("contributor", this.getAtomNamespace())).isEmpty()) {
            entry.setContributors(this.parsePersons(baseURI, contributors, locale));
        }
        if ((id = eEntry.getChild("id", this.getAtomNamespace())) != null) {
            entry.setId(id.getText());
        }
        if ((updated = eEntry.getChild("updated", this.getAtomNamespace())) != null) {
            entry.setUpdated(DateParser.parseDate(updated.getText(), locale));
        }
        if ((published = eEntry.getChild("published", this.getAtomNamespace())) != null) {
            entry.setPublished(DateParser.parseDate(published.getText(), locale));
        }
        if ((summary = eEntry.getChild("summary", this.getAtomNamespace())) != null) {
            entry.setSummary(this.parseContent(summary));
        }
        if ((content = eEntry.getChild("content", this.getAtomNamespace())) != null) {
            ArrayList<com.rometools.rome.feed.atom.Content> contents = new ArrayList<com.rometools.rome.feed.atom.Content>();
            contents.add(this.parseContent(content));
            entry.setContents(contents);
        }
        if ((rights = eEntry.getChild("rights", this.getAtomNamespace())) != null) {
            entry.setRights(rights.getText());
        }
        List categories = eEntry.getChildren("category", this.getAtomNamespace());
        entry.setCategories(this.parseCategories(baseURI, categories));
        Element source = eEntry.getChild("source", this.getAtomNamespace());
        if (source != null) {
            entry.setSource(this.parseFeedMetadata(baseURI, source, locale));
        }
        entry.setModules(this.parseItemModules(eEntry, locale));
        List<Element> foreignMarkup = this.extractForeignMarkup(eEntry, entry, this.getAtomNamespace());
        if (!foreignMarkup.isEmpty()) {
            entry.setForeignMarkup(foreignMarkup);
        }
        return entry;
    }

    private List<Category> parseCategories(String baseURI, List<Element> eCategories) {
        ArrayList<Category> cats = new ArrayList<Category>();
        for (Element eCategory : eCategories) {
            cats.add(this.parseCategory(baseURI, eCategory));
        }
        return Lists.emptyToNull(cats);
    }

    private Category parseCategory(String baseURI, Element eCategory) {
        String label;
        String scheme;
        Category category = new Category();
        String term = this.getAttributeValue(eCategory, "term");
        if (term != null) {
            category.setTerm(term);
        }
        if ((scheme = this.getAttributeValue(eCategory, "scheme")) != null) {
            category.setScheme(scheme);
            if (Atom10Parser.isRelativeURI(scheme)) {
                category.setSchemeResolved(Atom10Parser.resolveURI(baseURI, (Parent)eCategory, scheme));
            }
        }
        if ((label = this.getAttributeValue(eCategory, "label")) != null) {
            category.setLabel(label);
        }
        return category;
    }

    public static boolean isAbsoluteURI(String uri) {
        return absoluteURIPattern.matcher(uri).find();
    }

    public static boolean isRelativeURI(String uri) {
        return !Atom10Parser.isAbsoluteURI(uri);
    }

    public static String resolveURI(String baseURI, Parent parent, String url) {
        if (!resolveURIs) {
            return url;
        }
        if (Atom10Parser.isRelativeURI(url)) {
            if (".".equals(url) || "./".equals(url)) {
                url = "";
            }
            if (url.startsWith("/") && baseURI != null) {
                String base = null;
                int slashslash = baseURI.indexOf("//");
                int nextslash = baseURI.indexOf("/", slashslash + 2);
                if (nextslash != -1) {
                    base = baseURI.substring(0, nextslash);
                }
                return Atom10Parser.formURI(base, url);
            }
            if (parent != null && parent instanceof Element) {
                String xmlbase = ((Element)parent).getAttributeValue("base", Namespace.XML_NAMESPACE);
                if (xmlbase != null && xmlbase.trim().length() > 0) {
                    if (Atom10Parser.isAbsoluteURI(xmlbase)) {
                        if (url.startsWith("/")) {
                            int slashslash = xmlbase.indexOf("//");
                            int nextslash = xmlbase.indexOf("/", slashslash + 2);
                            if (nextslash != -1) {
                                xmlbase = xmlbase.substring(0, nextslash);
                            }
                            return Atom10Parser.formURI(xmlbase, url);
                        }
                        if (!xmlbase.endsWith("/")) {
                            xmlbase = xmlbase.substring(0, xmlbase.lastIndexOf("/"));
                        }
                        return Atom10Parser.formURI(xmlbase, url);
                    }
                    return Atom10Parser.resolveURI(baseURI, parent.getParent(), Atom10Parser.stripTrailingSlash(xmlbase) + "/" + Atom10Parser.stripStartingSlash(url));
                }
                return Atom10Parser.resolveURI(baseURI, parent.getParent(), url);
            }
            if (parent == null || parent instanceof Document) {
                return Atom10Parser.formURI(baseURI, url);
            }
        }
        return url;
    }

    private String findBaseURI(Element root) throws MalformedURLException {
        String ret = null;
        if (this.findAtomLink(root, "self") != null) {
            ret = this.findAtomLink(root, "self");
            if (".".equals(ret) || "./".equals(ret)) {
                ret = "";
            }
            if (ret.indexOf("/") != -1) {
                ret = ret.substring(0, ret.lastIndexOf("/"));
            }
            ret = Atom10Parser.resolveURI(null, (Parent)root, ret);
        }
        return ret;
    }

    private String findAtomLink(Element parent, String rel) {
        String ret = null;
        List linksList = parent.getChildren("link", ATOM_10_NS);
        if (linksList != null) {
            Iterator iterator = linksList.iterator();
            while (iterator.hasNext()) {
                Element element;
                Element link = element = (Element)iterator.next();
                Attribute relAtt = this.getAttribute(link, "rel");
                Attribute hrefAtt = this.getAttribute(link, "href");
                if ((relAtt != null || !"alternate".equals(rel)) && (relAtt == null || !relAtt.getValue().equals(rel))) continue;
                ret = hrefAtt.getValue();
                break;
            }
        }
        return ret;
    }

    private static String formURI(String base, String append) {
        base = Atom10Parser.stripTrailingSlash(base);
        if ((append = Atom10Parser.stripStartingSlash(append)).startsWith("..")) {
            String[] parts;
            for (String part : parts = append.split("/")) {
                if (!"..".equals(part)) continue;
                int last = base.lastIndexOf("/");
                if (last == -1) break;
                base = base.substring(0, last);
                append = append.substring(3, append.length());
            }
        }
        return base + "/" + append;
    }

    private static String stripStartingSlash(String s) {
        if (s != null && s.startsWith("/")) {
            s = s.substring(1, s.length());
        }
        return s;
    }

    private static String stripTrailingSlash(String s) {
        if (s != null && s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static Entry parseEntry(Reader rd, String baseURI, Locale locale) throws JDOMException, IOException, IllegalArgumentException, FeedException {
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        Document entryDoc = builder.build(rd);
        Element fetchedEntryElement = entryDoc.getRootElement();
        fetchedEntryElement.detach();
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        Document feedDoc = wireFeedOutput.outputJDom(feed);
        feedDoc.getRootElement().addContent((Content)fetchedEntryElement);
        if (baseURI != null) {
            feedDoc.getRootElement().setAttribute("base", baseURI, Namespace.XML_NAMESPACE);
        }
        WireFeedInput input = new WireFeedInput(false, locale);
        Feed parsedFeed = (Feed)input.build(feedDoc);
        return parsedFeed.getEntries().get(0);
    }
}

