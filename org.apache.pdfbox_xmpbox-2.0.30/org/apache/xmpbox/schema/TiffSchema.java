/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ProperNameType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="tiff", namespace="http://ns.adobe.com/tiff/1.0/")
public class TiffSchema
extends XMPSchema {
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String IMAGE_DESCRIPTION = "ImageDescription";
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String COPYRIGHT = "Copyright";
    @PropertyType(type=Types.ProperName, card=Cardinality.Simple)
    public static final String ARTIST = "Artist";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String IMAGE_WIDTH = "ImageWidth";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String IMAGE_LENGHT = "ImageLength";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String BITS_PER_SAMPLE = "BitsPerSample";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String COMPRESSION = "Compression";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String ORIENTATION = "Orientation";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SAMPLES_PER_PIXEL = "SamplesPerPixel";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String PLANAR_CONFIGURATION = "PlanarConfiguration";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String YCB_CR_SUB_SAMPLING = "YCbCrSubSampling";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String YCB_CR_POSITIONING = "YCbCrPositioning";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String XRESOLUTION = "XResolution";
    @PropertyType(type=Types.Rational, card=Cardinality.Simple)
    public static final String YRESOLUTION = "YResolution";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String RESOLUTION_UNIT = "ResolutionUnit";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String TRANSFER_FUNCTION = "TransferFunction";
    @PropertyType(type=Types.Rational, card=Cardinality.Seq)
    public static final String WHITE_POINT = "WhitePoint";
    @PropertyType(type=Types.Rational, card=Cardinality.Seq)
    public static final String PRIMARY_CHROMATICITIES = "PrimaryChromaticities";
    @PropertyType(type=Types.Rational, card=Cardinality.Seq)
    public static final String YCB_CR_COEFFICIENTS = "YCbCrCoefficients";
    @PropertyType(type=Types.Rational, card=Cardinality.Seq)
    public static final String REFERENCE_BLACK_WHITE = "ReferenceBlackWhite";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String DATE_TIME = "DateTime";
    @PropertyType(type=Types.AgentName, card=Cardinality.Simple)
    public static final String SOFTWARE = "Software";
    @PropertyType(type=Types.ProperName, card=Cardinality.Simple)
    public static final String MAKE = "Make";
    @PropertyType(type=Types.ProperName, card=Cardinality.Simple)
    public static final String MODEL = "Model";

    public TiffSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public TiffSchema(XMPMetadata metadata, String prefix) {
        super(metadata, prefix);
    }

    public ProperNameType getArtistProperty() {
        return (ProperNameType)this.getProperty(ARTIST);
    }

    public String getArtist() {
        ProperNameType tt = (ProperNameType)this.getProperty(ARTIST);
        return tt == null ? null : tt.getStringValue();
    }

    public void setArtist(String text) {
        this.addProperty(this.createTextType(ARTIST, text));
    }

    public ArrayProperty getImageDescriptionProperty() {
        return (ArrayProperty)this.getProperty(IMAGE_DESCRIPTION);
    }

    public List<String> getImageDescriptionLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(IMAGE_DESCRIPTION);
    }

    public String getImageDescription(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(IMAGE_DESCRIPTION, lang);
    }

    public String getImageDescription() {
        return this.getImageDescription(null);
    }

    public void addImageDescription(String lang, String value) {
        this.setUnqualifiedLanguagePropertyValue(IMAGE_DESCRIPTION, lang, value);
    }

    public ArrayProperty getCopyRightProperty() {
        return (ArrayProperty)this.getProperty(COPYRIGHT);
    }

    public List<String> getCopyRightLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(COPYRIGHT);
    }

    public String getCopyRight(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(COPYRIGHT, lang);
    }

    public String getCopyRight() {
        return this.getCopyRight(null);
    }

    public void addCopyright(String lang, String value) {
        this.setUnqualifiedLanguagePropertyValue(COPYRIGHT, lang, value);
    }
}

