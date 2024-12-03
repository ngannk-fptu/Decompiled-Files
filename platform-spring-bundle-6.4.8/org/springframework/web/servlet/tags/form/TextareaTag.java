/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class TextareaTag
extends AbstractHtmlInputElementTag {
    public static final String ROWS_ATTRIBUTE = "rows";
    public static final String COLS_ATTRIBUTE = "cols";
    public static final String ONSELECT_ATTRIBUTE = "onselect";
    @Nullable
    private String rows;
    @Nullable
    private String cols;
    @Nullable
    private String onselect;

    public void setRows(String rows) {
        this.rows = rows;
    }

    @Nullable
    protected String getRows() {
        return this.rows;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    @Nullable
    protected String getCols() {
        return this.cols;
    }

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @Nullable
    protected String getOnselect() {
        return this.onselect;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag("textarea");
        this.writeDefaultAttributes(tagWriter);
        this.writeOptionalAttribute(tagWriter, ROWS_ATTRIBUTE, this.getRows());
        this.writeOptionalAttribute(tagWriter, COLS_ATTRIBUTE, this.getCols());
        this.writeOptionalAttribute(tagWriter, ONSELECT_ATTRIBUTE, this.getOnselect());
        String value = this.getDisplayString(this.getBoundValue(), this.getPropertyEditor());
        tagWriter.appendValue("\r\n" + this.processFieldValue(this.getName(), value, "textarea"));
        tagWriter.endTag();
        return 0;
    }
}

