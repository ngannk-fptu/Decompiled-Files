/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.Collection;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.usermodel.Field;

public interface Fields {
    public Field getFieldByStartOffset(FieldsDocumentPart var1, int var2);

    public Collection<Field> getFields(FieldsDocumentPart var1);
}

