/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.taskdefs.Java
 *  org.apache.tools.ant.types.Commandline
 *  org.apache.tools.ant.types.Commandline$Argument
 *  org.apache.tools.ant.types.FileSet
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.types.Reference
 *  org.apache.tools.ant.util.FileUtils
 */
package org.codehaus.groovy.ant;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.lang.Script;
import groovy.util.AntBuilder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.groovy.ant.AntProjectPropertiesDelegate;
import org.codehaus.groovy.ant.LoggingHelper;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.tools.ErrorReporter;

public class Groovy
extends Java {
    private static final String PREFIX = "embedded_script_in_";
    private static final String SUFFIX = "groovy_Ant_task";
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private final LoggingHelper log = new LoggingHelper((Task)this);
    private Vector<FileSet> filesets = new Vector();
    private File srcFile = null;
    private String command = "";
    private File output = null;
    private boolean append = false;
    private Path classpath;
    private boolean fork = false;
    private boolean includeAntRuntime = true;
    private boolean useGroovyShell = false;
    private boolean indy = false;
    private String scriptBaseClass;
    private String configscript;
    private CompilerConfiguration configuration = new CompilerConfiguration();
    private Commandline cmdline = new Commandline();
    private boolean contextClassLoader;

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public void setUseGroovyShell(boolean useGroovyShell) {
        this.useGroovyShell = useGroovyShell;
    }

    public void setIncludeAntRuntime(boolean includeAntRuntime) {
        this.includeAntRuntime = includeAntRuntime;
    }

    public void setStacktrace(boolean stacktrace) {
        this.configuration.setDebug(stacktrace);
    }

    public void setSrc(File srcFile) {
        this.srcFile = srcFile;
    }

    public void addText(String txt) {
        this.log("addText('" + txt + "')", 3);
        this.command = this.command + txt;
    }

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference ref) {
        this.createClasspath().setRefid(ref);
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public void setConfigscript(String configscript) {
        this.configscript = configscript;
    }

    public void setIndy(boolean indy) {
        this.indy = indy;
    }

    public void setScriptBaseClass(String scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    public void execute() throws BuildException {
        block11: {
            this.log.debug("execute()");
            this.command = this.command.trim();
            if (this.srcFile == null && this.command.length() == 0 && this.filesets.isEmpty()) {
                throw new BuildException("Source file does not exist!", this.getLocation());
            }
            if (this.srcFile != null && !this.srcFile.exists()) {
                throw new BuildException("Source file does not exist!", this.getLocation());
            }
            try {
                PrintStream out = System.out;
                try {
                    if (this.output != null) {
                        this.log.verbose("Opening PrintStream to output file " + this.output);
                        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.output.getAbsolutePath(), this.append)));
                    }
                    if (this.command == null || this.command.trim().length() == 0) {
                        this.createClasspath().add(new Path(this.getProject(), this.srcFile.getParentFile().getCanonicalPath()));
                        this.command = Groovy.getText(new BufferedReader(new FileReader(this.srcFile)));
                    }
                    if (this.command != null) {
                        this.execGroovy(this.command, out);
                        break block11;
                    }
                    throw new BuildException("Source file does not exist!", this.getLocation());
                }
                finally {
                    if (out != null && out != System.out) {
                        out.close();
                    }
                }
            }
            catch (IOException e) {
                throw new BuildException((Throwable)e, this.getLocation());
            }
        }
        this.log.verbose("statements executed successfully");
    }

    private static String getText(BufferedReader reader) throws IOException {
        StringBuilder answer = new StringBuilder();
        char[] charBuffer = new char[4096];
        int nbCharRead = 0;
        while ((nbCharRead = reader.read(charBuffer)) != -1) {
            answer.append(charBuffer, 0, nbCharRead);
        }
        reader.close();
        return answer.toString();
    }

    public Commandline.Argument createArg() {
        return this.cmdline.createArgument();
    }

    protected void runStatements(Reader reader, PrintStream out) throws IOException {
        this.log.debug("runStatements()");
        StringBuilder txt = new StringBuilder();
        String line = "";
        BufferedReader in = new BufferedReader(reader);
        while ((line = in.readLine()) != null) {
            line = this.getProject().replaceProperties(line);
            if (line.indexOf("--") < 0) continue;
            txt.append("\n");
        }
        if (!txt.toString().equals("")) {
            this.execGroovy(txt.toString(), out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void execGroovy(String txt, PrintStream out) {
        ClassLoader baseClassLoader;
        this.log.debug("execGroovy()");
        if ("".equals(txt.trim())) {
            return;
        }
        this.log.verbose("Script: " + txt);
        if (this.classpath != null) {
            this.log.debug("Explicit Classpath: " + this.classpath.toString());
        }
        if (this.fork) {
            this.log.debug("Using fork mode");
            try {
                this.createClasspathParts();
                this.createNewArgs(txt);
                super.setFork(this.fork);
                super.setClassname(this.useGroovyShell ? "groovy.lang.GroovyShell" : "org.codehaus.groovy.ant.Groovy");
                this.configureCompiler();
                super.execute();
            }
            catch (Exception e) {
                StringWriter writer = new StringWriter();
                new ErrorReporter(e, false).write(new PrintWriter(writer));
                String message = writer.toString();
                throw new BuildException("Script Failed: " + message, (Throwable)e, this.getLocation());
            }
            return;
        }
        Object mavenPom = null;
        Project project = this.getProject();
        ClassLoader savedLoader = null;
        Thread thread = Thread.currentThread();
        boolean maven = "org.apache.commons.grant.GrantProject".equals(project.getClass().getName());
        if (maven) {
            if (this.contextClassLoader) {
                throw new BuildException("Using setContextClassLoader not permitted when using Maven.", this.getLocation());
            }
            try {
                Object propsHandler = project.getClass().getMethod("getPropsHandler", new Class[0]).invoke((Object)project, new Object[0]);
                Field contextField = propsHandler.getClass().getDeclaredField("context");
                contextField.setAccessible(true);
                Object context = contextField.get(propsHandler);
                mavenPom = InvokerHelper.invokeMethod(context, "getProject", EMPTY_OBJECT_ARRAY);
            }
            catch (Exception e) {
                throw new BuildException("Impossible to retrieve Maven's Ant project: " + e.getMessage(), this.getLocation());
            }
            baseClassLoader = mavenPom.getClass().getClassLoader();
        } else {
            baseClassLoader = GroovyShell.class.getClassLoader();
        }
        if (this.contextClassLoader || maven) {
            savedLoader = thread.getContextClassLoader();
            thread.setContextClassLoader(GroovyShell.class.getClassLoader());
        }
        String scriptName = this.computeScriptName();
        GroovyClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>(){

            @Override
            public GroovyClassLoader run() {
                return new GroovyClassLoader(baseClassLoader);
            }
        });
        this.addClassPathes(classLoader);
        this.configureCompiler();
        GroovyShell groovy = new GroovyShell(classLoader, new Binding(), this.configuration);
        try {
            this.parseAndRunScript(groovy, txt, mavenPom, scriptName, null, new AntBuilder((Task)this));
        }
        finally {
            groovy.resetLoadedClasses();
            groovy.getClassLoader().clearCache();
            if (this.contextClassLoader || maven) {
                thread.setContextClassLoader(savedLoader);
            }
        }
    }

    private void configureCompiler() {
        if (this.scriptBaseClass != null) {
            this.configuration.setScriptBaseClass(this.scriptBaseClass);
        }
        if (this.indy) {
            this.configuration.getOptimizationOptions().put("indy", Boolean.TRUE);
            this.configuration.getOptimizationOptions().put("int", Boolean.FALSE);
        }
        if (this.configscript != null) {
            Binding binding = new Binding();
            binding.setVariable("configuration", this.configuration);
            CompilerConfiguration configuratorConfig = new CompilerConfiguration();
            ImportCustomizer customizer = new ImportCustomizer();
            customizer.addStaticStars("org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder");
            configuratorConfig.addCompilationCustomizers(customizer);
            GroovyShell shell = new GroovyShell(binding, configuratorConfig);
            File confSrc = new File(this.configscript);
            try {
                shell.evaluate(confSrc);
            }
            catch (IOException e) {
                throw new BuildException("Unable to configure compiler using configuration file: " + confSrc, (Throwable)e);
            }
        }
    }

    private void parseAndRunScript(GroovyShell shell, String txt, Object mavenPom, String scriptName, File scriptFile, AntBuilder builder) {
        try {
            Script script = scriptFile != null ? shell.parse(scriptFile) : shell.parse(txt, scriptName);
            Project project = this.getProject();
            script.setProperty("ant", builder);
            script.setProperty("project", project);
            script.setProperty("properties", new AntProjectPropertiesDelegate(project));
            script.setProperty("target", this.getOwningTarget());
            script.setProperty("task", (Object)this);
            script.setProperty("args", this.cmdline.getCommandline());
            if (mavenPom != null) {
                script.setProperty("pom", mavenPom);
            }
            script.run();
        }
        catch (MissingMethodException mme) {
            if (scriptFile != null) {
                try {
                    shell.run(scriptFile, this.cmdline.getCommandline());
                }
                catch (IOException e) {
                    this.processError(e);
                }
            } else {
                shell.run(txt, scriptName, this.cmdline.getCommandline());
            }
        }
        catch (CompilationFailedException e) {
            this.processError(e);
        }
        catch (IOException e) {
            this.processError(e);
        }
    }

    private void processError(Exception e) {
        StringWriter writer = new StringWriter();
        new ErrorReporter(e, false).write(new PrintWriter(writer));
        String message = writer.toString();
        throw new BuildException("Script Failed: " + message, (Throwable)e, this.getLocation());
    }

    public static void main(String[] args) {
        GroovyShell shell = new GroovyShell(new Binding());
        Groovy groovy = new Groovy();
        for (int i = 1; i < args.length; ++i) {
            Commandline.Argument argument = groovy.createArg();
            argument.setValue(args[i]);
        }
        AntBuilder builder = new AntBuilder();
        groovy.setProject(builder.getProject());
        groovy.parseAndRunScript(shell, null, null, null, new File(args[0]), builder);
    }

    private void createClasspathParts() {
        File[] files;
        Path path;
        if (this.classpath != null) {
            path = super.createClasspath();
            path.setPath(this.classpath.toString());
        }
        if (this.includeAntRuntime) {
            path = super.createClasspath();
            path.setPath(System.getProperty("java.class.path"));
        }
        String groovyHome = null;
        String[] strings = this.getSysProperties().getVariables();
        if (strings != null) {
            for (String prop : strings) {
                if (!prop.startsWith("-Dgroovy.home=")) continue;
                groovyHome = prop.substring("-Dgroovy.home=".length());
            }
        }
        if (groovyHome == null) {
            groovyHome = System.getProperty("groovy.home");
        }
        if (groovyHome == null) {
            groovyHome = System.getenv("GROOVY_HOME");
        }
        if (groovyHome == null) {
            throw new IllegalStateException("Neither ${groovy.home} nor GROOVY_HOME defined.");
        }
        File jarDir = new File(groovyHome, "embeddable");
        if (!jarDir.exists()) {
            throw new IllegalStateException("GROOVY_HOME incorrectly defined. No embeddable directory found in: " + groovyHome);
        }
        for (File file : files = jarDir.listFiles()) {
            try {
                this.log.debug("Adding jar to classpath: " + file.getCanonicalPath());
            }
            catch (IOException iOException) {
                // empty catch block
            }
            path = super.createClasspath();
            path.setLocation(file);
        }
    }

    private void createNewArgs(String txt) throws IOException {
        String[] args = this.cmdline.getCommandline();
        File tempFile = FileUtils.getFileUtils().createTempFile(PREFIX, SUFFIX, null, true, true);
        String[] commandline = new String[args.length + 1];
        ResourceGroovyMethods.write(tempFile, txt);
        commandline[0] = tempFile.getCanonicalPath();
        System.arraycopy(args, 0, commandline, 1, args.length);
        super.clearArgs();
        for (String arg : commandline) {
            Commandline.Argument argument = super.createArg();
            argument.setValue(arg);
        }
    }

    private String computeScriptName() {
        if (this.srcFile != null) {
            return this.srcFile.getAbsolutePath();
        }
        String name = PREFIX;
        name = this.getLocation().getFileName().length() > 0 ? name + this.getLocation().getFileName().replaceAll("[^\\w_\\.]", "_").replaceAll("[\\.]", "_dot_") : name + SUFFIX;
        return name;
    }

    protected void addClassPathes(GroovyClassLoader classLoader) {
        if (this.classpath != null) {
            for (int i = 0; i < this.classpath.list().length; ++i) {
                classLoader.addClasspath(this.classpath.list()[i]);
            }
        }
    }

    protected void printResults(PrintStream out) {
        this.log.debug("printResults()");
        StringBuilder line = new StringBuilder();
        out.println(line);
        out.println();
    }

    public void setContextClassLoader(boolean contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }
}

