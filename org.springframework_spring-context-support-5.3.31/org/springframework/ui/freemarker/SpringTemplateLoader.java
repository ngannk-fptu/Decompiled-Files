/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.cache.TemplateLoader
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 */
package org.springframework.ui.freemarker;

import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

public class SpringTemplateLoader
implements TemplateLoader {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final ResourceLoader resourceLoader;
    private final String templateLoaderPath;

    public SpringTemplateLoader(ResourceLoader resourceLoader, String templateLoaderPath) {
        this.resourceLoader = resourceLoader;
        if (!templateLoaderPath.endsWith("/")) {
            templateLoaderPath = templateLoaderPath + "/";
        }
        this.templateLoaderPath = templateLoaderPath;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("SpringTemplateLoader for FreeMarker: using resource loader [" + this.resourceLoader + "] and template loader path [" + this.templateLoaderPath + "]"));
        }
    }

    @Nullable
    public Object findTemplateSource(String name) throws IOException {
        Resource resource;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Looking for FreeMarker template with name [" + name + "]"));
        }
        return (resource = this.resourceLoader.getResource(this.templateLoaderPath + name)).exists() ? resource : null;
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Resource resource = (Resource)templateSource;
        try {
            return new InputStreamReader(resource.getInputStream(), encoding);
        }
        catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Could not find FreeMarker template: " + resource));
            }
            throw ex;
        }
    }

    public long getLastModified(Object templateSource) {
        Resource resource = (Resource)templateSource;
        try {
            return resource.lastModified();
        }
        catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Could not obtain last-modified timestamp for FreeMarker template in " + resource + ": " + ex));
            }
            return -1L;
        }
    }

    public void closeTemplateSource(Object templateSource) throws IOException {
    }
}

