/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.tagext.TagLibraryInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tools.ant.AntClassLoader
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.util.FileUtils
 */
package org.apache.jasper;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.TrimSpacesOption;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.TldCache;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.jasper.servlet.JspCServletContext;
import org.apache.jasper.servlet.TldScanner;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.SAXException;

public class JspC
extends Task
implements Options {
    @Deprecated
    public static final String DEFAULT_IE_CLASS_ID = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
    private static final Log log;
    protected static final String SWITCH_VERBOSE = "-v";
    protected static final String SWITCH_HELP = "-help";
    protected static final String SWITCH_OUTPUT_DIR = "-d";
    protected static final String SWITCH_PACKAGE_NAME = "-p";
    protected static final String SWITCH_CACHE = "-cache";
    protected static final String SWITCH_CLASS_NAME = "-c";
    protected static final String SWITCH_FULL_STOP = "--";
    protected static final String SWITCH_COMPILE = "-compile";
    protected static final String SWITCH_FAIL_FAST = "-failFast";
    protected static final String SWITCH_SOURCE = "-source";
    protected static final String SWITCH_TARGET = "-target";
    protected static final String SWITCH_URI_BASE = "-uribase";
    protected static final String SWITCH_URI_ROOT = "-uriroot";
    protected static final String SWITCH_FILE_WEBAPP = "-webapp";
    protected static final String SWITCH_WEBAPP_INC = "-webinc";
    protected static final String SWITCH_WEBAPP_FRG = "-webfrg";
    protected static final String SWITCH_WEBAPP_XML = "-webxml";
    protected static final String SWITCH_WEBAPP_XML_ENCODING = "-webxmlencoding";
    protected static final String SWITCH_ADD_WEBAPP_XML_MAPPINGS = "-addwebxmlmappings";
    protected static final String SWITCH_MAPPED = "-mapped";
    protected static final String SWITCH_XPOWERED_BY = "-xpoweredBy";
    protected static final String SWITCH_TRIM_SPACES = "-trimSpaces";
    protected static final String SWITCH_CLASSPATH = "-classpath";
    protected static final String SWITCH_DIE = "-die";
    protected static final String SWITCH_POOLING = "-poolingEnabled";
    protected static final String SWITCH_ENCODING = "-javaEncoding";
    protected static final String SWITCH_SMAP = "-smap";
    protected static final String SWITCH_DUMP_SMAP = "-dumpsmap";
    protected static final String SWITCH_VALIDATE_TLD = "-validateTld";
    protected static final String SWITCH_VALIDATE_XML = "-validateXml";
    protected static final String SWITCH_NO_BLOCK_EXTERNAL = "-no-blockExternal";
    protected static final String SWITCH_NO_STRICT_QUOTE_ESCAPING = "-no-strictQuoteEscaping";
    protected static final String SWITCH_QUOTE_ATTRIBUTE_EL = "-quoteAttributeEL";
    protected static final String SWITCH_NO_QUOTE_ATTRIBUTE_EL = "-no-quoteAttributeEL";
    protected static final String SWITCH_THREAD_COUNT = "-threadCount";
    protected static final String SHOW_SUCCESS = "-s";
    protected static final String LIST_ERRORS = "-l";
    protected static final int INC_WEBXML = 10;
    protected static final int FRG_WEBXML = 15;
    protected static final int ALL_WEBXML = 20;
    protected static final int DEFAULT_DIE_LEVEL = 1;
    protected static final int NO_DIE_LEVEL = 0;
    protected static final Set<String> insertBefore;
    protected String classPath = null;
    protected ClassLoader loader = null;
    protected TrimSpacesOption trimSpaces = TrimSpacesOption.FALSE;
    protected boolean genStringAsCharArray = false;
    protected boolean validateTld;
    protected boolean validateXml;
    protected boolean blockExternal = true;
    protected boolean strictQuoteEscaping = true;
    protected boolean quoteAttributeEL = true;
    protected boolean xpoweredBy;
    protected boolean mappedFile = false;
    protected boolean poolingEnabled = true;
    protected File scratchDir;
    protected String ieClassId = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
    protected String targetPackage;
    protected String targetClassName;
    protected String uriBase;
    protected String uriRoot;
    protected int dieLevel;
    protected boolean helpNeeded = false;
    protected boolean compile = false;
    protected boolean failFast = false;
    protected boolean smapSuppressed = true;
    protected boolean smapDumped = false;
    protected boolean caching = true;
    protected final Map<String, TagLibraryInfo> cache = new HashMap<String, TagLibraryInfo>();
    protected String compiler = null;
    protected String compilerTargetVM = "1.8";
    protected String compilerSourceVM = "1.8";
    protected boolean classDebugInfo = true;
    protected boolean failOnError = true;
    private boolean fork = false;
    protected List<String> extensions;
    protected final List<String> pages = new ArrayList<String>();
    protected boolean errorOnUseBeanInvalidClassAttribute = true;
    protected String javaEncoding = "UTF-8";
    protected int threadCount = Runtime.getRuntime().availableProcessors();
    protected String webxmlFile;
    protected int webxmlLevel;
    protected String webxmlEncoding = "UTF-8";
    protected boolean addWebXmlMappings = false;
    protected Writer mapout;
    protected CharArrayWriter servletout;
    protected CharArrayWriter mappingout;
    protected JspCServletContext context;
    protected JspRuntimeContext rctxt;
    protected TldCache tldCache = null;
    protected JspConfig jspConfig = null;
    protected TagPluginManager tagPluginManager = null;
    protected TldScanner scanner = null;
    protected boolean verbose = false;
    protected boolean listErrors = false;
    protected boolean showSuccess = false;
    protected int argPos;
    protected boolean fullstop = false;
    protected String[] args;

    public static void main(String[] arg) {
        block6: {
            if (arg.length == 0) {
                System.out.println(Localizer.getMessage("jspc.usage"));
            } else {
                JspC jspc = new JspC();
                try {
                    jspc.setArgs(arg);
                    if (jspc.helpNeeded) {
                        System.out.println(Localizer.getMessage("jspc.usage"));
                    } else {
                        jspc.execute();
                    }
                }
                catch (JasperException | BuildException e) {
                    System.err.println(e);
                    if (jspc.dieLevel == 0) break block6;
                    System.exit(jspc.dieLevel);
                }
            }
        }
    }

    public void setArgs(String[] arg) throws JasperException {
        String file;
        String tok;
        this.args = arg;
        this.dieLevel = 0;
        while ((tok = this.nextArg()) != null) {
            if (tok.equals(SWITCH_VERBOSE)) {
                this.verbose = true;
                this.showSuccess = true;
                this.listErrors = true;
                continue;
            }
            if (tok.equals(SWITCH_OUTPUT_DIR)) {
                tok = this.nextArg();
                this.setOutputDir(tok);
                continue;
            }
            if (tok.equals(SWITCH_PACKAGE_NAME)) {
                this.targetPackage = this.nextArg();
                continue;
            }
            if (tok.equals(SWITCH_COMPILE)) {
                this.compile = true;
                continue;
            }
            if (tok.equals(SWITCH_FAIL_FAST)) {
                this.failFast = true;
                continue;
            }
            if (tok.equals(SWITCH_CLASS_NAME)) {
                this.targetClassName = this.nextArg();
                continue;
            }
            if (tok.equals(SWITCH_URI_BASE)) {
                this.uriBase = this.nextArg();
                continue;
            }
            if (tok.equals(SWITCH_URI_ROOT)) {
                this.setUriroot(this.nextArg());
                continue;
            }
            if (tok.equals(SWITCH_FILE_WEBAPP)) {
                this.setUriroot(this.nextArg());
                continue;
            }
            if (tok.equals(SHOW_SUCCESS)) {
                this.showSuccess = true;
                continue;
            }
            if (tok.equals(LIST_ERRORS)) {
                this.listErrors = true;
                continue;
            }
            if (tok.equals(SWITCH_WEBAPP_INC)) {
                this.webxmlFile = this.nextArg();
                if (this.webxmlFile == null) continue;
                this.webxmlLevel = 10;
                continue;
            }
            if (tok.equals(SWITCH_WEBAPP_FRG)) {
                this.webxmlFile = this.nextArg();
                if (this.webxmlFile == null) continue;
                this.webxmlLevel = 15;
                continue;
            }
            if (tok.equals(SWITCH_WEBAPP_XML)) {
                this.webxmlFile = this.nextArg();
                if (this.webxmlFile == null) continue;
                this.webxmlLevel = 20;
                continue;
            }
            if (tok.equals(SWITCH_WEBAPP_XML_ENCODING)) {
                this.setWebXmlEncoding(this.nextArg());
                continue;
            }
            if (tok.equals(SWITCH_ADD_WEBAPP_XML_MAPPINGS)) {
                this.setAddWebXmlMappings(true);
                continue;
            }
            if (tok.equals(SWITCH_MAPPED)) {
                this.mappedFile = true;
                continue;
            }
            if (tok.equals(SWITCH_XPOWERED_BY)) {
                this.xpoweredBy = true;
                continue;
            }
            if (tok.equals(SWITCH_TRIM_SPACES)) {
                tok = this.nextArg();
                if (TrimSpacesOption.SINGLE.toString().equalsIgnoreCase(tok)) {
                    this.setTrimSpaces(TrimSpacesOption.SINGLE);
                    continue;
                }
                this.setTrimSpaces(TrimSpacesOption.TRUE);
                --this.argPos;
                continue;
            }
            if (tok.equals(SWITCH_CACHE)) {
                tok = this.nextArg();
                if ("false".equals(tok)) {
                    this.caching = false;
                    continue;
                }
                this.caching = true;
                continue;
            }
            if (tok.equals(SWITCH_CLASSPATH)) {
                this.setClassPath(this.nextArg());
                continue;
            }
            if (tok.startsWith(SWITCH_DIE)) {
                try {
                    this.dieLevel = Integer.parseInt(tok.substring(SWITCH_DIE.length()));
                }
                catch (NumberFormatException nfe) {
                    this.dieLevel = 1;
                }
                continue;
            }
            if (tok.equals(SWITCH_HELP)) {
                this.helpNeeded = true;
                continue;
            }
            if (tok.equals(SWITCH_POOLING)) {
                tok = this.nextArg();
                if ("false".equals(tok)) {
                    this.poolingEnabled = false;
                    continue;
                }
                this.poolingEnabled = true;
                continue;
            }
            if (tok.equals(SWITCH_ENCODING)) {
                this.setJavaEncoding(this.nextArg());
                continue;
            }
            if (tok.equals(SWITCH_SOURCE)) {
                this.setCompilerSourceVM(this.nextArg());
                continue;
            }
            if (tok.equals(SWITCH_TARGET)) {
                this.setCompilerTargetVM(this.nextArg());
                continue;
            }
            if (tok.equals(SWITCH_SMAP)) {
                this.smapSuppressed = false;
                continue;
            }
            if (tok.equals(SWITCH_DUMP_SMAP)) {
                this.smapDumped = true;
                continue;
            }
            if (tok.equals(SWITCH_VALIDATE_TLD)) {
                this.setValidateTld(true);
                continue;
            }
            if (tok.equals(SWITCH_VALIDATE_XML)) {
                this.setValidateXml(true);
                continue;
            }
            if (tok.equals(SWITCH_NO_BLOCK_EXTERNAL)) {
                this.setBlockExternal(false);
                continue;
            }
            if (tok.equals(SWITCH_NO_STRICT_QUOTE_ESCAPING)) {
                this.setStrictQuoteEscaping(false);
                continue;
            }
            if (tok.equals(SWITCH_QUOTE_ATTRIBUTE_EL)) {
                this.setQuoteAttributeEL(true);
                continue;
            }
            if (tok.equals(SWITCH_NO_QUOTE_ATTRIBUTE_EL)) {
                this.setQuoteAttributeEL(false);
                continue;
            }
            if (tok.equals(SWITCH_THREAD_COUNT)) {
                this.setThreadCount(this.nextArg());
                continue;
            }
            if (tok.startsWith("-")) {
                throw new JasperException(Localizer.getMessage("jspc.error.unknownOption", tok));
            }
            if (this.fullstop) break;
            --this.argPos;
            break;
        }
        while ((file = this.nextFile()) != null) {
            this.pages.add(file);
        }
    }

    @Override
    public boolean getKeepGenerated() {
        return true;
    }

    @Override
    public TrimSpacesOption getTrimSpaces() {
        return this.trimSpaces;
    }

    public void setTrimSpaces(TrimSpacesOption trimSpaces) {
        this.trimSpaces = trimSpaces;
    }

    public void setTrimSpaces(String ts) {
        this.trimSpaces = TrimSpacesOption.valueOf(ts);
    }

    public void setTrimSpaces(boolean trimSpaces) {
        if (trimSpaces) {
            this.setTrimSpaces(TrimSpacesOption.TRUE);
        } else {
            this.setTrimSpaces(TrimSpacesOption.FALSE);
        }
    }

    @Override
    public boolean isPoolingEnabled() {
        return this.poolingEnabled;
    }

    public void setPoolingEnabled(boolean poolingEnabled) {
        this.poolingEnabled = poolingEnabled;
    }

    @Override
    public boolean isXpoweredBy() {
        return this.xpoweredBy;
    }

    public void setXpoweredBy(boolean xpoweredBy) {
        this.xpoweredBy = xpoweredBy;
    }

    @Override
    public boolean getDisplaySourceFragment() {
        return true;
    }

    @Override
    public int getMaxLoadedJsps() {
        return -1;
    }

    @Override
    public int getJspIdleTimeout() {
        return -1;
    }

    @Override
    public boolean getErrorOnUseBeanInvalidClassAttribute() {
        return this.errorOnUseBeanInvalidClassAttribute;
    }

    public void setErrorOnUseBeanInvalidClassAttribute(boolean b) {
        this.errorOnUseBeanInvalidClassAttribute = b;
    }

    @Override
    public boolean getMappedFile() {
        return this.mappedFile;
    }

    public void setMappedFile(boolean b) {
        this.mappedFile = b;
    }

    public void setClassDebugInfo(boolean b) {
        this.classDebugInfo = b;
    }

    @Override
    public boolean getClassDebugInfo() {
        return this.classDebugInfo;
    }

    @Override
    public boolean isCaching() {
        return this.caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    @Override
    public Map<String, TagLibraryInfo> getCache() {
        return this.cache;
    }

    @Override
    public int getCheckInterval() {
        return 0;
    }

    @Override
    public int getModificationTestInterval() {
        return 0;
    }

    @Override
    public boolean getRecompileOnFail() {
        return false;
    }

    @Override
    public boolean getDevelopment() {
        return false;
    }

    @Override
    public boolean isSmapSuppressed() {
        return this.smapSuppressed;
    }

    public void setSmapSuppressed(boolean smapSuppressed) {
        this.smapSuppressed = smapSuppressed;
    }

    @Override
    public boolean isSmapDumped() {
        return this.smapDumped;
    }

    public void setSmapDumped(boolean smapDumped) {
        this.smapDumped = smapDumped;
    }

    public void setGenStringAsCharArray(boolean genStringAsCharArray) {
        this.genStringAsCharArray = genStringAsCharArray;
    }

    @Override
    public boolean genStringAsCharArray() {
        return this.genStringAsCharArray;
    }

    @Deprecated
    public void setIeClassId(String ieClassId) {
        this.ieClassId = ieClassId;
    }

    @Override
    @Deprecated
    public String getIeClassId() {
        return this.ieClassId;
    }

    @Override
    public File getScratchDir() {
        return this.scratchDir;
    }

    @Override
    public String getCompiler() {
        return this.compiler;
    }

    public void setCompiler(String c) {
        this.compiler = c;
    }

    @Override
    public String getCompilerClassName() {
        return null;
    }

    @Override
    public String getCompilerTargetVM() {
        return this.compilerTargetVM;
    }

    public void setCompilerTargetVM(String vm) {
        this.compilerTargetVM = vm;
    }

    @Override
    public String getCompilerSourceVM() {
        return this.compilerSourceVM;
    }

    public void setCompilerSourceVM(String vm) {
        this.compilerSourceVM = vm;
    }

    @Override
    public TldCache getTldCache() {
        return this.tldCache;
    }

    @Override
    public String getJavaEncoding() {
        return this.javaEncoding;
    }

    public void setJavaEncoding(String encodingName) {
        this.javaEncoding = encodingName;
    }

    @Override
    public boolean getFork() {
        return this.fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    @Override
    public String getClassPath() {
        if (this.classPath != null) {
            return this.classPath;
        }
        return System.getProperty("java.class.path");
    }

    public void setClassPath(String s) {
        this.classPath = s;
    }

    public List<String> getExtensions() {
        return this.extensions;
    }

    protected void addExtension(String extension) {
        if (extension != null) {
            if (this.extensions == null) {
                this.extensions = new ArrayList<String>();
            }
            this.extensions.add(extension);
        }
    }

    public void setUriroot(String s) {
        if (s == null) {
            this.uriRoot = null;
            return;
        }
        try {
            this.uriRoot = this.resolveFile(s).getCanonicalPath();
        }
        catch (Exception ex) {
            this.uriRoot = s;
        }
    }

    public void setJspFiles(String jspFiles) {
        if (jspFiles == null) {
            return;
        }
        StringTokenizer tok = new StringTokenizer(jspFiles, ",");
        while (tok.hasMoreTokens()) {
            this.pages.add(tok.nextToken());
        }
    }

    public void setCompile(boolean b) {
        this.compile = b;
    }

    public void setVerbose(int level) {
        if (level > 0) {
            this.verbose = true;
            this.showSuccess = true;
            this.listErrors = true;
        }
    }

    public void setValidateTld(boolean b) {
        this.validateTld = b;
    }

    public boolean isValidateTld() {
        return this.validateTld;
    }

    public void setValidateXml(boolean b) {
        this.validateXml = b;
    }

    public boolean isValidateXml() {
        return this.validateXml;
    }

    public void setBlockExternal(boolean b) {
        this.blockExternal = b;
    }

    public boolean isBlockExternal() {
        return this.blockExternal;
    }

    public void setStrictQuoteEscaping(boolean b) {
        this.strictQuoteEscaping = b;
    }

    @Override
    public boolean getStrictQuoteEscaping() {
        return this.strictQuoteEscaping;
    }

    public void setQuoteAttributeEL(boolean b) {
        this.quoteAttributeEL = b;
    }

    @Override
    public boolean getQuoteAttributeEL() {
        return this.quoteAttributeEL;
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public void setThreadCount(String threadCount) {
        int newThreadCount;
        if (threadCount == null) {
            return;
        }
        try {
            if (threadCount.endsWith("C")) {
                double factor = Double.parseDouble(threadCount.substring(0, threadCount.length() - 1));
                newThreadCount = (int)(factor * (double)Runtime.getRuntime().availableProcessors());
            } else {
                newThreadCount = Integer.parseInt(threadCount);
            }
        }
        catch (NumberFormatException e) {
            throw new BuildException(Localizer.getMessage("jspc.error.parseThreadCount", threadCount));
        }
        if (newThreadCount < 1) {
            throw new BuildException(Localizer.getMessage("jspc.error.minThreadCount", newThreadCount));
        }
        this.threadCount = newThreadCount;
    }

    public void setListErrors(boolean b) {
        this.listErrors = b;
    }

    public void setOutputDir(String s) {
        this.scratchDir = s != null ? this.resolveFile(s).getAbsoluteFile() : null;
    }

    public void setPackage(String p) {
        this.targetPackage = p;
    }

    public void setClassName(String p) {
        this.targetClassName = p;
    }

    @Deprecated
    public void setWebXmlFragment(String s) {
        this.webxmlFile = this.resolveFile(s).getAbsolutePath();
        this.webxmlLevel = 10;
    }

    public void setWebXmlInclude(String s) {
        this.webxmlFile = this.resolveFile(s).getAbsolutePath();
        this.webxmlLevel = 10;
    }

    public void setWebFragmentXml(String s) {
        this.webxmlFile = this.resolveFile(s).getAbsolutePath();
        this.webxmlLevel = 15;
    }

    public void setWebXml(String s) {
        this.webxmlFile = this.resolveFile(s).getAbsolutePath();
        this.webxmlLevel = 20;
    }

    public void setWebXmlEncoding(String encoding) {
        this.webxmlEncoding = encoding;
    }

    public void setAddWebXmlMappings(boolean b) {
        this.addWebXmlMappings = b;
    }

    public void setFailOnError(boolean b) {
        this.failOnError = b;
    }

    public boolean getFailOnError() {
        return this.failOnError;
    }

    @Override
    public JspConfig getJspConfig() {
        return this.jspConfig;
    }

    @Override
    public TagPluginManager getTagPluginManager() {
        return this.tagPluginManager;
    }

    @Override
    public boolean getGeneratedJavaAddTimestamp() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generateWebMapping(String file, JspCompilationContext clctxt) throws IOException {
        CharArrayWriter charArrayWriter;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Generating web mapping for file " + file + " using compilation context " + clctxt));
        }
        String className = clctxt.getServletClassName();
        String packageName = clctxt.getServletPackageName();
        String thisServletName = packageName.isEmpty() ? className : packageName + '.' + className;
        if (this.servletout != null) {
            charArrayWriter = this.servletout;
            synchronized (charArrayWriter) {
                this.servletout.write("\n    <servlet>\n        <servlet-name>");
                this.servletout.write(thisServletName);
                this.servletout.write("</servlet-name>\n        <servlet-class>");
                this.servletout.write(thisServletName);
                this.servletout.write("</servlet-class>\n    </servlet>\n");
            }
        }
        if (this.mappingout != null) {
            charArrayWriter = this.mappingout;
            synchronized (charArrayWriter) {
                this.mappingout.write("\n    <servlet-mapping>\n        <servlet-name>");
                this.mappingout.write(thisServletName);
                this.mappingout.write("</servlet-name>\n        <url-pattern>");
                this.mappingout.write(file.replace('\\', '/'));
                this.mappingout.write("</url-pattern>\n    </servlet-mapping>\n");
            }
        }
    }

    protected void mergeIntoWebXml() throws IOException {
        File webappBase = new File(this.uriRoot);
        File webXml = new File(webappBase, "WEB-INF/web.xml");
        File webXml2 = new File(webappBase, "WEB-INF/web2.xml");
        String insertStartMarker = Localizer.getMessage("jspc.webinc.insertStart");
        String insertEndMarker = Localizer.getMessage("jspc.webinc.insertEnd");
        try (BufferedReader reader = new BufferedReader(this.openWebxmlReader(webXml));
             BufferedReader fragmentReader = new BufferedReader(this.openWebxmlReader(new File(this.webxmlFile)));
             PrintWriter writer = new PrintWriter(this.openWebxmlWriter(webXml2));){
            boolean inserted = false;
            int current = reader.read();
            while (current > -1) {
                if (current == 60) {
                    String element = this.getElement(reader);
                    if (!inserted && insertBefore.contains(element)) {
                        writer.println(insertStartMarker);
                        while (true) {
                            String line;
                            if ((line = fragmentReader.readLine()) == null) break;
                            writer.println(line);
                        }
                        writer.println();
                        writer.println(insertEndMarker);
                        writer.println();
                        writer.write(element);
                        inserted = true;
                    } else {
                        if (element.equals(insertStartMarker)) {
                            do {
                                if ((current = reader.read()) >= 0) continue;
                                throw new EOFException();
                            } while (current != 60 || !(element = this.getElement(reader)).equals(insertEndMarker));
                            current = reader.read();
                            while (current == 10 || current == 13) {
                                current = reader.read();
                            }
                            continue;
                        }
                        writer.write(element);
                    }
                } else {
                    writer.write(current);
                }
                current = reader.read();
            }
        }
        try (FileInputStream fis = new FileInputStream(webXml2);
             FileOutputStream fos = new FileOutputStream(webXml);){
            int n;
            byte[] buf = new byte[512];
            while ((n = fis.read(buf)) >= 0) {
                fos.write(buf, 0, n);
            }
        }
        if (!webXml2.delete() && log.isDebugEnabled()) {
            log.debug((Object)Localizer.getMessage("jspc.delete.fail", webXml2.toString()));
        }
        if (!new File(this.webxmlFile).delete() && log.isDebugEnabled()) {
            log.debug((Object)Localizer.getMessage("jspc.delete.fail", this.webxmlFile));
        }
    }

    private String getElement(Reader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append('<');
        boolean done = false;
        while (!done) {
            int current = reader.read();
            while (current != 62) {
                if (current < 0) {
                    throw new EOFException();
                }
                result.append((char)current);
                current = reader.read();
            }
            result.append((char)current);
            int len = result.length();
            if (len > 4 && result.substring(0, 4).equals("<!--")) {
                if (len < 7 || !result.substring(len - 3, len).equals("-->")) continue;
                done = true;
                continue;
            }
            done = true;
        }
        return result.toString();
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void processFile(String file) throws JasperException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Processing file: " + file));
        }
        ClassLoader originalClassLoader = null;
        Thread currentThread = Thread.currentThread();
        try {
            if (this.scratchDir == null) {
                String temp = System.getProperty("java.io.tmpdir");
                if (temp == null) {
                    temp = "";
                }
                this.scratchDir = new File(temp).getAbsoluteFile();
            }
            String jspUri = file.replace('\\', '/');
            JspCompilationContext clctxt = new JspCompilationContext(jspUri, this, this.context, null, this.rctxt);
            if (this.targetClassName != null && this.targetClassName.length() > 0) {
                clctxt.setServletClassName(this.targetClassName);
                this.targetClassName = null;
            }
            if (this.targetPackage != null) {
                clctxt.setBasePackageName(this.targetPackage);
            }
            originalClassLoader = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(this.loader);
            clctxt.setClassLoader(this.loader);
            clctxt.setClassPath(this.classPath);
            Compiler clc = clctxt.createCompiler();
            if (clc.isOutDated(this.compile)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)(jspUri + " is out dated, compiling..."));
                }
                clc.compile(this.compile, true);
            }
            this.generateWebMapping(file, clctxt);
            if (this.showSuccess) {
                log.info((Object)("Built File: " + file));
            }
            if (originalClassLoader == null) return;
            currentThread.setContextClassLoader(originalClassLoader);
            return;
        }
        catch (JasperException je) {
            try {
                Object rootCause = je;
                while (rootCause instanceof JasperException && rootCause.getRootCause() != null) {
                    rootCause = rootCause.getRootCause();
                }
                if (rootCause == je) throw je;
                log.error((Object)Localizer.getMessage("jspc.error.generalException", file), (Throwable)rootCause);
                throw je;
                catch (Exception e) {
                    if (!(e instanceof FileNotFoundException)) throw new JasperException(e);
                    if (!log.isWarnEnabled()) throw new JasperException(e);
                    log.warn((Object)Localizer.getMessage("jspc.error.fileDoesNotExist", e.getMessage()));
                    throw new JasperException(e);
                }
            }
            catch (Throwable throwable) {
                if (originalClassLoader == null) throw throwable;
                currentThread.setContextClassLoader(originalClassLoader);
                throw throwable;
            }
        }
    }

    @Deprecated
    public void scanFiles(File base) {
        this.scanFiles();
    }

    public void scanFiles() {
        if (this.getExtensions() == null || this.getExtensions().size() < 2) {
            this.addExtension("jsp");
            this.addExtension("jspx");
        }
        this.scanFilesInternal("/");
    }

    private void scanFilesInternal(String input) {
        Set<String> paths = this.context.getResourcePaths(input);
        for (String path : paths) {
            if (path.endsWith("/")) {
                this.scanFilesInternal(path);
                continue;
            }
            if (this.jspConfig.isJspPage(path)) {
                this.pages.add(path);
                continue;
            }
            String ext = path.substring(path.lastIndexOf(46) + 1);
            if (!this.extensions.contains(ext)) continue;
            this.pages.add(path);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("execute() starting for " + this.pages.size() + " pages."));
        }
        try {
            if (this.uriRoot == null) {
                if (this.pages.size() == 0) {
                    throw new JasperException(Localizer.getMessage("jsp.error.jspc.missingTarget"));
                }
                String firstJsp = this.pages.get(0);
                File firstJspF = new File(firstJsp);
                if (!firstJspF.exists()) {
                    throw new JasperException(Localizer.getMessage("jspc.error.fileDoesNotExist", firstJsp));
                }
                this.locateUriRoot(firstJspF);
            }
            if (this.uriRoot == null) {
                throw new JasperException(Localizer.getMessage("jsp.error.jspc.no_uriroot"));
            }
            File uriRootF = new File(this.uriRoot);
            if (!uriRootF.isDirectory()) {
                throw new JasperException(Localizer.getMessage("jsp.error.jspc.uriroot_not_dir"));
            }
            if (this.loader == null) {
                this.loader = this.initClassLoader();
            }
            if (this.context == null) {
                this.initServletContext(this.loader);
            }
            if (this.pages.size() == 0) {
                this.scanFiles();
            } else {
                for (int i = 0; i < this.pages.size(); ++i) {
                    String nextjsp = this.pages.get(i);
                    File fjsp = new File(nextjsp);
                    if (!fjsp.isAbsolute()) {
                        fjsp = new File(uriRootF, nextjsp);
                    }
                    if (!fjsp.exists()) {
                        if (!log.isWarnEnabled()) continue;
                        log.warn((Object)Localizer.getMessage("jspc.error.fileDoesNotExist", fjsp.toString()));
                        continue;
                    }
                    String s = fjsp.getAbsolutePath();
                    if (s.startsWith(this.uriRoot)) {
                        nextjsp = s.substring(this.uriRoot.length());
                    }
                    if (nextjsp.startsWith("." + File.separatorChar)) {
                        nextjsp = nextjsp.substring(2);
                    }
                    this.pages.set(i, nextjsp);
                }
            }
            this.initWebXml();
            int errorCount = 0;
            long start = System.currentTimeMillis();
            ExecutorService threadPool = Executors.newFixedThreadPool(this.threadCount);
            ExecutorCompletionService<Void> service = new ExecutorCompletionService<Void>(threadPool);
            try {
                int pageCount = this.pages.size();
                for (String nextjsp : this.pages) {
                    service.submit(new ProcessFile(nextjsp));
                }
                JasperException reportableError = null;
                for (int i = 0; i < pageCount; ++i) {
                    try {
                        service.take().get();
                        continue;
                    }
                    catch (ExecutionException e) {
                        if (this.failFast) {
                            List<Runnable> notExecuted = threadPool.shutdownNow();
                            i += notExecuted.size();
                            Throwable t = e.getCause();
                            if (t instanceof JasperException) {
                                reportableError = (JasperException)((Object)t);
                                continue;
                            }
                            reportableError = new JasperException(t);
                            continue;
                        }
                        ++errorCount;
                        log.error((Object)Localizer.getMessage("jspc.error.compilation"), (Throwable)e);
                        continue;
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
                if (reportableError != null) {
                    throw reportableError;
                }
            }
            finally {
                threadPool.shutdown();
            }
            long time = System.currentTimeMillis() - start;
            String msg = Localizer.getMessage("jspc.generation.result", Integer.toString(errorCount), Long.toString(time));
            if (this.failOnError && errorCount > 0) {
                System.out.println(Localizer.getMessage("jspc.errorCount", errorCount));
                throw new BuildException(msg);
            }
            log.info((Object)msg);
            this.completeWebXml();
            if (this.addWebXmlMappings) {
                this.mergeIntoWebXml();
            }
        }
        catch (IOException ioe) {
            throw new BuildException((Throwable)ioe);
        }
        catch (JasperException je) {
            if (this.failOnError) {
                throw new BuildException((Throwable)((Object)je));
            }
        }
        finally {
            if (this.loader != null) {
                LogFactory.release((ClassLoader)this.loader);
            }
        }
    }

    protected String nextArg() {
        if (this.argPos >= this.args.length || (this.fullstop = SWITCH_FULL_STOP.equals(this.args[this.argPos]))) {
            return null;
        }
        return this.args[this.argPos++];
    }

    protected String nextFile() {
        if (this.fullstop) {
            ++this.argPos;
        }
        if (this.argPos >= this.args.length) {
            return null;
        }
        return this.args[this.argPos++];
    }

    protected void initWebXml() throws JasperException {
        try {
            if (this.webxmlLevel >= 10) {
                this.mapout = this.openWebxmlWriter(new File(this.webxmlFile));
                this.servletout = new CharArrayWriter();
                this.mappingout = new CharArrayWriter();
            } else {
                this.mapout = null;
                this.servletout = null;
                this.mappingout = null;
            }
            if (this.webxmlLevel >= 20) {
                this.mapout.write(Localizer.getMessage("jspc.webxml.header", this.webxmlEncoding));
                this.mapout.flush();
            } else if (this.webxmlLevel >= 15) {
                this.mapout.write(Localizer.getMessage("jspc.webfrg.header", this.webxmlEncoding));
                this.mapout.flush();
            } else if (this.webxmlLevel >= 10 && !this.addWebXmlMappings) {
                this.mapout.write(Localizer.getMessage("jspc.webinc.header"));
                this.mapout.flush();
            }
        }
        catch (IOException ioe) {
            this.mapout = null;
            this.servletout = null;
            this.mappingout = null;
            throw new JasperException(ioe);
        }
    }

    protected void completeWebXml() {
        if (this.mapout != null) {
            try {
                this.servletout.writeTo(this.mapout);
                this.mappingout.writeTo(this.mapout);
                if (this.webxmlLevel >= 20) {
                    this.mapout.write(Localizer.getMessage("jspc.webxml.footer"));
                } else if (this.webxmlLevel >= 15) {
                    this.mapout.write(Localizer.getMessage("jspc.webfrg.footer"));
                } else if (this.webxmlLevel >= 10 && !this.addWebXmlMappings) {
                    this.mapout.write(Localizer.getMessage("jspc.webinc.footer"));
                }
                this.mapout.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    protected void initTldScanner(JspCServletContext context, ClassLoader classLoader) {
        if (this.scanner != null) {
            return;
        }
        this.scanner = this.newTldScanner(context, true, this.isValidateTld(), this.isBlockExternal());
        this.scanner.setClassLoader(classLoader);
    }

    protected TldScanner newTldScanner(JspCServletContext context, boolean namespaceAware, boolean validate, boolean blockExternal) {
        return new TldScanner(context, namespaceAware, validate, blockExternal);
    }

    protected void initServletContext(ClassLoader classLoader) throws IOException, JasperException {
        PrintWriter log = new PrintWriter(System.out);
        URL resourceBase = new File(this.uriRoot).getCanonicalFile().toURI().toURL();
        this.context = new JspCServletContext(log, resourceBase, classLoader, this.isValidateXml(), this.isBlockExternal());
        if (this.isValidateTld()) {
            this.context.setInitParameter("org.apache.jasper.XML_VALIDATE_TLD", "true");
        }
        this.initTldScanner(this.context, classLoader);
        try {
            this.scanner.scan();
        }
        catch (SAXException e) {
            throw new JasperException(e);
        }
        this.tldCache = new TldCache(this.context, this.scanner.getUriTldResourcePathMap(), this.scanner.getTldResourcePathTaglibXmlMap());
        this.context.setAttribute(TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME, this.tldCache);
        this.rctxt = new JspRuntimeContext(this.context, this);
        this.jspConfig = new JspConfig(this.context);
        this.tagPluginManager = new TagPluginManager(this.context);
    }

    protected ClassLoader initClassLoader() throws IOException {
        this.classPath = this.getClassPath();
        ClassLoader jspcLoader = this.getClass().getClassLoader();
        if (jspcLoader instanceof AntClassLoader) {
            this.classPath = this.classPath + File.pathSeparator + ((AntClassLoader)jspcLoader).getClasspath();
        }
        ArrayList<URL> urls = new ArrayList<URL>();
        StringTokenizer tokenizer = new StringTokenizer(this.classPath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();
            try {
                File libFile = new File(path);
                urls.add(libFile.toURI().toURL());
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.toString());
            }
        }
        File webappBase = new File(this.uriRoot);
        if (webappBase.exists()) {
            String[] libs;
            File classes = new File(webappBase, "/WEB-INF/classes");
            try {
                if (classes.exists()) {
                    this.classPath = this.classPath + File.pathSeparator + classes.getCanonicalPath();
                    urls.add(classes.getCanonicalFile().toURI().toURL());
                }
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.toString());
            }
            File webinfLib = new File(webappBase, "/WEB-INF/lib");
            if (webinfLib.exists() && webinfLib.isDirectory() && (libs = webinfLib.list()) != null) {
                for (String lib : libs) {
                    if (lib.length() < 5) continue;
                    String ext = lib.substring(lib.length() - 4);
                    if (!".jar".equalsIgnoreCase(ext)) {
                        if (!".tld".equalsIgnoreCase(ext)) continue;
                        log.warn((Object)Localizer.getMessage("jspc.warning.tldInWebInfLib"));
                        continue;
                    }
                    try {
                        File libFile = new File(webinfLib, lib);
                        this.classPath = this.classPath + File.pathSeparator + libFile.getAbsolutePath();
                        urls.add(libFile.getAbsoluteFile().toURI().toURL());
                    }
                    catch (IOException ioe) {
                        throw new RuntimeException(ioe.toString());
                    }
                }
            }
        }
        URL[] urlsA = urls.toArray(new URL[0]);
        this.loader = new URLClassLoader(urlsA, this.getClass().getClassLoader());
        return this.loader;
    }

    protected void locateUriRoot(File f) {
        String tUriBase = this.uriBase;
        if (tUriBase == null) {
            tUriBase = "/";
        }
        try {
            if (f.exists()) {
                f = new File(f.getAbsolutePath());
                while (true) {
                    String fParent;
                    File g;
                    if ((g = new File(f, "WEB-INF")).exists() && g.isDirectory()) {
                        this.uriRoot = f.getCanonicalPath();
                        this.uriBase = tUriBase;
                        if (!log.isInfoEnabled()) break;
                        log.info((Object)Localizer.getMessage("jspc.implicit.uriRoot", this.uriRoot));
                        break;
                    }
                    if (f.exists() && f.isDirectory()) {
                        tUriBase = "/" + f.getName() + "/" + tUriBase;
                    }
                    if ((fParent = f.getParent()) == null) break;
                    f = new File(fParent);
                }
                if (this.uriRoot != null) {
                    File froot = new File(this.uriRoot);
                    this.uriRoot = froot.getCanonicalPath();
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    protected File resolveFile(String s) {
        if (this.getProject() == null) {
            return FileUtils.getFileUtils().resolveFile(null, s);
        }
        return FileUtils.getFileUtils().resolveFile(this.getProject().getBaseDir(), s);
    }

    private Reader openWebxmlReader(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return this.webxmlEncoding != null ? new InputStreamReader((InputStream)fis, this.webxmlEncoding) : new InputStreamReader(fis);
        }
        catch (IOException ex) {
            fis.close();
            throw ex;
        }
    }

    private Writer openWebxmlWriter(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            return this.webxmlEncoding != null ? new OutputStreamWriter((OutputStream)fos, this.webxmlEncoding) : new OutputStreamWriter(fos);
        }
        catch (IOException ex) {
            fos.close();
            throw ex;
        }
    }

    static {
        JspFactory.setDefaultFactory((JspFactory)new JspFactoryImpl());
        log = LogFactory.getLog(JspC.class);
        insertBefore = new HashSet<String>();
        insertBefore.add("</web-app>");
        insertBefore.add("<servlet-mapping>");
        insertBefore.add("<session-config>");
        insertBefore.add("<mime-mapping>");
        insertBefore.add("<welcome-file-list>");
        insertBefore.add("<error-page>");
        insertBefore.add("<taglib>");
        insertBefore.add("<resource-env-ref>");
        insertBefore.add("<resource-ref>");
        insertBefore.add("<security-constraint>");
        insertBefore.add("<login-config>");
        insertBefore.add("<security-role>");
        insertBefore.add("<env-entry>");
        insertBefore.add("<ejb-ref>");
        insertBefore.add("<ejb-local-ref>");
    }

    private class ProcessFile
    implements Callable<Void> {
        private final String file;

        private ProcessFile(String file) {
            this.file = file;
        }

        @Override
        public Void call() throws Exception {
            JspC.this.processFile(this.file);
            return null;
        }
    }
}

