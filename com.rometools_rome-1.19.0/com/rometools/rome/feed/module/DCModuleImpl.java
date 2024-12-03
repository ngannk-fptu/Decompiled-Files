/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.module.DCSubject;
import com.rometools.rome.feed.module.DCSubjectImpl;
import com.rometools.rome.feed.module.ModuleImpl;
import com.rometools.utils.Lists;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DCModuleImpl
extends ModuleImpl
implements DCModule {
    private static final long serialVersionUID = 1L;
    private List<String> title;
    private List<String> creator;
    private List<DCSubject> subject;
    private List<String> description;
    private List<String> publisher;
    private List<String> contributors;
    private List<Date> date;
    private List<String> type;
    private List<String> format;
    private List<String> identifier;
    private List<String> source;
    private List<String> language;
    private List<String> relation;
    private List<String> coverage;
    private List<String> rights;
    private static final Set<String> IGNORE_PROPERTIES = new HashSet<String>();
    public static final Set<String> CONVENIENCE_PROPERTIES = Collections.unmodifiableSet(IGNORE_PROPERTIES);
    private static final CopyFromHelper COPY_FROM_HELPER;

    public DCModuleImpl() {
        super(DCModule.class, "http://purl.org/dc/elements/1.1/");
    }

    @Override
    public List<String> getTitles() {
        this.title = Lists.createWhenNull(this.title);
        return this.title;
    }

    @Override
    public void setTitles(List<String> titles) {
        this.title = titles;
    }

    @Override
    public String getTitle() {
        return (String)Lists.firstEntry(this.title);
    }

    @Override
    public void setTitle(String title) {
        this.title = Lists.create((Object)title);
    }

    @Override
    public List<String> getCreators() {
        this.creator = Lists.createWhenNull(this.creator);
        return this.creator;
    }

    @Override
    public void setCreators(List<String> creators) {
        this.creator = creators;
    }

    @Override
    public String getCreator() {
        return (String)Lists.firstEntry(this.creator);
    }

    @Override
    public void setCreator(String creator) {
        this.creator = Lists.create((Object)creator);
    }

    @Override
    public List<DCSubject> getSubjects() {
        this.subject = Lists.createWhenNull(this.subject);
        return this.subject;
    }

    @Override
    public void setSubjects(List<DCSubject> subjects) {
        this.subject = subjects;
    }

    @Override
    public DCSubject getSubject() {
        return (DCSubject)Lists.firstEntry(this.subject);
    }

    @Override
    public void setSubject(DCSubject subject) {
        this.subject = Lists.create((Object)subject);
    }

    @Override
    public List<String> getDescriptions() {
        this.description = Lists.createWhenNull(this.description);
        return this.description;
    }

    @Override
    public void setDescriptions(List<String> descriptions) {
        this.description = descriptions;
    }

    @Override
    public String getDescription() {
        return (String)Lists.firstEntry(this.description);
    }

    @Override
    public void setDescription(String description) {
        this.description = Lists.create((Object)description);
    }

    @Override
    public List<String> getPublishers() {
        this.publisher = Lists.createWhenNull(this.publisher);
        return this.publisher;
    }

    @Override
    public void setPublishers(List<String> publishers) {
        this.publisher = publishers;
    }

    @Override
    public String getPublisher() {
        return (String)Lists.firstEntry(this.publisher);
    }

    @Override
    public void setPublisher(String publisher) {
        this.publisher = Lists.create((Object)publisher);
    }

    @Override
    public List<String> getContributors() {
        this.contributors = Lists.createWhenNull(this.contributors);
        return this.contributors;
    }

    @Override
    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }

    @Override
    public String getContributor() {
        return (String)Lists.firstEntry(this.contributors);
    }

    @Override
    public void setContributor(String contributor) {
        this.contributors = Lists.create((Object)contributor);
    }

    @Override
    public List<Date> getDates() {
        this.date = Lists.createWhenNull(this.date);
        return this.date;
    }

    @Override
    public void setDates(List<Date> dates) {
        this.date = dates;
    }

    @Override
    public Date getDate() {
        return (Date)Lists.firstEntry(this.date);
    }

    @Override
    public void setDate(Date date) {
        this.date = Lists.create((Object)date);
    }

    @Override
    public List<String> getTypes() {
        this.type = Lists.createWhenNull(this.type);
        return this.type;
    }

    @Override
    public void setTypes(List<String> types) {
        this.type = types;
    }

    @Override
    public String getType() {
        return (String)Lists.firstEntry(this.type);
    }

    @Override
    public void setType(String type) {
        this.type = Lists.create((Object)type);
    }

    @Override
    public List<String> getFormats() {
        this.format = Lists.createWhenNull(this.format);
        return this.format;
    }

    @Override
    public void setFormats(List<String> formats) {
        this.format = formats;
    }

    @Override
    public String getFormat() {
        return (String)Lists.firstEntry(this.format);
    }

    @Override
    public void setFormat(String format) {
        this.format = Lists.create((Object)format);
    }

    @Override
    public List<String> getIdentifiers() {
        this.identifier = Lists.createWhenNull(this.identifier);
        return this.identifier;
    }

    @Override
    public void setIdentifiers(List<String> identifiers) {
        this.identifier = identifiers;
    }

    @Override
    public String getIdentifier() {
        return (String)Lists.firstEntry(this.identifier);
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = Lists.create((Object)identifier);
    }

    @Override
    public List<String> getSources() {
        this.source = Lists.createWhenNull(this.source);
        return this.source;
    }

    @Override
    public void setSources(List<String> sources) {
        this.source = sources;
    }

    @Override
    public String getSource() {
        return (String)Lists.firstEntry(this.source);
    }

    @Override
    public void setSource(String source) {
        this.source = Lists.create((Object)source);
    }

    @Override
    public List<String> getLanguages() {
        this.language = Lists.createWhenNull(this.language);
        return this.language;
    }

    @Override
    public void setLanguages(List<String> languages) {
        this.language = languages;
    }

    @Override
    public String getLanguage() {
        return (String)Lists.firstEntry(this.language);
    }

    @Override
    public void setLanguage(String language) {
        this.language = Lists.create((Object)language);
    }

    @Override
    public List<String> getRelations() {
        this.relation = Lists.createWhenNull(this.relation);
        return this.relation;
    }

    @Override
    public void setRelations(List<String> relations) {
        this.relation = relations;
    }

    @Override
    public String getRelation() {
        return (String)Lists.firstEntry(this.relation);
    }

    @Override
    public void setRelation(String relation) {
        this.relation = Lists.create((Object)relation);
    }

    @Override
    public List<String> getCoverages() {
        this.coverage = Lists.createWhenNull(this.coverage);
        return this.coverage;
    }

    @Override
    public void setCoverages(List<String> coverages) {
        this.coverage = coverages;
    }

    @Override
    public String getCoverage() {
        return (String)Lists.firstEntry(this.coverage);
    }

    @Override
    public void setCoverage(String coverage) {
        this.coverage = Lists.create((Object)coverage);
    }

    @Override
    public List<String> getRightsList() {
        this.rights = Lists.createWhenNull(this.rights);
        return this.rights;
    }

    @Override
    public void setRightsList(List<String> rights) {
        this.rights = rights;
    }

    @Override
    public String getRights() {
        return (String)Lists.firstEntry(this.rights);
    }

    @Override
    public void setRights(String rights) {
        this.rights = Lists.create((Object)rights);
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, CONVENIENCE_PROPERTIES);
    }

    @Override
    public final boolean equals(Object other) {
        return EqualsBean.beanEquals(DCModule.class, this, other);
    }

    @Override
    public final int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    @Override
    public final String toString() {
        return ToStringBean.toString(DCModule.class, this);
    }

    public final Class<DCModule> getInterface() {
        return DCModule.class;
    }

    @Override
    public final void copyFrom(CopyFrom obj) {
        COPY_FROM_HELPER.copy(this, obj);
    }

    static {
        IGNORE_PROPERTIES.add("title");
        IGNORE_PROPERTIES.add("creator");
        IGNORE_PROPERTIES.add("subject");
        IGNORE_PROPERTIES.add("description");
        IGNORE_PROPERTIES.add("publisher");
        IGNORE_PROPERTIES.add("contributor");
        IGNORE_PROPERTIES.add("date");
        IGNORE_PROPERTIES.add("type");
        IGNORE_PROPERTIES.add("format");
        IGNORE_PROPERTIES.add("identifier");
        IGNORE_PROPERTIES.add("source");
        IGNORE_PROPERTIES.add("language");
        IGNORE_PROPERTIES.add("relation");
        IGNORE_PROPERTIES.add("coverage");
        IGNORE_PROPERTIES.add("rights");
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("titles", String.class);
        basePropInterfaceMap.put("creators", String.class);
        basePropInterfaceMap.put("subjects", DCSubject.class);
        basePropInterfaceMap.put("descriptions", String.class);
        basePropInterfaceMap.put("publishers", String.class);
        basePropInterfaceMap.put("contributors", String.class);
        basePropInterfaceMap.put("dates", Date.class);
        basePropInterfaceMap.put("types", String.class);
        basePropInterfaceMap.put("formats", String.class);
        basePropInterfaceMap.put("identifiers", String.class);
        basePropInterfaceMap.put("sources", String.class);
        basePropInterfaceMap.put("languages", String.class);
        basePropInterfaceMap.put("relations", String.class);
        basePropInterfaceMap.put("coverages", String.class);
        basePropInterfaceMap.put("rightsList", String.class);
        HashMap basePropClassImplMap = new HashMap();
        basePropClassImplMap.put(DCSubject.class, DCSubjectImpl.class);
        COPY_FROM_HELPER = new CopyFromHelper(DCModule.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

