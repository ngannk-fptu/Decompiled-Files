/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package freemarker.cache;

import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLoaderUtils;
import freemarker.cache.URLTemplateSource;
import freemarker.log.Logger;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

public class WebappTemplateLoader
implements TemplateLoader {
    private static final Logger LOG = Logger.getLogger("freemarker.cache");
    private final ServletContext servletContext;
    private final String subdirPath;
    private Boolean urlConnectionUsesCaches;
    private boolean attemptFileAccess = true;

    public WebappTemplateLoader(ServletContext servletContext) {
        this(servletContext, "/");
    }

    public WebappTemplateLoader(ServletContext servletContext, String subdirPath) {
        NullArgumentException.check("servletContext", servletContext);
        NullArgumentException.check("subdirPath", subdirPath);
        subdirPath = subdirPath.replace('\\', '/');
        if (!subdirPath.endsWith("/")) {
            subdirPath = subdirPath + "/";
        }
        if (!subdirPath.startsWith("/")) {
            subdirPath = "/" + subdirPath;
        }
        this.subdirPath = subdirPath;
        this.servletContext = servletContext;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        String fullPath = this.subdirPath + name;
        if (this.attemptFileAccess) {
            try {
                File file;
                String realPath = this.servletContext.getRealPath(fullPath);
                if (realPath != null && (file = new File(realPath)).canRead() && file.isFile()) {
                    return file;
                }
            }
            catch (SecurityException realPath) {
                // empty catch block
            }
        }
        URL url = null;
        try {
            url = this.servletContext.getResource(fullPath);
        }
        catch (MalformedURLException e) {
            LOG.warn("Could not retrieve resource " + StringUtil.jQuoteNoXSS(fullPath), e);
            return null;
        }
        return url == null ? null : new URLTemplateSource(url, this.getURLConnectionUsesCaches());
    }

    @Override
    public long getLastModified(Object templateSource) {
        if (templateSource instanceof File) {
            return ((File)templateSource).lastModified();
        }
        return ((URLTemplateSource)templateSource).lastModified();
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if (templateSource instanceof File) {
            return new InputStreamReader((InputStream)new FileInputStream((File)templateSource), encoding);
        }
        return new InputStreamReader(((URLTemplateSource)templateSource).getInputStream(), encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        if (!(templateSource instanceof File)) {
            ((URLTemplateSource)templateSource).close();
        }
    }

    public Boolean getURLConnectionUsesCaches() {
        return this.urlConnectionUsesCaches;
    }

    public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
        this.urlConnectionUsesCaches = urlConnectionUsesCaches;
    }

    public String toString() {
        return TemplateLoaderUtils.getClassNameForToString(this) + "(subdirPath=" + StringUtil.jQuote(this.subdirPath) + ", servletContext={contextPath=" + StringUtil.jQuote(this.getContextPath()) + ", displayName=" + StringUtil.jQuote(this.servletContext.getServletContextName()) + "})";
    }

    private String getContextPath() {
        try {
            Method m = this.servletContext.getClass().getMethod("getContextPath", CollectionUtils.EMPTY_CLASS_ARRAY);
            return (String)m.invoke((Object)this.servletContext, CollectionUtils.EMPTY_OBJECT_ARRAY);
        }
        catch (Throwable e) {
            return "[can't query before Serlvet 2.5]";
        }
    }

    public boolean getAttemptFileAccess() {
        return this.attemptFileAccess;
    }

    public void setAttemptFileAccess(boolean attemptLoadingFromFile) {
        this.attemptFileAccess = attemptLoadingFromFile;
    }
}

