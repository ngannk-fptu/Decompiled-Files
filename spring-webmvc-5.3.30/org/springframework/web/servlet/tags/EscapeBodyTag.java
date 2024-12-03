/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTag
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.util.JavaScriptUtils
 */
package org.springframework.web.servlet.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.JavaScriptUtils;

public class EscapeBodyTag
extends HtmlEscapingAwareTag
implements BodyTag {
    private boolean javaScriptEscape = false;
    @Nullable
    private BodyContent bodyContent;

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override
    protected int doStartTagInternal() {
        return 2;
    }

    public void doInitBody() {
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }

    public int doAfterBody() throws JspException {
        try {
            String content = this.readBodyContent();
            content = this.htmlEscape(content);
            content = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape((String)content) : content;
            this.writeBodyContent(content);
        }
        catch (IOException ex) {
            throw new JspException("Could not write escaped body", (Throwable)ex);
        }
        return 0;
    }

    protected String readBodyContent() throws IOException {
        Assert.state((this.bodyContent != null ? 1 : 0) != 0, (String)"No BodyContent set");
        return this.bodyContent.getString();
    }

    protected void writeBodyContent(String content) throws IOException {
        Assert.state((this.bodyContent != null ? 1 : 0) != 0, (String)"No BodyContent set");
        this.bodyContent.getEnclosingWriter().print(content);
    }
}

