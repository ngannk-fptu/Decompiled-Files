/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  com.rometools.utils.Strings
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.synd.Converter;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndLinkImpl;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.impl.ConverterForAtom03;
import com.rometools.utils.Lists;
import com.rometools.utils.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jdom2.Element;

public class ConverterForAtom10
implements Converter {
    private final String type;

    public ConverterForAtom10() {
        this("atom_1.0");
    }

    protected ConverterForAtom10(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Date date;
        String rights;
        List<SyndPerson> contributors;
        List<SyndPerson> authors;
        List<Link> otherLinks;
        List<Link> alternateLinks;
        Content aSubtitle;
        String icon;
        Feed aFeed = (Feed)feed;
        syndFeed.setModules(ModuleUtils.cloneModules(aFeed.getModules()));
        List<Element> foreignMarkup = feed.getForeignMarkup();
        if (!foreignMarkup.isEmpty()) {
            syndFeed.setForeignMarkup(foreignMarkup);
        }
        syndFeed.setEncoding(aFeed.getEncoding());
        syndFeed.setStyleSheet(aFeed.getStyleSheet());
        String logo = aFeed.getLogo();
        if (logo != null) {
            SyndImageImpl image = new SyndImageImpl();
            image.setUrl(logo);
            syndFeed.setImage(image);
        }
        if ((icon = aFeed.getIcon()) != null) {
            SyndImageImpl image = new SyndImageImpl();
            image.setUrl(icon);
            syndFeed.setIcon(image);
        }
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
        if (Lists.isNotEmpty(alternateLinks = aFeed.getAlternateLinks())) {
            Link theLink = alternateLinks.get(0);
            syndFeed.setLink(theLink.getHrefResolved());
        }
        ArrayList<SyndLink> syndLinks = new ArrayList<SyndLink>();
        if (Lists.isNotEmpty(alternateLinks)) {
            syndLinks.addAll(this.createSyndLinks(alternateLinks));
        }
        if (Lists.isNotEmpty(otherLinks = aFeed.getOtherLinks())) {
            syndLinks.addAll(this.createSyndLinks(otherLinks));
        }
        syndFeed.setLinks(syndLinks);
        List<Entry> aEntries = aFeed.getEntries();
        if (aEntries != null) {
            syndFeed.setEntries(this.createSyndEntries(aFeed, aEntries, syndFeed.isPreservingWireFeed()));
        }
        if (Lists.isNotEmpty(authors = aFeed.getAuthors())) {
            syndFeed.setAuthors(ConverterForAtom03.createSyndPersons(authors));
        }
        if (Lists.isNotEmpty(contributors = aFeed.getContributors())) {
            syndFeed.setContributors(ConverterForAtom03.createSyndPersons(contributors));
        }
        if ((rights = aFeed.getRights()) != null) {
            syndFeed.setCopyright(rights);
        }
        if ((date = aFeed.getUpdated()) != null) {
            syndFeed.setPublishedDate(date);
        }
    }

    protected List<SyndLink> createSyndLinks(List<Link> atomLinks) {
        ArrayList<SyndLink> syndLinks = new ArrayList<SyndLink>();
        for (Link atomLink : atomLinks) {
            SyndLink syndLink = this.createSyndLink(atomLink);
            syndLinks.add(syndLink);
        }
        return syndLinks;
    }

    protected List<SyndEntry> createSyndEntries(Feed feed, List<Entry> atomEntries, boolean preserveWireItems) {
        ArrayList<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        for (Entry atomEntry : atomEntries) {
            syndEntries.add(this.createSyndEntry(feed, atomEntry, preserveWireItems));
        }
        return syndEntries;
    }

    protected SyndEntry createSyndEntry(Feed feed, Entry entry, boolean preserveWireItem) {
        List<Link> alternateLinks;
        List<Category> categories;
        Date date;
        List<SyndPerson> contributors;
        List<SyndPerson> authors;
        List<Content> contents;
        Content summary;
        Content eTitle;
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        if (preserveWireItem) {
            syndEntry.setWireEntry(entry);
        }
        syndEntry.setModules(ModuleUtils.cloneModules(entry.getModules()));
        List<Element> foreignMarkup = entry.getForeignMarkup();
        if (!foreignMarkup.isEmpty()) {
            syndEntry.setForeignMarkup(foreignMarkup);
        }
        if ((eTitle = entry.getTitleEx()) != null) {
            syndEntry.setTitleEx(this.createSyndContent(eTitle));
        }
        if ((summary = entry.getSummary()) != null) {
            syndEntry.setDescription(this.createSyndContent(summary));
        }
        if (Lists.isNotEmpty(contents = entry.getContents())) {
            ArrayList<SyndContent> sContents = new ArrayList<SyndContent>();
            for (Content content : contents) {
                sContents.add(this.createSyndContent(content));
            }
            syndEntry.setContents(sContents);
        }
        if (Lists.isNotEmpty(authors = entry.getAuthors())) {
            syndEntry.setAuthors(ConverterForAtom03.createSyndPersons(authors));
            SyndPerson person0 = syndEntry.getAuthors().get(0);
            syndEntry.setAuthor(person0.getName());
        }
        if (Lists.isNotEmpty(contributors = entry.getContributors())) {
            syndEntry.setContributors(ConverterForAtom03.createSyndPersons(contributors));
        }
        if ((date = entry.getPublished()) != null) {
            syndEntry.setPublishedDate(date);
        }
        if ((date = entry.getUpdated()) != null) {
            syndEntry.setUpdatedDate(date);
        }
        if ((categories = entry.getCategories()) != null) {
            ArrayList<SyndCategory> syndCategories = new ArrayList<SyndCategory>();
            for (Category category : categories) {
                SyndCategoryImpl syndCategory = new SyndCategoryImpl();
                syndCategory.setName(category.getTerm());
                syndCategory.setTaxonomyUri(category.getSchemeResolved());
                syndCategory.setLabel(category.getLabel());
                syndCategories.add(syndCategory);
            }
            syndEntry.setCategories(syndCategories);
        }
        if (Lists.isNotEmpty(alternateLinks = entry.getAlternateLinks())) {
            Link theLink = alternateLinks.get(0);
            syndEntry.setLink(theLink.getHrefResolved());
        }
        ArrayList<SyndEnclosure> syndEnclosures = new ArrayList<SyndEnclosure>();
        List<Link> otherLinks = entry.getOtherLinks();
        if (Lists.isNotEmpty(otherLinks)) {
            List<Link> oLinks = otherLinks;
            for (Link link : oLinks) {
                if (!"enclosure".equals(link.getRel())) continue;
                syndEnclosures.add(this.createSyndEnclosure(feed, entry, link));
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

    @Override
    public WireFeed createRealFeed(SyndFeed syndFeed) {
        List<Element> foreignMarkup;
        SyndImage icon;
        SyndImage image;
        List<SyndPerson> contributors;
        List<SyndPerson> authors;
        SyndContent sDesc;
        Feed aFeed = new Feed(this.getType());
        aFeed.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));
        aFeed.setEncoding(syndFeed.getEncoding());
        aFeed.setStyleSheet(syndFeed.getStyleSheet());
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
        List<SyndLink> slinks = syndFeed.getLinks();
        if (slinks != null) {
            for (SyndLink syndLink : slinks) {
                Link link = this.createAtomLink(syndLink);
                String rel = link.getRel();
                if (Strings.isBlank((String)rel) || "alternate".equals(rel)) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        if (alternateLinks.isEmpty() && syndFeed.getLink() != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(syndFeed.getLink());
            alternateLinks.add(link);
        }
        if (!alternateLinks.isEmpty()) {
            aFeed.setAlternateLinks(alternateLinks);
        }
        if (!otherLinks.isEmpty()) {
            aFeed.setOtherLinks(otherLinks);
        }
        List<SyndCategory> sCats = syndFeed.getCategories();
        ArrayList<Category> aCats = new ArrayList<Category>();
        if (sCats != null) {
            for (SyndCategory sCat : sCats) {
                Category aCat = new Category();
                aCat.setTerm(sCat.getName());
                aCat.setLabel(sCat.getLabel());
                aCat.setScheme(sCat.getTaxonomyUri());
                aCats.add(aCat);
            }
        }
        if (!aCats.isEmpty()) {
            aFeed.setCategories(aCats);
        }
        if (Lists.isNotEmpty(authors = syndFeed.getAuthors())) {
            aFeed.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        }
        if (Lists.isNotEmpty(contributors = syndFeed.getContributors())) {
            aFeed.setContributors(ConverterForAtom03.createAtomPersons(contributors));
        }
        if ((image = syndFeed.getImage()) != null) {
            aFeed.setLogo(image.getUrl());
        }
        if ((icon = syndFeed.getIcon()) != null) {
            aFeed.setIcon(icon.getUrl());
        }
        aFeed.setRights(syndFeed.getCopyright());
        aFeed.setUpdated(syndFeed.getPublishedDate());
        List<SyndEntry> sEntries = syndFeed.getEntries();
        if (sEntries != null) {
            aFeed.setEntries(this.createAtomEntries(sEntries));
        }
        if (!(foreignMarkup = syndFeed.getForeignMarkup()).isEmpty()) {
            aFeed.setForeignMarkup(foreignMarkup);
        }
        return aFeed;
    }

    protected SyndContent createSyndContent(Content content) {
        SyndContentImpl sContent = new SyndContentImpl();
        sContent.setType(content.getType());
        sContent.setValue(content.getValue());
        return sContent;
    }

    protected List<Entry> createAtomEntries(List<SyndEntry> syndEntries) {
        ArrayList<Entry> atomEntries = new ArrayList<Entry>();
        for (SyndEntry syndEntry : syndEntries) {
            atomEntries.add(this.createAtomEntry(syndEntry));
        }
        return atomEntries;
    }

    protected Content createAtomContent(SyndContent sContent) {
        Content content = new Content();
        content.setType(sContent.getType());
        content.setValue(sContent.getValue());
        return content;
    }

    protected List<Content> createAtomContents(List<SyndContent> syndContents) {
        ArrayList<Content> atomContents = new ArrayList<Content>();
        for (SyndContent syndContent : syndContents) {
            atomContents.add(this.createAtomContent(syndContent));
        }
        return atomContents;
    }

    protected Entry createAtomEntry(SyndEntry sEntry) {
        SyndFeed sSource;
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
        boolean linkRelEnclosureExists = false;
        List<SyndLink> slinks = sEntry.getLinks();
        List<SyndEnclosure> enclosures = sEntry.getEnclosures();
        if (slinks != null) {
            for (SyndLink syndLink : slinks) {
                String lRel;
                Link link = this.createAtomLink(syndLink);
                String sRel = syndLink.getRel();
                if (sRel != null && "enclosure".equals(sRel)) {
                    linkRelEnclosureExists = true;
                }
                if (Strings.isBlank((String)(lRel = link.getRel())) || "alternate".equals(sRel)) {
                    alternateLinks.add(link);
                    continue;
                }
                otherLinks.add(link);
            }
        }
        if (alternateLinks.isEmpty() && sEntry.getLink() != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(sEntry.getLink());
            alternateLinks.add(link);
        }
        if (enclosures != null && !linkRelEnclosureExists) {
            for (SyndEnclosure syndEnclosure : enclosures) {
                SyndEnclosure syndEncl = syndEnclosure;
                Link link = this.createAtomEnclosure(syndEncl);
                otherLinks.add(link);
            }
        }
        if (!alternateLinks.isEmpty()) {
            aEntry.setAlternateLinks(alternateLinks);
        }
        if (!otherLinks.isEmpty()) {
            aEntry.setOtherLinks(otherLinks);
        }
        List<SyndCategory> sCats = sEntry.getCategories();
        ArrayList<Category> aCats = new ArrayList<Category>();
        if (sCats != null) {
            for (SyndCategory sCat : sCats) {
                Category aCat = new Category();
                aCat.setTerm(sCat.getName());
                aCat.setLabel(sCat.getLabel());
                aCat.setScheme(sCat.getTaxonomyUri());
                aCats.add(aCat);
            }
        }
        if (!aCats.isEmpty()) {
            aEntry.setCategories(aCats);
        }
        List<SyndContent> syndContents = sEntry.getContents();
        aEntry.setContents(this.createAtomContents(syndContents));
        List<SyndPerson> authors = sEntry.getAuthors();
        String author = sEntry.getAuthor();
        if (Lists.isNotEmpty(authors)) {
            aEntry.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        } else if (author != null) {
            Person person = new Person();
            person.setName(author);
            authors = new ArrayList<SyndPerson>();
            authors.add(person);
            aEntry.setAuthors(authors);
        }
        List<SyndPerson> contributors = sEntry.getContributors();
        if (Lists.isNotEmpty(contributors)) {
            aEntry.setContributors(ConverterForAtom03.createAtomPersons(contributors));
        }
        aEntry.setPublished(sEntry.getPublishedDate());
        if (sEntry.getUpdatedDate() != null) {
            aEntry.setUpdated(sEntry.getUpdatedDate());
        } else {
            aEntry.setUpdated(sEntry.getPublishedDate());
        }
        List<Element> foreignMarkup = sEntry.getForeignMarkup();
        if (!foreignMarkup.isEmpty()) {
            aEntry.setForeignMarkup(foreignMarkup);
        }
        if ((sSource = sEntry.getSource()) != null) {
            Feed aSource = (Feed)sSource.createWireFeed(this.getType());
            aEntry.setSource(aSource);
        }
        return aEntry;
    }
}

