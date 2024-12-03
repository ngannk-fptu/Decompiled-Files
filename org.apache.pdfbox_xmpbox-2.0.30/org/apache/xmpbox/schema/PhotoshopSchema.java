/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.ArrayList;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.LayerType;
import org.apache.xmpbox.type.ProperNameType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URIType;

@StructuredType(preferedPrefix="photoshop", namespace="http://ns.adobe.com/photoshop/1.0/")
public class PhotoshopSchema
extends XMPSchema {
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String ANCESTORID = "AncestorID";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String AUTHORS_POSITION = "AuthorsPosition";
    @PropertyType(type=Types.ProperName, card=Cardinality.Simple)
    public static final String CAPTION_WRITER = "CaptionWriter";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String CATEGORY = "Category";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String CITY = "City";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String COLOR_MODE = "ColorMode";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String COUNTRY = "Country";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String CREDIT = "Credit";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String DATE_CREATED = "DateCreated";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String DOCUMENT_ANCESTORS = "DocumentAncestors";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String HEADLINE = "Headline";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String HISTORY = "History";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String ICC_PROFILE = "ICCProfile";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String INSTRUCTIONS = "Instructions";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String SOURCE = "Source";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String STATE = "State";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String SUPPLEMENTAL_CATEGORIES = "SupplementalCategories";
    @PropertyType(type=Types.Layer, card=Cardinality.Seq)
    public static final String TEXT_LAYERS = "TextLayers";
    private ArrayProperty seqLayer;
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String TRANSMISSION_REFERENCE = "TransmissionReference";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String URGENCY = "Urgency";

    public PhotoshopSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public PhotoshopSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public URIType getAncestorIDProperty() {
        return (URIType)this.getProperty(ANCESTORID);
    }

    public String getAncestorID() {
        TextType tt = (TextType)this.getProperty(ANCESTORID);
        return tt == null ? null : tt.getStringValue();
    }

    public void setAncestorID(String text) {
        URIType tt = (URIType)this.instanciateSimple(ANCESTORID, text);
        this.setAncestorIDProperty(tt);
    }

    public void setAncestorIDProperty(URIType text) {
        this.addProperty(text);
    }

    public TextType getAuthorsPositionProperty() {
        return (TextType)this.getProperty(AUTHORS_POSITION);
    }

    public String getAuthorsPosition() {
        TextType tt = (TextType)this.getProperty(AUTHORS_POSITION);
        return tt == null ? null : tt.getStringValue();
    }

    public void setAuthorsPosition(String text) {
        TextType tt = (TextType)this.instanciateSimple(AUTHORS_POSITION, text);
        this.setAuthorsPositionProperty(tt);
    }

    public void setAuthorsPositionProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getCaptionWriterProperty() {
        return (TextType)this.getProperty(CAPTION_WRITER);
    }

    public String getCaptionWriter() {
        TextType tt = (TextType)this.getProperty(CAPTION_WRITER);
        return tt == null ? null : tt.getStringValue();
    }

    public void setCaptionWriter(String text) {
        ProperNameType tt = (ProperNameType)this.instanciateSimple(CAPTION_WRITER, text);
        this.setCaptionWriterProperty(tt);
    }

    public void setCaptionWriterProperty(ProperNameType text) {
        this.addProperty(text);
    }

    public TextType getCategoryProperty() {
        return (TextType)this.getProperty(CATEGORY);
    }

    public String getCategory() {
        TextType tt = (TextType)this.getProperty(CATEGORY);
        return tt == null ? null : tt.getStringValue();
    }

    public void setCategory(String text) {
        TextType tt = (TextType)this.instanciateSimple(CATEGORY, text);
        this.setCategoryProperty(tt);
    }

    public void setCategoryProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getCityProperty() {
        return (TextType)this.getProperty(CITY);
    }

    public String getCity() {
        TextType tt = (TextType)this.getProperty(CITY);
        return tt == null ? null : tt.getStringValue();
    }

    public void setCity(String text) {
        TextType tt = (TextType)this.instanciateSimple(CITY, text);
        this.setCityProperty(tt);
    }

    public void setCityProperty(TextType text) {
        this.addProperty(text);
    }

    public IntegerType getColorModeProperty() {
        return (IntegerType)this.getProperty(COLOR_MODE);
    }

    public Integer getColorMode() {
        IntegerType tt = (IntegerType)this.getProperty(COLOR_MODE);
        return tt == null ? null : tt.getValue();
    }

    public void setColorMode(String text) {
        IntegerType tt = (IntegerType)this.instanciateSimple(COLOR_MODE, text);
        this.setColorModeProperty(tt);
    }

    public void setColorModeProperty(IntegerType text) {
        this.addProperty(text);
    }

    public TextType getCountryProperty() {
        return (TextType)this.getProperty(COUNTRY);
    }

    public String getCountry() {
        TextType tt = (TextType)this.getProperty(COUNTRY);
        return tt == null ? null : tt.getStringValue();
    }

    public void setCountry(String text) {
        TextType tt = (TextType)this.instanciateSimple(COUNTRY, text);
        this.setCountryProperty(tt);
    }

    public void setCountryProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getCreditProperty() {
        return (TextType)this.getProperty(CREDIT);
    }

    public String getCredit() {
        TextType tt = (TextType)this.getProperty(CREDIT);
        return tt == null ? null : tt.getStringValue();
    }

    public void setCredit(String text) {
        TextType tt = (TextType)this.instanciateSimple(CREDIT, text);
        this.setCreditProperty(tt);
    }

    public void setCreditProperty(TextType text) {
        this.addProperty(text);
    }

    public DateType getDateCreatedProperty() {
        return (DateType)this.getProperty(DATE_CREATED);
    }

    public String getDateCreated() {
        TextType tt = (TextType)this.getProperty(DATE_CREATED);
        return tt == null ? null : tt.getStringValue();
    }

    public void setDateCreated(String text) {
        DateType tt = (DateType)this.instanciateSimple(DATE_CREATED, text);
        this.setDateCreatedProperty(tt);
    }

    public void setDateCreatedProperty(DateType text) {
        this.addProperty(text);
    }

    public void addDocumentAncestors(String text) {
        this.addQualifiedBagValue(DOCUMENT_ANCESTORS, text);
    }

    public ArrayProperty getDocumentAncestorsProperty() {
        return (ArrayProperty)this.getProperty(DOCUMENT_ANCESTORS);
    }

    public List<String> getDocumentAncestors() {
        return this.getUnqualifiedBagValueList(DOCUMENT_ANCESTORS);
    }

    public TextType getHeadlineProperty() {
        return (TextType)this.getProperty(HEADLINE);
    }

    public String getHeadline() {
        TextType tt = (TextType)this.getProperty(HEADLINE);
        return tt == null ? null : tt.getStringValue();
    }

    public void setHeadline(String text) {
        TextType tt = (TextType)this.instanciateSimple(HEADLINE, text);
        this.setHeadlineProperty(tt);
    }

    public void setHeadlineProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getHistoryProperty() {
        return (TextType)this.getProperty(HISTORY);
    }

    public String getHistory() {
        TextType tt = (TextType)this.getProperty(HISTORY);
        return tt == null ? null : tt.getStringValue();
    }

    public void setHistory(String text) {
        TextType tt = (TextType)this.instanciateSimple(HISTORY, text);
        this.setHistoryProperty(tt);
    }

    public void setHistoryProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getICCProfileProperty() {
        return (TextType)this.getProperty(ICC_PROFILE);
    }

    public String getICCProfile() {
        TextType tt = (TextType)this.getProperty(ICC_PROFILE);
        return tt == null ? null : tt.getStringValue();
    }

    public void setICCProfile(String text) {
        TextType tt = (TextType)this.instanciateSimple(ICC_PROFILE, text);
        this.setICCProfileProperty(tt);
    }

    public void setICCProfileProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getInstructionsProperty() {
        return (TextType)this.getProperty(INSTRUCTIONS);
    }

    public String getInstructions() {
        TextType tt = (TextType)this.getProperty(INSTRUCTIONS);
        return tt == null ? null : tt.getStringValue();
    }

    public void setInstructions(String text) {
        TextType tt = (TextType)this.instanciateSimple(INSTRUCTIONS, text);
        this.setInstructionsProperty(tt);
    }

    public void setInstructionsProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getSourceProperty() {
        return (TextType)this.getProperty(SOURCE);
    }

    public String getSource() {
        TextType tt = (TextType)this.getProperty(SOURCE);
        return tt == null ? null : tt.getStringValue();
    }

    public void setSource(String text) {
        TextType source = (TextType)this.instanciateSimple(SOURCE, text);
        this.setSourceProperty(source);
    }

    public void setSourceProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getStateProperty() {
        return (TextType)this.getProperty(STATE);
    }

    public String getState() {
        TextType tt = (TextType)this.getProperty(STATE);
        return tt == null ? null : tt.getStringValue();
    }

    public void setState(String text) {
        TextType tt = (TextType)this.instanciateSimple(STATE, text);
        this.setStateProperty(tt);
    }

    public void setStateProperty(TextType text) {
        this.addProperty(text);
    }

    public TextType getSupplementalCategoriesProperty() {
        return (TextType)this.getProperty(SUPPLEMENTAL_CATEGORIES);
    }

    public String getSupplementalCategories() {
        TextType tt = (TextType)this.getProperty(SUPPLEMENTAL_CATEGORIES);
        return tt == null ? null : tt.getStringValue();
    }

    public void setSupplementalCategories(String text) {
        TextType tt = (TextType)this.instanciateSimple(SUPPLEMENTAL_CATEGORIES, text);
        this.setSupplementalCategoriesProperty(tt);
    }

    public void setSupplementalCategoriesProperty(TextType text) {
        this.addProperty(text);
    }

    public void addTextLayers(String layerName, String layerText) {
        if (this.seqLayer == null) {
            this.seqLayer = this.createArrayProperty(TEXT_LAYERS, Cardinality.Seq);
            this.addProperty(this.seqLayer);
        }
        LayerType layer = new LayerType(this.getMetadata());
        layer.setLayerName(layerName);
        layer.setLayerText(layerText);
        this.seqLayer.getContainer().addProperty(layer);
    }

    public List<LayerType> getTextLayers() throws BadFieldValueException {
        List<AbstractField> tmp = this.getUnqualifiedArrayList(TEXT_LAYERS);
        if (tmp != null) {
            ArrayList<LayerType> layers = new ArrayList<LayerType>();
            for (AbstractField abstractField : tmp) {
                if (abstractField instanceof LayerType) {
                    layers.add((LayerType)abstractField);
                    continue;
                }
                throw new BadFieldValueException("Layer expected and " + abstractField.getClass().getName() + " found.");
            }
            return layers;
        }
        return null;
    }

    public TextType getTransmissionReferenceProperty() {
        return (TextType)this.getProperty(TRANSMISSION_REFERENCE);
    }

    public String getTransmissionReference() {
        TextType tt = (TextType)this.getProperty(TRANSMISSION_REFERENCE);
        return tt == null ? null : tt.getStringValue();
    }

    public void setTransmissionReference(String text) {
        TextType tt = (TextType)this.instanciateSimple(TRANSMISSION_REFERENCE, text);
        this.setTransmissionReferenceProperty(tt);
    }

    public void setTransmissionReferenceProperty(TextType text) {
        this.addProperty(text);
    }

    public IntegerType getUrgencyProperty() {
        return (IntegerType)this.getProperty(URGENCY);
    }

    public Integer getUrgency() {
        IntegerType tt = (IntegerType)this.getProperty(URGENCY);
        return tt == null ? null : tt.getValue();
    }

    public void setUrgency(String s) {
        IntegerType tt = (IntegerType)this.instanciateSimple(URGENCY, s);
        this.setUrgencyProperty(tt);
    }

    public void setUrgency(Integer s) {
        IntegerType tt = (IntegerType)this.instanciateSimple(URGENCY, s);
        this.setUrgencyProperty(tt);
    }

    public void setUrgencyProperty(IntegerType text) {
        this.addProperty(text);
    }
}

