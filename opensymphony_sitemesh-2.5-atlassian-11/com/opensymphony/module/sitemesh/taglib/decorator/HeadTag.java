/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public class HeadTag
extends AbstractTag {
    public final int doEndTag() throws JspException {
        HTMLPage htmlPage = (HTMLPage)this.getPage();
        try {
            htmlPage.writeHead(this.getOut());
        }
        catch (IOException e) {
            throw new JspException("Error writing head element: " + e.toString(), (Throwable)e);
        }
        return 6;
    }
}

