/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.JspApplicationContext
 */
package freemarker.ext.jsp;

import freemarker.ext.jsp.FreeMarkerJspApplicationContext;
import freemarker.ext.jsp.FreeMarkerJspFactory;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;

class FreeMarkerJspFactory21
extends FreeMarkerJspFactory {
    private static final String JSPCTX_KEY = FreeMarkerJspFactory21.class.getName() + "#jspAppContext";

    FreeMarkerJspFactory21() {
    }

    @Override
    protected String getSpecificationVersion() {
        return "2.1";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JspApplicationContext getJspApplicationContext(ServletContext ctx) {
        JspApplicationContext jspctx = (JspApplicationContext)ctx.getAttribute(JSPCTX_KEY);
        if (jspctx == null) {
            ServletContext servletContext = ctx;
            synchronized (servletContext) {
                jspctx = (JspApplicationContext)ctx.getAttribute(JSPCTX_KEY);
                if (jspctx == null) {
                    jspctx = new FreeMarkerJspApplicationContext();
                    ctx.setAttribute(JSPCTX_KEY, (Object)jspctx);
                }
            }
        }
        return jspctx;
    }
}

