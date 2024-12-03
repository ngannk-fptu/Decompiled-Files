/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import groovy.lang.Binding;
import groovy.lang.GroovyResourceLoader;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.GroovyInternalPosixParser;
import groovyjarjarcommonscli.HelpFormatter;
import groovyjarjarcommonscli.OptionBuilder;
import groovyjarjarcommonscli.Options;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ConfigurationException;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.DefaultGroovyStaticMethods;
import org.codehaus.groovy.tools.ErrorReporter;
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit;

public class FileSystemCompiler {
    private final CompilationUnit unit;
    private static boolean displayStackTraceOnError = false;

    public FileSystemCompiler(CompilerConfiguration configuration) throws ConfigurationException {
        this(configuration, null);
    }

    public FileSystemCompiler(CompilerConfiguration configuration, CompilationUnit cu) throws ConfigurationException {
        this.unit = cu != null ? cu : (configuration.getJointCompilationOptions() != null ? new JavaAwareCompilationUnit(configuration) : new CompilationUnit(configuration));
    }

    public void compile(String[] paths) throws Exception {
        this.unit.addSources(paths);
        this.unit.compile();
    }

    public void compile(File[] files) throws Exception {
        this.unit.addSources(files);
        this.unit.compile();
    }

    public static void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(80, "groovyc [options] <source-files>", "options:", options, "");
    }

    public static void displayVersion() {
        String version = GroovySystem.getVersion();
        System.err.println("Groovy compiler version " + version);
        System.err.println("Copyright 2003-2018 The Apache Software Foundation. http://groovy-lang.org/");
        System.err.println("");
    }

    public static int checkFiles(String[] filenames) {
        int errors = 0;
        for (String filename : filenames) {
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("error: file not found: " + file);
                ++errors;
                continue;
            }
            if (file.canRead()) continue;
            System.err.println("error: file not readable: " + file);
            ++errors;
        }
        return errors;
    }

    public static boolean validateFiles(String[] filenames) {
        return FileSystemCompiler.checkFiles(filenames) == 0;
    }

    public static void commandLineCompile(String[] args) throws Exception {
        FileSystemCompiler.commandLineCompile(args, true);
    }

    public static void commandLineCompile(String[] args, boolean lookupUnnamedFiles) throws Exception {
        boolean fileNameErrors;
        GroovyInternalPosixParser cliParser = new GroovyInternalPosixParser();
        Options options = FileSystemCompiler.createCompilationOptions();
        CommandLine cli = cliParser.parse(options, args);
        if (cli.hasOption('h')) {
            FileSystemCompiler.displayHelp(options);
            return;
        }
        if (cli.hasOption('v')) {
            FileSystemCompiler.displayVersion();
            return;
        }
        displayStackTraceOnError = cli.hasOption('e');
        CompilerConfiguration configuration = FileSystemCompiler.generateCompilerConfigurationFromOptions(cli);
        String[] filenames = FileSystemCompiler.generateFileNamesFromOptions(cli);
        boolean bl = fileNameErrors = filenames == null;
        if (!fileNameErrors && filenames.length == 0) {
            FileSystemCompiler.displayHelp(options);
            return;
        }
        boolean bl2 = fileNameErrors = fileNameErrors && !FileSystemCompiler.validateFiles(filenames);
        if (!fileNameErrors) {
            FileSystemCompiler.doCompilation(configuration, null, filenames, lookupUnnamedFiles);
        }
    }

    public static void main(String[] args) {
        FileSystemCompiler.commandLineCompileWithErrorHandling(args, true);
    }

    public static void commandLineCompileWithErrorHandling(String[] args, boolean lookupUnnamedFiles) {
        try {
            FileSystemCompiler.commandLineCompile(args, lookupUnnamedFiles);
        }
        catch (Throwable e) {
            new ErrorReporter(e, displayStackTraceOnError).write(System.err);
            System.exit(1);
        }
    }

    public static void doCompilation(CompilerConfiguration configuration, CompilationUnit unit, String[] filenames) throws Exception {
        FileSystemCompiler.doCompilation(configuration, unit, filenames, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void doCompilation(CompilerConfiguration configuration, CompilationUnit unit, String[] filenames, boolean lookupUnnamedFiles) throws Exception {
        File tmpDir = null;
        try {
            if (configuration.getJointCompilationOptions() != null && !configuration.getJointCompilationOptions().containsKey("stubDir")) {
                tmpDir = DefaultGroovyStaticMethods.createTempDir(null, "groovy-generated-", "-java-source");
                configuration.getJointCompilationOptions().put("stubDir", tmpDir);
            }
            FileSystemCompiler compiler = new FileSystemCompiler(configuration, unit);
            if (lookupUnnamedFiles) {
                for (String filename : filenames) {
                    File file = new File(filename);
                    if (!file.isFile()) continue;
                    URL url = file.getAbsoluteFile().getParentFile().toURI().toURL();
                    compiler.unit.getClassLoader().addURL(url);
                }
            } else {
                compiler.unit.getClassLoader().setResourceLoader(new GroovyResourceLoader(){

                    @Override
                    public URL loadGroovySource(String filename) throws MalformedURLException {
                        return null;
                    }
                });
            }
            compiler.compile(filenames);
        }
        catch (Throwable throwable) {
            try {
                if (tmpDir != null) {
                    FileSystemCompiler.deleteRecursive(tmpDir);
                }
            }
            catch (Throwable t) {
                System.err.println("error: could not delete temp files - " + tmpDir.getPath());
            }
            throw throwable;
        }
        try {
            if (tmpDir != null) {
                FileSystemCompiler.deleteRecursive(tmpDir);
            }
        }
        catch (Throwable t) {
            System.err.println("error: could not delete temp files - " + tmpDir.getPath());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String[] generateFileNamesFromOptions(CommandLine cli) {
        String[] filenames = cli.getArgs();
        ArrayList<String> fileList = new ArrayList<String>(filenames.length);
        boolean errors = false;
        for (String filename : filenames) {
            if (filename.startsWith("@")) {
                String fn = filename.substring(1);
                BufferedReader br = null;
                try {
                    String file;
                    br = new BufferedReader(new FileReader(fn));
                    while ((file = br.readLine()) != null) {
                        fileList.add(file);
                    }
                    continue;
                }
                catch (IOException ioe) {
                    System.err.println("error: file not readable: " + fn);
                    errors = true;
                    continue;
                }
                finally {
                    if (null != br) {
                        try {
                            br.close();
                        }
                        catch (IOException e) {
                            System.err.println("error: failed to close buffered reader: " + fn);
                            errors = true;
                        }
                    }
                }
            }
            fileList.add(filename);
        }
        if (errors) {
            return null;
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    public static CompilerConfiguration generateCompilerConfigurationFromOptions(CommandLine cli) throws IOException {
        CompilerConfiguration configuration = new CompilerConfiguration();
        if (cli.hasOption("classpath")) {
            configuration.setClasspath(cli.getOptionValue("classpath"));
        }
        if (cli.hasOption('d')) {
            configuration.setTargetDirectory(cli.getOptionValue('d'));
        }
        if (cli.hasOption("encoding")) {
            configuration.setSourceEncoding(cli.getOptionValue("encoding"));
        }
        if (cli.hasOption("basescript")) {
            configuration.setScriptBaseClass(cli.getOptionValue("basescript"));
        }
        if (cli.hasOption('j')) {
            HashMap<String, Object> compilerOptions = new HashMap<String, Object>();
            String[] opts = cli.getOptionValues("J");
            compilerOptions.put("namedValues", opts);
            opts = cli.getOptionValues("F");
            compilerOptions.put("flags", opts);
            configuration.setJointCompilationOptions(compilerOptions);
        }
        if (cli.hasOption("indy")) {
            configuration.getOptimizationOptions().put("int", false);
            configuration.getOptimizationOptions().put("indy", true);
        }
        if (cli.hasOption("configscript")) {
            String path = cli.getOptionValue("configscript");
            File groovyConfigurator = new File(path);
            Binding binding = new Binding();
            binding.setVariable("configuration", configuration);
            CompilerConfiguration configuratorConfig = new CompilerConfiguration();
            ImportCustomizer customizer = new ImportCustomizer();
            customizer.addStaticStars("org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder");
            configuratorConfig.addCompilationCustomizers(customizer);
            GroovyShell shell = new GroovyShell(binding, configuratorConfig);
            shell.evaluate(groovyConfigurator);
        }
        return configuration;
    }

    public static Options createCompilationOptions() {
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
        OptionBuilder.withLongOpt("sourcepath");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("path");
        OptionBuilder.withDescription("Specify where to find the source files");
        options.addOption(OptionBuilder.create());
        OptionBuilder.withLongOpt("temp");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("temp");
        OptionBuilder.withDescription("Specify temporary directory");
        options.addOption(OptionBuilder.create());
        OptionBuilder.withLongOpt("encoding");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("encoding");
        OptionBuilder.withDescription("Specify the encoding of the user class files");
        options.addOption(OptionBuilder.create());
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Specify where to place generated class files");
        options.addOption(OptionBuilder.create('d'));
        OptionBuilder.withLongOpt("help");
        OptionBuilder.withDescription("Print a synopsis of standard options");
        options.addOption(OptionBuilder.create('h'));
        OptionBuilder.withLongOpt("version");
        OptionBuilder.withDescription("Print the version");
        options.addOption(OptionBuilder.create('v'));
        OptionBuilder.withLongOpt("exception");
        OptionBuilder.withDescription("Print stack trace on error");
        options.addOption(OptionBuilder.create('e'));
        OptionBuilder.withLongOpt("jointCompilation");
        OptionBuilder.withDescription("Attach javac compiler to compile .java files");
        options.addOption(OptionBuilder.create('j'));
        OptionBuilder.withLongOpt("basescript");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("class");
        OptionBuilder.withDescription("Base class name for scripts (must derive from Script)");
        options.addOption(OptionBuilder.create('b'));
        OptionBuilder.withArgName("property=value");
        OptionBuilder.withValueSeparator();
        OptionBuilder.hasArgs(2);
        OptionBuilder.withDescription("name-value pairs to pass to javac");
        options.addOption(OptionBuilder.create("J"));
        OptionBuilder.withArgName("flag");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("passed to javac for joint compilation");
        options.addOption(OptionBuilder.create("F"));
        OptionBuilder.withLongOpt("indy");
        OptionBuilder.withDescription("enables compilation using invokedynamic");
        options.addOption(OptionBuilder.create());
        OptionBuilder.withLongOpt("configscript");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("A script for tweaking the configuration options");
        options.addOption(OptionBuilder.create());
        return options;
    }

    @Deprecated
    public static File createTempDir() throws IOException {
        return DefaultGroovyStaticMethods.createTempDir(null);
    }

    public static void deleteRecursive(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; ++i) {
                FileSystemCompiler.deleteRecursive(files[i]);
            }
            file.delete();
        }
    }
}

