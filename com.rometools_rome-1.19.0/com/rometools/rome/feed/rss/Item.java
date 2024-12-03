/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Dates
 *  com.rometools.utils.Lists
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.rss;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Source;
import com.rometools.utils.Dates;
import com.rometools.utils.Lists;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jdom2.Element;

public class Item
implements Cloneable,
Serializable,
Extendable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String link;
    private String uri;
    private Description description;
    private Content content;
    private Source source;
    private List<Enclosure> enclosures;
    private List<Category> categories;
    private Guid guid;
    private String comments;
    private String author;
    private Date pubDate;
    private Date expirationDate;
    private List<Module> modules;
    private List<Element> foreignMarkup;

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof Item)) {
            return false;
        }
        List<Element> fm = this.getForeignMarkup();
        this.setForeignMarkup(((Item)other).getForeignMarkup());
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Description getDescription() {
        return this.description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Source getSource() {
        return this.source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<Enclosure> getEnclosures() {
        this.enclosures = Lists.createWhenNull(this.enclosures);
        return this.enclosures;
    }

    public void setEnclosures(List<Enclosure> enclosures) {
        this.enclosures = enclosures;
    }

    public List<Category> getCategories() {
        this.categories = Lists.createWhenNull(this.categories);
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Guid getGuid() {
        return this.guid;
    }

    public void setGuid(Guid guid) {
        this.guid = guid;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public Date getPubDate() {
        return Dates.copy((Date)this.pubDate);
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = Dates.copy((Date)pubDate);
    }

    public Date getExpirationDate() {
        return Dates.copy((Date)this.expirationDate);
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = Dates.copy((Date)expirationDate);
    }

    public List<Element> getForeignMarkup() {
        this.foreignMarkup = Lists.createWhenNull(this.foreignMarkup);
        return this.foreignMarkup;
    }

    public void setForeignMarkup(List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }
}

