/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.StatefulTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.utility.NullArgumentException;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTemplateLoader
implements StatefulTemplateLoader {
    private final TemplateLoader[] templateLoaders;
    private final Map<String, TemplateLoader> lastTemplateLoaderForName = new ConcurrentHashMap<String, TemplateLoader>();
    private boolean sticky = true;

    public MultiTemplateLoader(TemplateLoader[] templateLoaders) {
        NullArgumentException.check("templateLoaders", templateLoaders);
        this.templateLoaders = (TemplateLoader[])templateLoaders.clone();
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        Object source;
        TemplateLoader lastTemplateLoader = null;
        if (this.sticky && (lastTemplateLoader = this.lastTemplateLoaderForName.get(name)) != null && (source = lastTemplateLoader.findTemplateSource(name)) != null) {
            return new MultiSource(source, lastTemplateLoader);
        }
        for (TemplateLoader templateLoader : this.templateLoaders) {
            Object source2;
            if (lastTemplateLoader == templateLoader || (source2 = templateLoader.findTemplateSource(name)) == null) continue;
            if (this.sticky) {
                this.lastTemplateLoaderForName.put(name, templateLoader);
            }
            return new MultiSource(source2, templateLoader);
        }
        if (this.sticky) {
            this.lastTemplateLoaderForName.remove(name);
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        return ((MultiSource)templateSource).getLastModified();
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return ((MultiSource)templateSource).getReader(encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        ((MultiSource)templateSource).close();
    }

    @Override
    public void resetState() {
        this.lastTemplateLoaderForName.clear();
        for (TemplateLoader loader : this.templateLoaders) {
            if (!(loader instanceof StatefulTemplateLoader)) continue;
            ((StatefulTemplateLoader)loader).resetState();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiTemplateLoader(");
        for (int i = 0; i < this.templateLoaders.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("loader").append(i + 1).append(" = ").append(this.templateLoaders[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    public int getTemplateLoaderCount() {
        return this.templateLoaders.length;
    }

    public TemplateLoader getTemplateLoader(int index) {
        return this.templateLoaders[index];
    }

    public boolean isSticky() {
        return this.sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    static final class MultiSource {
        private final Object source;
        private final TemplateLoader loader;

        MultiSource(Object source, TemplateLoader loader) {
            this.source = source;
            this.loader = loader;
        }

        long getLastModified() {
            return this.loader.getLastModified(this.source);
        }

        Reader getReader(String encoding) throws IOException {
            return this.loader.getReader(this.source, encoding);
        }

        void close() throws IOException {
            this.loader.closeTemplateSource(this.source);
        }

        Object getWrappedSource() {
            return this.source;
        }

        public boolean equals(Object o) {
            if (o instanceof MultiSource) {
                MultiSource m = (MultiSource)o;
                return m.loader.equals(this.loader) && m.source.equals(this.source);
            }
            return false;
        }

        public int hashCode() {
            return this.loader.hashCode() + 31 * this.source.hashCode();
        }

        public String toString() {
            return this.source.toString();
        }
    }
}

