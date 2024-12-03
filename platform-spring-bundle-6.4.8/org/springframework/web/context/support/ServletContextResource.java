/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

public class ServletContextResource
extends AbstractFileResolvingResource
implements ContextResource {
    private final ServletContext servletContext;
    private final String path;

    public ServletContextResource(ServletContext servletContext, String path) {
        Assert.notNull((Object)servletContext, "Cannot resolve ServletContextResource without ServletContext");
        this.servletContext = servletContext;
        Assert.notNull((Object)path, "Path is required");
        String pathToUse = StringUtils.cleanPath(path);
        if (!pathToUse.startsWith("/")) {
            pathToUse = "/" + pathToUse;
        }
        this.path = pathToUse;
    }

    public final ServletContext getServletContext() {
        return this.servletContext;
    }

    public final String getPath() {
        return this.path;
    }

    @Override
    public boolean exists() {
        try {
            URL url = this.servletContext.getResource(this.path);
            return url != null;
        }
        catch (MalformedURLException ex) {
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isFile() {
        try {
            URL url = this.servletContext.getResource(this.path);
            if (url != null && ResourceUtils.isFileURL(url)) {
                return true;
            }
            String realPath = this.servletContext.getRealPath(this.path);
            if (realPath == null) {
                return false;
            }
            File file = new File(realPath);
            return file.exists() && file.isFile();
        }
        catch (IOException ex) {
            return false;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is == null) {
            throw new FileNotFoundException("Could not open " + this.getDescription());
        }
        return is;
    }

    @Override
    public URL getURL() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url == null) {
            throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    @Override
    public File getFile() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url != null && ResourceUtils.isFileURL(url)) {
            return super.getFile();
        }
        String realPath = WebUtils.getRealPath(this.servletContext, this.path);
        return new File(realPath);
    }

    @Override
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new ServletContextResource(this.servletContext, pathToUse);
    }

    @Override
    @Nullable
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    @Override
    public String getDescription() {
        return "ServletContext resource [" + this.path + "]";
    }

    @Override
    public String getPathWithinContext() {
        return this.path;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServletContextResource)) {
            return false;
        }
        ServletContextResource otherRes = (ServletContextResource)other;
        return this.path.equals(otherRes.path) && this.servletContext.equals(otherRes.servletContext);
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
}

