/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.util.List;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.AbstractFormField;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public class TextAreaFormField
extends AbstractFormField {
    private static final String FIELD_TYPE = "TextArea";
    private static final int DEFAULT_ROWS = 7;
    private static final int DEFAULT_COLS = 25;
    private List _lines;

    public TextAreaFormField(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
    }

    @Override
    protected String getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
    }

    @Override
    public int getBaseline() {
        return 0;
    }

    @Override
    public boolean hasBaseline() {
        return false;
    }
}

