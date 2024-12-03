/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.DimensionAwareUriResolver
 *  com.atlassian.lesscss.spi.EncodeStateResult
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.spi.DimensionAwareUriResolver;
import com.atlassian.lesscss.spi.EncodeStateResult;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class WebStaticUriResolver
implements DimensionAwareUriResolver {
    private final ServletContextFactory servletContextFactory;

    public WebStaticUriResolver(ServletContextFactory servletContextFactory) {
        this.servletContextFactory = servletContextFactory;
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty();
    }

    public boolean exists(URI uri) {
        try {
            return this.servletContextFactory.getServletContext().getResource(uri.getPath()) != null;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    public EncodeStateResult encodeState(URI uri, Coordinate coord) {
        return new EncodeStateResult(this.encodeState(uri), Optional.empty());
    }

    public String encodeState(URI uri) {
        URLConnection connection = null;
        try {
            URL url = this.servletContextFactory.getServletContext().getResource(uri.getPath());
            connection = url.openConnection();
            String string = String.valueOf(connection.getLastModified());
            return string;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            if (connection != null) {
                try {
                    connection.getInputStream().close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public InputStream open(URI uri) throws IOException {
        InputStream is = this.servletContextFactory.getServletContext().getResourceAsStream(uri.getPath());
        if (is == null) {
            throw new IOException(uri.getPath() + " does not exist in the servletContext");
        }
        return is;
    }

    public boolean supports(URI uri) {
        return "webstatic".equals(uri.getScheme());
    }
}

