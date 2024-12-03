/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.synd.Converter;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.impl.ConverterForAtom03;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ConverterForAtom10
implements Converter {
    private String _type;

    public ConverterForAtom10() {
        this("atom_1.0");
    }

    protected ConverterForAtom10(String type) {
        this._type = type;
    }

    public String getType() {
        return this._type;
    }

    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Date date;
        String rights;
        List contributors;
        List authors;
        Content aSubtitle;
        Feed aFeed = (Feed)feed;
        syndFeed.setModules(ModuleUtils.cloneModules(aFeed.getModules()));
        if (((List)feed.getForeignMarkup()).size() > 0) {
            syndFeed.setForeignMarkup((List)feed.getForeignMarkup());
        }
        syndFeed.setEncoding(aFeed.getEncoding());
        syndFeed.setUri(aFeed.getId());
        Content aTitle = aFeed.getTitleEx();
        if (aTitle != null) {
            SyndContentImpl c = new SyndContentImpl();
            c.setType(aTitle.getType());
            c.setValue(aTitle.getValue());
            syndFeed.setTitleEx(c);
        }
        if ((aSubtitle = aFeed.getSubtitle()) != null) {
            SyndContentImpl c = new SyndContentImpl();
            c.setType(aSubtitle.getType());
            c.setValue(aSubtitle.getValue());
            syndFeed.setDescriptionEx(c);
        }
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
        List aEntries = aFeed.getEntries();
        if (aEntries != null) {
            syndFeed.setEntries(this.createSyndEntries(aFeed, aEntries, syndFeed.isPreservingWireFeed()));
        }
        if ((authors = aFeed.getAuthors()) != null && authors.size() > 0) {
            syndFeed.setAuthors(ConverterForAtom03.createSyndPersons(authors));
        }
        if ((contributors = aFeed.getContributors()) != null && contributors.size() > 0) {
            syndFeed.setContributors(ConverterForAtom03.createSyndPersons(contributors));
        }
        if ((rights = aFeed.getRights()) != null) {
            syndFeed.setCopyright(rights);
        }
        if ((date = aFeed.getUpdated()) != null) {
            syndFeed.setPublishedDate(date);
        }
    }

    protected List createSyndLinks(List aLinks) {
        ArrayList<SyndLink> sLinks = new ArrayList<SyndLink>();
        Iterator iter = aLinks.iterator();
        while (iter.hasNext()) {
            Link link = (Link)iter.next();
            SyndLink sLink = this.createSyndLink(link);
            sLinks.add(sLink);
        }
        return sLinks;
    }

    protected List createSyndEntries(Feed feed, List atomEntries, boolean preserveWireItems) {
        ArrayList<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        for (int i = 0; i < atomEntries.size(); ++i) {
            syndEntries.add(this.createSyndEntry(feed, (Entry)atomEntries.get(i), preserveWireItems));
        }
        return syndEntries;
    }

    protected SyndEntry createSyndEntry(Feed feed, Entry entry, boolean preserveWireItem) {
        List categories;
        Date date;
        List contributors;
        List authors;
        List contents;
        Content summary;
        Content eTitle;
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        if (preserveWireItem) {
            syndEntry.setWireEntry(entry);
        }
        syndEntry.setModules(ModuleUtils.cloneModules(entry.getModules()));
        if (((List)entry.getForeignMarkup()).size() > 0) {
            syndEntry.setForeignMarkup((List)entry.getForeignMarkup());
        }
        if ((eTitle = entry.getTitleEx()) != null) {
            syndEntry.setTitleEx(this.createSyndContent(eTitle));
        }
        if ((summary = entry.getSummary()) != null) {
            syndEntry.setDescription(this.createSyndContent(summary));
        }
        if ((contents = entry.getContents()) != null && contents.size() > 0) {
            ArrayList<SyndContent> sContents = new ArrayList<SyndContent>();
            Iterator iter = contents.iterator();
            while (iter.hasNext()) {
                Content content = (Content)iter.next();
                sContents.add(this.createSyndContent(content));
            }
            syndEntry.setContents(sContents);
        }
        if ((authors = entry.getAuthors()) != null && authors.size() > 0) {
            syndEntry.setAuthors(ConverterForAtom03.createSyndPersons(authors));
            SyndPerson person0 = (SyndPerson)syndEntry.getAuthors().get(0);
            syndEntry.setAuthor(person0.getName());
        }
        if ((contributors = entry.getContributors()) != null && contributors.size() > 0) {
            syndEntry.setContributors(ConverterForAtom03.createSyndPersons(contributors));
        }
        if ((date = entry.getPublished()) != null) {
            syndEntry.setPublishedDate(date);
        }
        if ((date = entry.getUpdated()) != null) {
            syndEntry.setUpdatedDate(date);
        }
        if ((categories = entry.getCategories()) != null) {
            ArrayList<SyndCategoryImpl> syndCategories = new ArrayList<SyndCategoryImpl>();
            Iterator iter = categories.iterator();
            while (iter.hasNext()) {
                Category c = (Category)iter.next();
                SyndCategoryImpl syndCategory = new SyndCategoryImpl();
                syndCategory.setName(c.getTerm());
                syndCategory.setTaxonomyUri(c.getSchemeResolved());
                syndCategories.add(syndCategory);
            }
            syndEntry.setCategories(syndCategories);
        }
        if (entry.getAlternateLinks() != null && entry.getAlternateLinks().size() > 0) {
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
                syndEnclosures.add(this.createSyndEnclosure(feed, entry, thisLink));
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
        Feed source = entry.getSource();
        if (source != null) {
            SyndFeedImpl syndSource = new SyndFeedImpl(source);
            syndEntry.setSource(syndSource);
        }
        return syndEntry;
    }

    public SyndEnclosure createSyndEnclosure(Feed feed, Entry entry, Link link) {
        SyndEnclosureImpl syndEncl = new SyndEnclosureImpl();
        syndEncl.setUrl(link.getHrefResolved());
        syndEncl.setType(link.getType());
        syndEncl.setLength(link.getLength());
        return syndEncl;
    }

    public Link createAtomEnclosure(SyndEnclosure syndEnclosure) {
        Link link = new Link();
        link.setRel("enclosure");
        link.setType(syndEnclosure.getType());
        link.setHref(syndEnclosure.getUrl());
        link.setLength(syndEnclosure.getLength());
        return link;
    }

    public SyndLink createSyndLink(Link link) {
        SyndLinkImpl syndLink = new SyndLinkImpl();
        syndLink.setRel(link.getRel());
        syndLink.setType(link.getType());
        syndLink.setHref(link.getHrefResolved());
        syndLink.setHreflang(link.getHreflang());
        syndLink.setLength(link.getLength());
        syndLink.setTitle(link.getTitle());
        return syndLink;
    }

    public Link createAtomLink(SyndLink syndLink) {
        Link link = new Link();
        link.setRel(syndLink.getRel());
        link.setType(syndLink.getType());
        link.setHref(syndLink.getHref());
        link.setHreflang(syndLink.getHreflang());
        link.setLength(syndLink.getLength());
        link.setTitle(syndLink.getTitle());
        return link;
    }

    public WireFeed createRealFeed(SyndFeed syndFeed) {
        List contributors;
        List authors;
        SyndContent sDesc;
        Feed aFeed = new Feed(this.getType());
        aFeed.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));
        aFeed.setEncoding(syndFeed.getEncoding());
        aFeed.setId(syndFeed.getUri());
        SyndContent sTitle = syndFeed.getTitleEx();
        if (sTitle != null) {
            Content title = new Content();
            title.setType(sTitle.getType());
            title.setValue(sTitle.getValue());
            aFeed.setTitleEx(title);
        }
        if ((sDesc = syndFeed.getDescriptionEx()) != null) {
            Content subtitle = new Content();
            subtitle.setType(sDesc.getType());
            subtitle.setValue(sDesc.getValue());
            aFeed.setSubtitle(subtitle);
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
        List sCats = syndFeed.getCategories();
        ArrayList<Category> aCats = new ArrayList<Category>();
        if (sCats != null) {
            Iterator iter = sCats.iterator();
            while (iter.hasNext()) {
                SyndCategory sCat = (SyndCategory)iter.next();
                Category aCat = new Category();
                aCat.setTerm(sCat.getName());
                aCat.setScheme(sCat.getTaxonomyUri());
                aCats.add(aCat);
            }
        }
        if (aCats.size() > 0) {
            aFeed.setCategories(aCats);
        }
        if ((authors = syndFeed.getAuthors()) != null && authors.size() > 0) {
            aFeed.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        }
        if ((contributors = syndFeed.getContributors()) != null && contributors.size() > 0) {
            aFeed.setContributors(ConverterForAtom03.createAtomPersons(contributors));
        }
        aFeed.setRights(syndFeed.getCopyright());
        aFeed.setUpdated(syndFeed.getPublishedDate());
        List sEntries = syndFeed.getEntries();
        if (sEntries != null) {
            aFeed.setEntries(this.createAtomEntries(sEntries));
        }
        if (((List)syndFeed.getForeignMarkup()).size() > 0) {
            aFeed.setForeignMarkup(syndFeed.getForeignMarkup());
        }
        return aFeed;
    }

    protected SyndContent createSyndContent(Content content) {
        SyndContentImpl sContent = new SyndContentImpl();
        sContent.setType(content.getType());
        sContent.setValue(content.getValue());
        return sContent;
    }

    protected List createAtomEntries(List syndEntries) {
        ArrayList<Entry> atomEntries = new ArrayList<Entry>();
        for (int i = 0; i < syndEntries.size(); ++i) {
            atomEntries.add(this.createAtomEntry((SyndEntry)syndEntries.get(i)));
        }
        return atomEntries;
    }

    protected Content createAtomContent(SyndContent sContent) {
        Content content = new Content();
        content.setType(sContent.getType());
        content.setValue(sContent.getValue());
        return content;
    }

    protected List createAtomContents(List syndContents) {
        ArrayList<Content> atomContents = new ArrayList<Content>();
        for (int i = 0; i < syndContents.size(); ++i) {
            atomContents.add(this.createAtomContent((SyndContent)syndContents.get(i)));
        }
        return atomContents;
    }

    protected Entry createAtomEntry(SyndEntry sEntry) {
        SyndFeed sSource;
        Link link;
        Iterator iter;
        SyndContent sDescription;
        Entry aEntry = new Entry();
        aEntry.setModules(ModuleUtils.cloneModules(sEntry.getModules()));
        aEntry.setId(sEntry.getUri());
        SyndContent sTitle = sEntry.getTitleEx();
        if (sTitle != null) {
            Content title = new Content();
            title.setType(sTitle.getType());
            title.setValue(sTitle.getValue());
            aEntry.setTitleEx(title);
        }
        if ((sDescription = sEntry.getDescription()) != null) {
            Content summary = new Content();
            summary.setType(sDescription.getType());
            summary.setValue(sDescription.getValue());
            aEntry.setSummary(summary);
        }
        ArrayList<Link> alternateLinks = new ArrayList<Link>();
        ArrayList<Link> otherLinks = new ArrayList<Link>();
        List slinks = sEntry.getLinks();
        List enclosures = sEntry.getEnclosures();
        boolean linkRelEnclosureExists = false;
        if (slinks != null) {
            iter = slinks.iterator();
            while (iter.hasNext()) {
                SyndLink syndLink = (SyndLink)iter.next();
                link = this.createAtomLink(syndLink);
                if (syndLink.getRel() != null && "enclosure".equals(syndLink.getRel())) {
                    linkRelEnclosureExists = true;
                }
                if (link.getRel() == null || "".equals(link.getRel().trim()) || "alternate".equals(syndLink.getRel())) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        if (alternateLinks.size() == 0 && sEntry.getLink() != null) {
            Link link2 = new Link();
            link2.setRel("alternate");
            link2.setHref(sEntry.getLink());
            alternateLinks.add(link2);
        }
        if (enclosures != null && !linkRelEnclosureExists) {
            iter = enclosures.iterator();
            while (iter.hasNext()) {
                SyndEnclosure syndEncl = (SyndEnclosure)iter.next();
                link = this.createAtomEnclosure(syndEncl);
                otherLinks.add(link);
            }
        }
        if (alternateLinks.size() > 0) {
            aEntry.setAlternateLinks(alternateLinks);
        }
        if (otherLinks.size() > 0) {
            aEntry.setOtherLinks(otherLinks);
        }
        List sCats = sEntry.getCategories();
        ArrayList<Category> aCats = new ArrayList<Category>();
        if (sCats != null) {
            Iterator iter2 = sCats.iterator();
            while (iter2.hasNext()) {
                SyndCategory sCat = (SyndCategory)iter2.next();
                Category aCat = new Category();
                aCat.setTerm(sCat.getName());
                aCat.setScheme(sCat.getTaxonomyUri());
                aCats.add(aCat);
            }
        }
        if (aCats.size() > 0) {
            aEntry.setCategories(aCats);
        }
        List syndContents = sEntry.getContents();
        aEntry.setContents(this.createAtomContents(syndContents));
        ArrayList<Person> authors = sEntry.getAuthors();
        if (authors != null && authors.size() > 0) {
            aEntry.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        } else if (sEntry.getAuthor() != null) {
            Person person = new Person();
            person.setName(sEntry.getAuthor());
            authors = new ArrayList<Person>();
            authors.add(person);
            aEntry.setAuthors(authors);
        }
        List contributors = sEntry.getContributors();
        if (contributors != null && contributors.size() > 0) {
            aEntry.setContributors(ConverterForAtom03.createAtomPersons(contributors));
        }
        aEntry.setPublished(sEntry.getPublishedDate());
        if (sEntry.getUpdatedDate() != null) {
            aEntry.setUpdated(sEntry.getUpdatedDate());
        } else {
            aEntry.setUpdated(sEntry.getPublishedDate());
        }
        if (((List)sEntry.getForeignMarkup()).size() > 0) {
            aEntry.setForeignMarkup((List)sEntry.getForeignMarkup());
        }
        if ((sSource = sEntry.getSource()) != null) {
            Feed aSource = (Feed)sSource.createWireFeed(this.getType());
            aEntry.setSource(aSource);
        }
        return aEntry;
    }
}

