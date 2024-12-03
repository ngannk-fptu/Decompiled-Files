/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.TagSupport;

public class BodyTagSupport
extends TagSupport
implements BodyTag {
    private static final long serialVersionUID = -7235752615580319833L;
    protected transient BodyContent bodyContent;

    @Override
    public int doStartTag() throws JspException {
        return 2;
    }

    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }

    @Override
    public void setBodyContent(BodyContent b) {
        this.bodyContent = b;
    }

    @Override
    public void doInitBody() throws JspException {
    }

    @Override
    public int doAfterBody() throws JspException {
        return 0;
    }

    @Override
    public void release() {
        this.bodyContent = null;
        super.release();
    }

    public BodyContent getBodyContent() {
        return this.bodyContent;
    }

    public JspWriter getPreviousOut() {
        return this.bodyContent.getEnclosingWriter();
    }
}

