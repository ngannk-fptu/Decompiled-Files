/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.sling.commons.compiler.CompilationResult
 *  org.apache.sling.commons.compiler.CompilationUnit
 *  org.apache.sling.commons.compiler.CompilationUnitWithSource
 *  org.apache.sling.commons.compiler.CompilerMessage
 *  org.apache.sling.commons.compiler.Options
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import org.apache.sling.commons.compiler.CompilationResult;
import org.apache.sling.commons.compiler.CompilationUnit;
import org.apache.sling.commons.compiler.CompilationUnitWithSource;
import org.apache.sling.commons.compiler.CompilerMessage;
import org.apache.sling.commons.compiler.Options;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JavacErrorDetail;
import org.apache.sling.scripting.jsp.jasper.compiler.SmapUtil;

public class JDTCompiler
extends Compiler {
    public JDTCompiler(JspCompilationContext ctxt) {
        super(ctxt);
    }

    @Override
    protected void generateClass(String[] smap) throws FileNotFoundException, JasperException, Exception {
        long t1 = 0L;
        if (this.log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }
        final String sourceFile = this.ctxt.getServletJavaFileName();
        String packageName = this.ctxt.getServletPackageName();
        final String targetClassName = (packageName.length() != 0 ? packageName + "." : "") + this.ctxt.getServletClassName();
        CompilationUnitWithSource unit = new CompilationUnitWithSource(){

            public long getLastModified() {
                return -1L;
            }

            public String getMainClassName() {
                return targetClassName;
            }

            public Reader getSource() throws IOException {
                return new BufferedReader(new InputStreamReader(JDTCompiler.this.ctxt.getInputStream(sourceFile), JDTCompiler.this.ctxt.getOptions().getJavaEncoding()));
            }

            public String getFileName() {
                return sourceFile;
            }
        };
        Options options = new Options();
        options.put((Object)"classLoaderWriter", (Object)this.ctxt.getRuntimeContext().getIOProvider().getClassLoaderWriter());
        options.put((Object)"generateDebugInfo", (Object)this.ctxt.getOptions().getClassDebugInfo());
        if (this.ctxt.getOptions().getCompilerSourceVM() != null) {
            options.put((Object)"sourceVersion", (Object)this.ctxt.getOptions().getCompilerSourceVM());
        } else {
            options.put((Object)"sourceVersion", (Object)"1.6");
        }
        if (this.ctxt.getOptions().getCompilerTargetVM() != null) {
            options.put((Object)"targetVersion", (Object)this.ctxt.getOptions().getCompilerTargetVM());
        } else {
            options.put((Object)"targetVersion", (Object)"1.6");
        }
        ArrayList<JavacErrorDetail> problemList = new ArrayList<JavacErrorDetail>();
        CompilationResult result = this.ctxt.getRuntimeContext().getIOProvider().getJavaCompiler().compile(new CompilationUnit[]{unit}, options);
        if (result.getErrors() != null) {
            for (CompilerMessage cm : result.getErrors()) {
                String name = cm.getFile();
                try {
                    problemList.add(ErrorDispatcher.createJavacError(name, this.pageNodes, new StringBuffer(cm.getMessage()), cm.getLine(), this.ctxt));
                }
                catch (JasperException e) {
                    this.log.error("Error visiting node", (Throwable)((Object)e));
                }
            }
        }
        if (!this.ctxt.keepGenerated()) {
            this.ctxt.delete(this.ctxt.getServletJavaFileName());
        }
        if (!problemList.isEmpty()) {
            JavacErrorDetail[] jeds = problemList.toArray(new JavacErrorDetail[0]);
            this.errDispatcher.javacError(jeds);
        }
        if (this.log.isDebugEnabled()) {
            long t2 = System.currentTimeMillis();
            this.log.debug("Compiled " + this.ctxt.getServletJavaFileName() + " " + (t2 - t1) + "ms");
        }
        if (this.ctxt.isPrototypeMode()) {
            return;
        }
        if (!this.ctxt.getOptions().isSmapSuppressed()) {
            SmapUtil.installSmap(this.getCompilationContext(), smap);
        }
    }
}

