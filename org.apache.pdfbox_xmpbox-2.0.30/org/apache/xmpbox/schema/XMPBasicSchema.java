/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AgentNameType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.ThumbnailType;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URLType;

@StructuredType(preferedPrefix="xmp", namespace="http://ns.adobe.com/xap/1.0/")
public class XMPBasicSchema
extends XMPSchema {
    @PropertyType(type=Types.XPath, card=Cardinality.Bag)
    public static final String ADVISORY = "Advisory";
    @PropertyType(type=Types.URL, card=Cardinality.Simple)
    public static final String BASEURL = "BaseURL";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String CREATEDATE = "CreateDate";
    @PropertyType(type=Types.AgentName, card=Cardinality.Simple)
    public static final String CREATORTOOL = "CreatorTool";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String IDENTIFIER = "Identifier";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String LABEL = "Label";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String METADATADATE = "MetadataDate";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String MODIFYDATE = "ModifyDate";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String NICKNAME = "Nickname";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String RATING = "Rating";
    @PropertyType(type=Types.Thumbnail, card=Cardinality.Alt)
    public static final String THUMBNAILS = "Thumbnails";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String MODIFIER_DATE = "ModifierDate";
    private ArrayProperty altThumbs;

    public XMPBasicSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public XMPBasicSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public void addThumbnails(Integer height, Integer width, String format, String img) {
        if (this.altThumbs == null) {
            this.altThumbs = this.createArrayProperty(THUMBNAILS, Cardinality.Alt);
            this.addProperty(this.altThumbs);
        }
        ThumbnailType thumb = new ThumbnailType(this.getMetadata());
        thumb.setHeight(height);
        thumb.setWidth(width);
        thumb.setFormat(format);
        thumb.setImage(img);
        this.altThumbs.getContainer().addProperty(thumb);
    }

    public void addAdvisory(String xpath) {
        this.addQualifiedBagValue(ADVISORY, xpath);
    }

    public void removeAdvisory(String xpath) {
        this.removeUnqualifiedBagValue(ADVISORY, xpath);
    }

    public void setBaseURL(String url) {
        URLType tt = (URLType)this.instanciateSimple(BASEURL, url);
        this.setBaseURLProperty(tt);
    }

    public void setBaseURLProperty(URLType url) {
        this.addProperty(url);
    }

    public void setCreateDate(Calendar date) {
        DateType tt = (DateType)this.instanciateSimple(CREATEDATE, date);
        this.setCreateDateProperty(tt);
    }

    public void setCreateDateProperty(DateType date) {
        this.addProperty(date);
    }

    public void setCreatorTool(String creatorTool) {
        AgentNameType tt = (AgentNameType)this.instanciateSimple(CREATORTOOL, creatorTool);
        this.setCreatorToolProperty(tt);
    }

    public void setCreatorToolProperty(AgentNameType creatorTool) {
        this.addProperty(creatorTool);
    }

    public void addIdentifier(String text) {
        this.addQualifiedBagValue(IDENTIFIER, text);
    }

    public void removeIdentifier(String text) {
        this.removeUnqualifiedBagValue(IDENTIFIER, text);
    }

    public void setLabel(String text) {
        TextType tt = (TextType)this.instanciateSimple(LABEL, text);
        this.setLabelProperty(tt);
    }

    public void setLabelProperty(TextType text) {
        this.addProperty(text);
    }

    public void setMetadataDate(Calendar date) {
        DateType tt = (DateType)this.instanciateSimple(METADATADATE, date);
        this.setMetadataDateProperty(tt);
    }

    public void setMetadataDateProperty(DateType date) {
        this.addProperty(date);
    }

    public void setModifyDate(Calendar date) {
        DateType tt = (DateType)this.instanciateSimple(MODIFYDATE, date);
        this.setModifyDateProperty(tt);
    }

    public void setModifierDate(Calendar date) {
        DateType tt = (DateType)this.instanciateSimple(MODIFIER_DATE, date);
        this.setModifierDateProperty(tt);
    }

    public void setModifyDateProperty(DateType date) {
        this.addProperty(date);
    }

    public void setModifierDateProperty(DateType date) {
        this.addProperty(date);
    }

    public void setNickname(String text) {
        TextType tt = (TextType)this.instanciateSimple(NICKNAME, text);
        this.setNicknameProperty(tt);
    }

    public void setNicknameProperty(TextType text) {
        this.addProperty(text);
    }

    public void setRating(Integer rate) {
        IntegerType tt = (IntegerType)this.instanciateSimple(RATING, rate);
        this.setRatingProperty(tt);
    }

    public void setRatingProperty(IntegerType rate) {
        this.addProperty(rate);
    }

    public ArrayProperty getAdvisoryProperty() {
        return (ArrayProperty)this.getProperty(ADVISORY);
    }

    public List<String> getAdvisory() {
        return this.getUnqualifiedBagValueList(ADVISORY);
    }

    public TextType getBaseURLProperty() {
        return (TextType)this.getProperty(BASEURL);
    }

    public String getBaseURL() {
        TextType tt = (TextType)this.getProperty(BASEURL);
        return tt == null ? null : tt.getStringValue();
    }

    public DateType getCreateDateProperty() {
        return (DateType)this.getProperty(CREATEDATE);
    }

    public Calendar getCreateDate() {
        DateType createDate = (DateType)this.getProperty(CREATEDATE);
        if (createDate != null) {
            return createDate.getValue();
        }
        return null;
    }

    public TextType getCreatorToolProperty() {
        return (TextType)this.getProperty(CREATORTOOL);
    }

    public String getCreatorTool() {
        TextType tt = (TextType)this.getProperty(CREATORTOOL);
        return tt == null ? null : tt.getStringValue();
    }

    public ArrayProperty getIdentifiersProperty() {
        return (ArrayProperty)this.getProperty(IDENTIFIER);
    }

    public List<String> getIdentifiers() {
        return this.getUnqualifiedBagValueList(IDENTIFIER);
    }

    public TextType getLabelProperty() {
        return (TextType)this.getProperty(LABEL);
    }

    public String getLabel() {
        TextType tt = (TextType)this.getProperty(LABEL);
        return tt == null ? null : tt.getStringValue();
    }

    public DateType getMetadataDateProperty() {
        return (DateType)this.getProperty(METADATADATE);
    }

    public Calendar getMetadataDate() {
        DateType dt = (DateType)this.getProperty(METADATADATE);
        return dt == null ? null : dt.getValue();
    }

    public DateType getModifyDateProperty() {
        return (DateType)this.getProperty(MODIFYDATE);
    }

    public DateType getModifierDateProperty() {
        return (DateType)this.getProperty(MODIFIER_DATE);
    }

    public Calendar getModifyDate() {
        DateType modifyDate = (DateType)this.getProperty(MODIFYDATE);
        if (modifyDate != null) {
            return modifyDate.getValue();
        }
        return null;
    }

    public Calendar getModifierDate() {
        DateType modifierDate = (DateType)this.getProperty(MODIFIER_DATE);
        if (modifierDate != null) {
            return modifierDate.getValue();
        }
        return null;
    }

    public TextType getNicknameProperty() {
        return (TextType)this.getProperty(NICKNAME);
    }

    public String getNickname() {
        TextType tt = (TextType)this.getProperty(NICKNAME);
        return tt == null ? null : tt.getStringValue();
    }

    public IntegerType getRatingProperty() {
        return (IntegerType)this.getProperty(RATING);
    }

    public Integer getRating() {
        IntegerType it = (IntegerType)this.getProperty(RATING);
        return it == null ? null : it.getValue();
    }

    public List<ThumbnailType> getThumbnailsProperty() throws BadFieldValueException {
        List<AbstractField> tmp = this.getUnqualifiedArrayList(THUMBNAILS);
        if (tmp != null) {
            ArrayList<ThumbnailType> thumbs = new ArrayList<ThumbnailType>();
            for (AbstractField abstractField : tmp) {
                if (abstractField instanceof ThumbnailType) {
                    thumbs.add((ThumbnailType)abstractField);
                    continue;
                }
                throw new BadFieldValueException("Thumbnail expected and " + abstractField.getClass().getName() + " found.");
            }
            return thumbs;
        }
        return null;
    }
}

