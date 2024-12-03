/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.codehaus.groovy.control.BytecodeProcessor;
import org.codehaus.groovy.control.ConfigurationException;
import org.codehaus.groovy.control.ParserPluginFactory;
import org.codehaus.groovy.control.SourceExtensionHandler;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.io.NullWriter;

public class CompilerConfiguration {
    private static final String JDK5_CLASSNAME_CHECK = "java.lang.annotation.Annotation";
    public static final String INVOKEDYNAMIC = "indy";
    public static final String JDK4 = "1.4";
    public static final String JDK5 = "1.5";
    public static final String JDK6 = "1.6";
    public static final String JDK7 = "1.7";
    public static final String JDK8 = "1.8";
    public static final String POST_JDK5 = "1.5";
    public static final String PRE_JDK5 = "1.4";
    private static final String[] ALLOWED_JDKS = new String[]{"1.4", "1.5", "1.6", "1.7", "1.8"};
    public static final String CURRENT_JVM_VERSION = CompilerConfiguration.getVMVersion();
    public static final CompilerConfiguration DEFAULT = new CompilerConfiguration();
    private int warningLevel;
    private String sourceEncoding;
    private PrintWriter output;
    private File targetDirectory;
    private LinkedList<String> classpath;
    private boolean verbose;
    private boolean debug;
    private int tolerance;
    private String scriptBaseClass;
    private ParserPluginFactory pluginFactory;
    private String defaultScriptExtension;
    private Set<String> scriptExtensions = new LinkedHashSet<String>();
    private boolean recompileGroovySource;
    private int minimumRecompilationInterval;
    private String targetBytecode;
    private Map<String, Object> jointCompilationOptions;
    private Map<String, Boolean> optimizationOptions;
    private List<CompilationCustomizer> compilationCustomizers = new LinkedList<CompilationCustomizer>();
    private Set<String> disabledGlobalASTTransformations;
    private BytecodeProcessor bytecodePostprocessor;

