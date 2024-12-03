/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.BeanRepository;
import org.apache.sling.scripting.jsp.jasper.compiler.Collector;
import org.apache.sling.scripting.jsp.jasper.compiler.ELFunctionMapper;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.Generator;
import org.apache.sling.scripting.jsp.jasper.compiler.JspConfig;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;
import org.apache.sling.scripting.jsp.jasper.compiler.ParserController;
import org.apache.sling.scripting.jsp.jasper.compiler.ScriptingVariabler;
import org.apache.sling.scripting.jsp.jasper.compiler.ServletWriter;
import org.apache.sling.scripting.jsp.jasper.compiler.SmapUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.TagFileProcessor;
import org.apache.sling.scripting.jsp.jasper.compiler.TagPluginManager;
import org.apache.sling.scripting.jsp.jasper.compiler.TextOptimizer;
import org.apache.sling.scripting.jsp.jasper.compiler.Validator;

public abstract class Compiler {
    protected Log log = LogFactory.getLog(Compiler.class);
    protected final JspCompilationContext ctxt;
    protected ErrorDispatcher errDispatcher;
    protected PageInfo pageInfo;
    protected TagFileProcessor tfp;
    protected Node.Nodes pageNodes;

    public Compiler(JspCompilationContext ctxt) {
        this.ctxt = ctxt;
    }

    public Node.Nodes getPageNodes() {
        return this.pageNodes;
    }

    protected String[] generateJava() throws Exception {
        String[] smapStr = null;
        long t4 = 0L;
        long t3 = 0L;
        long t2 = 0L;
        long t1 = 0L;
        if (this.log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }
        this.pageInfo = new PageInfo(new BeanRepository(this.ctxt.getClassLoader(), this.errDispatcher), this.ctxt.getJspFile(), this.ctxt.getOptions().isDefaultSession());
        JspConfig jspConfig = this.ctxt.getOptions().getJspConfig();
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
        this.ctxt.checkOutputDir();
        String javaFileName = this.ctxt.getServletJavaFileName();
        ServletWriter writer = null;
        try {
            String javaEncoding = this.ctxt.getOptions().getJavaEncoding();
            OutputStreamWriter osw = null;
            try {
                osw = new OutputStreamWriter(this.ctxt.getOutputStream(javaFileName), javaEncoding);
            }
            catch (UnsupportedEncodingException ex) {
                this.errDispatcher.jspError("jsp.error.needAlternateJavaEncoding", javaEncoding);
            }
            catch (IOException ioe) {
                throw (IOException)new FileNotFoundException(ioe.getMessage()).initCause(ioe);
            }
            writer = new ServletWriter(new PrintWriter(osw));
            this.ctxt.setWriter(writer);
            ParserController parserCtl = new ParserController(this.ctxt, this);
            this.pageNodes = parserCtl.parse(this.ctxt.getJspFile());
            if (this.ctxt.isPrototypeMode()) {
                Generator.generate(writer, this, this.pageNodes);
                writer.close();
                writer = null;
                String[] stringArray = null;
                return stringArray;
            }
            Validator.validate(this, this.pageNodes);
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
            TagPluginManager tagPluginManager = this.ctxt.getOptions().getTagPluginManager();
            tagPluginManager.apply(this.pageNodes, this.errDispatcher, this.pageInfo);
            TextOptimizer.concatenate(this, this.pageNodes);
            ELFunctionMapper.map(this, this.pageNodes);
            Generator.generate(writer, this, this.pageNodes);
            ServletWriter w = writer;
            writer = null;
            w.close();
            this.ctxt.setWriter(null);
            if (this.log.isDebugEnabled()) {
                t4 = System.currentTimeMillis();
                this.log.debug("Generated " + javaFileName + " total=" + (t4 - t1) + " generate=" + (t4 - t3) + " validate=" + (t2 - t1));
            }
        }
        catch (Exception e) {
            if (writer != null) {
                try {
                    writer.close();
                    writer = null;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            this.ctxt.delete(javaFileName);
            throw e;
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (Exception exception) {}
            }
        }
        if (!this.ctxt.getOptions().isSmapSuppressed()) {
            smapStr = SmapUtil.generateSmap(this.ctxt, this.pageNodes);
        }
        this.tfp.removeProtoTypeFiles();
        return smapStr;
    }

    protected abstract void generateClass(String[] var1) throws FileNotFoundException, JasperException, Exception;

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
            String[] smap = this.generateJava();
            if (compileClass) {
                this.generateClass(smap);
            }
        }
        finally {
            if (this.tfp != null) {
                this.tfp.removeProtoTypeFiles();
            }
            this.tfp = null;
            this.errDispatcher = null;
            if (!jspcMode) {
                this.pageInfo = null;
            }
            if (this.ctxt.getWriter() != null) {
                this.ctxt.getWriter().close();
                this.ctxt.setWriter(null);
            }
        }
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
            String javaFileName = this.ctxt.getServletJavaFileName();
            if (javaFileName != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Deleting " + javaFileName);
                }
                this.ctxt.delete(javaFileName);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void removeGeneratedClassFiles() {
        try {
            String classFileName = this.ctxt.getClassFileName();
            if (classFileName != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Deleting " + classFileName);
                }
                this.ctxt.delete(classFileName);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void clean() {
        if (this.pageNodes != null) {
            try {
                this.pageNodes.visit(new CleanVisitor());
            }
            catch (JasperException jasperException) {
                // empty catch block
            }
        }
    }

    private static final class CleanVisitor
    extends Node.Visitor {
        private CleanVisitor() {
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            n.clean();
            this.visitBody(n);
        }
    }
}

