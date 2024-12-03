/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.Calendar;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.MIMEType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="dc", namespace="http://purl.org/dc/elements/1.1/")
public class DublinCoreSchema
extends XMPSchema {
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String CONTRIBUTOR = "contributor";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String COVERAGE = "coverage";
    @PropertyType(type=Types.Text, card=Cardinality.Seq)
    public static final String CREATOR = "creator";
    @PropertyType(type=Types.Date, card=Cardinality.Seq)
    public static final String DATE = "date";
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String DESCRIPTION = "description";
    @PropertyType(type=Types.MIMEType, card=Cardinality.Simple)
    public static final String FORMAT = "format";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String IDENTIFIER = "identifier";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String LANGUAGE = "language";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String PUBLISHER = "publisher";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String RELATION = "relation";
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String RIGHTS = "rights";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String SOURCE = "source";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String SUBJECT = "subject";
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String TITLE = "title";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String TYPE = "type";

    public DublinCoreSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public DublinCoreSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public void addContributor(String properName) {
        this.addQualifiedBagValue(CONTRIBUTOR, properName);
    }

    public void removeContributor(String properName) {
        this.removeUnqualifiedBagValue(CONTRIBUTOR, properName);
    }

    public void setCoverage(String text) {
        this.addProperty(this.createTextType(COVERAGE, text));
    }

    public void setCoverageProperty(TextType text) {
        this.addProperty(text);
    }

    public void addCreator(String properName) {
        this.addUnqualifiedSequenceValue(CREATOR, properName);
    }

    public void removeCreator(String name) {
        this.removeUnqualifiedSequenceValue(CREATOR, name);
    }

    public void addDate(Calendar date) {
        this.addUnqualifiedSequenceDateValue(DATE, date);
    }

    public void removeDate(Calendar date) {
        this.removeUnqualifiedSequenceDateValue(DATE, date);
    }

    public void addDescription(String lang, String value) {
        this.setUnqualifiedLanguagePropertyValue(DESCRIPTION, lang, value);
    }

    public void setDescription(String value) {
        this.addDescription(null, value);
    }

    public void setFormat(String mimeType) {
        this.addProperty(this.createTextType(FORMAT, mimeType));
    }

    public void setIdentifier(String text) {
        this.addProperty(this.createTextType(IDENTIFIER, text));
    }

    public void setIdentifierProperty(TextType text) {
        this.addProperty(text);
    }

    public void addLanguage(String locale) {
        this.addQualifiedBagValue(LANGUAGE, locale);
    }

    public void removeLanguage(String locale) {
        this.removeUnqualifiedBagValue(LANGUAGE, locale);
    }

    public void addPublisher(String properName) {
        this.addQualifiedBagValue(PUBLISHER, properName);
    }

    public void removePublisher(String name) {
        this.removeUnqualifiedBagValue(PUBLISHER, name);
    }

    public void addRelation(String text) {
        this.addQualifiedBagValue(RELATION, text);
    }

    public void removeRelation(String text) {
        this.removeUnqualifiedBagValue(RELATION, text);
    }

    public void addRights(String lang, String value) {
        this.setUnqualifiedLanguagePropertyValue(RIGHTS, lang, value);
    }

    public void setSource(String text) {
        this.addProperty(this.createTextType(SOURCE, text));
    }

    public void setSourceProperty(TextType text) {
        this.addProperty(text);
    }

    public void setFormatProperty(MIMEType text) {
        this.addProperty(text);
    }

    public void addSubject(String text) {
        this.addQualifiedBagValue(SUBJECT, text);
    }

    public void removeSubject(String text) {
        this.removeUnqualifiedBagValue(SUBJECT, text);
    }

    public void setTitle(String lang, String value) {
        this.setUnqualifiedLanguagePropertyValue(TITLE, lang, value);
    }

    public void setTitle(String value) {
        this.setTitle(null, value);
    }

    public void addTitle(String lang, String value) {
        this.setTitle(lang, value);
    }

    public void addType(String type) {
        this.addQualifiedBagValue(TYPE, type);
    }

    public ArrayProperty getContributorsProperty() {
        return (ArrayProperty)this.getProperty(CONTRIBUTOR);
    }

    public List<String> getContributors() {
        return this.getUnqualifiedBagValueList(CONTRIBUTOR);
    }

    public TextType getCoverageProperty() {
        return (TextType)this.getProperty(COVERAGE);
    }

    public String getCoverage() {
        TextType tt = (TextType)this.getProperty(COVERAGE);
        return tt == null ? null : tt.getStringValue();
    }

    public ArrayProperty getCreatorsProperty() {
        return (ArrayProperty)this.getProperty(CREATOR);
    }

    public List<String> getCreators() {
        return this.getUnqualifiedSequenceValueList(CREATOR);
    }

    public ArrayProperty getDatesProperty() {
        return (ArrayProperty)this.getProperty(DATE);
    }

    public List<Calendar> getDates() {
        return this.getUnqualifiedSequenceDateValueList(DATE);
    }

    public ArrayProperty getDescriptionProperty() {
        return (ArrayProperty)this.getProperty(DESCRIPTION);
    }

    public List<String> getDescriptionLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(DESCRIPTION);
    }

    public String getDescription(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(DESCRIPTION, lang);
    }

    public String getDescription() {
        return this.getDescription(null);
    }

    public TextType getFormatProperty() {
        return (TextType)this.getProperty(FORMAT);
    }

    public String getFormat() {
        TextType tt = (TextType)this.getProperty(FORMAT);
        return tt == null ? null : tt.getStringValue();
    }

    public TextType getIdentifierProperty() {
        return (TextType)this.getProperty(IDENTIFIER);
    }

    public String getIdentifier() {
        TextType tt = (TextType)this.getProperty(IDENTIFIER);
        return tt == null ? null : tt.getStringValue();
    }

    public ArrayProperty getLanguagesProperty() {
        return (ArrayProperty)this.getProperty(LANGUAGE);
    }

    public List<String> getLanguages() {
        return this.getUnqualifiedBagValueList(LANGUAGE);
    }

    public ArrayProperty getPublishersProperty() {
        return (ArrayProperty)this.getProperty(PUBLISHER);
    }

    public List<String> getPublishers() {
        return this.getUnqualifiedBagValueList(PUBLISHER);
    }

    public ArrayProperty getRelationsProperty() {
        return (ArrayProperty)this.getProperty(RELATION);
    }

    public List<String> getRelations() {
        return this.getUnqualifiedBagValueList(RELATION);
    }

    public ArrayProperty getRightsProperty() {
        return (ArrayProperty)this.getProperty(RIGHTS);
    }

    public List<String> getRightsLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(RIGHTS);
    }

    public String getRights(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(RIGHTS, lang);
    }

    public String getRights() {
        return this.getRights(null);
    }

    public TextType getSourceProperty() {
        return (TextType)this.getProperty(SOURCE);
    }

    public String getSource() {
        TextType tt = (TextType)this.getProperty(SOURCE);
        return tt == null ? null : tt.getStringValue();
    }

    public ArrayProperty getSubjectsProperty() {
        return (ArrayProperty)this.getProperty(SUBJECT);
    }

    public List<String> getSubjects() {
        return this.getUnqualifiedBagValueList(SUBJECT);
    }

    public ArrayProperty getTitleProperty() {
        return (ArrayProperty)this.getProperty(TITLE);
    }

    public List<String> getTitleLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(TITLE);
    }

    public String getTitle(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(TITLE, lang);
    }

    public String getTitle() {
        return this.getTitle(null);
    }

    public ArrayProperty getTypesProperty() {
        return (ArrayProperty)this.getProperty(TYPE);
    }

    public List<String> getTypes() {
        return this.getUnqualifiedBagValueList(TYPE);
    }

    public void removeType(String type) {
        this.removeUnqualifiedBagValue(TYPE, type);
    }
}

