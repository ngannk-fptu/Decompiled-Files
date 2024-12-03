/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.MissingMethodException;
import groovy.lang.Script;
import groovy.ui.GroovySocketServer;
import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.GroovyInternalPosixParser;
import groovyjarjarcommonscli.HelpFormatter;
import groovyjarjarcommonscli.OptionBuilder;
import groovyjarjarcommonscli.Options;
import groovyjarjarcommonscli.ParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.runtime.StackTraceUtils;

public class GroovyMain {
    private List args;
    private boolean isScriptFile;
    private String script;
    private boolean processFiles;
    private boolean editFiles;
    private boolean autoOutput;
    private boolean autoSplit;
    private String splitPattern = " ";
    private boolean processSockets;
    private int port;
    private String backupExtension;
    private boolean debug = false;
    private CompilerConfiguration conf = new CompilerConfiguration(System.getProperties());
    private static final Pattern URI_PATTERN = Pattern.compile("\\p{Alpha}[-+.\\p{Alnum}]*:[^\\\\]*");

    public static void main(String[] args) {
        GroovyMain.processArgs(args, System.out);
    }

    static void processArgs(String[] args, PrintStream out) {
        Options options = GroovyMain.buildOptions();
        try {
            CommandLine cmd = GroovyMain.parseCommandLine(options, args);
            if (cmd.hasOption('h')) {
                GroovyMain.printHelp(out, options);
            } else if (cmd.hasOption('v')) {
                String version = GroovySystem.getVersion();
                out.println("Groovy Version: " + version + " JVM: " + System.getProperty("java.version") + " Vendor: " + System.getProperty("java.vm.vendor") + " OS: " + System.getProperty("os.name"));
            } else if (!GroovyMain.process(cmd)) {
                System.exit(1);
            }
        }
        catch (ParseException pe) {
            out.println("error: " + pe.getMessage());
            GroovyMain.printHelp(out, options);
        }
        catch (IOException ioe) {
            out.println("error: " + ioe.getMessage());
        }
    }

    private static void printHelp(PrintStream out, Options options) {
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter pw = new PrintWriter(out);
        formatter.printHelp(pw, 80, "groovy [options] [args]", "options:", options, 2, 4, null, false);
        pw.flush();
    }

    private static CommandLine parseCommandLine(Options options, String[] args) throws ParseException {
        GroovyInternalPosixParser parser = new GroovyInternalPosixParser();
        return parser.parse(options, args, true);
    }

    private static synchronized Options buildOptions() {
        Options options = new Options();
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("path");
        OptionBuilder.withDescription("Specify where to find the class files - must be first argument");
        options.addOption(OptionBuilder.create("classpath"));
        OptionBuilder.withLongOpt("classpath");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("path");
        OptionBuilder.withDescription("Aliases for '-classpath'");
        options.addOption(OptionBuilder.create("cp"));
        OptionBuilder.withLongOpt("define");
        OptionBuilder.withDescription("define a system property");
        OptionBuilder.hasArgs(2);
        OptionBuilder.withValueSeparator();
        OptionBuilder.withArgName("name=value");
        options.addOption(OptionBuilder.create('D'));
        OptionBuilder.withLongOpt("disableopt");
        OptionBuilder.withDescription("disables one or all optimization elements. optlist can be a comma separated list with the elements: all (disables all optimizations), int (disable any int based optimizations)");
        OptionBuilder.hasArg(true);
        OptionBuilder.withArgName("optlist");
        options.addOption(OptionBuilder.create());
        OptionBuilder.hasArg(false);
        OptionBuilder.withDescription("usage information");
        OptionBuilder.withLongOpt("help");
        options.addOption(OptionBuilder.create('h'));
        OptionBuilder.hasArg(false);
        OptionBuilder.withDescription("debug mode will print out full stack traces");
        OptionBuilder.withLongOpt("debug");
        options.addOption(OptionBuilder.create('d'));
        OptionBuilder.hasArg(false);
        OptionBuilder.withDescription("display the Groovy and JVM versions");
        OptionBuilder.withLongOpt("version");
        options.addOption(OptionBuilder.create('v'));
        OptionBuilder.withArgName("charset");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("specify the encoding of the files");
        OptionBuilder.withLongOpt("encoding");
        options.addOption(OptionBuilder.create('c'));
        OptionBuilder.withArgName("script");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("specify a command line script");
        options.addOption(OptionBuilder.create('e'));
        OptionBuilder.withArgName("extension");
        OptionBuilder.hasOptionalArg();
        OptionBuilder.withDescription("modify files in place; create backup if extension is given (e.g. '.bak')");
        options.addOption(OptionBuilder.create('i'));
        OptionBuilder.hasArg(false);
        OptionBuilder.withDescription("process files line by line using implicit 'line' variable");
        options.addOption(OptionBuilder.create('n'));
        OptionBuilder.hasArg(false);
        OptionBuilder.withDescription("process files line by line and print result (see also -n)");
        options.addOption(OptionBuilder.create('p'));
        OptionBuilder.withArgName("port");
        OptionBuilder.hasOptionalArg();
        OptionBuilder.withDescription("listen on a port and process inbound lines (default: 1960)");
        options.addOption(OptionBuilder.create('l'));
        OptionBuilder.withArgName("splitPattern");
        OptionBuilder.hasOptionalArg();
        OptionBuilder.withDescription("split lines using splitPattern (default '\\s') using implicit 'split' variable");
        OptionBuilder.withLongOpt("autosplit");
        options.addOption(OptionBuilder.create('a'));
        OptionBuilder.withLongOpt("indy");
        OptionBuilder.withDescription("enables compilation using invokedynamic");
        options.addOption(OptionBuilder.create());
        OptionBuilder.withLongOpt("configscript");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("A script for tweaking the configuration options");
        options.addOption(OptionBuilder.create());
        OptionBuilder.withLongOpt("basescript");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("class");
        OptionBuilder.withDescription("Base class name for scripts (must derive from Script)");
        options.addOption(OptionBuilder.create('b'));
        return options;
    }

