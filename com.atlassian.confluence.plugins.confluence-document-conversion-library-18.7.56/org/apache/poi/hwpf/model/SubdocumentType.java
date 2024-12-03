/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.util.Internal;

@Internal
public enum SubdocumentType {
    MAIN,
    FOOTNOTE,
    HEADER,
    MACRO,
    ANNOTATION,
    ENDNOTE,
    TEXTBOX,
    HEADER_TEXTBOX;

    public static final List<SubdocumentType> ORDERED;

    static {
        ORDERED = Collections.unmodifiableList(Arrays.asList(MAIN, FOOTNOTE, HEADER, MACRO, ANNOTATION, ENDNOTE, TEXTBOX, HEADER_TEXTBOX));
    }
}

