/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Alternatives
 *  com.rometools.utils.Lists
 *  com.rometools.utils.Strings
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.synd.Converter;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndLinkImpl;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;
import com.rometools.utils.Alternatives;
import com.rometools.utils.Lists;
import com.rometools.utils.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

public class ConverterForAtom03
implements Converter {
    private final String type;

    public ConverterForAtom03() {
        this("atom_0.3");
    }

    protected ConverterForAtom03(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Date date;
        String copyright;
        List<SyndPerson> authors;
        String language;
        List<Entry> aEntries;
        List<Link> otherLinks;
        SyndImageImpl image;
        Feed aFeed = (Feed)feed;
        syndFeed.setModules(ModuleUtils.cloneModules(aFeed.getModules()));
        List<Element> foreignMarkup = feed.getForeignMarkup();
        if (Lists.isNotEmpty(foreignMarkup)) {
            syndFeed.setForeignMarkup(foreignMarkup);
        }
        syndFeed.setEncoding(aFeed.getEncoding());
        syndFeed.setStyleSheet(aFeed.getStyleSheet());
        String logo = aFeed.getLogo();
        String icon = aFeed.getIcon();
        if (logo != null) {
            image = new SyndImageImpl();
            image.setUrl(logo);
            syndFeed.setImage(image);
        } else if (icon != null) {
            image = new SyndImageImpl();
            image.setUrl(icon);
            syndFeed.setImage(image);
        }
        syndFeed.setUri(aFeed.getId());
        syndFeed.setTitle(aFeed.getTitle());
        List<Link> alternateLinks = aFeed.getAlternateLinks();
        if (Lists.isNotEmpty(alternateLinks)) {
            Link link = alternateLinks.get(0);
            syndFeed.setLink(link.getHrefResolved());
        }
        ArrayList<SyndLink> syndLinks = new ArrayList<SyndLink>();
        if (Lists.isNotEmpty(alternateLinks)) {
            syndLinks.addAll(this.createSyndLinks(alternateLinks));
        }
        if (Lists.isNotEmpty(otherLinks = aFeed.getOtherLinks())) {
            syndLinks.addAll(this.createSyndLinks(otherLinks));
        }
        syndFeed.setLinks(syndLinks);
        Content tagline = aFeed.getTagline();
        if (tagline != null) {
            syndFeed.setDescription(tagline.getValue());
        }
        if (Lists.isNotEmpty(aEntries = aFeed.getEntries())) {
            syndFeed.setEntries(this.createSyndEntries(aEntries, syndFeed.isPreservingWireFeed()));
        }
        if ((language = aFeed.getLanguage()) != null) {
            syndFeed.setLanguage(language);
        }
        if (Lists.isNotEmpty(authors = aFeed.getAuthors())) {
            syndFeed.setAuthors(ConverterForAtom03.createSyndPersons(authors));
        }
        if ((copyright = aFeed.getCopyright()) != null) {
            syndFeed.setCopyright(copyright);
        }
        if ((date = aFeed.getModified()) != null) {
            syndFeed.setPublishedDate(date);
        }
    }

    protected List<SyndLink> createSyndLinks(List<Link> atomLinks) {
        ArrayList<SyndLink> syndLinks = new ArrayList<SyndLink>();
        for (Link atomLink : atomLinks) {
            Link link = atomLink;
            if (link.getRel().equals("enclosure")) continue;
            SyndLink syndLink = this.createSyndLink(link);
            syndLinks.add(syndLink);
        }
        return syndLinks;
    }

    public SyndLink createSyndLink(Link link) {
        SyndLinkImpl syndLink = new SyndLinkImpl();
        syndLink.setRel(link.getRel());
        syndLink.setType(link.getType());
        syndLink.setHref(link.getHrefResolved());
        syndLink.setTitle(link.getTitle());
        return syndLink;
    }

    protected List<SyndEntry> createSyndEntries(List<Entry> atomEntries, boolean preserveWireItems) {
        ArrayList<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        for (Entry atomEntry : atomEntries) {
            syndEntries.add(this.createSyndEntry(atomEntry, preserveWireItems));
        }
        return syndEntries;
    }

    protected SyndEntry createSyndEntry(Entry entry, boolean preserveWireItem) {
        Date date;
        List<SyndPerson> authors;
        List<Content> contents;
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        if (preserveWireItem) {
            syndEntry.setWireEntry(entry);
        }
        syndEntry.setModules(ModuleUtils.cloneModules(entry.getModules()));
        List<Element> foreignMarkup = entry.getForeignMarkup();
        if (Lists.isNotEmpty(foreignMarkup)) {
            syndEntry.setForeignMarkup(foreignMarkup);
        }
        syndEntry.setTitle(entry.getTitle());
        List<Link> alternateLinks = entry.getAlternateLinks();
        if (Lists.sizeIs(alternateLinks, (int)1)) {
            Link theLink = alternateLinks.get(0);
            syndEntry.setLink(theLink.getHrefResolved());
        }
        ArrayList<SyndEnclosure> syndEnclosures = new ArrayList<SyndEnclosure>();
        List<Link> otherLinks = entry.getOtherLinks();
        if (Lists.isNotEmpty(otherLinks)) {
            for (Link otherLink : otherLinks) {
                Link thisLink = otherLink;
                if (!"enclosure".equals(thisLink.getRel())) continue;
                syndEnclosures.add(this.createSyndEnclosure(entry, thisLink));
            }
        }
        syndEntry.setEnclosures(syndEnclosures);
        ArrayList<SyndLink> syndLinks = new ArrayList<SyndLink>();
        if (Lists.isNotEmpty(alternateLinks)) {
            syndLinks.addAll(this.createSyndLinks(alternateLinks));
        }
        if (Lists.isNotEmpty(otherLinks)) {
            syndLinks.addAll(this.createSyndLinks(otherLinks));
        }
        syndEntry.setLinks(syndLinks);
        String id = entry.getId();
        if (id != null) {
            syndEntry.setUri(id);
        } else {
            String link = syndEntry.getLink();
            syndEntry.setUri(link);
        }
        Content summary = entry.getSummary();
        if (summary == null) {
            contents = entry.getContents();
            if (Lists.isNotEmpty(contents)) {
                summary = contents.get(0);
            }
        } else {
            SyndContentImpl sContent = new SyndContentImpl();
            sContent.setType(summary.getType());
            sContent.setValue(summary.getValue());
            syndEntry.setDescription(sContent);
        }
        contents = entry.getContents();
        if (Lists.isNotEmpty(contents)) {
            ArrayList<SyndContent> sContents = new ArrayList<SyndContent>();
            for (Content content : contents) {
                SyndContentImpl sContent = new SyndContentImpl();
                sContent.setType(content.getType());
                sContent.setValue(content.getValue());
                sContent.setMode(content.getMode());
                sContents.add(sContent);
            }
            syndEntry.setContents(sContents);
        }
        if (Lists.isNotEmpty(authors = entry.getAuthors())) {
            syndEntry.setAuthors(ConverterForAtom03.createSyndPersons(authors));
            SyndPerson firstPerson = syndEntry.getAuthors().get(0);
            syndEntry.setAuthor(firstPerson.getName());
        }
        if ((date = entry.getModified()) == null) {
            date = (Date)Alternatives.firstNotNull((Object[])new Date[]{entry.getIssued(), entry.getCreated()});
        }
        if (date != null) {
            syndEntry.setPublishedDate(date);
        }
        return syndEntry;
    }

    public SyndEnclosure createSyndEnclosure(Entry entry, Link link) {
        SyndEnclosureImpl syndEncl = new SyndEnclosureImpl();
        syndEncl.setUrl(link.getHrefResolved());
        syndEncl.setType(link.getType());
        syndEncl.setLength(link.getLength());
        return syndEncl;
    }

    @Override
    public WireFeed createRealFeed(SyndFeed syndFeed) {
        String sDesc;
        Feed aFeed = new Feed(this.getType());
        aFeed.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));
        aFeed.setEncoding(syndFeed.getEncoding());
        aFeed.setStyleSheet(syndFeed.getStyleSheet());
        aFeed.setId(syndFeed.getUri());
        SyndContent sTitle = syndFeed.getTitleEx();
        if (sTitle != null) {
            String mode;
            Content title = new Content();
            String type = sTitle.getType();
            if (type != null) {
                title.setType(type);
            }
            if ((mode = sTitle.getMode()) != null) {
                title.setMode(mode);
            }
            title.setValue(sTitle.getValue());
            aFeed.setTitleEx(title);
        }
        ArrayList<Link> alternateLinks = new ArrayList<Link>();
        ArrayList<Link> otherLinks = new ArrayList<Link>();
        List<SyndLink> slinks = syndFeed.getLinks();
        if (slinks != null) {
            for (SyndLink syndLink2 : slinks) {
                SyndLink syndLink = syndLink2;
                Link link = this.createAtomLink(syndLink);
                String rel = link.getRel();
                if (Strings.isBlank((String)rel) || "alternate".equals(rel)) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        String sLink = syndFeed.getLink();
        if (alternateLinks.isEmpty() && sLink != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(sLink);
            alternateLinks.add(link);
        }
        if (!alternateLinks.isEmpty()) {
            aFeed.setAlternateLinks(alternateLinks);
        }
        if (!otherLinks.isEmpty()) {
            aFeed.setOtherLinks(otherLinks);
        }
        if ((sDesc = syndFeed.getDescription()) != null) {
            Content tagline = new Content();
            tagline.setValue(sDesc);
            aFeed.setTagline(tagline);
        }
        aFeed.setLanguage(syndFeed.getLanguage());
        List<SyndPerson> authors = syndFeed.getAuthors();
        if (Lists.isNotEmpty(authors)) {
            aFeed.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        }
        aFeed.setCopyright(syndFeed.getCopyright());
        aFeed.setModified(syndFeed.getPublishedDate());
        List<SyndEntry> sEntries = syndFeed.getEntries();
        if (sEntries != null) {
            aFeed.setEntries(this.createAtomEntries(sEntries));
        }
        return aFeed;
    }

    protected static List<SyndPerson> createAtomPersons(List<SyndPerson> sPersons) {
        ArrayList<SyndPerson> persons = new ArrayList<SyndPerson>();
        Iterator<SyndPerson> iterator = sPersons.iterator();
        while (iterator.hasNext()) {
            SyndPerson syndPerson;
            SyndPerson sPerson = syndPerson = iterator.next();
            Person person = new Person();
            person.setName(sPerson.getName());
            person.setUri(sPerson.getUri());
            person.setEmail(sPerson.getEmail());
            person.setModules(sPerson.getModules());
            persons.add(person);
        }
        return persons;
    }

    protected static List<SyndPerson> createSyndPersons(List<SyndPerson> aPersons) {
        ArrayList<SyndPerson> persons = new ArrayList<SyndPerson>();
        for (SyndPerson person2 : aPersons) {
            SyndPersonImpl person = new SyndPersonImpl();
            person.setName(person2.getName());
            person.setUri(person2.getUri());
            person.setEmail(person2.getEmail());
            person.setModules(person2.getModules());
            persons.add(person);
        }
        return persons;
    }

    protected List<Entry> createAtomEntries(List<SyndEntry> syndEntries) {
        ArrayList<Entry> atomEntries = new ArrayList<Entry>();
        for (SyndEntry syndEntry : syndEntries) {
            atomEntries.add(this.createAtomEntry(syndEntry));
        }
        return atomEntries;
    }

    protected Entry createAtomEntry(SyndEntry sEntry) {
        List<SyndContent> contents;
        SyndContent sContent;
        List<SyndEnclosure> sEnclosures;
        Entry aEntry = new Entry();
        aEntry.setModules(ModuleUtils.cloneModules(sEntry.getModules()));
        aEntry.setId(sEntry.getUri());
        SyndContent sTitle = sEntry.getTitleEx();
        if (sTitle != null) {
            String mode;
            Content title = new Content();
            String type = sTitle.getType();
            if (type != null) {
                title.setType(type);
            }
            if ((mode = sTitle.getMode()) != null) {
                title.setMode(mode);
            }
            title.setValue(sTitle.getValue());
            aEntry.setTitleEx(title);
        }
        ArrayList<Link> alternateLinks = new ArrayList<Link>();
        ArrayList<Link> otherLinks = new ArrayList<Link>();
        List<SyndLink> syndLinks = sEntry.getLinks();
        if (syndLinks != null) {
            for (SyndLink syndLink : syndLinks) {
                Link link = this.createAtomLink(syndLink);
                String rel = link.getRel();
                if (Strings.isBlank((String)rel) || "alternate".equals(rel)) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        String sLink = sEntry.getLink();
        if (alternateLinks.isEmpty() && sLink != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(sLink);
            alternateLinks.add(link);
        }
        if ((sEnclosures = sEntry.getEnclosures()) != null) {
            for (SyndEnclosure syndEnclosure : sEnclosures) {
                Link link = this.createAtomEnclosure(syndEnclosure);
                otherLinks.add(link);
            }
        }
        if (!alternateLinks.isEmpty()) {
            aEntry.setAlternateLinks(alternateLinks);
        }
        if (!otherLinks.isEmpty()) {
            aEntry.setOtherLinks(otherLinks);
        }
        if ((sContent = sEntry.getDescription()) != null) {
            Content content = new Content();
            content.setType(sContent.getType());
            content.setValue(sContent.getValue());
            content.setMode("escaped");
            aEntry.setSummary(content);
        }
        if (!(contents = sEntry.getContents()).isEmpty()) {
            ArrayList<Content> aContents = new ArrayList<Content>();
            for (SyndContent syndContent : contents) {
                Content content = new Content();
                content.setType(syndContent.getType());
                content.setValue(syndContent.getValue());
                content.setMode(syndContent.getMode());
                aContents.add(content);
            }
            aEntry.setContents(aContents);
        }
        List<SyndPerson> sAuthors = sEntry.getAuthors();
        String author = sEntry.getAuthor();
        if (Lists.isNotEmpty(sAuthors)) {
            aEntry.setAuthors(ConverterForAtom03.createAtomPersons(sAuthors));
        } else if (author != null) {
            Person person = new Person();
            person.setName(author);
            ArrayList<SyndPerson> authors = new ArrayList<SyndPerson>();
            authors.add(person);
            aEntry.setAuthors(authors);
        }
        aEntry.setModified(sEntry.getPublishedDate());
        aEntry.setIssued(sEntry.getPublishedDate());
        return aEntry;
    }

    public Link createAtomLink(SyndLink syndLink) {
        Link link = new Link();
        link.setRel(syndLink.getRel());
        link.setType(syndLink.getType());
        link.setHref(syndLink.getHref());
        link.setTitle(syndLink.getTitle());
        return link;
    }

    public Link createAtomEnclosure(SyndEnclosure syndEnclosure) {
        Link link = new Link();
        link.setRel("enclosure");
        link.setType(syndEnclosure.getType());
        link.setHref(syndEnclosure.getUrl());
        link.setLength(syndEnclosure.getLength());
        return link;
    }
}

