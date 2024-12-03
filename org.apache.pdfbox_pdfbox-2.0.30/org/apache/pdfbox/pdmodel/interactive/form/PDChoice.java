/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.interactive.form.FieldUtils;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

public abstract class PDChoice
extends PDVariableText {
    static final int FLAG_COMBO = 131072;
    private static final int FLAG_SORT = 524288;
    private static final int FLAG_MULTI_SELECT = 0x200000;
    private static final int FLAG_DO_NOT_SPELL_CHECK = 0x400000;
    private static final int FLAG_COMMIT_ON_SEL_CHANGE = 0x4000000;

    public PDChoice(PDAcroForm acroForm) {
        super(acroForm);
        this.getCOSObject().setItem(COSName.FT, (COSBase)COSName.CH);
    }

    PDChoice(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public List<String> getOptions() {
        COSBase values = this.getCOSObject().getDictionaryObject(COSName.OPT);
        return FieldUtils.getPairableItems(values, 0);
    }

    public void setOptions(List<String> displayValues) {
        if (displayValues != null && !displayValues.isEmpty()) {
            if (this.isSort()) {
                Collections.sort(displayValues);
            }
            this.getCOSObject().setItem(COSName.OPT, (COSBase)COSArrayList.convertStringListToCOSStringCOSArray(displayValues));
        } else {
            this.getCOSObject().removeItem(COSName.OPT);
        }
    }

    public void setOptions(List<String> exportValues, List<String> displayValues) {
        if (exportValues != null && displayValues != null && !exportValues.isEmpty() && !displayValues.isEmpty()) {
            if (exportValues.size() != displayValues.size()) {
                throw new IllegalArgumentException("The number of entries for exportValue and displayValue shall be the same.");
            }
            List<FieldUtils.KeyValue> keyValuePairs = FieldUtils.toKeyValueList(exportValues, displayValues);
            if (this.isSort()) {
                FieldUtils.sortByValue(keyValuePairs);
            }
            COSArray options = new COSArray();
            for (int i = 0; i < exportValues.size(); ++i) {
                COSArray entry = new COSArray();
                FieldUtils.KeyValue pair = keyValuePairs.get(i);
                entry.add(new COSString(pair.getKey()));
                entry.add(new COSString(pair.getValue()));
                options.add(entry);
            }
            this.getCOSObject().setItem(COSName.OPT, (COSBase)options);
        } else {
            this.getCOSObject().removeItem(COSName.OPT);
        }
    }

    public List<String> getOptionsDisplayValues() {
        COSBase values = this.getCOSObject().getDictionaryObject(COSName.OPT);
        return FieldUtils.getPairableItems(values, 1);
    }

    public List<String> getOptionsExportValues() {
        return this.getOptions();
    }

    public List<Integer> getSelectedOptionsIndex() {
        COSBase value = this.getCOSObject().getDictionaryObject(COSName.I);
        if (value != null) {
            return COSArrayList.convertIntegerCOSArrayToList((COSArray)value);
        }
        return Collections.emptyList();
    }

    public void setSelectedOptionsIndex(List<Integer> values) {
        if (values != null && !values.isEmpty()) {
            if (!this.isMultiSelect()) {
                throw new IllegalArgumentException("Setting the indices is not allowed for choice fields not allowing multiple selections.");
            }
            this.getCOSObject().setItem(COSName.I, (COSBase)COSArrayList.converterToCOSArray(values));
        } else {
            this.getCOSObject().removeItem(COSName.I);
        }
    }

    public boolean isSort() {
        return this.getCOSObject().getFlag(COSName.FF, 524288);
    }

    public void setSort(boolean sort) {
        this.getCOSObject().setFlag(COSName.FF, 524288, sort);
    }

    public boolean isMultiSelect() {
        return this.getCOSObject().getFlag(COSName.FF, 0x200000);
    }

    public void setMultiSelect(boolean multiSelect) {
        this.getCOSObject().setFlag(COSName.FF, 0x200000, multiSelect);
    }

    public boolean isDoNotSpellCheck() {
        return this.getCOSObject().getFlag(COSName.FF, 0x400000);
    }

    public void setDoNotSpellCheck(boolean doNotSpellCheck) {
        this.getCOSObject().setFlag(COSName.FF, 0x400000, doNotSpellCheck);
    }

    public boolean isCommitOnSelChange() {
        return this.getCOSObject().getFlag(COSName.FF, 0x4000000);
    }

    public void setCommitOnSelChange(boolean commitOnSelChange) {
        this.getCOSObject().setFlag(COSName.FF, 0x4000000, commitOnSelChange);
    }

    public boolean isCombo() {
        return this.getCOSObject().getFlag(COSName.FF, 131072);
    }

    public void setCombo(boolean combo) {
        this.getCOSObject().setFlag(COSName.FF, 131072, combo);
    }

    @Override
    public void setValue(String value) throws IOException {
        this.getCOSObject().setString(COSName.V, value);
        this.setSelectedOptionsIndex(null);
        this.applyChange();
    }

    public void setDefaultValue(String value) throws IOException {
        this.getCOSObject().setString(COSName.DV, value);
    }

    public void setValue(List<String> values) throws IOException {
        if (values != null && !values.isEmpty()) {
            if (!this.isMultiSelect()) {
                throw new IllegalArgumentException("The list box does not allow multiple selections.");
            }
            List<String> options = this.getOptions();
            if (!options.containsAll(values)) {
                throw new IllegalArgumentException("The values are not contained in the selectable options.");
            }
            this.getCOSObject().setItem(COSName.V, (COSBase)COSArrayList.convertStringListToCOSStringCOSArray(values));
            this.updateSelectedOptionsIndex(values, options);
        } else {
            this.getCOSObject().removeItem(COSName.V);
            this.getCOSObject().removeItem(COSName.I);
        }
        this.applyChange();
    }

    public List<String> getValue() {
        return this.getValueFor(COSName.V);
    }

    public List<String> getDefaultValue() {
        return this.getValueFor(COSName.DV);
    }

    private List<String> getValueFor(COSName name) {
        COSBase value = this.getCOSObject().getDictionaryObject(name);
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

    @Override
    public String getValueAsString() {
        return Arrays.toString(this.getValue().toArray());
    }

    private void updateSelectedOptionsIndex(List<String> values, List<String> options) {
        ArrayList<Integer> indices = new ArrayList<Integer>(values.size());
        for (String value : values) {
            indices.add(options.indexOf(value));
        }
        Collections.sort(indices);
        this.setSelectedOptionsIndex(indices);
    }

    @Override
    abstract void constructAppearances() throws IOException;
}