    private static boolean process(CommandLine line) throws ParseException, IOException {
        String[] deopts;
        List args = line.getArgList();
        if (line.hasOption('D')) {
            Properties optionProperties = line.getOptionProperties("D");
            Enumeration<?> propertyNames = optionProperties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String nextName = (String)propertyNames.nextElement();
                System.setProperty(nextName, optionProperties.getProperty(nextName));
            }
        }
        GroovyMain main = new GroovyMain();
        main.conf.setSourceEncoding(line.getOptionValue('c', main.conf.getSourceEncoding()));
        main.isScriptFile = !line.hasOption('e');
        main.debug = line.hasOption('d');
        main.conf.setDebug(main.debug);
        main.processFiles = line.hasOption('p') || line.hasOption('n');
        main.autoOutput = line.hasOption('p');
        main.editFiles = line.hasOption('i');
        if (main.editFiles) {
            main.backupExtension = line.getOptionValue('i');
        }
        main.autoSplit = line.hasOption('a');
        String sp = line.getOptionValue('a');
        if (sp != null) {
            main.splitPattern = sp;
        }
        if (main.isScriptFile) {
            if (args.isEmpty()) {
                throw new ParseException("neither -e or filename provided");
            }
            main.script = (String)args.remove(0);
            if (main.script.endsWith(".java")) {
                throw new ParseException("error: cannot compile file with .java extension: " + main.script);
            }
        } else {
            main.script = line.getOptionValue('e');
        }
        main.processSockets = line.hasOption('l');
        if (main.processSockets) {
            String p = line.getOptionValue('l', "1960");
            main.port = Integer.parseInt(p);
        }
        String disabled = line.getOptionValue("disableopt", ",");
        for (String deopt_i : deopts = disabled.split(",")) {
            main.conf.getOptimizationOptions().put(deopt_i, false);
        }
        if (line.hasOption("indy")) {
            CompilerConfiguration.DEFAULT.getOptimizationOptions().put("indy", true);
            main.conf.getOptimizationOptions().put("indy", true);
        }
        if (line.hasOption("basescript")) {
            main.conf.setScriptBaseClass(line.getOptionValue("basescript"));
        }
        if (line.hasOption("configscript")) {
            String path = line.getOptionValue("configscript");
            File groovyConfigurator = new File(path);
            Binding binding = new Binding();
            binding.setVariable("configuration", main.conf);
            CompilerConfiguration configuratorConfig = new CompilerConfiguration();
            ImportCustomizer customizer = new ImportCustomizer();
            customizer.addStaticStars("org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder");
            configuratorConfig.addCompilationCustomizers(customizer);
            GroovyShell shell = new GroovyShell(binding, configuratorConfig);
            shell.evaluate(groovyConfigurator);
        }
        main.args = args;
        return main.run();
    }

    private boolean run() {
        try {
            if (this.processSockets) {
                this.processSockets();
            } else if (this.processFiles) {
                this.processFiles();
            } else {
                this.processOnce();
            }
            return true;
        }
        catch (CompilationFailedException e) {
            System.err.println(e);
            return false;
        }
        catch (Throwable e) {
            if (e instanceof InvokerInvocationException) {
                InvokerInvocationException iie = (InvokerInvocationException)e;
                e = iie.getCause();
            }
            System.err.println("Caught: " + e);
            if (!this.debug) {
                StackTraceUtils.deepSanitize(e);
            }
            e.printStackTrace();
            return false;
        }
    }

    private void processSockets() throws CompilationFailedException, IOException, URISyntaxException {
        GroovyShell groovy = new GroovyShell(this.conf);
        new GroovySocketServer(groovy, this.getScriptSource(this.isScriptFile, this.script), this.autoOutput, this.port);
    }

    @Deprecated
    public String getText(String uriOrFilename) throws IOException {
        if (URI_PATTERN.matcher(uriOrFilename).matches()) {
            try {
                return ResourceGroovyMethods.getText(new URL(uriOrFilename));
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Unable to get script from URL: ", e);
            }
        }
        return ResourceGroovyMethods.getText(this.huntForTheScriptFile(uriOrFilename));
    }

    protected GroovyCodeSource getScriptSource(boolean isScriptFile, String script) throws IOException, URISyntaxException {
        if (isScriptFile) {
            File scriptFile = this.huntForTheScriptFile(script);
            if (!scriptFile.exists() && URI_PATTERN.matcher(script).matches()) {
                return new GroovyCodeSource(new URI(script));
            }
            return new GroovyCodeSource(scriptFile);
        }
        return new GroovyCodeSource(script, "script_from_command_line", "/groovy/shell");
    }

    public static File searchForGroovyScriptFile(String input) {
        String scriptFileName = input.trim();
        File scriptFile = new File(scriptFileName);
        String[] standardExtensions = new String[]{".groovy", ".gvy", ".gy", ".gsh"};
        for (int i = 0; i < standardExtensions.length && !scriptFile.exists(); ++i) {
            scriptFile = new File(scriptFileName + standardExtensions[i]);
        }
        if (!scriptFile.exists()) {
            scriptFile = new File(scriptFileName);
        }
        return scriptFile;
    }

    public File huntForTheScriptFile(String input) {
        return GroovyMain.searchForGroovyScriptFile(input);
    }

    private static void setupContextClassLoader(GroovyShell shell) {
        Thread current = Thread.currentThread();
        class DoSetContext
        implements PrivilegedAction {
            ClassLoader classLoader;
            final /* synthetic */ Thread val$current;

            public DoSetContext(ClassLoader classLoader) {
                this.val$current = classLoader;
                this.classLoader = loader;
            }

            public Object run() {
                this.val$current.setContextClassLoader(this.classLoader);
                return null;
            }
        }
        AccessController.doPrivileged(new DoSetContext((ClassLoader)shell.getClassLoader(), current));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processFiles() throws CompilationFailedException, IOException, URISyntaxException {
        GroovyShell groovy = new GroovyShell(this.conf);
        GroovyMain.setupContextClassLoader(groovy);
        Script s = groovy.parse(this.getScriptSource(this.isScriptFile, this.script));
        if (this.args.isEmpty()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(System.out);
            try {
                this.processReader(s, reader, writer);
            }
            finally {
                writer.close();
                reader.close();
            }
        } else {
            for (String filename : this.args) {
                File file = this.huntForTheScriptFile(filename);
                this.processFile(s, file);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processFile(Script s, File file) throws IOException {
        File backup;
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        if (!this.editFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(System.out);
            try {
                this.processReader(s, reader, writer);
            }
            finally {
                writer.close();
                reader.close();
            }
        }
        if (this.backupExtension == null) {
            backup = File.createTempFile("groovy_", ".tmp");
            backup.deleteOnExit();
        } else {
            backup = new File(file.getPath() + this.backupExtension);
        }
        backup.delete();
        if (!file.renameTo(backup)) {
            throw new IOException("unable to rename " + file + " to " + backup);
        }
        BufferedReader reader = new BufferedReader(new FileReader(backup));
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        try {
            this.processReader(s, reader, writer);
        }
        finally {
            writer.close();
            reader.close();
        }
    }

    private void processReader(Script s, BufferedReader reader, PrintWriter pw) throws IOException {
        String line;
        String lineCountName = "count";
        s.setProperty(lineCountName, BigInteger.ZERO);
        String autoSplitName = "split";
        s.setProperty("out", pw);
        try {
            InvokerHelper.invokeMethod(s, "begin", null);
        }
        catch (MissingMethodException missingMethodException) {
            // empty catch block
        }
        while ((line = reader.readLine()) != null) {
            s.setProperty("line", line);
            s.setProperty(lineCountName, ((BigInteger)s.getProperty(lineCountName)).add(BigInteger.ONE));
            if (this.autoSplit) {
                s.setProperty(autoSplitName, line.split(this.splitPattern));
            }
            Object o = s.run();
            if (!this.autoOutput || o == null) continue;
            pw.println(o);
        }
        try {
            InvokerHelper.invokeMethod(s, "end", null);
        }
        catch (MissingMethodException missingMethodException) {
            // empty catch block
        }
    }

    private void processOnce() throws CompilationFailedException, IOException, URISyntaxException {
        GroovyShell groovy = new GroovyShell(this.conf);
        GroovyMain.setupContextClassLoader(groovy);
        groovy.run(this.getScriptSource(this.isScriptFile, this.script), this.args);
    }
}

