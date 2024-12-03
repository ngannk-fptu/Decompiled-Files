/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class LabelTag
extends AbstractHtmlElementTag {
    private static final String LABEL_TAG = "label";
    private static final String FOR_ATTRIBUTE = "for";
    @Nullable
    private TagWriter tagWriter;
    @Nullable
    private String forId;

    public void setFor(String forId) {
        this.forId = forId;
    }

    @Nullable
    protected String getFor() {
        return this.forId;
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag(LABEL_TAG);
        tagWriter.writeAttribute(FOR_ATTRIBUTE, this.resolveFor());
        this.writeDefaultAttributes(tagWriter);
        tagWriter.forceBlock();
        this.tagWriter = tagWriter;
        return 1;
    }

    @Override
    @Nullable
    protected String getName() throws JspException {
        return null;
    }

    protected String resolveFor() throws JspException {
        if (StringUtils.hasText((String)this.forId)) {
            return this.getDisplayString(this.evaluate(FOR_ATTRIBUTE, this.forId));
        }
        return this.autogenerateFor();
    }

    protected String autogenerateFor() throws JspException {
        return StringUtils.deleteAny((String)this.getPropertyPath(), (String)"[]");
    }

    public int doEndTag() throws JspException {
        Assert.state((this.tagWriter != null ? 1 : 0) != 0, (String)"No TagWriter set");
        this.tagWriter.endTag();
        return 6;
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.tagWriter = null;
    }
}

