/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.mvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.WebUtils;

public class ServletForwardingController
extends AbstractController
implements BeanNameAware {
    @Nullable
    private String servletName;
    @Nullable
    private String beanName;

    public ServletForwardingController() {
        super(false);
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        if (this.servletName == null) {
            this.servletName = name;
        }
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletContext servletContext = this.getServletContext();
        Assert.state(servletContext != null, "No ServletContext");
        RequestDispatcher rd = servletContext.getNamedDispatcher(this.servletName);
        if (rd == null) {
            throw new ServletException("No servlet with name '" + this.servletName + "' defined in web.xml");
        }
        if (this.useInclude(request, response)) {
            rd.include((ServletRequest)request, (ServletResponse)response);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Included servlet [" + this.servletName + "] in ServletForwardingController '" + this.beanName + "'"));
            }
        } else {
            rd.forward((ServletRequest)request, (ServletResponse)response);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Forwarded to servlet [" + this.servletName + "] in ServletForwardingController '" + this.beanName + "'"));
            }
        }
        return null;
    }

    protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
        return WebUtils.isIncludeRequest((ServletRequest)request) || response.isCommitted();
    }
}

