/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyTag
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.tags.form;

import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementBodyTag;
import org.springframework.web.servlet.tags.form.FormTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public class ErrorsTag
extends AbstractHtmlElementBodyTag
implements BodyTag {
    public static final String MESSAGES_ATTRIBUTE = "messages";
    public static final String SPAN_TAG = "span";
    private String element = "span";
    private String delimiter = "<br/>";
    @Nullable
    private Object oldMessages;
    private boolean errorMessagesWereExposed;

    public void setElement(String element) {
        Assert.hasText((String)element, (String)"'element' cannot be null or blank");
        this.element = element;
    }

    public String getElement() {
        return this.element;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    @Override
    protected String autogenerateId() throws JspException {
        String path = this.getPropertyPath();
        if (!StringUtils.hasLength((String)path) || "*".equals(path)) {
            path = (String)this.pageContext.getAttribute(FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME, 2);
        }
        return StringUtils.deleteAny((String)path, (String)"[]") + ".errors";
    }

    @Override
    @Nullable
    protected String getName() throws JspException {
        return null;
    }

    @Override
    protected boolean shouldRender() throws JspException {
        try {
            return this.getBindStatus().isError();
        }
        catch (IllegalStateException ex) {
            return false;
        }
    }

    @Override
    protected void renderDefaultContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag(this.getElement());
        this.writeDefaultAttributes(tagWriter);
        String delimiter = ObjectUtils.getDisplayString((Object)this.evaluate("delimiter", this.getDelimiter()));
        String[] errorMessages = this.getBindStatus().getErrorMessages();
        for (int i2 = 0; i2 < errorMessages.length; ++i2) {
            String errorMessage = errorMessages[i2];
            if (i2 > 0) {
                tagWriter.appendValue(delimiter);
            }
            tagWriter.appendValue(this.getDisplayString(errorMessage));
        }
        tagWriter.endTag();
    }

    @Override
    protected void exposeAttributes() throws JspException {
        ArrayList<String> errorMessages = new ArrayList<String>(Arrays.asList(this.getBindStatus().getErrorMessages()));
        this.oldMessages = this.pageContext.getAttribute(MESSAGES_ATTRIBUTE, 1);
        this.pageContext.setAttribute(MESSAGES_ATTRIBUTE, errorMessages, 1);
        this.errorMessagesWereExposed = true;
    }

    @Override
    protected void removeAttributes() {
        if (this.errorMessagesWereExposed) {
            if (this.oldMessages != null) {
                this.pageContext.setAttribute(MESSAGES_ATTRIBUTE, this.oldMessages, 1);
                this.oldMessages = null;
            } else {
                this.pageContext.removeAttribute(MESSAGES_ATTRIBUTE, 1);
            }
        }
    }
}

