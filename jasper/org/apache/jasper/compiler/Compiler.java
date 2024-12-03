/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.scan.JarFactory
 */
package org.apache.jasper.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.TrimSpacesOption;
import org.apache.jasper.compiler.BeanRepository;
import org.apache.jasper.compiler.Collector;
import org.apache.jasper.compiler.ELFunctionMapper;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Generator;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.NewlineReductionServletWriter;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.PageInfo;
import org.apache.jasper.compiler.ParserController;
import org.apache.jasper.compiler.ScriptingVariabler;
import org.apache.jasper.compiler.ServletWriter;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.compiler.SmapUtil;
import org.apache.jasper.compiler.TagFileProcessor;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.TextOptimizer;
import org.apache.jasper.compiler.Validator;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.scan.JarFactory;

public abstract class Compiler {
    private final Log log = LogFactory.getLog(Compiler.class);
    protected JspCompilationContext ctxt;
    protected ErrorDispatcher errDispatcher;
    protected PageInfo pageInfo;
    protected JspServletWrapper jsw;
    protected TagFileProcessor tfp;
    protected Options options;
    protected Node.Nodes pageNodes;

    public void init(JspCompilationContext ctxt, JspServletWrapper jsw) {
        this.jsw = jsw;
        this.ctxt = ctxt;
        this.options = ctxt.getOptions();
    }

