/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.impl.ConverterForRSS091Userland;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ConverterForRSS092
extends ConverterForRSS091Userland {
    public ConverterForRSS092() {
        this("rss_0.92");
    }

    protected ConverterForRSS092(String type) {
        super(type);
    }

    @Override
    protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
        List<Enclosure> enclosures;
        SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
        List<Category> cats = item.getCategories();
        if (!cats.isEmpty()) {
            LinkedHashSet<SyndCategory> s = new LinkedHashSet<SyndCategory>();
            s.addAll(this.createSyndCategories(cats));
            s.addAll(syndEntry.getCategories());
            syndEntry.setCategories(new ArrayList<SyndCategory>(s));
        }
        if (!(enclosures = item.getEnclosures()).isEmpty()) {
            syndEntry.setEnclosures(this.createSyndEnclosures(enclosures));
        }
        return syndEntry;
    }

    protected List<SyndCategory> createSyndCategories(List<Category> rssCats) {
        ArrayList<SyndCategory> syndCats = new ArrayList<SyndCategory>();
        for (Category rssCat : rssCats) {
            SyndCategoryImpl sCat = new SyndCategoryImpl();
            sCat.setTaxonomyUri(rssCat.getDomain());
            sCat.setName(rssCat.getValue());
            syndCats.add(sCat);
        }
        return syndCats;
    }

    protected List<SyndEnclosure> createSyndEnclosures(List<Enclosure> enclosures) {
        ArrayList<SyndEnclosure> sEnclosures = new ArrayList<SyndEnclosure>();
        for (Enclosure enc : enclosures) {
            SyndEnclosureImpl sEnc = new SyndEnclosureImpl();
            sEnc.setUrl(enc.getUrl());
            sEnc.setType(enc.getType());
            sEnc.setLength(enc.getLength());
            sEnclosures.add(sEnc);
        }
        return sEnclosures;
    }

    @Override
    protected Item createRSSItem(SyndEntry sEntry) {
        List<SyndEnclosure> sEnclosures;
        Item item = super.createRSSItem(sEntry);
        List<SyndCategory> sCats = sEntry.getCategories();
        if (!sCats.isEmpty()) {
            item.setCategories(this.createRSSCategories(sCats));
        }
        if (!(sEnclosures = sEntry.getEnclosures()).isEmpty()) {
            item.setEnclosures(this.createEnclosures(sEnclosures));
        }
        return item;
    }

    protected List<Category> createRSSCategories(List<SyndCategory> sCats) {
        ArrayList<Category> cats = new ArrayList<Category>();
        for (SyndCategory sCat : sCats) {
            Category cat = new Category();
            cat.setDomain(sCat.getTaxonomyUri());
            cat.setValue(sCat.getName());
            cats.add(cat);
        }
        return cats;
    }

    protected List<Enclosure> createEnclosures(List<SyndEnclosure> sEnclosures) {
        ArrayList<Enclosure> enclosures = new ArrayList<Enclosure>();
        for (SyndEnclosure sEnc : sEnclosures) {
            Enclosure enc = new Enclosure();
            enc.setUrl(sEnc.getUrl());
            enc.setType(sEnc.getType());
            enc.setLength(sEnc.getLength());
            enclosures.add(enc);
        }
        return enclosures;
    }
}

