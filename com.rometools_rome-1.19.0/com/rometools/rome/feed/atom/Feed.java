/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.atom;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Generator;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.utils.Lists;
import java.util.Date;
import java.util.List;

public class Feed
extends WireFeed {
    private static final long serialVersionUID = 1L;
    private String xmlBase;
    private List<Category> categories;
    private List<SyndPerson> authors;
    private List<SyndPerson> contributors;
    private Generator generator;
    private String icon;
    private String id;
    private String logo;
    private String rights;
    private Content subtitle;
    private Content title;
    private Date updated;
    private List<Link> alternateLinks;
    private List<Link> otherLinks;
    private List<Entry> entries;
    private List<Module> modules;
    private Content info;
    private String language;

    public Feed() {
    }

    public Feed(String type) {
        super(type);
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        if (this.title != null) {
            return this.title.getValue();
        }
        return null;
    }

    public void setTitle(String title) {
        if (this.title == null) {
            this.title = new Content();
        }
        this.title.setValue(title);
    }

    public Content getTitleEx() {
        return this.title;
    }

    public void setTitleEx(Content title) {
        this.title = title;
    }

    public List<Link> getAlternateLinks() {
        this.alternateLinks = Lists.createWhenNull(this.alternateLinks);
        return this.alternateLinks;
    }

    public void setAlternateLinks(List<Link> alternateLinks) {
        this.alternateLinks = alternateLinks;
    }

    public List<Link> getOtherLinks() {
        this.otherLinks = Lists.createWhenNull(this.otherLinks);
        return this.otherLinks;
    }

    public void setOtherLinks(List<Link> otherLinks) {
        this.otherLinks = otherLinks;
    }

    public List<SyndPerson> getAuthors() {
        this.authors = Lists.createWhenNull(this.authors);
        return this.authors;
    }

    public void setAuthors(List<SyndPerson> authors) {
        this.authors = authors;
    }

    public List<SyndPerson> getContributors() {
        this.contributors = Lists.createWhenNull(this.contributors);
        return this.contributors;
    }

    public void setContributors(List<SyndPerson> contributors) {
        this.contributors = contributors;
    }

    public Content getTagline() {
        return this.subtitle;
    }

    public void setTagline(Content tagline) {
        this.subtitle = tagline;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Generator getGenerator() {
        return this.generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public String getCopyright() {
        return this.rights;
    }

    public void setCopyright(String copyright) {
        this.rights = copyright;
    }

    public Content getInfo() {
        return this.info;
    }

    public void setInfo(Content info) {
        this.info = info;
    }

    public Date getModified() {
        return this.updated;
    }

    public void setModified(Date modified) {
        this.updated = modified;
    }

    public List<Entry> getEntries() {
        this.entries = Lists.createWhenNull(this.entries);
        return this.entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @Override
    public List<Module> getModules() {
        this.modules = Lists.createWhenNull(this.modules);
        return this.modules;
    }

    @Override
    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public Module getModule(String uri) {
        return ModuleUtils.getModule(this.modules, uri);
    }

    public List<Category> getCategories() {
        this.categories = Lists.createWhenNull(this.categories);
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRights() {
        return this.rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public Content getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(Content subtitle) {
        this.subtitle = subtitle;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getXmlBase() {
        return this.xmlBase;
    }

    public void setXmlBase(String xmlBase) {
        this.xmlBase = xmlBase;
    }
}

