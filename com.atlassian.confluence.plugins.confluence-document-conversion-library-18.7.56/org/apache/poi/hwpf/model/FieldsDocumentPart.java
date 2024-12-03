/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.util.Internal;

@Internal
public enum FieldsDocumentPart {
    ANNOTATIONS(19),
    ENDNOTES(48),
    FOOTNOTES(18),
    HEADER(17),
    HEADER_TEXTBOX(59),
    MAIN(16),
    TEXTBOX(57);

    private final int fibFieldsField;

    private FieldsDocumentPart(int fibHandlerField) {
        this.fibFieldsField = fibHandlerField;
    }

    public int getFibFieldsField() {
        return this.fibFieldsField;
    }
}

