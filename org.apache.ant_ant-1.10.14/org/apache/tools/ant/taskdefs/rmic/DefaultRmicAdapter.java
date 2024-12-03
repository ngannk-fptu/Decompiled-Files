/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.rmic;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.StringUtils;

public abstract class DefaultRmicAdapter
implements RmicAdapter {
    private static final Random RAND = new Random();
    public static final String RMI_STUB_SUFFIX = "_Stub";
    public static final String RMI_SKEL_SUFFIX = "_Skel";
    public static final String RMI_TIE_SUFFIX = "_Tie";
    public static final String STUB_COMPAT = "-vcompat";
    public static final String STUB_1_1 = "-v1.1";
    public static final String STUB_1_2 = "-v1.2";
    public static final String STUB_OPTION_1_1 = "1.1";
    public static final String STUB_OPTION_1_2 = "1.2";
    public static final String STUB_OPTION_COMPAT = "compat";
    private Rmic attributes;
    private FileNameMapper mapper;

    @Override
    public void setRmic(Rmic attributes) {
        this.attributes = attributes;
        this.mapper = new RmicFileNameMapper();
    }

    public Rmic getRmic() {
        return this.attributes;
    }

    protected String getStubClassSuffix() {
        return RMI_STUB_SUFFIX;
    }

    protected String getSkelClassSuffix() {
        return RMI_SKEL_SUFFIX;
    }

    protected String getTieClassSuffix() {
        return RMI_TIE_SUFFIX;
    }

    @Override
    public FileNameMapper getMapper() {
        return this.mapper;
    }

    @Override
    public Path getClasspath() {
        return this.getCompileClasspath();
    }

    protected Path getCompileClasspath() {
        Path classpath = new Path(this.attributes.getProject());
        classpath.setLocation(this.attributes.getBase());
        Path cp = this.attributes.getClasspath();
        if (cp == null) {
            cp = new Path(this.attributes.getProject());
        }
        if (this.attributes.getIncludeantruntime()) {
            classpath.addExisting(cp.concatSystemClasspath("last"));
        } else {
            classpath.addExisting(cp.concatSystemClasspath("ignore"));
        }
        if (this.attributes.getIncludejavaruntime()) {
            classpath.addJavaRuntime();
        }
        return classpath;
    }

    protected boolean areIiopAndIdlSupported() {
        return !JavaEnvUtils.isAtLeastJavaVersion("11");
    }

    protected Commandline setupRmicCommand() {
        return this.setupRmicCommand(null);
    }

    protected Commandline setupRmicCommand(String[] options) {
        Commandline cmd = new Commandline();
        if (options != null) {
            for (String option : options) {
                cmd.createArgument().setValue(option);
            }
        }
        Path classpath = this.getCompileClasspath();
        cmd.createArgument().setValue("-d");
        cmd.createArgument().setFile(this.attributes.getOutputDir());
        if (this.attributes.getExtdirs() != null) {
            cmd.createArgument().setValue("-extdirs");
            cmd.createArgument().setPath(this.attributes.getExtdirs());
        }
        cmd.createArgument().setValue("-classpath");
        cmd.createArgument().setPath(classpath);
        String stubOption = this.addStubVersionOptions();
        if (stubOption != null) {
            cmd.createArgument().setValue(stubOption);
        }
        if (null != this.attributes.getSourceBase()) {
            cmd.createArgument().setValue("-keepgenerated");
        }
        if (this.attributes.getIiop()) {
            if (!this.areIiopAndIdlSupported()) {
                throw new BuildException("this rmic implementation doesn't support the -iiop switch");
            }
            this.attributes.log("IIOP has been turned on.", 2);
            cmd.createArgument().setValue("-iiop");
            if (this.attributes.getIiopopts() != null) {
                this.attributes.log("IIOP Options: " + this.attributes.getIiopopts(), 2);
                cmd.createArgument().setValue(this.attributes.getIiopopts());
            }
        }
        if (this.attributes.getIdl()) {
            if (!this.areIiopAndIdlSupported()) {
                throw new BuildException("this rmic implementation doesn't support the -idl switch");
            }
            cmd.createArgument().setValue("-idl");
            this.attributes.log("IDL has been turned on.", 2);
            if (this.attributes.getIdlopts() != null) {
                cmd.createArgument().setValue(this.attributes.getIdlopts());
                this.attributes.log("IDL Options: " + this.attributes.getIdlopts(), 2);
            }
        }
        if (this.attributes.getDebug()) {
            cmd.createArgument().setValue("-g");
        }
        String[] compilerArgs = this.attributes.getCurrentCompilerArgs();
        compilerArgs = this.preprocessCompilerArgs(compilerArgs);
        cmd.addArguments(compilerArgs);
        this.verifyArguments(cmd);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }

    protected String addStubVersionOptions() {
        String stubVersion = this.attributes.getStubVersion();
        String stubOption = null;
        if (null != stubVersion) {
            if (STUB_OPTION_1_1.equals(stubVersion)) {
                stubOption = STUB_1_1;
            } else if (STUB_OPTION_1_2.equals(stubVersion)) {
                stubOption = STUB_1_2;
            } else if (STUB_OPTION_COMPAT.equals(stubVersion)) {
                stubOption = STUB_COMPAT;
            } else {
                this.attributes.log("Unknown stub option " + stubVersion);
            }
        }
        if (stubOption == null && !this.attributes.getIiop() && !this.attributes.getIdl()) {
            stubOption = STUB_COMPAT;
        }
        return stubOption;
    }

    protected String[] preprocessCompilerArgs(String[] compilerArgs) {
        return compilerArgs;
    }

    protected String[] filterJvmCompilerArgs(String[] compilerArgs) {
        int len = compilerArgs.length;
        ArrayList<String> args = new ArrayList<String>(len);
        for (String arg : compilerArgs) {
            if (arg.startsWith("-J")) {
                this.attributes.log("Dropping " + arg + " from compiler arguments");
                continue;
            }
            args.add(arg);
        }
        return args.toArray(new String[0]);
    }

    protected void logAndAddFilesToCompile(Commandline cmd) {
        Vector<String> compileList = this.attributes.getCompileList();
        this.attributes.log("Compilation " + cmd.describeArguments(), 3);
        String niceSourceList = (compileList.size() == 1 ? "File" : "Files") + " to be compiled:" + compileList.stream().peek(arg -> cmd.createArgument().setValue((String)arg)).collect(Collectors.joining("    "));
        this.attributes.log(niceSourceList, 3);
    }

    private void verifyArguments(Commandline cmd) {
        if (JavaEnvUtils.isAtLeastJavaVersion("9")) {
            for (String arg : cmd.getArguments()) {
                if (!"-Xnew".equals(arg)) continue;
                throw new BuildException("JDK9 has removed support for -Xnew");
            }
        }
    }

    private class RmicFileNameMapper
    implements FileNameMapper {
        private RmicFileNameMapper() {
        }

        @Override
        public void setFrom(String s) {
        }

        @Override
        public void setTo(String s) {
        }

        @Override
        public String[] mapFileName(String name) {
            if (name == null || !name.endsWith(".class") || name.endsWith(DefaultRmicAdapter.this.getStubClassSuffix() + ".class") || name.endsWith(DefaultRmicAdapter.this.getSkelClassSuffix() + ".class") || name.endsWith(DefaultRmicAdapter.this.getTieClassSuffix() + ".class")) {
                return null;
            }
            String base = StringUtils.removeSuffix(name, ".class");
            String classname = base.replace(File.separatorChar, '.');
            if (DefaultRmicAdapter.this.attributes.getVerify() && !DefaultRmicAdapter.this.attributes.isValidRmiRemote(classname)) {
                return null;
            }
            String[] target = new String[]{name + ".tmp." + RAND.nextLong()};
            if (!DefaultRmicAdapter.this.attributes.getIiop() && !DefaultRmicAdapter.this.attributes.getIdl()) {
                target = DefaultRmicAdapter.STUB_OPTION_1_2.equals(DefaultRmicAdapter.this.attributes.getStubVersion()) ? new String[]{base + DefaultRmicAdapter.this.getStubClassSuffix() + ".class"} : new String[]{base + DefaultRmicAdapter.this.getStubClassSuffix() + ".class", base + DefaultRmicAdapter.this.getSkelClassSuffix() + ".class"};
            } else if (!DefaultRmicAdapter.this.attributes.getIdl()) {
                String dirname;
                int lastSlash = base.lastIndexOf(File.separatorChar);
                int index = -1;
                if (lastSlash == -1) {
                    index = 0;
                    dirname = "";
                } else {
                    index = lastSlash + 1;
                    dirname = base.substring(0, index);
                }
                String filename = base.substring(index);
                try {
                    Class<?> c = DefaultRmicAdapter.this.attributes.getLoader().loadClass(classname);
                    if (c.isInterface()) {
                        target = new String[]{dirname + "_" + filename + DefaultRmicAdapter.this.getStubClassSuffix() + ".class"};
                    } else {
                        String iDir;
                        int iIndex;
                        Class<?> interf = DefaultRmicAdapter.this.attributes.getRemoteInterface(c);
                        String iName = interf.getName();
                        int lastDot = iName.lastIndexOf(46);
                        if (lastDot == -1) {
                            iIndex = 0;
                            iDir = "";
                        } else {
                            iIndex = lastDot + 1;
                            iDir = iName.substring(0, iIndex);
                            iDir = iDir.replace('.', File.separatorChar);
                        }
                        target = new String[]{dirname + "_" + filename + DefaultRmicAdapter.this.getTieClassSuffix() + ".class", iDir + "_" + iName.substring(iIndex) + DefaultRmicAdapter.this.getStubClassSuffix() + ".class"};
                    }
                }
                catch (ClassNotFoundException e) {
                    DefaultRmicAdapter.this.attributes.log("Unable to verify class " + classname + ". It could not be found.", 1);
                }
                catch (NoClassDefFoundError e) {
                    DefaultRmicAdapter.this.attributes.log("Unable to verify class " + classname + ". It is not defined.", 1);
                }
                catch (Throwable t) {
                    DefaultRmicAdapter.this.attributes.log("Unable to verify class " + classname + ". Loading caused Exception: " + t.getMessage(), 1);
                }
            }
            return target;
        }
    }
}

