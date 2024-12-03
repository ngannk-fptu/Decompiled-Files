/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.util.Internal;

@Internal
public enum FSPADocumentPart {
    HEADER(41),
    MAIN(40);

    private final int fibFieldsField;

    private FSPADocumentPart(int fibHandlerField) {
        this.fibFieldsField = fibHandlerField;
    }

    public int getFibFieldsField() {
        return this.fibFieldsField;
    }
}

