/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.descriptor.tld.TaglibXml
 *  org.apache.tomcat.util.descriptor.tld.TldParser
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 */
package org.apache.jasper.compiler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Localizer;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.xml.sax.SAXException;

public class TldCache {
    public static final String SERVLET_CONTEXT_ATTRIBUTE_NAME = TldCache.class.getName();
    private final ServletContext servletContext;
    private final Map<String, TldResourcePath> uriTldResourcePathMap = new HashMap<String, TldResourcePath>();
    private final Map<TldResourcePath, TaglibXmlCacheEntry> tldResourcePathTaglibXmlMap = new HashMap<TldResourcePath, TaglibXmlCacheEntry>();
    private final TldParser tldParser;

    public static TldCache getInstance(ServletContext servletContext) {
        if (servletContext == null) {
            throw new IllegalArgumentException(Localizer.getMessage("org.apache.jasper.compiler.TldCache.servletContextNull"));
        }
        return (TldCache)servletContext.getAttribute(SERVLET_CONTEXT_ATTRIBUTE_NAME);
    }

    public TldCache(ServletContext servletContext, Map<String, TldResourcePath> uriTldResourcePathMap, Map<TldResourcePath, TaglibXml> tldResourcePathTaglibXmlMap) {
        this.servletContext = servletContext;
        this.uriTldResourcePathMap.putAll(uriTldResourcePathMap);
        for (Map.Entry<TldResourcePath, TaglibXml> entry : tldResourcePathTaglibXmlMap.entrySet()) {
            TldResourcePath tldResourcePath = entry.getKey();
            long[] lastModified = this.getLastModified(tldResourcePath);
            TaglibXmlCacheEntry cacheEntry = new TaglibXmlCacheEntry(entry.getValue(), lastModified[0], lastModified[1]);
            this.tldResourcePathTaglibXmlMap.put(tldResourcePath, cacheEntry);
        }
        boolean validate = Boolean.parseBoolean(servletContext.getInitParameter("org.apache.jasper.XML_VALIDATE_TLD"));
        String blockExternalString = servletContext.getInitParameter("org.apache.jasper.XML_BLOCK_EXTERNAL");
        boolean blockExternal = blockExternalString == null ? true : Boolean.parseBoolean(blockExternalString);
        this.tldParser = new TldParser(true, validate, blockExternal);
    }

    public TldResourcePath getTldResourcePath(String uri) {
        return this.uriTldResourcePathMap.get(uri);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TaglibXml getTaglibXml(TldResourcePath tldResourcePath) throws JasperException {
        TaglibXmlCacheEntry cacheEntry = this.tldResourcePathTaglibXmlMap.get(tldResourcePath);
        if (cacheEntry == null) {
            return null;
        }
        long[] lastModified = this.getLastModified(tldResourcePath);
        if (lastModified[0] != cacheEntry.getWebAppPathLastModified() || lastModified[1] != cacheEntry.getEntryLastModified()) {
            TaglibXmlCacheEntry taglibXmlCacheEntry = cacheEntry;
            synchronized (taglibXmlCacheEntry) {
                if (lastModified[0] != cacheEntry.getWebAppPathLastModified() || lastModified[1] != cacheEntry.getEntryLastModified()) {
                    TaglibXml updatedTaglibXml;
                    try {
                        updatedTaglibXml = this.tldParser.parse(tldResourcePath);
                    }
                    catch (IOException | SAXException e) {
                        throw new JasperException(e);
                    }
                    cacheEntry.setTaglibXml(updatedTaglibXml);
                    cacheEntry.setWebAppPathLastModified(lastModified[0]);
                    cacheEntry.setEntryLastModified(lastModified[1]);
                }
            }
        }
        return cacheEntry.getTaglibXml();
    }

    private long[] getLastModified(TldResourcePath tldResourcePath) {
        long[] result = new long[]{-1L, -1L};
        try {
            String webappPath = tldResourcePath.getWebappPath();
            if (webappPath != null) {
                URL url = this.servletContext.getResource(tldResourcePath.getWebappPath());
                URLConnection conn = url.openConnection();
                result[0] = conn.getLastModified();
                if ("file".equals(url.getProtocol())) {
                    conn.getInputStream().close();
                }
            }
            try (Jar jar = tldResourcePath.openJar();){
                if (jar != null) {
                    result[1] = jar.getLastModified(tldResourcePath.getEntryName());
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return result;
    }

    private static class TaglibXmlCacheEntry {
        private volatile TaglibXml taglibXml;
        private volatile long webAppPathLastModified;
        private volatile long entryLastModified;

        TaglibXmlCacheEntry(TaglibXml taglibXml, long webAppPathLastModified, long entryLastModified) {
            this.taglibXml = taglibXml;
            this.webAppPathLastModified = webAppPathLastModified;
            this.entryLastModified = entryLastModified;
        }

        public TaglibXml getTaglibXml() {
            return this.taglibXml;
        }

        public void setTaglibXml(TaglibXml taglibXml) {
            this.taglibXml = taglibXml;
        }

        public long getWebAppPathLastModified() {
            return this.webAppPathLastModified;
        }

        public void setWebAppPathLastModified(long webAppPathLastModified) {
            this.webAppPathLastModified = webAppPathLastModified;
        }

        public long getEntryLastModified() {
            return this.entryLastModified;
        }

        public void setEntryLastModified(long entryLastModified) {
            this.entryLastModified = entryLastModified;
        }
    }
}

