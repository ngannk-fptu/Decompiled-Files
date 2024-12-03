/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.StringUtils;

public abstract class DefaultCompilerAdapter
implements CompilerAdapter,
CompilerAdapterExtension {
    private static final int COMMAND_LINE_LIMIT = Os.isFamily("os/2") ? 1000 : 4096;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    @Deprecated
    protected static final String lSep = StringUtils.LINE_SEP;
    private static final Pattern JAVAC_ARG_FILE_CHARS_TO_QUOTE = Pattern.compile("[ #]");
    protected Path src;
    protected File destDir;
    protected String encoding;
    protected boolean debug = false;
    protected boolean optimize = false;
    protected boolean deprecation = false;
    protected boolean depend = false;
    protected boolean verbose = false;
    protected String target;
    protected String release;
    protected Path bootclasspath;
    protected Path extdirs;
    protected Path compileClasspath;
    protected Path modulepath;
    protected Path upgrademodulepath;
    protected Path compileSourcepath;
    protected Path moduleSourcepath;
    protected Project project;
    protected Location location;
    protected boolean includeAntRuntime;
    protected boolean includeJavaRuntime;
    protected String memoryInitialSize;
    protected String memoryMaximumSize;
    protected File[] compileList;
    protected Javac attributes;

    @Override
    public void setJavac(Javac attributes) {
        this.attributes = attributes;
        this.src = attributes.getSrcdir();
        this.destDir = attributes.getDestdir();
        this.encoding = attributes.getEncoding();
        this.debug = attributes.getDebug();
        this.optimize = attributes.getOptimize();
        this.deprecation = attributes.getDeprecation();
        this.depend = attributes.getDepend();
        this.verbose = attributes.getVerbose();
        this.target = attributes.getTarget();
        this.release = attributes.getRelease();
        this.bootclasspath = attributes.getBootclasspath();
        this.extdirs = attributes.getExtdirs();
        this.compileList = attributes.getFileList();
        this.compileClasspath = attributes.getClasspath();
        this.modulepath = attributes.getModulepath();
        this.upgrademodulepath = attributes.getUpgrademodulepath();
        this.compileSourcepath = attributes.getSourcepath();
        this.moduleSourcepath = attributes.getModulesourcepath();
        this.project = attributes.getProject();
        this.location = attributes.getLocation();
        this.includeAntRuntime = attributes.getIncludeantruntime();
        this.includeJavaRuntime = attributes.getIncludejavaruntime();
        this.memoryInitialSize = attributes.getMemoryInitialSize();
        this.memoryMaximumSize = attributes.getMemoryMaximumSize();
        if (this.moduleSourcepath != null && this.src == null && this.compileSourcepath == null) {
            this.compileSourcepath = new Path(this.getProject());
        }
    }

    public Javac getJavac() {
        return this.attributes;
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[]{"java"};
    }

    protected Project getProject() {
        return this.project;
    }

    protected Path getCompileClasspath() {
        Path cp;
        Path classpath = new Path(this.project);
        if (this.destDir != null && this.getJavac().isIncludeDestClasses()) {
            classpath.setLocation(this.destDir);
        }
        if ((cp = this.compileClasspath) == null) {
            cp = new Path(this.project);
        }
        if (this.includeAntRuntime) {
            classpath.addExisting(cp.concatSystemClasspath("last"));
        } else {
            classpath.addExisting(cp.concatSystemClasspath("ignore"));
        }
        if (this.includeJavaRuntime) {
            classpath.addJavaRuntime();
        }
        return classpath;
    }

    protected Path getModulepath() {
        Path mp = new Path(this.getProject());
        if (this.modulepath != null) {
            mp.addExisting(this.modulepath);
        }
        return mp;
    }

    protected Path getUpgrademodulepath() {
        Path ump = new Path(this.getProject());
        if (this.upgrademodulepath != null) {
            ump.addExisting(this.upgrademodulepath);
        }
        return ump;
    }

    protected Path getModulesourcepath() {
        Path msp = new Path(this.getProject());
        if (this.moduleSourcepath != null) {
            msp.add(this.moduleSourcepath);
        }
        return msp;
    }

    protected Commandline setupJavacCommandlineSwitches(Commandline cmd) {
        return this.setupJavacCommandlineSwitches(cmd, false);
    }

    protected Commandline setupJavacCommandlineSwitches(Commandline cmd, boolean useDebugLevel) {
        String memoryParameterPrefix;
        Path classpath = this.getCompileClasspath();
        Path sourcepath = this.compileSourcepath != null ? this.compileSourcepath : this.src;
        String string = memoryParameterPrefix = this.assumeJava1_2Plus() ? "-J-X" : "-J-";
        if (this.memoryInitialSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log("Since fork is false, ignoring memoryInitialSize setting.", 1);
            } else {
                cmd.createArgument().setValue(memoryParameterPrefix + "ms" + this.memoryInitialSize);
            }
        }
        if (this.memoryMaximumSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log("Since fork is false, ignoring memoryMaximumSize setting.", 1);
            } else {
                cmd.createArgument().setValue(memoryParameterPrefix + "mx" + this.memoryMaximumSize);
            }
        }
        if (this.attributes.getNowarn()) {
            cmd.createArgument().setValue("-nowarn");
        }
        if (this.deprecation) {
            cmd.createArgument().setValue("-deprecation");
        }
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir);
        }
        cmd.createArgument().setValue("-classpath");
        if (!this.assumeJava1_2Plus()) {
            Path cp = new Path(this.project);
            Optional.ofNullable(this.getBootClassPath()).ifPresent(cp::append);
            if (this.extdirs != null) {
                cp.addExtdirs(this.extdirs);
            }
            cp.append(classpath);
            cp.append(sourcepath);
            cmd.createArgument().setPath(cp);
        } else {
            cmd.createArgument().setPath(classpath);
            if (sourcepath.size() > 0) {
                cmd.createArgument().setValue("-sourcepath");
                cmd.createArgument().setPath(sourcepath);
            }
            if (this.release == null || !this.assumeJava9Plus()) {
                Path bp;
                if (this.target != null) {
                    cmd.createArgument().setValue("-target");
                    cmd.createArgument().setValue(this.target);
                }
                if (!(bp = this.getBootClassPath()).isEmpty()) {
                    cmd.createArgument().setValue("-bootclasspath");
                    cmd.createArgument().setPath(bp);
                }
            }
            if (this.extdirs != null && !this.extdirs.isEmpty()) {
                cmd.createArgument().setValue("-extdirs");
                cmd.createArgument().setPath(this.extdirs);
            }
        }
        if (this.encoding != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(this.encoding);
        }
        if (this.debug) {
            if (useDebugLevel && this.assumeJava1_2Plus()) {
                String debugLevel = this.attributes.getDebugLevel();
                if (debugLevel != null) {
                    cmd.createArgument().setValue("-g:" + debugLevel);
                } else {
                    cmd.createArgument().setValue("-g");
                }
            } else {
                cmd.createArgument().setValue("-g");
            }
        } else if (this.getNoDebugArgument() != null) {
            cmd.createArgument().setValue(this.getNoDebugArgument());
        }
        if (this.optimize) {
            cmd.createArgument().setValue("-O");
        }
        if (this.depend) {
            if (this.assumeJava1_3Plus()) {
                this.attributes.log("depend attribute is not supported by the modern compiler", 1);
            } else if (this.assumeJava1_2Plus()) {
                cmd.createArgument().setValue("-Xdepend");
            } else {
                cmd.createArgument().setValue("-depend");
            }
        }
        if (this.verbose) {
            cmd.createArgument().setValue("-verbose");
        }
        this.addCurrentCompilerArgs(cmd);
        return cmd;
    }

    protected Commandline setupModernJavacCommandlineSwitches(Commandline cmd) {
        Path ump;
        Path mp;
        Path msp;
        this.setupJavacCommandlineSwitches(cmd, true);
        if (this.assumeJava1_4Plus()) {
            String t = this.attributes.getTarget();
            String s = this.attributes.getSource();
            if (this.release == null || !this.assumeJava9Plus()) {
                if (this.release != null) {
                    this.attributes.log("Support for javac --release has been added in Java9 ignoring it");
                }
                if (s != null) {
                    cmd.createArgument().setValue("-source");
                    cmd.createArgument().setValue(this.adjustSourceValue(s));
                } else if (t != null && this.mustSetSourceForTarget(t)) {
                    this.setImplicitSourceSwitch(cmd, t, this.adjustSourceValue(t));
                }
            } else {
                if (t != null || s != null || this.getBootClassPath().size() > 0) {
                    this.attributes.log("Ignoring source, target and bootclasspath as release has been set", 1);
                }
                cmd.createArgument().setValue("--release");
                cmd.createArgument().setValue(this.release);
            }
        }
        if (!(msp = this.getModulesourcepath()).isEmpty()) {
            cmd.createArgument().setValue("--module-source-path");
            cmd.createArgument().setPath(msp);
        }
        if (!(mp = this.getModulepath()).isEmpty()) {
            cmd.createArgument().setValue("--module-path");
            cmd.createArgument().setPath(mp);
        }
        if (!(ump = this.getUpgrademodulepath()).isEmpty()) {
            cmd.createArgument().setValue("--upgrade-module-path");
            cmd.createArgument().setPath(ump);
        }
        if (this.attributes.getNativeHeaderDir() != null) {
            if (!this.assumeJava1_8Plus()) {
                this.attributes.log("Support for javac -h has been added in Java8, ignoring it");
            } else {
                cmd.createArgument().setValue("-h");
                cmd.createArgument().setFile(this.attributes.getNativeHeaderDir());
            }
        }
        return cmd;
    }

    protected Commandline setupModernJavacCommand() {
        Commandline cmd = new Commandline();
        this.setupModernJavacCommandlineSwitches(cmd);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }

    protected Commandline setupJavacCommand() {
        return this.setupJavacCommand(false);
    }

    protected Commandline setupJavacCommand(boolean debugLevelCheck) {
        Commandline cmd = new Commandline();
        this.setupJavacCommandlineSwitches(cmd, debugLevelCheck);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }

    protected void logAndAddFilesToCompile(Commandline cmd) {
        this.attributes.log("Compilation " + cmd.describeArguments(), 3);
        this.attributes.log(String.format("%s to be compiled:", this.compileList.length == 1 ? "File" : "Files"), 3);
        this.attributes.log(Stream.of(this.compileList).map(File::getAbsolutePath).peek(arg -> cmd.createArgument().setValue((String)arg)).map(arg -> String.format("    %s%n", arg)).collect(Collectors.joining("")), 3);
    }

    protected int executeExternalCompile(String[] args, int firstFileName) {
        return this.executeExternalCompile(args, firstFileName, true);
    }

    protected int executeExternalCompile(String[] args, int firstFileName, boolean quoteFiles) {
        String[] commandArray = null;
        File tmpFile = null;
        try {
            block19: {
                if (Commandline.toString(args).length() > COMMAND_LINE_LIMIT && firstFileName >= 0) {
                    try {
                        tmpFile = FILE_UTILS.createTempFile(this.getProject(), "files", "", this.getJavac().getTempdir(), true, true);
                        try (BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile));){
                            for (int i = firstFileName; i < args.length; ++i) {
                                if (quoteFiles && JAVAC_ARG_FILE_CHARS_TO_QUOTE.matcher(args[i]).find()) {
                                    args[i] = args[i].replace(File.separatorChar, '/');
                                    out.write("\"" + args[i] + "\"");
                                } else {
                                    out.write(args[i]);
                                }
                                out.newLine();
                            }
                            out.flush();
                            commandArray = new String[firstFileName + 1];
                            System.arraycopy(args, 0, commandArray, 0, firstFileName);
                            commandArray[firstFileName] = "@" + tmpFile;
                            break block19;
                        }
                    }
                    catch (IOException e) {
                        throw new BuildException("Error creating temporary file", e, this.location);
                    }
                }
                commandArray = args;
            }
            try {
                Execute exe = new Execute(new LogStreamHandler(this.attributes, 2, 1));
                if (Os.isFamily("openvms")) {
                    exe.setVMLauncher(true);
                }
                exe.setAntRun(this.project);
                exe.setWorkingDirectory(this.project.getBaseDir());
                exe.setCommandline(commandArray);
                exe.execute();
                int n = exe.getExitValue();
                return n;
            }
            catch (IOException e) {
                throw new BuildException("Error running " + args[0] + " compiler", e, this.location);
            }
        }
        finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }

    @Deprecated
    protected void addExtdirsToClasspath(Path classpath) {
        classpath.addExtdirs(this.extdirs);
    }

    protected void addCurrentCompilerArgs(Commandline cmd) {
        cmd.addArguments(this.getJavac().getCurrentCompilerArgs());
    }

    @Deprecated
    protected boolean assumeJava11() {
        return this.assumeJava1_1Plus() && !this.assumeJava1_2Plus();
    }

    protected boolean assumeJava1_1Plus() {
        return "javac1.1".equalsIgnoreCase(this.attributes.getCompilerVersion()) || this.assumeJava1_2Plus();
    }

    @Deprecated
    protected boolean assumeJava12() {
        return this.assumeJava1_2Plus() && !this.assumeJava1_3Plus();
    }

    protected boolean assumeJava1_2Plus() {
        return "javac1.2".equalsIgnoreCase(this.attributes.getCompilerVersion()) || this.assumeJava1_3Plus();
    }

    @Deprecated
    protected boolean assumeJava13() {
        return this.assumeJava1_3Plus() && !this.assumeJava1_4Plus();
    }

    protected boolean assumeJava1_3Plus() {
        return "javac1.3".equalsIgnoreCase(this.attributes.getCompilerVersion()) || this.assumeJava1_4Plus();
    }

    @Deprecated
    protected boolean assumeJava14() {
        return this.assumeJava1_4Plus() && !this.assumeJava1_5Plus();
    }

    protected boolean assumeJava1_4Plus() {
        return this.assumeJavaXY("javac1.4", "1.4") || this.assumeJava1_5Plus();
    }

    @Deprecated
    protected boolean assumeJava15() {
        return this.assumeJava1_5Plus() && !this.assumeJava1_6Plus();
    }

    protected boolean assumeJava1_5Plus() {
        return this.assumeJavaXY("javac1.5", "1.5") || this.assumeJava1_6Plus();
    }

    @Deprecated
    protected boolean assumeJava16() {
        return this.assumeJava1_6Plus() && !this.assumeJava1_7Plus();
    }

    protected boolean assumeJava1_6Plus() {
        return this.assumeJavaXY("javac1.6", "1.6") || this.assumeJava1_7Plus();
    }

    @Deprecated
    protected boolean assumeJava17() {
        return this.assumeJava1_7Plus() && !this.assumeJava1_8Plus();
    }

    protected boolean assumeJava1_7Plus() {
        return this.assumeJavaXY("javac1.7", "1.7") || this.assumeJava1_8Plus();
    }

    @Deprecated
    protected boolean assumeJava18() {
        return this.assumeJava1_8Plus() && !this.assumeJava9Plus();
    }

    protected boolean assumeJava1_8Plus() {
        return this.assumeJavaXY("javac1.8", "1.8") || this.assumeJava9Plus();
    }

    @Deprecated
    protected boolean assumeJava19() {
        return this.assumeJava9();
    }

    @Deprecated
    protected boolean assumeJava9() {
        return this.assumeJava9Plus() && !this.assumeJava10Plus();
    }

    protected boolean assumeJava9Plus() {
        return this.assumeJavaXY("javac9", "9") || this.assumeJavaXY("javac1.9", "9") || this.assumeJava10Plus();
    }

    protected boolean assumeJava10Plus() {
        return "javac10+".equalsIgnoreCase(this.attributes.getCompilerVersion()) || JavaEnvUtils.isAtLeastJavaVersion("10") && CompilerAdapterFactory.isJdkCompilerNickname(this.attributes.getCompilerVersion());
    }

    private boolean assumeJavaXY(String javacXY, String javaEnvVersionXY) {
        String compilerVersion = this.attributes.getCompilerVersion();
        return javacXY.equalsIgnoreCase(compilerVersion) || JavaEnvUtils.isJavaVersion(javaEnvVersionXY) && CompilerAdapterFactory.isJdkCompilerNickname(this.attributes.getCompilerVersion());
    }

    protected Path getBootClassPath() {
        Path bp = new Path(this.project);
        if (this.bootclasspath != null) {
            bp.append(this.bootclasspath);
        }
        return bp.concatSystemBootClasspath("ignore");
    }

    protected String getNoDebugArgument() {
        return this.assumeJava1_2Plus() ? "-g:none" : null;
    }

    private void setImplicitSourceSwitch(Commandline cmd, String target, String source) {
        this.attributes.log("", 1);
        this.attributes.log("          WARNING", 1);
        this.attributes.log("", 1);
        this.attributes.log("The -source switch defaults to " + this.getDefaultSource() + ".", 1);
        this.attributes.log("If you specify -target " + target + " you now must also specify -source " + source + ".", 1);
        this.attributes.log("Ant will implicitly add -source " + source + " for you.  Please change your build file.", 1);
        cmd.createArgument().setValue("-source");
        cmd.createArgument().setValue(source);
    }

    private String getDefaultSource() {
        if (this.assumeJava9Plus()) {
            return "9 in JDK 9";
        }
        if (this.assumeJava1_8Plus()) {
            return "1.8 in JDK 1.8";
        }
        if (this.assumeJava1_7Plus()) {
            return "1.7 in JDK 1.7";
        }
        if (this.assumeJava1_5Plus()) {
            return "1.5 in JDK 1.5 and 1.6";
        }
        return "";
    }

    private boolean mustSetSourceForTarget(String t) {
        if (!this.assumeJava1_5Plus()) {
            return false;
        }
        if (t.startsWith("1.")) {
            t = t.substring(2);
        }
        return "1".equals(t) || "2".equals(t) || "3".equals(t) || "4".equals(t) || ("5".equals(t) || "6".equals(t)) && this.assumeJava1_7Plus() || "7".equals(t) && this.assumeJava1_8Plus() || "8".equals(t) && this.assumeJava9Plus() || "9".equals(t) && this.assumeJava10Plus();
    }

    private String adjustSourceValue(String source) {
        return "1.1".equals(source) || "1.2".equals(source) ? "1.3" : source;
    }
}

