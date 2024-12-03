/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.parser.stax.FOMException;

public class FOMUnsupportedTextTypeException
extends FOMException {
    private static final long serialVersionUID = 4156893310308105899L;

    public FOMUnsupportedTextTypeException(String message) {
        super(Localizer.sprintf("UNSUPPORTED.TEXT.TYPE", message));
    }
}