    public CompilerConfiguration() {
        this.setWarningLevel(1);
        this.setOutput(null);
        this.setTargetDirectory((File)null);
        this.setClasspath("");
        this.setVerbose(false);
        this.setDebug(false);
        this.setTolerance(10);
        this.setScriptBaseClass(null);
        this.setRecompileGroovySource(false);
        this.setMinimumRecompilationInterval(100);
        String targetByteCode = null;
        try {
            targetByteCode = System.getProperty("groovy.target.bytecode", targetByteCode);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (targetByteCode != null) {
            this.setTargetBytecode(targetByteCode);
        } else {
            this.setTargetBytecode(CompilerConfiguration.getVMVersion());
        }
        String tmpDefaultScriptExtension = null;
        try {
            tmpDefaultScriptExtension = System.getProperty("groovy.default.scriptExtension");
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (tmpDefaultScriptExtension != null) {
            this.setDefaultScriptExtension(tmpDefaultScriptExtension);
        } else {
            this.setDefaultScriptExtension(".groovy");
        }
        String encoding = null;
        try {
            encoding = System.getProperty("file.encoding", "US-ASCII");
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            encoding = System.getProperty("groovy.source.encoding", encoding);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.setSourceEncoding(encoding);
        try {
            this.setOutput(new PrintWriter(System.err));
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            String target = System.getProperty("groovy.target.directory");
            if (target != null) {
                this.setTargetDirectory(target);
            }
        }
        catch (Exception target) {
            // empty catch block
        }
        boolean indy = false;
        try {
            indy = Boolean.getBoolean("groovy.target.indy");
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (DEFAULT != null && Boolean.TRUE.equals(DEFAULT.getOptimizationOptions().get(INVOKEDYNAMIC))) {
            indy = true;
        }
        HashMap<String, Boolean> options = new HashMap<String, Boolean>(3);
        if (indy) {
            options.put(INVOKEDYNAMIC, Boolean.TRUE);
        }
        this.setOptimizationOptions(options);
    }

    public CompilerConfiguration(CompilerConfiguration configuration) {
        this.setWarningLevel(configuration.getWarningLevel());
        this.setOutput(configuration.getOutput());
        this.setTargetDirectory(configuration.getTargetDirectory());
        this.setClasspathList(new LinkedList<String>(configuration.getClasspath()));
        this.setVerbose(configuration.getVerbose());
        this.setDebug(configuration.getDebug());
        this.setTolerance(configuration.getTolerance());
        this.setScriptBaseClass(configuration.getScriptBaseClass());
        this.setRecompileGroovySource(configuration.getRecompileGroovySource());
        this.setMinimumRecompilationInterval(configuration.getMinimumRecompilationInterval());
        this.setTargetBytecode(configuration.getTargetBytecode());
        this.setDefaultScriptExtension(configuration.getDefaultScriptExtension());
        this.setSourceEncoding(configuration.getSourceEncoding());
        this.setTargetDirectory(configuration.getTargetDirectory());
        Map<String, Object> jointCompilationOptions = configuration.getJointCompilationOptions();
        if (jointCompilationOptions != null) {
            jointCompilationOptions = new HashMap<String, Object>(jointCompilationOptions);
        }
        this.setJointCompilationOptions(jointCompilationOptions);
        this.setPluginFactory(configuration.getPluginFactory());
        this.setScriptExtensions(configuration.getScriptExtensions());
        this.setOptimizationOptions(new HashMap<String, Boolean>(configuration.getOptimizationOptions()));
    }

    public CompilerConfiguration(Properties configuration) throws ConfigurationException {
        this();
        this.configure(configuration);
    }

    public static boolean isPostJDK5(String bytecodeVersion) {
        return "1.5".equals(bytecodeVersion) || JDK6.equals(bytecodeVersion) || JDK7.equals(bytecodeVersion) || JDK8.equals(bytecodeVersion);
    }

    public static boolean isPostJDK7(String bytecodeVersion) {
        return JDK7.equals(bytecodeVersion) || JDK8.equals(bytecodeVersion);
    }

    public void configure(Properties configuration) throws ConfigurationException {
        String text = null;
        int numeric = 0;
        numeric = this.getWarningLevel();
        try {
            text = configuration.getProperty("groovy.warnings", "likely errors");
            numeric = Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            text = text.toLowerCase();
            if (text.equals("none")) {
                numeric = 0;
            }
            if (text.startsWith("likely")) {
                numeric = 1;
            }
            if (text.startsWith("possible")) {
                numeric = 2;
            }
            if (text.startsWith("paranoia")) {
                numeric = 3;
            }
            throw new ConfigurationException("unrecognized groovy.warnings: " + text);
        }
        this.setWarningLevel(numeric);
        text = configuration.getProperty("groovy.source.encoding");
        if (text == null) {
            text = configuration.getProperty("file.encoding", "US-ASCII");
        }
        this.setSourceEncoding(text);
        text = configuration.getProperty("groovy.target.directory");
        if (text != null) {
            this.setTargetDirectory(text);
        }
        if ((text = configuration.getProperty("groovy.target.bytecode")) != null) {
            this.setTargetBytecode(text);
        }
        if ((text = configuration.getProperty("groovy.classpath")) != null) {
            this.setClasspath(text);
        }
        if ((text = configuration.getProperty("groovy.output.verbose")) != null && text.equalsIgnoreCase("true")) {
            this.setVerbose(true);
        }
        if ((text = configuration.getProperty("groovy.output.debug")) != null && text.equalsIgnoreCase("true")) {
            this.setDebug(true);
        }
        numeric = 10;
        try {
            text = configuration.getProperty("groovy.errors.tolerance", "10");
            numeric = Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            throw new ConfigurationException(e);
        }
        this.setTolerance(numeric);
        text = configuration.getProperty("groovy.script.base");
        if (text != null) {
            this.setScriptBaseClass(text);
        }
        if ((text = configuration.getProperty("groovy.recompile")) != null) {
            this.setRecompileGroovySource(text.equalsIgnoreCase("true"));
        }
        numeric = 100;
        try {
            text = configuration.getProperty("groovy.recompile.minimumIntervall");
            if (text == null) {
                text = configuration.getProperty("groovy.recompile.minimumInterval");
            }
            numeric = text != null ? Integer.parseInt(text) : 100;
        }
        catch (NumberFormatException e) {
            throw new ConfigurationException(e);
        }
        this.setMinimumRecompilationInterval(numeric);
        text = configuration.getProperty("groovy.disabled.global.ast.transformations");
        if (text != null) {
            String[] classNames = text.split(",\\s*}");
            HashSet<String> blacklist = new HashSet<String>(Arrays.asList(classNames));
            this.setDisabledGlobalASTTransformations(blacklist);
        }
    }

    public int getWarningLevel() {
        return this.warningLevel;
    }

    public void setWarningLevel(int level) {
        this.warningLevel = level < 0 || level > 3 ? 1 : level;
    }

    public String getSourceEncoding() {
        return this.sourceEncoding;
    }

    public void setSourceEncoding(String encoding) {
        if (encoding == null) {
            encoding = "US-ASCII";
        }
        this.sourceEncoding = encoding;
    }

    @Deprecated
    public PrintWriter getOutput() {
        return this.output;
    }

    @Deprecated
    public void setOutput(PrintWriter output) {
        this.output = output == null ? new PrintWriter(NullWriter.DEFAULT) : output;
    }

    public File getTargetDirectory() {
        return this.targetDirectory;
    }

    public void setTargetDirectory(String directory) {
        this.targetDirectory = directory != null && directory.length() > 0 ? new File(directory) : null;
    }

    public void setTargetDirectory(File directory) {
        this.targetDirectory = directory;
    }

    public List<String> getClasspath() {
        return this.classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = new LinkedList();
        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            this.classpath.add(tokenizer.nextToken());
        }
    }

    public void setClasspathList(List<String> parts) {
        this.classpath = new LinkedList<String>(parts);
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getTolerance() {
        return this.tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    public String getScriptBaseClass() {
        return this.scriptBaseClass;
    }

    public void setScriptBaseClass(String scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass;
    }

    public ParserPluginFactory getPluginFactory() {
        if (this.pluginFactory == null) {
            this.pluginFactory = ParserPluginFactory.newInstance();
        }
        return this.pluginFactory;
    }

    public void setPluginFactory(ParserPluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }

    public void setScriptExtensions(Set<String> scriptExtensions) {
        if (scriptExtensions == null) {
            scriptExtensions = new LinkedHashSet<String>();
        }
        this.scriptExtensions = scriptExtensions;
    }

    public Set<String> getScriptExtensions() {
        if (this.scriptExtensions == null || this.scriptExtensions.isEmpty()) {
            this.scriptExtensions = SourceExtensionHandler.getRegisteredExtensions(this.getClass().getClassLoader());
        }
        return this.scriptExtensions;
    }

    public String getDefaultScriptExtension() {
        return this.defaultScriptExtension;
    }

    public void setDefaultScriptExtension(String defaultScriptExtension) {
        this.defaultScriptExtension = defaultScriptExtension;
    }

    public void setRecompileGroovySource(boolean recompile) {
        this.recompileGroovySource = recompile;
    }

    public boolean getRecompileGroovySource() {
        return this.recompileGroovySource;
    }

    public void setMinimumRecompilationInterval(int time) {
        this.minimumRecompilationInterval = Math.max(0, time);
    }

    public int getMinimumRecompilationInterval() {
        return this.minimumRecompilationInterval;
    }

    public void setTargetBytecode(String version) {
        for (String allowedJdk : ALLOWED_JDKS) {
            if (!allowedJdk.equals(version)) continue;
            this.targetBytecode = version;
        }
    }

    public String getTargetBytecode() {
        return this.targetBytecode;
    }

    private static String getVMVersion() {
        try {
            Class.forName(JDK5_CLASSNAME_CHECK);
            return "1.5";
        }
        catch (Exception exception) {
            return "1.4";
        }
    }

    public Map<String, Object> getJointCompilationOptions() {
        return this.jointCompilationOptions;
    }

    public void setJointCompilationOptions(Map<String, Object> options) {
        this.jointCompilationOptions = options;
    }

    public Map<String, Boolean> getOptimizationOptions() {
        return this.optimizationOptions;
    }

    public void setOptimizationOptions(Map<String, Boolean> options) {
        if (options == null) {
            throw new IllegalArgumentException("provided option map must not be null");
        }
        this.optimizationOptions = options;
    }

    public CompilerConfiguration addCompilationCustomizers(CompilationCustomizer ... customizers) {
        if (customizers == null) {
            throw new IllegalArgumentException("provided customizers list must not be null");
        }
        this.compilationCustomizers.addAll(Arrays.asList(customizers));
        return this;
    }

    public List<CompilationCustomizer> getCompilationCustomizers() {
        return this.compilationCustomizers;
    }

    public Set<String> getDisabledGlobalASTTransformations() {
        return this.disabledGlobalASTTransformations;
    }

    public void setDisabledGlobalASTTransformations(Set<String> disabledGlobalASTTransformations) {
        this.disabledGlobalASTTransformations = disabledGlobalASTTransformations;
    }

    public BytecodeProcessor getBytecodePostprocessor() {
        return this.bytecodePostprocessor;
    }

    public void setBytecodePostprocessor(BytecodeProcessor bytecodePostprocessor) {
        this.bytecodePostprocessor = bytecodePostprocessor;
    }
}

