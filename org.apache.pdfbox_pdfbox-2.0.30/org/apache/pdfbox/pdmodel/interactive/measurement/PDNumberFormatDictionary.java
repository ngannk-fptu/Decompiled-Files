/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDNumberFormatDictionary
implements COSObjectable {
    public static final String TYPE = "NumberFormat";
    public static final String LABEL_SUFFIX_TO_VALUE = "S";
    public static final String LABEL_PREFIX_TO_VALUE = "P";
    public static final String FRACTIONAL_DISPLAY_DECIMAL = "D";
    public static final String FRACTIONAL_DISPLAY_FRACTION = "F";
    public static final String FRACTIONAL_DISPLAY_ROUND = "R";
    public static final String FRACTIONAL_DISPLAY_TRUNCATE = "T";
    private COSDictionary numberFormatDictionary;

    public PDNumberFormatDictionary() {
        this.numberFormatDictionary = new COSDictionary();
        this.numberFormatDictionary.setName(COSName.TYPE, TYPE);
    }

    public PDNumberFormatDictionary(COSDictionary dictionary) {
        this.numberFormatDictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.numberFormatDictionary;
    }

    public String getType() {
        return TYPE;
    }

    public String getUnits() {
        return this.getCOSObject().getString("U");
    }

    public void setUnits(String units) {
        this.getCOSObject().setString("U", units);
    }

    public float getConversionFactor() {
        return this.getCOSObject().getFloat("C");
    }

    public void setConversionFactor(float conversionFactor) {
        this.getCOSObject().setFloat("C", conversionFactor);
    }

    public String getFractionalDisplay() {
        return this.getCOSObject().getString(FRACTIONAL_DISPLAY_FRACTION, FRACTIONAL_DISPLAY_DECIMAL);
    }

    public void setFractionalDisplay(String fractionalDisplay) {
        if (!(fractionalDisplay == null || FRACTIONAL_DISPLAY_DECIMAL.equals(fractionalDisplay) || FRACTIONAL_DISPLAY_FRACTION.equals(fractionalDisplay) || FRACTIONAL_DISPLAY_ROUND.equals(fractionalDisplay) || FRACTIONAL_DISPLAY_TRUNCATE.equals(fractionalDisplay))) {
            throw new IllegalArgumentException("Value must be \"D\", \"F\", \"R\", or \"T\", (or null).");
        }
        this.getCOSObject().setString(FRACTIONAL_DISPLAY_FRACTION, fractionalDisplay);
    }

    public int getDenominator() {
        return this.getCOSObject().getInt(FRACTIONAL_DISPLAY_DECIMAL);
    }

    public void setDenominator(int denominator) {
        this.getCOSObject().setInt(FRACTIONAL_DISPLAY_DECIMAL, denominator);
    }

    public boolean isFD() {
        return this.getCOSObject().getBoolean("FD", false);
    }

    public void setFD(boolean fd) {
        this.getCOSObject().setBoolean("FD", fd);
    }

    public String getThousandsSeparator() {
        return this.getCOSObject().getString("RT", ",");
    }

    public void setThousandsSeparator(String thousandsSeparator) {
        this.getCOSObject().setString("RT", thousandsSeparator);
    }

    public String getDecimalSeparator() {
        return this.getCOSObject().getString("RD", ".");
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.getCOSObject().setString("RD", decimalSeparator);
    }

    public String getLabelPrefixString() {
        return this.getCOSObject().getString("PS", " ");
    }

    public void setLabelPrefixString(String labelPrefixString) {
        this.getCOSObject().setString("PS", labelPrefixString);
    }

    public String getLabelSuffixString() {
        return this.getCOSObject().getString("SS", " ");
    }

    public void setLabelSuffixString(String labelSuffixString) {
        this.getCOSObject().setString("SS", labelSuffixString);
    }

    public String getLabelPositionToValue() {
        return this.getCOSObject().getString("O", LABEL_SUFFIX_TO_VALUE);
    }

    public void setLabelPositionToValue(String labelPositionToValue) {
        if (labelPositionToValue != null && !LABEL_PREFIX_TO_VALUE.equals(labelPositionToValue) && !LABEL_SUFFIX_TO_VALUE.equals(labelPositionToValue)) {
            throw new IllegalArgumentException("Value must be \"S\", or \"P\" (or null).");
        }
        this.getCOSObject().setString("O", labelPositionToValue);
    }
}

