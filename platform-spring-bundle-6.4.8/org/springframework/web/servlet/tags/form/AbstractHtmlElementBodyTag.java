/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTag
 */
package org.springframework.web.servlet.tags.form;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractHtmlElementBodyTag
extends AbstractHtmlElementTag
implements BodyTag {
    @Nullable
    private BodyContent bodyContent;
    @Nullable
    private TagWriter tagWriter;

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        this.onWriteTagContent();
        this.tagWriter = tagWriter;
        if (this.shouldRender()) {
            this.exposeAttributes();
            return 2;
        }
        return 0;
    }

    public int doEndTag() throws JspException {
        if (this.shouldRender()) {
            Assert.state(this.tagWriter != null, "No TagWriter set");
            if (this.bodyContent != null && StringUtils.hasText(this.bodyContent.getString())) {
                this.renderFromBodyContent(this.bodyContent, this.tagWriter);
            } else {
                this.renderDefaultContent(this.tagWriter);
            }
        }
        return 6;
    }

    protected void renderFromBodyContent(BodyContent bodyContent, TagWriter tagWriter) throws JspException {
        this.flushBufferedBodyContent(bodyContent);
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.removeAttributes();
        this.tagWriter = null;
        this.bodyContent = null;
    }

    protected void onWriteTagContent() {
    }

    protected boolean shouldRender() throws JspException {
        return true;
    }

    protected void exposeAttributes() throws JspException {
    }

    protected void removeAttributes() {
    }

    protected void flushBufferedBodyContent(BodyContent bodyContent) throws JspException {
        try {
            bodyContent.writeOut((Writer)bodyContent.getEnclosingWriter());
        }
        catch (IOException ex) {
            throw new JspException("Unable to write buffered body content.", (Throwable)ex);
        }
    }

    protected abstract void renderDefaultContent(TagWriter var1) throws JspException;

    public void doInitBody() throws JspException {
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }
}

