/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;

public abstract class PDButton
extends PDTerminalField {
    static final int FLAG_RADIO = 32768;
    static final int FLAG_PUSHBUTTON = 65536;
    static final int FLAG_RADIOS_IN_UNISON = 0x2000000;

    public PDButton(PDAcroForm acroForm) {
        super(acroForm);
        this.getCOSObject().setItem(COSName.FT, (COSBase)COSName.BTN);
    }

    PDButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isPushButton() {
        return this.getCOSObject().getFlag(COSName.FF, 65536);
    }

    @Deprecated
    public void setPushButton(boolean pushbutton) {
        this.getCOSObject().setFlag(COSName.FF, 65536, pushbutton);
        if (pushbutton) {
            this.setRadioButton(false);
        }
    }

    public boolean isRadioButton() {
        return this.getCOSObject().getFlag(COSName.FF, 32768);
    }

    @Deprecated
    public void setRadioButton(boolean radiobutton) {
        this.getCOSObject().setFlag(COSName.FF, 32768, radiobutton);
        if (radiobutton) {
            this.setPushButton(false);
        }
    }

    public String getValue() {
        COSBase value = this.getInheritableAttribute(COSName.V);
        if (value instanceof COSName) {
            String stringValue = ((COSName)value).getName();
            List<String> exportValues = this.getExportValues();
            if (!exportValues.isEmpty()) {
                try {
                    int idx = Integer.parseInt(stringValue, 10);
                    if (idx >= 0 && idx < exportValues.size()) {
                        return exportValues.get(idx);
                    }
                }
                catch (NumberFormatException nfe) {
                    return stringValue;
                }
            }
            return stringValue;
        }
        return "Off";
    }

    @Override
    public void setValue(String value) throws IOException {
        boolean hasExportValues;
        this.checkValue(value);
        boolean bl = hasExportValues = this.getExportValues().size() > 0;
        if (hasExportValues) {
            this.updateByOption(value);
        } else {
            this.updateByValue(value);
        }
        this.applyChange();
    }

    public void setValue(int index) throws IOException {
        if (this.getExportValues().isEmpty() || index < 0 || index >= this.getExportValues().size()) {
            throw new IllegalArgumentException("index '" + index + "' is not a valid index for the field " + this.getFullyQualifiedName() + ", valid indices are from 0 to " + (this.getExportValues().size() - 1));
        }
        this.updateByValue(String.valueOf(index));
        this.applyChange();
    }

    public String getDefaultValue() {
        COSBase value = this.getInheritableAttribute(COSName.DV);
        if (value instanceof COSName) {
            return ((COSName)value).getName();
        }
        return "";
    }

    public void setDefaultValue(String value) {
        this.checkValue(value);
        this.getCOSObject().setName(COSName.DV, value);
    }

    @Override
    public String getValueAsString() {
        return this.getValue();
    }

    public List<String> getExportValues() {
        COSBase value = this.getInheritableAttribute(COSName.OPT);
        if (value instanceof COSString) {
            ArrayList<String> array = new ArrayList<String>();
            array.add(((COSString)value).getString());
            return array;
        }
        if (value instanceof COSArray) {
            return COSArrayList.convertCOSStringCOSArrayToList((COSArray)value);
        }
        return Collections.emptyList();
    }

    public void setExportValues(List<String> values) {
        if (values != null && !values.isEmpty()) {
            COSArray cosValues = COSArrayList.convertStringListToCOSStringCOSArray(values);
            this.getCOSObject().setItem(COSName.OPT, (COSBase)cosValues);
        } else {
            this.getCOSObject().removeItem(COSName.OPT);
        }
    }

    @Override
    void constructAppearances() throws IOException {
        List<String> exportValues = this.getExportValues();
        if (exportValues.size() > 0) {
            try {
                int optionsIndex = Integer.parseInt(this.getValue());
                if (optionsIndex < exportValues.size()) {
                    this.updateByOption(exportValues.get(optionsIndex));
                }
            }
            catch (NumberFormatException numberFormatException) {}
        } else {
            this.updateByValue(this.getValue());
        }
    }

    public Set<String> getOnValues() {
        LinkedHashSet<String> onValues = new LinkedHashSet<String>();
        if (this.getExportValues().size() > 0) {
            onValues.addAll(this.getExportValues());
            return onValues;
        }
        List<PDAnnotationWidget> widgets = this.getWidgets();
        for (PDAnnotationWidget widget : widgets) {
            onValues.add(this.getOnValueForWidget(widget));
        }
        return onValues;
    }

    private String getOnValue(int index) {
        List<PDAnnotationWidget> widgets = this.getWidgets();
        if (index < widgets.size()) {
            return this.getOnValueForWidget(widgets.get(index));
        }
        return "";
    }

    private String getOnValueForWidget(PDAnnotationWidget widget) {
        PDAppearanceEntry normalAppearance;
        PDAppearanceDictionary apDictionary = widget.getAppearance();
        if (apDictionary != null && (normalAppearance = apDictionary.getNormalAppearance()) != null) {
            Set<COSName> entries = normalAppearance.getSubDictionary().keySet();
            for (COSName entry : entries) {
                if (COSName.Off.compareTo(entry) == 0) continue;
                return entry.getName();
            }
        }
        return "";
    }

    void checkValue(String value) {
        Set<String> onValues = this.getOnValues();
        if (COSName.Off.getName().compareTo(value) != 0 && !onValues.contains(value)) {
            throw new IllegalArgumentException("value '" + value + "' is not a valid option for the field " + this.getFullyQualifiedName() + ", valid values are: " + onValues + " and " + COSName.Off.getName());
        }
    }

    private void updateByValue(String value) throws IOException {
        this.getCOSObject().setName(COSName.V, value);
        for (PDAnnotationWidget widget : this.getWidgets()) {
            if (widget.getAppearance() == null) continue;
            PDAppearanceEntry appearanceEntry = widget.getAppearance().getNormalAppearance();
            if (((COSDictionary)appearanceEntry.getCOSObject()).containsKey(value)) {
                widget.setAppearanceState(value);
                continue;
            }
            widget.setAppearanceState(COSName.Off.getName());
        }
    }

    private void updateByOption(String value) throws IOException {
        List<PDAnnotationWidget> widgets = this.getWidgets();
        List<String> options = this.getExportValues();
        if (widgets.size() != options.size()) {
            throw new IllegalArgumentException("The number of options doesn't match the number of widgets");
        }
        if (value.equals(COSName.Off.getName())) {
            this.updateByValue(value);
        } else {
            int optionsIndex = options.indexOf(value);
            if (optionsIndex != -1) {
                this.updateByValue(this.getOnValue(optionsIndex));
            }
        }
    }
}

