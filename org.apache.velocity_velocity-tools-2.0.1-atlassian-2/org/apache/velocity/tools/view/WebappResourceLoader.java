/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 */
package org.apache.velocity.tools.view;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import javax.servlet.ServletContext;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class WebappResourceLoader
extends ResourceLoader {
    protected String[] paths = null;
    protected HashMap templatePaths = null;
    protected ServletContext servletContext = null;

    public void init(ExtendedProperties configuration) {
        this.log.trace((Object)"WebappResourceLoader: initialization starting.");
        this.paths = configuration.getStringArray("path");
        if (this.paths == null || this.paths.length == 0) {
            this.paths = new String[1];
            this.paths[0] = "/";
        } else {
            for (int i = 0; i < this.paths.length; ++i) {
                if (!this.paths[i].endsWith("/")) {
                    int n = i;
                    this.paths[n] = this.paths[n] + '/';
                }
                this.log.info((Object)("WebappResourceLoader: added template path - '" + this.paths[i] + "'"));
            }
        }
        Object obj = this.rsvc.getApplicationAttribute((Object)ServletContext.class.getName());
        if (obj instanceof ServletContext) {
            this.servletContext = (ServletContext)obj;
        } else {
            this.log.error((Object)"WebappResourceLoader: unable to retrieve ServletContext");
        }
        this.templatePaths = new HashMap();
        this.log.trace((Object)"WebappResourceLoader: initialization complete.");
    }

    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        InputStream result = null;
        if (name == null || name.length() == 0) {
            throw new ResourceNotFoundException("WebappResourceLoader: No template name provided");
        }
        while (name.startsWith("/")) {
            name = name.substring(1);
        }
        Exception exception = null;
        for (int i = 0; i < this.paths.length; ++i) {
            String path = this.paths[i] + name;
            try {
                result = this.servletContext.getResourceAsStream(path);
                if (result == null) continue;
                this.templatePaths.put(name, this.paths[i]);
                break;
            }
            catch (NullPointerException npe) {
                throw npe;
            }
            catch (Exception e) {
                if (exception != null) continue;
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("WebappResourceLoader: Could not load " + path), (Throwable)e);
                }
                exception = e;
            }
        }
        if (result == null) {
            String msg = "WebappResourceLoader: Resource '" + name + "' not found.";
            if (exception == null) {
                throw new ResourceNotFoundException(msg);
            }
            msg = msg + "  Due to: " + exception;
            throw new ResourceNotFoundException(msg, (Throwable)exception);
        }
        return result;
    }

    private File getCachedFile(String rootPath, String fileName) {
        while (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        String savedPath = (String)this.templatePaths.get(fileName);
        return new File(rootPath + savedPath, fileName);
    }

    public boolean isSourceModified(Resource resource) {
        String rootPath = this.servletContext.getRealPath("/");
        if (rootPath == null) {
            return false;
        }
        String fileName = resource.getName();
        File cachedFile = this.getCachedFile(rootPath, fileName);
        if (!cachedFile.exists()) {
            return true;
        }
        File currentFile = null;
        for (int i = 0; i < this.paths.length && !(currentFile = new File(rootPath + this.paths[i], fileName)).canRead(); ++i) {
        }
        if (cachedFile.equals(currentFile) && cachedFile.canRead()) {
            return cachedFile.lastModified() != resource.getLastModified();
        }
        return true;
    }

    public long getLastModified(Resource resource) {
        String rootPath = this.servletContext.getRealPath("/");
        if (rootPath == null) {
            return 0L;
        }
        File cachedFile = this.getCachedFile(rootPath, resource.getName());
        if (cachedFile.canRead()) {
            return cachedFile.lastModified();
        }
        return 0L;
    }
}

