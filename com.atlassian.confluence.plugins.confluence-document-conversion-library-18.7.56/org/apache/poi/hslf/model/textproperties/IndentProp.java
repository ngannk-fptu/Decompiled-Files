/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.GenericRecordUtil;

public class IndentProp
implements GenericRecord {
    private int charactersCovered;
    private short indentLevel;

    public IndentProp(int charactersCovered, short indentLevel) {
        this.charactersCovered = charactersCovered;
        this.indentLevel = indentLevel;
    }

    public int getCharactersCovered() {
        return this.charactersCovered;
    }

    public int getIndentLevel() {
        return this.indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        if (indentLevel >= 5 || indentLevel < 0) {
            throw new IllegalArgumentException("Indent must be between 0 and 4");
        }
        this.indentLevel = (short)indentLevel;
    }

    public void updateTextSize(int textSize) {
        this.charactersCovered = textSize;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("charactersCovered", this::getCharactersCovered, "indentLevel", this::getIndentLevel);
    }
}

