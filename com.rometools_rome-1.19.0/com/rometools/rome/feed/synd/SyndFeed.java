/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndPerson;
import java.util.Date;
import java.util.List;
import org.jdom2.Element;

public interface SyndFeed
extends Cloneable,
CopyFrom,
Extendable {
    public List<String> getSupportedFeedTypes();

    public WireFeed createWireFeed();

    public WireFeed createWireFeed(String var1);

    public WireFeed originalWireFeed();

    public boolean isPreservingWireFeed();

    public String getFeedType();

    public void setFeedType(String var1);

    public String getEncoding();

    public void setEncoding(String var1);

    public String getUri();

    public void setUri(String var1);

    public String getTitle();

    public void setTitle(String var1);

    public SyndContent getTitleEx();

    public void setTitleEx(SyndContent var1);

    public String getLink();

    public void setLink(String var1);

    public List<SyndLink> getLinks();

    public void setLinks(List<SyndLink> var1);

    public String getDescription();

    public void setDescription(String var1);

    public SyndContent getDescriptionEx();

    public void setDescriptionEx(SyndContent var1);

    public Date getPublishedDate();

    public void setPublishedDate(Date var1);

    public List<SyndPerson> getAuthors();

    public void setAuthors(List<SyndPerson> var1);

    public String getAuthor();

    public void setAuthor(String var1);

    public List<SyndPerson> getContributors();

    public void setContributors(List<SyndPerson> var1);

    public String getCopyright();

    public void setCopyright(String var1);

    public SyndImage getImage();

    public void setImage(SyndImage var1);

    public SyndImage getIcon();

    public void setIcon(SyndImage var1);

    public List<SyndCategory> getCategories();

    public void setCategories(List<SyndCategory> var1);

    public List<SyndEntry> getEntries();

    public void setEntries(List<SyndEntry> var1);

    public String getLanguage();

    public void setLanguage(String var1);

    @Override
    public Module getModule(String var1);

    @Override
    public List<Module> getModules();

    @Override
    public void setModules(List<Module> var1);

    public List<Element> getForeignMarkup();

    public void setForeignMarkup(List<Element> var1);

    public String getDocs();

    public void setDocs(String var1);

    public String getGenerator();

    public void setGenerator(String var1);

    public String getManagingEditor();

    public void setManagingEditor(String var1);

    public String getWebMaster();

    public void setWebMaster(String var1);

    public String getStyleSheet();

    public void setStyleSheet(String var1);

    public Object clone() throws CloneNotSupportedException;
}

