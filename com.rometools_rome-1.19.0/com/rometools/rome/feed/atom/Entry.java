/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Dates
 *  com.rometools.utils.Lists
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.atom;

import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.utils.Dates;
import com.rometools.utils.Lists;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jdom2.Element;

public class Entry
implements Cloneable,
Serializable,
Extendable {
    private static final long serialVersionUID = 1L;
    private Content summary;
    private Content title;
    private Date created;
    private Date published;
    private Date updated;
    private Feed source;
    private List<Link> alternateLinks;
    private List<SyndPerson> authors;
    private List<Category> categories;
    private List<Content> contents;
    private List<SyndPerson> contributors;
    private List<Element> foreignMarkup;
    private List<Module> modules;
    private List<Link> otherLinks;
    private String id;
    private String rights;
    private String xmlBase;

    public void setAlternateLinks(List<Link> alternateLinks) {
        this.alternateLinks = alternateLinks;
    }

    public List<Link> getAlternateLinks() {
        this.alternateLinks = Lists.createWhenNull(this.alternateLinks);
        return this.alternateLinks;
    }

    public void setAuthors(List<SyndPerson> authors) {
        this.authors = authors;
    }

    public List<SyndPerson> getAuthors() {
        this.authors = Lists.createWhenNull(this.authors);
        return this.authors;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        this.categories = Lists.createWhenNull(this.categories);
        return this.categories;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public List<Content> getContents() {
        this.contents = Lists.createWhenNull(this.contents);
        return this.contents;
    }

    public void setContributors(List<SyndPerson> contributors) {
        this.contributors = contributors;
    }

    public List<SyndPerson> getContributors() {
        this.contributors = Lists.createWhenNull(this.contributors);
        return this.contributors;
    }

    public void setCreated(Date created) {
        this.created = Dates.copy((Date)created);
    }

    public Date getCreated() {
        return Dates.copy((Date)this.created);
    }

    public void setForeignMarkup(List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }

    public List<Element> getForeignMarkup() {
        this.foreignMarkup = Lists.createWhenNull(this.foreignMarkup);
        return this.foreignMarkup;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setIssued(Date issued) {
        this.published = Dates.copy((Date)issued);
    }

    public Date getIssued() {
        return Dates.copy((Date)this.published);
    }

    public boolean isMediaEntry() {
        boolean mediaEntry = false;
        List<Link> links = this.getOtherLinks();
        for (Link link : links) {
            if (!"edit-media".equals(link.getRel())) continue;
            mediaEntry = true;
            break;
        }
        return mediaEntry;
    }

    public void setModified(Date modified) {
        this.updated = Dates.copy((Date)modified);
    }

    public Date getModified() {
        return Dates.copy((Date)this.updated);
    }

    @Override
    public Module getModule(String uri) {
        return ModuleUtils.getModule(this.modules, uri);
    }

    @Override
    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public List<Module> getModules() {
        this.modules = Lists.createWhenNull(this.modules);
        return this.modules;
    }

    public void setOtherLinks(List<Link> otherLinks) {
        this.otherLinks = otherLinks;
    }

    public List<Link> getOtherLinks() {
        this.otherLinks = Lists.createWhenNull(this.otherLinks);
        return this.otherLinks;
    }

    public void setPublished(Date published) {
        this.published = Dates.copy((Date)published);
    }

    public Date getPublished() {
        return Dates.copy((Date)this.published);
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getRights() {
        return this.rights;
    }

    public void setSource(Feed source) {
        this.source = source;
    }

    public Feed getSource() {
        return this.source;
    }

    public void setSummary(Content summary) {
        this.summary = summary;
    }

    public Content getSummary() {
        return this.summary;
    }

    public void setTitle(String title) {
        if (this.title == null) {
            this.title = new Content();
        }
        this.title.setValue(title);
    }

    public String getTitle() {
        if (this.title != null) {
            return this.title.getValue();
        }
        return null;
    }

    public void setTitleEx(Content title) {
        this.title = title;
    }

    public Content getTitleEx() {
        return this.title;
    }

    public void setUpdated(Date updated) {
        this.updated = Dates.copy((Date)updated);
    }

    public Date getUpdated() {
        return Dates.copy((Date)this.updated);
    }

    public void setXmlBase(String xmlBase) {
        this.xmlBase = xmlBase;
    }

    public String getXmlBase() {
        return this.xmlBase;
    }

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Entry)) {
            return false;
        }
        List<Element> fm = this.getForeignMarkup();
        this.setForeignMarkup(((Entry)other).getForeignMarkup());
        boolean ret = EqualsBean.beanEquals(this.getClass(), this, other);
        this.setForeignMarkup(fm);
        return ret;
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    public Link findRelatedLink(String relation) {
        for (Link link : this.otherLinks) {
            if (!relation.equals(link.getRel())) continue;
            return link;
        }
        return null;
    }
}

