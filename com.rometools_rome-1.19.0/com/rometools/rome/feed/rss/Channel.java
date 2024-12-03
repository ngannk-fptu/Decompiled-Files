/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Dates
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.rss;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Cloud;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.rss.TextInput;
import com.rometools.utils.Dates;
import com.rometools.utils.Lists;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Channel
extends WireFeed {
    private static final long serialVersionUID = 1L;
    public static final String SUNDAY = "sunday";
    public static final String MONDAY = "monday";
    public static final String TUESDAY = "tuesday";
    public static final String WEDNESDAY = "wednesday";
    public static final String THURSDAY = "thursday";
    public static final String FRIDAY = "friday";
    public static final String SATURDAY = "saturday";
    private static final Set<String> DAYS;
    private String title;
    private String description;
    private String link;
    private String uri;
    private Image image;
    private List<Item> items;
    private TextInput textInput;
    private String language;
    private String rating;
    private String copyright;
    private Date pubDate;
    private Date lastBuildDate;
    private String docs;
    private String managingEditor;
    private String webMaster;
    private List<Integer> skipHours;
    private List<String> skipDays;
    private Cloud cloud;
    private List<Category> categories;
    private String generator;
    private int ttl = -1;
    private List<Module> modules;

    public Channel() {
    }

    public Channel(String type) {
        super(type);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Item> getItems() {
        this.items = Lists.createWhenNull(this.items);
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public TextInput getTextInput() {
        return this.textInput;
    }

    public void setTextInput(TextInput textInput) {
        this.textInput = textInput;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRating() {
        return this.rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Date getPubDate() {
        return Dates.copy((Date)this.pubDate);
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = Dates.copy((Date)pubDate);
    }

    public Date getLastBuildDate() {
        return Dates.copy((Date)this.lastBuildDate);
    }

    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = Dates.copy((Date)lastBuildDate);
    }

    public String getDocs() {
        return this.docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }

    public String getManagingEditor() {
        return this.managingEditor;
    }

    public void setManagingEditor(String managingEditor) {
        this.managingEditor = managingEditor;
    }

    public String getWebMaster() {
        return this.webMaster;
    }

    public void setWebMaster(String webMaster) {
        this.webMaster = webMaster;
    }

    public List<Integer> getSkipHours() {
        return Lists.createWhenNull(this.skipHours);
    }

    public void setSkipHours(List<Integer> skipHours) {
        if (skipHours != null) {
            for (int i = 0; i < skipHours.size(); ++i) {
                Integer iHour = skipHours.get(i);
                if (iHour != null) {
                    int hour = iHour;
                    if (hour >= 0 && hour <= 24) continue;
                    throw new IllegalArgumentException("Invalid hour [" + hour + "]");
                }
                throw new IllegalArgumentException("Invalid hour [null]");
            }
        }
        this.skipHours = skipHours;
    }

    public List<String> getSkipDays() {
        return Lists.createWhenNull(this.skipDays);
    }

    public void setSkipDays(List<String> skipDays) {
        if (skipDays != null) {
            for (int i = 0; i < skipDays.size(); ++i) {
                String day = skipDays.get(i);
                if (day != null) {
                    if (!DAYS.contains(day = day.toLowerCase())) {
                        throw new IllegalArgumentException("Invalid day [" + day + "]");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid day [null]");
                }
                skipDays.set(i, day);
            }
        }
        this.skipDays = skipDays;
    }

    public Cloud getCloud() {
        return this.cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public List<Category> getCategories() {
        this.categories = Lists.createWhenNull(this.categories);
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getGenerator() {
        return this.generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public int getTtl() {
        return this.ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
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

    static {
        HashSet<String> days = new HashSet<String>();
        days.add(SUNDAY);
        days.add(MONDAY);
        days.add(TUESDAY);
        days.add(WEDNESDAY);
        days.add(THURSDAY);
        days.add(FRIDAY);
        days.add(SATURDAY);
        DAYS = Collections.unmodifiableSet(days);
    }
}

