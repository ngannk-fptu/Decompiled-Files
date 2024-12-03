/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas;

import java.util.Date;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchemaAdapter;
import org.apache.xmlgraphics.xmp.XMPSchemaRegistry;

public class DublinCoreAdapter
extends XMPSchemaAdapter {
    private static final String CONTRIBUTOR = "contributor";
    private static final String COVERAGE = "coverage";
    private static final String CREATOR = "creator";
    private static final String DATE = "date";
    private static final String DESCRIPTION = "description";
    private static final String FORMAT = "format";
    private static final String IDENTIFIER = "identifier";
    private static final String LANGUAGE = "language";
    private static final String PUBLISHER = "publisher";
    private static final String RELATION = "relation";
    private static final String RIGHTS = "rights";
    private static final String SOURCE = "source";
    private static final String SUBJECT = "subject";
    private static final String TITLE = "title";
    private static final String TYPE = "type";

    public DublinCoreAdapter(Metadata meta) {
        super(meta, XMPSchemaRegistry.getInstance().getSchema("http://purl.org/dc/elements/1.1/"));
    }

    public void addContributor(String value) {
        this.addStringToBag(CONTRIBUTOR, value);
    }

    public boolean removeContributor(String value) {
        return this.removeStringFromArray(CONTRIBUTOR, value);
    }

    public String[] getContributors() {
        return this.getStringArray(CONTRIBUTOR);
    }

    public void setCoverage(String value) {
        this.setValue(COVERAGE, value);
    }

    public String getCoverage() {
        return this.getValue(COVERAGE);
    }

    public void addCreator(String value) {
        this.addStringToSeq(CREATOR, value);
    }

    public boolean removeCreator(String value) {
        return this.removeStringFromArray(CREATOR, value);
    }

    public String[] getCreators() {
        return this.getStringArray(CREATOR);
    }

    public void addDate(Date value) {
        this.addDateToSeq(DATE, value);
    }

    public Date[] getDates() {
        return this.getDateArray(DATE);
    }

    public Date getDate() {
        Date[] dates = this.getDates();
        if (dates != null) {
            Date latest = null;
            for (Date date : dates) {
                if (latest != null && date.getTime() <= latest.getTime()) continue;
                latest = date;
            }
            return latest;
        }
        return null;
    }

    public void setDescription(String lang, String value) {
        this.setLangAlt(DESCRIPTION, lang, value);
    }

    public String getDescription() {
        return this.getDescription(null);
    }

    public String getDescription(String lang) {
        return this.getLangAlt(lang, DESCRIPTION);
    }

    public void setFormat(String value) {
        this.setValue(FORMAT, value);
    }

    public String getFormat() {
        return this.getValue(FORMAT);
    }

    public void setIdentifier(String value) {
        this.setValue(IDENTIFIER, value);
    }

    public String getIdentifier() {
        return this.getValue(IDENTIFIER);
    }

    public void addLanguage(String value) {
        this.addStringToBag(LANGUAGE, value);
    }

    public String[] getLanguages() {
        return this.getStringArray(LANGUAGE);
    }

    public void addPublisher(String value) {
        this.addStringToBag(PUBLISHER, value);
    }

    public String[] getPublisher() {
        return this.getStringArray(PUBLISHER);
    }

    public void addRelation(String value) {
        this.addStringToBag(RELATION, value);
    }

    public String[] getRelations() {
        return this.getStringArray(RELATION);
    }

    public void setRights(String lang, String value) {
        this.setLangAlt(RIGHTS, lang, value);
    }

    public String getRights() {
        return this.getRights(null);
    }

    public String getRights(String lang) {
        return this.getLangAlt(lang, RIGHTS);
    }

    public void setSource(String value) {
        this.setValue(SOURCE, value);
    }

    public String getSource() {
        return this.getValue(SOURCE);
    }

    public void addSubject(String value) {
        this.addStringToBag(SUBJECT, value);
    }

    public String[] getSubjects() {
        return this.getStringArray(SUBJECT);
    }

    public void setTitle(String value) {
        this.setTitle(null, value);
    }

    public void setTitle(String lang, String value) {
        this.setLangAlt(TITLE, lang, value);
    }

    public String getTitle() {
        return this.getTitle(null);
    }

    public String getTitle(String lang) {
        return this.getLangAlt(lang, TITLE);
    }

    public String removeTitle(String lang) {
        return this.removeLangAlt(lang, TITLE);
    }

    public void addType(String value) {
        this.addStringToBag(TYPE, value);
    }

    public String[] getTypes() {
        return this.getStringArray(TYPE);
    }
}

