/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.tagext.TagInfo
 *  org.apache.sling.commons.compiler.source.JavaEscapeHelper
 */
package org.apache.sling.scripting.jsp.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.TagInfo;
import org.apache.sling.commons.compiler.source.JavaEscapeHelper;
import org.apache.sling.scripting.jsp.jasper.Constants;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.Options;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.JDTCompiler;
import org.apache.sling.scripting.jsp.jasper.compiler.JspRuntimeContext;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.compiler.ServletWriter;

public class JspCompilationContext {
    private final Map<String, URL> tagFileJarUrls;
    private volatile String className;
    private final String jspUri;
    private volatile boolean isErrPage;
    private final String basePackageName;
    private volatile String derivedPackageName;
    private volatile String servletJavaFileName;
    private volatile String javaPath;
    private volatile String classFileName;
    private volatile String contentType;
    private volatile ServletWriter writer;
    private final Options options;
    private volatile Compiler jspCompiler;
    private volatile String baseURI;
    private volatile String outputDir;
    private final ServletContext context;
    private final JspRuntimeContext rctxt;
    private volatile boolean isTagFile;
    private volatile boolean protoTypeMode;
    private volatile TagInfo tagInfo;
    private volatile URL tagFileJarUrl;

    public JspCompilationContext(String jspUri, boolean isErrPage, Options options, ServletContext context, JspRuntimeContext rctxt) {
        this(jspUri, isErrPage, options, context, rctxt, Constants.JSP_PACKAGE_NAME);
    }

    public JspCompilationContext(String jspUri, boolean isErrPage, Options options, ServletContext context, JspRuntimeContext rctxt, String basePckName) {
        this.jspUri = JspCompilationContext.canonicalURI(jspUri);
        this.isErrPage = isErrPage;
        this.options = options;
        this.context = context;
        this.baseURI = jspUri.substring(0, jspUri.lastIndexOf(47) + 1);
        if (this.baseURI == null) {
            this.baseURI = "/";
        } else if (this.baseURI.charAt(0) != '/') {
            this.baseURI = "/" + this.baseURI;
        }
        if (this.baseURI.charAt(this.baseURI.length() - 1) != '/') {
            this.baseURI = this.baseURI + '/';
        }
        this.rctxt = rctxt;
        this.tagFileJarUrls = new HashMap<String, URL>();
        this.basePackageName = basePckName;
    }

    public JspCompilationContext(String tagfile, TagInfo tagInfo, Options options, ServletContext context, JspRuntimeContext rctxt, URL tagFileJarUrl) {
        this(tagfile, false, options, context, rctxt);
        this.isTagFile = true;
        this.tagInfo = tagInfo;
        this.tagFileJarUrl = tagFileJarUrl;
    }

    public ClassLoader getClassLoader() {
        return this.getRuntimeContext().getIOProvider().getClassLoader();
    }

    public OutputStream getOutputStream(String fileName) throws IOException {
        return this.getRuntimeContext().getIOProvider().getOutputStream(fileName);
    }

    public InputStream getInputStream(String fileName) throws FileNotFoundException, IOException {
        return this.getRuntimeContext().getIOProvider().getInputStream(fileName);
    }

    public boolean delete(String fileName) {
        return this.getRuntimeContext().getIOProvider().delete(fileName);
    }

    public boolean rename(String oldFileName, String newFileName) {
        return this.getRuntimeContext().getIOProvider().rename(oldFileName, newFileName);
    }

    public String getOutputDir() {
        if (this.outputDir == null) {
            this.createOutputDir();
        }
        return this.outputDir;
    }

    private Compiler createCompiler() {
        if (this.jspCompiler != null) {
            return this.jspCompiler;
        }
        this.jspCompiler = new JDTCompiler(this);
        return this.jspCompiler;
    }

