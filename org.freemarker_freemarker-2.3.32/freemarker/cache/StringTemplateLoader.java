/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLoaderUtils;
import freemarker.template.utility.StringUtil;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class StringTemplateLoader
implements TemplateLoader {
    private final Map<String, StringTemplateSource> templates = new HashMap<String, StringTemplateSource>();

    public void putTemplate(String name, String templateContent) {
        this.putTemplate(name, templateContent, System.currentTimeMillis());
    }

    public void putTemplate(String name, String templateContent, long lastModified) {
        this.templates.put(name, new StringTemplateSource(name, templateContent, lastModified));
    }

    public boolean removeTemplate(String name) {
        return this.templates.remove(name) != null;
    }

    @Override
    public void closeTemplateSource(Object templateSource) {
    }

    @Override
    public Object findTemplateSource(String name) {
        return this.templates.get(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return ((StringTemplateSource)templateSource).lastModified;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) {
        return new StringReader(((StringTemplateSource)templateSource).templateContent);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TemplateLoaderUtils.getClassNameForToString(this));
        sb.append("(Map { ");
        int cnt = 0;
        for (String name : this.templates.keySet()) {
            if (++cnt != 1) {
                sb.append(", ");
            }
            if (cnt > 10) {
                sb.append("...");
                break;
            }
            sb.append(StringUtil.jQuote(name));
            sb.append("=...");
        }
        if (cnt != 0) {
            sb.append(' ');
        }
        sb.append("})");
        return sb.toString();
    }

    private static class StringTemplateSource {
        private final String name;
        private final String templateContent;
        private final long lastModified;

        StringTemplateSource(String name, String templateContent, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (templateContent == null) {
                throw new IllegalArgumentException("source == null");
            }
            if (lastModified < -1L) {
                throw new IllegalArgumentException("lastModified < -1L");
            }
            this.name = name;
            this.templateContent = templateContent;
            this.lastModified = lastModified;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            StringTemplateSource other = (StringTemplateSource)obj;
            return !(this.name == null ? other.name != null : !this.name.equals(other.name));
        }

        public String toString() {
            return this.name;
        }
    }
}

