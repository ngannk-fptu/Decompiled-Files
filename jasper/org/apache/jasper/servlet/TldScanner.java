/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.descriptor.TaglibDescriptor
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.JarScanType
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.JarScannerCallback
 *  org.apache.tomcat.util.descriptor.tld.TaglibXml
 *  org.apache.tomcat.util.descriptor.tld.TldParser
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 */
package org.apache.jasper.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import org.apache.jasper.compiler.JarScannerFactory;
import org.apache.jasper.compiler.Localizer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.xml.sax.SAXException;

public class TldScanner {
    private final Log log = LogFactory.getLog(TldScanner.class);
    private static final String MSG = "org.apache.jasper.servlet.TldScanner";
    private static final String TLD_EXT = ".tld";
    private static final String WEB_INF = "/WEB-INF/";
    private final ServletContext context;
    private final TldParser tldParser;
    private final Map<String, TldResourcePath> uriTldResourcePathMap = new HashMap<String, TldResourcePath>();
    private final Map<TldResourcePath, TaglibXml> tldResourcePathTaglibXmlMap = new HashMap<TldResourcePath, TaglibXml>();
    private final List<String> listeners = new ArrayList<String>();

    public TldScanner(ServletContext context, boolean namespaceAware, boolean validation, boolean blockExternal) {
        this.context = context;
        this.tldParser = new TldParser(namespaceAware, validation, blockExternal);
    }

    public void scan() throws IOException, SAXException {
        this.scanPlatform();
        this.scanJspConfig();
        this.scanResourcePaths(WEB_INF);
        this.scanJars();
    }

    public Map<String, TldResourcePath> getUriTldResourcePathMap() {
        return this.uriTldResourcePathMap;
    }

    public Map<TldResourcePath, TaglibXml> getTldResourcePathTaglibXmlMap() {
        return this.tldResourcePathTaglibXmlMap;
    }

    public List<String> getListeners() {
        return this.listeners;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.tldParser.setClassLoader(classLoader);
    }

    protected void scanPlatform() {
    }