    public SmapStratum getSmap(String className) {
        Map<String, SmapStratum> smaps = this.ctxt.getRuntimeContext().getSmaps();
        SmapStratum smap = smaps.get(className);
        if (smap == null && !this.options.isSmapSuppressed() && (smap = SmapUtil.loadSmap(className, this.ctxt.getJspLoader())) != null) {
            smaps.put(className, smap);
        }
        return smap;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected Map<String, SmapStratum> generateJava() throws Exception {
        long t4 = 0L;
        long t3 = 0L;
        long t2 = 0L;
        long t1 = 0L;
        if (this.log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }
        this.pageInfo = new PageInfo(new BeanRepository(this.ctxt.getClassLoader(), this.errDispatcher), this.ctxt.getJspFile(), this.ctxt.isTagFile());
        JspConfig jspConfig = this.options.getJspConfig();
        JspConfig.JspProperty jspProperty = jspConfig.findJspProperty(this.ctxt.getJspFile());
        if (jspProperty.isELIgnored() != null) {
            this.pageInfo.setELIgnored(JspUtil.booleanValue(jspProperty.isELIgnored()));
        }
        if (jspProperty.isScriptingInvalid() != null) {
            this.pageInfo.setScriptingInvalid(JspUtil.booleanValue(jspProperty.isScriptingInvalid()));
        }
        if (jspProperty.getIncludePrelude() != null) {
            this.pageInfo.setIncludePrelude(jspProperty.getIncludePrelude());
        }
        if (jspProperty.getIncludeCoda() != null) {
            this.pageInfo.setIncludeCoda(jspProperty.getIncludeCoda());
        }
        if (jspProperty.isDeferedSyntaxAllowedAsLiteral() != null) {
            this.pageInfo.setDeferredSyntaxAllowedAsLiteral(JspUtil.booleanValue(jspProperty.isDeferedSyntaxAllowedAsLiteral()));
        }
        if (jspProperty.isTrimDirectiveWhitespaces() != null) {
            this.pageInfo.setTrimDirectiveWhitespaces(JspUtil.booleanValue(jspProperty.isTrimDirectiveWhitespaces()));
        }
        if (jspProperty.getBuffer() != null) {
            this.pageInfo.setBufferValue(jspProperty.getBuffer(), null, this.errDispatcher);
        }
        if (jspProperty.isErrorOnUndeclaredNamespace() != null) {
            this.pageInfo.setErrorOnUndeclaredNamespace(JspUtil.booleanValue(jspProperty.isErrorOnUndeclaredNamespace()));
        }
        if (this.ctxt.isTagFile()) {
            try {
                double libraryVersion = Double.parseDouble(this.ctxt.getTagInfo().getTagLibrary().getRequiredVersion());
                if (libraryVersion < 2.0) {
                    this.pageInfo.setIsELIgnored("true", null, this.errDispatcher, true);
                }
                if (libraryVersion < 2.1) {
                    this.pageInfo.setDeferredSyntaxAllowedAsLiteral("true", null, this.errDispatcher, true);
                }
            }
            catch (NumberFormatException ex) {
                this.errDispatcher.jspError(ex);
            }
        }
        this.ctxt.checkOutputDir();
        String javaFileName = this.ctxt.getServletJavaFileName();
        try {
            ParserController parserCtl = new ParserController(this.ctxt, this);
            Node.Nodes directives = parserCtl.parseDirectives(this.ctxt.getJspFile());
            Validator.validateDirectives(this, directives);
            this.pageNodes = parserCtl.parse(this.ctxt.getJspFile());
            if (this.pageInfo.getContentType() == null && jspProperty.getDefaultContentType() != null) {
                this.pageInfo.setContentType(jspProperty.getDefaultContentType());
            }
            if (this.ctxt.isPrototypeMode()) {
                try (ServletWriter writer = this.setupContextWriter(javaFileName);){
                    Generator.generate(writer, this, this.pageNodes);
                    Map<String, SmapStratum> map = null;
                    return map;
                }
            }
            Validator.validateExDirectives(this, this.pageNodes);
            if (this.log.isDebugEnabled()) {
                t2 = System.currentTimeMillis();
            }
            Collector.collect(this, this.pageNodes);
            this.tfp = new TagFileProcessor();
            this.tfp.loadTagFiles(this, this.pageNodes);
            if (this.log.isDebugEnabled()) {
                t3 = System.currentTimeMillis();
            }
            ScriptingVariabler.set(this.pageNodes, this.errDispatcher);
            TagPluginManager tagPluginManager = this.options.getTagPluginManager();
            tagPluginManager.apply(this.pageNodes, this.errDispatcher, this.pageInfo);
            TextOptimizer.concatenate(this, this.pageNodes);
            ELFunctionMapper.map(this.pageNodes);
            try (ServletWriter writer = this.setupContextWriter(javaFileName);){
                Generator.generate(writer, this, this.pageNodes);
            }
            this.ctxt.setWriter(null);
            if (this.log.isDebugEnabled()) {
                t4 = System.currentTimeMillis();
                this.log.debug((Object)("Generated " + javaFileName + " total=" + (t4 - t1) + " generate=" + (t4 - t3) + " validate=" + (t2 - t1)));
            }
        }
        catch (RuntimeException e) {
            File file = new File(javaFileName);
            if (!file.exists()) throw e;
            if (file.delete()) throw e;
            this.log.warn((Object)Localizer.getMessage("jsp.warning.compiler.javafile.delete.fail", file.getAbsolutePath()));
            throw e;
        }
        Map<String, SmapStratum> smaps = null;
        if (!this.options.isSmapSuppressed()) {
            smaps = SmapUtil.generateSmap(this.ctxt, this.pageNodes);
            this.ctxt.getRuntimeContext().getSmaps().putAll(smaps);
        }
        this.tfp.removeProtoTypeFiles(this.ctxt.getClassFileName());
        return smaps;
    }

    private ServletWriter setupContextWriter(String javaFileName) throws FileNotFoundException, JasperException {
        String javaEncoding = this.ctxt.getOptions().getJavaEncoding();
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter((OutputStream)new FileOutputStream(javaFileName), javaEncoding);
        }
        catch (UnsupportedEncodingException ex) {
            this.errDispatcher.jspError("jsp.error.needAlternateJavaEncoding", javaEncoding);
        }
        ServletWriter writer = this.ctxt.getOptions().getTrimSpaces().equals((Object)TrimSpacesOption.EXTENDED) ? new NewlineReductionServletWriter(new PrintWriter(osw)) : new ServletWriter(new PrintWriter(osw));
        this.ctxt.setWriter(writer);
        return writer;
    }

    protected abstract void generateClass(Map<String, SmapStratum> var1) throws FileNotFoundException, JasperException, Exception;

    public void compile() throws FileNotFoundException, JasperException, Exception {
        this.compile(true);
    }

