/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.CaptchaService
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.Tag
 */
package com.octo.captcha.module.taglib;

import com.octo.captcha.module.config.CaptchaModuleConfig;
import com.octo.captcha.module.taglib.BaseCaptchaTag;
import com.octo.captcha.service.CaptchaService;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public class MessageTag
extends BaseCaptchaTag
implements Tag {
    private String messageKey = CaptchaModuleConfig.getInstance().getMessageKey();

    @Override
    public int doEndTag() throws JspException {
        String message = (String)this.pageContext.getRequest().getAttribute(this.messageKey);
        if (message != null) {
            try {
                this.pageContext.getOut().write(message);
            }
            catch (IOException e) {
                throw new JspException((Throwable)e);
            }
        }
        return 6;
    }

    @Override
    protected CaptchaService getService() {
        return null;
    }
}

