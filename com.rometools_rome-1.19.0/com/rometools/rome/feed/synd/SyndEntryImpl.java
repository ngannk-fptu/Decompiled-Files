/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Dates
 *  com.rometools.utils.Lists
 *  com.rometools.utils.Strings
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.module.DCModuleImpl;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.SyModule;
import com.rometools.rome.feed.module.SyModuleImpl;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.impl.URINormalizer;
import com.rometools.utils.Dates;
import com.rometools.utils.Lists;
import com.rometools.utils.Strings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jdom2.Element;

public class SyndEntryImpl
implements Serializable,
SyndEntry {
    private static final long serialVersionUID = 1L;
    private static final CopyFromHelper COPY_FROM_HELPER;
    private final Class<?> beanClass;
    private final Set<String> convenienceProperties;
    private String uri;
    private String link;
    private String comments;
    private Date updatedDate;
    private SyndContent title;
    private SyndContent description;
    private List<SyndLink> links;
    private List<SyndContent> contents;
    private List<Module> modules;
    private List<SyndEnclosure> enclosures;
    private List<SyndPerson> authors;
    private List<SyndPerson> contributors;
    private SyndFeed source;
    private List<Element> foreignMarkup;
    private Object wireEntry;
    private List<SyndCategory> categories = new ArrayList<SyndCategory>();
    private static final Set<String> IGNORE_PROPERTIES;
    public static final Set<String> CONVENIENCE_PROPERTIES;

    protected SyndEntryImpl(Class<?> beanClass, Set<String> convenienceProperties) {
        this.beanClass = beanClass;
        this.convenienceProperties = convenienceProperties;
    }

    public SyndEntryImpl() {
        this(SyndEntry.class, IGNORE_PROPERTIES);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, this.convenienceProperties);
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof SyndEntryImpl)) {
            return false;
        }
        List<Element> fm = this.getForeignMarkup();
        this.setForeignMarkup(((SyndEntryImpl)other).getForeignMarkup());
        boolean ret = EqualsBean.beanEquals(this.beanClass, this, other);
        this.setForeignMarkup(fm);
        return ret;
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.beanClass, this);
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = URINormalizer.normalize(uri);
    }

    @Override
    public String getTitle() {
        if (this.title != null) {
            return this.title.getValue();
        }
        return null;
    }

    @Override
    public void setTitle(String title) {
        if (this.title == null) {
            this.title = new SyndContentImpl();
        }
        this.title.setValue(title);
    }

    @Override
    public SyndContent getTitleEx() {
        return this.title;
    }

    @Override
    public void setTitleEx(SyndContent title) {
        this.title = title;
    }

    @Override
    public String getLink() {
        return this.link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public SyndContent getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(SyndContent description) {
        this.description = description;
    }

    @Override
    public List<SyndContent> getContents() {
        this.contents = Lists.createWhenNull(this.contents);
        return this.contents;
    }

    @Override
    public void setContents(List<SyndContent> contents) {
        this.contents = contents;
    }

    @Override
    public List<SyndEnclosure> getEnclosures() {
        this.enclosures = Lists.createWhenNull(this.enclosures);
        return this.enclosures;
    }

    @Override
    public void setEnclosures(List<SyndEnclosure> enclosures) {
        this.enclosures = enclosures;
    }

    @Override
    public Date getPublishedDate() {
        return this.getDCModule().getDate();
    }

    @Override
    public void setPublishedDate(Date publishedDate) {
        this.getDCModule().setDate(publishedDate);
    }

    @Override
    public List<SyndCategory> getCategories() {
        return this.categories;
    }

    @Override
    public void setCategories(List<SyndCategory> categories) {
        this.categories = categories;
    }

    @Override
    public List<Module> getModules() {
        this.modules = Lists.createWhenNull(this.modules);
        if (ModuleUtils.getModule(this.modules, "http://purl.org/dc/elements/1.1/") == null) {
            this.modules.add(new DCModuleImpl());
        }
        return this.modules;
    }

    @Override
    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public Module getModule(String uri) {
        return ModuleUtils.getModule(this.getModules(), uri);
    }

    private DCModule getDCModule() {
        return (DCModule)this.getModule("http://purl.org/dc/elements/1.1/");
    }

    public Class<SyndEntry> getInterface() {
        return SyndEntry.class;
    }

    @Override
    public void copyFrom(CopyFrom obj) {
        COPY_FROM_HELPER.copy(this, obj);
    }

    @Override
    public List<SyndLink> getLinks() {
        this.links = Lists.createWhenNull(this.links);
        return this.links;
    }

    @Override
    public void setLinks(List<SyndLink> links) {
        this.links = links;
    }

    @Override
    public Date getUpdatedDate() {
        return Dates.copy((Date)this.updatedDate);
    }

    @Override
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = new Date(updatedDate.getTime());
    }

    @Override
    public List<SyndPerson> getAuthors() {
        this.authors = Lists.createWhenNull(this.authors);
        return this.authors;
    }

    @Override
    public void setAuthors(List<SyndPerson> authors) {
        this.authors = authors;
    }

    @Override
    public String getAuthor() {
        String author = Lists.isNotEmpty(this.authors) ? this.authors.get(0).getName() : this.getDCModule().getCreator();
        if (author == null) {
            author = "";
        }
        return author;
    }

    @Override
    public void setAuthor(String author) {
        DCModule dcModule = this.getDCModule();
        String currentValue = dcModule.getCreator();
        if (Strings.isEmpty((String)currentValue)) {
            this.getDCModule().setCreator(author);
        }
    }

    @Override
    public List<SyndPerson> getContributors() {
        this.contributors = Lists.createWhenNull(this.contributors);
        return this.contributors;
    }

    @Override
    public void setContributors(List<SyndPerson> contributors) {
        this.contributors = contributors;
    }

    @Override
    public SyndFeed getSource() {
        return this.source;
    }

    @Override
    public void setSource(SyndFeed source) {
        this.source = source;
    }

    @Override
    public List<Element> getForeignMarkup() {
        this.foreignMarkup = Lists.createWhenNull(this.foreignMarkup);
        return this.foreignMarkup;
    }

    @Override
    public void setForeignMarkup(List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }

    @Override
    public String getComments() {
        return this.comments;
    }

    @Override
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public Object getWireEntry() {
        return this.wireEntry;
    }

    public void setWireEntry(Object wireEntry) {
        this.wireEntry = wireEntry;
    }

    @Override
    public SyndLink findRelatedLink(String relation) {
        List<SyndLink> syndLinks = this.getLinks();
        for (SyndLink syndLink : syndLinks) {
            if (!relation.equals(syndLink.getRel())) continue;
            return syndLink;
        }
        return null;
    }

    static {
        IGNORE_PROPERTIES = new HashSet<String>();
        CONVENIENCE_PROPERTIES = Collections.unmodifiableSet(IGNORE_PROPERTIES);
        IGNORE_PROPERTIES.add("publishedDate");
        IGNORE_PROPERTIES.add("author");
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("title", String.class);
        basePropInterfaceMap.put("link", String.class);
        basePropInterfaceMap.put("uri", String.class);
        basePropInterfaceMap.put("description", SyndContent.class);
        basePropInterfaceMap.put("contents", SyndContent.class);
        basePropInterfaceMap.put("enclosures", SyndEnclosure.class);
        basePropInterfaceMap.put("modules", Module.class);
        basePropInterfaceMap.put("categories", SyndCategory.class);
        HashMap basePropClassImplMap = new HashMap();
        basePropClassImplMap.put(SyndContent.class, SyndContentImpl.class);
        basePropClassImplMap.put(SyndEnclosure.class, SyndEnclosureImpl.class);
        basePropClassImplMap.put(SyndCategory.class, SyndCategoryImpl.class);
        basePropClassImplMap.put(DCModule.class, DCModuleImpl.class);
        basePropClassImplMap.put(SyModule.class, SyModuleImpl.class);
        COPY_FROM_HELPER = new CopyFromHelper(SyndEntry.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