    public void compile(boolean compileClass) throws FileNotFoundException, JasperException, Exception {
        this.compile(compileClass, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void compile(boolean compileClass, boolean jspcMode) throws FileNotFoundException, JasperException, Exception {
        if (this.errDispatcher == null) {
            this.errDispatcher = new ErrorDispatcher(jspcMode);
        }
        try {
            Long jspLastModified = this.ctxt.getLastModified(this.ctxt.getJspFile());
            Map<String, SmapStratum> smaps = this.generateJava();
            File javaFile = new File(this.ctxt.getServletJavaFileName());
            if (!javaFile.setLastModified(jspLastModified)) {
                throw new JasperException(Localizer.getMessage("jsp.error.setLastModified", javaFile));
            }
            if (compileClass) {
                this.generateClass(smaps);
                File targetFile = new File(this.ctxt.getClassFileName());
                if (targetFile.exists()) {
                    if (!targetFile.setLastModified(jspLastModified)) {
                        throw new JasperException(Localizer.getMessage("jsp.error.setLastModified", targetFile));
                    }
                    if (this.jsw != null) {
                        this.jsw.setServletClassLastModifiedTime(jspLastModified);
                    }
                }
            }
        }
        finally {
            if (this.tfp != null && this.ctxt.isPrototypeMode()) {
                this.tfp.removeProtoTypeFiles(null);
            }
            this.tfp = null;
            this.errDispatcher = null;
            this.pageInfo = null;
            this.pageNodes = null;
            if (this.ctxt.getWriter() != null) {
                this.ctxt.getWriter().close();
                this.ctxt.setWriter(null);
            }
        }
    }

    public boolean isOutDated() {
        return this.isOutDated(true);
    }

    public boolean isOutDated(boolean checkClass) {
        Long jspRealLastModified;
        File targetFile;
        if (this.jsw != null && this.ctxt.getOptions().getModificationTestInterval() > 0) {
            if (this.jsw.getLastModificationTest() + (long)(this.ctxt.getOptions().getModificationTestInterval() * 1000) > System.currentTimeMillis()) {
                return false;
            }
            this.jsw.setLastModificationTest(System.currentTimeMillis());
        }
        if (!(targetFile = checkClass ? new File(this.ctxt.getClassFileName()) : new File(this.ctxt.getServletJavaFileName())).exists()) {
            return true;
        }
        long targetLastModified = targetFile.lastModified();
        if (checkClass && this.jsw != null) {
            this.jsw.setServletClassLastModifiedTime(targetLastModified);
        }
        if ((jspRealLastModified = this.ctxt.getLastModified(this.ctxt.getJspFile())) < 0L) {
            return true;
        }
        if (targetLastModified != jspRealLastModified) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Compiler: outdated: " + targetFile + " " + targetLastModified));
            }
            return true;
        }
        if (this.jsw == null) {
            return false;
        }
        Map<String, Long> depends = this.jsw.getDependants();
        if (depends == null) {
            return false;
        }
        for (Map.Entry<String, Long> include : depends.entrySet()) {
            try {
                String key = include.getKey();
                long includeLastModified = 0L;
                if (key.startsWith("jar:jar:")) {
                    int entryStart = key.lastIndexOf("!/");
                    String entry = key.substring(entryStart + 2);
                    try (Jar jar = JarFactory.newInstance((URL)new URI(key.substring(4, entryStart)).toURL());){
                        includeLastModified = jar.getLastModified(entry);
                    }
                } else {
                    URL includeUrl = key.startsWith("jar:") || key.startsWith("file:") ? new URI(key).toURL() : this.ctxt.getResource(include.getKey());
                    if (includeUrl == null) {
                        return true;
                    }
                    URLConnection iuc = includeUrl.openConnection();
                    includeLastModified = iuc instanceof JarURLConnection ? ((JarURLConnection)iuc).getJarEntry().getTime() : iuc.getLastModified();
                    iuc.getInputStream().close();
                }
                if (includeLastModified == include.getValue()) continue;
                return true;
            }
            catch (Exception e) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Problem accessing resource. Treat as outdated.", (Throwable)e);
                }
                return true;
            }
        }
        return false;
    }

    public ErrorDispatcher getErrorDispatcher() {
        return this.errDispatcher;
    }

    public PageInfo getPageInfo() {
        return this.pageInfo;
    }

    public JspCompilationContext getCompilationContext() {
        return this.ctxt;
    }

    public void removeGeneratedFiles() {
        this.removeGeneratedClassFiles();
        try {
            File javaFile = new File(this.ctxt.getServletJavaFileName());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Deleting " + javaFile));
            }
            if (javaFile.exists() && !javaFile.delete()) {
                this.log.warn((Object)Localizer.getMessage("jsp.warning.compiler.javafile.delete.fail", javaFile.getAbsolutePath()));
            }
        }
        catch (Exception e) {
            this.log.warn((Object)Localizer.getMessage("jsp.warning.compiler.classfile.delete.fail.unknown"), (Throwable)e);
        }
    }

    public void removeGeneratedClassFiles() {
        try {
            File classFile = new File(this.ctxt.getClassFileName());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Deleting " + classFile));
            }
            if (classFile.exists() && !classFile.delete()) {
                this.log.warn((Object)Localizer.getMessage("jsp.warning.compiler.classfile.delete.fail", classFile.getAbsolutePath()));
            }
        }
        catch (Exception e) {
            this.log.warn((Object)Localizer.getMessage("jsp.warning.compiler.classfile.delete.fail.unknown"), (Throwable)e);
        }
    }
}

