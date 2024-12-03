/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package javax.servlet.jsp;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.PageContext;

public abstract class JspFactory {
    private static volatile JspFactory deflt = null;

    public static void setDefaultFactory(JspFactory deflt) {
        JspFactory.deflt = deflt;
    }

    public static JspFactory getDefaultFactory() {
        return deflt;
    }

    public abstract PageContext getPageContext(Servlet var1, ServletRequest var2, ServletResponse var3, String var4, boolean var5, int var6, boolean var7);

    public abstract void releasePageContext(PageContext var1);

    public abstract JspEngineInfo getEngineInfo();

    public abstract JspApplicationContext getJspApplicationContext(ServletContext var1);
}

