/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndFeed;
import java.util.Date;
import java.util.List;

public interface SyndEntry
extends Cloneable,
CopyFrom,
Extendable {
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

    public SyndContent getDescription();

    public void setDescription(SyndContent var1);

    public List getContents();

    public void setContents(List var1);

    public List getEnclosures();

    public void setEnclosures(List var1);

    public Date getPublishedDate();

    public void setPublishedDate(Date var1);

    public Date getUpdatedDate();

    public void setUpdatedDate(Date var1);

    public List getAuthors();

    public void setAuthors(List var1);

    public String getAuthor();

    public void setAuthor(String var1);

    public List getContributors();

    public void setContributors(List var1);

    public List getCategories();

    public void setCategories(List var1);

    public SyndFeed getSource();

    public void setSource(SyndFeed var1);

    public Object getWireEntry();

    public Module getModule(String var1);

    public List getModules();

    public void setModules(List var1);

    public Object getForeignMarkup();

    public void setForeignMarkup(Object var1);

    public Object clone() throws CloneNotSupportedException;
}

