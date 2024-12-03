/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package org.tuckey.web.filters.urlrewrite.sample;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;
import org.tuckey.web.filters.urlrewrite.sample.SampleConfExt;

public class SampleMultiUrlRewriteFilter
extends UrlRewriteFilter {
    private List urlrewriters = new ArrayList();

    public void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
        try {
            Conf conf1 = new Conf(filterConfig.getServletContext(), new FileInputStream("someconf.xml"), "someconf.xml", "");
            this.urlrewriters.add(new UrlRewriter(conf1));
            SampleConfExt conf2 = new SampleConfExt();
            this.urlrewriters.add(new UrlRewriter(conf2));
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }

    public UrlRewriter getUrlRewriter(ServletRequest request, ServletResponse response, FilterChain chain) {
        return (UrlRewriter)this.urlrewriters.get(0);
    }

    public void destroyUrlRewriter() {
        for (int i = 0; i < this.urlrewriters.size(); ++i) {
            UrlRewriter urlRewriter = (UrlRewriter)this.urlrewriters.get(i);
            urlRewriter.destroy();
        }
    }
}

