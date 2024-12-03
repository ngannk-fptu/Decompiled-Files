/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.Tag
 */
package com.octo.captcha.module.taglib;

import com.octo.captcha.module.taglib.BaseCaptchaTag;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public abstract class QuestionTag
extends BaseCaptchaTag
implements Tag {
    @Override
    public int doEndTag() throws JspException {
        String question = this.getService().getQuestionForID(this.pageContext.getSession().getId(), this.pageContext.getRequest().getLocale());
        try {
            this.pageContext.getOut().write(question);
        }
        catch (IOException e) {
            throw new JspException((Throwable)e);
        }
        return 6;
    }
}

