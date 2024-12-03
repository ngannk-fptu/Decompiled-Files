/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.synd.Converter;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ConverterForAtom03
implements Converter {
    private String _type;

    public ConverterForAtom03() {
        this("atom_0.3");
    }

    protected ConverterForAtom03(String type) {
        this._type = type;
    }

    public String getType() {
        return this._type;
    }

    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Date date;
        String copyright;
        List authors;
        String language;
        List aEntries;
        Feed aFeed = (Feed)feed;
        syndFeed.setModules(ModuleUtils.cloneModules(aFeed.getModules()));
        if (((List)feed.getForeignMarkup()).size() > 0) {
            syndFeed.setForeignMarkup(feed.getForeignMarkup());
        }
        syndFeed.setEncoding(aFeed.getEncoding());
        syndFeed.setUri(aFeed.getId());
        syndFeed.setTitle(aFeed.getTitle());
        if (aFeed.getAlternateLinks() != null && aFeed.getAlternateLinks().size() > 0) {
            Link theLink = (Link)aFeed.getAlternateLinks().get(0);
            syndFeed.setLink(theLink.getHrefResolved());
        }
        ArrayList syndLinks = new ArrayList();
        if (aFeed.getAlternateLinks() != null && aFeed.getAlternateLinks().size() > 0) {
            syndLinks.addAll(this.createSyndLinks(aFeed.getAlternateLinks()));
        }
        if (aFeed.getOtherLinks() != null && aFeed.getOtherLinks().size() > 0) {
            syndLinks.addAll(this.createSyndLinks(aFeed.getOtherLinks()));
        }
        syndFeed.setLinks(syndLinks);
        Content tagline = aFeed.getTagline();
        if (tagline != null) {
            syndFeed.setDescription(tagline.getValue());
        }
        if ((aEntries = aFeed.getEntries()) != null) {
            syndFeed.setEntries(this.createSyndEntries(aEntries, syndFeed.isPreservingWireFeed()));
        }
        if ((language = aFeed.getLanguage()) != null) {
            syndFeed.setLanguage(language);
        }
        if ((authors = aFeed.getAuthors()) != null && authors.size() > 0) {
            syndFeed.setAuthors(ConverterForAtom03.createSyndPersons(authors));
        }
        if ((copyright = aFeed.getCopyright()) != null) {
            syndFeed.setCopyright(copyright);
        }
        if ((date = aFeed.getModified()) != null) {
            syndFeed.setPublishedDate(date);
        }
    }

    protected List createSyndLinks(List aLinks) {
        ArrayList<SyndLink> sLinks = new ArrayList<SyndLink>();
        Iterator iter = aLinks.iterator();
        while (iter.hasNext()) {
            Link link = (Link)iter.next();
            if (link.getRel().equals("enclosure")) continue;
            SyndLink sLink = this.createSyndLink(link);
            sLinks.add(sLink);
        }
        return sLinks;
    }

    public SyndLink createSyndLink(Link link) {
        SyndLinkImpl syndLink = new SyndLinkImpl();
        syndLink.setRel(link.getRel());
        syndLink.setType(link.getType());
        syndLink.setHref(link.getHrefResolved());
        syndLink.setTitle(link.getTitle());
        return syndLink;
    }

    protected List createSyndEntries(List atomEntries, boolean preserveWireItems) {
        ArrayList<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        for (int i = 0; i < atomEntries.size(); ++i) {
            syndEntries.add(this.createSyndEntry((Entry)atomEntries.get(i), preserveWireItems));
        }
        return syndEntries;
    }

    protected SyndEntry createSyndEntry(Entry entry, boolean preserveWireItem) {
        Date date;
        List authors;
        List contents;
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        if (preserveWireItem) {
            syndEntry.setWireEntry(entry);
        }
        syndEntry.setModules(ModuleUtils.cloneModules(entry.getModules()));
        if (((List)entry.getForeignMarkup()).size() > 0) {
            syndEntry.setForeignMarkup((List)entry.getForeignMarkup());
        }
        syndEntry.setTitle(entry.getTitle());
        if (entry.getAlternateLinks() != null && entry.getAlternateLinks().size() == 1) {
            Link theLink = (Link)entry.getAlternateLinks().get(0);
            syndEntry.setLink(theLink.getHrefResolved());
        }
        ArrayList<SyndEnclosure> syndEnclosures = new ArrayList<SyndEnclosure>();
        if (entry.getOtherLinks() != null && entry.getOtherLinks().size() > 0) {
            List oLinks = entry.getOtherLinks();
            Iterator iter = oLinks.iterator();
            while (iter.hasNext()) {
                Link thisLink = (Link)iter.next();
                if (!"enclosure".equals(thisLink.getRel())) continue;
                syndEnclosures.add(this.createSyndEnclosure(entry, thisLink));
            }
        }
        syndEntry.setEnclosures(syndEnclosures);
        ArrayList syndLinks = new ArrayList();
        if (entry.getAlternateLinks() != null && entry.getAlternateLinks().size() > 0) {
            syndLinks.addAll(this.createSyndLinks(entry.getAlternateLinks()));
        }
        if (entry.getOtherLinks() != null && entry.getOtherLinks().size() > 0) {
            syndLinks.addAll(this.createSyndLinks(entry.getOtherLinks()));
        }
        syndEntry.setLinks(syndLinks);
        String id = entry.getId();
        if (id != null) {
            syndEntry.setUri(entry.getId());
        } else {
            syndEntry.setUri(syndEntry.getLink());
        }
        Content content = entry.getSummary();
        if (content == null && (contents = entry.getContents()) != null && contents.size() > 0) {
            content = (Content)contents.get(0);
        }
        if (content != null) {
            SyndContentImpl sContent = new SyndContentImpl();
            sContent.setType(content.getType());
            sContent.setValue(content.getValue());
            syndEntry.setDescription(sContent);
        }
        if ((contents = entry.getContents()).size() > 0) {
            ArrayList<SyndContentImpl> sContents = new ArrayList<SyndContentImpl>();
            for (int i = 0; i < contents.size(); ++i) {
                content = (Content)contents.get(i);
                SyndContentImpl sContent = new SyndContentImpl();
                sContent.setType(content.getType());
                sContent.setValue(content.getValue());
                sContent.setMode(content.getMode());
                sContents.add(sContent);
            }
            syndEntry.setContents(sContents);
        }
        if ((authors = entry.getAuthors()) != null && authors.size() > 0) {
            syndEntry.setAuthors(ConverterForAtom03.createSyndPersons(authors));
            SyndPerson person0 = (SyndPerson)syndEntry.getAuthors().get(0);
            syndEntry.setAuthor(person0.getName());
        }
        if ((date = entry.getModified()) == null && (date = entry.getIssued()) == null) {
            date = entry.getCreated();
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

    public WireFeed createRealFeed(SyndFeed syndFeed) {
        String sDesc;
        Feed aFeed = new Feed(this.getType());
        aFeed.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));
        aFeed.setEncoding(syndFeed.getEncoding());
        aFeed.setId(syndFeed.getUri());
        SyndContent sTitle = syndFeed.getTitleEx();
        if (sTitle != null) {
            Content title = new Content();
            if (sTitle.getType() != null) {
                title.setType(sTitle.getType());
            }
            if (sTitle.getMode() != null) {
                title.setMode(sTitle.getMode());
            }
            title.setValue(sTitle.getValue());
            aFeed.setTitleEx(title);
        }
        ArrayList<Link> alternateLinks = new ArrayList<Link>();
        ArrayList<Link> otherLinks = new ArrayList<Link>();
        List slinks = syndFeed.getLinks();
        if (slinks != null) {
            Iterator iter = slinks.iterator();
            while (iter.hasNext()) {
                SyndLink syndLink = (SyndLink)iter.next();
                Link link = this.createAtomLink(syndLink);
                if (link.getRel() == null || "".equals(link.getRel().trim()) || "alternate".equals(link.getRel())) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        if (alternateLinks.size() == 0 && syndFeed.getLink() != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(syndFeed.getLink());
            alternateLinks.add(link);
        }
        if (alternateLinks.size() > 0) {
            aFeed.setAlternateLinks(alternateLinks);
        }
        if (otherLinks.size() > 0) {
            aFeed.setOtherLinks(otherLinks);
        }
        if ((sDesc = syndFeed.getDescription()) != null) {
            Content tagline = new Content();
            tagline.setValue(sDesc);
            aFeed.setTagline(tagline);
        }
        aFeed.setLanguage(syndFeed.getLanguage());
        List authors = syndFeed.getAuthors();
        if (authors != null && authors.size() > 0) {
            aFeed.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        }
        aFeed.setCopyright(syndFeed.getCopyright());
        aFeed.setModified(syndFeed.getPublishedDate());
        List sEntries = syndFeed.getEntries();
        if (sEntries != null) {
            aFeed.setEntries(this.createAtomEntries(sEntries));
        }
        return aFeed;
    }

    protected static List createAtomPersons(List sPersons) {
        ArrayList<Person> persons = new ArrayList<Person>();
        Iterator iter = sPersons.iterator();
        while (iter.hasNext()) {
            SyndPerson sPerson = (SyndPerson)iter.next();
            Person person = new Person();
            person.setName(sPerson.getName());
            person.setUri(sPerson.getUri());
            person.setEmail(sPerson.getEmail());
            person.setModules(sPerson.getModules());
            persons.add(person);
        }
        return persons;
    }

    protected static List createSyndPersons(List aPersons) {
        ArrayList<SyndPersonImpl> persons = new ArrayList<SyndPersonImpl>();
        Iterator iter = aPersons.iterator();
        while (iter.hasNext()) {
            Person aPerson = (Person)iter.next();
            SyndPersonImpl person = new SyndPersonImpl();
            person.setName(aPerson.getName());
            person.setUri(aPerson.getUri());
            person.setEmail(aPerson.getEmail());
            person.setModules(aPerson.getModules());
            persons.add(person);
        }
        return persons;
    }

    protected List createAtomEntries(List syndEntries) {
        ArrayList<Entry> atomEntries = new ArrayList<Entry>();
        for (int i = 0; i < syndEntries.size(); ++i) {
            atomEntries.add(this.createAtomEntry((SyndEntry)syndEntries.get(i)));
        }
        return atomEntries;
    }

    protected Entry createAtomEntry(SyndEntry sEntry) {
        List sAuthors;
        List contents;
        SyndContent sContent;
        List sEnclosures;
        Entry aEntry = new Entry();
        aEntry.setModules(ModuleUtils.cloneModules(sEntry.getModules()));
        aEntry.setId(sEntry.getUri());
        SyndContent sTitle = sEntry.getTitleEx();
        if (sTitle != null) {
            Content title = new Content();
            if (sTitle.getType() != null) {
                title.setType(sTitle.getType());
            }
            if (sTitle.getMode() != null) {
                title.setMode(sTitle.getMode());
            }
            title.setValue(sTitle.getValue());
            aEntry.setTitleEx(title);
        }
        ArrayList<Link> alternateLinks = new ArrayList<Link>();
        ArrayList<Link> otherLinks = new ArrayList<Link>();
        List slinks = sEntry.getLinks();
        if (slinks != null) {
            Iterator iter = slinks.iterator();
            while (iter.hasNext()) {
                SyndLink syndLink = (SyndLink)iter.next();
                Link link = this.createAtomLink(syndLink);
                if (link.getRel() == null || "".equals(link.getRel().trim()) || "alternate".equals(link.getRel())) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        if (alternateLinks.size() == 0 && sEntry.getLink() != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(sEntry.getLink());
            alternateLinks.add(link);
        }
        if ((sEnclosures = sEntry.getEnclosures()) != null) {
            Iterator iter = sEnclosures.iterator();
            while (iter.hasNext()) {
                SyndEnclosure syndEnclosure = (SyndEnclosure)iter.next();
                Link link = this.createAtomEnclosure(syndEnclosure);
                otherLinks.add(link);
            }
        }
        if (alternateLinks.size() > 0) {
            aEntry.setAlternateLinks(alternateLinks);
        }
        if (otherLinks.size() > 0) {
            aEntry.setOtherLinks(otherLinks);
        }
        if ((sContent = sEntry.getDescription()) != null) {
            Content content = new Content();
            content.setType(sContent.getType());
            content.setValue(sContent.getValue());
            content.setMode("escaped");
            aEntry.setSummary(content);
        }
        if ((contents = sEntry.getContents()).size() > 0) {
            ArrayList<Content> aContents = new ArrayList<Content>();
            for (int i = 0; i < contents.size(); ++i) {
                sContent = (SyndContentImpl)contents.get(i);
                Content content = new Content();
                content.setType(sContent.getType());
                content.setValue(sContent.getValue());
                content.setMode(sContent.getMode());
                aContents.add(content);
            }
            aEntry.setContents(aContents);
        }
        if ((sAuthors = sEntry.getAuthors()) != null && sAuthors.size() > 0) {
            aEntry.setAuthors(ConverterForAtom03.createAtomPersons(sAuthors));
        } else if (sEntry.getAuthor() != null) {
            Person person = new Person();
            person.setName(sEntry.getAuthor());
            ArrayList<Person> authors = new ArrayList<Person>();
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

