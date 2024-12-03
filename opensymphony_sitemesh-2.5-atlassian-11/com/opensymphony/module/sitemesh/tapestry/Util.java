/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tapestry.IRender
 *  org.apache.tapestry.IRequestCycle
 *  org.apache.tapestry.valid.RenderString
 */
package com.opensymphony.module.sitemesh.tapestry;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.valid.RenderString;

public class Util {
    public static String getTitle(IRequestCycle cycle) {
        return Util.getPage(cycle).getTitle();
    }

    public static String getProperty(String name, IRequestCycle cycle) {
        return Util.getPage(cycle).getProperty(name);
    }

    public static Page getPage(IRequestCycle cycle) {
        return (Page)cycle.getRequestContext().getRequest().getAttribute(RequestConstants.PAGE);
    }

    public static IRender getHeadRenderer(IRequestCycle cycle) {
        return new RenderString(((HTMLPage)Util.getPage(cycle)).getHead(), true);
    }
}

