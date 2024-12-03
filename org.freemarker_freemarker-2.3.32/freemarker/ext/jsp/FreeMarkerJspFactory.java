/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.jsp.JspEngineInfo
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.PageContext
 */
package freemarker.ext.jsp;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

abstract class FreeMarkerJspFactory
extends JspFactory {
    FreeMarkerJspFactory() {
    }

    protected abstract String getSpecificationVersion();

    public JspEngineInfo getEngineInfo() {
        return new JspEngineInfo(){

            public String getSpecificationVersion() {
                return FreeMarkerJspFactory.this.getSpecificationVersion();
            }
        };
    }

    public PageContext getPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
        throw new UnsupportedOperationException();
    }

    public void releasePageContext(PageContext ctx) {
        throw new UnsupportedOperationException();
    }
}

