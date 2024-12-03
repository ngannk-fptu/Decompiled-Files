/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts.util.MessageResources
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.struts;

import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.util.MessageResources;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.struts.StrutsUtils;
import org.apache.velocity.tools.view.ViewContext;

public abstract class MessageResourcesTool {
    protected Log LOG;
    protected ServletContext application;
    protected HttpServletRequest request;
    private Locale locale;
    private MessageResources resources;

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            ViewContext ctx = (ViewContext)obj;
            this.request = ctx.getRequest();
            this.application = ctx.getServletContext();
            this.LOG = ctx.getVelocityEngine().getLog();
        }
    }

    public void configure(Map params) {
        this.request = (HttpServletRequest)params.get("request");
        this.application = (ServletContext)params.get("servletContext");
        this.LOG = (Log)params.get("log");
    }

    protected Locale getLocale() {
        if (this.locale == null) {
            this.locale = StrutsUtils.getLocale(this.request, this.request.getSession(false));
        }
        return this.locale;
    }

    protected MessageResources getResources(String bundle) {
        if (bundle == null) {
            if (this.resources == null) {
                this.resources = StrutsUtils.getMessageResources(this.request, this.application);
                if (this.resources == null) {
                    this.LOG.error((Object)"MessageResourcesTool : Message resources are not available.");
                }
            }
            return this.resources;
        }
        MessageResources res = StrutsUtils.getMessageResources(this.request, this.application, bundle);
        if (res == null) {
            this.LOG.error((Object)("MessageResourcesTool : MessageResources bundle '" + bundle + "' is not available."));
        }
        return res;
    }
}