    protected void scanJspConfig() throws IOException, SAXException {
        JspConfigDescriptor jspConfigDescriptor = this.context.getJspConfigDescriptor();
        if (jspConfigDescriptor == null) {
            return;
        }
        Collection descriptors = jspConfigDescriptor.getTaglibs();
        for (TaglibDescriptor descriptor : descriptors) {
            URL url;
            String taglibURI = descriptor.getTaglibURI();
            String resourcePath = descriptor.getTaglibLocation();
            if (!resourcePath.startsWith("/")) {
                resourcePath = WEB_INF + resourcePath;
            }
            if (this.uriTldResourcePathMap.containsKey(taglibURI)) {
                this.log.warn((Object)Localizer.getMessage("org.apache.jasper.servlet.TldScanner.webxmlSkip", resourcePath, taglibURI));
                continue;
            }
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)Localizer.getMessage("org.apache.jasper.servlet.TldScanner.webxmlAdd", resourcePath, taglibURI));
            }
            if ((url = this.context.getResource(resourcePath)) != null) {
                TldResourcePath tldResourcePath = resourcePath.endsWith(".jar") ? new TldResourcePath(url, resourcePath, "META-INF/taglib.tld") : new TldResourcePath(url, resourcePath);
                TaglibXml tld = this.tldParser.parse(tldResourcePath);
                this.uriTldResourcePathMap.put(taglibURI, tldResourcePath);
                this.tldResourcePathTaglibXmlMap.put(tldResourcePath, tld);
                if (tld.getListeners() == null) continue;
                this.listeners.addAll(tld.getListeners());
                continue;
            }
            this.log.warn((Object)Localizer.getMessage("org.apache.jasper.servlet.TldScanner.webxmlFailPathDoesNotExist", resourcePath, taglibURI));
        }
    }

    protected void scanResourcePaths(String startPath) throws IOException, SAXException {
        boolean found = false;
        Set dirList = this.context.getResourcePaths(startPath);
        if (dirList != null) {
            for (String path : dirList) {
                if (path.startsWith("/WEB-INF/classes/") || path.startsWith("/WEB-INF/lib/")) continue;
                if (path.endsWith("/")) {
                    this.scanResourcePaths(path);
                    continue;
                }
                if (path.startsWith("/WEB-INF/tags/")) {
                    if (!path.endsWith("/implicit.tld")) continue;
                    found = true;
                    this.parseTld(path);
                    continue;
                }
                if (!path.endsWith(TLD_EXT)) continue;
                found = true;
                this.parseTld(path);
            }
        }
        if (found) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.tldCache.tldInResourcePath", startPath));
            }
        } else if (this.log.isDebugEnabled()) {
            this.log.debug((Object)Localizer.getMessage("jsp.tldCache.noTldInResourcePath", startPath));
        }
    }

    public void scanJars() {
        JarScanner scanner = JarScannerFactory.getJarScanner(this.context);
        TldScannerCallback callback = new TldScannerCallback();
        scanner.scan(JarScanType.TLD, this.context, (JarScannerCallback)callback);
        if (callback.scanFoundNoTLDs()) {
            this.log.info((Object)Localizer.getMessage("jsp.tldCache.noTldSummary"));
        }
    }

    protected void parseTld(String resourcePath) throws IOException, SAXException {
        TldResourcePath tldResourcePath = new TldResourcePath(this.context.getResource(resourcePath), resourcePath);
        this.parseTld(tldResourcePath);
    }

    protected void parseTld(TldResourcePath path) throws IOException, SAXException {
        TaglibXml tld = this.tldParser.parse(path);
        String uri = tld.getUri();
        if (uri != null && !this.uriTldResourcePathMap.containsKey(uri)) {
            this.uriTldResourcePathMap.put(uri, path);
        }
        if (this.tldResourcePathTaglibXmlMap.containsKey(path)) {
            return;
        }
        this.tldResourcePathTaglibXmlMap.put(path, tld);
        if (tld.getListeners() != null) {
            this.listeners.addAll(tld.getListeners());
        }
    }

    class TldScannerCallback
    implements JarScannerCallback {
        private boolean foundJarWithoutTld = false;
        private boolean foundFileWithoutTld = false;

        TldScannerCallback() {
        }

        public void scan(Jar jar, String webappPath, boolean isWebapp) throws IOException {
            boolean found = false;
            URL jarFileUrl = jar.getJarFileURL();
            jar.nextEntry();
            String entryName = jar.getEntryName();
            while (entryName != null) {
                if (entryName.startsWith("META-INF/") && entryName.endsWith(TldScanner.TLD_EXT)) {
                    found = true;
                    TldResourcePath tldResourcePath = new TldResourcePath(jarFileUrl, webappPath, entryName);
                    try {
                        TldScanner.this.parseTld(tldResourcePath);
                    }
                    catch (SAXException e) {
                        throw new IOException(e);
                    }
                }
                jar.nextEntry();
                entryName = jar.getEntryName();
            }
            if (found) {
                if (TldScanner.this.log.isDebugEnabled()) {
                    TldScanner.this.log.debug((Object)Localizer.getMessage("jsp.tldCache.tldInJar", jarFileUrl.toString()));
                }
            } else {
                this.foundJarWithoutTld = true;
                if (TldScanner.this.log.isDebugEnabled()) {
                    TldScanner.this.log.debug((Object)Localizer.getMessage("jsp.tldCache.noTldInJar", jarFileUrl.toString()));
                }
            }
        }

        public void scan(File file, final String webappPath, boolean isWebapp) throws IOException {
            File metaInf = new File(file, "META-INF");
            if (!metaInf.isDirectory()) {
                return;
            }
            this.foundFileWithoutTld = false;
            final Path filePath = file.toPath();
            Files.walkFileTree(metaInf.toPath(), (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String resourcePath;
                    Path fileName = file.getFileName();
                    if (fileName == null || !fileName.toString().toLowerCase(Locale.ENGLISH).endsWith(TldScanner.TLD_EXT)) {
                        return FileVisitResult.CONTINUE;
                    }
                    TldScannerCallback.this.foundFileWithoutTld = true;
                    if (webappPath == null) {
                        resourcePath = null;
                    } else {
                        String subPath = file.subpath(filePath.getNameCount(), file.getNameCount()).toString();
                        if ('/' != File.separatorChar) {
                            subPath = subPath.replace(File.separatorChar, '/');
                        }
                        resourcePath = webappPath + "/" + subPath;
                    }
                    try {
                        URL url = file.toUri().toURL();
                        TldResourcePath path = new TldResourcePath(url, resourcePath);
                        TldScanner.this.parseTld(path);
                    }
                    catch (SAXException e) {
                        throw new IOException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            if (this.foundFileWithoutTld) {
                if (TldScanner.this.log.isDebugEnabled()) {
                    TldScanner.this.log.debug((Object)Localizer.getMessage("jsp.tldCache.tldInDir", file.getAbsolutePath()));
                }
            } else if (TldScanner.this.log.isDebugEnabled()) {
                TldScanner.this.log.debug((Object)Localizer.getMessage("jsp.tldCache.noTldInDir", file.getAbsolutePath()));
            }
        }

        public void scanWebInfClasses() throws IOException {
            Set paths = TldScanner.this.context.getResourcePaths("/WEB-INF/classes/META-INF");
            if (paths == null) {
                return;
            }
            for (String path : paths) {
                if (!path.endsWith(TldScanner.TLD_EXT)) continue;
                try {
                    TldScanner.this.parseTld(path);
                }
                catch (SAXException e) {
                    throw new IOException(e);
                }
            }
        }

        boolean scanFoundNoTLDs() {
            return this.foundJarWithoutTld;
        }
    }
}

