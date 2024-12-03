/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.BuildListener
 *  org.apache.tools.ant.DefaultLogger
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.taskdefs.Javac
 *  org.apache.tools.ant.taskdefs.Javac$ImplementationSpecificArgument
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.types.PatternSet$NameEntry
 */
package org.apache.jasper.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.compiler.SmapUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;

public class AntCompiler
extends Compiler {
    private final Log log = LogFactory.getLog(AntCompiler.class);
    protected static final Object javacLock = new Object();
    protected Project project = null;
    protected JasperAntLogger logger;

    protected Project getProject() {
        if (this.project != null) {
            return this.project;
        }
        this.project = new Project();
        this.logger = new JasperAntLogger();
        this.logger.setOutputPrintStream(System.out);
        this.logger.setErrorPrintStream(System.err);
        this.logger.setMessageOutputLevel(2);
        this.project.addBuildListener((BuildListener)this.logger);
        if (System.getProperty("catalina.home") != null) {
            this.project.setBasedir(System.getProperty("catalina.home"));
        }
        if (this.options.getCompiler() != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Compiler " + this.options.getCompiler()));
            }
            this.project.setProperty("build.compiler", this.options.getCompiler());
        }
        this.project.init();
        return this.project;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void generateClass(Map<String, SmapStratum> smaps) throws FileNotFoundException, JasperException, Exception {
        File javaFile;
        BuildException be;
        StringBuilder errorReport;
        String javaFileName;
        long t1;
        block24: {
            t1 = 0L;
            if (this.log.isDebugEnabled()) {
                t1 = System.currentTimeMillis();
            }
            String javaEncoding = this.ctxt.getOptions().getJavaEncoding();
            javaFileName = this.ctxt.getServletJavaFileName();
            String classpath = this.ctxt.getClassPath();
            errorReport = new StringBuilder();
            StringBuilder info = new StringBuilder();
            info.append("Compile: javaFileName=" + javaFileName + "\n");
            info.append("    classpath=" + classpath + "\n");
            SystemLogHandler.setThread();
            this.getProject();
            Javac javac = (Javac)this.project.createTask("javac");
            Path path = new Path(this.project);
            path.setPath(System.getProperty("java.class.path"));
            info.append("    cp=" + System.getProperty("java.class.path") + "\n");
            StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
            while (tokenizer.hasMoreElements()) {
                String pathElement = tokenizer.nextToken();
                File repository = new File(pathElement);
                path.setLocation(repository);
                info.append("    cp=" + repository + "\n");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Using classpath: " + System.getProperty("java.class.path") + File.pathSeparator + classpath));
            }
            Path srcPath = new Path(this.project);
            srcPath.setLocation(this.options.getScratchDir());
            info.append("    work dir=" + this.options.getScratchDir() + "\n");
            String exts = System.getProperty("java.ext.dirs");
            if (exts != null) {
                Path extdirs = new Path(this.project);
                extdirs.setPath(exts);
                javac.setExtdirs(extdirs);
                info.append("    extension dir=" + exts + "\n");
            }
            if (this.ctxt.getOptions().getFork()) {
                String endorsed = System.getProperty("java.endorsed.dirs");
                if (endorsed != null) {
                    Javac.ImplementationSpecificArgument endorsedArg = javac.createCompilerArg();
                    endorsedArg.setLine("-J-Djava.endorsed.dirs=" + this.quotePathList(endorsed));
                    info.append("    endorsed dir=" + this.quotePathList(endorsed) + "\n");
                } else {
                    info.append("    no endorsed dirs specified\n");
                }
            }
            javac.setEncoding(javaEncoding);
            javac.setClasspath(path);
            javac.setDebug(this.ctxt.getOptions().getClassDebugInfo());
            javac.setSrcdir(srcPath);
            javac.setTempdir(this.options.getScratchDir());
            javac.setFork(this.ctxt.getOptions().getFork());
            info.append("    srcDir=" + srcPath + "\n");
            if (this.options.getCompiler() != null) {
                javac.setCompiler(this.options.getCompiler());
                info.append("    compiler=" + this.options.getCompiler() + "\n");
            }
            if (this.options.getCompilerTargetVM() != null) {
                javac.setTarget(this.options.getCompilerTargetVM());
                info.append("   compilerTargetVM=" + this.options.getCompilerTargetVM() + "\n");
            }
            if (this.options.getCompilerSourceVM() != null) {
                javac.setSource(this.options.getCompilerSourceVM());
                info.append("   compilerSourceVM=" + this.options.getCompilerSourceVM() + "\n");
            }
            PatternSet.NameEntry includes = javac.createInclude();
            includes.setName(this.ctxt.getJavaPath());
            info.append("    include=" + this.ctxt.getJavaPath() + "\n");
            be = null;
            try {
                if (this.ctxt.getOptions().getFork()) {
                    javac.execute();
                    break block24;
                }
                Object object = javacLock;
                synchronized (object) {
                    javac.execute();
                }
            }
            catch (BuildException e) {
                be = e;
                this.log.error((Object)Localizer.getMessage("jsp.error.javac"), (Throwable)e);
                this.log.error((Object)(Localizer.getMessage("jsp.error.javac.env") + info.toString()));
            }
        }
        errorReport.append(this.logger.getReport());
        String errorCapture = SystemLogHandler.unsetThread();
        if (errorCapture != null) {
            errorReport.append(System.lineSeparator());
            errorReport.append(errorCapture);
        }
        if (!this.ctxt.keepGenerated() && !(javaFile = new File(javaFileName)).delete()) {
            throw new JasperException(Localizer.getMessage("jsp.warning.compiler.javafile.delete.fail", javaFile));
        }
        if (be != null) {
            String errorReportString = errorReport.toString();
            this.log.error((Object)Localizer.getMessage("jsp.error.compilation", javaFileName, errorReportString));
            JavacErrorDetail[] javacErrors = ErrorDispatcher.parseJavacErrors(errorReportString, javaFileName, this.pageNodes);
            if (javacErrors != null) {
                this.errDispatcher.javacError(javacErrors);
            } else {
                this.errDispatcher.javacError(errorReportString, (Exception)((Object)be));
            }
        }
        if (this.log.isDebugEnabled()) {
            long t2 = System.currentTimeMillis();
            this.log.debug((Object)("Compiled " + this.ctxt.getServletJavaFileName() + " " + (t2 - t1) + "ms"));
        }
        this.logger = null;
        this.project = null;
        if (this.ctxt.isPrototypeMode()) {
            return;
        }
        if (!this.options.isSmapSuppressed()) {
            SmapUtil.installSmap(smaps);
        }
    }

    private String quotePathList(String list) {
        StringBuilder result = new StringBuilder(list.length() + 10);
        StringTokenizer st = new StringTokenizer(list, File.pathSeparator);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.indexOf(32) == -1) {
                result.append(token);
            } else {
                result.append('\"');
                result.append(token);
                result.append('\"');
            }
            if (!st.hasMoreTokens()) continue;
            result.append(File.pathSeparatorChar);
        }
        return result.toString();
    }

    static {
        System.setErr(new SystemLogHandler(System.err));
    }

    public static class JasperAntLogger
    extends DefaultLogger {
        protected final StringBuilder reportBuf = new StringBuilder();

        protected void printMessage(String message, PrintStream stream, int priority) {
        }

        protected void log(String message) {
            this.reportBuf.append(message);
            this.reportBuf.append(System.lineSeparator());
        }

        protected String getReport() {
            String report = this.reportBuf.toString();
            this.reportBuf.setLength(0);
            return report;
        }
    }

    protected static class SystemLogHandler
    extends PrintStream {
        protected final PrintStream wrapped;
        protected static final ThreadLocal<PrintStream> streams = new ThreadLocal();
        protected static final ThreadLocal<ByteArrayOutputStream> data = new ThreadLocal();

        public SystemLogHandler(PrintStream wrapped) {
            super(wrapped);
            this.wrapped = wrapped;
        }

        public static void setThread() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            data.set(baos);
            streams.set(new PrintStream(baos));
        }

        public static String unsetThread() {
            ByteArrayOutputStream baos = data.get();
            if (baos == null) {
                return null;
            }
            streams.set(null);
            data.set(null);
            return baos.toString();
        }

        protected PrintStream findStream() {
            PrintStream ps = streams.get();
            if (ps == null) {
                ps = this.wrapped;
            }
            return ps;
        }

        @Override
        public void flush() {
            this.findStream().flush();
        }

        @Override
        public void close() {
            this.findStream().close();
        }

        @Override
        public boolean checkError() {
            return this.findStream().checkError();
        }

        @Override
        protected void setError() {
        }

        @Override
        public void write(int b) {
            this.findStream().write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.findStream().write(b);
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            this.findStream().write(buf, off, len);
        }

        @Override
        public void print(boolean b) {
            this.findStream().print(b);
        }

        @Override
        public void print(char c) {
            this.findStream().print(c);
        }

        @Override
        public void print(int i) {
            this.findStream().print(i);
        }

        @Override
        public void print(long l) {
            this.findStream().print(l);
        }

        @Override
        public void print(float f) {
            this.findStream().print(f);
        }

        @Override
        public void print(double d) {
            this.findStream().print(d);
        }

        @Override
        public void print(char[] s) {
            this.findStream().print(s);
        }

        @Override
        public void print(String s) {
            this.findStream().print(s);
        }

        @Override
        public void print(Object obj) {
            this.findStream().print(obj);
        }

        @Override
        public void println() {
            this.findStream().println();
        }

        @Override
        public void println(boolean x) {
            this.findStream().println(x);
        }

        @Override
        public void println(char x) {
            this.findStream().println(x);
        }

        @Override
        public void println(int x) {
            this.findStream().println(x);
        }

        @Override
        public void println(long x) {
            this.findStream().println(x);
        }

        @Override
        public void println(float x) {
            this.findStream().println(x);
        }

        @Override
        public void println(double x) {
            this.findStream().println(x);
        }

        @Override
        public void println(char[] x) {
            this.findStream().println(x);
        }

        @Override
        public void println(String x) {
            this.findStream().println(x);
        }

        @Override
        public void println(Object x) {
            this.findStream().println(x);
        }
    }
}

