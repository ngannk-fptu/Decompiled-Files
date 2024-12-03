/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.velocity.tools.struts;

import javax.servlet.ServletContext;
import org.apache.velocity.tools.generic.ValueParser;
import org.apache.velocity.tools.struts.StrutsUtils;
import org.apache.velocity.tools.view.tools.LinkTool;

public class StrutsLinkTool
extends LinkTool {
    protected ServletContext application;
    private String get;

    @Override
    protected void configure(ValueParser props) {
        super.configure(props);
        this.application = (ServletContext)props.getValue("servletContext");
    }

    public StrutsLinkTool get(String getme) {
        StrutsLinkTool sub = null;
        sub = "action".equalsIgnoreCase(this.get) ? this.action(getme) : ("forward".equalsIgnoreCase(this.get) ? this.forward(getme) : (StrutsLinkTool)this.duplicate());
        if (sub == null) {
            return null;
        }
        sub.get = getme;
        return sub;
    }

    public StrutsLinkTool action(String action) {
        String url = StrutsUtils.getActionMappingURL(this.application, this.request, action);
        if (url == null) {
            this.debug("StrutsLinkTool : In method setAction(" + action + "): Parameter does not map to a valid action.", new Object[0]);
            return null;
        }
        return (StrutsLinkTool)this.absolute(url);
    }

    public StrutsLinkTool setAction(String action) {
        return this.action(action);
    }

    public StrutsLinkTool forward(String forward) {
        String url = StrutsUtils.getForwardURL(this.request, this.application, forward);
        if (url == null) {
            this.debug("StrutsLinkTool : In method setForward(" + forward + "): Parameter does not map to a valid forward.", new Object[0]);
            return null;
        }
        return (StrutsLinkTool)this.absolute(url);
    }

    public StrutsLinkTool setForward(String forward) {
        return this.forward(forward);
    }
}

