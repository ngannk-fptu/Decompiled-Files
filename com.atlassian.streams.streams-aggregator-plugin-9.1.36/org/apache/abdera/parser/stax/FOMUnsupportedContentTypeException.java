/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.parser.stax.FOMException;

public class FOMUnsupportedContentTypeException
extends FOMException {
    private static final long serialVersionUID = 4156893310308105899L;

    public FOMUnsupportedContentTypeException(String message) {
        super(Localizer.sprintf("UNSUPPORTED.CONTENT.TYPE", message));
    }
}

