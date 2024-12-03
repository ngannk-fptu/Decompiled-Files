/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.module;

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.module.DCSubject;
import com.sun.syndication.feed.module.Module;
import java.util.Date;
import java.util.List;

public interface DCModule
extends Module,
CopyFrom {
    public static final String URI = "http://purl.org/dc/elements/1.1/";

    public List getTitles();

    public void setTitles(List var1);

    public String getTitle();

    public void setTitle(String var1);

    public List getCreators();

    public void setCreators(List var1);

    public String getCreator();

    public void setCreator(String var1);

    public List getSubjects();

    public void setSubjects(List var1);

    public DCSubject getSubject();

    public void setSubject(DCSubject var1);

    public List getDescriptions();

    public void setDescriptions(List var1);

    public String getDescription();

    public void setDescription(String var1);

    public List getPublishers();

    public void setPublishers(List var1);

    public String getPublisher();

    public void setPublisher(String var1);

    public List getContributors();

    public void setContributors(List var1);

    public String getContributor();

    public void setContributor(String var1);

    public List getDates();

    public void setDates(List var1);

    public Date getDate();

    public void setDate(Date var1);

    public List getTypes();

    public void setTypes(List var1);

    public String getType();

    public void setType(String var1);

    public List getFormats();

    public void setFormats(List var1);

    public String getFormat();

    public void setFormat(String var1);

    public List getIdentifiers();

    public void setIdentifiers(List var1);

    public String getIdentifier();

    public void setIdentifier(String var1);

    public List getSources();

    public void setSources(List var1);

    public String getSource();

    public void setSource(String var1);

    public List getLanguages();

    public void setLanguages(List var1);

    public String getLanguage();

    public void setLanguage(String var1);

    public List getRelations();

    public void setRelations(List var1);

    public String getRelation();

    public void setRelation(String var1);

    public List getCoverages();

    public void setCoverages(List var1);

    public String getCoverage();

    public void setCoverage(String var1);

    public List getRightsList();

    public void setRightsList(List var1);

    public String getRights();

    public void setRights(String var1);
}

