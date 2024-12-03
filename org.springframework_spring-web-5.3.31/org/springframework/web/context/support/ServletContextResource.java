/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.core.io.AbstractFileResolvingResource
 *  org.springframework.core.io.ContextResource
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ResourceUtils
 *  org.springframework.util.StringUtils
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
        Assert.notNull((Object)servletContext, (String)"Cannot resolve ServletContextResource without ServletContext");
        this.servletContext = servletContext;
        Assert.notNull((Object)path, (String)"Path is required");
        String pathToUse = StringUtils.cleanPath((String)path);
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

    public boolean exists() {
        try {
            URL url = this.servletContext.getResource(this.path);
            return url != null;
        }
        catch (MalformedURLException ex) {
            return false;
        }
    }

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

    public boolean isFile() {
        try {
            URL url = this.servletContext.getResource(this.path);
            if (url != null && ResourceUtils.isFileURL((URL)url)) {
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

    public InputStream getInputStream() throws IOException {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is == null) {
            throw new FileNotFoundException("Could not open " + this.getDescription());
        }
        return is;
    }

    public URL getURL() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url == null) {
            throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    public File getFile() throws IOException {
        URL url = this.servletContext.getResource(this.path);
        if (url != null && ResourceUtils.isFileURL((URL)url)) {
            return super.getFile();
        }
        String realPath = WebUtils.getRealPath(this.servletContext, this.path);
        return new File(realPath);
    }

    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath((String)this.path, (String)relativePath);
        return new ServletContextResource(this.servletContext, pathToUse);
    }

    @Nullable
    public String getFilename() {
        return StringUtils.getFilename((String)this.path);
    }

    public String getDescription() {
        return "ServletContext resource [" + this.path + "]";
    }

    public String getPathWithinContext() {
        return this.path;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServletContextResource)) {
            return false;
        }
        ServletContextResource otherRes = (ServletContextResource)((Object)other);
        return this.path.equals(otherRes.path) && this.servletContext.equals(otherRes.servletContext);
    }

    public int hashCode() {
        return this.path.hashCode();
    }
}

