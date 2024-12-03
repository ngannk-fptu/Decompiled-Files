/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.importexport.resolvers;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.springframework.web.context.ServletContextAware;

@Deprecated
public class ServletContextURIResolver
implements URIResolver,
ServletContextAware {
    private ServletContext servletContext;

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            URL resourceUrl = this.servletContext.getResource(href);
            if (resourceUrl != null) {
                return new StreamSource(this.servletContext.getResourceAsStream(href), resourceUrl.toExternalForm());
            }
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        return null;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

