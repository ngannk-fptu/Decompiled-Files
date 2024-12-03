/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndImage;
import java.util.Date;
import java.util.List;

public interface SyndFeed
extends Cloneable,
CopyFrom,
Extendable {
    public List getSupportedFeedTypes();

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

    public List getLinks();

    public void setLinks(List var1);

    public String getDescription();

    public void setDescription(String var1);

    public SyndContent getDescriptionEx();

    public void setDescriptionEx(SyndContent var1);

    public Date getPublishedDate();

    public void setPublishedDate(Date var1);

    public List getAuthors();

    public void setAuthors(List var1);

    public String getAuthor();

    public void setAuthor(String var1);

    public List getContributors();

    public void setContributors(List var1);

    public String getCopyright();

    public void setCopyright(String var1);

    public SyndImage getImage();

    public void setImage(SyndImage var1);

    public List getCategories();

    public void setCategories(List var1);

    public List getEntries();

    public void setEntries(List var1);

    public String getLanguage();

    public void setLanguage(String var1);

    public Module getModule(String var1);

    public List getModules();

    public void setModules(List var1);

    public Object getForeignMarkup();

    public void setForeignMarkup(Object var1);

    public Object clone() throws CloneNotSupportedException;
}

