/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.InternalResourceView;

public class JstlView
extends InternalResourceView {
    @Nullable
    private MessageSource messageSource;

    public JstlView() {
    }

    public JstlView(String url) {
        super(url);
    }

    public JstlView(String url, MessageSource messageSource) {
        this(url);
        this.messageSource = messageSource;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {
        if (this.messageSource != null) {
            this.messageSource = JstlUtils.getJstlAwareMessageSource(servletContext, this.messageSource);
        }
        super.initServletContext(servletContext);
    }

    @Override
    protected void exposeHelpers(HttpServletRequest request) throws Exception {
        if (this.messageSource != null) {
            JstlUtils.exposeLocalizationContext(request, this.messageSource);
        } else {
            JstlUtils.exposeLocalizationContext(new RequestContext(request, this.getServletContext()));
        }
    }
}

