/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateLoader;
import freemarker.cache.URLTemplateSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public abstract class URLTemplateLoader
implements TemplateLoader {
    private Boolean urlConnectionUsesCaches;

    @Override
    public Object findTemplateSource(String name) throws IOException {
        URL url = this.getURL(name);
        return url == null ? null : new URLTemplateSource(url, this.getURLConnectionUsesCaches());
    }

    @Override
    public long getLastModified(Object templateSource) {
        return ((URLTemplateSource)templateSource).lastModified();
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new InputStreamReader(((URLTemplateSource)templateSource).getInputStream(), encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        ((URLTemplateSource)templateSource).close();
    }

    public Boolean getURLConnectionUsesCaches() {
        return this.urlConnectionUsesCaches;
    }

    public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
        this.urlConnectionUsesCaches = urlConnectionUsesCaches;
    }

    protected abstract URL getURL(String var1);

    protected static String canonicalizePrefix(String prefix) {
        if ((prefix = prefix.replace('\\', '/')).length() > 0 && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix;
    }
}

