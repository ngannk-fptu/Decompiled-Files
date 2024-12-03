/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLoaderUtils;
import freemarker.template.utility.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ByteArrayTemplateLoader
implements TemplateLoader {
    private final Map<String, ByteArrayTemplateSource> templates = new HashMap<String, ByteArrayTemplateSource>();

    public void putTemplate(String name, byte[] templateContent) {
        this.putTemplate(name, templateContent, System.currentTimeMillis());
    }

    public void putTemplate(String name, byte[] templateContent, long lastModified) {
        this.templates.put(name, new ByteArrayTemplateSource(name, templateContent, lastModified));
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
        return ((ByteArrayTemplateSource)templateSource).lastModified;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws UnsupportedEncodingException {
        return new InputStreamReader((InputStream)new ByteArrayInputStream(((ByteArrayTemplateSource)templateSource).templateContent), encoding);
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

    private static class ByteArrayTemplateSource {
        private final String name;
        private final byte[] templateContent;
        private final long lastModified;

        ByteArrayTemplateSource(String name, byte[] templateContent, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (templateContent == null) {
                throw new IllegalArgumentException("templateContent == null");
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
            ByteArrayTemplateSource other = (ByteArrayTemplateSource)obj;
            return !(this.name == null ? other.name != null : !this.name.equals(other.name));
        }
    }
}

