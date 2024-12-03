/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.tagext.TagInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.descriptor.tld.TldResourcePath
 */
package org.apache.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Set;
import java.util.jar.JarEntry;
import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.TagInfo;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.ServletWriter;
import org.apache.jasper.servlet.JasperLoader;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;

public class JspCompilationContext {
    private final Log log = LogFactory.getLog(JspCompilationContext.class);
    private String className;
    private final String jspUri;
    private String basePackageName;
    private String derivedPackageName;
    private String servletJavaFileName;
    private String javaPath;
    private String classFileName;
    private ServletWriter writer;
    private final Options options;
    private final JspServletWrapper jsw;
    private Compiler jspCompiler;
    private String classPath;
    private final String baseURI;
    private String outputDir;
    private final ServletContext context;
    private ClassLoader loader;
    private final JspRuntimeContext rctxt;
    private volatile boolean removed = false;
    private volatile URLClassLoader jspLoader;
    private URL baseUrl;
    private Class<?> servletClass;
    private final boolean isTagFile;
    private boolean protoTypeMode;
    private TagInfo tagInfo;
    private Jar tagJar;
    private static final Object outputDirLock = new Object();

    public JspCompilationContext(String jspUri, Options options, ServletContext context, JspServletWrapper jsw, JspRuntimeContext rctxt) {
        this(jspUri, null, options, context, jsw, rctxt, null, false);
    }

    public JspCompilationContext(String tagfile, TagInfo tagInfo, Options options, ServletContext context, JspServletWrapper jsw, JspRuntimeContext rctxt, Jar tagJar) {
        this(tagfile, tagInfo, options, context, jsw, rctxt, tagJar, true);
    }

    private JspCompilationContext(String jspUri, TagInfo tagInfo, Options options, ServletContext context, JspServletWrapper jsw, JspRuntimeContext rctxt, Jar tagJar, boolean isTagFile) {
        this.jspUri = JspCompilationContext.canonicalURI(jspUri);
        this.options = options;
        this.jsw = jsw;
        this.context = context;
        String baseURI = jspUri.substring(0, jspUri.lastIndexOf(47) + 1);
        if (baseURI.isEmpty()) {
            baseURI = "/";
        } else if (baseURI.charAt(0) != '/') {
            baseURI = "/" + baseURI;
        }
        if (baseURI.charAt(baseURI.length() - 1) != '/') {
            baseURI = baseURI + '/';
        }
        this.baseURI = baseURI;
        this.rctxt = rctxt;
        this.basePackageName = Constants.JSP_PACKAGE_NAME;
        this.tagInfo = tagInfo;
        this.tagJar = tagJar;
        this.isTagFile = isTagFile;
    }

    public String getClassPath() {
        if (this.classPath != null) {
            return this.classPath;
        }
        return this.rctxt.getClassPath();
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public ClassLoader getClassLoader() {
        if (this.loader != null) {
            return this.loader;
        }
        return this.rctxt.getParentClassLoader();
    }

    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public ClassLoader getJspLoader() {
        if (this.jspLoader == null) {
            this.jspLoader = new JasperLoader(new URL[]{this.baseUrl}, this.getClassLoader(), this.rctxt.getPermissionCollection());
        }
        return this.jspLoader;
    }

    public void clearJspLoader() {
        this.jspLoader = null;
    }

    public String getOutputDir() {
        if (this.outputDir == null) {
            this.createOutputDir();
        }
        return this.outputDir;
    }

    public Compiler createCompiler() {
        if (this.jspCompiler != null) {
            return this.jspCompiler;
        }
        this.jspCompiler = null;
        if (this.options.getCompilerClassName() != null) {
            this.jspCompiler = this.createCompiler(this.options.getCompilerClassName());
        } else if (this.options.getCompiler() == null) {
            this.jspCompiler = this.createCompiler("org.apache.jasper.compiler.JDTCompiler");
            if (this.jspCompiler == null) {
                this.jspCompiler = this.createCompiler("org.apache.jasper.compiler.AntCompiler");
            }
        } else {
            this.jspCompiler = this.createCompiler("org.apache.jasper.compiler.AntCompiler");
            if (this.jspCompiler == null) {
                this.jspCompiler = this.createCompiler("org.apache.jasper.compiler.JDTCompiler");
            }
        }
        if (this.jspCompiler == null) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.compiler.config", this.options.getCompilerClassName(), this.options.getCompiler()));
        }
        this.jspCompiler.init(this, this.jsw);
        return this.jspCompiler;
    }

