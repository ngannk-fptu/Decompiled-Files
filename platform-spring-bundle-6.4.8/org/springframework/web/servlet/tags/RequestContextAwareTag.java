/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.tagext.TagSupport
 *  javax.servlet.jsp.tagext.TryCatchFinally
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.JspAwareRequestContext;
import org.springframework.web.servlet.support.RequestContext;

public abstract class RequestContextAwareTag
extends TagSupport
implements TryCatchFinally {
    public static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.REQUEST_CONTEXT";
    protected final Log logger = LogFactory.getLog(((Object)((Object)this)).getClass());
    @Nullable
    private RequestContext requestContext;

    public final int doStartTag() throws JspException {
        try {
            this.requestContext = (RequestContext)this.pageContext.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
            if (this.requestContext == null) {
                this.requestContext = new JspAwareRequestContext(this.pageContext);
                this.pageContext.setAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE, (Object)this.requestContext);
            }
            return this.doStartTagInternal();
        }
        catch (RuntimeException | JspException ex) {
            this.logger.error((Object)ex.getMessage(), ex);
            throw ex;
        }
        catch (Exception ex) {
            this.logger.error((Object)ex.getMessage(), (Throwable)ex);
            throw new JspTagException(ex.getMessage());
        }
    }

    protected final RequestContext getRequestContext() {
        Assert.state(this.requestContext != null, "No current RequestContext");
        return this.requestContext;
    }

    protected abstract int doStartTagInternal() throws Exception;

    public void doCatch(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void doFinally() {
        this.requestContext = null;
    }
}

