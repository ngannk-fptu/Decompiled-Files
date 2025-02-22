/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.JDOMException
 *  org.jdom.Namespace
 *  org.jdom.Parent
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
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.WireFeedOutput;
import com.sun.syndication.io.impl.BaseWireFeedParser;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.NumberParser;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

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

    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        return defaultNS != null && defaultNS.equals((Object)this.getAtomNamespace());
    }

    public WireFeed parse(Document document, boolean validate) throws IllegalArgumentException, FeedException {
        if (validate) {
            this.validateFeed(document);
        }
        Element rssRoot = document.getRootElement();
        return this.parseFeed(rssRoot);
    }

    protected void validateFeed(Document document) throws FeedException {
    }

    protected WireFeed parseFeed(Element eFeed) throws FeedException {
        List foreignMarkup;
        String baseURI = null;
        try {
            baseURI = this.findBaseURI(eFeed);
        }
        catch (Exception e) {
            throw new FeedException("ERROR while finding base URI of feed", e);
        }
        Feed feed = this.parseFeedMetadata(baseURI, eFeed);
        String xmlBase = eFeed.getAttributeValue("base", Namespace.XML_NAMESPACE);
        if (xmlBase != null) {
            feed.setXmlBase(xmlBase);
        }
        feed.setModules(this.parseFeedModules(eFeed));
        List eList = eFeed.getChildren("entry", this.getAtomNamespace());
        if (eList.size() > 0) {
            feed.setEntries(this.parseEntries(feed, baseURI, eList));
        }
        if ((foreignMarkup = this.extractForeignMarkup(eFeed, feed, this.getAtomNamespace())).size() > 0) {
            feed.setForeignMarkup(foreignMarkup);
        }
        return feed;
    }

    private Feed parseFeedMetadata(String baseURI, Element eFeed) {
        Feed feed = new Feed(this.getType());
        Element e = eFeed.getChild("title", this.getAtomNamespace());
        if (e != null) {
            Content c = new Content();
            c.setValue(this.parseTextConstructToString(e));
            c.setType(this.getAttributeValue(e, "type"));
            feed.setTitleEx(c);
        }
        List eList = eFeed.getChildren("link", this.getAtomNamespace());
        feed.setAlternateLinks(this.parseAlternateLinks(feed, null, baseURI, eList));
        feed.setOtherLinks(this.parseOtherLinks(feed, null, baseURI, eList));
        List cList = eFeed.getChildren("category", this.getAtomNamespace());
        feed.setCategories(this.parseCategories(baseURI, cList));
        eList = eFeed.getChildren("author", this.getAtomNamespace());
        if (eList.size() > 0) {
            feed.setAuthors(this.parsePersons(baseURI, eList));
        }
        if ((eList = eFeed.getChildren("contributor", this.getAtomNamespace())).size() > 0) {
            feed.setContributors(this.parsePersons(baseURI, eList));
        }
        if ((e = eFeed.getChild("subtitle", this.getAtomNamespace())) != null) {
            Content subtitle = new Content();
            subtitle.setValue(this.parseTextConstructToString(e));
            subtitle.setType(this.getAttributeValue(e, "type"));
            feed.setSubtitle(subtitle);
        }
        if ((e = eFeed.getChild("id", this.getAtomNamespace())) != null) {
            feed.setId(e.getText());
        }
        if ((e = eFeed.getChild("generator", this.getAtomNamespace())) != null) {
            Generator gen = new Generator();
            gen.setValue(e.getText());
            String att = this.getAttributeValue(e, "uri");
            if (att != null) {
                gen.setUrl(att);
            }
            if ((att = this.getAttributeValue(e, "version")) != null) {
                gen.setVersion(att);
            }
            feed.setGenerator(gen);
        }
        if ((e = eFeed.getChild("rights", this.getAtomNamespace())) != null) {
            feed.setRights(this.parseTextConstructToString(e));
        }
        if ((e = eFeed.getChild("icon", this.getAtomNamespace())) != null) {
            feed.setIcon(e.getText());
        }
        if ((e = eFeed.getChild("logo", this.getAtomNamespace())) != null) {
            feed.setLogo(e.getText());
        }
        if ((e = eFeed.getChild("updated", this.getAtomNamespace())) != null) {
            feed.setUpdated(DateParser.parseDate(e.getText()));
        }
        return feed;
    }

    private Link parseLink(Feed feed, Entry entry, String baseURI, Element eLink) {
        Long val;
        Link link = new Link();
        String att = this.getAttributeValue(eLink, "rel");
        if (att != null) {
            link.setRel(att);
        }
        if ((att = this.getAttributeValue(eLink, "type")) != null) {
            link.setType(att);
        }
        if ((att = this.getAttributeValue(eLink, "href")) != null) {
            link.setHref(att);
            if (Atom10Parser.isRelativeURI(att)) {
                link.setHrefResolved(Atom10Parser.resolveURI(baseURI, (Parent)eLink, att));
            }
        }
        if ((att = this.getAttributeValue(eLink, "title")) != null) {
            link.setTitle(att);
        }
        if ((att = this.getAttributeValue(eLink, "hreflang")) != null) {
            link.setHreflang(att);
        }
        if ((att = this.getAttributeValue(eLink, "length")) != null && (val = NumberParser.parseLong(att)) != null) {
            link.setLength(val);
        }
        return link;
    }

    private List parseAlternateLinks(Feed feed, Entry entry, String baseURI, List eLinks) {
        ArrayList<Link> links = new ArrayList<Link>();
        for (int i = 0; i < eLinks.size(); ++i) {
            Element eLink = (Element)eLinks.get(i);
            Link link = this.parseLink(feed, entry, baseURI, eLink);
            if (link.getRel() != null && !"".equals(link.getRel().trim()) && !"alternate".equals(link.getRel())) continue;
            links.add(link);
        }
        return links.size() > 0 ? links : null;
    }

    private List parseOtherLinks(Feed feed, Entry entry, String baseURI, List eLinks) {
        ArrayList<Link> links = new ArrayList<Link>();
        for (int i = 0; i < eLinks.size(); ++i) {
            Element eLink = (Element)eLinks.get(i);
            Link link = this.parseLink(feed, entry, baseURI, eLink);
            if ("alternate".equals(link.getRel())) continue;
            links.add(link);
        }
        return links.size() > 0 ? links : null;
    }

    private Person parsePerson(String baseURI, Element ePerson) {
        Person person = new Person();
        Element e = ePerson.getChild("name", this.getAtomNamespace());
        if (e != null) {
            person.setName(e.getText());
        }
        if ((e = ePerson.getChild("uri", this.getAtomNamespace())) != null) {
            person.setUri(e.getText());
            if (Atom10Parser.isRelativeURI(e.getText())) {
                person.setUriResolved(Atom10Parser.resolveURI(baseURI, (Parent)ePerson, e.getText()));
            }
        }
        if ((e = ePerson.getChild("email", this.getAtomNamespace())) != null) {
            person.setEmail(e.getText());
        }
        person.setModules(this.parsePersonModules(ePerson));
        return person;
    }

    private List parsePersons(String baseURI, List ePersons) {
        ArrayList<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < ePersons.size(); ++i) {
            persons.add(this.parsePerson(baseURI, (Element)ePersons.get(i)));
        }
        return persons.size() > 0 ? persons : null;
    }

    private Content parseContent(Element e) {
        String value = this.parseTextConstructToString(e);
        String src = this.getAttributeValue(e, "src");
        String type = this.getAttributeValue(e, "type");
        Content content = new Content();
        content.setSrc(src);
        content.setType(type);
        content.setValue(value);
        return content;
    }

    private String parseTextConstructToString(Element e) {
        String value = null;
        String type = this.getAttributeValue(e, "type");
        String string = type = type != null ? type : "text";
        if (type.equals("xhtml") || type.indexOf("/xml") != -1 || type.indexOf("+xml") != -1) {
            XMLOutputter outputter = new XMLOutputter();
            List eContent = e.getContent();
            Iterator i = eContent.iterator();
            while (i.hasNext()) {
                Element eC;
                org.jdom.Content c = (org.jdom.Content)i.next();
                if (!(c instanceof Element) || !(eC = (Element)c).getNamespace().equals((Object)this.getAtomNamespace())) continue;
                ((Element)c).setNamespace(Namespace.NO_NAMESPACE);
            }
            value = outputter.outputString(eContent);
        } else {
            value = e.getText();
        }
        return value;
    }

    protected List parseEntries(Feed feed, String baseURI, List eEntries) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < eEntries.size(); ++i) {
            entries.add(this.parseEntry(feed, (Element)eEntries.get(i), baseURI));
        }
        return entries.size() > 0 ? entries : null;
    }

    protected Entry parseEntry(Feed feed, Element eEntry, String baseURI) {
        Element e;
        Entry entry = new Entry();
        String xmlBase = eEntry.getAttributeValue("base", Namespace.XML_NAMESPACE);
        if (xmlBase != null) {
            entry.setXmlBase(xmlBase);
        }
        if ((e = eEntry.getChild("title", this.getAtomNamespace())) != null) {
            Content c = new Content();
            c.setValue(this.parseTextConstructToString(e));
            c.setType(this.getAttributeValue(e, "type"));
            entry.setTitleEx(c);
        }
        List eList = eEntry.getChildren("link", this.getAtomNamespace());
        entry.setAlternateLinks(this.parseAlternateLinks(feed, entry, baseURI, eList));
        entry.setOtherLinks(this.parseOtherLinks(feed, entry, baseURI, eList));
        eList = eEntry.getChildren("author", this.getAtomNamespace());
        if (eList.size() > 0) {
            entry.setAuthors(this.parsePersons(baseURI, eList));
        }
        if ((eList = eEntry.getChildren("contributor", this.getAtomNamespace())).size() > 0) {
            entry.setContributors(this.parsePersons(baseURI, eList));
        }
        if ((e = eEntry.getChild("id", this.getAtomNamespace())) != null) {
            entry.setId(e.getText());
        }
        if ((e = eEntry.getChild("updated", this.getAtomNamespace())) != null) {
            entry.setUpdated(DateParser.parseDate(e.getText()));
        }
        if ((e = eEntry.getChild("published", this.getAtomNamespace())) != null) {
            entry.setPublished(DateParser.parseDate(e.getText()));
        }
        if ((e = eEntry.getChild("summary", this.getAtomNamespace())) != null) {
            entry.setSummary(this.parseContent(e));
        }
        if ((e = eEntry.getChild("content", this.getAtomNamespace())) != null) {
            ArrayList<Content> contents = new ArrayList<Content>();
            contents.add(this.parseContent(e));
            entry.setContents(contents);
        }
        if ((e = eEntry.getChild("rights", this.getAtomNamespace())) != null) {
            entry.setRights(e.getText());
        }
        List cList = eEntry.getChildren("category", this.getAtomNamespace());
        entry.setCategories(this.parseCategories(baseURI, cList));
        e = eEntry.getChild("source", this.getAtomNamespace());
        if (e != null) {
            entry.setSource(this.parseFeedMetadata(baseURI, e));
        }
        entry.setModules(this.parseItemModules(eEntry));
        List foreignMarkup = this.extractForeignMarkup(eEntry, entry, this.getAtomNamespace());
        if (foreignMarkup.size() > 0) {
            entry.setForeignMarkup(foreignMarkup);
        }
        return entry;
    }

    private List parseCategories(String baseURI, List eCategories) {
        ArrayList<Category> cats = new ArrayList<Category>();
        for (int i = 0; i < eCategories.size(); ++i) {
            Element eCategory = (Element)eCategories.get(i);
            cats.add(this.parseCategory(baseURI, eCategory));
        }
        return cats.size() > 0 ? cats : null;
    }

    private Category parseCategory(String baseURI, Element eCategory) {
        Category category = new Category();
        String att = this.getAttributeValue(eCategory, "term");
        if (att != null) {
            category.setTerm(att);
        }
        if ((att = this.getAttributeValue(eCategory, "scheme")) != null) {
            category.setScheme(att);
            if (Atom10Parser.isRelativeURI(att)) {
                category.setSchemeResolved(Atom10Parser.resolveURI(baseURI, (Parent)eCategory, att));
            }
        }
        if ((att = this.getAttributeValue(eCategory, "label")) != null) {
            category.setLabel(att);
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
            String string = url = !".".equals(url) && !"./".equals(url) ? url : "";
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
            Iterator links = linksList.iterator();
            while (links.hasNext()) {
                Element link = (Element)links.next();
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
            Object ret = null;
            String[] parts = append.split("/");
            for (int i = 0; i < parts.length; ++i) {
                if (!"..".equals(parts[i])) continue;
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

    public static Entry parseEntry(Reader rd, String baseURI) throws JDOMException, IOException, IllegalArgumentException, FeedException {
        SAXBuilder builder = new SAXBuilder();
        Document entryDoc = builder.build(rd);
        Element fetchedEntryElement = entryDoc.getRootElement();
        fetchedEntryElement.detach();
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        Document feedDoc = wireFeedOutput.outputJDom(feed);
        feedDoc.getRootElement().addContent((org.jdom.Content)fetchedEntryElement);
        if (baseURI != null) {
            feedDoc.getRootElement().setAttribute("base", baseURI, Namespace.XML_NAMESPACE);
        }
        WireFeedInput input = new WireFeedInput();
        Feed parsedFeed = (Feed)input.build(feedDoc);
        return (Entry)parsedFeed.getEntries().get(0);
    }
}

