/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.WireFeed;
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
import com.rometools.rome.feed.synd.Converter;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndCategoryListFacade;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.impl.Converters;
import com.rometools.rome.feed.synd.impl.URINormalizer;
import com.rometools.utils.Lists;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jdom2.Element;

public class SyndFeedImpl
implements Serializable,
SyndFeed {
    private static final long serialVersionUID = 1L;
    private static final CopyFromHelper COPY_FROM_HELPER;
    private final Class<?> beanClass;
    private final Set<String> convenienceProperties;
    private String encoding;
    private String uri;
    private SyndContent title;
    private SyndContent description;
    private String feedType;
    private String link;
    private String webMaster;
    private String managingEditor;
    private String docs;
    private String generator;
    private String styleSheet;
    private List<SyndLink> links;
    private SyndImage icon;
    private SyndImage image;
    private List<SyndEntry> entries;
    private List<Module> modules;
    private List<SyndPerson> authors;
    private List<SyndPerson> contributors;
    private List<Element> foreignMarkup;
    private WireFeed wireFeed = null;
    private boolean preserveWireFeed = false;
    private static final Converters CONVERTERS;
    private static final Set<String> IGNORE_PROPERTIES;
    public static final Set<String> CONVENIENCE_PROPERTIES;

    @Override
    public List<String> getSupportedFeedTypes() {
        return CONVERTERS.getSupportedFeedTypes();
    }

    protected SyndFeedImpl(Class<?> beanClass, Set<String> convenienceProperties) {
        this.beanClass = beanClass;
        this.convenienceProperties = convenienceProperties;
    }

    public SyndFeedImpl() {
        this(null);
    }

    public SyndFeedImpl(WireFeed feed) {
        this(feed, false);
    }

    public SyndFeedImpl(WireFeed feed, boolean preserveWireFeed) {
        this(SyndFeed.class, IGNORE_PROPERTIES);
        if (preserveWireFeed) {
            this.wireFeed = feed;
            this.preserveWireFeed = preserveWireFeed;
        }
        if (feed != null) {
            this.feedType = feed.getFeedType();
            Converter converter = CONVERTERS.getConverter(this.feedType);
            if (converter == null) {
                throw new IllegalArgumentException("Invalid feed type [" + this.feedType + "]");
            }
            converter.copyInto(feed, this);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, this.convenienceProperties);
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof SyndFeedImpl)) {
            return false;
        }
        List<Element> fm = this.getForeignMarkup();
        this.setForeignMarkup(((SyndFeedImpl)other).getForeignMarkup());
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
    public WireFeed createWireFeed() {
        return this.createWireFeed(this.feedType);
    }

    @Override
    public WireFeed createWireFeed(String feedType) {
        if (feedType == null) {
            throw new IllegalArgumentException("Feed type cannot be null");
        }
        Converter converter = CONVERTERS.getConverter(feedType);
        if (converter == null) {
            throw new IllegalArgumentException("Invalid feed type [" + feedType + "]");
        }
        return converter.createRealFeed(this);
    }

    @Override
    public WireFeed originalWireFeed() {
        return this.wireFeed;
    }

    @Override
    public String getFeedType() {
        return this.feedType;
    }

    @Override
    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
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
    public String getDescription() {
        if (this.description != null) {
            return this.description.getValue();
        }
        return null;
    }

    @Override
    public void setDescription(String description) {
        if (this.description == null) {
            this.description = new SyndContentImpl();
        }
        this.description.setValue(description);
    }

    @Override
    public SyndContent getDescriptionEx() {
        return this.description;
    }

    @Override
    public void setDescriptionEx(SyndContent description) {
        this.description = description;
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
    public String getCopyright() {
        return this.getDCModule().getRights();
    }

    @Override
    public void setCopyright(String copyright) {
        this.getDCModule().setRights(copyright);
    }

    @Override
    public SyndImage getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(SyndImage icon) {
        this.icon = icon;
    }

    @Override
    public SyndImage getImage() {
        return this.image;
    }

    @Override
    public void setImage(SyndImage image) {
        this.image = image;
    }

    @Override
    public List<SyndCategory> getCategories() {
        return new SyndCategoryListFacade(this.getDCModule().getSubjects());
    }

    @Override
    public void setCategories(List<SyndCategory> categories) {
        this.getDCModule().setSubjects(SyndCategoryListFacade.convertElementsSyndCategoryToSubject(categories));
    }

    @Override
    public List<SyndEntry> getEntries() {
        this.entries = Lists.createWhenNull(this.entries);
        return this.entries;
    }

    @Override
    public void setEntries(List<SyndEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String getLanguage() {
        return this.getDCModule().getLanguage();
    }

    @Override
    public void setLanguage(String language) {
        this.getDCModule().setLanguage(language);
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

    public Class<SyndFeed> getInterface() {
        return SyndFeed.class;
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
        return this.getDCModule().getCreator();
    }

    @Override
    public void setAuthor(String author) {
        this.getDCModule().setCreator(author);
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
    public List<Element> getForeignMarkup() {
        this.foreignMarkup = Lists.createWhenNull(this.foreignMarkup);
        return this.foreignMarkup;
    }

    @Override
    public void setForeignMarkup(List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }

    @Override
    public boolean isPreservingWireFeed() {
        return this.preserveWireFeed;
    }

    @Override
    public String getDocs() {
        return this.docs;
    }

    @Override
    public void setDocs(String docs) {
        this.docs = docs;
    }

    @Override
    public String getGenerator() {
        return this.generator;
    }

    @Override
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    @Override
    public String getManagingEditor() {
        return this.managingEditor;
    }

    @Override
    public void setManagingEditor(String managingEditor) {
        this.managingEditor = managingEditor;
    }

    @Override
    public String getWebMaster() {
        return this.webMaster;
    }

    @Override
    public void setWebMaster(String webMaster) {
        this.webMaster = webMaster;
    }

    @Override
    public String getStyleSheet() {
        return this.styleSheet;
    }

    @Override
    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }

    static {
        CONVERTERS = new Converters();
        IGNORE_PROPERTIES = new HashSet<String>();
        CONVENIENCE_PROPERTIES = Collections.unmodifiableSet(IGNORE_PROPERTIES);
        IGNORE_PROPERTIES.add("publishedDate");
        IGNORE_PROPERTIES.add("author");
        IGNORE_PROPERTIES.add("copyright");
        IGNORE_PROPERTIES.add("categories");
        IGNORE_PROPERTIES.add("language");
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("feedType", String.class);
        basePropInterfaceMap.put("encoding", String.class);
        basePropInterfaceMap.put("uri", String.class);
        basePropInterfaceMap.put("title", String.class);
        basePropInterfaceMap.put("link", String.class);
        basePropInterfaceMap.put("description", String.class);
        basePropInterfaceMap.put("image", SyndImage.class);
        basePropInterfaceMap.put("entries", SyndEntry.class);
        basePropInterfaceMap.put("modules", Module.class);
        basePropInterfaceMap.put("categories", SyndCategory.class);
        HashMap basePropClassImplMap = new HashMap();
        basePropClassImplMap.put(SyndEntry.class, SyndEntryImpl.class);
        basePropClassImplMap.put(SyndImage.class, SyndImageImpl.class);
        basePropClassImplMap.put(SyndCategory.class, SyndCategoryImpl.class);
        basePropClassImplMap.put(DCModule.class, DCModuleImpl.class);
        basePropClassImplMap.put(SyModule.class, SyModuleImpl.class);
        COPY_FROM_HELPER = new CopyFromHelper(SyndFeed.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

