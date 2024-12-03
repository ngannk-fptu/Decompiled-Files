/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndPerson;
import java.util.Date;
import java.util.List;
import org.jdom2.Element;

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

    public List<SyndLink> getLinks();

    public void setLinks(List<SyndLink> var1);

    public SyndContent getDescription();

    public void setDescription(SyndContent var1);

    public List<SyndContent> getContents();

    public void setContents(List<SyndContent> var1);

    public List<SyndEnclosure> getEnclosures();

    public void setEnclosures(List<SyndEnclosure> var1);

    public Date getPublishedDate();

    public void setPublishedDate(Date var1);

    public Date getUpdatedDate();

    public void setUpdatedDate(Date var1);

    public List<SyndPerson> getAuthors();

    public void setAuthors(List<SyndPerson> var1);

    public String getAuthor();

    public void setAuthor(String var1);

    public List<SyndPerson> getContributors();

    public void setContributors(List<SyndPerson> var1);

    public List<SyndCategory> getCategories();

    public void setCategories(List<SyndCategory> var1);

    public SyndFeed getSource();

    public void setSource(SyndFeed var1);

    public Object getWireEntry();

    @Override
    public Module getModule(String var1);

    @Override
    public List<Module> getModules();

    @Override
    public void setModules(List<Module> var1);

    public List<Element> getForeignMarkup();

    public void setForeignMarkup(List<Element> var1);

    public String getComments();

    public void setComments(String var1);

    public Object clone() throws CloneNotSupportedException;

    public SyndLink findRelatedLink(String var1);
}