    public Compiler getCompiler() {
        return this.jspCompiler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Compiler activateCompiler() {
        if (this.jspCompiler == null) {
            JspCompilationContext jspCompilationContext = this;
            synchronized (jspCompilationContext) {
                if (this.jspCompiler == null) {
                    this.compile();
                }
            }
        }
        return this.jspCompiler;
    }

    public String resolveRelativeUri(String uri) {
        if (uri.startsWith("/") || uri.startsWith(File.separator)) {
            return uri;
        }
        return this.baseURI + uri;
    }

    public InputStream getResourceAsStream(String res) {
        return this.context.getResourceAsStream(JspCompilationContext.canonicalURI(res));
    }

    public URL getResource(String res) throws MalformedURLException {
        return this.context.getResource(JspCompilationContext.canonicalURI(res));
    }

    public Set<String> getResourcePaths(String path) {
        return this.context.getResourcePaths(JspCompilationContext.canonicalURI(path));
    }

    public String getRealPath(String path) {
        if (this.context != null) {
            return this.context.getRealPath(path);
        }
        return path;
    }

    public URL getTagFileJarUrl(String tagFile) {
        return this.tagFileJarUrls.get(tagFile);
    }

    public void setTagFileJarUrl(String tagFile, URL tagFileURL) {
        this.tagFileJarUrls.put(tagFile, tagFileURL);
    }

    public URL getTagFileUrl(String tagFile) {
        return this.tagFileJarUrls.get("tagfile:" + tagFile);
    }

    public void setTagFileUrl(String tagFile, URL tagFileURL) {
        this.tagFileJarUrls.put("tagfile:" + tagFile, tagFileURL);
    }

    public URL getTagFileJarUrl() {
        return this.tagFileJarUrl;
    }

    public void setTagFileUrls(JspCompilationContext ctxt) {
        this.tagFileJarUrls.putAll(ctxt.tagFileJarUrls);
    }

    public String getServletClassName() {
        if (this.className != null) {
            return this.className;
        }
        if (this.isTagFile) {
            this.className = this.tagInfo.getTagClassName();
            int lastIndex = this.className.lastIndexOf(46);
            if (lastIndex != -1) {
                this.className = this.className.substring(lastIndex + 1);
            }
        } else {
            int iSep = this.jspUri.lastIndexOf(47) + 1;
            this.className = JavaEscapeHelper.getJavaIdentifier((String)this.jspUri.substring(iSep));
        }
        return this.className;
    }

    public String getJspFile() {
        return this.jspUri;
    }

    public boolean isErrorPage() {
        return this.isErrPage;
    }

    public void setErrorPage(boolean isErrPage) {
        this.isErrPage = isErrPage;
    }

    public boolean isTagFile() {
        return this.isTagFile;
    }

    public TagInfo getTagInfo() {
        return this.tagInfo;
    }

    public void setTagInfo(TagInfo tagi) {
        this.tagInfo = tagi;
    }

    public boolean isPrototypeMode() {
        return this.protoTypeMode;
    }

    public void setPrototypeMode(boolean pm) {
        this.protoTypeMode = pm;
    }

    public String getServletPackageName() {
        if (this.isTagFile()) {
            String className = this.tagInfo.getTagClassName();
            int lastIndex = className.lastIndexOf(46);
            String pkgName = "";
            if (lastIndex != -1) {
                pkgName = className.substring(0, lastIndex);
            }
            return pkgName;
        }
        if (this.basePackageName == null || this.basePackageName.length() == 0) {
            return this.getDerivedPackageName();
        }
        String dPackageName = this.getDerivedPackageName();
        if (dPackageName.length() == 0) {
            return this.basePackageName;
        }
        return this.basePackageName + '.' + this.getDerivedPackageName();
    }

    protected String getDerivedPackageName() {
        if (this.derivedPackageName == null) {
            int iSep = this.jspUri.lastIndexOf(47);
            this.derivedPackageName = iSep > 0 ? JavaEscapeHelper.makeJavaPackage((String)this.jspUri.substring(1, iSep)) : "";
        }
        return this.derivedPackageName;
    }

    public String getServletJavaFileName() {
        if (this.servletJavaFileName == null) {
            this.servletJavaFileName = this.getOutputDir() + this.getServletClassName() + ".java";
        }
        return this.servletJavaFileName;
    }

    public Options getOptions() {
        return this.options;
    }

    public ServletContext getServletContext() {
        return this.context;
    }

    public JspRuntimeContext getRuntimeContext() {
        return this.rctxt;
    }

    public String getJavaPath() {
        if (this.javaPath != null) {
            return this.javaPath;
        }
        if (this.isTagFile()) {
            String tagName = this.tagInfo.getTagClassName();
            this.javaPath = tagName.replace('.', '/') + ".java";
        } else {
            this.javaPath = this.getServletPackageName().replace('.', '/') + '/' + this.getServletClassName() + ".java";
        }
        return this.javaPath;
    }

    public String getClassFileName() {
        if (this.classFileName == null) {
            this.classFileName = this.getOutputDir() + this.getServletClassName() + ".class";
        }
        return this.classFileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ServletWriter getWriter() {
        return this.writer;
    }

    public void setWriter(ServletWriter writer) {
        this.writer = writer;
    }

    public String[] getTldLocation(String uri) throws JasperException {
        String[] location = this.getOptions().getTldLocationsCache().getLocation(uri);
        return location;
    }

    public boolean keepGenerated() {
        return this.getOptions().getKeepGenerated();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JasperException compile() {
        Compiler c = this.createCompiler();
        try {
            c.removeGeneratedFiles();
            c.compile(true, false);
        }
        catch (JasperException ex) {
            JasperException jasperException = ex;
            return jasperException;
        }
        catch (IOException ioe) {
            JasperException je;
            JasperException jasperException = je = new JasperException(Localizer.getMessage("jsp.error.unable.compile"), ioe);
            return jasperException;
        }
        catch (Exception ex) {
            JasperException je;
            JasperException jasperException = je = new JasperException(Localizer.getMessage("jsp.error.unable.compile"), ex);
            return jasperException;
        }
        finally {
            c.clean();
        }
        return null;
    }

    public String getClassName() {
        String name = this.isTagFile() ? this.tagInfo.getTagClassName() : this.getServletPackageName() + "." + this.getServletClassName();
        return name;
    }

    public Class<?> load() throws JasperException {
        try {
            String name = this.getClassName();
            Class<?> servletClass = this.getClassLoader().loadClass(name);
            return servletClass;
        }
        catch (ClassNotFoundException cex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.load"), cex);
        }
        catch (Exception ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"), ex);
        }
    }

    public void checkOutputDir() {
        this.getOutputDir();
    }

    private boolean makeOutputDir() {
        return this.getRuntimeContext().getIOProvider().mkdirs(this.outputDir);
    }

    private void createOutputDir() {
        String path = null;
        if (this.isTagFile()) {
            String tagName = this.tagInfo.getTagClassName();
            path = tagName.replace('.', '/');
            path = path.substring(0, path.lastIndexOf(47));
        } else {
            path = this.getServletPackageName().replace('.', '/');
        }
        this.outputDir = this.options.getScratchDir() + File.separator + path + File.separator;
        if (!this.makeOutputDir()) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.outputfolder"));
        }
    }

    private static final boolean isPathSeparator(char c) {
        return c == '/' || c == '\\';
    }

    private static final String canonicalURI(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        int len = s.length();
        int pos = 0;
        block4: while (pos < len) {
            char c = s.charAt(pos);
            if (JspCompilationContext.isPathSeparator(c)) {
                while (pos + 1 < len && JspCompilationContext.isPathSeparator(s.charAt(pos + 1))) {
                    ++pos;
                }
                if (pos + 1 < len && s.charAt(pos + 1) == '.') {
                    if (pos + 2 >= len) break;
                    switch (s.charAt(pos + 2)) {
                        case '/': 
                        case '\\': {
                            pos += 2;
                            continue block4;
                        }
                        case '.': {
                            int separatorPos;
                            if (pos + 3 >= len || !JspCompilationContext.isPathSeparator(s.charAt(pos + 3))) break;
                            pos += 3;
                            for (separatorPos = result.length() - 1; separatorPos >= 0 && !JspCompilationContext.isPathSeparator(result.charAt(separatorPos)); --separatorPos) {
                            }
                            if (separatorPos < 0) continue block4;
                            result.setLength(separatorPos);
                            continue block4;
                        }
                    }
                }
            }
            result.append(c);
            ++pos;
        }
        return result.toString();
    }
}