    protected Compiler createCompiler(String className) {
        Compiler compiler = null;
        try {
            compiler = (Compiler)Class.forName(className).getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.error.compiler"), e);
            }
        }
        catch (ReflectiveOperationException e) {
            this.log.warn((Object)Localizer.getMessage("jsp.error.compiler"), (Throwable)e);
        }
        return compiler;
    }

    public Compiler getCompiler() {
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

    public Jar getTagFileJar() {
        return this.tagJar;
    }

    public void setTagFileJar(Jar tagJar) {
        this.tagJar = tagJar;
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
            this.className = JspUtil.makeJavaIdentifier(this.jspUri.substring(iSep));
        }
        return this.className;
    }

    public void setServletClassName(String className) {
        this.className = className;
    }

    public String getJspFile() {
        return this.jspUri;
    }

    public Long getLastModified(String resource) {
        return this.getLastModified(resource, this.tagJar);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Long getLastModified(String resource, Jar tagJar) {
        URLConnection uc;
        long result;
        block21: {
            URL jspUrl;
            block22: {
                result = -1L;
                uc = null;
                if (tagJar != null) {
                    if (resource.startsWith("/")) {
                        resource = resource.substring(1);
                    }
                    result = tagJar.getLastModified(resource);
                    break block21;
                }
                jspUrl = this.getResource(resource);
                if (jspUrl != null) break block22;
                this.incrementRemoved();
                Long l = result;
                if (uc == null) return l;
                try {
                    uc.getInputStream().close();
                    return l;
                }
                catch (IOException e) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)Localizer.getMessage("jsp.error.lastModified", this.getJspFile()), (Throwable)e);
                    }
                    result = -1L;
                }
                return l;
            }
            uc = jspUrl.openConnection();
            if (uc instanceof JarURLConnection) {
                JarEntry jarEntry = ((JarURLConnection)uc).getJarEntry();
                result = jarEntry != null ? jarEntry.getTime() : uc.getLastModified();
                break block21;
            }
            result = uc.getLastModified();
        }
        if (uc == null) return result;
        try {
            uc.getInputStream().close();
            return result;
        }
        catch (IOException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.error.lastModified", this.getJspFile()), (Throwable)e);
            }
            result = -1L;
        }
        return result;
        catch (IOException e) {
            try {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)Localizer.getMessage("jsp.error.lastModified", this.getJspFile()), (Throwable)e);
                }
                result = -1L;
                if (uc == null) return result;
            }
            catch (Throwable throwable) {
                if (uc == null) throw throwable;
                try {
                    uc.getInputStream().close();
                    throw throwable;
                }
                catch (IOException e2) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)Localizer.getMessage("jsp.error.lastModified", this.getJspFile()), (Throwable)e2);
                    }
                    result = -1L;
                }
                throw throwable;
            }
            try {
                uc.getInputStream().close();
                return result;
            }
            catch (IOException e3) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)Localizer.getMessage("jsp.error.lastModified", this.getJspFile()), (Throwable)e3);
                }
                result = -1L;
            }
            return result;
        }
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
            String packageName = "";
            if (lastIndex != -1) {
                packageName = className.substring(0, lastIndex);
            }
            return packageName;
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
            this.derivedPackageName = iSep > 0 ? JspUtil.makeJavaPackage(this.jspUri.substring(1, iSep)) : "";
        }
        return this.derivedPackageName;
    }

    public String getBasePackageName() {
        return this.basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        this.basePackageName = basePackageName;
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

    public ServletWriter getWriter() {
        return this.writer;
    }

    public void setWriter(ServletWriter writer) {
        this.writer = writer;
    }

    public TldResourcePath getTldResourcePath(String uri) {
        return this.getOptions().getTldCache().getTldResourcePath(uri);
    }

    public boolean keepGenerated() {
        return this.getOptions().getKeepGenerated();
    }

    public void incrementRemoved() {
        if (!this.removed && this.rctxt != null) {
            this.rctxt.removeWrapper(this.jspUri);
        }
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public void compile() throws JasperException, FileNotFoundException {
        this.createCompiler();
        if (this.jspCompiler.isOutDated()) {
            if (this.isRemoved()) {
                throw new FileNotFoundException(this.jspUri);
            }
            try {
                this.jspCompiler.removeGeneratedFiles();
                this.jspLoader = null;
                this.jspCompiler.compile();
                this.jsw.setReload(true);
                this.jsw.setCompilationException(null);
            }
            catch (JasperException ex) {
                this.jsw.setCompilationException(ex);
                if (this.options.getDevelopment() && this.options.getRecompileOnFail()) {
                    this.jsw.setLastModificationTest(-1L);
                }
                throw ex;
            }
            catch (FileNotFoundException fnfe) {
                throw fnfe;
            }
            catch (Exception ex) {
                JasperException je = new JasperException(Localizer.getMessage("jsp.error.unable.compile"), ex);
                this.jsw.setCompilationException(je);
                throw je;
            }
        }
    }

    public Class<?> load() throws JasperException {
        try {
            this.getJspLoader();
            String name = this.getFQCN();
            this.servletClass = this.jspLoader.loadClass(name);
        }
        catch (ClassNotFoundException cex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.load"), cex);
        }
        catch (Exception ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"), ex);
        }
        this.removed = false;
        return this.servletClass;
    }

    public String getFQCN() {
        String name = this.isTagFile() ? this.tagInfo.getTagClassName() : this.getServletPackageName() + "." + this.getServletClassName();
        return name;
    }

    public void checkOutputDir() {
        if (this.outputDir != null) {
            if (!new File(this.outputDir).exists()) {
                this.makeOutputDir();
            }
        } else {
            this.createOutputDir();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean makeOutputDir() {
        Object object = outputDirLock;
        synchronized (object) {
            File outDirFile = new File(this.outputDir);
            return outDirFile.mkdirs() || outDirFile.isDirectory();
        }
    }

    protected void createOutputDir() {
        String path = null;
        if (this.isTagFile()) {
            String tagName = this.tagInfo.getTagClassName();
            path = tagName.replace('.', File.separatorChar);
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
        } else {
            path = this.getServletPackageName().replace('.', File.separatorChar);
        }
        try {
            File base = this.options.getScratchDir();
            this.baseUrl = base.toURI().toURL();
            this.outputDir = base.getAbsolutePath() + File.separator + path + File.separator;
            if (!this.makeOutputDir()) {
                this.log.error((Object)Localizer.getMessage("jsp.error.outputfolder.detail", this.outputDir));
                throw new IllegalStateException(Localizer.getMessage("jsp.error.outputfolder"));
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.outputfolder"), e);
        }
    }

    protected static final boolean isPathSeparator(char c) {
        return c == '/' || c == '\\';
    }

    protected static final String canonicalURI(String s) {
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

