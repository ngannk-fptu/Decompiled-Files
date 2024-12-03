/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.jsp.PageContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.AttributeMap;

public class TagUtils {
    private static final Logger LOG = LogManager.getLogger(TagUtils.class);

    public static ValueStack getStack(PageContext pageContext) {
        LOG.trace("Reading ValueStack out of page context: {}", (Object)pageContext);
        HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
        ValueStack stack = ServletActionContext.getValueStack(req);
        ValueStack valueStack = stack = stack != null ? stack : ActionContext.getContext().getValueStack();
        if (stack == null) {
            LOG.warn("No ValueStack in ActionContext!");
            throw new ConfigurationException("Rendering tag out of Action scope, accessing directly JSPs is not recommended! Please read https://struts.apache.org/security/#never-expose-jsp-files-directly");
        }
        LOG.trace("Adds the current PageContext to ActionContext");
        stack.getActionContext().withPageContext(pageContext).with("attr", new AttributeMap(stack.getContext()));
        return stack;
    }
}

