/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.module.DCSubject;
import com.rometools.rome.feed.module.Module;
import java.util.Date;
import java.util.List;

public interface DCModule
extends Module {
    public static final String URI = "http://purl.org/dc/elements/1.1/";

    public List<String> getTitles();

    public void setTitles(List<String> var1);

    public String getTitle();

    public void setTitle(String var1);

    public List<String> getCreators();

    public void setCreators(List<String> var1);

    public String getCreator();

    public void setCreator(String var1);

    public List<DCSubject> getSubjects();

    public void setSubjects(List<DCSubject> var1);

    public DCSubject getSubject();

    public void setSubject(DCSubject var1);

    public List<String> getDescriptions();

    public void setDescriptions(List<String> var1);

    public String getDescription();

    public void setDescription(String var1);

    public List<String> getPublishers();

    public void setPublishers(List<String> var1);

    public String getPublisher();

    public void setPublisher(String var1);

    public List<String> getContributors();

    public void setContributors(List<String> var1);

    public String getContributor();

    public void setContributor(String var1);

    public List<Date> getDates();

    public void setDates(List<Date> var1);

    public Date getDate();

    public void setDate(Date var1);

    public List<String> getTypes();

    public void setTypes(List<String> var1);

    public String getType();

    public void setType(String var1);

    public List<String> getFormats();

    public void setFormats(List<String> var1);

    public String getFormat();

    public void setFormat(String var1);

    public List<String> getIdentifiers();

    public void setIdentifiers(List<String> var1);

    public String getIdentifier();

    public void setIdentifier(String var1);

    public List<String> getSources();

    public void setSources(List<String> var1);

    public String getSource();

    public void setSource(String var1);

    public List<String> getLanguages();

    public void setLanguages(List<String> var1);

    public String getLanguage();

    public void setLanguage(String var1);

    public List<String> getRelations();

    public void setRelations(List<String> var1);

    public String getRelation();

    public void setRelation(String var1);

    public List<String> getCoverages();

    public void setCoverages(List<String> var1);

    public String getCoverage();

    public void setCoverage(String var1);

    public List<String> getRightsList();

    public void setRightsList(List<String> var1);

    public String getRights();

    public void setRights(String var1);
}

